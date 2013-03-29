/**
 * 
 */
package com.fanfou.app.hd.util;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;

import org.apache.http.HttpHost;
import org.apache.http.HttpVersion;
import org.apache.http.conn.ClientConnectionManager;
import org.apache.http.conn.params.ConnManagerParams;
import org.apache.http.conn.params.ConnPerRoute;
import org.apache.http.conn.params.ConnRoutePNames;
import org.apache.http.conn.routing.HttpRoute;
import org.apache.http.conn.scheme.PlainSocketFactory;
import org.apache.http.conn.scheme.Scheme;
import org.apache.http.conn.scheme.SchemeRegistry;
import org.apache.http.conn.ssl.SSLSocketFactory;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.conn.tsccm.ThreadSafeClientConnManager;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.apache.http.params.HttpProtocolParams;
import org.apache.http.protocol.HTTP;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.WifiManager;
import android.text.TextUtils;
import android.util.Log;

import com.fanfou.app.hd.App;
import com.fanfou.app.hd.App.ApnType;

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

	public static HttpURLConnection newHttpURLConnection(ApnType apnType,
			String url) throws IOException {
		HttpURLConnection conn = null;
		URL serverUrl;
		String domain = getDomain(url);
		if (apnType == ApnType.WAP) {
			url = "http://10.0.0.172:80/" + getUrlNoDomain(url);
			serverUrl = new URL(url);
			conn = (HttpURLConnection) serverUrl.openConnection();
			conn.setRequestMethod("GET");
			conn.setRequestProperty("X-Online-Host", domain);
			conn.setRequestProperty("Host", domain);
		} else {
			serverUrl = new URL(url);
			conn = (HttpURLConnection) serverUrl.openConnection();
		}
		conn.setRequestMethod("GET");
		conn.setRequestProperty("Accept", "*/*");
		conn.setConnectTimeout(20 * 1000);
		conn.setRequestProperty("Accept-Language", "zh-CN");
		conn.setRequestProperty("Charset", "UTF-8");
		// conn.setRequestProperty("User-Agent", mContext
		// .getString(R.string.userAgent));
		conn.setDoInput(true);
		conn.setDoOutput(true);
		return conn;
	}

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

	/**
	 * Build and return a user-agent string that can identify this application
	 * to remote servers. Contains the package name and version code.
	 */
	@SuppressWarnings("unused")
	private static String buildUserAgent(Context context) {
		try {
			final PackageManager manager = context.getPackageManager();
			final PackageInfo info = manager.getPackageInfo(
					context.getPackageName(), 0);

			// Some APIs require "(gzip)" in the user-agent string.
			return info.packageName + "/" + info.versionName + " ("
					+ info.versionCode + ") (gzip)";
		} catch (NameNotFoundException e) {
			return null;
		}
	}

	/**
	 * 根据当前网络状态填充代理
	 * 
	 * @param context
	 * @param httpParams
	 */
	public static void setProxy(final Context context,
			final HttpParams httpParams) {
		if (context == null) {
			return;
		}

		WifiManager wifiManager = (WifiManager) context
				.getSystemService(Context.WIFI_SERVICE);
		if (wifiManager.isWifiEnabled()) {
			return;
		}

		ConnectivityManager connectivityManager = (ConnectivityManager) context
				.getSystemService(Context.CONNECTIVITY_SERVICE);
		final NetworkInfo networkInfo = connectivityManager
				.getActiveNetworkInfo();
		if (networkInfo == null || networkInfo.getExtraInfo() == null) {
			return;
		}
		String info = networkInfo.getExtraInfo().toLowerCase();
		// 3gnet/3gwap/uninet/uniwap/cmnet/cmwap/ctnet/ctwap
		// 先根据网络apn信息判断,并进行 proxy 自动补齐
		if (info != null) {
			if (info.startsWith("cmwap") || info.startsWith("uniwap")
					|| info.startsWith("3gwap")) {
				HttpHost proxy = new HttpHost("10.0.0.172", 80);
				httpParams.setParameter(ConnRoutePNames.DEFAULT_PROXY, proxy);
				return;
			} else if (info.startsWith("ctwap")) {
				HttpHost proxy = new HttpHost("10.0.0.200", 80);
				httpParams.setParameter(ConnRoutePNames.DEFAULT_PROXY, proxy);
				return;
			} else if (info.startsWith("cmnet") || info.startsWith("uninet")
					|| info.startsWith("ctnet") || info.startsWith("3gnet")) {
				return;
			} // else fall through
		} // else fall through
	}

	public static ApnType getApnType(Context context) {
		ApnType type = ApnType.NET;
		try {
			ConnectivityManager cm = (ConnectivityManager) context
					.getSystemService(Context.CONNECTIVITY_SERVICE);
			NetworkInfo info = cm.getActiveNetworkInfo();
			if (App.DEBUG) {
				Log.d("App", "NetworkInfo: " + info);
			}
			if (info != null) {
				if (info.getType() == ConnectivityManager.TYPE_WIFI) {
					type = ApnType.WIFI;
				} else if (info.getType() == ConnectivityManager.TYPE_MOBILE) {
					String apnTypeName = info.getExtraInfo().toLowerCase();
					if (!TextUtils.isEmpty(apnTypeName)) {
						if ("ctwap".equals(apnTypeName)) {
							type = ApnType.CTWAP;
						} else if (apnTypeName.contains("wap")) {
							type = ApnType.WAP;
						} else if (apnTypeName.equals("3gnet")) {
							type = ApnType.NET;
						}
					}
				}
			} else {
				type = ApnType.WIFI;
			}
		} catch (Exception e) {
			if (App.DEBUG) {
				Log.d("NetworkHelper", e.toString());
			}
		}
		return type;
	}

	static DefaultHttpClient newHttpClient() {
		ConnPerRoute connPerRoute = new ConnPerRoute() {
			@Override
			public int getMaxForRoute(HttpRoute route) {
				return 10;
			}
		};
		HttpParams params = new BasicHttpParams();
		HttpProtocolParams.setVersion(params, HttpVersion.HTTP_1_1);
		// ConnManagerParams.setTimeout(params, CONNECTION_TIMEOUT_MS);
		ConnManagerParams.setMaxConnectionsPerRoute(params, connPerRoute);
		HttpProtocolParams.setContentCharset(params, HTTP.UTF_8);
		HttpConnectionParams
				.setConnectionTimeout(params, CONNECTION_TIMEOUT_MS);
		HttpConnectionParams.setSoTimeout(params, SOCKET_TIMEOUT_MS);
		HttpConnectionParams.setSocketBufferSize(params, SOCKET_BUFFER_SIZE);

		SchemeRegistry schReg = new SchemeRegistry();
		schReg.register(new Scheme("http", PlainSocketFactory
				.getSocketFactory(), 80));
		schReg.register(new Scheme("https",
				SSLSocketFactory.getSocketFactory(), 443));
		ClientConnectionManager manager = new ThreadSafeClientConnManager(
				params, schReg);
		DefaultHttpClient client = new DefaultHttpClient(manager, params);
		// client.addResponseInterceptor(new GzipResponseInterceptor());
		// client.setHttpRequestRetryHandler(new RequestRetryHandler(3));
		return client;
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
