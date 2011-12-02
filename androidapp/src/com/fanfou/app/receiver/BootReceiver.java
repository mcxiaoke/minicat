package com.fanfou.app.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.fanfou.app.R;
import com.fanfou.app.service.AutoCompleteService;
import com.fanfou.app.service.DownloadService;
import com.fanfou.app.service.NotificationService;
import com.fanfou.app.util.AlarmHelper;
import com.fanfou.app.util.OptionHelper;

/**
 * @author mcxiaoke
 * @version 1.0 2011.08.03
 * @version 2.0 2011.09.22
 * @version 3.0 2011.11.08
 * @version 3.1 2011.12.02
 * 
 */
public class BootReceiver extends BroadcastReceiver {

	@Override
	public void onReceive(Context context, Intent intent) {

		NotificationService.set(context);
		AutoCompleteService.set(context);
		DownloadService.set(context);
	}
}
