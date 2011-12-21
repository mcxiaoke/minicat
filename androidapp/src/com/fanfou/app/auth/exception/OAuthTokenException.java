package com.fanfou.app.auth.exception;

public class OAuthTokenException extends AuthException {

	public OAuthTokenException() {
		super();
	}

	public OAuthTokenException(String detailMessage, Throwable throwable) {
		super(detailMessage, throwable);
	}

	public OAuthTokenException(String detailMessage) {
		super(detailMessage);
	}

	public OAuthTokenException(Throwable throwable) {
		super(throwable);
	}

	private static final long serialVersionUID = 1L;

}
