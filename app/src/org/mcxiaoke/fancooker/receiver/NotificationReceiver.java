package org.mcxiaoke.fancooker.receiver;

import org.mcxiaoke.fancooker.AppContext;
import org.mcxiaoke.fancooker.R;
import org.mcxiaoke.fancooker.UIConversation;
import org.mcxiaoke.fancooker.UIHome;
import org.mcxiaoke.fancooker.UIStatus;
import org.mcxiaoke.fancooker.dao.model.BaseModel;
import org.mcxiaoke.fancooker.dao.model.DirectMessageModel;
import org.mcxiaoke.fancooker.dao.model.StatusModel;
import org.mcxiaoke.fancooker.util.AlarmHelper;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

/**
 * @author mcxiaoke
 * @version 1.0 2011.09.21
 * @version 1.1 2011.11.03
 * @version 1.2 2012.02.22
 * @version 1.3 2012.02.24
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
		int type = intent.getIntExtra("type", BaseModel.TYPE_NONE);
		int count = intent.getIntExtra("count", 1);
		if (AppContext.DEBUG) {
			Log.d(TAG, "broadcast received type=" + type + " count=" + count);
		}
		switch (type) {
		case StatusModel.TYPE_HOME:
			if (count == 1) {
				final StatusModel status = (StatusModel) intent
						.getParcelableExtra("data");
				if (status != null) {
					showHomeOneNotification(context, status);
				}
			} else {
				showHomeMoreNotification(context, count);
			}
			break;
		case StatusModel.TYPE_MENTIONS:
			if (count == 1) {
				final StatusModel status = (StatusModel) intent
						.getParcelableExtra("data");
				if (status != null) {
					showMentionOneNotification(context, status);
				}
			} else {
				showMentionMoreNotification(context, count);
			}
			break;
		case DirectMessageModel.TYPE_INBOX:
			if (count == 1) {
				final DirectMessageModel dm = (DirectMessageModel) intent
						.getParcelableExtra("data");
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
			final StatusModel status) {
		if (AppContext.DEBUG) {
			Log.d(TAG, "showHomeOneNotification " + status);
		}
		String title = status.getUserScreenName();
		String message = status.getSimpleText();
		Intent intent = new Intent(context, UIStatus.class);
		intent.setAction("DUMY_ACTION " + System.currentTimeMillis());
		intent.putExtra("data", status);
		PendingIntent contentIntent = PendingIntent.getActivity(context, 0,
				intent, PendingIntent.FLAG_UPDATE_CURRENT);
		showNotification(NOTIFICATION_ID_HOME, context, contentIntent, title,
				message, R.drawable.ic_stat_home);
	}

	private static void showHomeMoreNotification(Context context, int count) {
		if (AppContext.DEBUG) {
			Log.d(TAG, "showHomeMoreNotification  count=" + count);
		}
		String title = "饭否消息";
		String message = "收到" + count + "条来自好友的消息";
		Intent intent = new Intent(context, UIHome.class);
		intent.setAction("DUMY_ACTION " + System.currentTimeMillis());
		// intent.putExtra("page", 0);
		PendingIntent contentIntent = PendingIntent.getActivity(context, 0,
				intent, 0);
		showNotification(NOTIFICATION_ID_HOME, context, contentIntent, title,
				message, R.drawable.ic_stat_home);
	}

	private static void showMentionOneNotification(Context context,
			final StatusModel status) {
		if (AppContext.DEBUG) {
			Log.i(TAG, "showMentionOneNotification " + status);
		}
		String title = status.getUserScreenName() + "@你的消息";
		String message = status.getSimpleText();
		Intent intent = new Intent(context, UIStatus.class);
		intent.setAction("DUMY_ACTION " + System.currentTimeMillis());
		intent.putExtra("data", status);
		PendingIntent contentIntent = PendingIntent.getActivity(context, 0,
				intent, PendingIntent.FLAG_UPDATE_CURRENT);
		showNotification(NOTIFICATION_ID_MENTION, context, contentIntent,
				title, message, R.drawable.ic_stat_mention);
	}

	private static void showMentionMoreNotification(Context context, int count) {
		if (AppContext.DEBUG) {
			Log.d(TAG, "showMentionMoreNotification count=" + count);
		}
		String title = "饭否消息";
		String message = "收到" + count + "条提到你的消息";
		Intent intent = new Intent(context, UIHome.class);
		intent.setAction("DUMY_ACTION " + System.currentTimeMillis());
		// intent.putExtra("page", 1);
		PendingIntent contentIntent = PendingIntent.getActivity(context, 0,
				intent, 0);
		showNotification(NOTIFICATION_ID_MENTION, context, contentIntent,
				title, message, R.drawable.ic_stat_mention);
	}

	private static void showDmOneNotification(Context context,
			final DirectMessageModel dm) {
		if (AppContext.DEBUG) {
			Log.d(TAG, "showDmOneNotification " + dm);
		}
		Intent intent = new Intent(context, UIConversation.class);
		intent.setAction("DUMY_ACTION " + System.currentTimeMillis());
		intent.putExtra("id", dm.getSenderId());
		intent.putExtra("screen_name", dm.getSenderScreenName());
		intent.putExtra("profile_image_url", dm.getSenderProfileImageUrl());
		PendingIntent contentIntent = PendingIntent.getActivity(context, 0,
				intent, PendingIntent.FLAG_UPDATE_CURRENT);

		String title = "收到" + dm.getSenderScreenName() + "的私信";
		String message = dm.getSenderScreenName() + ":" + dm.getText();
		showNotification(NOTIFICATION_ID_DM, context, contentIntent, title,
				message, R.drawable.ic_stat_dm);
	}

	private static void showDmMoreNotification(Context context, int count) {
		if (AppContext.DEBUG) {
			Log.d(TAG, "showDmMoreNotification count=" + count);
		}
		String title = "饭否私信";
		String message = "收到" + count + "条发给你的私信";
		Intent intent = new Intent(context, UIHome.class);
		intent.setAction("DUMY_ACTION " + System.currentTimeMillis());
		// intent.putExtra("page", 2);
		PendingIntent contentIntent = PendingIntent.getActivity(context, 0,
				intent, 0);
		showNotification(NOTIFICATION_ID_DM, context, contentIntent, title,
				message, R.drawable.ic_stat_dm);

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
