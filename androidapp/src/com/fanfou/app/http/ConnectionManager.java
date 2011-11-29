package com.fanfou.app.http;

import java.io.IOException;
import java.util.List;

import org.apache.http.Header;
import org.apache.http.HttpHost;
import org.apache.http.HttpResponse;
import org.apache.http.HttpVersion;
import org.apache.http.RequestLine;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.client.params.HttpClientParams;
import org.apache.http.conn.ClientConnectionManager;
import org.apache.http.conn.params.ConnManagerParams;
import org.apache.http.conn.params.ConnPerRouteBean;
import org.apache.http.conn.params.ConnRoutePNames;
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

import android.util.Log;

import com.fanfou.app.App;
import com.fanfou.app.App.ApnType;
import com.fanfou.app.api.ApiException;
import com.fanfou.app.auth.OAuth;
import com.fanfou.app.util.StringHelper;

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
 * 
 */
public final class ConnectionManager {

	private static final String TAG = ConnectionManager.class.getSimpleName();

	public static final int SOCKET_BUFFER_SIZE = 8192;
	public static final int CONNECTION_TIMEOUT_MS = 20000;
	public static final int SOCKET_TIMEOUT_MS = 20000;
	public static final int MAX_TOTAL_CONNECTIONS = 20;
	public static final int MAX_RETRY_TIMES = 3;

	private DefaultHttpClient mHttpClient;

	// private static final ConnectionManager INSTANCE = new
	// ConnectionManager();

	public static final ConnectionManager newInstance() {
		return new ConnectionManager();
	}

	private final void log(String message) {
		Log.d(TAG, message);
	}

	private ConnectionManager() {
		prepareHttpClient();
	}

	public final void setHttpClient(DefaultHttpClient client) {
		mHttpClient = client;
	}

	public final HttpResponse get(String url) throws IOException {
		return getImpl(url);
	}

	public final HttpResponse post(String url, List<Parameter> params)
			throws IOException {
		return postImpl(url, params);
	}

	public final HttpResponse execWithOAuth(ConnectionRequest cr)
			throws IOException, ApiException {

		HttpRequestBase request = null;
		if (cr.entity != null) {
			request = new HttpPost(cr.url);
			((HttpPost) request).setEntity(cr.entity);
		} else {
			request = new HttpGet(cr.url);
		}
		setHeaders(request, cr.headers);
		setOAuth(request, cr.params);
		return execute(request);
	}

	public final HttpResponse exec(ConnectionRequest cr) throws IOException,
			ApiException {

		HttpRequestBase request = null;
		if (cr.entity != null) {
			request = new HttpPost(cr.url);
			((HttpPost) request).setEntity(cr.entity);
		} else {
			request = new HttpGet(cr.url);
		}
		setHeaders(request, cr.headers);
		return execute(request);
	}

	private final HttpResponse getImpl(String url) throws IOException {
		HttpGet request = new HttpGet(url);
		return execute(request);
	}

	private final HttpResponse postImpl(String url, List<Parameter> params)
			throws IOException {
		HttpPost request = new HttpPost(url);
		request.setEntity(ConnectionRequest.encodeForPost(params));
		return execute(request);
	}

	private final HttpResponse execute(HttpRequestBase request)
			throws IOException {
		setProxy(mHttpClient);
		if (App.DEBUG) {
			log("==========[Request]==========");
			log(request.getRequestLine().toString());
//			Header[] headers = request.getAllHeaders();
//			for (Header header : headers) {
//				log(header.getName() + ":"
//						+ header.getValue());
//			}
		}
		HttpResponse response = mHttpClient.execute(request);
		if (App.DEBUG) {
			log("==========[Response]==========");
			log(response.getStatusLine().toString());
//			Header[] headers = response.getAllHeaders();
//			for (Header header : headers) {
//				log(header.getName() + ":"
//						+ header.getValue());
//			}
//			log("\n");
		}
		return response;
	}

	private final static void setHeaders(HttpRequestBase request,
			List<Header> headers) {
		if (headers != null) {
			for (Header header : headers) {
				request.addHeader(header);
			}
		}
	}

