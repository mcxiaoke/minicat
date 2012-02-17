package com.fanfou.app.hd.auth;

import java.io.IOException;
import java.util.List;

import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.message.BasicHeader;

import android.util.Log;

import com.fanfou.app.hd.App;
import com.fanfou.app.hd.auth.exception.OAuthTokenException;
import com.fanfou.app.hd.http.NetClient;
import com.fanfou.app.hd.http.NetRequest;
import com.fanfou.app.hd.http.NetResponse;
import com.fanfou.app.hd.http.Parameter;

/**
 * @author mcxiaoke
 * @version 2.0 2011.11.03
 * @version 2.1 2011.11.24
 * @version 2.2 2011.11.28
 * @version 3.0 2011.11.30
 * @version 4.0 2011.12.01
 * @version 4.1 2011.12.07
 * @version 5.0 2012.02.17
 * 
 */
public class OAuthService {
	private static final String TAG = OAuthService.class.getSimpleName();

	private OAuthProvider mOAuthProvider;
	private OAuthToken mAccessToken;

	public OAuthService(OAuthProvider provider) {
		mOAuthProvider = provider;
	}

	public OAuthService(OAuthProvider provider, OAuthToken token) {
		this.mOAuthProvider = provider;
		this.mAccessToken = token;
	}

	public void setOAuthToken(OAuthToken token) {
		this.mAccessToken = token;
	}

	public void signRequest(HttpUriRequest request, List<Parameter> params) {
		String authorization = OAuthHelper.buildOAuthHeader(
				request.getMethod(), request.getURI().toString(), params,
				mOAuthProvider, mAccessToken);
		request.addHeader(new BasicHeader("Authorization", authorization));
	}

	public RequestToken getOAuthRequestToken() throws OAuthTokenException, IOException {
		// 163 callback=null
		// TODO
		// FIXME
		String url = mOAuthProvider.getRequestTokenURL();
		String authorization = OAuthHelper.buildOAuthHeader(
				HttpGet.METHOD_NAME, url, null, mOAuthProvider, null);
		NetRequest nr = NetRequest.newBuilder().url(url)
				.header("Authorization", authorization).build();
		NetClient client = new NetClient();
		HttpResponse response = client.exec(nr);
		NetResponse res = new NetResponse(response);
		String content = res.getContent();
		if (App.DEBUG) {
			Log.d(TAG, "getOAuthRequestToken() code=" + res.statusCode
					+ " response=" + content);
		}
		if (res.statusCode == 200) {
			return new RequestToken(content);
		}
		throw new OAuthTokenException("登录失败，帐号或密码错误");
	}

	public RequestToken getOAuthRequestToken(String callback)
			throws OAuthTokenException, IOException {
		// TODO
		// FIXME
		String url = mOAuthProvider.getRequestTokenURL();
		String authorization = OAuthHelper.buildOAuthHeader(
				HttpGet.METHOD_NAME, url, null, mOAuthProvider, null);
		NetRequest nr = NetRequest.newBuilder().url(url)
				.param(OAuthHelper.OAUTH_CALLBACK, callback)
				.header("Authorization", authorization).build();
		NetClient client = new NetClient();
		HttpResponse response = client.exec(nr);
		NetResponse res = new NetResponse(response);
		String content = res.getContent();
		if (App.DEBUG) {
			Log.d(TAG, "getOAuthRequestToken() code=" + res.statusCode
					+ " response=" + content);
		}
		if (res.statusCode == 200) {
			return new RequestToken(content);
		}
		throw new OAuthTokenException("登录失败，帐号或密码错误");
	}

	public AccessToken getOAuthAccessToken(RequestToken requestToken)
			throws OAuthTokenException, IOException {
		// TODO
		// FIXME
		String url = mOAuthProvider.getAccessTokenURL();
		String authorization = OAuthHelper.buildOAuthHeader(
				HttpPost.METHOD_NAME, url, null, mOAuthProvider, null);
		NetRequest nr = NetRequest.newBuilder().url(url).post()
				.header("Authorization", authorization).build();
		NetClient client = new NetClient();
		HttpResponse response = client.exec(nr);
		NetResponse res = new NetResponse(response);
		String content = res.getContent();
		if (App.DEBUG) {
			Log.d(TAG, "getOAuthRequestToken() code=" + res.statusCode
					+ " response=" + content);
		}
		if (res.statusCode == 200) {
			return new AccessToken(content);
		}
		throw new OAuthTokenException("登录失败，帐号或密码错误");
	}

	public AccessToken getOAuthAccessToken(RequestToken requestToken,
			String verifier) throws OAuthTokenException, IOException {
		// TODO
		// FIXME
		String url = mOAuthProvider.getAccessTokenURL();
		String authorization = OAuthHelper.buildOAuthHeader(
				HttpPost.METHOD_NAME, url, null, mOAuthProvider, null);
		NetRequest nr = NetRequest.newBuilder().url(url).post()
				.param(OAuthHelper.OAUTH_VERIFIER, verifier)
				.header("Authorization", authorization).build();
		NetClient client = new NetClient();
		HttpResponse response = client.exec(nr);
		NetResponse res = new NetResponse(response);
		String content = res.getContent();
		if (App.DEBUG) {
			Log.d(TAG, "getOAuthRequestToken() code=" + res.statusCode
					+ " response=" + content);
		}
		if (res.statusCode == 200) {
			return new AccessToken(content);
		}
		throw new OAuthTokenException("登录失败，帐号或密码错误");
	}

}
