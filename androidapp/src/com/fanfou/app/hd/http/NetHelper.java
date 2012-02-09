package com.fanfou.app.hd.http;

import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.nio.charset.Charset;
import java.util.List;
import java.util.zip.GZIPInputStream;

import org.apache.http.Header;
import org.apache.http.HeaderElement;
import org.apache.http.HttpEntity;
import org.apache.http.HttpHost;
import org.apache.http.HttpRequest;
import org.apache.http.HttpRequestInterceptor;
import org.apache.http.HttpResponse;
import org.apache.http.HttpResponseInterceptor;
import org.apache.http.HttpVersion;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.conn.ClientConnectionManager;
import org.apache.http.conn.params.ConnManagerParams;
import org.apache.http.conn.params.ConnPerRouteBean;
import org.apache.http.conn.params.ConnRoutePNames;
import org.apache.http.conn.scheme.PlainSocketFactory;
import org.apache.http.conn.scheme.Scheme;
import org.apache.http.conn.scheme.SchemeRegistry;
import org.apache.http.conn.ssl.SSLSocketFactory;
import org.apache.http.entity.HttpEntityWrapper;
import org.apache.http.entity.mime.MultipartEntity;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.entity.mime.content.StringBody;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.conn.SingleClientConnManager;
import org.apache.http.impl.conn.tsccm.ThreadSafeClientConnManager;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.apache.http.params.HttpProtocolParams;
import org.apache.http.protocol.HTTP;
import org.apache.http.protocol.HttpContext;

import android.util.Log;

import com.fanfou.app.hd.App;
import com.fanfou.app.hd.App.ApnType;
import com.fanfou.app.hd.util.Utils;

/**
 * @author mcxiaoke
 * @version 1.0 2011.12.02
 * @version 1.1 2011.12.07
 * @version 1.2 2011.12.12
 * @version 1.3 2011.12.21
 * 
 */
public final class NetHelper {
	private static final String TAG = NetHelper.class.getSimpleName();
	public static final int SOCKET_BUFFER_SIZE = 16 * 1024;
	public static final int CONNECTION_TIMEOUT_MS = 30000;
	public static final int SOCKET_TIMEOUT_MS = 30000;
	public static final int MAX_TOTAL_CONNECTIONS = 20;
	public static final int MAX_RETRY_TIMES = 3;

	public static MultipartEntity encodeMultipart(List<Parameter> params) {
		if (Utils.isEmpty(params)) {
			return null;
		}
		MultipartEntity entity = new MultipartEntity();
		try {
			for (Parameter param : params) {
				if (param.isFile()) {
					entity.addPart(param.getName(),
							new FileBody(param.getFile()));
				} else {
					entity.addPart(
							param.getName(),
							new StringBody(param.getValue(), Charset
									.forName(HTTP.UTF_8)));
				}
			}
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		return entity;
	}

	public static HttpEntity encodeForPost(List<Parameter> params) {
		if (Utils.isEmpty(params)) {
			return null;
		}
		try {
			return new UrlEncodedFormEntity(params, HTTP.UTF_8);
		} catch (UnsupportedEncodingException e) {
		}
		return null;
	}

	public static String encodeForGet(List<Parameter> params) {
		if (Utils.isEmpty(params)) {
			return "";
		}
		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < params.size(); i++) {
			Parameter p = params.get(i);
			if (p.isFile()) {
				throw new IllegalArgumentException("GET参数不能包含文件");
			}
			if (i > 0) {
				sb.append("&");
			}
			sb.append(encode(p.getName())).append("=")
					.append(encode(p.getValue()));
		}

		return sb.toString();
	}

	public static boolean containsFile(List<Parameter> params) {
		if (Utils.isEmpty(params)) {
			return false;
		}
		boolean containsFile = false;
		for (Parameter param : params) {
			if (param.isFile()) {
				containsFile = true;
				break;
			}
		}
		return containsFile;
	}

	public static String encode(String input) {
		try {
			return URLEncoder.encode(input, HTTP.UTF_8);
		} catch (UnsupportedEncodingException e) {
		}
		return input;
	}

