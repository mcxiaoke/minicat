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
import com.fanfou.app.App.ApnType;
import com.fanfou.app.WritePage;
import com.fanfou.app.api.Api;
import com.fanfou.app.api.ApiException;
import com.fanfou.app.api.Draft;
import com.fanfou.app.api.FanFouApi;
import com.fanfou.app.api.Status;
import com.fanfou.app.db.Contents.DraftInfo;
import com.fanfou.app.util.ImageHelper;

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
 * @version 3.4 2011.12.26
 * 
 */
public class QueueService extends WakefulIntentService {
	public QueueService() {
		super("TaskQueueService");
	}

	private static final String TAG = QueueService.class.getSimpleName();

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
			Api api = FanFouApi.newInstance();
			Status result = null;
			File srcFile = new File(d.filePath);
			if (srcFile == null || !srcFile.exists()) {
				if (d.type == WritePage.TYPE_REPLY) {
					result = api.statusesCreate(d.text, d.replyTo, null, null,
							null, Constants.FORMAT, Constants.MODE);
				} else {
					result = api
							.statusesCreate(d.text, null, null, null,
									d.replyTo, Constants.FORMAT,
									Constants.MODE);
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
					result = api.photosUpload(photo, d.text, null, null,
							Constants.FORMAT, Constants.MODE);
					photo.delete();
				}
			}
			if (result != null && !result.isNull()) {
				// IOHelper.storeStatus(this, result);
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
	protected void doWakefulWork(Intent intent) {
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

		int nums = 0;
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
