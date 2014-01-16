package com.mcxiaoke.minicat.receiver;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.support.v4.app.NotificationCompat;
import com.mcxiaoke.minicat.R;
import com.mcxiaoke.minicat.AppContext;
import com.mcxiaoke.minicat.app.UIHome;
import com.mcxiaoke.minicat.app.UIStatus;
import com.mcxiaoke.minicat.dao.model.DirectMessageModel;
import com.mcxiaoke.minicat.dao.model.StatusModel;
import com.mcxiaoke.minicat.push.PushService;
import com.mcxiaoke.minicat.util.DateTimeHelper;
import com.mcxiaoke.minicat.util.LogUtil;

/**
 * Project: fanfouapp
 * Package: com.mcxiaoke.fanfouapp.receiver
 * User: mcxiaoke
 * Date: 13-6-2
 * Time: 下午5:42
 */
public class PushReceiver extends BroadcastReceiver {
    private static final String TAG = PushReceiver.class.getSimpleName();
    private static boolean DEBUG = AppContext.DEBUG;


    private static void debug(String message) {
        LogUtil.v(TAG, message);
    }


    @Override
    public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();
        if (Intent.ACTION_REBOOT.equals(action)) {
            PushService.check(context);
        } else if (PushService.ACTION_ALARM.equals(action)) {
            PushService.check(context);
        } else if (PushService.ACTION_CHECK.equals(action)) {
            PushService.start(context);
        } else if (PushService.ACTION_NOTIFY.equals(action)) {
            int type = intent.getIntExtra(PushService.EXTRA_TYPE, PushService.NOTIFICATION_TYPE_TIMELINE);
            if (PushService.NOTIFICATION_TYPE_TIMELINE == type) {
                StatusModel st = intent.getParcelableExtra(PushService.EXTRA_DATA);
                showMentionNotification(context, st);
            } else if (PushService.NOTIFICATION_TYPE_DIRECTMESSAGE == type) {
                DirectMessageModel dm = intent.getParcelableExtra(PushService.EXTRA_DATA);
                showDMNotification(context, dm);
            }

        }
    }


    private static final int NOTIFICATION_STATUS_ID = 1234;
    private static final int NOTIFICATION_DM_ID = 2234;

    private void showMentionNotification(Context context, final StatusModel sm) {
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

    private void showDMNotification(Context context, final DirectMessageModel dm) {
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

    private void showNotification(Context context, int id, int iconId, PendingIntent pi, String ticker, String title, String text, String subText) {
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
}
