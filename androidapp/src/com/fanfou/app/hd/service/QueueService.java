package com.fanfou.app.hd.service;

import java.io.File;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.util.Log;

import com.fanfou.app.hd.App;
import com.fanfou.app.hd.App.ApnType;
import com.fanfou.app.hd.api.Api;
import com.fanfou.app.hd.api.ApiException;
import com.fanfou.app.hd.controller.DataController;
import com.fanfou.app.hd.dao.model.RecordColumns;
import com.fanfou.app.hd.dao.model.RecordModel;
import com.fanfou.app.hd.dao.model.StatusModel;
import com.fanfou.app.hd.util.ImageHelper;

/**
 * @author mcxiaoke
 * @version 1.0 2011.10.28
 * @version 1.1 2011.11.02
 * @version 2.0 2011.11.15
 * @version 3.0 2011.11.18
 * @version 3.1 2011.11.22
 * @version 3.2 2011.11.28
 * @version 3.3 2011.12.05
 * @version 3.4 2011.12.13
 * @version 3.5 2011.12.26
 * @version 4.0 2012.02.22
 * 
 */
public class QueueService extends BaseIntentService {
	public QueueService() {
		super("TaskQueueService");
	}

	public static void start(Context context) {
		context.startService(new Intent(context, QueueService.class));
	}

	private static final String TAG = QueueService.class.getSimpleName();

	private void log(String message) {
		Log.d(TAG, message);
	}

	private boolean deleteRecord(long id) {
		return DataController.deleteRecord(this, id)>0;
	}

	private boolean doSend(final RecordModel rm) {
		boolean res = false;
		try {
			Api api = App.getApi();
			StatusModel result = null;
			File srcFile = new File(rm.getFile());
			if (srcFile == null || !srcFile.exists()) {
				result = api.updateStatus(rm.text, rm.reply, null, null);
			} else {
				int quality;
				ApnType apnType = App.getApnType();
				if (apnType == ApnType.WIFI) {
					quality = ImageHelper.IMAGE_QUALITY_HIGH;
				} else {
					quality = ImageHelper.IMAGE_QUALITY_MEDIUM;
				}
				File photo = ImageHelper.prepareUploadFile(this, srcFile,
						quality);
				if (photo != null && photo.length() > 0) {
					if (App.DEBUG)
						log("photo file=" + srcFile.getName() + " size="
								+ photo.length() / 1024 + " quality=" + quality
								+ " apnType=" + apnType);
					result = api.uploadPhoto(photo, rm.text, null);
					photo.delete();
				}
			}
			if (result != null) {
				DataController.store(this, result);
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

	@Override
	protected void onHandleIntent(Intent intent) {
		sendQueue();
	}

	private void sendQueue() {
		BlockingQueue<RecordModel> queue = new LinkedBlockingQueue<RecordModel>();
		boolean running = true;
		Cursor cursor = getContentResolver().query(RecordColumns.CONTENT_URI,
				null, null, null, null);
		if (cursor != null && cursor.moveToFirst()) {
			while (!cursor.isAfterLast()) {
				final RecordModel rm = RecordModel.from(cursor);
				if (rm != null) {
					queue.add(rm);
				}
				cursor.moveToNext();
			}
		}

		int nums = 0;
		while (running) {
			final RecordModel rm = queue.poll();
			if (rm != null) {
				if (doSend(rm)) {
					deleteRecord(rm.getId());
					nums++;
				}
			} else {
				running = false;
			}
		}
		if (nums > 0) {
			sendSuccessBroadcast();
		}
	}

	private void sendSuccessBroadcast() {
		Intent intent = new Intent(Constants.ACTION_STATUS_SENT);
		intent.setPackage(getPackageName());
		sendOrderedBroadcast(intent, null);
	}

}
