package com.fanfou.app.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.fanfou.app.App;
import com.fanfou.app.R;
import com.fanfou.app.util.OptionHelper;

/**
 * @author mcxiaoke
 * @version 1.0 20110804
 * 
 */
public class AlarmReceiver extends BroadcastReceiver {

	@Override
	public void onReceive(Context context, Intent intent) {
		if (App.DEBUG)
			Log.d("AlarmReceiver", "received a alarm, start cleanservice");
		// Intent service = new Intent(context, CleanService.class);
		// context.startService(service);
		OptionHelper.saveBoolean(context, R.string.option_cleandb, false);
	}

}
