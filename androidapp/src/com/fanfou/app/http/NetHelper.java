package com.fanfou.app.http;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.nio.charset.Charset;
import java.util.List;

import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.HttpHost;
import org.apache.http.HttpResponse;
import org.apache.http.HttpVersion;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
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
import org.apache.http.entity.mime.MultipartEntity;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.entity.mime.content.StringBody;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.conn.tsccm.ThreadSafeClientConnManager;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.apache.http.params.HttpProtocolParams;
import org.apache.http.protocol.HTTP;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;

import com.fanfou.app.App;
import com.fanfou.app.App.ApnType;
import com.fanfou.app.util.Utils;

/**
 * @author mcxiaoke
 * @version 1.0 2011.12.02
 * @version 1.1 2011.12.07
 * 
 */
public final class NetHelper {
	private static final String TAG=NetHelper.class.getSimpleName();
	public static final int SOCKET_BUFFER_SIZE = 16*1024;
	public static final int CONNECTION_TIMEOUT_MS = 20000;
	public static final int SOCKET_TIMEOUT_MS = 20000;
	public static final int MAX_TOTAL_CONNECTIONS = 20;
	public static final int MAX_RETRY_TIMES = 3;

	public static MultipartEntity encodeMultipart(List<Parameter> params) {
		if (Utils.isEmpty(params)) {
			return null;
		}
		MultipartEntity entity = new MultipartEntity();
		try {
			for (Parameter param : params) {
				if (param.isFile()) {
					entity.addPart(param.getName(),
							new FileBody(param.getFile()));
				} else {
					entity.addPart(
							param.getName(),
							new StringBody(param.getValue(), Charset
									.forName(HTTP.UTF_8)));
				}
			}
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		return entity;
	}

	public static HttpEntity encodeForPost(List<Parameter> params) {
		if (Utils.isEmpty(params)) {
			return null;
		}
		try {
			return new UrlEncodedFormEntity(params, HTTP.UTF_8);
		} catch (UnsupportedEncodingException e) {
		}
		return null;
	}

	public static String encodeForGet(List<Parameter> params) {
		if (Utils.isEmpty(params)) {
			return "";
		}
		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < params.size(); i++) {
			Parameter p = params.get(i);
			if (p.isFile()) {
				throw new IllegalArgumentException("GET参数不能包含文件");
			}
			if (i > 0) {
				sb.append("&");
			}
			sb.append(encode(p.getName())).append("=")
					.append(encode(p.getValue()));
		}

		return sb.toString();
	}

	public static boolean containsFile(List<Parameter> params) {
		if (Utils.isEmpty(params)) {
			return false;
		}
		boolean containsFile = false;
		for (Parameter param : params) {
			if (param.isFile()) {
				containsFile = true;
				break;
			}
		}
		return containsFile;
	}

	public static String encode(String input) {
		try {
			return URLEncoder.encode(input, HTTP.UTF_8);
		} catch (UnsupportedEncodingException e) {
		}
		return input;
	}

	public final static void setHeaders(HttpRequestBase request,
			List<Header> headers) {
		if (headers != null) {
			for (Header header : headers) {
				request.addHeader(header);
			}
		}
	}

	public static boolean containsFile(Parameter[] params) {
		boolean containsFile = false;
		if (null == params) {
			return false;
		}
		for (Parameter param : params) {
			if (param.isFile()) {
				containsFile = true;
				break;
			}
		}
		return containsFile;
	}

	public final static synchronized DefaultHttpClient newHttpClient() {
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
				+ App.appVersionName);

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
		DefaultHttpClient client = new DefaultHttpClient(manager, params);
		client.addRequestInterceptor(new GzipRequestInterceptor());
		client.addResponseInterceptor(new GzipResponseInterceptor());
		client.setHttpRequestRetryHandler(new RequestRetryHandler(
				MAX_RETRY_TIMES));
		return client;
	}

	public final static void setProxy(final HttpClient client) {
		if (client == null) {
			return;
		}
		HttpParams params = client.getParams();
		ApnType type = App.getApnType();
		if (type == ApnType.CTWAP) {
			if (App.DEBUG) {
				Log.d(TAG, "use proxy 10.0.0.200:80");
			}
			params.setParameter(ConnRoutePNames.DEFAULT_PROXY, new HttpHost(
					"10.0.0.200", 80));
		} else if (type == ApnType.WAP) {
			if (App.DEBUG) {
				Log.d(TAG, "use proxy 10.0.0.172:80");
			}
			params.setParameter(ConnRoutePNames.DEFAULT_PROXY, new HttpHost(
					"10.0.0.172", 80));
		} else {
			params.removeParameter(ConnRoutePNames.DEFAULT_PROXY);
		}
	}

	public static Parameter[] getParameterArray(String name, String value) {
		return new Parameter[] { new Parameter(name, value) };
	}

	public static Parameter[] getParameterArray(String name, int value) {
		return getParameterArray(name, String.valueOf(value));
	}

	public static Parameter[] getParameterArray(String name1, String value1,
			String name2, String value2) {
		return new Parameter[] { new Parameter(name1, value1),
				new Parameter(name2, value2) };
	}

	public static Parameter[] getParameterArray(String name1, int value1,
			String name2, int value2) {
		return getParameterArray(name1, String.valueOf(value1), name2,
				String.valueOf(value2));
	}
}
