package org.mcxiaoke.fancooker.auth;

import java.io.IOException;
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

import org.apache.http.HttpInetConnection;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.message.BasicHeader;
import org.mcxiaoke.fancooker.AppContext;
import org.mcxiaoke.fancooker.auth.exception.AuthException;
import org.mcxiaoke.fancooker.http.HttpEngine;
import org.mcxiaoke.fancooker.http.NetHelper;
import org.mcxiaoke.fancooker.http.Parameter;
import org.mcxiaoke.fancooker.http.RestClient;
import org.mcxiaoke.fancooker.http.RestRequest;
import org.mcxiaoke.fancooker.http.RestResponse;
import org.mcxiaoke.fancooker.util.Base64;

import android.util.Log;

import com.google.api.client.http.GenericUrl;
import com.google.api.client.http.HttpExecuteInterceptor;
import com.google.api.client.http.HttpHeaders;
import com.google.api.client.http.HttpRequest;
import com.google.api.client.http.HttpRequestFactory;
import com.google.api.client.http.HttpRequestInitializer;
import com.google.api.client.http.HttpResponse;
import com.google.api.client.http.HttpTransport;

/**
 * @author mcxiaoke
 * @version 2.0 2011.11.03
 * @version 2.1 2011.11.24
 * @version 2.2 2011.11.28
 * @version 3.0 2011.11.30
 * @version 4.0 2011.12.01
 * @version 4.1 2011.12.07
 * @version 5.0 2012.02.17
 * @version 6.0 2012.02.20
 * @version 6.1 2012.02.23
 * 
 */
public class OAuthService {
	private static final String TAG = OAuthService.class.getSimpleName();

	private final OAuthProvider mOAuthProvider;
	private AccessToken mAccessToken;

	public OAuthService(OAuthProvider provider) {
		mOAuthProvider = provider;
	}

	public OAuthService(OAuthProvider provider, AccessToken token) {
		this.mOAuthProvider = provider;
		this.mAccessToken = token;
	}

	public void setAccessToken(AccessToken token) {
		this.mAccessToken = token;
		if (AppContext.DEBUG) {
			Log.d(TAG, "setAccessToken() AccessToken:  " + token);
		}
	}

	public void authorize(HttpUriRequest request, List<Parameter> params) {
		String authorization = buildOAuthHeader(request.getMethod(), request
				.getURI().toString(), params, mOAuthProvider, mAccessToken);
		request.addHeader(new BasicHeader("Authorization", authorization));
		if (AppContext.DEBUG) {
			Log.d(TAG, "authorize() Authorization:  " + authorization);
		}
	}

	public RequestToken getOAuthRequestToken() throws AuthException,
			IOException {
		// 163 callback=null
		// TODO
		// FIXME
		String url = mOAuthProvider.getRequestTokenURL();
		String authorization = buildOAuthHeader(HttpGet.METHOD_NAME, url, null,
				mOAuthProvider, null);
		RestRequest nr = RestRequest.newBuilder().url(url)
				.header("Authorization", authorization).build();
		// NetClient client = new NetClient();
		// HttpResponse response = client.exec(nr);
		RestResponse res = new RestClient().execute(nr, false);

		String content = res.getContent();
		if (AppContext.DEBUG) {
			Log.d(TAG, "getOAuthRequestToken() code=" + res.statusCode
					+ " response=" + content);
		}
		if (res.statusCode == 200) {
			return new RequestToken(content);
		}
		throw new AuthException("登录失败，帐号或密码错误");
	}

