package com.fanfou.app.config;

/**
 * @author mcxiaoke
 * 
 */
public interface Actions {
	public static final String ACTION_PACKAGE = "com.fanfou.app.action.";

	public static final String ACTION_STATUS = ACTION_PACKAGE + "STATUS";
	public static final String ACTION_SHARE = ACTION_PACKAGE + "SHARE";

	public static final String ACTION_PROFILE = ACTION_PACKAGE + "PROFILE";

	public static final String ACTION_HOME = ACTION_PACKAGE + "HOME";
	public static final String ACTION_MENTIONS = ACTION_PACKAGE + "MENTIONS";
	public static final String ACTION_PUBLIC = ACTION_PACKAGE + "PUBLIC";
	public static final String ACTION_FAVORITES = ACTION_PACKAGE + "FAVORITES";
	public static final String ACTION_TIMELINE = ACTION_PACKAGE + "TIMELINE";

	public static final String ACTION_FRIENDS = ACTION_PACKAGE + "FRIENDS";
	public static final String ACTION_FOLLOWERS = ACTION_PACKAGE + "FOLLOWERS";

	public static final String ACTION_SEARCH = ACTION_PACKAGE + "SEARCH";

	public static final String ACTION_MESSAGES = ACTION_PACKAGE + "MESSAGES";

	public static final String ACTION_REPEAT = ACTION_PACKAGE + "REPEAT";

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
}
