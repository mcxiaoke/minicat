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
import org.apache.http.entity.mime.MultipartEntity;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.entity.mime.content.StringBody;
import org.apache.http.message.BasicHeader;
import org.apache.http.protocol.HTTP;

import android.text.TextUtils;
import com.fanfou.app.util.Utils;

/**
 * @author mcxiaoke
 * @version 1.0 2011.11.03
 * @version 1.1 2011.11.04
 * 
 */
public final class ConnectionRequest {

	public final boolean post;
	public final List<Parameter> params;
	public final List<Header> headers;
	public final HttpEntity entity;
	public final String url;

	private ConnectionRequest(Builder builder) {
		this.post = builder.post;
		this.headers = builder.headers;
		this.params = builder.params;
		if (post) {
			if (Utils.isEmpty(params)) {
				throw new IllegalArgumentException("POST参数不能为空");
			}
			this.url = builder.url;
			if (containsFile(params)) {
				entity = encodeMultipart(params);
			} else {
				entity = encodeForPost(params);
			}
		} else {
			if (Utils.isEmpty(params)) {
				this.url = builder.url;
			} else {
				this.url = builder.url + "?" + encodeForGet(params);
				;
			}
			this.entity = null;
		}
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
		
		public Builder format(boolean isHtml) {
			if(isHtml){
				this.params.add(new Parameter("format", "html"));
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

		public ConnectionRequest build() {
			return create();
		}

		private ConnectionRequest create() {
			return new ConnectionRequest(this);
		}

	}

	public static String encodeForGet(List<Parameter> params) {
		if (Utils.isEmpty(params)) {
			return "";
		}
		StringBuffer buf = new StringBuffer();
		for (int i = 0; i < params.size(); i++) {
			Parameter p = params.get(i);
			if (p.isFile()) {
				throw new IllegalArgumentException("GET参数不能为文件");
			}
			if (i > 0) {
				buf.append("&");
			}
			buf.append(encode(p.getName())).append("=")
					.append(encode(p.getValue()));
		}
		return buf.toString();
	}

	public static MultipartEntity encodeMultipart(List<Parameter> params) {
		if (Utils.isEmpty(params)) {
			throw new IllegalArgumentException("POST参数不能为空");
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
			throw new IllegalArgumentException("POST参数不能为空");
		}
		HttpEntity entity = null;
		try {
			entity = new UrlEncodedFormEntity(params, HTTP.UTF_8);
		} catch (UnsupportedEncodingException e) {
		}
		return entity;
	}

	public static String encode(String value) {
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

}
