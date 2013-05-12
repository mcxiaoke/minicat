package org.mcxiaoke.fancooker.api.rest;

import org.mcxiaoke.fancooker.api.ApiException;
import org.oauthsimple.model.OAuthToken;

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
			throws ApiException;

	public void setAccessToken(OAuthToken token);

}
