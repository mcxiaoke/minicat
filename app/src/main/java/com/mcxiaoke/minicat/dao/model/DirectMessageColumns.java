package com.mcxiaoke.minicat.dao.model;

import android.content.ContentResolver;
import android.net.Uri;

/**
 * @author mcxiaoke
 * @version 1.1 2012.02.27
 */
public interface DirectMessageColumns extends IBaseColumns {
    public static final String TEXT = "text";

    public static final String SENDER_ID = "sender_id";
    public static final String SENDER_SCREEN_NAME = "sender_screen_name";
    public static final String SENDER_PROFILE_IMAGE_URL = "sender_profile_image_url";

    public static final String RECIPIENT_ID = "recipient_id";
    public static final String RECIPIENT_SCREEN_NAME = "recipient_screen_name";
    public static final String RECIPIENT_PROFILE_IMAGE_URL = "recipient_profile_image_url";

    public static final String CONVERSATION_ID = "conversation_id";

    public static final String READ = "read";
    public static final String INCOMING = "incoming";


    public static final String TABLE_NAME = "dm";
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

            + SENDER_ID + " text not null, "
            + SENDER_SCREEN_NAME + " text not null, "
            + SENDER_PROFILE_IMAGE_URL + " text not null, "

            + RECIPIENT_ID + " text not null, "
            + RECIPIENT_SCREEN_NAME + " text not null, "
            + RECIPIENT_PROFILE_IMAGE_URL + " text not null, "

            + CONVERSATION_ID + " text not null, "

            + READ + " boolean not null, "
            + INCOMING + " boolean not null, "

            + "unique ( "
            + ACCOUNT + ","
            + TYPE + ","
            + ID
            + " ) on conflict ignore );";
    public static final String CONTENT_TYPE = ContentResolver.CURSOR_DIR_BASE_TYPE
            + "/vnd.mcxiaoke.dm";
    public static final String CONTENT_ITEM_TYPE = ContentResolver.CURSOR_ITEM_BASE_TYPE
            + "/vnd.mcxiaoke.dm";


}
