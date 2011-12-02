package com.fanfou.app.auth;

import java.io.IOException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;

import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.EntityUtils;

import android.util.Log;

import com.fanfou.app.App;
import com.fanfou.app.auth.exception.OAuthTokenException;
import com.fanfou.app.http.NetClient;
import com.fanfou.app.http.NetRequest;
import com.fanfou.app.http.Parameter;
import com.fanfou.app.util.Base64;

/**
 * @author mcxiaoke
 * @version 2.0 2011.11.03
 * @version 2.1 2011.11.24
 * @version 2.2 2011.11.28
 * @version 3.0 2011.11.30
 * @version 4.0 2011.12.01
 * @version 4.1 2011.12.02
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
				HttpGet.METHOD_NAME, mOAuthProvider.getAccessTokenURL(),mOAuthProvider,OAuthHelper.getSecretKeySpec(mOAuthProvider));
		NetRequest nr=NetRequest.newBuilder().url(mOAuthProvider.getAccessTokenURL()).header("Authorization",
				authorization).build();
		HttpResponse response = nr.send(NetClient.newInstance());
		int statusCode = response.getStatusLine().getStatusCode();
		String content = EntityUtils.toString(response.getEntity(), HTTP.UTF_8);
		if (App.DEBUG)
			log("requestOAuthAccessToken() code=" + statusCode + " response="
					+ content);
		if (statusCode == 200) {
			return OAuthToken.from(content);
		} else {
			if (App.DEBUG) {
				log("requestOAuthAccessToken content=" + content);
			}
			throw new OAuthTokenException("帐号或密码不正确，登录失败");
		}
	}
}
