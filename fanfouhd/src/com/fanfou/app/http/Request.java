package com.fanfou.app.http;

import java.io.Serializable;
import java.util.List;
import org.apache.http.Header;
import org.apache.http.HttpEntity;
import com.fanfou.app.util.Utils;

/**
 * @author mcxiaoke
 * @version 1.0 2011.05.02
 * @version 1.1 2011.05.03
 * @version 1.2 2011.05.04
 * @version 1.3 2011.05.05
 * @version 1.4 2011.05.17
 * 
 */
public class Request implements Serializable {

	private static final long serialVersionUID = -2609931326464668309L;

	private final boolean post;
	private final List<Parameter> params;
	private final List<Header> headers;

	private final HttpEntity entity;
	private final String url;
	private final String method;

	public Request(String url) {
		this(url, null, null, false);
	}

	public Request(String url, boolean post) {
		this(url, null, null, post);
	}

	public Request(String url, List<Parameter> params) {
		this(url, params, null, false);
	}

	public Request(String url, List<Parameter> params, boolean post) {
		this(url, params, null, post);
	}

	public Request(String url, List<Parameter> params, List<Header> headers) {
		this(url, params, headers, false);
	}

	public Request(String url, List<Parameter> params, List<Header> headers,
			boolean post) {
		this.post = post;
		this.headers = headers;
		this.params = params;
		if (post) {
			this.method="POST";
			if (Utils.isEmpty(params)) {
				throw new IllegalArgumentException("POST参数不能为空");
			}
			this.url = url;
			if (Parameter.containsFile(params)) {
				entity = Parameter.encodeMultipart(params);
			} else {
				entity = Parameter.encodeForPost(params);
			}
		} else {
			this.method="GET";
			if(Utils.isEmpty(params)){
				this.url = url;
			}else{
				this.url = url + "?" + Parameter.encodeForGet(params);;
			}
			this.entity = null;
		}
	}

	public boolean isPostMethod() {
		return post;
	}
	
	public String getMethod(){
		return method;
	}

	public HttpEntity getEntity() {
		if (!post) {
			throw new IllegalAccessError("访问非法：不是POST请求");
		}
		return entity;
	}

	public String getURL() {
		return url;
	}

	public List<Header> getHeaders() {
		return headers;
	}

	public List<Parameter> getParams() {
		return params;
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append("\n[Request] URL=").append(url).append("\n[Request] Method=")
				.append(post ? "POST" : "GET");
		if (post) {
			sb.append("\n[Request] HasFile=").append(
					Parameter.containsFile(params) ? "YES" : "NO");
		}

		if (headers != null && headers.size() > 0) {
			sb.append("\n[Request] Headers= [ ");
			for (int i = 0; i < headers.size(); i++) {
				Header h = headers.get(i);
				sb.append(h.getName()).append("=").append(h.getValue())
						.append(" ");
			}
			sb.append(" ]");
		}
		if (params != null && params.size() > 0) {
			sb.append("\n[Request] Params= [ ");
			for (int i = 0; i < params.size(); i++) {
				Parameter p = params.get(i);
				if (p.isFile()) {
					sb.append(p.getName()).append("=").append("[FILE ")
							.append(p.getFile().getName()).append("], ");
				} else {
					sb.append(p.getName()).append("=").append(p.getValue())
							.append(", ");
				}
			}
			sb.append(" ]");
		}
		return sb.toString();
	}

}
