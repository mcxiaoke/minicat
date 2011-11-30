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
import com.fanfou.app.http.ConnectionManager;
import com.fanfou.app.http.ConnectionRequest;
import com.fanfou.app.http.Parameter;
import com.fanfou.app.util.Base64;

/**
 * @author mcxiaoke
 * @version 2.0 2011.11.03
 * @version 2.1 2011.11.24
 * @version 2.2 2011.11.28
 * @version 3.0 2011.11.30
 * 
 */
public class XAuthService {
	private static final String TAG = XAuthService.class.getSimpleName();
	private static final Random RAND = new Random();

	private void log(String message) {
		Log.d(TAG, message);
	}

	private OAuthProvider mOAuthProvider;

	public XAuthService(OAuthProvider provider) {
		mOAuthProvider = provider;
	}

	public OAuthToken requestOAuthAccessToken(String username, String password)
			throws OAuthTokenException, IOException {
		String authorization = getXAuthHeader(username, password,
				HttpGet.METHOD_NAME, mOAuthProvider.getAccessTokenURL());
		ConnectionRequest.Builder builder = new ConnectionRequest.Builder();
		builder.url(mOAuthProvider.getAccessTokenURL()).header("Authorization",
				authorization);
		HttpResponse response = ConnectionManager.newInstance().exec(
				builder.build());
		int statusCode = response.getStatusLine().getStatusCode();
		String content = EntityUtils.toString(response.getEntity(), HTTP.UTF_8);
		if (App.DEBUG)
			log("getOAuthAccessToken() code=" + statusCode + " response="
					+ content);
		if (statusCode == 200) {
			return OAuthToken.from(content);
		} else {
			if (App.DEBUG) {
				log("getOAuthAccessToken content=" + content);
			}
			throw new OAuthTokenException("帐号或密码不正确，登录失败");
		}
	}

	private String getXAuthHeader(String username, String password,
			String method, String url) {
		long timestamp = System.currentTimeMillis() / 1000;
		long nonce = System.nanoTime() + RAND.nextInt();
		List<Parameter> oauthHeaderParams = new ArrayList<Parameter>();
		oauthHeaderParams.add(new Parameter("oauth_consumer_key",
				mOAuthProvider.getConsumerKey()));
		oauthHeaderParams.add(new Parameter("oauth_signature_method", "HMAC-SHA1"));
		oauthHeaderParams.add(new Parameter("oauth_timestamp", timestamp));
		oauthHeaderParams.add(new Parameter("oauth_nonce", nonce));
		oauthHeaderParams.add(new Parameter("oauth_version", "1.0"));
		oauthHeaderParams.add(new Parameter("x_auth_username", username));
		oauthHeaderParams.add(new Parameter("x_auth_password", password));
		oauthHeaderParams.add(new Parameter("x_auth_mode", "client_auth"));
		StringBuffer base = new StringBuffer(method)
				.append("&")
				.append(OAuthHelper.encode(OAuthHelper.constructRequestURL(url)))
				.append("&");
		base.append(OAuthHelper.encode(OAuthHelper
				.alignParams(oauthHeaderParams)));
		String oauthBaseString = base.toString();
		String signature = getSignature(oauthBaseString);
		oauthHeaderParams.add(new Parameter("oauth_signature", signature));
		return "OAuth "
				+ OAuthHelper.encodeParameters(oauthHeaderParams, ",", true);
	}

	

	String getSignature(String data) {
		byte[] byteHMAC = null;
		try {
			Mac mac = Mac.getInstance("HmacSHA1");
			String oauthSignature = OAuthHelper.encode(mOAuthProvider
					.getConsumerSercret()) + "&";
			SecretKeySpec spec = new SecretKeySpec(oauthSignature.getBytes(),
					"HmacSHA1");

			mac.init(spec);
			byteHMAC = mac.doFinal(data.getBytes());
		} catch (InvalidKeyException ike) {
			throw new AssertionError(ike);
		} catch (NoSuchAlgorithmException nsae) {
			throw new AssertionError(nsae);
		}
		return Base64.encodeBytes(byteHMAC);
	}
}
