package com.fanfou.app.service;

import java.util.List;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.PowerManager;
import android.util.Log;

import com.fanfou.app.App;
import com.fanfou.app.R;
import com.fanfou.app.api.ApiException;
import com.fanfou.app.api.DirectMessage;
import com.fanfou.app.api.FanFouApi;
import com.fanfou.app.api.FanFouApiConfig;
import com.fanfou.app.api.Parser;
import com.fanfou.app.api.Status;
import com.fanfou.app.config.Actions;
import com.fanfou.app.config.Commons;
import com.fanfou.app.db.Contents.BasicColumns;
import com.fanfou.app.db.Contents.DirectMessageInfo;
import com.fanfou.app.db.Contents.StatusInfo;
import com.fanfou.app.App.ApnType;
import com.fanfou.app.util.IntentHelper;
import com.fanfou.app.util.OptionHelper;
import com.fanfou.app.util.Utils;

/**
 * @author mcxiaoke
 * @version 1.0 2011.09.20
 * @version 1.1 2011.11.02
 * @version 1.2 2011.11.03
 * @version 1.3 2011.11.04
 * @version 2.0 2011.11.18
 * @version 2.1 2011.11.23
 * 
 */
public class NotificationService extends BaseIntentService {
	private static final String TAG = NotificationService.class.getSimpleName();

	public static final int NOTIFICATION_TYPE_HOME = Status.TYPE_HOME;
	public static final int NOTIFICATION_TYPE_MENTION = Status.TYPE_MENTION; // @消息
	public static final int NOTIFICATION_TYPE_DM = DirectMessage.TYPE_IN; // 私信

	private static final int DEFAULT_COUNT = FanFouApiConfig.DEFAULT_TIMELINE_COUNT;
	private static final int MAX_COUNT = FanFouApiConfig.MAX_TIMELINE_COUNT;
	private static final int DEFAULT_PAGE = 0;

	private PowerManager.WakeLock mWakeLock;

	public NotificationService() {
		super("NotificationService");
	}

