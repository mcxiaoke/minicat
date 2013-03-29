package org.mcxiaoke.fancooker.http;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.NameValuePair;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.message.BasicHeader;
import org.mcxiaoke.fancooker.api.Paging;
import org.mcxiaoke.fancooker.util.Assert;
import org.mcxiaoke.fancooker.util.Utils;

import android.text.TextUtils;


/**
 * @author mcxiaoke
 * @version 1.0 2011.11.03
 * @version 1.1 2011.11.04
 * @version 1.2 2011.11.18
 * @version 1.3 2011.11.22
 * @version 1.4 2011.11.23
 * @version 2.0 2011.12.01
 * @version 2.1 2011.12.02
 * @version 2.2 2011.12.05
 * @version 2.3 2012.02.20
 * 
 */
public final class RestRequest {
	private static final String TAG = RestRequest.class.getSimpleName();

	public final boolean post;
	private final List<Parameter> params;
	private final List<Header> headers;
	public final HttpEntity entity;
	public final String url;
	public final HttpRequestBase request;

	private RestRequest(Builder builder) {
		this.post = builder.post;
		this.headers = builder.headers;
		this.params = builder.params;
		if (post) {
			this.url = builder.url;
			this.request = new HttpPost(url);
			if (!Utils.isEmpty(params)) {
				if (NetHelper.containsFile(params)) {
					entity = NetHelper.encodeMultipart(params);
				} else {
					entity = NetHelper.encodeForPost(params);
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
				this.url = builder.url + "?" + NetHelper.encodeForGet(params);
				;
			}
			this.request = new HttpGet(url);
		}
		NetHelper.setHeaders(request, headers);
	}

	public RestResponse send(final RestClient client) throws IOException {
		return client.execute(this, true);
	}

	public void abort() {
		if (request != null) {
			request.abort();
		}
	}

	public List<Parameter> getParams() {
		return this.params;
	}

	public List<Header> getHeaders() {
		return this.headers;
	}

	@Override
	public String toString() {
		return "NetRequest [post=" + post + ", params=" + params + ", headers="
				+ headers + ", entity=" + entity + ", url=" + url
				+ ", request=" + request + "]";
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
			post = false;
			params = new ArrayList<Parameter>();
			headers = new ArrayList<Header>();
		}

		public Builder url(String url) {
			Assert.notEmpty(url);
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

		public Builder paging(Paging p) {
			Assert.notNull(p);
			this.count(p.count);
			this.page(p.page);
			this.sinceId(p.sinceId);
			this.maxId(p.maxId);
			return this;
		}

		public Builder count(int count) {
			this.params.add(new Parameter("count", count));
			return this;
		}

		public Builder format(String format) {
			this.params.add(new Parameter("format", format));
			return this;
		}

		public Builder mode(String mode) {
			this.params.add(new Parameter("mode", mode));
			return this;
		}

		public Builder id(String id) {
			if (!TextUtils.isEmpty(id)) {
				this.params.add(new Parameter("id", id));
			}
			return this;
		}

		public Builder status(String status) {
			Assert.notEmpty(status);
			this.params.add(new Parameter("status", status));
			return this;
		}

		public Builder location(String location) {
			this.params.add(new Parameter("location", location));
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

		public Builder withNameValuePair(List<NameValuePair> params) {
			for (NameValuePair pair : params) {
				this.params.add(new Parameter(pair));
			}
			return this;
		}

		public Builder withNameValuePair(NameValuePair[] params) {
			for (NameValuePair pair : params) {
				this.param(pair);
			}
			return this;
		}

		public Builder params(List<Parameter> params) {
			this.params.addAll(params);
			return this;
		}

		public Builder params(Parameter[] params) {
			for (Parameter param : params) {
				this.param(param);
			}
			return this;
		}

		public Builder params(Map<String, String> map) {
			for (String key : map.keySet()) {
				params.add(new Parameter(key, map.get(key)));
			}
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

		public RestRequest build() {
			return new RestRequest(this);
		}

	}

}
