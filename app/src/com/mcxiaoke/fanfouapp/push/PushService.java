package com.mcxiaoke.fanfouapp.push;

import android.content.Intent;
import com.mcxiaoke.fanfouapp.service.WakefulIntentService;

/**
 * Project: fanfouapp
 * Package: com.mcxiaoke.fanfouapp.push
 * User: mcxiaoke
 * Date: 13-6-2
 * Time: 下午6:04
 */
public class PushService extends WakefulIntentService {
    private static final String TAG = PushService.class.getSimpleName();

    @Override
    protected void doWakefulWork(Intent intent) {
    }

    public PushService() {
        super(TAG);
    }

    @Override
    public void onCreate() {
        super.onCreate();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }
}
