package com.fanfou.app.http;

import java.io.File;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.entity.mime.MultipartEntity;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.entity.mime.content.StringBody;
import org.apache.http.message.BasicHeader;
import org.apache.http.protocol.HTTP;

import android.text.TextUtils;
import android.util.Log;

import com.fanfou.app.App;
import com.fanfou.app.util.Utils;

/**
 * @author mcxiaoke
 * @version 1.0 2011.11.03
 * @version 1.1 2011.11.04
 * @version 1.2 2011.11.18
 * @version 1.3 2011.11.22
 * @version 1.4 2011.11.23
 * @version 2.0 2011.12.01
 * 
 */
public final class NetRequest {
	private static final String TAG = NetRequest.class.getSimpleName();

	public final boolean post;
	public final List<Parameter> params;
	public final List<Header> headers;
	public final HttpEntity entity;
	public final String url;
	public final HttpRequestBase request;

	private NetRequest(Builder builder) {
		this.post = builder.post;
		this.headers = builder.headers;
		this.params = builder.params;
		if (post) {
			this.url = builder.url;
			this.request = new HttpPost(url);
			if (!Utils.isEmpty(params)) {
				if (containsFile(params)) {
					entity = encodeMultipart(params);
				} else {
					entity = encodeForPost(params);
				}
				((HttpPost) request).setEntity(entity);
			} else {
				entity = null;
			}
		} else {
			this.entity = null;
			if (Utils.isEmpty(params)) {
				this.url = builder.url;
			} else {
				this.url = builder.url + "?" + encodeForGet(params);
				;
			}
			this.request = new HttpGet(url);
		}
		setHeaders(request, headers);
		
	}

	public static Builder newBuilder() {
		return new Builder();
	}

	public static class Builder {
		private boolean post;
		private List<Parameter> params;
		private List<Header> headers;
		private String url;

		public Builder() {
			this.post = false;
			params = new ArrayList<Parameter>();
			headers = new ArrayList<Header>();
		}

		public Builder url(String url) {
			if (TextUtils.isEmpty(url)) {
				throw new NullPointerException(
						"Builder.url() request url must not be empty or null.");
			}
			this.url = url;
			return this;
		}

		public Builder post() {
			this.post = true;
			return this;
		}

		public Builder post(boolean post) {
			this.post = post;
			return this;
		}

		public Builder page(int page) {
			if (page > 0) {
				this.params.add(new Parameter("page", page));
			}
			return this;
		}

		public Builder count(int count) {
			this.params.add(new Parameter("count", count));
			return this;
		}

		public Builder format(String format) {
			if (!TextUtils.isEmpty(format)) {
				this.params.add(new Parameter("format", format));
			}
			return this;
		}

		public Builder mode(String mode) {
			if (!TextUtils.isEmpty(mode)) {
				this.params.add(new Parameter("mode", mode));
			}
			return this;
		}

		public Builder id(String id) {
			if (!TextUtils.isEmpty(id)) {
				this.params.add(new Parameter("id", id));
			}
			return this;
		}

		public Builder status(String status) {
			if (!TextUtils.isEmpty(status)) {
				this.params.add(new Parameter("status", status));
			}
			return this;
		}

		public Builder location(String location) {
			if (!TextUtils.isEmpty(location)) {
				this.params.add(new Parameter("location", location));
			}
			return this;
		}

		public Builder sinceId(String sinceId) {
			if (!TextUtils.isEmpty(sinceId)) {
				this.params.add(new Parameter("since_id", sinceId));
			}
			return this;
		}

		public Builder maxId(String maxId) {
			if (!TextUtils.isEmpty(maxId)) {
				this.params.add(new Parameter("max_id", maxId));
			}
			return this;
		}

		public Builder param(String name, String value) {
			if (!TextUtils.isEmpty(name) && !TextUtils.isEmpty(value)) {
				this.params.add(new Parameter(name, value));
			}
			return this;
		}

		public Builder param(String name, int value) {
			if (!TextUtils.isEmpty(name)) {
				this.params.add(new Parameter(name, value));
			}
			return this;
		}

		public Builder param(String name, long value) {
			if (!TextUtils.isEmpty(name)) {
				this.params.add(new Parameter(name, value));
			}
			return this;
		}

		public Builder param(String name, boolean value) {
			if (!TextUtils.isEmpty(name)) {
				this.params.add(new Parameter(name, value));
			}
			return this;
		}

		public Builder param(String name, File value) {
			if (!TextUtils.isEmpty(name) && value != null) {
				this.params.add(new Parameter(name, value));
			}
			return this;
		}

		public Builder param(NameValuePair pair) {
			this.params.add(new Parameter(pair));
			return this;
		}

		public Builder param(Parameter param) {
			this.params.add(param);
			return this;
		}

		public Builder params(List<Parameter> params) {
			this.params.addAll(params);
			return this;
		}

		public Builder header(String name, String value) {
			if (!TextUtils.isEmpty(name) && !TextUtils.isEmpty(value)) {
				this.headers.add(new BasicHeader(name, value));
			}
			return this;
		}

		public Builder header(Header header) {
			this.headers.add(header);
			return this;
		}

		public Builder headers(List<Header> headers) {
			this.headers.addAll(headers);
			return this;
		}

		public NetRequest build() {
			return create();
		}

		private NetRequest create() {
			return new NetRequest(this);
		}

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
		
		if(App.DEBUG){
			Log.d(TAG, "encodeForGet 1 result="+sb.toString());
		}
		
		return sb.toString();
	}

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
	
	public static String encode(String input){
		try {
			return URLEncoder.encode(input, HTTP.UTF_8);
		} catch (UnsupportedEncodingException e) {
		}
		return input;
	}
	
	private final static void setHeaders(HttpRequestBase request,
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
	
//	private static final String BOUNDARY="GFDGFD34gdft4tdgdg";
//	private byte[] generatePhotoRequest(byte[] imagedata){
//	    byte[] requestData = null;
//	    ByteArrayOutputStream bufer = new ByteArrayOutputStream();
//	    DataOutputStream dataOut = new DataOutputStream(bufer);
//	    try{
//	        dataOut.writeBytes("--");
//	        dataOut.writeBytes(BOUNDARY);
//	        dataOut.writeBytes("\r\n");
//	        dataOut.writeBytes("Content-Disposition: form-data; name=\"auth\"; filename=\"auth\"\r\n");
//	        dataOut.writeBytes("Content-Type: text/xml; charset=utf-8\r\n");
//	        dataOut.writeBytes("\r\n");
//	        dataOut.write(generateAuth());
//	        dataOut.writeBytes("\r\n--" + BOUNDARY + "\r\n");
//	        dataOut.writeBytes("Content-Disposition: form-data; name=\""+CIMAGE+"\"; filename=\""+CIMAGE+"\"\r\n");
//	        dataOut.writeBytes("Content-Type: "+IMAGE_PNG+"\r\n");
//	        dataOut.writeBytes("\r\n");
//	        bufer.write(imagedata);
//	        dataOut.writeBytes("\r\n");
//	        dataOut.writeBytes("\r\n--" + BOUNDARY + "--\r\n");
//
//	        requestData = bufer.toByteArray();
//	    } catch(IOException ex){
//	        ex.printStackTrace();
//	    } finally{
//	        if(bufer!=null){
//	            try {
//	                bufer.close();
//	            } catch (IOException e) {
//	                // TODO Auto-generated catch block
//	                e.printStackTrace();
//	            }
//	        }
//	    }}

}
