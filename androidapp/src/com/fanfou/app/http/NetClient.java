package com.fanfou.app.http;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.Header;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.impl.client.DefaultHttpClient;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.text.TextUtils;
import android.util.Log;

import com.fanfou.app.App;
import com.fanfou.app.auth.OAuthService;

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
 * 
 */
public class NetClient {

	private static final String TAG = NetClient.class.getSimpleName();

	public NetClient() {
	}

	public final Bitmap getBitmap(String url) throws IOException {
		HttpResponse response = get(url);
		int statusCode = response.getStatusLine().getStatusCode();
		if (App.DEBUG) {
			Log.d(TAG, "getBitmap() statusCode=" + statusCode + " [" + url
					+ "]");
		}
		if (statusCode == 200) {
			return BitmapFactory
					.decodeStream(response.getEntity().getContent());
		}
		return null;
	}

	public final HttpResponse get(String url) throws IOException {
		return executeImpl(new HttpGet(url));
	}

	public final HttpResponse post(String url, List<Parameter> params)
			throws IOException {
		return executeImpl(NetRequest.newBuilder().url(url).params(params).post()
				.build().request);
	}

	public HttpResponse exec(NetRequest cr) throws IOException {
		if (TextUtils.isEmpty(cr.url)) {
			throw new IllegalArgumentException(
					"request url must not be empty or null.");
		}
		signRequest(cr);
		return executeImpl(cr.request);
	}

	protected void signRequest(NetRequest cr) {
	}
	
	protected HttpClient getHttpClient(){
		return NetHelper.newThreadSafeHttpClient();
	}

	private final HttpResponse executeImpl(HttpRequestBase request)
			throws IOException {
		final HttpClient client=getHttpClient();
		NetHelper.setProxy(client);
		if (App.DEBUG) {
			Log.d(TAG, "[Request] " + request.getRequestLine().toString());
		}
		HttpResponse response = client.execute(request);
		if (App.DEBUG) {
			Log.d(TAG, "[Response] " + response.getStatusLine().toString());
		}
		return response;
	}
	
	


}
