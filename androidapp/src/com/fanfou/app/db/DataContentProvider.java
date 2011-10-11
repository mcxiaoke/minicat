package com.fanfou.app.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteOpenHelper;
import android.net.Uri;

/**
 * @author mcxiaoke
 * @version 1.0 2011.10.09
 * 
 */
public class DataContentProvider extends SQLiteContentProvider {

	@Override
	protected SQLiteOpenHelper getDatabaseHelper(Context context) {
		return null;
	}

	@Override
	protected Uri insertInTransaction(Uri uri, ContentValues values) {
		return null;
	}

	@Override
	protected int updateInTransaction(Uri uri, ContentValues values,
			String selection, String[] selectionArgs) {
		return 0;
	}

	@Override
	protected int deleteInTransaction(Uri uri, String selection,
			String[] selectionArgs) {
		return 0;
	}

	@Override
	protected void notifyChange() {
	}

	@Override
	public Cursor query(Uri uri, String[] projection, String selection,
			String[] selectionArgs, String sortOrder) {
		return null;
	}

	@Override
	public String getType(Uri uri) {
		return null;
	}

}
