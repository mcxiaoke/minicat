package com.fanfou.app.http;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.text.TextUtils;
import android.util.Log;

/**
 * 网络连接状态判断，是否连接，接入点，WIFI等，
 * 
 * @author mcxiaoke
 * @version 1.0 2011.01.29
 * @version 1.1 2011.05.02
 * @version 1.2 2011.05.02
 * 
 */
public class NetworkState {
	/**
	 * 字符串标志，是否为WAP接入点
	 */
	private static final String tag = NetworkState.class.getSimpleName();

	private Context context;
	private ConnectivityManager cm;

	private boolean connected = true;

	private String apnTypeName = "cmnet";
	private Type apnType = Type.NET;

	/**
	 * 记录调试信息
	 * 
	 * @param message
	 *            调试信息内容
	 */
	private void log(String message) {
		Log.e(tag, message);
	}

	/**
	 * New网络连接状态对象
	 * 
	 * @param c
	 */
	public NetworkState(Context c) {
		this.context = c;
		this.cm = (ConnectivityManager) context
				.getSystemService(Context.CONNECTIVITY_SERVICE);
		initState();
	}

	private void initState() {
		try {
			NetworkInfo info = cm.getActiveNetworkInfo();
			if (info != null && info.isAvailable()) {
				connected = true;
				log(info.toString());
				if (info.getType() == ConnectivityManager.TYPE_WIFI) {
					apnType = Type.WIFI;
					apnTypeName = "wifi";
				} else if (info.getType() == ConnectivityManager.TYPE_MOBILE) {
					apnTypeName = info.getExtraInfo();
					if (!TextUtils.isEmpty(apnTypeName)) {
						if (apnTypeName.equals("ctwap")) {
							apnType = Type.CTWAP;
						} else if (apnTypeName.contains("wap")) {
							apnType = Type.WAP;
						}
					}
				}
			} else {
				connected = false;
			}
		} catch (Exception e) {
		}
	}

	/**
	 * 判断网络连接状态
	 * 
	 * @return 返回网络是否可用
	 */
	public boolean isAvailable() {
		return connected;
	}

	/**
	 * 判断接入点类型
	 * 
	 * @return 返回接入点类型
	 */
	public Type getApnType() {
		return apnType;
	}

	public String getApnName() {
		return apnTypeName;
	}

	/**
	 * 判断是否WIFI接入
	 * 
	 * @return 是否WIFI
	 */
	public boolean isWIFI() {
		boolean result = false;
		NetworkInfo info = cm.getActiveNetworkInfo();
		if (info != null && info.getType() == ConnectivityManager.TYPE_WIFI
				&& info.isConnected()) {
			result = true;
		}
		return result;
	}

	/**
	 * 网络连接类型
	 */
	public static enum Type {
		WIFI, NET, WAP, CTWAP, ;
	}

}
