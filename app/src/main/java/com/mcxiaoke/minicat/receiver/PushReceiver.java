package com.mcxiaoke.minicat.receiver;

import android.content.Context;
import android.content.Intent;
import android.support.v4.content.WakefulBroadcastReceiver;
import android.util.Log;
import com.mcxiaoke.minicat.AppContext;
import com.mcxiaoke.minicat.push.PushService;

/**
 * Project: fanfouapp
 * Package: com.mcxiaoke.fanfouapp.receiver
 * User: mcxiaoke
 * Date: 13-6-2
 * Time: 下午5:42
 */
public class PushReceiver extends WakefulBroadcastReceiver {
    private static final String TAG = PushReceiver.class.getSimpleName();
    private static boolean DEBUG = AppContext.DEBUG;

    @Override
    public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();
        if (DEBUG) {
            Log.e(TAG, "PushReceiver onReceive() action=" + action);
        }

        if (Intent.ACTION_REBOOT.equals(action)) {
            PushService.check(context);
        } else if (PushService.ACTION_START.equals(action)) {
            PushService.checkAll(context);
        }
    }


}
