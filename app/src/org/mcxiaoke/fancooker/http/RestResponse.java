package org.mcxiaoke.fancooker.http;

import java.io.IOException;
import java.io.InputStream;

import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.StatusLine;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.mcxiaoke.fancooker.api.ApiException;


/**
 * @author mcxiaoke
 * @version 1.0 2011.06.10
 * @version 2.0 2011.09.05
 * @version 3.0 2011.11.10
 * @version 3.1 2011.12.05
 * @version 3.2 2012.02.20
 * @version 3.3 2012.02.27
 * 
 */
public class RestResponse {
	private static final String TAG = RestResponse.class.getSimpleName();

	private String content;
	public final int statusCode;
	public final StatusLine statusLine;
	public final HttpEntity entity;
	public final Header[] headers;

	public RestResponse(HttpResponse response) {
		this.statusLine = response.getStatusLine();
		this.statusCode = statusLine.getStatusCode();
		this.headers = response.getAllHeaders();
		this.entity = response.getEntity();
	}

	public final String getContent() throws IOException {
		if (content == null) {
			content = EntityUtils.toString(entity, HTTP.UTF_8);
			// entity.consumeContent();
		}
		return content;
	}

	public final InputStream getInputStream() throws IOException {
		InputStream is = entity.getContent();
		// entity.consumeContent();
		return is;

	}

	public final JSONObject getJSONObject() throws ApiException {
		try {

			return new JSONObject(getContent());
		} catch (IOException e) {
			throw new ApiException(ApiException.IO_ERROR, e.getMessage(), e);
		} catch (JSONException e) {
			throw new ApiException(ApiException.DATA_ERROR, e.getMessage(), e);
		}
	}

	public final JSONArray getJSONArray() throws ApiException {
		try {
			return new JSONArray(getContent());
		} catch (IOException e) {
			throw new ApiException(ApiException.IO_ERROR, e.getMessage(), e);
		} catch (JSONException e) {
			throw new ApiException(ApiException.DATA_ERROR, e.getMessage(), e);
		}
	}

	@Override
	public String toString() {
		return "RestResponse{" + "statusCode=" + statusCode + ", content='"
				+ content + '}';
	}
}