package com.fanfou.app.hd.dao.model;

import android.content.ContentResolver;
import android.net.Uri;

/**
 * @author mcxiaoke
 * @version 1.0 2012.02.16
 *
 */
public interface UserColumns extends IBaseColumns {
	public static final String NAME = "name";
	public static final String SCREEN_NAME = "screen_name";
	public static final String LOCATION = "location";
	public static final String GENDER = "gender";
	public static final String BIRTHDAY = "birthday";
	public static final String DESCRIPTION = "description";
	
	public static final String PROFILE_IMAGE_URL = "profile_image_url";
	public static final String PROFILE_IMAGE_URL_LARGE = "profile_image_url_large";
	public static final String URL = "url";
	public static final String STATUS="status";
	
	public static final String FOLLOWERS_COUNT = "followers_count";
	public static final String FRIENDS_COUNT = "friends_count";
	public static final String FAVORITES_COUNT = "favourites_count";
	public static final String STATUSES_COUNT = "statuses_count";
	
	public static final String FOLLOWING = "following";
	public static final String PROTECTED = "protected";
	public static final String NOTIFICATIONS="notifications";
	public static final String VERIFIED="verified";
	public static final String FOLLOW_ME="follow_me";
	
	
	public static final String TABLE_NAME = "user";
	public static final Uri CONTENT_URI = Uri.parse("content://"
			+ AUTHORITY + "/" + TABLE_NAME);
	public static final String URI_PATH = TABLE_NAME;
	public static final String CONTENT_TYPE = ContentResolver.CURSOR_DIR_BASE_TYPE
			+ "/vnd.mcxiaoke.user";
	public static final String CONTENT_ITEM_TYPE = ContentResolver.CURSOR_ITEM_BASE_TYPE
			+ "/vnd.mcxiaoke.user";
	
	public static final String CREATE_TABLE = "create table " 
			+ TABLE_NAME + " ( " 
			+ _ID + " integer primary key autoincrement, " 
			
			+ IDSTR + " text not null, " 
			+ ACCOUNT + "text not null, "
			+ OWNER + " text not null, " 
			+ NOTE + " text, "
			
			+ TYPE + " integer not null, " 
			+ FLAG + " integer not null, " 
			
			+ ID + " integer not null, "
			+ TIME + " integer not null, "
			
			+ NAME + " text not null, " 
			+ SCREEN_NAME + " text not null, " 
			+ LOCATION + " text not null, " 
			+ GENDER + " text not null, " 
			+ BIRTHDAY + " text not null, "
			+ DESCRIPTION + " text not null, " 
			
			+ PROFILE_IMAGE_URL + " text not null, "
			+ PROFILE_IMAGE_URL_LARGE + " text not null, "
			+ URL + " text not null, " 
			+ STATUS + " text not null, "
			
			+ FOLLOWERS_COUNT + " integer not null, " 
			+ FRIENDS_COUNT + " integer not null, "
			+ FAVORITES_COUNT + " integer not null, " 
			+ STATUSES_COUNT + " integer not null, " 
			
			+ FOLLOWING + " boolean not null, "
			+ PROTECTED + " boolean not null, "
			+ NOTIFICATIONS + " boolean not null, "
			+ VERIFIED + " boolean not null, "
			+ FOLLOW_ME + " boolean not null, "
			
			+ "unique ( " 
			+ ACCOUNT + "," 
			+ TYPE + "," 
			+ IDSTR + "," 
			+ " ) on conflict ignore );";
	
	
}
