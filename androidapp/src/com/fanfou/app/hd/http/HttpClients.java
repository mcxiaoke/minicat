package com.fanfou.app.hd.http;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Map;

import org.apache.http.HttpHost;
import org.apache.http.HttpResponse;
import org.apache.http.HttpVersion;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.conn.params.ConnRoutePNames;
import org.apache.http.conn.scheme.PlainSocketFactory;
import org.apache.http.conn.scheme.Scheme;
import org.apache.http.conn.scheme.SchemeRegistry;
import org.apache.http.conn.ssl.SSLSocketFactory;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.apache.http.params.HttpProtocolParams;
import org.apache.http.protocol.HTTP;

import android.util.Log;

import com.fanfou.app.hd.App;
import com.fanfou.app.hd.App.ApnType;
import com.fanfou.app.hd.auth.OAuthService;

/**
 * @author mcxiaoke
 * @version 1.0 2011.05.02
 * @version 1.1 2011.05.03
 * @version 1.2 2011.05.03
 * @version 1.3 2011.05.04
 * @version 1.4 2011.05.05
 * @version 1.5 2011.10.25
 * @version 2.0 2011.11.03
 * @version 2.1 2011.11.04
 * @version 3.0 2011.11.09
 * @version 3.1 2011.11.15
 * @version 3.2 2011.11.24
 * @version 3.3 2011.11.28
 * @version 3.4 2011.11.29
 * @version 4.0 2011.12.01
 * @version 4.1 2011.12.02
 * @version 4.2 2011.12.05
 * @version 4.3 2011.12.07
 * @version 5.0 2011.12.12
 * @version 6.0 2012.02.20
 * 
 */
public class HttpClients {

	private static final String TAG = HttpClients.class.getSimpleName();

	public static final String DEFAULT_CONTENT_CHARSET = HTTP.UTF_8;
	public static final int DEFAULT_CONNECTION_TIMEOUT_MS = 30 * 1000;
	public static final int DEFAULT_SOCKET_TIMEOUT_MS = 30 * 1000;
	public static final int DEFAULT_RETRY_COUNT = 3;
	public static final int DEFAULT_RETRY_INTERVAL_MS = 3 * 1000;

	private int retryCount = DEFAULT_RETRY_COUNT;
	private int retryIntervalMillis = DEFAULT_RETRY_INTERVAL_MS;
	private int connectionTimeout = DEFAULT_CONNECTION_TIMEOUT_MS;
	private int socketTimeout = DEFAULT_SOCKET_TIMEOUT_MS;
	private String contentCharset = DEFAULT_CONTENT_CHARSET;
	private String userAgent;
	private String proxyIP;
	private int proxyPort;

	private OAuthService oAuthService;
	
	public HttpClients(){
		
	}

	public int getRetryCount() {
		return retryCount;
	}

	public void setRetryCount(int retryCount) {
		this.retryCount = retryCount;
	}

	public int getRetryIntervalMillis() {
		return retryIntervalMillis;
	}

	public void setRetryIntervalMillis(int retryIntervalMillis) {
		this.retryIntervalMillis = retryIntervalMillis;
	}

	public int getConnectionTimeout() {
		return connectionTimeout;
	}

	public void setConnectionTimeout(int connectionTimeout) {
		this.connectionTimeout = connectionTimeout;
	}

	public int getSocketTimeout() {
		return socketTimeout;
	}

	public void setSocketTimeout(int socketTimeout) {
		this.socketTimeout = socketTimeout;
	}

	public String getContentCharset() {
		return contentCharset;
	}

	public void setContentCharset(String contentCharset) {
		this.contentCharset = contentCharset;
	}

	public String getUserAgent() {
		return userAgent;
	}

	public void setUserAgent(String userAgent) {
		this.userAgent = userAgent;
	}

	public OAuthService getoAuthService() {
		return oAuthService;
	}

	public void setoAuthService(OAuthService oAuthService) {
		this.oAuthService = oAuthService;
	}

	public void setProxy(String ip, int port) {
		this.proxyIP = ip;
		this.proxyPort = port;
	}

	public NetResponse get(String url, boolean authenticated)
			throws IOException {
		NetRequest.Builder builder = NetRequest.newBuilder();
		builder.url(url).post();
		return execute(builder.build(), authenticated);
	}

	public NetResponse get(String url, List<NameValuePair> params,
			boolean authenticated) throws IOException {
		NetRequest.Builder builder = NetRequest.newBuilder();
		builder.url(url).post();
		if (params != null) {
			builder.withNameValuePair(params);
		}
		return execute(builder.build(), authenticated);
	}

	public NetResponse get(String url, NameValuePair[] params,
			boolean authenticated) throws IOException {
		NetRequest.Builder builder = NetRequest.newBuilder();
		builder.url(url).post();
		if (params != null) {
			builder.withNameValuePair(params);
		}
		return execute(builder.build(), authenticated);
	}

	public NetResponse get(String url, Map<String, String> map,
			boolean authenticated) throws IOException {
		NetRequest.Builder builder = NetRequest.newBuilder();
		builder.url(url).post();
		if (map != null) {
			builder.params(map);
		}
		return execute(builder.build(), authenticated);
	}

