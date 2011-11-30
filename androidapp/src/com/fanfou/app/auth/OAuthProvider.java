package com.fanfou.app.auth;

/**
 * @author mcxiaoke
 * @version 1.0 2011.11.30
 *
 */
public interface OAuthProvider {
	
	String getProviderBaseURL();
	
	String getRequestTokenURL();
	String getAuthenticateURL();
	String getAuthorizeURL();
	String getAccessTokenURL();
	
	String getConsumerKey();
	String getConsumerSercret();

}
