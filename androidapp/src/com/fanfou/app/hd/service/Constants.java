package com.fanfou.app.hd.service;

/**
 * @author mcxiaoke
 * @version 1.0 2011.12.16
 * @version 2.0 2011.12.19
 * @version 3.0 2012.02.24
 * 
 */
public final class Constants {
	
	public static final String PACKAGE_NAME="com.fanfou.app.hd";

	public static final String ACTION_PACKAGE = PACKAGE_NAME+".action.";
	public static final String ACTION_STATUS = ACTION_PACKAGE + "STATUS";
	public static final String ACTION_SHARE = ACTION_PACKAGE + "SHARE";
	public static final String ACTION_SEARCH = ACTION_PACKAGE + "SEARCH";
	public static final String ACTION_MESSAGES = ACTION_PACKAGE + "MESSAGES";
	
	public static final String ACTION_ALARM_NOTITICATION = ACTION_PACKAGE + "ALARM_NOTITICATION";
	public static final String ACTION_ALARM_AUTO_COMPLETE = ACTION_PACKAGE + "ALARM_AUTO_COMPLETE";
	public static final String ACTION_ALARM_AUTO_UPDATE_CHECK = ACTION_PACKAGE + "AUTO_UPDATE_CHECK";
	
	public static final String ACTION_NOTIFICATION = ACTION_PACKAGE
			+ "NOTIFICATION";
	public static final String ACTION_SEND = ACTION_PACKAGE + "SEND";
	public static final String ACTION_SEND_FROM_GALLERY = ACTION_PACKAGE
			+ "GALLERY";
	public static final String ACTION_SEND_FROM_CAMERA = ACTION_PACKAGE
			+ "CAMERA";
	public static final String ACTION_MESSAGE_SENT = ACTION_PACKAGE
			+ "ACTION_MESSAGE_SEND";
	public static final String ACTION_STATUS_SENT = ACTION_PACKAGE
			+ "ACTION_STATUS_SEND";
	public static final String ACTION_DRAFTS_SENT = ACTION_PACKAGE
			+ "ACTION_DRAFTS_SENT";

	public static final int RESULT_SUCCESS = -1;
	public static final int RESULT_FAILED = -2;
	public static final int RESULT_ERROR = -3;
}