	public NetResponse post(String url, boolean authenticated)
			throws IOException {
		NetRequest.Builder builder = NetRequest.newBuilder();
		builder.url(url).post();
		return execute(builder.build(), authenticated);
	}

	public NetResponse post(String url, List<Parameter> params,
			boolean authenticated) throws IOException {
		NetRequest.Builder builder = NetRequest.newBuilder();
		builder.url(url).post();
		if (params != null) {
			builder.params(params);
		}
		return execute(builder.build(), authenticated);
	}
	
	public NetResponse post(String url, Parameter[] params,
			boolean authenticated) throws IOException {
		NetRequest.Builder builder = NetRequest.newBuilder();
		builder.url(url).post();
		if (params != null) {
			builder.params(params);
		}
		return execute(builder.build(), authenticated);
	}

	public NetResponse post(String url, NameValuePair[] params,
			boolean authenticated) throws IOException {
		NetRequest.Builder builder = NetRequest.newBuilder();
		builder.url(url).post();
		if (params != null) {
			builder.withNameValuePair(params);
		}
		return execute(builder.build(), authenticated);
	}

	public NetResponse post(String url, Map<String, String> map,
			boolean authenticated) throws IOException {
		NetRequest.Builder builder = NetRequest.newBuilder();
		builder.url(url).post();
		if (map != null) {
			builder.params(map);
		}
		return execute(builder.build(), authenticated);
	}

	public NetResponse postWithFile(String url, List<NameValuePair> params,
			String name, File file, boolean authenticated) throws IOException {
		NetRequest.Builder builder = NetRequest.newBuilder();
		builder.url(url).post();
		if (file != null && name != null) {
			builder.param(name, file);
		}
		if (params != null) {
			builder.withNameValuePair(params);
		}
		return execute(builder.build(), authenticated);
	}

	public NetResponse postWithFile(String url, NameValuePair[] params,
			String name, File file, boolean authenticated) throws IOException {
		NetRequest.Builder builder = NetRequest.newBuilder();
		builder.url(url).post();
		if (file != null && name != null) {
			builder.param(name, file);
		}
		if (params != null) {
			builder.withNameValuePair(params);
		}
		return execute(builder.build(), authenticated);
	}

	public NetResponse postWithFile(String url, Map<String, String> map,
			String name, File file, boolean authenticated) throws IOException {
		NetRequest.Builder builder = NetRequest.newBuilder();
		builder.url(url).post();
		if (file != null && name != null) {
			builder.param(name, file);
		}
		for (String key : map.keySet()) {
			builder.param(key, map.get(key));
		}
		return execute(builder.build(), authenticated);
	}
	
	public NetResponse execute(NetRequest cr)
			throws IOException {
		HttpResponse response = execute(cr.request);
		return new NetResponse(response);
	}

	public NetResponse execute(NetRequest cr, boolean authenticated)
			throws IOException {
		if (authenticated) {
			authorize(cr);
		}
		HttpResponse response = execute(cr.request);
		return new NetResponse(response);
	}

	private void authorize(NetRequest cr) {
		if(oAuthService==null){
			return;
		}
		oAuthService.authorize(cr.request, cr.getParams());
	}

	private HttpResponse execute(HttpUriRequest request) throws IOException {
		final HttpClient client = getHttpClient();
		// NetHelper.setProxy(client);
		if (App.DEBUG) {
			Log.d(TAG, "[Request] " + request.getRequestLine().toString()
					+ " --" + System.currentTimeMillis());
		}
		HttpResponse response = client.execute(request);
		if (App.DEBUG) {
			Log.d(TAG, "[Response] " + response.getStatusLine().toString()
					+ " --" + System.currentTimeMillis());
		}
		return response;
	}

	private DefaultHttpClient getHttpClient() {
		HttpParams params = new BasicHttpParams();
		HttpProtocolParams.setVersion(params, HttpVersion.HTTP_1_1);
		HttpProtocolParams.setContentCharset(params, contentCharset);
		HttpConnectionParams.setConnectionTimeout(params, connectionTimeout);
		HttpConnectionParams.setSoTimeout(params, socketTimeout);
		HttpConnectionParams.setTcpNoDelay(params, true);
		// HttpProtocolParams.setUserAgent(params,userAgent);

		SchemeRegistry schReg = new SchemeRegistry();
		schReg.register(new Scheme("http", PlainSocketFactory
				.getSocketFactory(), 80));
		schReg.register(new Scheme("https",
				SSLSocketFactory.getSocketFactory(), 443));
		DefaultHttpClient client = new DefaultHttpClient(params);
		client.addRequestInterceptor(new GzipRequestInterceptor());
		client.addResponseInterceptor(new GzipResponseInterceptor());
		client.setHttpRequestRetryHandler(new RequestRetryHandler(retryCount));
		return client;
	}

	private void setProxy(final HttpClient client) {
		HttpParams params = client.getParams();
		ApnType type = App.getApnType();
		if (type == ApnType.WAP) {
			if (App.DEBUG) {
				Log.d(TAG, "use proxy 10.0.0.172:80");
			}
			params.setParameter(ConnRoutePNames.DEFAULT_PROXY, new HttpHost(
					"10.0.0.172", 80));
		} else {
			params.removeParameter(ConnRoutePNames.DEFAULT_PROXY);
		}
	}

}
