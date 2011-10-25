package com.fanfou.app.db;

import android.content.ContentResolver;
import android.net.Uri;
import android.provider.BaseColumns;

/**
 * @author mcxiaoke
 * @version 1.0 2011.06.01
 * @version 2.0 2011.07.04
 * @version 3.0 2011.09.10
 * @version 4.0 2011.09.26
 * @version 5.0 2011.10.20
 * @version 5.1 2011.10.25
 *
 */
public final class Contents {
	
	public static final String AUTHORITY = "com.fanfou.app.provider";
	
	public static interface BasicColumns extends BaseColumns{
		public static final String ID="id";
        public static final String OWNER_ID="owner_id";
		public static final String CREATED_AT="created_at";
        public static final String TYPE="type";
        public static final String TIMESTAMP="timestamp";
        public static final String REAL_ID="real_id";
	} 
	
    public static interface UserInfo extends BasicColumns{
    	
    	public static final Uri CONTENT_URI = Uri.parse("content://" + AUTHORITY + "/users");
    	public static final String URI_PATH="users";
    	public static final String CONTENT_TYPE = ContentResolver.CURSOR_DIR_BASE_TYPE+"/vnd.fanfou.user";
    	public static final String CONTENT_ITEM_TYPE = ContentResolver.CURSOR_ITEM_BASE_TYPE+"/vnd.fanfou.user";
    	public static final String TABLE_NAME="user";	

        public static final String NAME= "name";
        public static final String SCREEN_NAME="screen_name";
        public static final String LOCATION="location";
        public static final String GENDER="gender";
        public static final String BIRTHDAY="birthday";
        public static final String DESCRIPTION="description";
        public static final String PROFILE_IMAGE_URL="profile_image_url";
        public static final String URL="url";
        public static final String PROTECTED="protected";
        public static final String FOLLOWERS_COUNT="followers_count";
        public static final String FRIENDS_COUNT="friends_count";
        public static final String FAVORITES_COUNT="favourites_count";
        public static final String STATUSES_COUNT="statuses_count";
        public static final String FOLLOWING="following";
        public static final String NOTIFICATIONS="notifications";
        public static final String UTC_OFFSET="utc_offset";
        
        public static final String LAST_STATUS_CREATED_AT="status_created_at";
        public static final String LAST_STATUS_ID="status_id";
        public static final String LAST_STATUS_TEXT="status_text";
        
        public static final String COLUMNS[]={
            _ID,
            ID,
            REAL_ID,
            OWNER_ID,
            NAME,
            SCREEN_NAME,
            LOCATION,
            GENDER,
            BIRTHDAY,
            DESCRIPTION,
            PROFILE_IMAGE_URL,
            URL,
            PROTECTED,
            FOLLOWERS_COUNT,
            FRIENDS_COUNT,
            FAVORITES_COUNT,
            STATUSES_COUNT,
            FOLLOWING,
            NOTIFICATIONS,
            CREATED_AT,
            UTC_OFFSET,
            LAST_STATUS_CREATED_AT,
            LAST_STATUS_ID,
            LAST_STATUS_TEXT,
            TYPE,
            TIMESTAMP,
        };
        
        public static final String CREATE_TABLE="create table "+TABLE_NAME+" ( "
        	+ _ID+" integer primary key autoincrement, "
            + ID+" text not null, "
            + REAL_ID+" long not null,"
            + NAME+" text not null, "
            + OWNER_ID+" text, "
            + SCREEN_NAME+" text not null, "
            + LOCATION+" text not null, "
            + GENDER+" text not null, "
            + BIRTHDAY+" text not null, "
            + DESCRIPTION+" text not null, "
            + PROFILE_IMAGE_URL+" text not null, "
            + URL+" text not null, "
            + PROTECTED+" boolean not null, "
            + FOLLOWERS_COUNT+" integer not null, "
            + FRIENDS_COUNT+" integer not null, "
            + FAVORITES_COUNT+" integer not null, "
            + STATUSES_COUNT+" integer not null, "
            + FOLLOWING+" boolean not null, "
            + NOTIFICATIONS+" boolean not null, "
            + CREATED_AT+" integer not null, "
            + UTC_OFFSET+" integer not null, "         
            + LAST_STATUS_CREATED_AT+" integer, "
            + LAST_STATUS_ID+" text, "
            + LAST_STATUS_TEXT+" integer, "     
            + TYPE+" integer not null, "
            + TIMESTAMP+" integer not null, " 
//            + "unique ( "+ID+","+TYPE+" ) on conflict replace );";
            + "unique ( "+ID+","+TYPE+","+OWNER_ID+") on conflict ignore );";
    }
    