	@Override
	public void onCreate() {
		super.onCreate();
		PowerManager pm = (PowerManager) getSystemService(Context.POWER_SERVICE);
		mWakeLock = pm.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, TAG);
		mWakeLock.acquire();
	}

	@Override
	public void onDestroy() {
		mWakeLock.release();
		super.onDestroy();
	}

	@Override
	protected void onHandleIntent(Intent intent) {
		if (App.DEBUG) {
			Log.i(TAG, "onHandleIntent");
		}

		boolean flag = OptionHelper.readBoolean(this,
				R.string.option_notification, false);
		if (!flag) {
			return;
		}

		boolean dm = OptionHelper.readBoolean(this,
				R.string.option_notification_dm, false);
		boolean mention = OptionHelper.readBoolean(this,
				R.string.option_notification_mention, false);
		boolean home = OptionHelper.readBoolean(this,
				R.string.option_notification_home, false);

		int count = DEFAULT_COUNT;
		if (App.me.apnType == ApnType.WIFI) {
			count = MAX_COUNT;
		}
		try {
			if (dm) {
				handleDm(count);
				try {
					Thread.sleep(500);
				} catch (InterruptedException e) {
				}
			}
			if (mention) {
				handleMention(count);
				try {
					Thread.sleep(500);
				} catch (InterruptedException e) {
				}
			}
			if (home) {
				handleHome(count);

			}
		} catch (ApiException e) {
			if (App.DEBUG) {
				Log.e(TAG, "error code=" + e.statusCode + " error message="
						+ e.errorMessage);
				e.printStackTrace();
			}
		}
	}

	private void handleDm(int count) throws ApiException {
		Cursor mc = initCursor(DirectMessage.TYPE_IN);
		List<DirectMessage> dms = FanFouApi.getInstance().directMessagesInbox(count, DEFAULT_PAGE,
				Utils.getDmSinceId(mc), null, FanFouApiConfig.MODE_LITE);
		if (dms != null) {
			int size = dms.size();
			if (size > 0) {
				if (App.DEBUG) {
					Log.d(TAG, "handleDm() size=" + size);
				}
				if (size == 1) {
					DirectMessage dm = dms.get(0);
					getContentResolver().insert(DirectMessageInfo.CONTENT_URI,
							dm.toContentValues());
					notifyDmOne(NOTIFICATION_TYPE_DM, dms.get(0));
				} else {
					getContentResolver().bulkInsert(
							DirectMessageInfo.CONTENT_URI,
							Parser.toContentValuesArray(dms));
					notifyDmList(NOTIFICATION_TYPE_DM, size);
				}
				getContentResolver().notifyChange(
						DirectMessageInfo.CONTENT_URI, null, false);
			}
		}
		mc.close();
	}

	private void handleMention(int count) throws ApiException {
		Cursor mc = initCursor(Status.TYPE_MENTION);
		List<Status> ss = FanFouApi.getInstance().mentions(count, DEFAULT_PAGE,
				Utils.getSinceId(mc), null, FanFouApiConfig.FORMAT_HTML,
				FanFouApiConfig.MODE_LITE);
		if (ss != null) {
			int size = ss.size();
			if (size > 0) {
				if (App.DEBUG) {
					Log.d(TAG, "handleMention() size=" + size);
				}
				if (size == 1) {
					Status s = ss.get(0);
					getContentResolver().insert(StatusInfo.CONTENT_URI,
							s.toContentValues());
					notifyStatusOne(NOTIFICATION_TYPE_MENTION, s);
				} else {
					getContentResolver().bulkInsert(StatusInfo.CONTENT_URI,
							Parser.toContentValuesArray(ss));
					notifyStatusList(NOTIFICATION_TYPE_MENTION, size);
				}
				getContentResolver().bulkInsert(StatusInfo.CONTENT_URI,
						Parser.toContentValuesArray(ss));
			}
		}
		mc.close();
	}

	private void handleHome(int count) throws ApiException {
		Cursor mc = initCursor(Status.TYPE_HOME);
		List<Status> ss = FanFouApi.getInstance().homeTimeline(count, DEFAULT_PAGE,
				Utils.getSinceId(mc), null, FanFouApiConfig.FORMAT_HTML,
				FanFouApiConfig.MODE_LITE);
		if (ss != null) {
			int size = ss.size();
			if (size > 0) {
				if (App.DEBUG) {
					Log.d(TAG, "handleHome() size=" + size);
				}
				if (size == 1) {
					Status s = ss.get(0);
					getContentResolver().insert(StatusInfo.CONTENT_URI,
							s.toContentValues());
					notifyStatusOne(NOTIFICATION_TYPE_HOME, ss.get(0));
				} else {
					getContentResolver().bulkInsert(StatusInfo.CONTENT_URI,
							Parser.toContentValuesArray(ss));
					notifyStatusList(NOTIFICATION_TYPE_HOME, size);
				}
				getContentResolver().notifyChange(StatusInfo.CONTENT_URI, null,
						false);
			}
		}
		mc.close();
	}

	private void notifyStatusOne(int type, Status status) {
		sendStatusNotification(type, 1, status);
	}

	private void notifyStatusList(int type, int count) {
		sendStatusNotification(type, count, null);
	}

	private void notifyDmOne(int type, DirectMessage dm) {
		sendMessageNotification(type, 1, dm);
	}

	private void notifyDmList(int type, int count) {
		sendMessageNotification(type, count, null);
	}

	private Cursor initCursor(int type) {
		String where = BasicColumns.TYPE + "=?";
		String[] whereArgs = new String[] { String.valueOf(type) };
		Uri uri = StatusInfo.CONTENT_URI;
		String[] columns = StatusInfo.COLUMNS;
		if (type == DirectMessage.TYPE_IN) {
			uri = DirectMessageInfo.CONTENT_URI;
			columns = DirectMessageInfo.COLUMNS;
		}
		return getContentResolver().query(uri, columns, where, whereArgs, null);
	}

	private void sendStatusNotification(int type, int count, Status status) {
		if (App.DEBUG) {
			Log.d(TAG, "sendStatusNotification type=" + type + " count="
					+ count + " status=" + (status == null ? "null" : status)
					+ " active=" + App.active);
		}
		Intent intent = new Intent();
		intent.putExtra(Commons.EXTRA_TYPE, type);
		intent.putExtra(Commons.EXTRA_COUNT, count);
		intent.putExtra(Commons.EXTRA_STATUS, status);
		intent.setAction(Actions.ACTION_NOTIFICATION);
		broadcast(intent);
	}

	private void sendMessageNotification(int type, int count, DirectMessage dm) {
		if (App.DEBUG) {
			Log.d(TAG, "sendMessageNotification type=" + type + " count="
					+ count + " active=" + App.active);
		}
		Intent intent = new Intent();
		intent.putExtra(Commons.EXTRA_TYPE, type);
		intent.putExtra(Commons.EXTRA_COUNT, count);
		intent.putExtra(Commons.EXTRA_MESSAGE, dm);
		intent.setAction(Actions.ACTION_NOTIFICATION);
		broadcast(intent);
	}

	private void broadcast(Intent intent) {
		intent.setPackage(getPackageName());
		sendOrderedBroadcast(intent, null);
		if (App.DEBUG) {
			Log.d(TAG, "broadcast() ");
			IntentHelper.logIntent(TAG, intent);
		}
	}

}
