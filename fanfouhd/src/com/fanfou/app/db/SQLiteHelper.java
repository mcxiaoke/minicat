package com.fanfou.app.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.fanfou.app.db.Contents.AutoCompleteColumns;
import com.fanfou.app.db.Contents.DirectMessageInfo;
import com.fanfou.app.db.Contents.StatusInfo;
import com.fanfou.app.db.Contents.UserInfo;

public class SQLiteHelper extends SQLiteOpenHelper {
	public static final String TAG = "SQLiteHelper";

	public static final String DATABASE_NAME = "fanfou.db";
	public static final int DATABASE_VERSION = 1;

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
		db.execSQL(AutoCompleteColumns.CREATE_TABLE);

	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		db.execSQL("DROP TABLE IF EXISTS " + StatusInfo.TABLE_NAME);
		db.execSQL("DROP TABLE IF EXISTS " + UserInfo.TABLE_NAME);
		db.execSQL("DROP TABLE IF EXISTS " + DirectMessageInfo.TABLE_NAME);
		db.execSQL("DROP TABLE IF EXISTS " + AutoCompleteColumns.TABLE_NAME);
		onCreate(db);

	}

}