    public static interface StatusInfo extends BasicColumns{
    	
    	public static final Uri CONTENT_URI = Uri.parse("content://" + AUTHORITY + "/statuses");
//    	public static final Uri PUBLIC_URI=Uri.parse("content://" + AUTHORITY + "/public");
//    	public static final String PUBLIC_URI_PATH="public";
    	public static final String URI_PATH="statuses";
    	public static final String CONTENT_TYPE = ContentResolver.CURSOR_DIR_BASE_TYPE+"/vnd.fanfou.status";
    	public static final String CONTENT_ITEM_TYPE = ContentResolver.CURSOR_ITEM_BASE_TYPE+"/vnd.fanfou.status";
    	
    	public static final String TABLE_NAME="status";
//    	public static final String PUBLIC_TABLE_NAME="public";
    	
        public static final String TEXT="text";
        public static final String SOURCE="source";
        public static final String TRUNCATED="truncated";
        public static final String IN_REPLY_TO_STATUS_ID="in_reply_to_status_id";
        public static final String IN_REPLY_TO_USER_ID="in_reply_to_user_id";
        public static final String FAVORITED="favorited";
        public static final String IN_REPLY_TO_SCREEN_NAME="in_reply_to_screen_name";
        public static final String PHOTO_IMAGE_URL="imageurl";
        public static final String PHOTO_THUMB_URL="thumburl";
        public static final String PHOTO_LARGE_URL="largeurl";
        
        public static final String USER_ID="user_id";
        public static final String USER_SCREEN_NAME="user_screen_name";
        public static final String USER_PROFILE_IMAGE_URL="user_profile_image_url";
        
        public static final String IS_READ="is_read";
        public static final String IS_THREAD="is_thread";
        public static final String HAS_PHOTO="has_photo";
        public static final String SIMPLE_TEXT="simple_text";
        public static final String SPECIAL="special";
        
            
        public static final String COLUMNS[]={
            _ID,
            ID,
            REAL_ID,
            OWNER_ID,
            CREATED_AT,
            TEXT,
            SOURCE,
            TRUNCATED,
            IN_REPLY_TO_STATUS_ID,
            IN_REPLY_TO_USER_ID,
            FAVORITED,
            IN_REPLY_TO_SCREEN_NAME,
            PHOTO_IMAGE_URL,
            PHOTO_THUMB_URL,
            PHOTO_LARGE_URL,
            USER_ID,
            USER_SCREEN_NAME,
            USER_PROFILE_IMAGE_URL,
            TYPE,
            IS_READ,

            IS_THREAD,
            HAS_PHOTO,
            SIMPLE_TEXT,
            SPECIAL,
            TIMESTAMP,
            
        };
        

        
        static final String STATUS_SQL=" ("
            + _ID+" integer primary key autoincrement, "
            + CREATED_AT+" integer not null, "
            + ID+" text not null, "
            + REAL_ID+" long not null,"
            + OWNER_ID+" text, "
            + TEXT+" text not null, "
            + SOURCE+" text not null, "
            + TRUNCATED+" boolean not null, "
            
            + IN_REPLY_TO_STATUS_ID+" text, "
            + IN_REPLY_TO_USER_ID+" text, "
            + IN_REPLY_TO_SCREEN_NAME+" text, "
            
