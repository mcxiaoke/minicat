package com.fanfou.app.receiver;

import com.fanfou.app.App;
import com.fanfou.app.http.NetworkState.Type;
import com.fanfou.app.util.NetworkHelper;

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
			App.me.connected = !intent.getBooleanExtra(
					ConnectivityManager.EXTRA_NO_CONNECTIVITY, false);
			if (App.DEBUG) {
				logIntent(intent);
			}
			NetworkInfo info = (NetworkInfo) intent
					.getParcelableExtra(ConnectivityManager.EXTRA_NETWORK_INFO);
			if (info != null && info.isAvailable()) {
				Type apnType = Type.NET;
				App.me.connected=true;
				if (info.getType() == ConnectivityManager.TYPE_MOBILE) {
					String apnTypeName = info.getExtraInfo();
					if (!TextUtils.isEmpty(apnTypeName)) {
						if (apnTypeName.equals("ctwap")) {
							apnType = Type.CTWAP;
						} else if (apnTypeName.contains("wap")) {
							apnType = Type.WAP;

						}
					}
				}
				NetworkHelper.setProxy(App.me.getHttpClient().getParams(), apnType);
			}
		}
	}

	protected void logIntent(Intent intent) {
		if (App.DEBUG) {
			StringBuffer sb = new StringBuffer();
			sb.append(" intent.getAction():" + intent.getAction());
			sb.append(" intent.getData():" + intent.getData());
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