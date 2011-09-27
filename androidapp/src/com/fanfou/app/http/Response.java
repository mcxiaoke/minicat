package com.fanfou.app.http;

import java.io.IOException;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.StatusLine;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.EntityUtils;

import com.fanfou.app.App;
import com.fanfou.app.api.ApiException;

/**
 * @author mcxiaoke
 * @version 1.0 20110610
 * @version 2.0 20110905
 * 
 */
public class Response implements ResponseCode {

//	private HttpResponse response;
	private HttpEntity entity;
	private String content;
	private boolean used;
	public final StatusLine statusLine;
	public final int statusCode;
//	public final Header[] headers;

	public Response(HttpResponse response) {
//		this.response = response;
		this.entity=response.getEntity();
		this.statusLine=response.getStatusLine();
		this.statusCode = statusLine.getStatusCode();
//		this.headers = response.getAllHeaders();
	}

	public final String getContent() throws ApiException {
		if (content == null) {
//			BufferedReader br = null;
//			InputStream stream = null;
//			ByteArrayOutputStream bas=new ByteArrayOutputStream();
			try {
//				long st = System.currentTimeMillis();
//				entity.writeTo(bas);
//				content=bas.toString(HTTP.UTF_8);
				content=EntityUtils.toString(entity, HTTP.UTF_8);
//				if(App.DEBUG){
//					Utils.logTime("getContent", System.currentTimeMillis() - st);
//				}
//				stream = response.getEntity().getContent();
//				if (null == stream) {
//					return null;
//				}
//				br = new BufferedReader(new InputStreamReader(stream, "UTF-8"));
//				StringBuffer buf = new StringBuffer();
//				String line;
//				while ((line = br.readLine()) != null) {
//					buf.append(line).append("\n");
//				}
//				content = buf.toString();
//				stream.close();
				used = true;
			} catch (IOException e) {
				if(App.DEBUG){
					e.printStackTrace();
				}
				throw new ApiException(ERROR_NOT_CONNECTED, e.getMessage(),
						e);
			} finally {
//				IOHelper.forceClose(bas);
//				IOHelper.forceClose(stream);
//				IOHelper.forceClose(br);
			}
		}
		return content;
	}

	@Override
	public String toString() {
		return "HttpResponse{" + "statusCode=" + statusCode + ", content='"
				+ content + '\'' + ", used=" + used + '}';
	}
}