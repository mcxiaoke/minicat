package com.fanfou.app.auth;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;

import org.apache.http.client.methods.HttpGet;
import org.apache.http.protocol.HTTP;

import android.util.Log;

import com.fanfou.app.App;
import com.fanfou.app.http.NetHelper;
import com.fanfou.app.http.Parameter;
import com.fanfou.app.util.Base64;

/**
 * @author mcxiaoke
 * @version 1.0 2011.11.30
 * @version 2.0 2011.12.01
 * @version 2.1 2011.12.02
 * @version 2.2 2011.12.07
 * 
 */
public final class OAuthHelper {
	public static final String OAUTH_VERSION1 = "1.0";
	public static final String HMAC_SHA1 = "HmacSHA1";
	public final static String KEY_SUFFIX = "FE0687E249EBF374";
	public static final Parameter OAUTH_SIGNATURE_METHOD = new Parameter(
			"oauth_signature_method", "HMAC-SHA1");

	private static final String TAG = OAuthHelper.class.getSimpleName();

	public final static Random RAND = new Random();

	static long createNonce() {
		return System.currentTimeMillis() / 1000 + RAND.nextInt();
	}

	static long createTimestamp() {
		return System.currentTimeMillis() / 1000;
	}

	static String buildOAuthHeader(String method, String url,
			List<Parameter> params, OAuthProvider provider, OAuthToken otoken) {
		if (params == null) {
			params = new ArrayList<Parameter>();
		}
		long timestamp = System.currentTimeMillis() / 1000;
		long nonce = timestamp + RAND.nextInt();
		List<Parameter> oauthHeaderParams = new ArrayList<Parameter>();
		oauthHeaderParams.add(new Parameter("oauth_consumer_key", provider
				.getConsumerKey()));
		oauthHeaderParams.add(OAUTH_SIGNATURE_METHOD);
		oauthHeaderParams.add(new Parameter("oauth_timestamp", timestamp));
		oauthHeaderParams.add(new Parameter("oauth_nonce", nonce));
		oauthHeaderParams.add(new Parameter("oauth_version", OAUTH_VERSION1));
		if (null != otoken) {
			oauthHeaderParams.add(new Parameter("oauth_token", otoken
					.getToken()));
		}
		List<Parameter> signatureBaseParams = new ArrayList<Parameter>(
				oauthHeaderParams.size() + params.size());
		signatureBaseParams.addAll(oauthHeaderParams);
		if (method != HttpGet.METHOD_NAME && params != null
				&& !NetHelper.containsFile(params)) {
			signatureBaseParams.addAll(params);
		}
		parseGetParams(url, signatureBaseParams);

		String encodedUrl = OAuthHelper.encode(
				OAuthHelper.constructRequestURL(url));

		String encodedParams = OAuthHelper.encode(
				OAuthHelper.alignParams(signatureBaseParams));

		StringBuffer base = new StringBuffer(method).append("&")
				.append(encodedUrl).append("&").append(encodedParams);
		String oauthBaseString = base.toString();

		if (App.DEBUG) {
			Log.d(TAG, "getOAuthHeader() url=" + url);
			Log.d(TAG, "getOAuthHeader() encodedUrl=" + encodedUrl);
			Log.d(TAG, "getOAuthHeader() encodedParams=" + encodedParams);
			Log.d(TAG, "getOAuthHeader() baseString=" + oauthBaseString);
		}
		SecretKeySpec spec = getSecretKeySpec(provider, otoken);
		oauthHeaderParams.add(new Parameter("oauth_signature", OAuthHelper
				.getSignature(oauthBaseString, spec)));
		return "OAuth "
				+ OAuthHelper.encodeParameters(oauthHeaderParams, ",", true);
	}

	static String buildXAuthHeader(String username, String password,
			String method, String url, OAuthProvider provider) {
		long timestamp = System.currentTimeMillis() / 1000;
		long nonce = System.nanoTime() + RAND.nextInt();
		List<Parameter> oauthHeaderParams = new ArrayList<Parameter>();
		oauthHeaderParams.add(new Parameter("oauth_consumer_key", provider
				.getConsumerKey()));
		oauthHeaderParams.add(new Parameter("oauth_signature_method",
				"HMAC-SHA1"));
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
		SecretKeySpec spec = getSecretKeySpec(provider, null);
		String signature = getSignature(oauthBaseString, spec);
		oauthHeaderParams.add(new Parameter("oauth_signature", signature));
		return "OAuth "
				+ OAuthHelper.encodeParameters(oauthHeaderParams, ",", true);
	}

