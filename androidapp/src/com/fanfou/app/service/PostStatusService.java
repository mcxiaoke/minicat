package com.fanfou.app.service;

import java.io.File;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.util.Log;

import com.fanfou.app.App;
import com.fanfou.app.App.ApnType;
import com.fanfou.app.DraftsPage;
import com.fanfou.app.R;
import com.fanfou.app.WritePage;
import com.fanfou.app.api.Api;
import com.fanfou.app.api.ApiException;
import com.fanfou.app.api.Draft;
import com.fanfou.app.api.FanFouApi;
import com.fanfou.app.api.Status;
import com.fanfou.app.db.Contents.DraftInfo;
import com.fanfou.app.util.ImageHelper;
import com.fanfou.app.util.StringHelper;

/**
 * @author mcxiaoke
 * @version 1.0 2011.06.10
 * @version 1.1 2011.10.25
 * @version 2.0 2011.10.27
 * @version 2.1 2011.10.28
 * @version 2.2 2011.11.02
 * @version 3.0 2011.11.18
 * @version 3.1 2011.11.28
 * @version 3.2 2011.12.05
 * @version 3.3 2011.12.13
 * @version 3.4 2011.12.26
 * 
 */
public class PostStatusService extends WakefulIntentService {

	private static final String TAG = PostStatusService.class.getSimpleName();
	private NotificationManager nm;

	public void log(String message) {
		Log.d(TAG, message);
	}

	private String text;
	private File srcFile;
	private String location;
	private String relationId;
	private int type;

	public PostStatusService() {
		super("UpdateService");

	}

	@Override
	protected void onHandleIntent(Intent intent) {
		if (intent == null) {
			return;
		}
		if (App.DEBUG) {
			log("intent=" + intent);
		}
		this.nm = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
		parseIntent(intent);
		if (doSend()) {
			sendSuccessBroadcast();
		}
	}

	private void parseIntent(Intent intent) {
		type = intent.getIntExtra(Constants.EXTRA_TYPE, WritePage.TYPE_NORMAL);
		text = intent.getStringExtra(Constants.EXTRA_TEXT);
		srcFile = (File) intent.getSerializableExtra(Constants.EXTRA_DATA);
		relationId = intent.getStringExtra(Constants.EXTRA_IN_REPLY_TO_ID);
		location = intent.getStringExtra(Constants.EXTRA_LOCATION);
		if (App.DEBUG) {
			log("location="
					+ (StringHelper.isEmpty(location) ? "null" : location));
		}
	}

	private boolean doSend() {
		showSendingNotification();
		boolean res = false;
		Api api = FanFouApi.newInstance();
		try {
			Status result = null;
			if (srcFile == null || !srcFile.exists()) {
				if (type == WritePage.TYPE_REPLY) {
					result = api.statusesCreate(text, relationId, null,
							location, null, Constants.FORMAT, Constants.MODE);
				} else {
					result = api.statusesCreate(text, null, null, location,
							relationId, Constants.FORMAT, Constants.MODE);
				}
			} else {
				int quality;
				ApnType apnType = App.getApnType();
				if (apnType == ApnType.WIFI) {
					quality = ImageHelper.IMAGE_QUALITY_HIGH;
				} else if (apnType == ApnType.HSDPA) {
					quality = ImageHelper.IMAGE_QUALITY_MEDIUM;
				} else {
					quality = ImageHelper.IMAGE_QUALITY_LOW;
				}
				File photo = ImageHelper.prepareUploadFile(this, srcFile,
						quality);
				if (photo != null && photo.length() > 0) {
					if (App.DEBUG)
						log("photo file=" + srcFile.getName() + " size="
								+ photo.length() / 1024 + " quality=" + quality+" apnType="+apnType);
					result = api.photosUpload(photo, text, null, location,
							Constants.FORMAT, Constants.MODE);
					photo.delete();
				}

			}
			nm.cancel(0);
			if (result != null && !result.isNull()) {
				res = true;
			}
		} catch (ApiException e) {
			if (App.DEBUG) {
				Log.e(TAG, e.toString());
				e.printStackTrace();
			}
			if (e.statusCode >= 500) {
				showFailedNotification("消息未发送，已保存到草稿箱",
						getString(R.string.msg_server_error));
			} else {
				showFailedNotification("消息未发送，已保存到草稿箱",
						getString(R.string.msg_connection_error));
			}

		} finally {
			nm.cancel(0);
		}
		return res;
	}

	private int showSendingNotification() {
		int id = 0;
		Notification notification = new Notification(R.drawable.ic_notify_icon,
				"饭否消息正在发送...", System.currentTimeMillis());
		PendingIntent contentIntent = PendingIntent.getActivity(this, 0,
				new Intent(), 0);
		notification.setLatestEventInfo(this, "饭否消息", "正在发送...", contentIntent);
		notification.flags |= Notification.FLAG_ONGOING_EVENT;
		nm.notify(id, notification);
		return id;
	}

	private int showFailedNotification(String title, String message) {
		doSaveDrafts();
		int id = 1;
		Notification notification = new Notification(R.drawable.ic_notify_icon,
				title, System.currentTimeMillis());
		Intent intent = new Intent(this, DraftsPage.class);
		PendingIntent contentIntent = PendingIntent.getActivity(this, 0,
				intent, PendingIntent.FLAG_UPDATE_CURRENT);
		notification.setLatestEventInfo(this, title, message, contentIntent);
		notification.flags |= Notification.FLAG_AUTO_CANCEL;
		nm.notify(id, notification);
		return id;
	}

	private void doSaveDrafts() {
		Draft d = new Draft();
		d.text = text;
		d.filePath = srcFile == null ? "" : srcFile.getPath();
		d.replyTo = relationId;
		d.type = type;
		Uri resultUri = getContentResolver().insert(DraftInfo.CONTENT_URI,
				d.toContentValues());
		if (App.DEBUG) {
			log("doSaveDrafts resultUri=" + resultUri + " type=" + d.type
					+ " text=" + d.text + " filepath=" + d.filePath);
		}
	}

	private void sendSuccessBroadcast() {
		Intent intent = new Intent(Constants.ACTION_STATUS_SENT);
		intent.setPackage(getPackageName());
		sendOrderedBroadcast(intent, null);
	}

}
