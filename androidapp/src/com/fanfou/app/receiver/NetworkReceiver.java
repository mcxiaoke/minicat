package com.fanfou.app.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.WifiManager;
import android.text.TextUtils;
import android.util.Log;

import com.fanfou.app.App;
import com.fanfou.app.App.ApnType;
import com.fanfou.app.R;
import com.fanfou.app.service.DownloadService;
import com.fanfou.app.util.IntentHelper;
import com.fanfou.app.util.OptionHelper;

/**
 * @author mcxiaoke
 * @version 2.0 2011.10.29
 * @version 2.5 2011.10.30
 * @version 2.6 2011.11.03
 * @version 2.7 2011.11.10
 * @version 2.8 2011.11.17
 * 
 */
public class NetworkReceiver extends BroadcastReceiver {
	private static String TAG = NetworkReceiver.class.getSimpleName();

	private void log(String msg) {
		Log.d(TAG, msg);
	}

	@Override
	public void onReceive(Context context, Intent intent) {
		String action = intent.getAction();
		if (action.equals(ConnectivityManager.CONNECTIVITY_ACTION)) {
			handleConnectionStateChange(context, intent);
		} else if (action.equals(WifiManager.WIFI_STATE_CHANGED_ACTION)) {
			handleWifiStateChange(context, intent);
		}
	}

	private void onWifiConnected(Context context) {
		if (App.DEBUG) {
			log("onWifiConnected");
		}
		// when wifi is connected, start fetch friends for autocomplete and
		// check update.
		// AutoCompleteService.start(context);
		startUpdateCheck(context);
	}

	public static void startUpdateCheck(Context context) {
		boolean autoUpdate = OptionHelper.readBoolean(
				R.string.option_autoupdate, true);
		if (autoUpdate) {
			DownloadService.startCheck(context);
		}
	}

	private void handleConnectionStateChange(Context context, Intent intent) {
		boolean noConnection = intent.getBooleanExtra(
				ConnectivityManager.EXTRA_NO_CONNECTIVITY, false);
		if (App.DEBUG) {
			IntentHelper.logIntent(TAG, intent);
			log("onReceive noConnection =  " + noConnection);
		}

		App.noConnection = noConnection;
		if (noConnection) {
			return;
		}

		NetworkInfo info = (NetworkInfo) intent
				.getParcelableExtra(ConnectivityManager.EXTRA_NETWORK_INFO);

		if (info != null && info.isConnectedOrConnecting()) {
			App.noConnection = false;
			if (info.getType() == ConnectivityManager.TYPE_MOBILE) {
				App.setApnType(ApnType.NET);
				String apnTypeName = info.getExtraInfo();
				if (App.DEBUG) {
					Log.d(TAG, "type=TYPE_MOBILE apnTypeName: " + apnTypeName);
				}
				if (!TextUtils.isEmpty(apnTypeName)) {
					if (apnTypeName.equals("3gnet")) {
						App.setApnType(ApnType.HSDPA);
					} else if (apnTypeName.contains("wap")) {
						App.setApnType(ApnType.WAP);
					}
				}
			} else if (info.getType() == ConnectivityManager.TYPE_WIFI) {
				if (App.DEBUG) {
					log("onReceive type=TYPE_WIFI ");
				}
				App.setApnType(ApnType.WIFI);
				onWifiConnected(context);
			}
		} else {
			if (App.DEBUG) {
				log("onReceive NetworkInfo is null.");
			}
		}
	}

	private void handleWifiStateChange(Context context, Intent intent) {
		int wifiState = intent.getExtras().getInt(WifiManager.EXTRA_WIFI_STATE);
		if (App.DEBUG) {
			log("wifi state=" + wifiState);
		}
		switch (wifiState) {
		case WifiManager.WIFI_STATE_DISABLING:
			break;
		case WifiManager.WIFI_STATE_DISABLED:
			if (App.DEBUG) {
				Log.v(TAG, "wifi is disabled.");
			}
			break;
		case WifiManager.WIFI_STATE_ENABLING:
			break;
		case WifiManager.WIFI_STATE_ENABLED:
			if (App.DEBUG) {
				Log.v(TAG, "wifi is enabled.");
			}
			break;
		default:
			break;
		}
	}

}