	public RequestToken getOAuthRequestToken(String callback)
			throws AuthException, IOException {
		// TODO
		// FIXME
		String url = mOAuthProvider.getRequestTokenURL();
		String authorization = buildOAuthHeader(HttpGet.METHOD_NAME, url, null,
				mOAuthProvider, null);
		RestRequest nr = RestRequest.newBuilder().url(url)
				.param(OAUTH_CALLBACK, callback)
				.header("Authorization", authorization).build();
		// NetClient client = new NetClient();
		// HttpResponse response = client.exec(nr);
		// NetResponse res = new NetResponse(response);

		RestResponse res = new RestClient().execute(nr, false);
		String content = res.getContent();
		if (AppContext.DEBUG) {
			Log.d(TAG, "getOAuthRequestToken() code=" + res.statusCode
					+ " response=" + content);
		}
		if (res.statusCode == 200) {
			return new RequestToken(content);
		}
		throw new AuthException("登录失败，帐号或密码错误");
	}

	public AccessToken getOAuthAccessToken(RequestToken requestToken)
			throws AuthException, IOException {
		// TODO
		// FIXME
		String url = mOAuthProvider.getAccessTokenURL();
		String authorization = buildOAuthHeader(HttpPost.METHOD_NAME, url,
				null, mOAuthProvider, null);
		RestRequest nr = RestRequest.newBuilder().url(url).post()
				.header("Authorization", authorization).build();
		// NetClient client = new NetClient();
		// HttpResponse response = client.exec(nr);
		// NetResponse res = new NetResponse(response);
		RestResponse res = new RestClient().execute(nr, false);
		String content = res.getContent();
		if (AppContext.DEBUG) {
			Log.d(TAG, "getOAuthRequestToken() code=" + res.statusCode
					+ " response=" + content);
		}
		if (res.statusCode == 200) {
			return new AccessToken(content);
		}
		throw new AuthException("登录失败，帐号或密码错误");
	}

	public AccessToken getOAuthAccessToken(RequestToken requestToken,
			String verifier) throws AuthException, IOException {
		// TODO
		// FIXME
		String url = mOAuthProvider.getAccessTokenURL();
		String authorization = buildOAuthHeader(HttpPost.METHOD_NAME, url,
				null, mOAuthProvider, null);
		RestRequest nr = RestRequest.newBuilder().url(url).post()
				.param(OAUTH_VERIFIER, verifier)
				.header("Authorization", authorization).build();
		// NetClient client = new NetClient();
		// HttpResponse response = client.exec(nr);
		// NetResponse res = new NetResponse(response);

		RestResponse res = new RestClient().execute(nr, false);
		String content = res.getContent();
		if (AppContext.DEBUG) {
			Log.d(TAG, "getOAuthRequestToken() code=" + res.statusCode
					+ " response=" + content);
		}
		if (res.statusCode == 200) {
			return new AccessToken(content);
		}
		throw new AuthException("登录失败，帐号或密码错误");
	}

	public AccessToken getOAuthAccessToken(String username, String password)
			throws AuthException, IOException {
		final String authorization = buildXAuthHeader(username, password,
				mOAuthProvider, false);
		HttpTransport httpTransport = HttpEngine.getInstance()
				.getHttpTransport();
		HttpRequestInitializer httpRequestInitializer = new HttpRequestInitializer() {
			@Override
			public void initialize(HttpRequest request) throws IOException {
				request.getHeaders().setAuthorization(authorization);
			}
		};
		HttpRequestFactory factory = httpTransport
				.createRequestFactory(httpRequestInitializer);
		HttpRequest request = factory.buildGetRequest(new GenericUrl(
				mOAuthProvider.getAccessTokenURL()));

		HttpResponse response = request.execute();
		String content = response.parseAsString();
		int statusCode = response.getStatusCode();
		if (AppContext.DEBUG) {
			Log.d(TAG, "getOAuthAccessToken() code=" + statusCode
					+ " response=" + content);
		}
		if (statusCode == 200) {
			return new AccessToken(content);
		}
		throw new AuthException("登录失败，帐号或密码错误");
	}

