package com.mcxiaoke.minicat.push;

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
import com.mcxiaoke.bus.Bus;
import com.mcxiaoke.minicat.AppContext;
import com.mcxiaoke.minicat.R;
import com.mcxiaoke.minicat.api.Api;
import com.mcxiaoke.minicat.api.Paging;
import com.mcxiaoke.minicat.app.UIHome;
import com.mcxiaoke.minicat.app.UIStatus;
import com.mcxiaoke.minicat.controller.DataController;
import com.mcxiaoke.minicat.dao.model.DirectMessageModel;
import com.mcxiaoke.minicat.dao.model.StatusModel;
import com.mcxiaoke.minicat.preference.PreferenceHelper;
import com.mcxiaoke.minicat.receiver.PushReceiver;
import com.mcxiaoke.minicat.service.BaseIntentService;
import com.mcxiaoke.minicat.service.SyncService;
import com.mcxiaoke.minicat.util.DateTimeHelper;
import com.mcxiaoke.minicat.util.LogUtil;

import java.util.Calendar;
import java.util.List;

/**
 * Project: fanfouapp
 * Package: com.mcxiaoke.fanfouapp.push
 * User: mcxiaoke
 * Date: 13-6-2
 * Time: 下午6:04
 */
public class PushService extends BaseIntentService {
    public static final String ACTION_START = "com.mcxiaoke.fanfouapp.PushService.ACTION_START";
    public static final int TYPE_ALL = -100;
    public static final int TYPE_MENTION = -101;
    public static final int TYPE_MESSAGE = -102;
    public static final String EXTRA_TYPE = "com.mcxiaoke.fanfouapp.PushService.EXTRA_TYPE";
    public static final String EXTRA_DATA = "com.mcxiaoke.fanfouapp.PushService.EXTRA_DATA";
    private static final String TAG = PushService.class.getSimpleName();
    private static final int NOTIFICATION_STATUS_ID = 1234;
    private static final int NOTIFICATION_DM_ID = 2234;
    private static boolean DEBUG = AppContext.DEBUG;

    public PushService() {
        super(TAG);
        debug("PushService()");
    }

    private static void debug(String message) {
        LogUtil.v(TAG, message);
    }

    public static void checkAll(Context context) {
        Intent intent = new Intent(context, PushService.class);
        intent.putExtra("type", TYPE_ALL);
        PushReceiver.startWakefulService(context, intent);
    }

    public static void checkMentions(Context context) {
        Intent intent = new Intent(context, PushService.class);
        intent.putExtra("type", TYPE_MENTION);
        PushReceiver.startWakefulService(context, intent);
    }

    public static void checkMessages(Context context) {
        Intent intent = new Intent(context, PushService.class);
        intent.putExtra("type", TYPE_MESSAGE);
        PushReceiver.startWakefulService(context, intent);
    }

    public static void check(Context context) {
        boolean enabled = PreferenceHelper.getInstance(context).isPushNotificationEnabled();
        if (DEBUG) {
            debug("check() enabled=" + enabled);
        }
        if (enabled) {
            set(context);
        } else {
            cancel(context);
        }
    }

