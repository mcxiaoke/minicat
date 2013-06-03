package com.mcxiaoke.fanfouapp.push;

import android.app.AlarmManager;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.Environment;
import android.text.TextUtils;
import com.mcxiaoke.fanfouapp.api.Api;
import com.mcxiaoke.fanfouapp.api.Paging;
import com.mcxiaoke.fanfouapp.app.AppContext;
import com.mcxiaoke.fanfouapp.controller.DataController;
import com.mcxiaoke.fanfouapp.dao.model.DirectMessageModel;
import com.mcxiaoke.fanfouapp.dao.model.StatusModel;
import com.mcxiaoke.fanfouapp.preference.PreferenceHelper;
import com.mcxiaoke.fanfouapp.service.BaseIntentService;
import com.mcxiaoke.fanfouapp.util.DateTimeHelper;
import com.mcxiaoke.fanfouapp.util.LogUtil;
import com.mcxiaoke.fanfouapp.util.NetworkHelper;
import com.mcxiaoke.fanfouapp.util.Utils;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

/**
 * Project: fanfouapp
 * Package: com.mcxiaoke.fanfouapp.push
 * User: mcxiaoke
 * Date: 13-6-2
 * Time: 下午6:04
 */
public class PushService extends BaseIntentService {
    private static final String TAG = PushService.class.getSimpleName();
    private static boolean DEBUG = AppContext.DEBUG;

    public static final String ACTION_ALARM = "com.mcxiaoke.fanfouapp.PushService.ACTION_ALARM";
    public static final String ACTION_CHECK = "com.mcxiaoke.fanfouapp.PushService.ACTION_CHECK";
    public static final String ACTION_NOTIFY = "com.mcxiaoke.fanfouapp.PushService.ACTION_NOTIFY";

    public static final int NOTIFICATION_TYPE_TIMELINE = -101;
    public static final int NOTIFICATION_TYPE_DIRECTMESSAGE = -102;

    public static final String EXTRA_TYPE = "com.mcxiaoke.fanfouapp.PushService.EXTRA_TYPE";
    public static final String EXTRA_DATA = "com.mcxiaoke.fanfouapp.PushService.EXTRA_DATA";

    private NotificationManager mNotificationManager;

    private static void debug(String message) {
        LogUtil.v(TAG, message);
    }

    public static void start(Context context) {
        Intent intent = new Intent(context, PushService.class);
        context.startService(intent);
    }

    public static void check(Context context) {
        boolean enablePushNotifications = PreferenceHelper.getInstance(context).isPushNotificationEnabled();
        if (enablePushNotifications) {
            set(context);
        } else {
            cancel(context);
        }
    }

    private static void set(Context context) {
        Calendar calendar = Calendar.getInstance();
        if (DEBUG) {
            debug("setAlarm() now time is " + DateTimeHelper.formatDate(calendar.getTime()));
        }
        int hours = calendar.get(Calendar.HOUR_OF_DAY);
        if (hours < 7) {
            calendar.add(Calendar.DAY_OF_YEAR, 1);
            calendar.set(Calendar.HOUR_OF_DAY, 7);
            calendar.set(Calendar.MINUTE, 0);
            calendar.set(Calendar.SECOND, 0);
        } else {
            calendar.add(Calendar.MINUTE, 5);
        }
        AlarmManager alarmManager = (AlarmManager) context.getSystemService(ALARM_SERVICE);
        alarmManager.set(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), getPendingIntent(context));
        if (DEBUG) {
            debug("setAlarm() next time is " + DateTimeHelper.formatDate(calendar.getTime()));
        }
    }

    public static void cancel(Context context) {
        AlarmManager alarmManager = (AlarmManager) context.getSystemService(ALARM_SERVICE);
        alarmManager.cancel(getPendingIntent(context));
        if (DEBUG) {
            debug("cancelAlarm()");
        }
    }


    private static PendingIntent getPendingIntent(Context context) {
        Intent broadcast = new Intent(ACTION_CHECK);
        PendingIntent pi = PendingIntent.getBroadcast(context, 0, broadcast, PendingIntent.FLAG_UPDATE_CURRENT);
        return pi;
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        super.onHandleIntent(intent);
        doWakefulWork(intent);
    }

    protected void doWakefulWork(Intent intent) {
        if (NetworkHelper.isConnected(this)) {
            debug("doWakefulWork()");
            long now = System.currentTimeMillis();
            saveDebugInfo("doWakefulWork check.");
            checkMentions();
            checkDirectMessages();
            long ms = System.currentTimeMillis() - now;
            saveDebugInfo("doWakefulWork end, time is " + ms + "ms.");
        }
        check(this);
    }


    private void checkMentions() {
        String sinceId = getSinceId();
        debug("check mentions sinceId=" + sinceId);
        if (!TextUtils.isEmpty(sinceId)) {
            Api api = AppContext.getApi();
            Paging p = new Paging();
            if (DEBUG) {
                sinceId = null;
            }
            p.sinceId = sinceId;
            try {
                List<StatusModel> ss = api.getMentions(p);
                if (ss != null && ss.size() > 0) {
                    DataController.store(this, ss);
                    StatusModel sm = ss.get(0);
                    showMentionNotification(sm);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

    }

    private void checkDirectMessages() {
        String sinceId = getDMSinceId();
        debug("check dm sinceId=" + sinceId);
        if (!TextUtils.isEmpty(sinceId)) {
            Api api = AppContext.getApi();
            Paging p = new Paging();
            if (DEBUG) {
                sinceId = null;
            }
            p.sinceId = sinceId;
            try {
                List<DirectMessageModel> dms = api.getDirectMessagesInbox(p);
                if (dms != null && dms.size() > 0) {
                    DataController.store(this, dms);
                    DirectMessageModel dm = dms.get(0);
                    showDMNotification(dm);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }

        }
    }

    private String getDMSinceId() {
        Cursor cursor = null;
        try {
            cursor = DataController.getDirectMessageCursor(this);
            return Utils.getDmSinceId(cursor);
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
    }

    private String getSinceId() {
        Cursor cursor = null;
        try {
            cursor = DataController.getHomeTimelineCursor(this);
            return Utils.getSinceId(cursor);
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
    }

    private void showMentionNotification(StatusModel st) {
        Intent intent = new Intent(ACTION_NOTIFY);
        intent.putExtra(EXTRA_TYPE, NOTIFICATION_TYPE_TIMELINE);
        intent.putExtra(EXTRA_DATA, st);
        sendBroadcast(intent);
    }

    private void showDMNotification(DirectMessageModel dm) {
        Intent intent = new Intent(ACTION_NOTIFY);
        intent.putExtra(EXTRA_TYPE, NOTIFICATION_TYPE_DIRECTMESSAGE);
        intent.putExtra(EXTRA_DATA, dm);
        sendBroadcast(intent);
    }

    public PushService() {
        super(TAG);
        debug("PushService()");
    }

    @Override
    public void onCreate() {
        super.onCreate();
        debug("onCreate()");
        mNotificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        debug("onDestroy()");
    }

    private void saveDebugInfo(String message) {
        try {
            File file = new File(Environment.getExternalStorageDirectory(), "circleapp.log");
            FileWriter fw = new FileWriter(file, true);
            fw.write("" + new Date() + " - " + message + "\n");
            fw.flush();
            fw.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {

        }

    }
}
