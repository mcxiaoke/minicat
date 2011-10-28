package com.fanfou.app.http;

import java.util.List;

import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.message.BasicHeader;

import com.fanfou.app.App;
import com.fanfou.app.api.ApiException;
import com.fanfou.app.util.Base64;

/**
 * @author mcxiaoke
 * @version 1.0 2011.05.05
 * @version 1.1 2011.05.15
 * @version 1.2 2011.10.28
 * 
 */
public class BasicClient extends BaseClient {
	private String username;
	private String password;

	public BasicClient() {
		super();
		this.username = App.me.userId;
		this.password = App.me.password;
	}

	public BasicClient(String username, String password) {
		super();
		this.username = username;
		this.password = password;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	@Override
	void setAuthorization(HttpUriRequest request, List<Parameter> params)
			throws ApiException {
		if (null != username && null != password) {
			String basicAuth = "Basic "
					+ Base64.encodeBytes((username + ":" + password).getBytes());
			BasicHeader header = new BasicHeader("Authorization", basicAuth);
			request.setHeader(header);
		} else {
			throw new ApiException(ResponseCode.ERROR_AUTH_EMPTY,
					"username and password must not be empty.");
		}
	}
}
