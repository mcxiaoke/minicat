package com.fanfou.app.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.fanfou.app.R;
import com.fanfou.app.util.AlarmHelper;
import com.fanfou.app.util.OptionHelper;

/**
 * @author mcxiaoke
 * @version 1.0 2011.08.03
 * @version 2.0 2011.09.22
 * @version 3.0 2011.11.08
 * 
 */
public class BootReceiver extends BroadcastReceiver {

	@Override
	public void onReceive(Context context, Intent intent) {

		boolean notification = OptionHelper.readBoolean(context,
				R.string.option_notification, false);
		if (notification) {
			int intervel = OptionHelper.parseInt(context,
					R.string.option_notification_interval, "5");
			AlarmHelper.setNotificationTaskOn(context, intervel);
		}

		boolean update = OptionHelper.readBoolean(context,
				R.string.option_autoupdate, false);
		if (update) {
			AlarmHelper.setAutoUpdateTask(context);
		}

		AlarmHelper.setCleanTask(context);
		AlarmHelper.setAutoCompleteTask(context);
	}
}
