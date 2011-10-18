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
import com.fanfou.app.WritePage;
import com.fanfou.app.api.Api;
import com.fanfou.app.api.ApiException;
import com.fanfou.app.api.Status;
import com.fanfou.app.config.Actions;
import com.fanfou.app.config.Commons;
import com.fanfou.app.util.IOHelper;
import com.fanfou.app.util.ImageHelper;
import com.fanfou.app.util.IntentHelper;
import com.fanfou.app.util.OptionHelper;
import com.fanfou.app.util.StringHelper;

/**
 * @author mcxiaoke
 * 
 */
public class PostStatusService extends WakefulIntentService {

	private static final String TAG = PostStatusService.class.getSimpleName();
	private NotificationManager nm;
	private Intent mIntent;

	public void log(String message) {
		Log.d(TAG, message);
	}

	private String content;
	private File srcFile;
	private String location;
	private Status src;
	private int type;

	public PostStatusService() {
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
		parseIntent();
		// if(!doUpdateStatus()){
		doUpdateStatus();
		// }
	}

	private void parseIntent() {
		type = mIntent.getIntExtra(Commons.EXTRA_TYPE, WritePage.TYPE_NORMAL);
		content = mIntent.getStringExtra(Commons.EXTRA_TEXT);
		srcFile = (File) mIntent.getSerializableExtra(Commons.EXTRA_FILE);
		src = (Status) mIntent.getSerializableExtra(Commons.EXTRA_STATUS);
		location = mIntent.getStringExtra(Commons.EXTRA_LOCATION);
		if (App.DEBUG) {
			log("location="
					+ (StringHelper.isEmpty(location) ? "null" : location));
		}
	}

	private boolean doUpdateStatus() {
		showSendingNotification();
		boolean res = true;
		Api api = App.me.api;
		try {
			Status result = null;
			String replyId = null;
			String repostId = null;
			if (src != null) {
				if (type == WritePage.TYPE_REPOST) {
					repostId = src.id;
				} else {
					replyId = src.id;
				}
			}
			if (srcFile == null || srcFile.length() == 0) {
				result = api.statusUpdate(content, replyId, null, location,
						repostId);
			} else {
				int quality = OptionHelper.parseInt(this,
						R.string.option_photo_quality);
				if (quality < 0) {
					quality = ImageHelper.IMAGE_QUALITY_MEDIUM;
				}
				File photo = ImageHelper.prepareUploadFile(this, srcFile,
						quality);
				if (photo != null && photo.length() > 0) {
					if (App.DEBUG)
						log("photo file=" + srcFile.getName() + " size="
								+ photo.length() / 1024 + " quality=" + quality);
					result = api.photoUpload(photo, content, null, location);
				}
				photo.delete();
			}
			nm.cancel(0);
			if (result == null || result.isNull()) {
				// showFailedNotification("饭否消息未发送，内容重复",
				// "消息内容重复，发送不成功，点击重新编辑");
				// 重复消息不要显示错误提示
				res = false;
			} else {
				IOHelper.storeStatus(this, result);
				res = true;
				sendSuccessBroadcast();
			}
		} catch (ApiException e) {
			if (App.DEBUG) {
				Log.e(TAG,
						"error: code=" + e.statusCode + " msg="
								+ e.getMessage());
				e.printStackTrace();
			}
			showFailedNotification("饭否消息发送失败,点击重新编辑", e.getMessage());
		} finally {
			nm.cancel(0);
		}
		return res;
	}

	private int showSendingNotification() {
		int id = 0;
		Notification notification = new Notification(R.drawable.ic_notify_home,
				"饭否消息正在发送...", System.currentTimeMillis());
		PendingIntent contentIntent = PendingIntent.getActivity(this, 0,
				new Intent(), 0);
		notification.setLatestEventInfo(this, "饭否消息", "正在发送...", contentIntent);
		notification.flags |= Notification.FLAG_ONGOING_EVENT;
		nm.notify(id, notification);
		return id;
	}

	@SuppressWarnings("unused")
	private int showSuccessNotification() {
		int id = 2;
		Notification notification = new Notification(R.drawable.ic_notify_home,
				"饭否消息发送成功", System.currentTimeMillis());
		PendingIntent contentIntent = PendingIntent.getActivity(this, 0, new Intent(), 0);
		notification.setLatestEventInfo(this, "饭否消息", "消息发送成功", contentIntent);
		notification.flags |= Notification.FLAG_AUTO_CANCEL;
		nm.notify(id, notification);
		return id;
	}

	private int showFailedNotification(String title, String message) {
		int id = 1;
		Notification notification = new Notification(R.drawable.ic_notify_home,
				title, System.currentTimeMillis());
		Intent intent = new Intent(this, WritePage.class);
		intent.putExtra(Commons.EXTRA_TEXT, content);
		intent.putExtra(Commons.EXTRA_TYPE, type);
		if (src != null) {
			intent.putExtra(Commons.EXTRA_STATUS, src);
		}
		PendingIntent contentIntent = PendingIntent.getActivity(this, 0,
				intent, PendingIntent.FLAG_UPDATE_CURRENT);
		notification.setLatestEventInfo(this, title, message, contentIntent);
		notification.flags |= Notification.FLAG_AUTO_CANCEL;
		nm.notify(id, notification);
		return id;
	}

	private void sendSuccessBroadcast() {
		Intent intent = new Intent(Actions.ACTION_STATUS_SEND);
		intent.setPackage(getPackageName());
		sendOrderedBroadcast(intent, null);
	}

}
