package com.mcxiaoke.minicat.util;

import android.app.Activity;
import android.app.ActivityManager;
import android.app.ActivityManager.RunningServiceInfo;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.database.Cursor;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.text.TextPaint;
import android.util.Log;
import android.view.Gravity;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.webkit.MimeTypeMap;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.RelativeLayout.LayoutParams;
import android.widget.TextView;
import android.widget.Toast;
import com.mcxiaoke.minicat.AppContext;
import com.mcxiaoke.minicat.controller.DataController;
import com.mcxiaoke.minicat.dao.model.DirectMessageModel;
import com.mcxiaoke.minicat.dao.model.IBaseColumns;

import java.io.File;
import java.util.Collection;
import java.util.List;

/**
 * 网络连接包需要用到的一些静态工具函数
 *
 * @author mcxiaoke
 * @version 3.7 2012.02.22
 */
public final class Utils {

    private static final String TAG = "Utils";

    /**
     * @param c 集合
     * @return 判断集合对象是否为空
     */
    public static boolean isEmpty(Collection<?> c) {
        return c == null || c.size() == 0;
    }

    public static boolean isEmpty(String str) {
        return str == null || str == "";
    }

    public static void hideKeyboard(final Context context, final EditText input) {
        InputMethodManager imm = (InputMethodManager) context
                .getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(input.getWindowToken(), 0);
    }

    public static ProgressBar createProgress(Context context) {
        ProgressBar p = new ProgressBar(context);
        p.setIndeterminate(true);
        LayoutParams lp = new LayoutParams(40, 40);
        lp.addRule(RelativeLayout.CENTER_IN_PARENT);
        p.setLayoutParams(lp);
        return p;
    }

    public static String getDmSinceId(Cursor c) {
        if (c != null && c.moveToFirst()) {
            final DirectMessageModel dm = DirectMessageModel.from(c);
            if (dm != null) {
                if (AppContext.DEBUG) {
                    Log.d(TAG, "getDmSinceId() dm=" + dm);
                }
                return dm.getId();
            }
        }
        return null;
    }

    public static String getMaxId(Cursor c) {
        if (c != null && c.moveToLast()) {
            return DataController.parseString(c, IBaseColumns.ID);
        }
        return null;
    }

    public static String getSinceId(Cursor c) {
        if (c != null && c.moveToFirst()) {
            return DataController.parseString(c, IBaseColumns.ID);
        }
        return null;
    }

    public static void notify(Context context, CharSequence text) {
        final Toast toast = Toast.makeText(context, text, Toast.LENGTH_SHORT);
        toast.show();
    }

    public static void notifyLong(Context context, CharSequence text) {
        final Toast toast = Toast.makeText(context, text, Toast.LENGTH_LONG);
        toast.setGravity(Gravity.CENTER, 0, 0);
        toast.show();
    }

    public static void notify(Context context, int resId) {
        final Toast toast = Toast.makeText(context, resId, Toast.LENGTH_SHORT);
        toast.setGravity(Gravity.CENTER, 0, 0);
        toast.show();
    }

    public static void open(Context context, final String fileName) {
        String mimeType = MimeTypeMap.getSingleton().getMimeTypeFromExtension(
                getExtension(fileName));
        if (mimeType != null) {
            Intent intent = new Intent(Intent.ACTION_VIEW);
            intent.setDataAndType(Uri.fromFile(new File(fileName)), mimeType);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(intent);
        }
    }

    public static String getExtension(String filename) {
        String filenameArray[] = filename.split("\\.");
        return filenameArray[filenameArray.length - 1].toLowerCase();
    }

    public static void logTime(String event, long time) {
        Log.e("Timer", event + " use time: " + time);
    }

    public static float easeOut(float time, float start, float end,
                                float duration) {
        return end * ((time = time / duration - 1) * time * time + 1) + start;
    }

    public static void setFullScreen(final Activity activity,
                                     final boolean fullscreen) {
        if (fullscreen) {
            activity.getWindow().addFlags(
                    WindowManager.LayoutParams.FLAG_FULLSCREEN);
            activity.getWindow().clearFlags(
                    WindowManager.LayoutParams.FLAG_FORCE_NOT_FULLSCREEN);
        } else {
            activity.getWindow().addFlags(
                    WindowManager.LayoutParams.FLAG_FORCE_NOT_FULLSCREEN);
            activity.getWindow().clearFlags(
                    WindowManager.LayoutParams.FLAG_FULLSCREEN);
        }
    }

    public static void setPortraitOrientation(final Activity activity,
                                              final boolean portrait) {
        if (portrait) {
            activity.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        } else {
            activity.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED);
        }
    }

    public static void lockScreenOrientation(final Activity context) {
        boolean portrait = false;
        if (portrait) {
            context.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        } else {
            if (context.getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) {
                context.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
            }
            if (context.getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT) {
                context.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
            }
        }
    }

    public static void unlockScreenOrientation(final Activity context) {
        boolean portrait = false;
        if (!portrait) {
            context.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED);
        }
    }

    public static void setBoldText(final TextView tv) {
        TextPaint tp = tv.getPaint();
        tp.setFakeBoldText(true);
    }

    /**
     * Checks whether the recording service is currently running.
     *
     * @param ctx the current context
     * @return true if the service is running, false otherwise
     */
    public static boolean isServiceRunning(Context ctx, Class<?> cls) {
        ActivityManager activityManager = (ActivityManager) ctx
                .getSystemService(Context.ACTIVITY_SERVICE);
        List<RunningServiceInfo> services = activityManager
                .getRunningServices(Integer.MAX_VALUE);

        for (RunningServiceInfo serviceInfo : services) {
            ComponentName componentName = serviceInfo.service;
            String serviceName = componentName.getClassName();
            if (serviceName.equals(cls.getName())) {
                return true;
            }
        }
        return false;
    }

    public static void checkAuthorization(Activity context, int statusCode) {
        if (statusCode == 401) {
            AppContext.doLogin(context);
            context.finish();
        }
    }

    public static void mediaScan(Context context, Uri uri) {
        Intent intent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
        intent.setData(uri);
        context.sendBroadcast(intent);
    }

    // another media scan way
    public static void addToMediaStore(Context context, File file) {
        String[] path = new String[]{file.getPath()};
        MediaScannerConnection.scanFile(context, path, null, null);
    }

}
