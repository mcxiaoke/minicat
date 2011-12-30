package com.fanfou.app.receiver;

import com.fanfou.app.service.AutoCompleteService;
import com.fanfou.app.service.Constants;
import com.fanfou.app.service.DownloadService;
import com.fanfou.app.service.NotificationService;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

/**
 * @author mcxiaoke
 * @version 1.0 2011.12.27
 * @version 1.1 2011.12.30
 * 
 */
public class AlarmReceiver extends BroadcastReceiver {

	@Override
	public void onReceive(Context context, Intent intent) {
//		String action = intent.getAction();
		String action=intent.getStringExtra(Constants.EXTRA_TYPE);
		if (action.equals(Constants.ACTION_ALARM_NOTITICATION)) {
			NotificationService.start(context);
		} else if (action.equals(Constants.ACTION_ALARM_AUTO_COMPLETE)) {
			AutoCompleteService.start(context);
		} else if (action.equals(Constants.ACTION_ALARM_AUTO_UPDATE_CHECK)) {
			DownloadService.startCheck(context);
		}
		
	}

}
