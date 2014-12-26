package com.mcxiaoke.minicat.service;

import android.content.Context;
import android.content.Intent;
import android.os.PowerManager;

/**
 * Project: fanfouapp
 * Package: com.mcxiaoke.fanfouapp.service
 * User: mcxiaoke
 * Date: 13-6-3
 * Time: 下午9:34
 */
public abstract class WakefulIntentService extends BaseIntentService {

    private static final Object LOCK = WakefulIntentService.class;
    private static PowerManager.WakeLock sWakeLock;

    public WakefulIntentService(String name) {
        super(name);
    }

    public static void runOnWake(Context context, Intent intent) {
        synchronized (LOCK) {
            if (sWakeLock == null) {
                PowerManager pm = (PowerManager) context.getSystemService(Context.POWER_SERVICE);
                sWakeLock = pm.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, "WakefulIntentService");
            }
        }
        sWakeLock.acquire();
        intent.setClassName(context, WakefulIntentService.class.getName());
        context.startService(intent);
    }

    @Override
    public final void onHandleIntent(Intent intent) {
        try {
            doWakefulWork(intent);
        } finally {
            synchronized (LOCK) {
                sWakeLock.release();
            }
        }
    }

    protected abstract void doWakefulWork(Intent intent);


}
