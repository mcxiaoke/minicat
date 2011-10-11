package com.fanfou.app.service;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.fanfou.app.App;
import com.fanfou.app.R;
import com.fanfou.app.api.Api;
import com.fanfou.app.api.ApiException;
import com.fanfou.app.api.DirectMessage;
import com.fanfou.app.config.Actions;
import com.fanfou.app.config.Commons;
import com.fanfou.app.util.IOHelper;
import com.fanfou.app.util.IntentHelper;

/**
 * @author mcxiaoke
 * 
 */
public class PostMessageService extends BaseIntentService {

	private static final String TAG = PostMessageService.class.getSimpleName();
	private NotificationManager nm;
	private Intent mIntent;

	public void log(String message) {
		Log.i(TAG, message);
	}

	private String content;
	private String userId;
	private String userName;

	public PostMessageService() {
		super("UpdateService");

	}

	@Override
	protected void onHandleIntent(Intent intent) {
		if (intent == null) {
			return;
		}
		log("intent=" + intent);
		this.mIntent = intent;
		parseIntent(intent);
		this.nm = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
		// if(!doUpdateStatus()){
		doSend();
		// }
	}

	private void parseIntent(Intent intent) {
		userId = intent.getStringExtra(Commons.EXTRA_USER_ID);
		userName = intent.getStringExtra(Commons.EXTRA_USER_NAME);
		content = intent.getStringExtra(Commons.EXTRA_TEXT);
		if (App.DEBUG) {
			log("parseIntent userId=" + userId);
			log("parseIntent userName=" + userName);
			log("parseIntent content=" + content);
		}
	}

	private boolean doSend() {
		showSendingNotification();
		boolean res = true;
		Api api = App.me.api;
		try {
			DirectMessage result = api.messageCreate(userId, content, null);
			nm.cancel(10);
			if (result == null || result.isNull()) {
				IOHelper.copyToClipBoard(this, content);
				showFailedNotification("私信未发送，内容已保存到剪贴板", "未知原因");
				res = false;
			} else {
				IOHelper.storeDirectMessage(this, result);
				res = true;
//				sendSuccessBroadcast();
			}
		} catch (ApiException e) {
			nm.cancel(10);
			if (App.DEBUG) {
				Log.e(TAG,
						"error: code=" + e.statusCode + " msg="
								+ e.getMessage());
			}
			IOHelper.copyToClipBoard(this, content);
			showFailedNotification("私信未发送，内容已保存到剪贴板", e.getMessage());
		} finally {
			nm.cancel(12);
		}
		return res;
	}

	private int showSendingNotification() {
		int id = 10;
		Notification notification = new Notification(R.drawable.icon,
				"饭否私信正在发送...", System.currentTimeMillis());
		PendingIntent contentIntent = PendingIntent.getActivity(this, 0,
				IntentHelper.getHomeIntent(), 0);
		notification.setLatestEventInfo(this, "饭否私信", "正在发送...", contentIntent);
		notification.flags |= Notification.FLAG_ONGOING_EVENT;
		nm.notify(id, notification);
		return id;
	}

	@SuppressWarnings("unused")
	private int showSuccessNotification() {
		int id = 12;
		Notification notification = new Notification(R.drawable.statusbar_icon,
				"私信发送成功", System.currentTimeMillis());
		PendingIntent contentIntent = PendingIntent
				.getService(this, 0, null, 0);
		notification.setLatestEventInfo(this, "饭否私信", "私信发送成功", contentIntent);
		notification.flags |= Notification.FLAG_AUTO_CANCEL;
		nm.notify(id, notification);
		return id;
	}

	private int showFailedNotification(String title, String message) {
		int id = 11;

		Notification notification = new Notification(R.drawable.statusbar_icon,
				title, System.currentTimeMillis());
		PendingIntent contentIntent = PendingIntent
				.getService(this, 0, null, 0);
		notification.setLatestEventInfo(this, title, message, contentIntent);
		notification.flags |= Notification.FLAG_AUTO_CANCEL;
		nm.notify(id, notification);
		return id;

	}

	private void sendSuccessBroadcast() {
		Intent intent = new Intent(Actions.ACTION_MESSAGE_SEND);
		intent.setPackage(getPackageName());
		sendOrderedBroadcast(intent, null);
	}

}
