package com.fanfou.app.service;

import java.util.List;

import com.fanfou.app.App;
import com.fanfou.app.R;
import com.fanfou.app.api.ApiException;
import com.fanfou.app.api.DirectMessage;
import com.fanfou.app.api.Parser;
import com.fanfou.app.api.Status;
import com.fanfou.app.config.Commons;
import com.fanfou.app.db.Contents.BasicColumns;
import com.fanfou.app.db.Contents.DirectMessageInfo;
import com.fanfou.app.db.Contents.StatusInfo;
import com.fanfou.app.util.OptionHelper;
import com.fanfou.app.util.Utils;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.PowerManager;
import android.util.Log;

/**
 * @author mcxiaoke
 * @version 1.0 20110920
 * 
 */
public class NotificationService extends BaseIntentService {
	private static final String TAG = NotificationService.class.getSimpleName();

	public static final String ACTION_NOTIFICATION = "com.fanfou.app.action.NOTIFICATION";
	public static final int NOTIFICATION_TYPE_HOME = Status.TYPE_HOME;
	public static final int NOTIFICATION_TYPE_MENTION = Status.TYPE_MENTION; // @消息
	public static final int NOTIFICATION_TYPE_DM = DirectMessage.TYPE_NONE; // 私信

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
		boolean dm = OptionHelper.readBoolean(this,
				R.string.option_notification_dm, false);
		boolean mention = OptionHelper.readBoolean(this,
				R.string.option_notification_mention, false);
		boolean home = OptionHelper.readBoolean(this,
				R.string.option_notification_home, false);

		try {

			if (home) {
				handleHome();
				try {
					Thread.sleep(200);
				} catch (InterruptedException e) {
				}
			}
			if (mention) {
				handleMention();
				try {
					Thread.sleep(200);
				} catch (InterruptedException e) {
				}
			}
			if (dm) {
				handleDm();
			}

		} catch (ApiException e) {
			if (App.DEBUG) {
				Log.e(TAG, "error code=" + e.statusCode + " error message="
						+ e.errorMessage);
				e.printStackTrace();
			}
		}
	}

	private void handleDm() throws ApiException {
		Cursor mc = initCursor(DirectMessage.TYPE_NONE);
		List<DirectMessage> dms = App.me.api.messagesInbox(0, 0,
				Utils.getDmSinceId(mc), null);
		if (dms != null) {
			int size = dms.size();
			if (size > 0) {
				getContentResolver().bulkInsert(DirectMessageInfo.CONTENT_URI,
						Parser.toContentValuesArray(dms));
				getContentResolver().notifyChange(DirectMessageInfo.CONTENT_URI, null,false);
				notifyDmList(DirectMessage.TYPE_NONE, size);

			}
		}
		// mc.close();
	}

	private void handleMention() throws ApiException {
		Cursor mc = initCursor(Status.TYPE_MENTION);
		List<Status> ss = App.me.api.mentions(0, 0, Utils.getSinceId(mc), null,
				true);
		if (ss != null) {
			int size = ss.size();
			if (size > 0) {
				getContentResolver().bulkInsert(StatusInfo.CONTENT_URI,
						Parser.toContentValuesArray(ss));
				getContentResolver().notifyChange(DirectMessageInfo.CONTENT_URI, null,false);
				if (size == 1) {
					notifyStatusOne(NOTIFICATION_TYPE_MENTION, ss.get(0));
				} else {
					notifyStatusList(NOTIFICATION_TYPE_MENTION, size);
				}
			}
		}
		// mc.close();
	}

	private void handleHome() throws ApiException {
		Cursor mc = initCursor(Status.TYPE_HOME);
		List<Status> ss = App.me.api.homeTimeline(0, 0, Utils.getSinceId(mc),
				null, true);
		if (ss != null) {
			int size = ss.size();
			if (size > 0) {
				getContentResolver().bulkInsert(StatusInfo.CONTENT_URI,
						Parser.toContentValuesArray(ss));
				getContentResolver().notifyChange(DirectMessageInfo.CONTENT_URI, null,false);
				if (size == 1) {
					notifyStatusOne(NOTIFICATION_TYPE_HOME, ss.get(0));
				} else {
					notifyStatusList(NOTIFICATION_TYPE_HOME, size);
				}
			}
		}
		// mc.close();
	}

	private void notifyStatusOne(int type, Status status) {
		sendStatusNotification(type, 1, status);
	}

	private void notifyStatusList(int type, int count) {
		sendStatusNotification(type, count, null);
	}

	private void notifyDmOne() {

	}

	private void notifyDmList(int type, int count) {
		sendMessageNotification(type, count);
	}

	private Cursor initCursor(int type) {
		String where = BasicColumns.TYPE + "=?";
		String[] whereArgs = new String[] { String.valueOf(type) };
		Uri uri = StatusInfo.CONTENT_URI;
		String[] columns = StatusInfo.COLUMNS;
		if (type == DirectMessage.TYPE_NONE) {
			uri = DirectMessageInfo.CONTENT_URI;
			columns = DirectMessageInfo.COLUMNS;
		}
		return getContentResolver().query(uri, columns, where, whereArgs, null);
	}

	private void sendStatusNotification(int type, int count, Status status) {
		boolean needNotification = OptionHelper.readBoolean(this,
				R.string.option_notification_switch, false);
		if (!needNotification) {
			return;
		}
		if (App.DEBUG) {
			Log.i(TAG, "sendStatusNotification type=" + type + " count="
					+ count + " status=" + (status == null ? "null" : status));
		}
		Intent intent = new Intent();
		intent.putExtra(Commons.EXTRA_TYPE, type);
		intent.putExtra(Commons.EXTRA_COUNT, count);
		intent.putExtra(Commons.EXTRA_STATUS, status);
		intent.setAction(ACTION_NOTIFICATION);
		broadcast(intent);
	}

	private void sendMessageNotification(int type, int count) {
		boolean needNotification = OptionHelper.readBoolean(this,
				R.string.option_notification_switch, false);
		if (!needNotification) {
			return;
		}
		if (App.DEBUG) {
			Log.i(TAG, "sendMessageNotification type=" + type + " count="
					+ count);
		}
		Intent intent = new Intent();
		intent.putExtra(Commons.EXTRA_TYPE, type);
		intent.putExtra(Commons.EXTRA_COUNT, count);
		intent.setAction(ACTION_NOTIFICATION);
		broadcast(intent);
	}

	private void broadcast(Intent intent) {
		intent.setPackage(getPackageName());
		sendOrderedBroadcast(intent, null);
	}

}
