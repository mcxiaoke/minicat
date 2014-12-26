/**
 *
 */
package com.mcxiaoke.minicat.util;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;


/**
 * @author mcxiaoke
 * @version 2.0 2011.10.29
 */
public final class NetworkHelper {
    public static final int SOCKET_BUFFER_SIZE = 2048;
    public static final int CONNECTION_TIMEOUT_MS = 5000;
    public static final int SOCKET_TIMEOUT_MS = 15000;
    private static final String TAG = NetworkHelper.class.getSimpleName();

    public static boolean isConnected(Context context) {
        if (context == null)
            return false;
        ConnectivityManager connectivity = (ConnectivityManager) context
                .getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo info = connectivity.getActiveNetworkInfo();
        return info != null && info.isConnectedOrConnecting();
    }

    public static boolean isNotConnected(Context context) {
        return !isConnected(context);
    }

    public static boolean isWifi(Context context) {
        ConnectivityManager connectivityManager = (ConnectivityManager) context
                .getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo info = connectivityManager.getActiveNetworkInfo();
        return info != null && info.getType() == ConnectivityManager.TYPE_WIFI;
    }

    public static String getNetworkInfo(Context context) {
        if (context == null)
            return "";
        StringBuilder builder = new StringBuilder();
        ConnectivityManager connectivity = (ConnectivityManager) context
                .getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo info = connectivity.getActiveNetworkInfo();
        builder.append(info == null ? "" : info.toString());
        return builder.toString();
    }

}
