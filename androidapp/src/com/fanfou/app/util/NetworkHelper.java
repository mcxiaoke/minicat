/**
 * 
 */
package com.fanfou.app.util;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.zip.GZIPInputStream;

import org.apache.http.Header;
import org.apache.http.HeaderElement;
import org.apache.http.HttpEntity;
import org.apache.http.HttpHost;
import org.apache.http.HttpRequest;
import org.apache.http.HttpRequestInterceptor;
import org.apache.http.HttpResponse;
import org.apache.http.HttpResponseInterceptor;
import org.apache.http.HttpVersion;
import org.apache.http.client.HttpClient;
import org.apache.http.conn.ClientConnectionManager;
import org.apache.http.conn.params.ConnManagerParams;
import org.apache.http.conn.params.ConnPerRoute;
import org.apache.http.conn.params.ConnRoutePNames;
import org.apache.http.conn.routing.HttpRoute;
import org.apache.http.conn.scheme.PlainSocketFactory;
import org.apache.http.conn.scheme.Scheme;
import org.apache.http.conn.scheme.SchemeRegistry;
import org.apache.http.conn.ssl.SSLSocketFactory;
import org.apache.http.entity.HttpEntityWrapper;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.conn.tsccm.ThreadSafeClientConnManager;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.apache.http.params.HttpProtocolParams;
import org.apache.http.protocol.HTTP;
import org.apache.http.protocol.HttpContext;
import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.text.format.DateUtils;
import android.util.Log;

import com.fanfou.app.App;
import com.fanfou.app.R;
import com.fanfou.app.http.GzipResponseInterceptor;
import com.fanfou.app.http.NetworkState;
import com.fanfou.app.http.RequestRetryHandler;
import com.fanfou.app.http.NetworkState.Type;
import com.fanfou.app.update.AutoUpdateManager;

/**
 * @author mcxiaoke
 * @version 1.0 2011.05.05
 * 
 */
public final class NetworkHelper {

	public static final int SOCKET_BUFFER_SIZE = 8192;
	public static final int CONNECTION_TIMEOUT_MS = 20000;
	public static final int SOCKET_TIMEOUT_MS = 20000;

	public static HttpURLConnection newHttpURLConnection(NetworkState state,
			String url) throws IOException {
		HttpURLConnection conn = null;
		URL serverUrl;
		String domain = getDomain(url);
		NetworkState.Type apnType = state.getApnType();
		if (apnType == Type.CTWAP) {
			url = "http://10.0.0.200:80/" + getUrlNoDomain(url);
			serverUrl = new URL(url);
			conn = (HttpURLConnection) serverUrl.openConnection();
			conn.setRequestMethod("GET");
			conn.setRequestProperty("X-Online-Host", domain);
			conn.setRequestProperty("Host", domain);
		} else if (apnType == Type.WAP) {
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

	private static final int SECOND_IN_MILLIS = (int) DateUtils.SECOND_IN_MILLIS;
	private static final String HEADER_ACCEPT_ENCODING = "Accept-Encoding";
	private static final String ENCODING_GZIP = "gzip";

	public static HttpClient getHttpClient(Context context) {
		final HttpParams params = new BasicHttpParams();

		// Use generous timeouts for slow mobile networks
		HttpConnectionParams
				.setConnectionTimeout(params, 20 * SECOND_IN_MILLIS);
		HttpConnectionParams.setSoTimeout(params, 20 * SECOND_IN_MILLIS);

		HttpConnectionParams.setSocketBufferSize(params, 8192);
		HttpProtocolParams.setUserAgent(params, buildUserAgent(context));

		final DefaultHttpClient client = new DefaultHttpClient(params);

		client.addRequestInterceptor(new HttpRequestInterceptor() {
			@Override
			public void process(HttpRequest request, HttpContext context) {
				// Add header to accept gzip content
				if (!request.containsHeader(HEADER_ACCEPT_ENCODING)) {
					request.addHeader(HEADER_ACCEPT_ENCODING, ENCODING_GZIP);
				}
			}
		});

		client.addResponseInterceptor(new HttpResponseInterceptor() {
			@Override
			public void process(HttpResponse response, HttpContext context) {
				// Inflate any responses compressed with gzip
				final HttpEntity entity = response.getEntity();
				final Header encoding = entity.getContentEncoding();
				if (encoding != null) {
					for (HeaderElement element : encoding.getElements()) {
						if (element.getName().equalsIgnoreCase(ENCODING_GZIP)) {
							response.setEntity(new InflatingEntity(response
									.getEntity()));
							break;
						}
					}
				}
			}
		});

		return client;
	}

	/**
	 * Build and return a user-agent string that can identify this application
	 * to remote servers. Contains the package name and version code.
	 */
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
	 * Simple {@link HttpEntityWrapper} that inflates the wrapped
	 * {@link HttpEntity} by passing it through {@link GZIPInputStream}.
	 */
	private static class InflatingEntity extends HttpEntityWrapper {
		public InflatingEntity(HttpEntity wrapped) {
			super(wrapped);
		}

		@Override
		public InputStream getContent() throws IOException {
			return new GZIPInputStream(wrappedEntity.getContent());
		}

		@Override
		public long getContentLength() {
			return -1;
		}
	}

	public static void setProxy(HttpParams params, NetworkState.Type type) {
		if (type == Type.CTWAP) {
			if (App.DEBUG) {
				Log.d("setProxy", "set proxy for ctwap");
			}
			params.setParameter(ConnRoutePNames.DEFAULT_PROXY, new HttpHost(
					"10.0.0.200", 80));
		} else if (type == Type.WAP) {
			if (App.DEBUG) {
				Log.d("setProxy", "set proxy for wap");
			}
			params.setParameter(ConnRoutePNames.DEFAULT_PROXY, new HttpHost(
					"10.0.0.172", 80));
		} else {
			if (App.DEBUG) {
				Log.d("setProxy", "set no proxy");
			}
			params.removeParameter(ConnRoutePNames.DEFAULT_PROXY);
		}
	}

	public static DefaultHttpClient setHttpClient() {
		ConnPerRoute connPerRoute = new ConnPerRoute() {
			@Override
			public int getMaxForRoute(HttpRoute route) {
				return 10;
			}
		};
		HttpParams params = new BasicHttpParams();
		HttpProtocolParams.setVersion(params, HttpVersion.HTTP_1_1);
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
		client.addResponseInterceptor(new GzipResponseInterceptor());
		client.setHttpRequestRetryHandler(new RequestRetryHandler(4));
		return client;
	}
	
	public static void doAutoUpdate(Context context) {
		boolean autoUpdate = OptionHelper.readBoolean(context,
				R.string.option_autoupdate, true);
		if (autoUpdate) {
			Thread task = new Thread() {
				@Override
				public void run() {
					AutoUpdateManager.checkUpdate(App.me);
				}
			};
			task.start();
		}
	}

}
