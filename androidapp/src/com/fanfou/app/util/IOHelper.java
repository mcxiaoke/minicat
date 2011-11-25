/**
 * 
 */
package com.fanfou.app.util;

import java.io.Closeable;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.channels.FileChannel;
import java.text.SimpleDateFormat;
import java.util.Date;

import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore.MediaColumns;
import android.text.ClipboardManager;

import com.fanfou.app.api.DirectMessage;
import com.fanfou.app.api.Status;
import com.fanfou.app.api.User;
import com.fanfou.app.db.Contents.DirectMessageInfo;
import com.fanfou.app.db.Contents.DraftInfo;
import com.fanfou.app.db.Contents.StatusInfo;
import com.fanfou.app.db.Contents.UserInfo;

/**
 * @author mcxiaoke
 * @version 1.0 2011.05.19
 * @version 1.1 2011.10.11
 * 
 */
public final class IOHelper {
	public static final SimpleDateFormat FILENAME_FORMAT = new SimpleDateFormat(
			"'fanfou'_yyyyMMdd_HHmmss.'jpg'");

	private IOHelper() {
		throw new IllegalAccessError("此类为静态工具类，不能被实例化");
	}

	public static File getDownloadDir(Context context) {
		File cacheDir;
		if (Environment.getExternalStorageState().equals(
				Environment.MEDIA_MOUNTED)) {
			cacheDir = new File(Environment.getExternalStorageDirectory(),
					"/download");
		} else {
			cacheDir = context.getCacheDir();
		}
		if (!cacheDir.exists()) {
			cacheDir.mkdirs();
		}
		return cacheDir;
	}

	public static File getImageCacheDir(Context context) {
		File cacheDir;
		if (Environment.getExternalStorageState().equals(
				Environment.MEDIA_MOUNTED)) {
			cacheDir = new File(Environment.getExternalStorageDirectory(),
					"/Android/data/"+context.getPackageName()+"/photocache");
		} else {
			cacheDir = context.getCacheDir();
		}
		if (!cacheDir.exists()) {
			cacheDir.mkdirs();
			File nomedia = new File(cacheDir, ".nomedia");
			if (!nomedia.exists()) {
				nomedia.mkdirs();
			}
		}
		return cacheDir;
	}

	public static File getPhotoFilePath(Context context) {
		File baseDir = getPhotoDir(context);
		if (!baseDir.exists()) {
			baseDir.mkdirs();
		}
		Date date = new Date();
		String filename = FILENAME_FORMAT.format(date);
		return new File(baseDir, filename);
	}

	public static File getPhotoDir(Context context) {
		File photoDir;
		if (Environment.getExternalStorageState().equals(
				Environment.MEDIA_MOUNTED)) {
			photoDir = new File(Environment.getExternalStorageDirectory(),
					"/DCIM/FANFOU");
		} else {
			photoDir = context.getCacheDir();
		}
		if (!photoDir.exists()) {
			photoDir.mkdirs();
		}
		return photoDir;
	}

	public static String getRealPathFromURI(Context context, Uri contentUri) {
		// get path from uri like content://media//
		Cursor cursor = context.getContentResolver().query(contentUri,
				new String[] { MediaColumns.DATA }, null, null, null);
		String path = null;
		if (cursor != null) {
			int column_index = cursor.getColumnIndexOrThrow(MediaColumns.DATA);
			cursor.moveToFirst();
			path = cursor.getString(column_index);
		} else {
			path = null;
		}
		cursor.close();
		if (path == null) {
			path = contentUri.getPath();
		}
		return path;
	}

	public static void ClearCache(Context context) {
		File target = getImageCacheDir(context);
		if (target.exists() == false) {
			return;
		}
		if (target.isFile()) {
			target.delete();
		}

		if (target.isDirectory()) {
			File[] files = target.listFiles();
			for (int i = 0; i < files.length; i++) {
				deleteDir(files[i]);
			}
			target.delete();
		}
	}

	public static void ClearBigPictures(Context context) {
		deleteDir(getImageCacheDir(context), 6 * 1024);
	}

	public static void deleteDir(File target) {
		if (!target.exists()) {
			return;
		}
		if (target.isFile()) {
			target.delete();
		}

		if (target.isDirectory()) {
			File[] files = target.listFiles();
			for (File file : files) {
				deleteDir(file);
			}
			target.delete();
		}
	}

	public static void deleteDir(File target, int minFileSize) {
		if (!target.exists()) {
			return;
		}
		if (target.isFile()) {
			if (target.length() > minFileSize) {
				target.delete();
			}
		}

		if (target.isDirectory()) {
			File[] files = target.listFiles();
			for (File file : files) {
				deleteDir(file, minFileSize);
			}
		}
	}

	public static void copyFile(File src, File dest) throws IOException {
		FileChannel srcChannel = new FileInputStream(src).getChannel();
		FileChannel destChannel = new FileOutputStream(dest).getChannel();
		srcChannel.transferTo(0, srcChannel.size(), destChannel);
		srcChannel.close();
		destChannel.close();
	}

	public static void copyToClipBoard(Context context, String content) {
		ClipboardManager cm = (ClipboardManager) context
				.getSystemService(Context.CLIPBOARD_SERVICE);
		cm.setText(content);
	}

	public static void copyStream(InputStream in, OutputStream out,
			int bufferSize) throws IOException {
		byte[] buf = new byte[bufferSize];

		int len = 0;

		while ((len = in.read(buf)) >= 0) {
			out.write(buf, 0, len);
		}
	}

	public static void copyStream(InputStream in, OutputStream out)
			throws IOException {
		byte[] b = new byte[8 * 1024];
		int read;
		while ((read = in.read(b)) != -1) {
			out.write(b, 0, read);
		}
	}

	public static void closeStream(Closeable stream) {
		if (stream != null) {
			try {
				stream.close();
			} catch (IOException e) {
			}
		}
	}

	public static void forceClose(Closeable c) {
		try {
			if (c != null) {
				c.close();
			}
		} catch (IOException e) {
		}
	}

	public static void storeDirectMessage(Context context, DirectMessage dm) {
		ContentResolver cr = context.getContentResolver();
		cr.insert(DirectMessageInfo.CONTENT_URI, dm.toContentValues());
	}

	public static void storeStatus(Context context, Status s) {
		ContentResolver cr = context.getContentResolver();
		cr.insert(StatusInfo.CONTENT_URI, s.toContentValues());
	}

	public static void storeUser(Context context, User u) {
		ContentResolver cr = context.getContentResolver();
		cr.insert(UserInfo.CONTENT_URI, u.toContentValues());
	}

	public static void cleanDB(Context context) {
		ContentResolver cr = context.getContentResolver();
		cr.delete(StatusInfo.CONTENT_URI, null, null);
		cr.delete(UserInfo.CONTENT_URI, null, null);
		cr.delete(DirectMessageInfo.CONTENT_URI, null, null);
		cr.delete(DraftInfo.CONTENT_URI, null, null);
	}

}
