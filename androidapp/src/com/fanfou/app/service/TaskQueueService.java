package com.fanfou.app.service;

import java.io.File;
import java.util.concurrent.BlockingDeque;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.atomic.AtomicBoolean;

import com.fanfou.app.App;
import com.fanfou.app.R;
import com.fanfou.app.WritePage;
import com.fanfou.app.api.Api;
import com.fanfou.app.api.ApiException;
import com.fanfou.app.api.Draft;
import com.fanfou.app.api.Status;
import com.fanfou.app.config.Commons;
import com.fanfou.app.db.Contents.DraftInfo;
import com.fanfou.app.util.ImageHelper;
import com.fanfou.app.util.IntentHelper;
import com.fanfou.app.util.OptionHelper;

import android.app.NotificationManager;
import android.app.Service;
import android.content.ContentUris;
import android.content.Intent;
import android.content.res.Configuration;
import android.database.Cursor;
import android.net.Uri;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.util.Log;

/**
 * @author mcxiaoke
 * @version 1.0 2011.10.28
 * @version 1.1 2011.11.02
 * 
 */
public class TaskQueueService extends Service {
	private static final String TAG = TaskQueueService.class.getSimpleName();

	private static final int CORE_POOL_SIZE = 2;

	public static final int TYPE_DRAFTS_LIST = 101;
	private static final int TYPE_DEFAULT = 0;

	private NotificationManager mNM;
	private ExecutorService mExecutorService;
	private TaskHandler mHandler;
//	private HttpClient mHttpClient;
	private Api api;

	private void log(String message) {
		Log.d(TAG, message);
	}

	@Override
	public void onCreate() {
		super.onCreate();
		mNM = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
		mExecutorService = Executors.newFixedThreadPool(CORE_POOL_SIZE);
		mHandler = new TaskHandler();
//		mHttpClient = NetworkHelper.newHttpClient();
		api = App.me.api;
	}

	@Override
	public void onStart(Intent intent, int startId) {
		onCommandReceived(intent);
	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		if(intent==null){
			stopSelf();
		}else{
			if (App.DEBUG) {
				log("onStartCommand");
				IntentHelper.logIntent(TAG, intent);
			}
			onCommandReceived(intent);
		}
		return START_STICKY;
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
	}

	@Override
	public void onConfigurationChanged(Configuration newConfig) {
		super.onConfigurationChanged(newConfig);
	}

	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}

	private void deleteDraft(final int id) {
		if (id > -1) {
			Uri uri = ContentUris.withAppendedId(DraftInfo.CONTENT_URI, id);
			getContentResolver().delete(uri, null, null);
		}
	}

	private void addDraft(final Draft d) {
		getContentResolver().insert(DraftInfo.CONTENT_URI, d.toContentValues());
	}

	private boolean doSend(final Draft d) {
		boolean res = false;
		try {
			Status result = null;
			if (d.type == WritePage.TYPE_REPLY) {
				result = api.statusUpdate(d.text, d.replyTo, null, null, null);
			} else {
				File srcFile = new File(d.filePath);
				if (srcFile == null || !srcFile.exists()) {
					result = api.statusUpdate(d.text, null, null, null,
							d.replyTo);
				} else {
					int quality = OptionHelper.parseInt(this,
							R.string.option_photo_quality,
							String.valueOf(ImageHelper.IMAGE_QUALITY_MEDIUM));
					File photo = ImageHelper.prepareUploadFile(this, srcFile,
							quality);
					if (photo != null && photo.length() > 0) {
						if (App.DEBUG)
							log("photo file=" + srcFile.getName() + " size="
									+ photo.length() / 1024 + " quality="
									+ quality);
						result = api.photoUpload(photo, d.text, null, null);
					}
					photo.delete();
				}
			}
			if (result != null && !result.isNull()) {
				res = true;
			}
		} catch (ApiException e) {
			if (App.DEBUG) {
				Log.e(TAG,
						"error: code=" + e.statusCode + " msg="
								+ e.getMessage());
			}
		}
		return res;
	}

	private class DraftsTaskRunnable implements Runnable {
		// 逻辑
		// 读取数据库中的全部草稿
		// 草稿全部添加到队列
		// 队列按先后顺序发送草稿
		// 发送失败则添加到草稿箱
		@Override
		public void run() {
			BlockingDeque<Draft> queue = new LinkedBlockingDeque<Draft>();
			AtomicBoolean running = new AtomicBoolean(true);

			Cursor cursor = getContentResolver().query(DraftInfo.CONTENT_URI,
					DraftInfo.COLUMNS, null, null, null);
			if (cursor != null && cursor.moveToFirst()) {
				while (!cursor.isAfterLast()) {
					final Draft d = Draft.parse(cursor);
					if (d != null) {
						queue.add(d);
					}
					cursor.moveToNext();
				}
			}
			while (running.get()) {
				final Draft d = queue.poll();
				if (d != null) {
					if (App.DEBUG) {
						log("Start sending draft: text=" + d.text + " file="
								+ d.filePath);
					}
					if (doSend(d)) {
						deleteDraft(d.id);
						if (App.DEBUG) {
							log("Send draft successful: id=" + d.id + " text="
									+ d.text + " filepath=" + d.filePath);
						}
					}
				} else {
					running.set(false);
				}
			}
			stopSelf();
		}
	}

	private class TaskHandler extends Handler {

		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
		}

	}

	private void onCommandReceived(Intent intent) {
		
		int type = intent.getIntExtra(Commons.EXTRA_TYPE, TYPE_DEFAULT);
		if (App.DEBUG) {
			log("onCommandReceived type=" + type);
		}
		switch (type) {
		case TYPE_DRAFTS_LIST:
			// if(App.me.apnType==ApnType.NONE){
			// stopSelf();
			// }else{
			mExecutorService.submit(new DraftsTaskRunnable());
			// }
			break;
		default:
			stopSelf();
			break;
		}
	}

}
