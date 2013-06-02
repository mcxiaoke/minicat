package com.mcxiaoke.fanfouapp.push;

import android.app.AlarmManager;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Color;
import android.support.v4.app.NotificationCompat;
import android.text.TextUtils;
import com.mcxiaoke.fanfouapp.R;
import com.mcxiaoke.fanfouapp.api.Api;
import com.mcxiaoke.fanfouapp.api.Paging;
import com.mcxiaoke.fanfouapp.app.AppContext;
import com.mcxiaoke.fanfouapp.app.UIHome;
import com.mcxiaoke.fanfouapp.app.UIStatus;
import com.mcxiaoke.fanfouapp.controller.DataController;
import com.mcxiaoke.fanfouapp.dao.model.DirectMessageModel;
import com.mcxiaoke.fanfouapp.dao.model.StatusModel;
import com.mcxiaoke.fanfouapp.preference.PreferenceHelper;
import com.mcxiaoke.fanfouapp.service.WakefulIntentService;
import com.mcxiaoke.fanfouapp.util.DateTimeHelper;
import com.mcxiaoke.fanfouapp.util.LogUtil;
import com.mcxiaoke.fanfouapp.util.NetworkHelper;
import com.mcxiaoke.fanfouapp.util.Utils;

import java.util.List;

/**
 * Project: fanfouapp
 * Package: com.mcxiaoke.fanfouapp.push
 * User: mcxiaoke
 * Date: 13-6-2
 * Time: 下午6:04
 */
public class PushService extends WakefulIntentService {
    private static final String TAG = PushService.class.getSimpleName();
    private static boolean DEBUG = AppContext.DEBUG;

    public static final String ACTION_ALARM = "com.mcxiaoke.fanfouapp.PushService.ACTION_ALARM";
    public static final String ACTION_CHECK = "com.mcxiaoke.fanfouapp.PushService.ACTION_CHECK";
    public static final String ACTION_NOTIFY = "com.mcxiaoke.fanfouapp.PushService.ACTION_NOTIFY";
    private static final long PUSH_CHECK_INTERVAL = 1000L * 60 * 5; // five minutes

    private NotificationManager mNotificationManager;

    private static void debug(String message) {
        LogUtil.v(TAG, message);
    }

    public static void start(Context context) {
        sendWakefulWork(context, PushService.class);
    }

    public static void check(Context context) {
        boolean enablePushNotifications = PreferenceHelper.getInstance(context).isPushNotificationEnabled();
        if (enablePushNotifications) {
            setAlarm(context);
        } else {
            cancelAlarm(context);
        }
    }

    private static PendingIntent getPendingIntent(Context context) {
        Intent broadcast = new Intent(ACTION_CHECK);
        PendingIntent pi = PendingIntent.getBroadcast(context, 0, broadcast, PendingIntent.FLAG_UPDATE_CURRENT);
        return pi;
    }

    private static void setAlarm(Context context) {
        AlarmManager alarmManager = (AlarmManager) context.getSystemService(ALARM_SERVICE);
        long fiveMinutesLater = System.currentTimeMillis() + PUSH_CHECK_INTERVAL;
        alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, fiveMinutesLater, PUSH_CHECK_INTERVAL, getPendingIntent(context));
        if (DEBUG) {
            debug("setAlarm()");
        }
    }

    private static void cancelAlarm(Context context) {
        AlarmManager alarmManager = (AlarmManager) context.getSystemService(ALARM_SERVICE);
        alarmManager.cancel(getPendingIntent(context));
        if (DEBUG) {
            debug("cancelAlarm()");
        }
    }

    @Override
    protected void doWakefulWork(Intent intent) {
        if (NetworkHelper.isConnected(this)) {
            debug("doWakefulWork()");
            checkMentions();
            checkDirectMessages();
        }

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

    private static final int NOTIFICATION_STATUS_ID = 1234;
    private static final int NOTIFICATION_DM_ID = 2234;

    private void showMentionNotification(final StatusModel sm) {
        debug("showMentionNotification() sm=" + sm);
        Intent intent = new Intent(this, UIStatus.class);
        intent.setAction("DUMMY_ACTION" + System.currentTimeMillis());
        intent.putExtra("data", sm);
        PendingIntent pi = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        String ticker = "来自@" + sm.getUserScreenName() + "的新消息";
        String title = sm.getUserScreenName();
        String text = sm.getSimpleText();
        String subText = DateTimeHelper.getInterval(sm.getTime());
        showNotification(NOTIFICATION_STATUS_ID, R.drawable.ic_stat_mention, pi, ticker, title, text, subText);
    }

    private void showDMNotification(final DirectMessageModel dm) {
        debug("showDMNotification() dm=" + dm);
        Intent intent = new Intent(this, UIHome.class);
        intent.setAction("DUMMY_ACTION" + System.currentTimeMillis());
        intent.putExtra("data", dm);
        PendingIntent pi = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        String ticker = "来自@" + dm.getSenderScreenName() + "的新私信";
        String title = dm.getSenderScreenName();
        String text = dm.getText();
        String subText = DateTimeHelper.getInterval(dm.getTime());
        showNotification(NOTIFICATION_DM_ID, R.drawable.ic_stat_dm, pi, ticker, title, text, subText);
    }

    private void showNotification(int id, int iconId, PendingIntent pi, String ticker, String title, String text, String subText) {
        debug("showNotification() id=" + id + " ticker=" + ticker);
        NotificationCompat.Builder nb = new NotificationCompat.Builder(this);
        nb.setWhen(System.currentTimeMillis());
        nb.setSmallIcon(iconId);
        nb.setTicker(ticker).setContentTitle(title).setContentText(text);
        nb.setSubText(subText);
        nb.setLights(Color.GREEN, 200, 200);
        nb.setDefaults(Notification.DEFAULT_ALL);
        nb.setAutoCancel(true).setOnlyAlertOnce(true);
        nb.setContentIntent(pi);
        mNotificationManager.notify(id, nb.build());
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
}
