package com.fanfou.app.receiver;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.fanfou.app.App;
import com.fanfou.app.hd.UIHome;
import com.fanfou.app.R;
import com.fanfou.app.SendPage;
import com.fanfou.app.StatusPage;
import com.fanfou.app.api.DirectMessage;
import com.fanfou.app.api.Status;
import com.fanfou.app.service.Constants;
import com.fanfou.app.service.NotificationService;
import com.fanfou.app.util.AlarmHelper;

/**
 * @author mcxiaoke
 * @version 1.0 2011.09.21
 * @version 1.1 2011.11.03
 * 
 */
public class NotificationReceiver extends BroadcastReceiver {
	private static final int NOTIFICATION_ID_DM = -101;
	private static final int NOTIFICATION_ID_MENTION = -102;
	private static final int NOTIFICATION_ID_HOME = -103;
	private static final String TAG = NotificationReceiver.class
			.getSimpleName();

	@Override
	public void onReceive(Context context, Intent intent) {
		int type = intent.getIntExtra(Constants.EXTRA_TYPE, -1);
		int count = intent.getIntExtra(Constants.EXTRA_COUNT, 1);
		if (App.DEBUG) {
			Log.d(TAG, "broadcast received type=" + type + " count=" + count);
		}
		switch (type) {
		case NotificationService.NOTIFICATION_TYPE_HOME:
			if (count == 1) {
				Status status = (Status) intent
						.getParcelableExtra(Constants.EXTRA_DATA);
				if (status != null) {
					showHomeOneNotification(context, status);
				}
			} else {
				showHomeMoreNotification(context, count);
			}
			break;
		case NotificationService.NOTIFICATION_TYPE_MENTION:
			if (count == 1) {
				Status status = (Status) intent
						.getParcelableExtra(Constants.EXTRA_DATA);
				if (status != null) {
					showMentionOneNotification(context, status);
				}
			} else {
				showMentionMoreNotification(context, count);
			}
			break;
		case NotificationService.NOTIFICATION_TYPE_DM:
			if (count == 1) {
				final DirectMessage dm = (DirectMessage) intent
						.getParcelableExtra(Constants.EXTRA_DATA);
				if (dm != null) {
					showDmOneNotification(context, dm);
				}
			} else {
				showDmMoreNotification(context, count);
			}
			break;
		default:
			break;
		}
	}

	private static void showHomeOneNotification(Context context,
			final Status status) {
		if (App.DEBUG) {
			Log.d(TAG, "showHomeOneNotification " + status);
		}
		String title = status.userScreenName;
		String message = status.simpleText;
		Intent intent = new Intent(context, StatusPage.class);
		intent.setAction("DUMY_ACTION " + System.currentTimeMillis());
		intent.putExtra(Constants.EXTRA_DATA, status);
		PendingIntent contentIntent = PendingIntent.getActivity(context, 0,
				intent, PendingIntent.FLAG_UPDATE_CURRENT);
		showNotification(NOTIFICATION_ID_HOME, context, contentIntent, title,
				message, R.drawable.ic_notify_home);
	}

	private static void showHomeMoreNotification(Context context, int count) {
		if (App.DEBUG) {
			Log.d(TAG, "showHomeMoreNotification  count=" + count);
		}
		String title = "饭否消息";
		String message = "收到" + count + "条来自好友的消息";
		Intent intent = new Intent(context, UIHome.class);
		intent.setAction("DUMY_ACTION " + System.currentTimeMillis());
		intent.putExtra(Constants.EXTRA_PAGE, 0);
		PendingIntent contentIntent = PendingIntent.getActivity(context, 0,
				intent, 0);
		showNotification(NOTIFICATION_ID_HOME, context, contentIntent, title,
				message, R.drawable.ic_notify_home);
	}

	private static void showMentionOneNotification(Context context,
			final Status status) {
		if (App.DEBUG) {
			Log.i(TAG, "showMentionOneNotification " + status);
		}
		String title = status.userScreenName + "@你的消息";
		String message = status.simpleText;
		Intent intent = new Intent(context, StatusPage.class);
		intent.setAction("DUMY_ACTION " + System.currentTimeMillis());
		intent.putExtra(Constants.EXTRA_DATA, status);
		PendingIntent contentIntent = PendingIntent.getActivity(context, 0,
				intent, PendingIntent.FLAG_UPDATE_CURRENT);
		showNotification(NOTIFICATION_ID_MENTION, context, contentIntent,
				title, message, R.drawable.ic_notify_mention);
	}

	private static void showMentionMoreNotification(Context context, int count) {
		if (App.DEBUG) {
			Log.d(TAG, "showMentionMoreNotification count=" + count);
		}
		String title = "饭否消息";
		String message = "收到" + count + "条提到你的消息";
		Intent intent = new Intent(context, UIHome.class);
		intent.setAction("DUMY_ACTION " + System.currentTimeMillis());
		intent.putExtra(Constants.EXTRA_PAGE, 1);
		PendingIntent contentIntent = PendingIntent.getActivity(context, 0,
				intent, 0);
		showNotification(NOTIFICATION_ID_MENTION, context, contentIntent,
				title, message, R.drawable.ic_notify_mention);
	}

	private static void showDmOneNotification(Context context,
			final DirectMessage dm) {
		if (App.DEBUG) {
			Log.d(TAG, "showDmOneNotification " + dm);
		}
		Intent intent = new Intent(context, SendPage.class);
		intent.setAction("DUMY_ACTION " + System.currentTimeMillis());
		intent.putExtra(Constants.EXTRA_ID, dm.senderId);
		intent.putExtra(Constants.EXTRA_USER_NAME, dm.senderScreenName);
		PendingIntent contentIntent = PendingIntent.getActivity(context, 0,
				intent, PendingIntent.FLAG_UPDATE_CURRENT);

		String title = "收到" + dm.senderScreenName + "的私信";
		String message = dm.senderScreenName + ":" + dm.text;
		showNotification(NOTIFICATION_ID_DM, context, contentIntent, title,
				message, R.drawable.ic_notify_dm);
	}

	private static void showDmMoreNotification(Context context, int count) {
		if (App.DEBUG) {
			Log.d(TAG, "showDmMoreNotification count=" + count);
		}
		String title = "饭否私信";
		String message = "收到" + count + "条发给你的私信";
		Intent intent = new Intent(context, UIHome.class);
		intent.setAction("DUMY_ACTION " + System.currentTimeMillis());
		intent.putExtra(Constants.EXTRA_PAGE, 2);
		PendingIntent contentIntent = PendingIntent.getActivity(context, 0,
				intent, 0);
		showNotification(NOTIFICATION_ID_DM, context, contentIntent, title,
				message, R.drawable.ic_notify_dm);

	}

	private static void showNotification(int notificationId, Context context,
			PendingIntent contentIntent, String title, String message,
			int iconId) {
		NotificationManager nm = (NotificationManager) context
				.getSystemService(Context.NOTIFICATION_SERVICE);
		Notification notification = new Notification(iconId, title,
				System.currentTimeMillis());
		notification.setLatestEventInfo(context, title, message, contentIntent);
		notification.flags |= Notification.FLAG_AUTO_CANCEL;
		notification.flags |= Notification.FLAG_ONLY_ALERT_ONCE;
		AlarmHelper.setNotificationType(context, notification);
		nm.notify(notificationId, notification);
	}

}
