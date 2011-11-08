package com.fanfou.app.http;

import java.io.IOException;

import org.apache.http.HttpException;
import org.apache.http.HttpRequest;
import org.apache.http.HttpRequestInterceptor;
import org.apache.http.protocol.HttpContext;

/**
 * @author mcxiaoke
 * @version 1.0 2011.11.03
 * 
 */
class GzipRequestInterceptor implements HttpRequestInterceptor {
	private static final String HEADER_ACCEPT_ENCODING = "Accept-Encoding";
	private static final String ENCODING_GZIP = "gzip";

	@Override
	public void process(HttpRequest request, HttpContext context)
			throws HttpException, IOException {
		if (!request.containsHeader(HEADER_ACCEPT_ENCODING)) {
			request.addHeader(HEADER_ACCEPT_ENCODING, ENCODING_GZIP);
		}
	}

}
