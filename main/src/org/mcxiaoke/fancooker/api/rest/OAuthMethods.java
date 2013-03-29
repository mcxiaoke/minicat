package org.mcxiaoke.fancooker.api.rest;

import org.mcxiaoke.fancooker.api.ApiException;
import org.mcxiaoke.fancooker.auth.AccessToken;
import org.mcxiaoke.fancooker.auth.RequestToken;

/**
 * @author mcxiaoke
 * @version 1.0 2012-2-23 上午10:16:46
 * @version 1.1 2012.02.27
 * 
 */
public interface OAuthMethods {

	public void setAccount(String account);
	
	public String getAccount();

	public void setAccessToken(AccessToken token);

	public AccessToken getAccessToken();
	
	public RequestToken getOAuthRequestToken() throws ApiException;
	public RequestToken getOAuthRequestToken(String callback) throws ApiException;
	public AccessToken getOAuthAccessToken(RequestToken requestToken) throws ApiException;
	public AccessToken getOAuthAccessToken(RequestToken requestToken,
			String verifier) throws ApiException;
	public AccessToken getOAuthAccessToken(String username, String password)
			throws ApiException;

}
