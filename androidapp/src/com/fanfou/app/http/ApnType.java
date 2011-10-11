package com.fanfou.app.http;

/**
 * @author mcxiaoke
 * @version 1.0 2011.10.08
 * 
 */
public enum ApnType {
	WIFI("wifi"), HSDPA("hsdpa"), NET("net"), WAP("wap"), CTWAP("ctwap"), NONE(
			"none"), ;

	private String tag;

	ApnType(String tag) {
		this.tag = tag;
	}

	@Override
	public String toString() {
		return tag;
	}
}
