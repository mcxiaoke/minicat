package com.fanfou.app.api;

import java.util.Date;

import com.fanfou.app.db.Contents.DraftInfo;

import android.content.ContentValues;
import android.database.Cursor;
import android.provider.BaseColumns;
import android.util.Log;

/**
 * @author mcxiaoke
 * @version 1.0 2011.10
 * .26
 *
 */
public class Draft implements Storable<Draft> {
	
	public static final int TYPE_NONE=0;
	public static final int ID_NONE=0;

	private static final long serialVersionUID = 8363607193025710949L;

	public static final String tag = Draft.class.getSimpleName();

	private static void log(String message) {
		Log.d(tag, message);
	}

	public int id;
	public String ownerId;
	public String text;
	public Date createdAt;
	public int type;
	public String replyTo;
	public String filePath;
	
	public static Draft parse(Cursor c){
		if(c==null){
			return null;
		}
		Draft d=new Draft();
		d.id=Parser.parseInt(c, BaseColumns._ID);
		d.ownerId=Parser.parseString(c, DraftInfo.OWNER_ID);
		d.text=Parser.parseString(c, DraftInfo.TEXT);
		d.createdAt=Parser.parseDate(c, DraftInfo.CREATED_AT);
		d.type=Parser.parseInt(c, DraftInfo.TYPE);
		d.replyTo=Parser.parseString(c, DraftInfo.REPLY_TO);
		d.filePath=Parser.parseString(c, DraftInfo.FILE_PATH);
		return d;
	}

	@Override
	public int compareTo(Draft another) {
		return createdAt.compareTo(another.createdAt);
	}

	@Override
	public ContentValues toContentValues() {
		ContentValues cv = new ContentValues();
		cv.put(DraftInfo.OWNER_ID, ownerId);
		cv.put(DraftInfo.TEXT, text);
		cv.put(DraftInfo.CREATED_AT, new Date().getTime());
		cv.put(DraftInfo.TYPE, type);
		cv.put(DraftInfo.REPLY_TO, replyTo);
		cv.put(DraftInfo.FILE_PATH, filePath);
		return cv;
	}

}
