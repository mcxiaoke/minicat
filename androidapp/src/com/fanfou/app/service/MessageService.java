package com.fanfou.app.service;

import java.io.File;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.fanfou.app.App;
import com.fanfou.app.R;
import com.fanfou.app.SendPage;
import com.fanfou.app.WritePage;
import com.fanfou.app.api.Api;
import com.fanfou.app.api.ApiException;
import com.fanfou.app.api.DirectMessage;
import com.fanfou.app.api.Status;
import com.fanfou.app.config.Commons;
import com.fanfou.app.http.Response;
import com.fanfou.app.http.ResponseCode;
import com.fanfou.app.util.ImageHelper;
import com.fanfou.app.util.OptionHelper;
import com.fanfou.app.util.StringHelper;
import com.fanfou.app.util.Utils;

/**
 * @author mcxiaoke
 * 
 */
public class MessageService extends BaseIntentService {

	private static final String TAG = MessageService.class.getSimpleName();
	private NotificationManager nm;
	private Intent mIntent;

	public void log(String message) {
		Log.e(TAG, message);
	}

	private String content;
	private String userId;
	private String inReplyToId;

	public MessageService() {
		super("UpdateService");

	}

	@Override
	protected void onHandleIntent(Intent intent) {
		if (intent == null) {
			return;
		}
		log("intent=" + intent);
		this.mIntent = intent;
		this.nm = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
		parseIntent(intent);
		// if(!doUpdateStatus()){
		doSend();
		// }
	}

	private void parseIntent(Intent intent) {
		userId=intent.getStringExtra(Commons.EXTRA_USER_ID);
		content = intent.getStringExtra(Commons.EXTRA_TEXT);
		inReplyToId = intent.getStringExtra(Commons.EXTRA_IN_REPLY_TO_ID);
		if(App.DEBUG){
			log("parseIntent userId="+userId);
			log("parseIntent content="+content);
		}
	}

	private boolean doSend() {
		showSendingNotification();
		boolean res = true;
		Api api = App.me.api;
		try {
			DirectMessage result = api.messageCreate(userId, content, inReplyToId);

			nm.cancel(0);
			if (result == null || result.isNull()) {
				showFailedNotification("私信发送失败", "原因未知");
				res = false;
			}
		} catch (ApiException e) {
			nm.cancel(0);
			if (App.DEBUG) {
				Log.e(TAG, "error: code=" + e.statusCode + " msg="
						+ e.getMessage());
				e.printStackTrace();
			}		
			if(e.statusCode>=500||e.statusCode==Response.ERROR_NOT_CONNECTED){
				showRetryNotification("错误：" + e.getMessage());
			}else{
				showFailedNotification("私信发送失败", e.getMessage());
			}
		} finally {
			nm.cancel(2);
		}
		return res;
	}

	private int showSendingNotification() {
		int id = 0;
		Notification notification = new Notification(R.drawable.icon,
				"私信正在发送...", System.currentTimeMillis());
		PendingIntent contentIntent = PendingIntent
				.getService(this, 0, null, 0);
		notification.setLatestEventInfo(this, "饭否私信", "正在发送...", contentIntent);
		notification.flags |= Notification.FLAG_ONGOING_EVENT;
		nm.notify(id, notification);
		return id;
	}

	private int showSuccessNotification() {
		int id = 2;
		Notification notification = new Notification(R.drawable.statusbar_icon, "私信发送成功",
				System.currentTimeMillis());
		PendingIntent contentIntent = PendingIntent
				.getService(this, 0, null, 0);
		notification.setLatestEventInfo(this, "饭否私信", "私信发送成功", contentIntent);
		notification.flags |= Notification.FLAG_AUTO_CANCEL;
		nm.notify(id, notification);
		return id;
	}

	private int showRetryNotification(String msg) {
		int id = 1;

		Notification notification = new Notification(R.drawable.statusbar_icon,
				"网络异常，私信发送失败", System.currentTimeMillis());
		PendingIntent contentIntent = PendingIntent.getService(this, 0,
				mIntent, PendingIntent.FLAG_UPDATE_CURRENT);
		notification.setLatestEventInfo(this, "网络异常，私信发送失败", "私信发送失败，点击重新发送",
				contentIntent);
		notification.flags |= Notification.FLAG_AUTO_CANCEL;
		nm.notify(id, notification);
		return id;

	}

	private int showFailedNotification(String title, String message) {
		int id = 1;

		Notification notification = new Notification(R.drawable.statusbar_icon,
				title, System.currentTimeMillis());
		PendingIntent contentIntent = PendingIntent
				.getService(this, 0, null, 0);
		notification.setLatestEventInfo(this, title, message, contentIntent);
		notification.flags |= Notification.FLAG_AUTO_CANCEL;
		nm.notify(id, notification);
		return id;

	}

}
