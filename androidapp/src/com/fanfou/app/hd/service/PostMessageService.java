package com.fanfou.app.hd.service;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.fanfou.app.hd.App;
import com.fanfou.app.hd.R;
import com.fanfou.app.hd.UILogin;
import com.fanfou.app.hd.api.Api;
import com.fanfou.app.hd.api.ApiException;
import com.fanfou.app.hd.controller.DataController;
import com.fanfou.app.hd.dao.model.DirectMessageModel;
import com.fanfou.app.hd.util.IOHelper;

/**
 * @author mcxiaoke
 * @version 1.0 2011.06.25
 * @version 1.1 2011.10.25
 * @version 2.0 2011.11.18
 * @version 2.1 2011.11.21
 * @version 2.2 2011.12.13
 * @version 2.3 2012.02.22
 * @version 2.4 2012.02.24
 * 
 */
public class PostMessageService extends BaseIntentService {

	private static final String TAG = PostMessageService.class.getSimpleName();
	private NotificationManager nm;

	public void log(String message) {
		Log.i(TAG, message);
	}

	private String content;
	private String userId;
	private String screenName;
	private String reply;

	public PostMessageService() {
		super("UpdateService");

	}

	@Override
	protected void onHandleIntent(Intent intent) {
		if (intent == null) {
			return;
		}
		parseIntent(intent);
		this.nm = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
		doSend();
	}

	private void parseIntent(Intent intent) {
		userId = intent.getStringExtra("id");
		screenName = intent.getStringExtra("screen_name");
		content = intent.getStringExtra("text");
		reply = intent.getStringExtra("reply");
		if (App.DEBUG) {
			log("parseIntent userId=" + userId);
			log("parseIntent userName=" + screenName);
			log("parseIntent content=" + content);
		}
	}

	private boolean doSend() {
		showSendingNotification();
		boolean res = true;
		Api api = App.getApi();
		try {
			DirectMessageModel result = api.createDirectmessage(userId,
					content, reply);
			nm.cancel(10);
			if (result == null) {
				IOHelper.copyToClipBoard(this, content);
				showFailedNotification("私信未发送，内容已保存到剪贴板", "未知原因");
				res = false;
			} else {
				DataController.store(this, result);
				res = true;
				sendSuccessBroadcast();
			}
		} catch (ApiException e) {
			nm.cancel(10);
			if (App.DEBUG) {
				Log.e(TAG,
						"error: code=" + e.statusCode + " msg="
								+ e.getMessage());
			}
			IOHelper.copyToClipBoard(this, content);
			if (e.statusCode >= 500) {
				showFailedNotification("私信未发送，内容已保存到剪贴板",
						getString(R.string.msg_server_error));
			} else {
				showFailedNotification("私信未发送，内容已保存到剪贴板",
						getString(R.string.msg_connection_error));
			}
		} finally {
			nm.cancel(12);
		}
		return res;
	}

	private int showSendingNotification() {
		int id = 10;
		Notification notification = new Notification(R.drawable.ic_notify_icon,
				"饭否私信正在发送...", System.currentTimeMillis());
		PendingIntent contentIntent = PendingIntent.getActivity(this, 0,
				new Intent(), 0);
		notification.setLatestEventInfo(this, "饭否私信", "正在发送...", contentIntent);
		notification.flags |= Notification.FLAG_ONGOING_EVENT;
		nm.notify(id, notification);
		return id;
	}

	@SuppressWarnings("unused")
	private int showSuccessNotification() {
		int id = 12;
		Notification notification = new Notification(R.drawable.ic_notify_icon,
				"私信发送成功", System.currentTimeMillis());
		Intent intent = new Intent(this, UILogin.class);
		PendingIntent contentIntent = PendingIntent.getActivity(this, 0,
				intent, 0);
		notification.setLatestEventInfo(this, "饭否私信", "私信发送成功", contentIntent);
		notification.flags |= Notification.FLAG_AUTO_CANCEL;
		notification.flags |= Notification.FLAG_ONLY_ALERT_ONCE;
		nm.notify(id, notification);
		return id;
	}

	private int showFailedNotification(String title, String message) {
		int id = 11;

		Notification notification = new Notification(R.drawable.ic_notify_icon,
				title, System.currentTimeMillis());
		PendingIntent contentIntent = PendingIntent.getActivity(this, 0,
				new Intent(), 0);
		notification.setLatestEventInfo(this, title, message, contentIntent);
		notification.flags |= Notification.FLAG_AUTO_CANCEL;
		notification.flags |= Notification.FLAG_ONLY_ALERT_ONCE;
		nm.notify(id, notification);
		return id;

	}

	private void sendSuccessBroadcast() {
		Intent intent = new Intent(Constants.ACTION_MESSAGE_SENT);
		intent.setPackage(getPackageName());
		sendOrderedBroadcast(intent, null);
	}

}