    private static void set(Context context) {
        final Calendar calendar = Calendar.getInstance();
        if (DEBUG) {
            debug("setAlarm() now time is " + DateTimeHelper.formatDate(calendar.getTime()));
        }
        calendar.add(Calendar.MINUTE, 5);

        final long interval = 10 * 60 * 1000L;// 提醒间隔10分钟
        final long nextTime = calendar.getTimeInMillis();

        AlarmManager am = (AlarmManager) context.getSystemService(ALARM_SERVICE);
        am.setInexactRepeating(AlarmManager.RTC_WAKEUP, nextTime, interval, getPendingIntent(context));
        if (DEBUG) {
            debug("setAlarm() next time is " + DateTimeHelper.formatDate(nextTime));
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
        Intent broadcast = new Intent(context, PushReceiver.class);
        broadcast.setAction(ACTION_START);
        return PendingIntent.getBroadcast(context, 0, broadcast,
                PendingIntent.FLAG_UPDATE_CURRENT);
    }

    private static void showMentionNotification(Context context, final StatusModel sm) {
        debug("showMentionNotification() sm=" + sm);
        Intent intent = new Intent(context, UIStatus.class);
        intent.setAction("DUMMY_ACTION" + System.currentTimeMillis());
        intent.putExtra("data", sm);
        PendingIntent pi = PendingIntent.getActivity(context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        String ticker = "来自@" + sm.getUserScreenName() + "的新消息";
        String title = sm.getUserScreenName();
        String text = sm.getSimpleText();
        String subText = DateTimeHelper.getInterval(sm.getTime());
        showNotification(context, NOTIFICATION_STATUS_ID, R.drawable.ic_stat_mention, pi, ticker, title, text, subText);
    }

    private static void showDMNotification(Context context, final DirectMessageModel dm) {
        debug("showDMNotification() dm=" + dm);
        Intent intent = new Intent(context, UIHome.class);
        intent.setAction("DUMMY_ACTION" + System.currentTimeMillis());
        intent.putExtra("data", dm);
        PendingIntent pi = PendingIntent.getActivity(context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        String ticker = "来自@" + dm.getSenderScreenName() + "的新私信";
        String title = dm.getSenderScreenName();
        String text = dm.getText();
        String subText = DateTimeHelper.getInterval(dm.getTime());
        showNotification(context, NOTIFICATION_DM_ID, R.drawable.ic_stat_dm, pi, ticker, title, text, subText);
    }

    private static void showNotification(Context context, int id, int iconId, PendingIntent pi, String ticker, String title, String text, String subText) {
        debug("showNotification() id=" + id + " ticker=" + ticker);
        NotificationCompat.Builder nb = new NotificationCompat.Builder(context);
        nb.setWhen(System.currentTimeMillis());
        nb.setSmallIcon(iconId);
        nb.setTicker(ticker).setContentTitle(title).setContentText(text);
        nb.setSubText(subText);
        nb.setLights(Color.GREEN, 200, 200);
        nb.setDefaults(Notification.DEFAULT_ALL);
        nb.setAutoCancel(true).setOnlyAlertOnce(true);
        nb.setContentIntent(pi);
        NotificationManager nm = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        nm.notify(id, nb.build());
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        super.onHandleIntent(intent);
        debug("onHandleIntent()");
        doWakefulWork(intent);
        PushReceiver.completeWakefulIntent(intent);
    }

    protected void doWakefulWork(Intent intent) {
        boolean enabled = PreferenceHelper.getInstance(this).isPushNotificationEnabled();
        if (!enabled) {
            debug("doWakefulWork() push disabled, ignore");
            return;
        }
        final Calendar calendar = Calendar.getInstance();
        if (calendar.get(Calendar.HOUR_OF_DAY) < 6) {
            // 6点不提醒
            return;
        }
        int type = intent.getIntExtra("type", TYPE_ALL);
        debug("doWakefulWork() type=" + type);
        switch (type) {
            case TYPE_MENTION:
                checkMentions();
                break;
            case TYPE_MESSAGE:
                checkDirectMessages();
                break;
            case TYPE_ALL:
                checkMentions();
                checkDirectMessages();
                break;
            default:
                break;
        }
    }

    private void checkMentions() {
        String sinceId = getSinceId();
        debug("checkMentions() sinceId=" + sinceId);
        if (!TextUtils.isEmpty(sinceId)) {
            Api api = AppContext.getApi();
            Paging p = new Paging();
            p.sinceId = sinceId;
            try {
                List<StatusModel> ss = api.getMentions(p);
                if (ss != null && ss.size() > 0) {
                    debug("checkMentions() result=" + ss);
                    DataController.store(this, ss);
                    StatusModel sm = ss.get(0);
                    showMention(sm);
                    if (AppContext.homeVisible) {
                        Bus.getDefault().post(new PushStatusEvent(sm));
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

    }

    private void checkDirectMessages() {
        String sinceId = getDMSinceId();
        debug("checkDirectMessages() sinceId=" + sinceId);
        if (!TextUtils.isEmpty(sinceId)) {
            Api api = AppContext.getApi();
            Paging p = new Paging();
            p.sinceId = sinceId;
            try {
                List<DirectMessageModel> dms = api.getDirectMessagesInbox(p);
                if (dms != null && dms.size() > 0) {
                    debug("checkDirectMessages() result=" + dms);
                    DataController.store(this, dms);
                    DirectMessageModel dm = dms.get(0);
                    showDM(dm);
                    if (AppContext.homeVisible) {
                        Bus.getDefault().post(new PushMessageEvent(dm));
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }

        } else {
            debug("checkDirectMessages() init fetch");
            SyncService.getConversationList(getApplication());
        }
    }

    private String getDMSinceId() {
        Cursor cursor = null;
        try {
            cursor = DataController.getDirectMessageCursor(this);
            if (cursor != null && cursor.moveToFirst()) {
                DirectMessageModel dm = DirectMessageModel.from(cursor);
                debug("getDMSinceId dm=" + dm);
                if (dm != null) {
                    return dm.getId();
                }
            }
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
        return null;
    }

    private String getSinceId() {
        Cursor cursor = null;
        try {
            cursor = DataController.getHomeTimelineCursor(this);
            if (cursor != null && cursor.moveToFirst()) {
                StatusModel st = StatusModel.from(cursor);
                debug("getSinceId st=" + st);
                if (st != null) {
                    return st.getId();
                }
            }
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
        return null;
    }

    private void showMention(StatusModel st) {
        String lastId = PreferenceHelper.getInstance(this).getLastPushStatusId();
        if (st.getId().equals(lastId)) {
            return;
        }
        showMentionNotification(this, st);
    }

    private void showDM(DirectMessageModel dm) {
        String lastId = PreferenceHelper.getInstance(this).getKeyLastPushDmId();
        if (dm.getId().equals(lastId)) {
            return;
        }
        PreferenceHelper.getInstance(this).setLastPushStatusId(dm.getId());
        showDMNotification(this, dm);
    }

    @Override
    public void onCreate() {
        super.onCreate();
        debug("onCreate()");
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        debug("onDestroy()");
    }

}
