package com.fanfou.app.hd.dao;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteOpenHelper;
import android.net.Uri;

/**
 * @author mcxiaoke
 * @version 1.0 2012.02.16
 *
 */
public class DataContentProvider extends SQLiteContentProvider {

	@Override
	protected SQLiteOpenHelper getDatabaseHelper(Context context) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected Uri insertInTransaction(Uri uri, ContentValues values) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected int updateInTransaction(Uri uri, ContentValues values,
			String selection, String[] selectionArgs) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	protected int deleteInTransaction(Uri uri, String selection,
			String[] selectionArgs) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	protected void notifyChange() {
		// TODO Auto-generated method stub

	}

	@Override
	public Cursor query(Uri uri, String[] projection, String selection,
			String[] selectionArgs, String sortOrder) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getType(Uri uri) {
		// TODO Auto-generated method stub
		return null;
	}

}
