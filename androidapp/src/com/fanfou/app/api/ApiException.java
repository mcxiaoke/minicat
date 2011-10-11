/**
 * 
 */
package com.fanfou.app.api;

/**
 * @author mcxiaoke
 * @version 1.0 2011.05.02
 * @version 1.1 2011.05.05
 * @version 1.2 2011.05.06
 * @version 1.3 2011.05.18
 * 
 */
public class ApiException extends Exception {

	private static final long serialVersionUID = 6673077544941712048L;
	public final int statusCode;
	public final String errorMessage;

	public ApiException(int statusCode) {
		super();
		this.statusCode = statusCode;
		this.errorMessage = "";
	}

	public ApiException(int statusCode, String detailMessage) {
		super(detailMessage);
		this.statusCode = statusCode;
		this.errorMessage = detailMessage;
	}

	public ApiException(int statusCode, String detailMessage,
			Throwable throwable) {
		super(detailMessage, throwable);
		this.statusCode = statusCode;
		this.errorMessage = detailMessage;

	}

	public ApiException(int statusCode, Throwable throwable) {
		super(throwable);
		this.statusCode = statusCode;
		this.errorMessage = throwable.toString();

	}

}
