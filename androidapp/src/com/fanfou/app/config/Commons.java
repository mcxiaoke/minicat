/**
 * 
 */
package com.fanfou.app.config;

/**
 * Common Constants
 * 
 * @author mcxiaoke
 * @version 1.0 2011.07.18
 * @version 1.5 2011.10.10
 * @version 1.6 2011.11.08
 * @version 1.7 2011.11.11
 * 
 */
public interface Commons {

	public static final String EXTRA_DOWNLOAD_URL = "extra_download_url";
	public static final String EXTRA_DOWNLOAD_FILENAME = "extra_download_filename";
	public static final String EXTRA_PHOTO_URL = "extra_photo_url";
	public static final String EXTRA_TYPE = "extra_type";
	public static final String EXTRA_CALLBACK = "extra_callback";
	public static final String EXTRA_RECEIVER = "extra_receiver";
	public static final String EXTRA_BUNDLE = "extra_bundle";
	public static final String EXTRA_BOOLEAN = "extra_boolean";

	public static final String EXTRA_SOURCE = "extra_source";
	public static final String EXTRA_ENUM = "extra_enum";

	public static final String EXTRA_INTEGER = "extra_integer";

	public static final String EXTRA_DRAFT = "extra_draft";
	public static final String EXTRA_DRAFT_ID = "extra_draft_id";

	public static final String EXTRA_GAP_TIME = "extra_gap_time";
	public static final String EXTRA_GAP_ID = "extra_gap_id";
	public static final String EXTRA_GAP_UP_STATUS = "extra_gap_up_status";
	public static final String EXTRA_GAP_DOWN_STATUS = "extra_gap_down_status";

	public static final String EXTRA_ERROR = "extra_error";
	public static final String EXTRA_ERROR_CODE = "extra_error_code";
	public static final String EXTRA_ERROR_MESSAGE = "extra_error_message";

	public static final String EXTRA_COUNT = "extra_count";
	public static final String EXTRA_PAGE = "extra_page";
	public static final String EXTRA_SINCE_ID = "extra_since_id";
	public static final String EXTRA_MAX_ID = "extra_max_id";

	public static final String EXTRA_ID = "extra_id";
	public static final String EXTRA_USER_ID = EXTRA_ID;
	public static final String EXTRA_STATUS_ID = EXTRA_ID;
	public static final String EXTRA_MESSAGE_ID = EXTRA_ID;

	public static final String EXTRA_USER_NAME = "extra_user_name";
	public static final String EXTRA_USER_HEAD = "extra_user_head";
	public static final String EXTRA_USER = "extra_user";

	public static final String EXTRA_STATUS = "extra_status";

	public static final String EXTRA_MESSAGE = "extra_message";

	public static final String EXTRA_FORMAT = "extra_format";
	public static final String EXTRA_QUERY = "extra_query";
	public static final String EXTRA_DATA = "extra_data";
	public static final String EXTRA_TEXT = "extra_text";

	public static final String EXTRA_FILENAME = "extra_filename";
	public static final String EXTRA_FILE = "extra_file";
	public static final String EXTRA_LOCATION = "extra_location";
	public static final String EXTRA_IN_REPLY_TO_ID = "extra_in_reply_to_id";
	public static final String EXTRA_REPOST_ID = "extra_repost_id";

	public static final String EXTRA_URL = "extra_url";

	public static final int RESULT_CODE_ERROR = 10001;
	public static final int RESULT_CODE_START = 10002;
	public static final int RESULT_CODE_FINISH = 10003;

	public static final int TYPE_NONE = 0;

	public static final int STATUS_TYPE_HOME = 1;
	public static final int STATUS_TYPE_MENTION = 2;
	public static final int STATUS_TYPE_USER = 4;
	public static final int STATUS_TYPE_FAVORITES = 5;
	public static final int STATUS_TYPE_PUBLIC = 6;
	public static final int STATUS_TYPE_SEARCH = 7;
	public static final int STATUS_TYPE_CONTEXT = 8;

	public static final int DIRECT_MESSAGE_TYPE_INBOX = 21;
	public static final int DIRECT_MESSAGE_TYPE_OUTBOX = 22;
	public static final int DIRECT_MESSAGE_TYPE_ALL = 23;

	public static final int USER_TYPE_FRIENDS = 41;
	public static final int USER_TYPE_FOLLOWERS = 42;
	public static final int USER_TYPE_ALL = 43;
	// public static final int USER_AUTO_COMPLETE = 45;

	public static final int ACTION_STATUS_SHOW = 101;
	public static final int ACTION_STATUS_CREATE = 102;
	public static final int ACTION_STATUS_DELETE = 103;
	public static final int ACTION_STATUS_FAVORITE = 104;
	public static final int ACTION_STATUS_UNFAVORITE = 105;

	public static final int ACTION_USER_SHOW = 121;
	public static final int ACTION_USER_FOLLOW = 122;
	public static final int ACTION_USER_UNFOLLOW = 123;
	public static final int ACTION_USER_BLOCK = 124;
	public static final int ACTION_USER_UNBLOCK = 125;
	public static final int ACTION_USER_RELATION = 126;

	public static final int ACTION_DIRECT_MESSAGE_SHOW = 141;
	public static final int ACTION_DIRECT_MESSAGE_DELETE = 142;
	public static final int ACTION_DIRECT_MESSAGE_CREATE = 143;

}
