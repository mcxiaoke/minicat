package com.fanfou.app.receiver;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.fanfou.app.App;
import com.fanfou.app.HomePage;
import com.fanfou.app.R;
import com.fanfou.app.StatusPage;
import com.fanfou.app.api.Status;
import com.fanfou.app.config.Commons;
import com.fanfou.app.service.NotificationService;
import com.fanfou.app.util.AlarmHelper;
import com.fanfou.app.util.StatusHelper;

/**
 * @author mcxiaoke
 * @version 1.0 20110921
 * 
 */
public class NotificationReceiver extends BroadcastReceiver {
	public static final int NOTIFICATION_ID_DM = 0;
	public static final int NOTIFICATION_ID_MENTION = 1;
	public static final int NOTIFICATION_ID_HOME = 2;
	private static final String TAG = NotificationReceiver.class
			.getSimpleName();

	@Override
	public void onReceive(Context context, Intent intent) {
		int type = intent.getIntExtra(Commons.EXTRA_TYPE, -1);
		int count = intent.getIntExtra(Commons.EXTRA_COUNT, 1);
		if (App.DEBUG) {
			Log.i(TAG, "broadcast received type=" + type + " count=" + count);
		}
		switch (type) {
		case NotificationService.NOTIFICATION_TYPE_HOME:
			if (count == 1) {
				Status status = (Status) intent
						.getSerializableExtra(Commons.EXTRA_STATUS);
				if (status != null) {
					showHomeOneNotification(context, type, status);
				}
			} else {
				showHomeMoreNotification(context, type, count);
			}
			break;
		case NotificationService.NOTIFICATION_TYPE_MENTION:
			if (count == 1) {
				Status status = (Status) intent
						.getSerializableExtra(Commons.EXTRA_STATUS);
				if (status != null) {
					showMentionOneNotification(context, type, status);
				}
			} else {
				showMentionMoreNotification(context, type, count);
			}
			break;
		case NotificationService.NOTIFICATION_TYPE_DM:
			showDmMoreNotification(context, type, count);
			break;
		default:
			break;
		}
	}

	private static void showHomeOneNotification(Context context, int type,
			Status status) {
		if (App.DEBUG) {
			Log.i(TAG, "showHomeOneNotification type=" + type);
		}
		String title = status.userScreenName;
		String message = StatusHelper.getSimpifiedText(status.text);
		Intent intent = new Intent(context, StatusPage.class);
		intent.setAction("DUMY_ACTION "+System.currentTimeMillis());
		intent.putExtra(Commons.EXTRA_STATUS, status);
		PendingIntent contentIntent = PendingIntent.getActivity(context, 0,
				intent, PendingIntent.FLAG_UPDATE_CURRENT);
		showNotification(NOTIFICATION_ID_HOME, context, contentIntent, title,
				message);
	}

	private static void showHomeMoreNotification(Context context, int type,
			int count) {
		if (App.DEBUG) {
			Log.i(TAG, "showHomeMoreNotification type=" + type + " count="
					+ count);
		}
		String title = "饭否消息";
		String message = count + "条新消息";
		Intent intent = new Intent(context, HomePage.class);
		intent.setAction("DUMY_ACTION "+System.currentTimeMillis());
		intent.putExtra(Commons.EXTRA_PAGE, 0);
		PendingIntent contentIntent = PendingIntent.getActivity(context, 0,
				intent, 0);
		showNotification(NOTIFICATION_ID_HOME, context, contentIntent, title,
				message);
	}

	private static void showMentionOneNotification(Context context, int type,
			Status status) {
		if (App.DEBUG) {
			Log.i(TAG, "showMentionOneNotification type=" + type);
		}
		String title = status.userScreenName + "@你的消息";
		String message = StatusHelper.getSimpifiedText(status.text);
		Intent intent = new Intent(context, StatusPage.class);
		intent.setAction("DUMY_ACTION "+System.currentTimeMillis());
		intent.putExtra(Commons.EXTRA_STATUS, status);
		PendingIntent contentIntent = PendingIntent.getActivity(context, 0,
				intent, PendingIntent.FLAG_UPDATE_CURRENT);
		showNotification(NOTIFICATION_ID_MENTION, context, contentIntent,
				title, message);
	}

	private static void showMentionMoreNotification(Context context, int type,
			int count) {
		if (App.DEBUG) {
			Log.i(TAG, "showMentionMoreNotification type=" + type + " count="
					+ count);
		}
		String title = "饭否消息";
		String message = count + "条@你的消息";
		Intent intent = new Intent(context, HomePage.class);
		intent.setAction("DUMY_ACTION "+System.currentTimeMillis());
		intent.putExtra(Commons.EXTRA_PAGE, 1);
		PendingIntent contentIntent = PendingIntent.getActivity(context, 0,
				intent, 0);
		showNotification(NOTIFICATION_ID_MENTION, context, contentIntent,
				title, message);
	}

	private static void showDmMoreNotification(Context context, int type,
			int count) {
		if (App.DEBUG) {
			Log.i(TAG, "showDmMoreNotification type=" + type + " count="
					+ count);
		}
		String title = "饭否私信";
		String message = count + "条私信";
		Intent intent = new Intent(context, HomePage.class);
		intent.setAction("DUMY_ACTION "+System.currentTimeMillis());
		intent.putExtra(Commons.EXTRA_PAGE, 2);
		PendingIntent contentIntent = PendingIntent.getActivity(context, 0,
				intent, 0);
		showNotification(NOTIFICATION_ID_DM, context, contentIntent, title,
				message);

	}

	private static void showNotification(int notificationId, Context context,
			PendingIntent contentIntent, String title, String message) {
		NotificationManager nm = (NotificationManager) context
				.getSystemService(Context.NOTIFICATION_SERVICE);
		Notification notification = new Notification(R.drawable.statusbar_icon,
				title, System.currentTimeMillis());
		notification.setLatestEventInfo(context, title, message, contentIntent);
		notification.flags |= Notification.FLAG_AUTO_CANCEL;
		notification.flags |= Notification.FLAG_ONLY_ALERT_ONCE;
		AlarmHelper.setNotificationType(context, notification);
		nm.notify(notificationId, notification);
	}

}
