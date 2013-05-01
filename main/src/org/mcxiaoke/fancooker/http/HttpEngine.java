/**
 * 
 */
package org.mcxiaoke.fancooker.http;

import com.google.api.client.extensions.android.http.AndroidHttp;
import com.google.api.client.http.HttpTransport;

/**
 * @author mcxiaoke
 * 
 */
public final class HttpEngine {
	private static HttpEngine sInstance = new HttpEngine();
	private HttpTransport httpTransport;

	private HttpEngine() {
		this.httpTransport = AndroidHttp.newCompatibleTransport();
	}

	public static HttpEngine getInstance() {
		return sInstance;
	}

	public HttpTransport getHttpTransport() {
		return httpTransport;
	}

}
