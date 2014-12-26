package com.mcxiaoke.minicat.dao.model;

import android.content.ContentResolver;
import android.net.Uri;

/**
 * @author mcxiaoke
 * @version 1.1 2012.02.22
 */
public interface StatusColumns extends IBaseColumns {
    public static final String TEXT = "text";
    public static final String SIMPLE_TEXT = "simple_text";
    public static final String SOURCE = "source";
    public static final String GEO = "geo";
    public static final String MEDIA = "media";

    public static final String USER_RAWID = "user_rawid";
    public static final String USER_ID = "user_id";
    public static final String USER_SCREEN_NAME = "user_screen_name";
    public static final String USER_PROFILE_IMAGE_URL = "user_profile_image_url";

    public static final String IN_REPLY_TO_STATUS_ID = "in_reply_to_status_id";
    public static final String IN_REPLY_TO_USER_ID = "in_reply_to_user_id";
    public static final String IN_REPLY_TO_SCREEN_NAME = "in_reply_to_screen_name";

    public static final String RT_STATUS_ID = "rt_status_id";
    public static final String RT_USER_ID = "rt_user_id";
    public static final String RT_USER_SCREEN_NAME = "rt_user_screen_name";

    public static final String PHOTO_IMAGE_URL = "imageurl";
    public static final String PHOTO_THUMB_URL = "thumburl";
    public static final String PHOTO_LARGE_URL = "largeurl";

    public static final String TRUNCATED = "truncated";
    public static final String FAVORITED = "favorited";
    public static final String RETWEETED = "retweeted";
    public static final String SELF = "self";

    public static final String READ = "read";
    public static final String THREAD = "thread";
    public static final String PHOTO = "photo";
    public static final String SPECIAL = "special";


    public static final String TABLE_NAME = "status";
    public static final Uri CONTENT_URI = Uri.parse("content://"
            + AUTHORITY + "/" + TABLE_NAME);
    public static final String CREATE_TABLE = "create table "
            + TABLE_NAME + " ( "
            + _ID + " integer primary key autoincrement, "

            + ID + " text not null, "
            + ACCOUNT + " text not null, "
            + OWNER + " text, "
            + NOTE + " text, "

            + TYPE + " integer not null, "
            + FLAG + " integer not null, "

            + RAWID + " integer not null, "
            + TIME + " integer not null, "

            + TEXT + " text not null, "
            + SIMPLE_TEXT + " text not null, "
            + SOURCE + " text not null, "
            + GEO + " text, "
            + MEDIA + " text, "

            + USER_RAWID + " integer not null, "
            + USER_ID + " text not null, "
            + USER_SCREEN_NAME + " text not null, "
            + USER_PROFILE_IMAGE_URL + " text not null, "

            + IN_REPLY_TO_STATUS_ID + " text, "
            + IN_REPLY_TO_USER_ID + " text, "
            + IN_REPLY_TO_SCREEN_NAME + " text, "

            + RT_STATUS_ID + " text, "
            + RT_USER_ID + " text, "
            + RT_USER_SCREEN_NAME + " text, "

            + PHOTO_IMAGE_URL + " text, "
            + PHOTO_THUMB_URL + " text, "
            + PHOTO_LARGE_URL + " text, "

            + TRUNCATED + " boolean not null, "
            + FAVORITED + " boolean not null, "
            + RETWEETED + " boolean not null, "
            + SELF + " boolean not null, "

            + READ + " boolean not null, "
            + THREAD + " boolean not null, "
            + PHOTO + " boolean not null, "
            + SPECIAL + " boolean not null, "

            + "unique ( "
            + ACCOUNT + ","
            + TYPE + ","
            + ID
            + " ) on conflict ignore );";
    public static final String CONTENT_TYPE = ContentResolver.CURSOR_DIR_BASE_TYPE
            + "/vnd.mcxiaoke.status";
    public static final String CONTENT_ITEM_TYPE = ContentResolver.CURSOR_ITEM_BASE_TYPE
            + "/vnd.mcxiaoke.status";

}
