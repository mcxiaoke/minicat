package com.fanfou.app.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.fanfou.app.util.AlarmHelper;

/**
 * @author mcxiaoke
 * @version 1.0 2011.08.03
 * @version 2.0 2011.09.22
 * 
 */
public class BootReceiver extends BroadcastReceiver {

	@Override
	public void onReceive(Context context, Intent intent) {
		AlarmHelper.setNotificationTaskOn(context);
		AlarmHelper.setCleanTask(context);
		AlarmHelper.setAutoCompleteTask(context);
	}
}