	private static void parseGetParams(String url,
			List<Parameter> signatureBaseParams) {
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

	private static String getSignature(String data, SecretKeySpec spec) {
		byte[] byteHMAC = null;
		try {
			Mac mac = Mac.getInstance(HMAC_SHA1);
			mac.init(spec);
			byteHMAC = mac.doFinal(data.getBytes());
		} catch (InvalidKeyException ike) {
			throw new AssertionError(ike);
		} catch (NoSuchAlgorithmException nsae) {
			throw new AssertionError(nsae);
		}
		return Base64.encodeBytes(byteHMAC);
	}

	private static String alignParams(List<Parameter> params) {
		Collections.sort(params);
		return encodeParameters(params);
	}

	private static String encodeParameters(List<Parameter> httpParams) {
		return encodeParameters(httpParams, "&", false);
	}

	private static String encodeParameters(List<Parameter> httpParams,
			String splitter, boolean quot) {
		StringBuffer buf = new StringBuffer();
		for (Parameter param : httpParams) {
			if (!param.isFile()) {
				if (buf.length() != 0) {
					if (quot) {
						buf.append("\"");
					}
					buf.append(splitter);
				}
				buf.append(encode(param.getName())).append("=");
				if (quot) {
					buf.append("\"");
				}
				buf.append(encode(param.getValue()));
			}
		}
		if (buf.length() != 0) {
			if (quot) {
				buf.append("\"");
			}
		}
		return buf.toString();
	}

	private static String constructRequestURL(String url) {
		int index = url.indexOf("?");
		if (-1 != index) {
			url = url.substring(0, index);
		}
		int slashIndex = url.indexOf("/", 8);

		String baseURL = url.substring(0, slashIndex).toLowerCase();

		// int colonIndex = baseURL.indexOf(":", 8);
		// if (-1 != colonIndex) {
		// if (baseURL.startsWith("http://") && baseURL.endsWith(":80")) {
		// baseURL = baseURL.substring(0, colonIndex);
		// } else if (baseURL.startsWith("https://")
		// && baseURL.endsWith(":443")) {
		// baseURL = baseURL.substring(0, colonIndex);
		// }
		// }

		url = baseURL + url.substring(slashIndex);
		if (App.DEBUG) {
			Log.d(TAG, "constructRequestURL result=" + url);
		}
		return url;
	}

	static String encode(String value) {
		String encoded = null;
		try {
			encoded = URLEncoder.encode(value, "UTF-8");
		} catch (UnsupportedEncodingException ignore) {
		}
		StringBuffer buf = new StringBuffer(encoded.length());
		char focus;
		for (int i = 0; i < encoded.length(); i++) {
			focus = encoded.charAt(i);
			if (focus == '*') {
				buf.append("%2A");
			} else if (focus == '+') {
				buf.append("%20");
			} else if (focus == '%' && (i + 1) < encoded.length()
					&& encoded.charAt(i + 1) == '7'
					&& encoded.charAt(i + 2) == 'E') {
				buf.append('~');
				i += 2;
			} else {
				buf.append(focus);
			}
		}
		return buf.toString();
	}

	static final String HEX_DIGITS = "0123456789ABCDEF";

	private static void convert(String s, StringBuilder buf, String enc) {
		byte[] bytes;
		try {
			bytes = s.getBytes(enc);
		} catch (Exception e) {
			bytes = s.getBytes();
		}
		for (int j = 0; j < bytes.length; j++) {
			buf.append('%');
			buf.append(HEX_DIGITS.charAt((bytes[j] & 0xf0) >> 4));
			buf.append(HEX_DIGITS.charAt(bytes[j] & 0xf));
		}
	}

	static SecretKeySpec getSecretKeySpec(OAuthProvider provider,
			OAuthToken token) {
		if (null == token) {
			String oauthSignature = OAuthHelper.encode(provider
					.getConsumerSercret()) + "&";
			return new SecretKeySpec(oauthSignature.getBytes(),
					OAuthHelper.HMAC_SHA1);
		} else {
			String oauthSignature = OAuthHelper.encode(provider
					.getConsumerSercret())
					+ "&"
					+ OAuthHelper.encode(token.getTokenSecret());
			return new SecretKeySpec(oauthSignature.getBytes(),
					OAuthHelper.HMAC_SHA1);
		}
	}

	static SecretKeySpec getSecretKeySpec(OAuthProvider provider) {
		String oauthSignature = OAuthHelper.encode(provider
				.getConsumerSercret()) + "&";
		return new SecretKeySpec(oauthSignature.getBytes(),
				OAuthHelper.HMAC_SHA1);
	}

}
