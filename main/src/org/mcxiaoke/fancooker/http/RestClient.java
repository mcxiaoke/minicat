package org.mcxiaoke.fancooker.http;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Map;

import org.apache.http.Header;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.protocol.HTTP;
import org.mcxiaoke.fancooker.AppContext;

import android.util.Log;


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
 * @version 6.1 2012.02.23
 * @version 7.0 2012.02.27
 * 
 */
public class RestClient {

	private static final String TAG = RestClient.class.getSimpleName();

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

	public RestClient() {

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

	public RestResponse get(String url, boolean authenticated)
			throws IOException {
		RestRequest.Builder builder = RestRequest.newBuilder();
		builder.url(url).post();
		return execute(builder.build(), authenticated);
	}

	public RestResponse get(String url, List<NameValuePair> params,
			boolean authenticated) throws IOException {
		RestRequest.Builder builder = RestRequest.newBuilder();
		builder.url(url).post();
		if (params != null) {
			builder.withNameValuePair(params);
		}
		return execute(builder.build(), authenticated);
	}

	public RestResponse get(String url, NameValuePair[] params,
			boolean authenticated) throws IOException {
		RestRequest.Builder builder = RestRequest.newBuilder();
		builder.url(url).post();
		if (params != null) {
			builder.withNameValuePair(params);
		}
		return execute(builder.build(), authenticated);
	}

	public RestResponse get(String url, Map<String, String> map,
			boolean authenticated) throws IOException {
		RestRequest.Builder builder = RestRequest.newBuilder();
		builder.url(url).post();
		if (map != null) {
			builder.params(map);
		}
		return execute(builder.build(), authenticated);
	}

	public RestResponse post(String url, boolean authenticated)
			throws IOException {
		RestRequest.Builder builder = RestRequest.newBuilder();
		builder.url(url).post();
		return execute(builder.build(), authenticated);
	}

	public RestResponse post(String url, List<Parameter> params,
			boolean authenticated) throws IOException {
		RestRequest.Builder builder = RestRequest.newBuilder();
		builder.url(url).post();
		if (params != null) {
			builder.params(params);
		}
		return execute(builder.build(), authenticated);
	}

	public RestResponse post(String url, Parameter[] params,
			boolean authenticated) throws IOException {
		RestRequest.Builder builder = RestRequest.newBuilder();
		builder.url(url).post();
		if (params != null) {
			builder.params(params);
		}
		return execute(builder.build(), authenticated);
	}

	public RestResponse post(String url, NameValuePair[] params,
			boolean authenticated) throws IOException {
		RestRequest.Builder builder = RestRequest.newBuilder();
		builder.url(url).post();
		if (params != null) {
			builder.withNameValuePair(params);
		}
		return execute(builder.build(), authenticated);
	}

	public RestResponse post(String url, Map<String, String> map,
			boolean authenticated) throws IOException {
		RestRequest.Builder builder = RestRequest.newBuilder();
		builder.url(url).post();
		if (map != null) {
			builder.params(map);
		}
		return execute(builder.build(), authenticated);
	}

	public RestResponse postWithFile(String url, List<NameValuePair> params,
			String name, File file, boolean authenticated) throws IOException {
		RestRequest.Builder builder = RestRequest.newBuilder();
		builder.url(url).post();
		if (file != null && name != null) {
			builder.param(name, file);
		}
		if (params != null) {
			builder.withNameValuePair(params);
		}
		return execute(builder.build(), authenticated);
	}

	public RestResponse postWithFile(String url, NameValuePair[] params,
			String name, File file, boolean authenticated) throws IOException {
		RestRequest.Builder builder = RestRequest.newBuilder();
		builder.url(url).post();
		if (file != null && name != null) {
			builder.param(name, file);
		}
		if (params != null) {
			builder.withNameValuePair(params);
		}
		return execute(builder.build(), authenticated);
	}

	public RestResponse postWithFile(String url, Map<String, String> map,
			String name, File file, boolean authenticated) throws IOException {
		RestRequest.Builder builder = RestRequest.newBuilder();
		builder.url(url).post();
		if (file != null && name != null) {
			builder.param(name, file);
		}
		for (String key : map.keySet()) {
			builder.param(key, map.get(key));
		}
		return execute(builder.build(), authenticated);
	}

	public RestResponse execute(RestRequest cr) throws IOException {
		HttpResponse response = execute(cr.request);
		return new RestResponse(response);
	}

	public RestResponse execute(RestRequest cr, boolean authenticated)
			throws IOException {
		HttpResponse response = execute(cr.request);
		return new RestResponse(response);
	}

	private HttpResponse execute(HttpUriRequest request) throws IOException {
		final HttpClient client = NetHelper.getHttpClient();
		if (AppContext.DEBUG) {
			Log.d(TAG, "[Request] " + request.getRequestLine().toString()
					+ " --" + System.currentTimeMillis());
			Header[] headers = request.getAllHeaders();
			for (Header header : headers) {
				Log.d(TAG, "[Request Header] " + header.getName() + ":"
						+ header.getValue());
			}
		}
		HttpResponse response = client.execute(request);
		if (AppContext.DEBUG) {
			Log.d(TAG, "[Response] " + response.getStatusLine().toString()
					+ " --" + System.currentTimeMillis());
			Header[] headers = response.getAllHeaders();
			for (Header header : headers) {
				Log.d(TAG, "[Response Header] " + header.getName() + ":"
						+ header.getValue());
			}
		}
		return response;
	}

}
