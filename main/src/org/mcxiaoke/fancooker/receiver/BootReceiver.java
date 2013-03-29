package org.mcxiaoke.fancooker.receiver;

import org.mcxiaoke.fancooker.service.AutoCompleteService;
import org.mcxiaoke.fancooker.service.DownloadService;
import org.mcxiaoke.fancooker.service.NotificationService;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;


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
