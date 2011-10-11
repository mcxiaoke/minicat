package com.fanfou.app.http;

import java.io.IOException;
import java.util.List;

import org.apache.http.Header;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.impl.client.DefaultHttpClient;

import android.util.Log;

import com.fanfou.app.App;
import com.fanfou.app.api.ApiException;
import com.fanfou.app.util.NetworkHelper;

/**
 * @author mcxiaoke
 * @version 1.0 2011.05.02
 * @version 1.1 2011.05.03
 * @version 1.2 2011.05.03
 * @version 1.3 2011.05.04
 * @version 1.4 2011.05.05
 * 
 */
public abstract class BaseClient implements ResponseCode {

	private static final String TAG = BaseClient.class.getSimpleName();

	DefaultHttpClient mHttpClient;

	void log(String message) {
		Log.d(TAG, message);
	}

	BaseClient() {
		setConnection();
	}

	public HttpResponse exec(Request rc) throws IOException, ApiException {
		HttpRequestBase request = null;
		if (rc.isPostMethod()) {
			request = new HttpPost(rc.getURL());
			if (rc.getEntity() != null) {
				((HttpPost) request).setEntity(rc.getEntity());
			}
		} else {
			request = new HttpGet(rc.getURL());
		}
		setHeaders(rc, request);
		setAuthorization(request, rc.getParams());
		if (App.DEBUG) {
			Header[] headers = request.getAllHeaders();
			for (Header header : headers) {
				log("exec() [Header] " + header.getName() + ":"
						+ header.getValue());
			}
			log("exec() Authorization: "
					+ request.getFirstHeader("Authorization").getValue());
		}
		return mHttpClient.execute(request);
	}

	void setConnection() {
		mHttpClient = NetworkHelper.newHttpClient();
	}

	static void setHeaders(Request rc, HttpRequestBase request) {
		List<Header> headers = rc.getHeaders();
		if (headers != null) {
			for (Header header : headers) {
				request.setHeader(header);
			}
		}
		request.addHeader("Accept-Encoding", "gzip, deflate");
	}

	public static void abort(HttpRequestBase request) {
		request.abort();
	}

	abstract void setAuthorization(HttpUriRequest request,
			List<Parameter> params) throws ApiException;

}
