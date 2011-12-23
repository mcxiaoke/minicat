package com.fanfou.app.service;

/**
 * @author mcxiaoke
 * @version 1.0 2011.12.16
 * @version 2.0 2011.12.19
 * 
 */
public final class Constants {
	public static final int TYPE_NONE = 0;

	public static final int TYPE_ACCOUNT_REGISTER = 10;
	// POST or GET
	public static final int TYPE_ACCOUNT_VERIFY_CREDENTIALS = 11;
	public static final int TYPE_ACCOUNT_RATE_LIMIT_STATUS = 12;
	// POST
	public static final int TYPE_ACCOUNT_UPDATE_PROFILE = 13;
	// post
	public static final int TYPE_ACCOUNT_UPDATE_PROFILE_IMAGE = 14;
	public static final int TYPE_ACCOUNT_NOTIFICATION = 15;

	public static final int TYPE_STATUSES_HOME_TIMELINE = 30;
	public static final int TYPE_STATUSES_MENTIONS = 31;
	public static final int TYPE_STATUSES_USER_TIMELINE = 32;
	public static final int TYPE_STATUSES_CONTEXT_TIMELINE = 33;
	public static final int TYPE_STATUSES_PUBLIC_TIMELINE = 34;

	public static final int TYPE_STATUSES_SHOW = 35;
	// POST
	public static final int TYPE_STATUSES_UPDATE = 36;
	// POST
	public static final int TYPE_STATUSES_DESTROY = 37;

	public static final int TYPE_DIRECT_MESSAGES_INBOX = 50;
	public static final int TYPE_DIRECT_MESSAGES_OUTBOX = 51;
	public static final int TYPE_DIRECT_MESSAGES_CONVERSTATION_LIST = 52;
	public static final int TYPE_DIRECT_MESSAGES_CONVERSTATION = 53;
	// POST
	public static final int TYPE_DIRECT_MESSAGES_CREATE = 54;
	// POST
	public static final int TYPE_DIRECT_MESSAGES_DESTROY = 55;

	public static final int TYPE_USERS_SHOW = 70;
	public static final int TYPE_USERS_FRIENDS = 71;
	public static final int TYPE_USERS_FOLLOWERS = 72;

	// POST
	public static final int TYPE_FRIENDSHIPS_CREATE = 81;
	// POST
	public static final int TYPE_FRIENDSHIPS_DESTROY = 82;
	public static final int TYPE_FRIENDSHIPS_EXISTS = 83;
	public static final int TYPE_FRIENDSHIPS_SHOW = 84;
	public static final int TYPE_FRIENDSHIPS_REQUESTS = 85;
	public static final int TYPE_FRIENDSHIPS_DENY = 86;
	public static final int TYPE_FRIENDSHIPS_ACCEPT = 87;

	public static final int TYPE_BLOCKS = 100;
	public static final int TYPE_BLOCKS_IDS = 101;
	// POST
	public static final int TYPE_BLOCKS_CREATE = 102;
	// POST
	public static final int TYPE_BLOCKS_DESTROY = 103;
	public static final int TYPE_BLOCKS_EXISTS = 104;

	public static final int TYPE_FRIENDS_IDS = 110;
	public static final int TYPE_FOLLOWERS_IDS = 111;

	public static final int TYPE_FAVORITES_LIST = 120;
	// POST
	public static final int TYPE_FAVORITES_CREATE = 121;
	// POST
	public static final int TYPE_FAVORITES_DESTROY = 122;

	public static final int TYPE_PHOTOS_USER_TIMELINE = 130;
	// POST
	public static final int TYPE_PHOTOS_UPLOAD = 131;

	public static final int TYPE_SEARCH_PUBLIC_TIMELINE = 140;
	public static final int TYPE_SEARCH_USER_TIMELINE = 141;
	public static final int TYPE_SEARCH_USERS = 142;

	public static final int TYPE_SAVED_SEARCHES_LIST = 150;
	public static final int TYPE_SAVED_SEARCHES_SHOW = 151;
	// POST
	public static final int TYPE_SAVED_SEARCHES_CREATE = 152;
	// POST
	public static final int TYPE_SAVED_SEARCHES_DESTROY = 153;

	public static final int TYPE_TRENDS_LIST = 154;

	public static final String EXTRA_RECEIVER = "com.fanfou.app.EXTRA_RECEIVER";
	public static final String EXTRA_MESSENGER = "com.fanfou.app.EXTRA_MESSENGER";
	public static final String EXTRA_BUNDLE = "com.fanfou.app.EXTRA_BUNDLE";
	public static final String EXTRA_CODE = "com.fanfou.app.EXTRA_CODE";
	public static final String EXTRA_ERROR = "com.fanfou.app.EXTRA_ERROR";
	public static final String EXTRA_SIZE = "com.fanfou.app.EXTRA_SIZE";
	public static final String EXTRA_TYPE = "com.fanfou.app.EXTRA_TYPE";
	public static final String EXTRA_BOOLEAN = "com.fanfou.app.EXTRA_BOOLEAN";
	public static final String EXTRA_PAGE = "com.fanfou.app.EXTRA_PAGE";
	public static final String EXTRA_COUNT = "com.fanfou.app.EXTRA_COUNT";
	public static final String EXTRA_ID = "com.fanfou.app.EXTRA_ID";
	public static final String EXTRA_SINCE_ID = "com.fanfou.app.EXTRA_SINCE_ID";
	public static final String EXTRA_MAX_ID = "com.fanfou.app.EXTRA_MAX_ID";
	public static final String EXTRA_DATA = "com.fanfou.app.EXTRA_DATA";
	public static final String EXTRA_URL = "com.fanfou.app.EXTRA_URL";
	public static final String EXTRA_USER_NAME = "com.fanfou.app.EXTRA_USER_NAME";
	public static final String EXTRA_USER_HEAD = "com.fanfou.app.EXTRA_USER_HEAD";
	public static final String EXTRA_TEXT = "com.fanfou.app.EXTRA_TEXT";
	public static final String EXTRA_FILENAME = "com.fanfou.app.EXTRA_FILENAME";
	public static final String EXTRA_LOCATION = "com.fanfou.app.EXTRA_LOCATION";
	public static final String EXTRA_IN_REPLY_TO_ID = "com.fanfou.app.EXTRA_IN_REPLY_TO_ID";
	public static final String EXTRA_REPOST_ID = "com.fanfou.app.EXTRA_REPOST_ID";

	public static final String ACTION_PACKAGE = "com.fanfou.app.action.";
	public static final String ACTION_STATUS = ACTION_PACKAGE + "STATUS";
	public static final String ACTION_SHARE = ACTION_PACKAGE + "SHARE";
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
	public static final String ACTION_DRAFTS_SENT = ACTION_PACKAGE
			+ "ACTION_DRAFTS_SENT";

	public static final int RESULT_SUCCESS = -1;
	public static final int RESULT_FAILED = -2;
	public static final int RESULT_ERROR = -3;

	public static final int MAX_TIMELINE_COUNT = 60;
	public static final int DEFAULT_TIMELINE_COUNT = 20;
	public static final int MAX_USERS_COUNT = 60;
	public static final int DEFAULT_USERS_COUNT = 20;
	public static final int MAX_IDS_COUNT = 2000;
	
	public static final String FORMAT = "html";
	public static final String MODE = "lite";
}