	private final synchronized void prepareHttpClient() {
		HttpParams params = new BasicHttpParams();

		HttpConnectionParams.setStaleCheckingEnabled(params, false);
		HttpProtocolParams.setVersion(params, HttpVersion.HTTP_1_1);
		HttpProtocolParams.setContentCharset(params, HTTP.UTF_8);
		HttpProtocolParams.setUseExpectContinue(params, true);
		HttpConnectionParams
				.setConnectionTimeout(params, CONNECTION_TIMEOUT_MS);
		HttpConnectionParams.setSoTimeout(params, SOCKET_TIMEOUT_MS);
		HttpConnectionParams.setSocketBufferSize(params, SOCKET_BUFFER_SIZE);
		HttpConnectionParams.setTcpNoDelay(params, true);
		HttpProtocolParams.setUserAgent(params, "FanFou for Android/"
				+ App.me.appVersionName);

		HttpClientParams.setRedirecting(params, false);

		ConnManagerParams.setMaxConnectionsPerRoute(params,
				new ConnPerRouteBean(MAX_TOTAL_CONNECTIONS));
		ConnManagerParams.setMaxTotalConnections(params, MAX_TOTAL_CONNECTIONS);

		SchemeRegistry schReg = new SchemeRegistry();
		schReg.register(new Scheme("http", PlainSocketFactory
				.getSocketFactory(), 80));
		schReg.register(new Scheme("https",
				SSLSocketFactory.getSocketFactory(), 443));
		ClientConnectionManager manager = new ThreadSafeClientConnManager(
				params, schReg);
		mHttpClient = new DefaultHttpClient(manager, params);
		mHttpClient.addRequestInterceptor(new GzipRequestInterceptor());
		mHttpClient.addResponseInterceptor(new GzipResponseInterceptor());
		mHttpClient.setHttpRequestRetryHandler(new RequestRetryHandler(
				MAX_RETRY_TIMES));
	}

	private final static void setProxy(final HttpClient client) {
		if (client == null) {
			return;
		}
		HttpParams params = client.getParams();
		ApnType type = App.me.apnType;
		if (type == ApnType.CTWAP) {
			if (App.DEBUG) {
				Log.d("setProxy", "use proxy 10.0.0.200:80");
			}
			params.setParameter(ConnRoutePNames.DEFAULT_PROXY, new HttpHost(
					"10.0.0.200", 80));
		} else if (type == ApnType.WAP) {
			if (App.DEBUG) {
				Log.d("setProxy", "use proxy 10.0.0.172:80");
			}
			params.setParameter(ConnRoutePNames.DEFAULT_PROXY, new HttpHost(
					"10.0.0.172", 80));
		} else {
			if (App.DEBUG) {
				Log.d("setProxy", "use no proxy, direct connect");
			}
			params.removeParameter(ConnRoutePNames.DEFAULT_PROXY);
		}
	}

	private final static void setOAuth(HttpRequestBase request,
			List<Parameter> params) throws ApiException {
		if (StringHelper.isEmpty(App.me.oauthAccessToken)
				|| StringHelper.isEmpty(App.me.oauthAccessTokenSecret)) {
			throw new ApiException(ResponseCode.ERROR_AUTH_FAILED, "未通过验证，请登录");
		} else {
			request.addHeader("Host", "api.fanfou.com");
			OAuth oauth = new OAuth(App.me.oauthAccessToken,
					App.me.oauthAccessTokenSecret);
			oauth.signRequest(request, params);
		}
	}

	// private static void setBasicAuth(HttpUriRequest request,
	// List<Parameter> params) throws ApiException {
	// if (null != username && null != password) {
	// String basicAuth = "Basic "
	// + Base64.encodeBytes((username + ":" + password).getBytes());
	// BasicHeader header = new BasicHeader("Authorization", basicAuth);
	// request.setHeader(header);
	// } else {
	// throw new ApiException(ResponseCode.ERROR_AUTH_EMPTY,
	// "username and password must not be empty.");
	// }
	// }

}
