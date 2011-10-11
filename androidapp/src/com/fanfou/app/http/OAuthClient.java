package com.fanfou.app.http;

import java.util.List;

import org.apache.http.client.methods.HttpUriRequest;

import com.fanfou.app.App;
import com.fanfou.app.api.ApiException;
import com.fanfou.app.auth.OAuth;
import com.fanfou.app.util.StringHelper;

/**
 * @author mcxiaoke
 * @version 1.0 2011.05.05
 * @version 1.1 2011.05.15
 * 
 */
public class OAuthClient extends BaseClient {

	public OAuthClient() {
		super();
	}

	@Override
	void setAuthorization(HttpUriRequest request, List<Parameter> params)
			throws ApiException {
		if (StringHelper.isEmpty(App.me.oauthAccessToken)
				|| StringHelper.isEmpty(App.me.oauthAccessTokenSecret)) {
			throw new ApiException(ResponseCode.ERROR_AUTH_FAILED, "未通过验证，请登录");
		}
		OAuth oauth = new OAuth(App.me.oauthAccessToken,
				App.me.oauthAccessTokenSecret);
		oauth.signRequest(request, params);
	}

}
