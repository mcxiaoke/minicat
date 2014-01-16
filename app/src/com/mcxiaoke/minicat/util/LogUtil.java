package com.mcxiaoke.minicat.util;

import android.content.Context;
import android.text.TextUtils;
import android.util.Log;
import com.mcxiaoke.minicat.AppContext;

// @SuppressWarnings("unused")
public class LogUtil {
    /**
     * 默认的文库日志Tag标签
     */
    public final static String DEFAULT_TAG = "LogUtil";

    /**
     * 此常量用于控制是否打日志到Logcat中 release版本中本变量应置为false
     */

    private final static boolean LOGGABLE = AppContext.DEBUG;

    /**
     * 打印debug级别的log
     *
     * @param tag tag标签
     * @param str 内容
     */
    public static void d(String tag, String str) {
        if (LOGGABLE && !TextUtils.isEmpty(str)) {
            Log.d(tag, str);
        }
    }

    /**
     * 打印debug级别的log
     *
     * @param str 内容
     */
    public static void d(String str) {
        if (LOGGABLE && !TextUtils.isEmpty(str)) {
            Log.d(DEFAULT_TAG, str);
        }
    }

    /**
     * 打印warning级别的log
     *
     * @param tag tag标签
     * @param str 内容
     */
    public static void w(String tag, String str) {
        if (LOGGABLE && !TextUtils.isEmpty(str)) {
            Log.w(tag, str);
        }
    }

    /**
     * 打印warning级别的log
     *
     * @param str 内容
     */
    public static void w(String str) {
        if (LOGGABLE && !TextUtils.isEmpty(str)) {
            Log.w(DEFAULT_TAG, str);
        }
    }

    /**
     * 打印error级别的log
     *
     * @param tag tag标签
     * @param str 内容
     */
    public static void e(String tag, String str, Throwable tr) {
        if (LOGGABLE && !TextUtils.isEmpty(str)) {
            Log.e(tag, str);
            tr.printStackTrace();
        }
    }

    /**
     * 打印error级别的log
     *
     * @param tag tag标签
     * @param str 内容
     */
    public static void e(String tag, String str) {
        if (LOGGABLE && !TextUtils.isEmpty(str)) {
            Log.e(tag, str);
        }
    }

    /**
     * 打印error级别的log
     *
     * @param str 内容
     */
    public static void e(String str) {
        if (LOGGABLE && !TextUtils.isEmpty(str)) {
            Log.e(DEFAULT_TAG, str);
        }
    }

    /**
     * 打印info级别的log
     *
     * @param tag tag标签
     * @param str 内容
     */
    public static void i(String tag, String str) {
        if (LOGGABLE && !TextUtils.isEmpty(str)) {
            Log.i(tag, str);
        }
    }

    /**
     * 打印info级别的log
     *
     * @param str 内容
     */
    public static void i(String str) {
        if (LOGGABLE && !TextUtils.isEmpty(str)) {
            Log.i(DEFAULT_TAG, str);
        }
    }

    /**
     * 打印verbose级别的log
     *
     * @param tag tag标签
     * @param str 内容
     */
    public static void v(String tag, String str) {
        if (LOGGABLE && !TextUtils.isEmpty(str)) {
            Log.v(tag, str);
        }
    }

    /**
     * 打印verbose级别的log
     *
     * @param str 内容
     */
    public static void v(String str) {
        if (LOGGABLE && !TextUtils.isEmpty(str)) {
            Log.v(DEFAULT_TAG, str);
        }
    }

    /**
     * 将log写入文件(/data/data/package name/files/log)
     *
     * @param str 内容
     */
    public static void flood(Context context, String str) {
        if (LOGGABLE && !TextUtils.isEmpty(str)) {
//			str += "\n";
//			FileUtils.writeToFile(context, str.getBytes(), "/log", true);
        }
    }
}
