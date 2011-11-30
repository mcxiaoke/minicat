package com.fanfou.app.auth;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;

import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.message.BasicHeader;

import android.util.Log;

import com.fanfou.app.App;
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
public class OAuthService implements OAuthSupport {

	private static final String HMAC_SHA1 = "HmacSHA1";
	private static final Parameter OAUTH_SIGNATURE_METHOD = new Parameter(
			"oauth_signature_method", "HMAC-SHA1");

	private static final String TAG = OAuthService.class.getSimpleName();

	private void log(String message) {
		Log.d(TAG, message);
	}

	private OAuthProvider mOAuthProvider;
	private OAuthToken mOAuthToken;
	private SecretKeySpec mSecretKeySpec;

	public OAuthService(OAuthProvider provider) {
		mOAuthProvider = provider;
	}

	public OAuthService(OAuthToken token) {
		setOAuthAccessToken(token);
	}

	public OAuthService(OAuthProvider provider, String token, String tokenSecret) {
		this.mOAuthProvider = provider;
		setOAuthAccessToken(new OAuthToken(token, tokenSecret));
	}

	public OAuthService(OAuthProvider provider, OAuthToken token) {
		this.mOAuthProvider = provider;
		setOAuthAccessToken(token);
	}

	@Override
	public void signRequest(HttpUriRequest request, List<Parameter> params) {
		String authorization = buildOAuthHeader(request.getMethod(), request
				.getURI().toString(), params, mOAuthToken);
		request.addHeader(new BasicHeader("Authorization", authorization));
	}

	public void setOAuthAccessToken(OAuthToken token) {
		this.mOAuthToken = token;
		setSecretKeySpec(token);
	}

	private void parseGetParams(String url, List<Parameter> signatureBaseParams) {
		int queryStart = url.indexOf("?");
		if (-1 != queryStart) {
			String[] queryStrs = url.substring(queryStart + 1).split("&");
			try {
				for (String query : queryStrs) {
					String[] split = query.split("=");
					if (split.length == 2) {
						signatureBaseParams.add(new Parameter(URLDecoder
								.decode(split[0], "UTF-8"), URLDecoder.decode(
								split[1], "UTF-8")));
					} else {
						signatureBaseParams.add(new Parameter(URLDecoder
								.decode(split[0], "UTF-8"), ""));
					}
				}
			} catch (UnsupportedEncodingException ignore) {
			}

		}

	}

	private static Random RAND = new Random();

	private String buildOAuthHeader(String method, String url,
			List<Parameter> params, OAuthToken otoken) {
		if (params == null) {
			params = new ArrayList<Parameter>();
		}
		long timestamp = System.currentTimeMillis() / 1000;
		long nonce = timestamp + RAND.nextInt();
		List<Parameter> oauthHeaderParams = new ArrayList<Parameter>();
		oauthHeaderParams.add(new Parameter("oauth_consumer_key",
				mOAuthProvider.getConsumerKey()));
		oauthHeaderParams.add(OAUTH_SIGNATURE_METHOD);
		oauthHeaderParams.add(new Parameter("oauth_timestamp", timestamp));
		oauthHeaderParams.add(new Parameter("oauth_nonce", nonce));
		oauthHeaderParams.add(new Parameter("oauth_version", "1.0"));
		if (null != otoken) {
			// if (App.DEBUG){
			// log("getOAuthHeader() oauth_token="
			// + otoken.getToken() + " oauth_token_secret="
			// + otoken.getTokenSecret());}
			oauthHeaderParams.add(new Parameter("oauth_token", otoken
					.getToken()));
		}
		List<Parameter> signatureBaseParams = new ArrayList<Parameter>(
				oauthHeaderParams.size() + params.size());
		signatureBaseParams.addAll(oauthHeaderParams);
		if (method != HttpGet.METHOD_NAME && params != null
				&& !ConnectionRequest.containsFile(params)) {
			signatureBaseParams.addAll(params);
		}
		parseGetParams(url, signatureBaseParams);

		String encodedUrl = OAuthHelper.encode(OAuthHelper
				.constructRequestURL(url));
		String encodedParams = OAuthHelper.encode(OAuthHelper
				.alignParams(signatureBaseParams));

		StringBuffer base = new StringBuffer(method).append("&")
				.append(encodedUrl).append("&").append(encodedParams);
		String oauthBaseString = base.toString();

		if (App.DEBUG) {
			log("getOAuthHeader() url=" + url);
			log("getOAuthHeader() encodedUrl=" + encodedUrl);
			log("getOAuthHeader() encodedParams=" + encodedParams);
			log("getOAuthHeader() baseString=" + oauthBaseString);
		}
		oauthHeaderParams.add(new Parameter("oauth_signature", getSignature(
				oauthBaseString, otoken)));
		return "OAuth "
				+ OAuthHelper.encodeParameters(oauthHeaderParams, ",", true);
	}