	public static final String OAUTH_VERSION1 = "1.0";
	public static final String OAUTH_CALLBACK = "oauth_callback";
	public static final String OAUTH_VERIFIER = "oauth_verifier";
	public static final String HMAC_SHA1 = "HmacSHA1";
	public final static String KEY_SUFFIX = "FE0687E249EBF374";
	public static final Parameter OAUTH_SIGNATURE_METHOD = new Parameter(
			"oauth_signature_method", "HMAC-SHA1");

	public final static Random RAND = new Random();

	static long createNonce() {
		return System.currentTimeMillis() / 1000 + RAND.nextInt();
	}

	static long createTimestamp() {
		return System.currentTimeMillis() / 1000;
	}

	static String buildOAuthHeader(String method, String url,
			List<Parameter> params, OAuthProvider provider, AccessToken otoken) {
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

		String encodedUrl = encode(constructRequestURL(url));

		String encodedParams = encode(alignParams(signatureBaseParams));

		StringBuffer base = new StringBuffer(method).append("&")
				.append(encodedUrl).append("&").append(encodedParams);
		String oauthBaseString = base.toString();

		if (AppContext.DEBUG) {
			Log.d(TAG, "getOAuthHeader() url=" + url);
			Log.d(TAG, "getOAuthHeader() encodedUrl=" + encodedUrl);
			Log.d(TAG, "getOAuthHeader() encodedParams=" + encodedParams);
			Log.d(TAG, "getOAuthHeader() baseString=" + oauthBaseString);
		}
		SecretKeySpec spec = getSecretKeySpec(provider, otoken);
		oauthHeaderParams.add(new Parameter("oauth_signature", getSignature(
				oauthBaseString, spec)));
		return "OAuth " + encodeParameters(oauthHeaderParams, ",", true);
	}

	static String buildXAuthHeader(String username, String password,
			OAuthProvider provider, boolean post) {
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
		StringBuffer base = new StringBuffer(post ? HttpPost.METHOD_NAME
				: HttpGet.METHOD_NAME)
				.append("&")
				.append(encode(constructRequestURL(provider.getAccessTokenURL())))
				.append("&");
		base.append(encode(alignParams(oauthHeaderParams)));
		String oauthBaseString = base.toString();
		SecretKeySpec spec = getSecretKeySpec(provider, null);
		String signature = getSignature(oauthBaseString, spec);
		oauthHeaderParams.add(new Parameter("oauth_signature", signature));
		return "OAuth " + encodeParameters(oauthHeaderParams, ",", true);
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

	public static String constructRequestURL(String url) {
		int index = url.indexOf("?");
		if (-1 != index) {
			url = url.substring(0, index);
		}
		int slashIndex = url.indexOf("/", 8);
		String baseURL = url.substring(0, slashIndex).toLowerCase();
		int colonIndex = baseURL.indexOf(":", 8);
		if (-1 != colonIndex) {
			// url contains port number
			if (baseURL.startsWith("http://") && baseURL.endsWith(":80")) {
				// http default port 80 MUST be excluded
				baseURL = baseURL.substring(0, colonIndex);
			} else if (baseURL.startsWith("https://")
					&& baseURL.endsWith(":443")) {
				// http default port 443 MUST be excluded
				baseURL = baseURL.substring(0, colonIndex);
			}
		}
		url = baseURL + url.substring(slashIndex);

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

	static SecretKeySpec getSecretKeySpec(OAuthProvider provider,
			OAuthToken token) {
		if (null == token) {
			String oauthSignature = encode(provider.getConsumerSercret()) + "&";
			return new SecretKeySpec(oauthSignature.getBytes(), HMAC_SHA1);
		} else {
			String oauthSignature = encode(provider.getConsumerSercret()) + "&"
					+ encode(token.getTokenSecret());
			return new SecretKeySpec(oauthSignature.getBytes(), HMAC_SHA1);
		}
	}

	static SecretKeySpec getSecretKeySpec(OAuthProvider provider) {
		String oauthSignature = encode(provider.getConsumerSercret()) + "&";
		return new SecretKeySpec(oauthSignature.getBytes(), HMAC_SHA1);
	}

}
