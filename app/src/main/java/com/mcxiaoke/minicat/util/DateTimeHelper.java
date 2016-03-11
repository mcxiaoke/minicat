package com.mcxiaoke.minicat.util;

import java.text.ParsePosition;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

/**
 * @author mcxiaoke
 * @version 2.0 2012.02.21
 */
public class DateTimeHelper {

    private static final String FANFOU_DATE_FORMAT_STRING = "EEE MMM dd HH:mm:ss Z yyyy";
    public static final SimpleDateFormat FANFOU_DATE_FORMAT = new SimpleDateFormat(
            FANFOU_DATE_FORMAT_STRING, Locale.US);
    private static final String SIMPLE_DATE_FORMAT_STRING = "yyyy-MM-dd HH:mm:ss";
    private static final SimpleDateFormat SIMPLE_DATE_FORMAT = new SimpleDateFormat(
            SIMPLE_DATE_FORMAT_STRING, Locale.US);
    private static final String FILENAME_DATE_FORMAT_STRING = "yyyy_MM_dd_HH_mm_ss";
    private static final SimpleDateFormat FILENAME_DATE_FORMAT = new SimpleDateFormat(
            FILENAME_DATE_FORMAT_STRING, Locale.US);
    private static final String DATE_ONLY_FORMAT_STRING = "yyyy-MM-dd";
    private static final SimpleDateFormat DATE_ONLY_FORMAT = new SimpleDateFormat(
            DATE_ONLY_FORMAT_STRING, Locale.US);
    private static final String TIME_ONLY_FORMAT_STRING = "HH:mm:ss";
    private static final SimpleDateFormat TIME_ONLY_FORMAT = new SimpleDateFormat(
            TIME_ONLY_FORMAT_STRING, Locale.US);

    /**
     * 以秒为单位计算时间间隔
     */
    private static final long MIN = 60;
    private static final long HOUR = MIN * 60;
    private static final long DAY = HOUR * 24;
    private static final long WEEK = DAY * 7;
    private static final long MONTH = DAY * 30;
    private static final long YEAR = MONTH * 12;
    /**
     * @param s
     * 代表饭否日期和时间的字符串
     * @return 字符串解析为对应的Date对象
     */
    private static final ParsePosition mPosition = new ParsePosition(0);

    /**
     * 返回指定时间与当前时间的间隔
     *
     * @param date 指定的日期
     * @return 返回字符串类型的时间间隔
     */
    public static String getInterval(Date date) {
        if (date == null) {
            return "";
        }
        long time = date.getTime();
        return getInterval(time);
    }

    public static String getInterval(long time) {
        long seconds = (System.currentTimeMillis() - time) / 1000;
        if (seconds < 3) {
            return "刚刚";
        } else if (seconds < MIN) {
            return seconds + "秒钟";
        } else if (seconds < HOUR) {
            return seconds / MIN + "分钟";
        } else if (seconds < DAY) {
            return seconds / HOUR + "小时";
        } else if (seconds < YEAR * 5) {
            return seconds / DAY + "天";
        } else {
            return seconds / DAY + "天";
        }
    }

    /**
     * 返回指定时间与当前时间的间隔，单位为秒
     *
     * @param date 指定日期
     * @return 返回时间间隔，单位为秒
     */
    public static long interval(Date date) {
        return (Calendar.getInstance(TimeZone.getTimeZone("GMT+08:00"))
                .getTimeInMillis() - date.getTime()) / 1000;
    }

    public static Date fanfouStringToDate(String s) {
        // Fanfou Date String example --> "Mon Dec 13 03:10:21 +0000 2010"
        // final ParsePosition position = new ParsePosition(0);//
        // 这个如果放在方法外面的话，必须每次重置Index为0
        mPosition.setIndex(0);
        return FANFOU_DATE_FORMAT.parse(s, mPosition);
    }

    /**
     * 将Date对象解析为饭否格式的字符串
     *
     * @param date Date对象
     * @return 饭否格式日期字符串
     */
    public static String formatDate(Date date) {
        return formatDate(date, SIMPLE_DATE_FORMAT);
    }

    public static String formatDateFileName(Date date) {
        return formatDate(date, FILENAME_DATE_FORMAT);
    }

    public static String formatTimeOnly(Date date) {
        return formatDate(date, TIME_ONLY_FORMAT);
    }

    public static String formatDateOnly(Date date) {
        return formatDate(date, DATE_ONLY_FORMAT);
    }

    public static String formatDate(long time) {
        return formatDate(time, SIMPLE_DATE_FORMAT);
    }

    public static String formatDateFileName(long time) {
        return formatDate(time, FILENAME_DATE_FORMAT);
    }

    public static String formatTimeOnly(long time) {
        return formatDate(time, TIME_ONLY_FORMAT);
    }

    public static String formatDateOnly(long time) {
        return formatDate(time, DATE_ONLY_FORMAT);
    }

    public static String formatDate(long time, SimpleDateFormat format) {
        Date date = new Date(time);
        if (format == null) {
            return SIMPLE_DATE_FORMAT.format(date);
        }
        return format.format(date);
    }

    public static String formatDate(Date date, SimpleDateFormat format) {
        if (date == null) {
            return "";
        }
        if (format == null) {
            return SIMPLE_DATE_FORMAT.format(date);
        }
        return format.format(date);
    }

}
