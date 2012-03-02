package com.fanfou.app.hd.dao.model;

import android.content.ContentResolver;
import android.net.Uri;
import android.provider.BaseColumns;

/**
 * @author mcxiaoke
 * @version 1.0 2012.02.16
 * @version 2.0 2012.03.02
 *
 */
public interface RecordColumns extends BaseColumns {

	public static final String TYPE = "type";
	public static final String TEXT = "text";
	public static final String REPLY = "reply";
	public static final String FILE = "file";
	public static final String NOTE="note";
	
	
	public static final String TABLE_NAME = "record";
	public static final Uri CONTENT_URI = Uri.parse("content://"
			+ IBaseColumns.AUTHORITY + "/" + TABLE_NAME);
	public static final String CONTENT_TYPE = ContentResolver.CURSOR_DIR_BASE_TYPE
			+ "/vnd.mcxiaoke.record";
	public static final String CONTENT_ITEM_TYPE = ContentResolver.CURSOR_ITEM_BASE_TYPE
			+ "/vnd.mcxiaoke.record";
	
	public static final String CREATE_TABLE = "create table " 
			+ TABLE_NAME + " ( " 
			+ _ID + " integer primary key autoincrement, " 
			+ TYPE + " integer not null, " 
			+ TEXT + " text not null, " 
			
			+ REPLY + " text, " 
			+ FILE + " text, " 
			
			+ "unique ( " 
			+ TEXT
			+ " ) on conflict ignore );";
	

}
