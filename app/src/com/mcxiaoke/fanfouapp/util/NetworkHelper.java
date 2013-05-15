/**
 * 
 */
package com.mcxiaoke.fanfouapp.util;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;


/**
 * @author mcxiaoke
 * @version 1.0 2011.05.05
 * @version 2.0 2011.10.29
 * 
 */
public final class NetworkHelper {
	private static final String TAG = NetworkHelper.class.getSimpleName();

	public static final int SOCKET_BUFFER_SIZE = 2048;
	public static final int CONNECTION_TIMEOUT_MS = 5000;
	public static final int SOCKET_TIMEOUT_MS = 15000;

	/**
	 * @param url
	 *            链接地址
	 * @return URL的域名部分
	 */
	public static String getDomain(String url) {
		String domain = "";
		if ((url == null) || ("".equals(url))) {
			return "";
		} else {
			domain = url.replaceAll("http://", "");
			domain = domain.substring(0, domain.indexOf('/'));
			return domain;
		}
	}

	/**
	 * @param url
	 *            链接地址
	 * @return URL的相对链接地址(不包含域名)
	 */
	public static String getUrlNoDomain(String url) {
		String domain = "";
		if ((url == null) || ("".equals(url))) {
			return "";
		} else {
			domain = url.replaceAll("http://", "");
			domain = domain.substring(domain.indexOf('/') + 1);
			return domain;
		}
	}

	public static boolean isConnected(Context context) {
		if (context == null)
			return false;
		ConnectivityManager connectivity = (ConnectivityManager) context
				.getSystemService(Context.CONNECTIVITY_SERVICE);
		if (connectivity == null) {
			LogUtil.d(TAG, "+++couldn't get connectivity manager");
		} else {
			NetworkInfo[] info = connectivity.getAllNetworkInfo();
			if (info != null) {
				for (int i = 0; i < info.length; i++) {
					if (info[i].getState() == NetworkInfo.State.CONNECTED) {
						LogUtil.d(TAG, "+++network is available");
						return true;
					}
				}
			}
		}

		LogUtil.d(TAG, "+++network is not available");

		return false;
	}

	public static boolean isWifi(Context context) {
		ConnectivityManager connec = (ConnectivityManager) context
				.getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo info = connec.getActiveNetworkInfo();
		String typeName = "";
		if (info != null) {
			typeName = info.getTypeName();
		}

		return "wifi".equalsIgnoreCase(typeName);
	}

}
