package com.fanfou.app.receiver;

import com.fanfou.app.App;
import com.fanfou.app.http.ApnType;
import com.fanfou.app.util.IntentHelper;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;

public class NetworkReceiver extends BroadcastReceiver {
	private static String TAG = NetworkReceiver.class.getSimpleName();

	@Override
	public void onReceive(Context context, Intent intent) {
		String action = intent.getAction();
		if (App.DEBUG)
			Log.d(TAG, "Action Received: " + action + " From intent: " + intent);
		if (action.equals(ConnectivityManager.CONNECTIVITY_ACTION)) {
			boolean disconnected = intent.getBooleanExtra(
					ConnectivityManager.EXTRA_NO_CONNECTIVITY, false);
			if (App.DEBUG) {
				IntentHelper.logIntent(TAG,intent);
			}
			if(disconnected){
				App.me.apnType = ApnType.NONE;
				return;
			}
			NetworkInfo info = (NetworkInfo) intent
					.getParcelableExtra(ConnectivityManager.EXTRA_NETWORK_INFO);
			if (info != null && info.isAvailable()) {
				App.me.apnType = ApnType.NET;
				disconnected=false;
				if (info.getType() == ConnectivityManager.TYPE_MOBILE) {
					String apnTypeName = info.getExtraInfo();
					if (!TextUtils.isEmpty(apnTypeName)) {
						if (apnTypeName.equals("3gnet")) {
							App.me.apnType = ApnType.HSDPA;
						} else if (apnTypeName.equals("ctwap")) {
							App.me.apnType = ApnType.CTWAP;
						} else if (apnTypeName.contains("wap")) {
							App.me.apnType = ApnType.WAP;
						}
					}
				}
			}
		}
	}

}