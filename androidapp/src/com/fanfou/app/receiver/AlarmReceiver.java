package com.fanfou.app.receiver;

import com.fanfou.app.service.NotificationService;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

/**
 * @author mcxiaoke
 * @version 1.0 2011.12.27
 * 
 */
public class AlarmReceiver extends BroadcastReceiver {

	@Override
	public void onReceive(Context context, Intent intent) {
		NotificationService.start(context);
	}

}
