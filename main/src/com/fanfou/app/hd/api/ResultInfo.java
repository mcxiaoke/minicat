package com.fanfou.app.hd.api;

import java.io.Serializable;

/**
 * @author mcxiaoke
 * @version 1.0 20110830
 * 
 */
public final class ResultInfo implements Serializable {
	public static final int CODE_ERROR = -1;
	public static final int CODE_SUCCESS = 0;
	public static final int CODE_FAILED = 1;
	public static final int CODE_CANCELED = 2;;

	private static final long serialVersionUID = 4195237447592568873L;
	public final int code;
	public final String message;
	public final Object content;

	public ResultInfo(int code) {
		this(code, null, null);
	}

	public ResultInfo(int code, String message) {
		this(code, message, null);
	}

	public ResultInfo(int code, String message, Object content) {
		this.code = code;
		this.message = message;
		this.content = content;
	}

}
