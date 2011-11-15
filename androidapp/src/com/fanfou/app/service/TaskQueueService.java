package com.fanfou.app.service;

import java.io.File;
import java.util.concurrent.BlockingDeque;
import java.util.concurrent.LinkedBlockingDeque;

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
import com.fanfou.app.api.Status;
import com.fanfou.app.db.Contents.DraftInfo;
import com.fanfou.app.util.ImageHelper;
import com.fanfou.app.util.OptionHelper;

/**
 * @author mcxiaoke
 * @version 1.0 2011.10.28
 * @version 1.1 2011.11.02
 * @version 2.0 2011.11.15
 * 
 */
public class TaskQueueService extends BaseIntentService {
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
			Api api = App.me.api;
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

	@Override
	protected void onHandleIntent(Intent intent) {
		sendQueue();
	}

	private void sendQueue() {
		BlockingDeque<Draft> queue = new LinkedBlockingDeque<Draft>();
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
		while (running) {
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
				running = false;
			}
		}
	}

}
