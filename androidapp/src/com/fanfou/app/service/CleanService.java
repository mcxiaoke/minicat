package com.fanfou.app.service;

import java.util.ArrayList;
import java.util.List;

import android.app.IntentService;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.PowerManager;
import android.util.Log;

import com.fanfou.app.App;
import com.fanfou.app.api.ApiException;
import com.fanfou.app.api.Parser;
import com.fanfou.app.api.Status;
import com.fanfou.app.db.Contents.BasicColumns;
import com.fanfou.app.db.Contents.StatusInfo;
import com.fanfou.app.util.Utils;

public class CleanService extends IntentService {

	private static final String TAG = CleanService.class.getSimpleName();
	private PowerManager.WakeLock lock;

	public CleanService() {
		super("CleanService");
	}
	
	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		if ((flags & START_FLAG_REDELIVERY)!=0) {
			PowerManager mgr = (PowerManager) getSystemService(Context.POWER_SERVICE);
			lock = mgr.newWakeLock(
					PowerManager.PARTIAL_WAKE_LOCK, "com.fanfou.app.service.CleanService");
			lock.acquire();
		}
		super.onStartCommand(intent, flags, startId);
		return START_REDELIVER_INTENT;
	}
	
	@Override
	protected void onHandleIntent(Intent intent) {
		doUpdateHome();
		doUpdateMention();
		doClean();
		if(lock!=null){
			lock.release();
		}
	}

	private void doClean() {
		ContentResolver cr = getContentResolver();
		Uri cleanUri = Uri.withAppendedPath(StatusInfo.CONTENT_URI,
				"action/clean");
		int result = cr.delete(cleanUri, null, null);
		if (App.DEBUG) {
			Log.d("CleanService", "cleaned items count=" + result);
		}
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
					result = App.me.api.homeTimeline(20, page, sinceId, null,
							true);
				} else if (type == Status.TYPE_MENTION) {
					result = App.me.api.mentions(20, page, sinceId, null, true);
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
				result = App.me.api.homeTimeline(20, 0, sinceId, null, true);
			} else if (type == Status.TYPE_MENTION) {
				result = App.me.api.mentions(20, 0, sinceId, null, true);
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
