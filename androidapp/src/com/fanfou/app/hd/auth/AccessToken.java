package com.fanfou.app.hd.auth;

public class AccessToken extends OAuthToken {
	private static final long serialVersionUID = 1L;

	public AccessToken(String token, String tokenSecret) {
		super(token, tokenSecret);
	}

	public AccessToken(String content) {
		super(content);
	}

}