	public final static void setHeaders(HttpRequestBase request,
			List<Header> headers) {
		if (headers != null) {
			for (Header header : headers) {
				request.addHeader(header);
			}
		}
	}

	public static boolean containsFile(Parameter[] params) {
		boolean containsFile = false;
		if (null == params) {
			return false;
		}
		for (Parameter param : params) {
			if (param.isFile()) {
				containsFile = true;
				break;
			}
		}
		return containsFile;
	}

	public final static DefaultHttpClient newSingleHttpClient() {
		HttpParams params = new BasicHttpParams();
		HttpProtocolParams.setVersion(params, HttpVersion.HTTP_1_1);
		HttpProtocolParams.setContentCharset(params, HTTP.UTF_8);
		HttpConnectionParams
				.setConnectionTimeout(params, CONNECTION_TIMEOUT_MS);
		HttpConnectionParams.setSoTimeout(params, SOCKET_TIMEOUT_MS);
		HttpConnectionParams.setTcpNoDelay(params, true);
		HttpProtocolParams.setUserAgent(params,
				"FanFou for Android(com.fanfou.app)/" + App.appVersionName);

		SchemeRegistry schReg = new SchemeRegistry();
		schReg.register(new Scheme("http", PlainSocketFactory
				.getSocketFactory(), 80));
		schReg.register(new Scheme("https",
				SSLSocketFactory.getSocketFactory(), 443));
		SingleClientConnManager manager = new SingleClientConnManager(params,
				schReg);
		DefaultHttpClient client = new DefaultHttpClient(manager, params);
		client.addRequestInterceptor(new GzipRequestInterceptor());
		client.addResponseInterceptor(new GzipResponseInterceptor());
		client.setHttpRequestRetryHandler(new RequestRetryHandler(
				MAX_RETRY_TIMES));
		return client;
	}

	public final static DefaultHttpClient newThreadSafeHttpClient() {
		HttpParams params = new BasicHttpParams();
		HttpProtocolParams.setVersion(params, HttpVersion.HTTP_1_1);
		HttpProtocolParams.setContentCharset(params, HTTP.UTF_8);
		HttpProtocolParams.setUseExpectContinue(params, true);
		HttpConnectionParams
				.setConnectionTimeout(params, CONNECTION_TIMEOUT_MS);
		HttpConnectionParams.setSoTimeout(params, SOCKET_TIMEOUT_MS);
		HttpConnectionParams.setSocketBufferSize(params, SOCKET_BUFFER_SIZE);
		HttpConnectionParams.setTcpNoDelay(params, true);
		HttpProtocolParams.setUserAgent(params, "FanFou for Android/"
				+ App.appVersionName);

		ConnManagerParams.setMaxConnectionsPerRoute(params,
				new ConnPerRouteBean(MAX_TOTAL_CONNECTIONS));
		ConnManagerParams.setMaxTotalConnections(params, MAX_TOTAL_CONNECTIONS);

		SchemeRegistry schReg = new SchemeRegistry();
		schReg.register(new Scheme("http", PlainSocketFactory
				.getSocketFactory(), 80));
		schReg.register(new Scheme("https",
				SSLSocketFactory.getSocketFactory(), 443));
		ClientConnectionManager manager = new ThreadSafeClientConnManager(
				params, schReg);
		DefaultHttpClient client = new DefaultHttpClient(manager, params);
		client.addRequestInterceptor(new GzipRequestInterceptor());
		client.addResponseInterceptor(new GzipResponseInterceptor());
		client.setHttpRequestRetryHandler(new RequestRetryHandler(
				MAX_RETRY_TIMES));

		return client;
	}

	public final static void setProxy(final HttpClient client) {
		if (client == null) {
			return;
		}
		HttpParams params = client.getParams();
		ApnType type = App.getApnType();
		if (type == ApnType.WAP) {
			if (App.DEBUG) {
				Log.d(TAG, "use proxy 10.0.0.172:80");
			}
			params.setParameter(ConnRoutePNames.DEFAULT_PROXY, new HttpHost(
					"10.0.0.172", 80));
		} else {
			params.removeParameter(ConnRoutePNames.DEFAULT_PROXY);
		}
	}

