package com.fanfou.app.service;

import java.io.File;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

import android.content.ContentUris;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.util.Log;

import com.fanfou.app.App;
import com.fanfou.app.R;
import com.fanfou.app.WritePage;
import com.fanfou.app.api.Api;
import com.fanfou.app.api.ApiException;
import com.fanfou.app.api.Draft;
import com.fanfou.app.api.FanFouApiConfig;
import com.fanfou.app.api.Status;
import com.fanfou.app.config.Actions;
import com.fanfou.app.db.Contents.DraftInfo;
import com.fanfou.app.util.IOHelper;
import com.fanfou.app.util.ImageHelper;
import com.fanfou.app.util.OptionHelper;

/**
 * @author mcxiaoke
 * @version 1.0 2011.10.28
 * @version 1.1 2011.11.02
 * @version 2.0 2011.11.15
 * @version 3.0 2011.11.18
 * @version 3.1 2011.11.22
 * @version 3.2 2011.11.28
 * @version 3.3 2011.12.05
 * 
 */
public class TaskQueueService extends WakefulIntentService {
	public TaskQueueService() {
		super("TaskQueueService");
	}

	private static final String TAG = TaskQueueService.class.getSimpleName();

	private void log(String message) {
		Log.d(TAG, message);
	}

	private void deleteDraft(final int id) {
		if (id > -1) {
			Uri uri = ContentUris.withAppendedId(DraftInfo.CONTENT_URI, id);
			getContentResolver().delete(uri, null, null);
		}
	}

	private boolean doSend(final Draft d) {
		boolean res = false;
		try {
			Api api = App.getApi();
			Status result = null;
			File srcFile = new File(d.filePath);
			if (srcFile == null || !srcFile.exists()) {
				if (d.type == WritePage.TYPE_REPLY) {
					result = api.statusesCreate(d.text, d.replyTo, null, null,
							null, FanFouApiConfig.FORMAT_HTML,
							FanFouApiConfig.MODE_LITE);
				} else {
					result = api.statusesCreate(d.text, null, null, null,
							d.replyTo, FanFouApiConfig.FORMAT_HTML,
							FanFouApiConfig.MODE_LITE);
				}
			} else {
				int quality = OptionHelper.parseInt(
						R.string.option_photo_quality,
						String.valueOf(ImageHelper.IMAGE_QUALITY_MEDIUM));
				File photo = ImageHelper.prepareUploadFile(this, srcFile,
						quality);
				if (photo != null && photo.length() > 0) {
					if (App.DEBUG)
						log("photo file=" + srcFile.getName() + " size="
								+ photo.length() / 1024 + " quality=" + quality);
					result = api.photosUpload(photo, d.text, null, null,
							FanFouApiConfig.FORMAT_HTML,
							FanFouApiConfig.MODE_LITE);
					photo.delete();
				}
			}
			if (result != null && !result.isNull()) {
//				IOHelper.storeStatus(this, result);
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
		BlockingQueue<Draft> queue = new LinkedBlockingQueue<Draft>();
		boolean running = true;
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
		
		int nums=0;
		while (running) {
			final Draft d = queue.poll();
			if (d != null) {
				if (App.DEBUG) {
					log("Start sending draft: text=" + d.text + " file="
							+ d.filePath);
				}
				if (doSend(d)) {
					deleteDraft(d.id);
					nums++;
					if (App.DEBUG) {
						log("Send draft successful: id=" + d.id + " text="
								+ d.text + " filepath=" + d.filePath);
					}
				}
			} else {
				running = false;
			}
		}
		if(nums>0){
			sendSuccessBroadcast();
		}
	}
	
	private void sendSuccessBroadcast() {
		Intent intent = new Intent(Actions.ACTION_STATUS_SENT);
		intent.setPackage(getPackageName());
		sendOrderedBroadcast(intent, null);
	}

}
