package com.mcxiaoke.fanfouapp.api.rest;

import com.mcxiaoke.fanfouapp.api.ApiException;
import org.oauthsimple.model.OAuthToken;

import java.io.IOException;

/**
 * @author mcxiaoke
 * @version 1.0 2012-2-23 上午10:16:46
 * @version 1.1 2012.02.27
 * 
 */
public interface OAuthMethods {

	public void setAccount(String account);

	public String getAccount();

	public OAuthToken getOAuthRequestToken() throws ApiException;

	public OAuthToken getOAuthAccessToken(String username, String password)
			throws IOException,ApiException;

	public void setAccessToken(OAuthToken token);

}
