package com.fanfou.app.auth;

import java.util.List;
import org.apache.http.client.methods.HttpUriRequest;

import com.fanfou.app.http.Parameter;

public interface OAuthSupport extends AuthSupport {
	
	void signRequest(HttpUriRequest request, List<Parameter> params);

}
