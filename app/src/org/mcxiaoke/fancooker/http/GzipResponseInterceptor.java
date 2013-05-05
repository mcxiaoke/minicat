package org.mcxiaoke.fancooker.http;

import java.io.IOException;

import org.apache.http.Header;
import org.apache.http.HeaderElement;
import org.apache.http.HttpEntity;
import org.apache.http.HttpException;
import org.apache.http.HttpResponse;
import org.apache.http.HttpResponseInterceptor;
import org.apache.http.protocol.HttpContext;
import org.mcxiaoke.fancooker.http.support.GzipDecompressingEntity;

/**
 * @author mcxiaoke
 * @version 2.0 2011.11.03
 * 
 */
public class GzipResponseInterceptor implements HttpResponseInterceptor {
	private static final String ENCODING_GZIP = "gzip";

	@Override
	public void process(HttpResponse response, HttpContext context)
			throws HttpException, IOException {
		final HttpEntity entity = response.getEntity();
		final Header encoding = entity.getContentEncoding();
		if (encoding != null) {
			for (HeaderElement element : encoding.getElements()) {
				if (element.getName().equalsIgnoreCase(ENCODING_GZIP)) {
					response.setEntity(new GzipDecompressingEntity(response
							.getEntity()));
					break;
				}
			}
		}
	}

}
