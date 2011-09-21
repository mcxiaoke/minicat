package com.fanfou.app.receiver;

import com.fanfou.app.App;
import com.fanfou.app.HomePage;
import com.fanfou.app.R;
import com.fanfou.app.StatusPage;
import com.fanfou.app.WritePage;
import com.fanfou.app.api.Status;
import com.fanfou.app.config.Commons;
import com.fanfou.app.service.NotificationService;
import com.fanfou.app.util.StatusHelper;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

/**
 * @author mcxiaoke
 * @version 1.0 20110921
 * 
 */
public class NotificationReceiver extends BroadcastReceiver {
	private static final String TAG = NotificationReceiver.class
			.getSimpleName();

	@Override
	public void onReceive(Context context, Intent intent) {
		if (App.DEBUG) {
			Log.i(TAG, "broadcast received intent: " + intent.toString());
		}

		int type = intent.getIntExtra(Commons.EXTRA_TYPE,
				NotificationService.NOTIFICATION_TYPE_MENTION);
		int count = intent.getIntExtra(Commons.EXTRA_COUNT, 1);
		if (count ==1) {
			Status status = (Status) intent
					.getSerializableExtra(Commons.EXTRA_STATUS);
			if (status != null) {
				showStatusOneNotification(context, type, status);
			}
		}else{
			showStatusMoreNotification(context, type, count);
		}

	}

	private void showStatusOneNotification(Context context, int type,
			Status status) {
		int id = 1;
		NotificationManager nm = (NotificationManager) context
				.getSystemService(Context.NOTIFICATION_SERVICE);
		Notification notification = new Notification(R.drawable.statusbar_icon,
				"来自" + status.userScreenName + "@你的饭否消息",
				System.currentTimeMillis());
		Intent intent = new Intent(context, StatusPage.class);
		intent.putExtra(Commons.EXTRA_STATUS, status);
		PendingIntent contentIntent = PendingIntent.getActivity(context, 0,
				intent, 0);
		notification.setLatestEventInfo(context, "来自"
				+ status.userScreenName + "@你的饭否消息", status.userScreenName
				+ ":" + StatusHelper.getSimpifiedText(status.text),
				contentIntent);
		notification.flags |= Notification.FLAG_AUTO_CANCEL;
		nm.notify(id, notification);
	}

	private void showStatusMoreNotification(Context context, int type, int count) {
		int id = 2;
		NotificationManager nm = (NotificationManager) context
				.getSystemService(Context.NOTIFICATION_SERVICE);
		Notification notification = new Notification(R.drawable.statusbar_icon,
				"收到" + count + "条@你的饭否消息", System.currentTimeMillis());
		Intent intent = new Intent(context, HomePage.class);
		if (type == NotificationService.NOTIFICATION_TYPE_MENTION) {
			intent.putExtra(Commons.EXTRA_PAGE, 1);
		}
		PendingIntent contentIntent = PendingIntent.getActivity(context, 0,
				intent, 0);
		notification.setLatestEventInfo(context, "收到" + count + "条@你的饭否消息",
				"点击打开客户端查看@你的消息", contentIntent);
		notification.flags |= Notification.FLAG_AUTO_CANCEL;
		nm.notify(id, notification);
	}

}
