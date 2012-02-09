package com.fanfou.app.hd.auth;

import java.io.IOException;

import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;

import android.util.Log;

import com.fanfou.app.hd.App;
import com.fanfou.app.hd.auth.exception.OAuthTokenException;
import com.fanfou.app.hd.http.NetClient;
import com.fanfou.app.hd.http.NetRequest;
import com.fanfou.app.hd.http.NetResponse;

/**
 * @author mcxiaoke
 * @version 2.0 2011.11.03
 * @version 2.1 2011.11.24
 * @version 2.2 2011.11.28
 * @version 3.0 2011.11.30
 * @version 4.0 2011.12.01
 * @version 4.1 2011.12.02
 * @version 4.2 2011.12.05
 * @version 4.3 2011.12.07
 * @version 4.4 2011.12.12
 * 
 */
public class XAuthService {
	private static final String TAG = XAuthService.class.getSimpleName();

	private void log(String message) {
		Log.d(TAG, message);
	}

	private OAuthProvider mOAuthProvider;

	public XAuthService(OAuthProvider provider) {
		mOAuthProvider = provider;
	}

	public OAuthToken requestOAuthAccessToken(String username, String password)
			throws OAuthTokenException, IOException {
		String authorization = OAuthHelper.buildXAuthHeader(username, password,
				HttpGet.METHOD_NAME, mOAuthProvider.getAccessTokenURL(),
				mOAuthProvider);
		NetRequest nr = NetRequest.newBuilder()
				.url(mOAuthProvider.getAccessTokenURL())
				.header("Authorization", authorization).build();
		NetClient client = new NetClient();
		HttpResponse response = client.exec(nr);
		NetResponse res = new NetResponse(response);
		String content = res.getContent();
		if (App.DEBUG) {
			log("requestOAuthAccessToken() code=" + res.statusCode
					+ " response=" + content);
		}
		if (res.statusCode == 200) {
			return OAuthToken.from(content);
		}
		// throw new OAuthTokenException(Parser.error(content));
		throw new OAuthTokenException("登录失败，帐号或密码错误");
	}
}
