package com.fanfou.app.config;

public interface ACTION {
	public static final String PACKAGE_NAME = "com.fanfou.app";
	public static final String ACTION_PACKAGE = PACKAGE_NAME + ".action.";

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
}
