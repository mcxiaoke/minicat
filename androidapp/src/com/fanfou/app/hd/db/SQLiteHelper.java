package com.fanfou.app.hd.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.fanfou.app.hd.db.Contents.DirectMessageInfo;
import com.fanfou.app.hd.db.Contents.DraftInfo;
import com.fanfou.app.hd.db.Contents.StatusInfo;
import com.fanfou.app.hd.db.Contents.UserInfo;

/**
 * @author mcxiaoke
 * @version 1.0 2011.06.20
 * @version 2.0 2011.10.21
 * @version 3.0 2011.10.26
 * @version 3.1 2011.10.27
 * @version 3.2 2011.10.28
 * @version 3.3 2011.11.07
 * @version 3.4 2011.11.10
 * @version 3.5 2011.11.18
 * @version 3.6 2011.11.23
 * 
 */
public class SQLiteHelper extends SQLiteOpenHelper {
	public static final String TAG = "SQLiteHelper";

	public static final String DATABASE_NAME = "fanfou.db";
	public static final int DATABASE_VERSION = 15;

	/**
	 * @param context
	 * @param name
	 * @param factory
	 * @param version
	 */
	public SQLiteHelper(Context context) {
		super(context, DATABASE_NAME, null, DATABASE_VERSION);
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		db.execSQL(StatusInfo.CREATE_TABLE);
		db.execSQL(UserInfo.CREATE_TABLE);
		db.execSQL(DirectMessageInfo.CREATE_TABLE);
		db.execSQL(DraftInfo.CREATE_TABLE);
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		db.execSQL("DROP TABLE IF EXISTS " + StatusInfo.TABLE_NAME);
		db.execSQL("DROP TABLE IF EXISTS " + UserInfo.TABLE_NAME);
		db.execSQL("DROP TABLE IF EXISTS " + DirectMessageInfo.TABLE_NAME);
		db.execSQL("DROP TABLE IF EXISTS " + DraftInfo.TABLE_NAME);
		onCreate(db);

	}

}