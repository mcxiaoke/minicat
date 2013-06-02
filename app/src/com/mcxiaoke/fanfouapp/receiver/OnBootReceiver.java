package com.mcxiaoke.fanfouapp.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

/**
 * Project: fanfouapp
 * Package: com.mcxiaoke.fanfouapp.receiver
 * User: mcxiaoke
 * Date: 13-6-2
 * Time: 下午5:42
 */
public class OnBootReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();
        if (Intent.ACTION_REBOOT.equals(action)) {

        }
    }
}
