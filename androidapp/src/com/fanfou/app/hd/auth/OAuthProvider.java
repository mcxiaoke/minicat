package com.fanfou.app.hd.auth;

/**
 * @author mcxiaoke
 * @version 1.0 2011.11.30
 * 
 */
public interface OAuthProvider {

	String getRequestTokenURL();

	String getAuthorizeURL();

	String getAccessTokenURL();

	String getConsumerKey();

	String getConsumerSercret();
	
	String getApiHostURL();

}
