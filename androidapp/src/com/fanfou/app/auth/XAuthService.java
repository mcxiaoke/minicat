package com.fanfou.app.auth;

import java.io.IOException;

import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.EntityUtils;

import android.util.Log;

import com.fanfou.app.App;
import com.fanfou.app.api.Parser;
import com.fanfou.app.auth.exception.OAuthTokenException;
import com.fanfou.app.http.NetClient;
import com.fanfou.app.http.NetRequest;

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
		int statusCode = response.getStatusLine().getStatusCode();
		String content = EntityUtils.toString(response.getEntity(), HTTP.UTF_8);
		if (App.DEBUG) {
			log("requestOAuthAccessToken() code=" + statusCode + " response="
					+ content);
		}
		if (statusCode == 200) {
			return OAuthToken.from(content);
		}
//		throw new OAuthTokenException(Parser.error(content));
		throw new OAuthTokenException("登录失败，帐号或密码错误");
	}
}
