package com.fanfou.app.http;

import java.util.List;

import org.apache.http.client.methods.HttpUriRequest;

import com.fanfou.app.api.ApiException;

/**
 * @author mcxiaoke
 * @version 1.0 2011.08.29
 * @version 1.1 2011.10.25
 */
public class SimpleAuthClient extends BaseClient {

	public SimpleAuthClient() {
		super();
	}

	@Override
	void setAuthorization(HttpUriRequest request, List<Parameter> params)
			throws ApiException {
	}
}
