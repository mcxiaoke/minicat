package com.fanfou.app.hd.http;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.StatusLine;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.CharArrayBuffer;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.fanfou.app.hd.api.ApiException;
import com.fanfou.app.hd.util.IOHelper;

/**
 * @author mcxiaoke
 * @version 1.0 2011.06.10
 * @version 2.0 2011.09.05
 * @version 3.0 2011.11.10
 * @version 3.1 2011.12.05
 * 
 */
public class NetResponse implements ResponseInterface, ResponseCode {
	private static final String TAG = NetResponse.class.getSimpleName();

	private static final int BUFFER_SIZE = 8196;

	// private HttpResponse response;
	private HttpEntity entity;
	private String content;
	private boolean used;
	public final StatusLine statusLine;
	public final int statusCode;

	// public final Header[] headers;

	public NetResponse(HttpResponse response) {
		// this.response = response;
		this.entity = response.getEntity();
		this.statusLine = response.getStatusLine();
		this.statusCode = statusLine.getStatusCode();
		// this.headers = response.getAllHeaders();
	}

	@Override
	public final String getContent() throws IOException {
		if (content == null) {
			// content = EntityUtils.toString(entity, HTTP.UTF_8);
			content = entityToString(entity);
			used = true;
		}
//		if (App.DEBUG) {
//			Log.d(TAG, "getContent() [" + content + "]");
//		}
		return content;
	}

	public final JSONObject getJSONObject() throws ApiException {
		try {

			return new JSONObject(getContent());
		} catch (IOException e) {
			throw new ApiException(ResponseCode.ERROR_IO_EXCEPTION,
					e.getMessage(), e);
		} catch (JSONException e) {
			throw new ApiException(ResponseCode.ERROR_JSON_EXCEPTION,
					e.getMessage(), e);
		}
	}

	public final JSONArray getJSONArray() throws ApiException {
		try {
			return new JSONArray(getContent());
		} catch (IOException e) {
			throw new ApiException(ResponseCode.ERROR_IO_EXCEPTION,
					e.getMessage(), e);
		} catch (JSONException e) {
			throw new ApiException(ResponseCode.ERROR_JSON_EXCEPTION,
					e.getMessage(), e);
		}
	}

	@Override
	public String toString() {
		return "HttpResponse{" + "statusCode=" + statusCode + ", content='"
				+ content + '\'' + ", used=" + used + '}';
	}

	public static String entityToString(final HttpEntity entity)
			throws IOException {
		if (entity == null) {
			return "";
		}
		InputStream is = entity.getContent();
		if (is == null) {
			return "";
		}
		if (entity.getContentLength() > Integer.MAX_VALUE) {
			throw new IllegalArgumentException(
					"HTTP entity too large to be buffered in memory");
		}
		int i = (int) entity.getContentLength();
		if (i < 0) {
			i = 4096;
		}
		String charset = HTTP.UTF_8;
		Reader reader = new InputStreamReader(is, charset);
		CharArrayBuffer buffer = new CharArrayBuffer(i);
		try {
			char[] tmp = new char[BUFFER_SIZE];
			int c;
			while ((c = reader.read(tmp)) != -1) {
				buffer.append(tmp, 0, c);
			}
		} finally {
			IOHelper.forceClose(reader);
			IOHelper.forceClose(is);
		}
		return buffer.toString();
	}
}