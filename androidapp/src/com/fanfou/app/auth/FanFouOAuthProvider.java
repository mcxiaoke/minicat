package com.fanfou.app.auth;

/**
 * @author mcxiaoke
 * @version 1.0 2011.11.30
 *
 */
public class FanFouOAuthProvider implements OAuthProvider {

	private static final String CONSUMER_KEY = "c403d5a51bde9cce58fe31f4cec06b0a";
	private static final String CONSUMER_SECRET = "5da0bcda353f7d2fe8e3de01e3c97741";

	@Override
	public String getProviderBaseURL() {
		return "http://fanfou.com/";
	}

	@Override
	public String getRequestTokenURL() {
		return "http://fanfou.com/oauth/request_token";
	}

	@Override
	public String getAuthenticateURL() {
		return "http://fanfou.com/oauth/oauth/authenticate";
	}

	@Override
	public String getAuthorizeURL() {
		return "http://fanfou.com/oauth/authorize";
	}

	@Override
	public String getAccessTokenURL() {
		return "http://fanfou.com/oauth/access_token";
	}

	@Override
	public String getConsumerKey() {
		return CONSUMER_KEY;
	}

	@Override
	public String getConsumerSercret() {
		return CONSUMER_SECRET;
	}

}
