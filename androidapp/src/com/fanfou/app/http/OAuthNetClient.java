package com.fanfou.app.http;

import java.io.IOException;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpUriRequest;

import android.text.TextUtils;
import android.util.Log;

import com.fanfou.app.App;
import com.fanfou.app.auth.OAuthService;

/**
 * @author mcxiaoke
 * @version 1.0 2011.12.01
 * @version 1.1 2011.12.05
 * @version 1.2 2011.12.06
 * @version 2.0 2011.12.12
 * 
 */
public class OAuthNetClient {
	private static final String TAG = OAuthNetClient.class.getSimpleName();
	private OAuthService mOAuth;
	private HttpClient mClient;
	private NetRequest mRequest;

	public OAuthNetClient(OAuthService oauth) {
		super();
		this.mOAuth = oauth;
		this.mClient = NetHelper.newSingleHttpClient();
	}

	public HttpResponse open(NetRequest cr) throws IOException {
		if (TextUtils.isEmpty(cr.url)) {
			throw new IllegalArgumentException(
					"request url must not be empty or null.");
		}
		mRequest = cr;
		final HttpUriRequest request = mRequest.request;
		if (mOAuth != null) {
			mOAuth.signRequest(cr.request, cr.getParams());
		}
		NetHelper.setProxy(mClient);
		if (App.DEBUG) {
			Log.d(TAG, "[Request] " + request.getRequestLine().toString());
		}
		HttpResponse response = mClient.execute(request);
		if (App.DEBUG) {
			Log.d(TAG, "[Response] " + response.getStatusLine().toString());
		}
		return response;
	}

	public void abort() {
		if (mRequest != null) {
			mRequest.abort();
			close();
		}
	}

	private void close() {
		mClient.getConnectionManager().shutdown();
	}

}
