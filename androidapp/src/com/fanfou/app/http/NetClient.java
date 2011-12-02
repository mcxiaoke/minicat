package com.fanfou.app.http;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.HttpHost;
import org.apache.http.HttpResponse;
import org.apache.http.HttpVersion;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.client.params.HttpClientParams;
import org.apache.http.conn.ClientConnectionManager;
import org.apache.http.conn.params.ConnManagerParams;
import org.apache.http.conn.params.ConnPerRouteBean;
import org.apache.http.conn.params.ConnRoutePNames;
import org.apache.http.conn.scheme.PlainSocketFactory;
import org.apache.http.conn.scheme.Scheme;
import org.apache.http.conn.scheme.SchemeRegistry;
import org.apache.http.conn.ssl.SSLSocketFactory;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.conn.tsccm.ThreadSafeClientConnManager;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.apache.http.params.HttpProtocolParams;
import org.apache.http.protocol.HTTP;

import android.util.Log;

import com.fanfou.app.App;
import com.fanfou.app.App.ApnType;
import com.fanfou.app.auth.OAuthService;

/**
 * @author mcxiaoke
 * @version 1.0 2011.05.02
 * @version 1.1 2011.05.03
 * @version 1.2 2011.05.03
 * @version 1.3 2011.05.04
 * @version 1.4 2011.05.05
 * @version 1.5 2011.10.25
 * @version 2.0 2011.11.03
 * @version 2.1 2011.11.04
 * @version 3.0 2011.11.09
 * @version 3.1 2011.11.15
 * @version 3.2 2011.11.24
 * @version 3.3 2011.11.28
 * @version 3.4 2011.11.29
 * @version 4.0 2011.12.01
 * @version 4.1 2011.12.02
 * 
 */
public abstract class NetClient {

	private static final String TAG = NetClient.class.getSimpleName();

	private DefaultHttpClient mHttpClient;
	private final static ArrayList<HttpClient> mClients=new ArrayList<HttpClient>();

	public static final NetClient newInstance() {
		return new SimpleNetClient();
	}

	private final void log(String message) {
		Log.d(TAG, message);
	}

	protected NetClient() {
		prepareHttpClient();
	}
	
	private void prepareHttpClient(){
		mHttpClient=NetHelper.newHttpClient();
		mClients.add(mHttpClient);
	}

	public final HttpResponse get(String url) throws IOException {
//		NetRequest nr=NetRequest.newBuilder().url(url).build();
//		return executeImpl(nr.request);
		return executeImpl(new HttpGet(url));
	}

	public final HttpResponse post(String url, List<Parameter> params)
			throws IOException {
		NetRequest nr=NetRequest.newBuilder().url(url).params(params).post().build();
		return executeImpl(nr.request);
	}

	protected final HttpResponse exec(NetRequest cr) throws IOException{
		signRequest(cr);
		return executeImpl(cr.request);
	}
	
	protected abstract void signRequest(NetRequest cr);

	private final HttpResponse executeImpl(HttpRequestBase request)
			throws IOException {
		NetHelper.setProxy(mHttpClient);
		if (App.DEBUG) {
			log("==========[Request]==========");
			log(request.getRequestLine().toString());
//			Header[] headers = request.getAllHeaders();
//			for (Header header : headers) {
//				log(header.getName() + ":"
//						+ header.getValue());
//			}
		}
		HttpResponse response = mHttpClient.execute(request);
		if (App.DEBUG) {
			log("==========[Response]==========");
			log(response.getStatusLine().toString());
//			Header[] headers = response.getAllHeaders();
//			for (Header header : headers) {
//				log(header.getName() + ":"
//						+ header.getValue());
//			}
//			log("\n");
		}
		return response;
	}


}