	List<Parameter> buildSignatureParams(String method, String url) {
		long timestamp = System.currentTimeMillis() / 1000;
		long nonce = System.nanoTime() + RAND.nextInt();

		List<Parameter> oauthHeaderParams = new ArrayList<Parameter>(5);
		oauthHeaderParams.add(new Parameter("oauth_consumer_key",
				mOAuthProvider.getConsumerKey()));
		oauthHeaderParams.add(OAUTH_SIGNATURE_METHOD);
		oauthHeaderParams.add(new Parameter("oauth_timestamp", timestamp));
		oauthHeaderParams.add(new Parameter("oauth_nonce", nonce));
		oauthHeaderParams.add(new Parameter("oauth_version", "1.0"));
		if (null != mOAuthToken) {
			oauthHeaderParams.add(new Parameter("oauth_token", mOAuthToken
					.getToken()));
		}

		List<Parameter> signatureBaseParams = new ArrayList<Parameter>(
				oauthHeaderParams.size());
		signatureBaseParams.addAll(oauthHeaderParams);

		// log("=========createOAuthSignatureParams========");
		// if (App.DEBUG) {
		// for (Parameter parameter : signatureBaseParams) {
		// log(parameter.toString());
		// }
		// }

		parseGetParams(url, signatureBaseParams);

		StringBuffer base = new StringBuffer(method)
				.append("&")
				.append(OAuthHelper.encode(OAuthHelper.constructRequestURL(url)))
				.append("&");
		base.append(OAuthHelper.encode(OAuthHelper
				.alignParams(signatureBaseParams)));

		String oauthBaseString = base.toString();
		String signature = getSignature(oauthBaseString, mOAuthToken);

		oauthHeaderParams.add(new Parameter("oauth_signature", signature));

		return oauthHeaderParams;
	}

	private void setSecretKeySpec(OAuthToken token) {
		if (null == token) {
			String oauthSignature = OAuthHelper.encode(mOAuthProvider
					.getConsumerSercret()) + "&";
			mSecretKeySpec = new SecretKeySpec(oauthSignature.getBytes(),
					HMAC_SHA1);
		} else {
			String oauthSignature = OAuthHelper.encode(mOAuthProvider
					.getConsumerSercret())
					+ "&"
					+ ConnectionRequest.encode(token.getTokenSecret());
			mSecretKeySpec = new SecretKeySpec(oauthSignature.getBytes(),
					HMAC_SHA1);
		}
	}

	private String getSignature(String data, OAuthToken token) {
		byte[] byteHMAC = null;
		try {
			// setSecretKeySpec(token);
			Mac mac = Mac.getInstance(HMAC_SHA1);
			mac.init(mSecretKeySpec);
			byteHMAC = mac.doFinal(data.getBytes());
		} catch (InvalidKeyException ike) {
			throw new AssertionError(ike);
		} catch (NoSuchAlgorithmException nsae) {
			throw new AssertionError(nsae);
		}
		return Base64.encodeBytes(byteHMAC);
	}

	@Override
	public boolean equals(Object o) {
		if (this == o)
			return true;
		return false;
	}

	@Override
	public int hashCode() {
		return mOAuthProvider.getConsumerKey().hashCode();
	}

	@Override
	public String toString() {
		return "OAuth { " + ", oauthToken=" + mOAuthToken + '}';
	}
}
