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
import com.fanfou.app.service.AutoCompleteService;
import com.fanfou.app.service.DownloadService;
import com.fanfou.app.util.IntentHelper;
import com.fanfou.app.util.OptionHelper;

/**
 * @author mcxiaoke
 * @version 2.0 2011.10.29
 * @version 2.5 2011.10.30
 * @version 2.6 2011.11.03
 * @version 2.7 2011.11.10
 * 
 */
public class NetworkReceiver extends BroadcastReceiver {
	private static String TAG = NetworkReceiver.class.getSimpleName();

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
		// when wifi is connected, start fetch friends for autocomplete and
		// check update.
		startAutoComplete(context);
		startUpdateCheck(context);
	}

	public static void startAutoComplete(Context context) {
		Intent intent = new Intent(context, AutoCompleteService.class);
		context.startService(intent);
	}

	public static void startUpdateCheck(Context context) {
		boolean autoUpdate = OptionHelper.readBoolean(context,
				R.string.option_autoupdate, true);
		if (autoUpdate) {
			DownloadService.startCheck(context);
		}
	}

	private void handleConnectionStateChange(Context context, Intent intent) {
		boolean disconnected = intent.getBooleanExtra(
				ConnectivityManager.EXTRA_NO_CONNECTIVITY, false);
		if (App.DEBUG) {
			Log.v(TAG, "onReceive disconnected =  " + disconnected);
			IntentHelper.logIntent(TAG, intent);
		}
		if (disconnected) {
			App.me.apnType = ApnType.NONE;
			if (App.DEBUG) {
				Log.v(TAG, "onReceive apnType=" + App.me.apnType.name());
			}
			return;
		}

		NetworkInfo info = (NetworkInfo) intent
				.getParcelableExtra(ConnectivityManager.EXTRA_NETWORK_INFO);

		if (info != null && info.isConnectedOrConnecting()) {
			if (info.getType() == ConnectivityManager.TYPE_MOBILE) {
				App.me.apnType = ApnType.NET;
				String apnTypeName = info.getExtraInfo();
				if (App.DEBUG) {
					Log.d(TAG, "type=TYPE_MOBILE apnTypeName: " + apnTypeName);
				}
				if (!TextUtils.isEmpty(apnTypeName)) {
					if (apnTypeName.equals("3gnet")) {
						App.me.apnType = ApnType.HSDPA;
					} else if (apnTypeName.equals("ctwap")) {
						App.me.apnType = ApnType.CTWAP;
					} else if (apnTypeName.contains("wap")) {
						App.me.apnType = ApnType.WAP;
					}
				}
			} else if (info.getType() == ConnectivityManager.TYPE_WIFI) {
				if (App.DEBUG) {
					Log.d(TAG, "type=TYPE_WIFI ");
				}
				App.me.apnType = ApnType.WIFI;
				onWifiConnected(context);
			}
		} else {
//			App.me.apnType = ApnType.NONE;
			if (App.DEBUG) {
				Log.v(TAG, "onReceive NetworkInfo is null.");
			}
		}
		if (App.DEBUG) {
			Log.v(TAG, "onReceive apnType=" + App.me.apnType.name());
		}
	}

	private void handleWifiStateChange(Context context, Intent intent) {
		int wifiState = intent.getExtras().getInt(WifiManager.EXTRA_WIFI_STATE);
		if (App.DEBUG) {
			Log.v(TAG, "wifi state=" + wifiState);
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