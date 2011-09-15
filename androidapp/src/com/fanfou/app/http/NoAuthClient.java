package com.fanfou.app.http;

import java.util.List;

import org.apache.http.client.methods.HttpUriRequest;
import com.fanfou.app.api.ApiException;

/**
 * @author mcxiaoke
 * @version 1.0 2011.08.29
 */
public class NoAuthClient extends BaseClient {

	public NoAuthClient() {
		super();
	}

	@Override
	void setAuthorization(HttpUriRequest request, List<Parameter> params) throws ApiException{
	}
}