	public static Parameter[] getParameterArray(String name, String value) {
		return new Parameter[] { new Parameter(name, value) };
	}

	public static Parameter[] getParameterArray(String name, int value) {
		return getParameterArray(name, String.valueOf(value));
	}

	public static Parameter[] getParameterArray(String name1, String value1,
			String name2, String value2) {
		return new Parameter[] { new Parameter(name1, value1),
				new Parameter(name2, value2) };
	}

	public static Parameter[] getParameterArray(String name1, int value1,
			String name2, int value2) {
		return getParameterArray(name1, String.valueOf(value1), name2,
				String.valueOf(value2));
	}
	
//	 public static void signOAuth(final String host, final String path, final String method, final boolean https, final Parameters params, final String token, final String tokenSecret) {
//	        params.put(
//	                "oauth_consumer_key", Settings.getKeyConsumerPublic(),
//	                "oauth_nonce", CryptUtils.md5(Long.toString(System.currentTimeMillis())),
//	                "oauth_signature_method", "HMAC-SHA1",
//	                "oauth_timestamp", Long.toString(new Date().getTime() / 1000),
//	                "oauth_token", StringUtils.defaultString(token),
//	                "oauth_version", "1.0");
//	        params.sort();
//
//	        final List<String> paramsEncoded = new ArrayList<String>();
//	        for (final NameValuePair nameValue : params) {
//	            paramsEncoded.add(nameValue.getName() + "=" + cgBase.urlencode_rfc3986(nameValue.getValue()));
//	        }
//
//	        final String keysPacked = Settings.getKeyConsumerSecret() + "&" + StringUtils.defaultString(tokenSecret); // both even if empty some of them!
//	        final String requestPacked = method + "&" + cgBase.urlencode_rfc3986((https ? "https" : "http") + "://" + host + path) + "&" + cgBase.urlencode_rfc3986(StringUtils.join(paramsEncoded.toArray(), '&'));
//	        params.put("oauth_signature", cgBase.base64Encode(CryptUtils.hashHmac(requestPacked, keysPacked)));
//	    }
	
	
    private static final String HEADER_ACCEPT_ENCODING = "Accept-Encoding";
    private static final String ENCODING_GZIP = "gzip";
	
    /**
     * Simple {@link HttpRequestInterceptor} that adds GZIP accept encoding header.
     */
	private static class GZIPHttpRequestInterceptor implements HttpRequestInterceptor {
        public void process(final HttpRequest request, final HttpContext context) {
            // Add header to accept gzip content
            if (!request.containsHeader(HEADER_ACCEPT_ENCODING)) {
                request.addHeader(HEADER_ACCEPT_ENCODING, ENCODING_GZIP);
            }
        }
    }

    /**
     * Simple {@link HttpResponseInterceptor} that inflates response if GZIP encoding header.
     */
	private static class GZIPHttpResponseInterceptor implements HttpResponseInterceptor {
        public void process(final HttpResponse response, final HttpContext context) {
            // Inflate any responses compressed with gzip
            final HttpEntity entity = response.getEntity();
            final Header encoding = entity.getContentEncoding();
            if (encoding != null) {
                for (HeaderElement element : encoding.getElements()) {
                    if (element.getName().equalsIgnoreCase(ENCODING_GZIP)) {
                        response.setEntity(new GZIPInflatingEntity(response.getEntity()));
                        break;
                    }
                }
            }
        }
    }

    /**
     * Simple {@link HttpEntityWrapper} that inflates the wrapped {@link HttpEntity} by passing it
     * through {@link GZIPInputStream}.
     */
    private static class GZIPInflatingEntity extends HttpEntityWrapper {
        public GZIPInflatingEntity(final HttpEntity wrapped) {
            super(wrapped);
        }

        @Override
        public InputStream getContent() throws IOException {
            return new GZIPInputStream(wrappedEntity.getContent());
        }

        @Override
        public long getContentLength() {
            return -1;
        }
    }
}
