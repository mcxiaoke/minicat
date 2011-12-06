package com.fanfou.app.service;

import java.util.ArrayList;
import java.util.List;

import android.content.ContentResolver;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.util.Log;

import com.fanfou.app.App;
import com.fanfou.app.api.Api;
import com.fanfou.app.api.ApiException;
import com.fanfou.app.api.FanFouApiConfig;
import com.fanfou.app.api.Parser;
import com.fanfou.app.api.Status;
import com.fanfou.app.db.Contents.BasicColumns;
import com.fanfou.app.db.Contents.DirectMessageInfo;
import com.fanfou.app.db.Contents.StatusInfo;
import com.fanfou.app.db.Contents.UserInfo;
import com.fanfou.app.util.IOHelper;
import com.fanfou.app.util.Utils;

/**
 * @author mcxiaoke
 * @version 1.0 2011.09.01
 * @version 1.5 2011.10.09
 * @version 2.0 2011.10.21
 * @version 3.0 2011.11.18
 * 
 */
public class CleanService extends WakefulIntentService {

	private static final String TAG = CleanService.class.getSimpleName();

	public CleanService() {
		super("CleanService");
	}

	@Override
	protected void onHandleIntent(Intent intent) {
		if (!App.active) {
			// doUpdateHome();
			// doUpdateMention();
			// doCleanAction();
			doCleanStatusData();
			doCleanMessageData();
			doCleanUserData();
			doCleanPhotos();
		}
	}

	private void doCleanAction() {
		Uri uri = Uri.withAppendedPath(StatusInfo.CONTENT_URI, "action/clean");
		int result = getContentResolver().delete(uri, null, null);
		if (App.DEBUG) {
			Log.d(TAG, "doCleanAction cleaned items: " + result);
		}
	}

	private void doCleanStatusData() {
		ContentResolver cr = getContentResolver();
		int result = cr.delete(StatusInfo.CONTENT_URI, null, null);
		if (App.DEBUG) {
			Log.d("CleanService", "cleaned status items count=" + result);
		}
	}

	private void doCleanMessageData() {
		ContentResolver cr = getContentResolver();
		int result = cr.delete(DirectMessageInfo.CONTENT_URI, null, null);
		if (App.DEBUG) {
			Log.d("CleanService", "cleaned message items count=" + result);
		}
	}

	private void doCleanUserData() {
		ContentResolver cr = getContentResolver();
		String where = BasicColumns.OWNER_ID + "!=?";
		String[] whereArgs = new String[] { App.getUserId() };
		int result = cr.delete(UserInfo.CONTENT_URI, where, whereArgs);
		if (App.DEBUG) {
			Log.d("CleanService", "cleaned user items count=" + result);
		}
	}

	private void doCleanPhotos() {
		IOHelper.deleteDir(IOHelper.getImageCacheDir(this), 10 * 1024);
	}

	private void doUpdateHome() {
		if (App.DEBUG) {
			Log.d("CleanService", "doUpdateHome()");
		}
		doUpdate(Status.TYPE_HOME);
	}

	private void doUpdateMention() {
		if (App.DEBUG) {
			Log.d("CleanService", "doUpdateMention()");
		}
		doUpdate(Status.TYPE_MENTION);
	}

	@SuppressWarnings("unused")
	private void doUpdate2(int type) {
		try {
			Api api = App.getApi();
			String where = BasicColumns.TYPE + "=?";
			String[] whereArgs = new String[] { String.valueOf(type) };
			Uri uri = StatusInfo.CONTENT_URI;
			String[] columns = StatusInfo.COLUMNS;
			Cursor c = getContentResolver().query(uri, columns, where,
					whereArgs, null);
			String sinceId = Utils.getSinceId(c);
			if (App.DEBUG) {
				Log.d(TAG, "doUpdate sinceId=" + sinceId);
			}
			int page = 1;
			boolean more = true;
			List<Status> ss = new ArrayList<Status>();
			while (more && page < 3) {
				if (App.DEBUG) {
					Log.d(TAG, "doUpdate page=" + page);
				}
				List<Status> result = null;
				if (type == Status.TYPE_HOME) {
					result = api.homeTimeline(20, page, sinceId, null,
							FanFouApiConfig.FORMAT_HTML,
							FanFouApiConfig.MODE_LITE);
				} else if (type == Status.TYPE_MENTION) {
					result = api.mentions(20, page, sinceId, null,
							FanFouApiConfig.FORMAT_HTML,
							FanFouApiConfig.MODE_LITE);
				}

				if (result != null) {
					int size = result.size();
					if (App.DEBUG) {
						Log.d(TAG, "doUpdate result.size=" + size);
					}
					if (size < 20) {
						more = false;
					}
					ss.addAll(result);
				} else {
					more = false;
				}
				page++;
			}
			if (ss.size() > 0) {
				if (App.DEBUG) {
					Log.d(TAG, "doUpdate all result.size=" + ss.size());
				}
				getContentResolver().bulkInsert(uri,
						Parser.toContentValuesArray(ss));
			}
		} catch (ApiException e) {
			if (App.DEBUG)
				e.printStackTrace();
		} catch (Exception e) {
			if (App.DEBUG)
				e.printStackTrace();
		}
	}

	private void doUpdate(int type) {
		try {
			Api api = App.getApi();
			String where = BasicColumns.TYPE + "=?";
			String[] whereArgs = new String[] { String.valueOf(type) };
			Uri uri = StatusInfo.CONTENT_URI;
			String[] columns = StatusInfo.COLUMNS;
			Cursor c = getContentResolver().query(uri, columns, where,
					whereArgs, null);
			String sinceId = Utils.getSinceId(c);
			if (App.DEBUG) {
				Log.d(TAG, "doUpdate sinceId=" + sinceId);
			}
			List<Status> result = null;
			if (type == Status.TYPE_HOME) {
				result = api.homeTimeline(
						FanFouApiConfig.DEFAULT_TIMELINE_COUNT, 0, sinceId,
						null, FanFouApiConfig.FORMAT_HTML,
						FanFouApiConfig.MODE_LITE);
			} else if (type == Status.TYPE_MENTION) {
				result = api.mentions(FanFouApiConfig.DEFAULT_TIMELINE_COUNT,
						0, sinceId, null, FanFouApiConfig.FORMAT_HTML,
						FanFouApiConfig.MODE_LITE);
			}

			if (result != null) {
				int size = result.size();
				if (App.DEBUG) {
					Log.d(TAG, "doUpdate result.size=" + size);
				}
				if (size > 0) {
					getContentResolver().bulkInsert(uri,
							Parser.toContentValuesArray(result));
				}
			}
		} catch (ApiException e) {
			if (App.DEBUG)
				e.printStackTrace();
		} catch (Exception e) {
			if (App.DEBUG)
				e.printStackTrace();
		}
	}

}
