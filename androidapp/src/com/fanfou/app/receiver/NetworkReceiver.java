package com.fanfou.app.receiver;

import com.fanfou.app.App;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.NetworkInfo.State;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.util.Log;

public class NetworkReceiver extends BroadcastReceiver {
	private static String TAG = NetworkReceiver.class.getSimpleName();

	@Override
	public void onReceive(Context context, Intent intent) {
		String action = intent.getAction();
		if (App.DEBUG)
			Log.d(TAG, "Action Received: " + action + " From intent: " + intent);
		if(action.equals(ConnectivityManager.CONNECTIVITY_ACTION)){	
			App.me.connected = !intent.getBooleanExtra(
					ConnectivityManager.EXTRA_NO_CONNECTIVITY, false);
//		if (isConnectedIntent(intent)) {
//		} else if (isDisconnectedIntent(intent)) {
//			if (App.DEBUG)
//				Log.d(TAG, "Disconnected");
//		}
		}
	}

	private boolean isConnectedIntent(Intent intent) {
		logIntent(intent);
		NetworkInfo networkInfo = (NetworkInfo) intent
				.getParcelableExtra(ConnectivityManager.EXTRA_NETWORK_INFO);	
		return (networkInfo != null && networkInfo.isConnected() && networkInfo
				.getType() == ConnectivityManager.TYPE_WIFI);
	}

	private boolean isDisconnectedIntent(Intent intent) {
		logIntent(intent);
		boolean res = false;
		NetworkInfo networkInfo = (NetworkInfo) intent
				.getParcelableExtra(ConnectivityManager.EXTRA_NETWORK_INFO);
		if (networkInfo != null) {
			State state = networkInfo.getState();
			res = (state.equals(NetworkInfo.State.DISCONNECTING) || state
					.equals(NetworkInfo.State.DISCONNECTED))
					&& (networkInfo.getType() == ConnectivityManager.TYPE_WIFI);
		} else {
			int wifiState = intent.getIntExtra(WifiManager.EXTRA_WIFI_STATE,
					WifiManager.WIFI_STATE_UNKNOWN);
			if (wifiState == WifiManager.WIFI_STATE_DISABLED
					|| wifiState == WifiManager.WIFI_STATE_DISABLING) {
				res = true;
			}
		}

		return res;
	}

	protected void logIntent(Intent intent) {
		if (App.DEBUG) {
			StringBuffer sb=new StringBuffer();
			sb.append(" intent.getAction():" + intent.getAction());
			sb.append( " intent.getData():" + intent.getData());
			sb.append(" intent.getDataString():" + intent.getDataString());
			sb.append(" intent.getScheme():" + intent.getScheme());
			sb.append(" intent.getType():" + intent.getType());
			Bundle extras = intent.getExtras();
			if (extras != null && !extras.isEmpty()) {
				for (String key : extras.keySet()) {
					Object value = extras.get(key);
					sb.append(" EXTRA: {" + key + "::" + value + "}");
				}
			} else {
				sb.append(" NO EXTRAS");
			}
			
			Log.d(TAG, sb.toString());
		}
	}

}