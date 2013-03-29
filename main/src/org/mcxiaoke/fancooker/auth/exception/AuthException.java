package org.mcxiaoke.fancooker.auth.exception;

public class AuthException extends Exception {
	public AuthException() {
		super();
	}

	public AuthException(String detailMessage, Throwable throwable) {
		super(detailMessage, throwable);
	}

	public AuthException(String detailMessage) {
		super(detailMessage);
	}

	public AuthException(Throwable throwable) {
		super(throwable);
	}

	private static final long serialVersionUID = 1L;

}
