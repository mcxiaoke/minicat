package com.fanfou.app.hd.dao.model;

import android.content.ContentResolver;
import android.net.Uri;

/**
 * @author mcxiaoke
 * @version 2012.02.16
 *
 */
public interface RecordColumns extends IBaseColumns {

	public static final String TEXT = "text";
	public static final String REPLY = "reply";
	public static final String FILE = "file";
	public static final String NOTE="note";
	
	
	public static final String TABLE_NAME = "record";
	public static final Uri CONTENT_URI = Uri.parse("content://"
			+ AUTHORITY + "/" + TABLE_NAME);
	public static final String CONTENT_TYPE = ContentResolver.CURSOR_DIR_BASE_TYPE
			+ "/vnd.mcxiaoke.record";
	public static final String CONTENT_ITEM_TYPE = ContentResolver.CURSOR_ITEM_BASE_TYPE
			+ "/vnd.mcxiaoke.record";
	
	public static final String CREATE_TABLE = "create table " 
			+ TABLE_NAME + " ( " 
			+ _ID + " integer primary key autoincrement, " 
			
			+ ID + " text not null, " 
			+ ACCOUNT + "text not null, "
			+ OWNER + " text not null, " 
			+ NOTE + " text, "
			
			+ TYPE + " integer not null, " 
			+ FLAG + " integer not null, "  
			
			+ RAWID + " integer not null, "
			+ TIME + " integer not null, "
			
			+ TEXT + " text not null, " 
			
			+ TEXT + " text not null, " 
			+ REPLY + " text, " 
			+ FILE + " text, " 
			+ NOTE + " text, "
			
			+ "unique ( " 
			+ ACCOUNT + "," 
			+ TYPE + "," 
			+ ID
			+ " ) on conflict ignore );";
	

}
