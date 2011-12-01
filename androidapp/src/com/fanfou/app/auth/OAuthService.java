package com.fanfou.app.auth;

import java.util.List;

import javax.crypto.spec.SecretKeySpec;

import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.message.BasicHeader;
import com.fanfou.app.http.Parameter;

/**
 * @author mcxiaoke
 * @version 2.0 2011.11.03
 * @version 2.1 2011.11.24
 * @version 2.2 2011.11.28
 * @version 3.0 2011.11.30
 * @version 4.0 2011.12.01
 * 
 */
public class OAuthService{

	private OAuthProvider mOAuthProvider;
	private OAuthToken mOAuthToken;
	private SecretKeySpec mSecretKeySpec;

	public OAuthService(OAuthProvider provider) {
		mOAuthProvider = provider;
	}

	public OAuthService(OAuthProvider provider, OAuthToken token) {
		this.mOAuthProvider = provider;
		setOAuthToken(token);
	}

	public void signRequest(HttpUriRequest request, List<Parameter> params) {
		String authorization = OAuthHelper.buildOAuthHeader(
				request.getMethod(), request.getURI().toString(), params,
				mOAuthProvider, mOAuthToken, mSecretKeySpec);
		request.addHeader(new BasicHeader("Authorization", authorization));
	}

	public void setOAuthToken(OAuthToken token) {
		if (token == null) {
			return;
		}
		this.mOAuthToken = token;
		this.mSecretKeySpec = OAuthHelper.getSecretKeySpec(mOAuthProvider,
				mOAuthToken);
	}

}
