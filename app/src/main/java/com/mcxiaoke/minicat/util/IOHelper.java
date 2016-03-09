/**
 *
 */
package com.mcxiaoke.minicat.util;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore.MediaColumns;

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

/**
 * @author mcxiaoke
 * @version 1.2 2012.02.22
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
                    "/Android/data/" + context.getPackageName() + "/photocache");
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

    public static File getPictureDir(Context context) {
        File photoDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES);
        if (!photoDir.exists()) {
            photoDir.mkdirs();
        }
        return photoDir;
    }

    public static String getRealPathFromURI(Context context, Uri contentUri) {
        // get path from uri like content://media//
        Cursor cursor = null;
        String path = null;
        try {
            cursor = context.getContentResolver().query(contentUri,
                    new String[]{MediaColumns.DATA}, null, null, null);
            if (cursor != null) {
                int column_index = cursor.getColumnIndexOrThrow(MediaColumns.DATA);
                cursor.moveToFirst();
                path = cursor.getString(column_index);
            }
        } catch (Exception ignored) {
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
        if (path == null) {
            path = contentUri.getPath();
        }
        return path;
    }

    public static void ClearCache(Context context) {
        File target = getImageCacheDir(context);
        if (!target.exists()) {
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

    public static boolean copyFile(File src, File dest) {
        try {
            FileChannel srcChannel = new FileInputStream(src).getChannel();
            FileChannel destChannel = new FileOutputStream(dest).getChannel();
            srcChannel.transferTo(0, srcChannel.size(), destChannel);
            srcChannel.close();
            destChannel.close();
            return true;
        } catch (Exception ex) {

        } finally {

        }
        return false;


    }

    public static void copyToClipBoard(Context context, String content) {
        ClipboardManager clipboard = (ClipboardManager) context.getSystemService(Context.CLIPBOARD_SERVICE);
        ClipData clip = ClipData.newPlainText("text", content);
        clipboard.setPrimaryClip(clip);
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

}