            + FAVORITED+" boolean not null, "
            
            + PHOTO_IMAGE_URL+" text, "
            + PHOTO_THUMB_URL+" text, "
            + PHOTO_LARGE_URL+" text, "
            
            + USER_ID+" text not null, "
            + USER_SCREEN_NAME+" text not null, "
            + USER_PROFILE_IMAGE_URL+" text not null, "
            
            + TYPE+" integer not null, "
            
            + IS_READ+" boolean not null, "
            
            + IS_THREAD+" boolean not null, "
            + HAS_PHOTO+" boolean not null, "
            + SIMPLE_TEXT+" text not null, "
            + SPECIAL+" boolean not null, "
            + TIMESTAMP+" integer not null, " 
            + "unique ( "+ID+","+TYPE+","+OWNER_ID+") on conflict ignore );";
        
        public static final String CREATE_TABLE="create table "+TABLE_NAME+STATUS_SQL;
    } 
    
    public static interface DirectMessageInfo extends BasicColumns{
    	
    	public static final Uri CONTENT_URI = Uri.parse("content://" + AUTHORITY + "/messages");
    	public static final String URI_PATH="messages";
    	public static final String CONTENT_TYPE = ContentResolver.CURSOR_DIR_BASE_TYPE+"/vnd.fanfou.message";
    	public static final String CONTENT_ITEM_TYPE = ContentResolver.CURSOR_ITEM_BASE_TYPE+"/vnd.fanfou.message";
    	
    	public static final String TABLE_NAME="message";

        public static final String TEXT="text";
        public static final String SENDER_ID="sender_id";
        public static final String SENDER_SCREEN_NAME="sender_screen_name";
        public static final String RECIPIENT_ID="recipient_id";
        public static final String RECIPIENT_SCREEN_NAME="recipient_screen_name";
        public static final String SENDER_PROFILE_IMAGE_URL="sender_profile_image_url";
        public static final String RECIPIENT_PROFILE_IMAGE_URL="recipient_profile_image_url";
        
        public static final String THREAD_USER_ID="thread_user_id";
        public static final String THREAD_USER_NAME="thread_user_name";
        public static final String IS_READ="is_read";
        
        public static final String COLUMNS[]={
          _ID,
          ID,
          REAL_ID,
          OWNER_ID,
          TEXT,
          CREATED_AT,
          SENDER_ID,
          SENDER_SCREEN_NAME,
          RECIPIENT_ID,
          RECIPIENT_SCREEN_NAME,
          SENDER_PROFILE_IMAGE_URL,
          RECIPIENT_PROFILE_IMAGE_URL,
          TYPE,
          TIMESTAMP,
          
          THREAD_USER_ID,
          THREAD_USER_NAME,
          IS_READ,
          
        };
        
        public static final String CREATE_TABLE="create table " + TABLE_NAME+" ("
        	+ _ID+" integer primary key autoincrement, "
        	+ ID+" text not null, "
        	+ REAL_ID+" long not null,"
        	+ OWNER_ID+" text , "
        	+ TEXT+" text not null, "
        	+ CREATED_AT+" integer not null, "
        	+ SENDER_ID+" text not null, "
        	+ SENDER_SCREEN_NAME+" text not null, "
        	+ RECIPIENT_ID+" text not null, "
        	+ RECIPIENT_SCREEN_NAME+" text not null, "
        	+ SENDER_PROFILE_IMAGE_URL+" text not null, "
        	+ RECIPIENT_PROFILE_IMAGE_URL+" text not null, "
        	+ TYPE+" integer not null, "
        	+ TIMESTAMP+" integer not null, "
        	
        	+ THREAD_USER_ID+" text not null, "
        	+ THREAD_USER_NAME+" text not null, "
        	+ IS_READ+" boolean not null, "
        	
            + "unique ( "+ID+" ) on conflict ignore );";
    }

}
