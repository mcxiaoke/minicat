package com.fanfou.app.hd.dao;

import java.util.List;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;
import android.util.Log;

import com.fanfou.app.hd.App;
import com.fanfou.app.hd.dao.model.DirectMessageColumns;
import com.fanfou.app.hd.dao.model.IBaseColumns;
import com.fanfou.app.hd.dao.model.RecordColumns;
import com.fanfou.app.hd.dao.model.StatusColumns;
import com.fanfou.app.hd.dao.model.UserColumns;
import com.fanfou.app.hd.dao.model.UserModel;
import com.fanfou.app.hd.util.StringHelper;

/**
 * @author mcxiaoke
 * @version 1.0 2011.05.24
 * @version 1.5 2011.05.25
 * @version 1.6 2011.06.04
 * @version 1.7 2011.07.14
 * @version 1.8 2011.07.22
 * @version 1.9 2011.10.09
 * @version 2.0 2011.10.25
 * @version 2.5 2011.10.26
 * @version 3.0 2011.10.28
 * @version 3.1 2011.10.30
 * @version 3.2 2011.11.07
 * @version 3.5 2011.11.10
 * @version 3.6 2011.11.11
 * @version 3.7 2011.11.15
 * @version 3.8 2011.11.21
 * @version 3.9 2011.11.23
 * @version 4.0 2011.12.19
 * @version 5.0 2012.02.16
 * @version 5.1 2012.02.17
 * @version 6.0 2012.02.21
 * @version 6.1 2012.02.24
 * @version 6.2 2012.02.28
 * @version 6.3 2012.03.02
 * @version 7.0 2012.03.19
 * 
 */
public final class DataProvider extends ContentProvider implements IBaseColumns {

	private static final boolean DEBUG = App.DEBUG;

	private static final String TAG = DataProvider.class.getSimpleName();

	private void log(String message) {
		Log.d(TAG, message);
	}

	private SQLiteHelper dbHelper;

	public static final String ORDERBY_TIME = IBaseColumns.TIME;
	public static final String ORDERBY_TIME_DESC = IBaseColumns.TIME + " DESC";
	public static final String ORDERBY_RAWID = IBaseColumns.RAWID;
	public static final String ORDERBY_RAWID_DESC = IBaseColumns.RAWID
			+ " DESC";

	public static final int USERS = 1;// 查询全部用户信息，可附加条件参数
	public static final int USER_ID = 5; // 根据ID查询单个用户

	public static final int STATUSES = 21;
	public static final int STATUS_ID = 22;

	public static final int MESSAGES = 41;// 所有私信
	public static final int MESSAGE_ID = 46;

	public static final int RECORDS = 61;
	public static final int RECORD_ID = 62;

	private static final UriMatcher sUriMatcher;

	static {
		// no match
		sUriMatcher = new UriMatcher(UriMatcher.NO_MATCH);

		// user list
		sUriMatcher.addURI(AUTHORITY, UserColumns.TABLE_NAME, USERS);
		sUriMatcher
				.addURI(AUTHORITY, UserColumns.TABLE_NAME + "/id/*", USER_ID);

		// timeline
		sUriMatcher.addURI(AUTHORITY, StatusColumns.TABLE_NAME, STATUSES);
		sUriMatcher.addURI(AUTHORITY, StatusColumns.TABLE_NAME + "/id/*",
				STATUS_ID);

		// direct message
		sUriMatcher
				.addURI(AUTHORITY, DirectMessageColumns.TABLE_NAME, MESSAGES);

		sUriMatcher.addURI(AUTHORITY,
				DirectMessageColumns.TABLE_NAME + "/id/*", MESSAGE_ID);

		// record
		sUriMatcher.addURI(AUTHORITY, RecordColumns.TABLE_NAME, RECORDS);
		sUriMatcher.addURI(AUTHORITY, RecordColumns.TABLE_NAME + "/#",
				RECORD_ID);

	}

	@Override
	public boolean onCreate() {
		dbHelper = new SQLiteHelper(getContext());
		return true;
	}

	@Override
	public String getType(Uri uri) {
		switch (sUriMatcher.match(uri)) {
		case USERS:
			return UserColumns.CONTENT_TYPE;
		case USER_ID:
			return UserColumns.CONTENT_ITEM_TYPE;
		case STATUSES:
			return StatusColumns.CONTENT_TYPE;
		case STATUS_ID:
			return StatusColumns.CONTENT_ITEM_TYPE;
		case MESSAGES:
			return DirectMessageColumns.CONTENT_TYPE;
		case MESSAGE_ID:
			return DirectMessageColumns.CONTENT_ITEM_TYPE;
		case RECORDS:
			return RecordColumns.CONTENT_TYPE;
		case RECORD_ID:
			return RecordColumns.CONTENT_ITEM_TYPE;
		default:
			throw new IllegalArgumentException("getType() Unknown URI " + uri);
		}
	}

	private Cursor queryWithNotify(Uri uri, Cursor cursor) {
		if (cursor == null) {
			if (App.DEBUG) {
				log("query() uri " + uri + " failed.");
			}
		} else {
			cursor.setNotificationUri(getContext().getContentResolver(), uri);
		}

		return cursor;
	}

	private Cursor queryCollection(Uri uri, String[] columns, String where,
			String[] whereArgs, String orderBy) {
		String table = uri.getPathSegments().get(0);
		SQLiteDatabase db = dbHelper.getReadableDatabase();
		Cursor cursor = db.query(table, null, where, whereArgs, null, null,
				orderBy);
		return queryWithNotify(uri, cursor);
	}

	private Cursor queryItem(Uri uri) {
		SQLiteDatabase db = dbHelper.getReadableDatabase();
		SQLiteQueryBuilder qb = new SQLiteQueryBuilder();
		final List<String> path = uri.getPathSegments();
		String table = path.get(0);
		String id = path.get(2);
		String selection = IBaseColumns.ID + " =? ";
		String[] selectionArgs = new String[] { id };
		qb.setTables(table);
		Cursor cursor = qb.query(db, null, selection, selectionArgs, null,
				null, null);
		return queryWithNotify(uri, cursor);
	}

	@Override
	public Cursor query(Uri uri, String[] columns, String where,
			String[] whereArgs, String orderBy) {
		if (App.DEBUG) {
			Log.d(TAG, "query() uri = " + uri + " where = (" + where
					+ ") whereArgs = " + StringHelper.toString(whereArgs)
					+ " orderBy = " + orderBy);

			List<String> paths = uri.getPathSegments();
			for (int i = 0; i < paths.size(); i++) {
				Log.d(TAG,
						"getPathSegments() path[" + i + "] --> " + paths.get(i));
			}

		}
		switch (sUriMatcher.match(uri)) {
		case USERS:
		case STATUSES:
		case MESSAGES:
		case RECORDS:
			return queryCollection(uri, columns, where, whereArgs, orderBy);
		case USER_ID:
		case STATUS_ID:
		case MESSAGE_ID:
			return queryItem(uri);
		case RECORD_ID:
			throw new UnsupportedOperationException("unsupported operation: "
					+ uri);
		default:
			throw new IllegalArgumentException("query() Unknown URI " + uri);
		}
	}

	@Override
	public Uri insert(Uri uri, ContentValues values) {

		if (DEBUG) {
			Log.d(TAG, "insert() uri: " + uri);
			List<String> paths = uri.getPathSegments();
			for (int i = 0; i < paths.size(); i++) {
				Log.d(TAG,
						"getPathSegments() path[" + i + "] --> " + paths.get(i));
			}
		}

		switch (sUriMatcher.match(uri)) {
		case USERS:
		case STATUSES:
		case MESSAGES:
		case RECORDS:
			insertItem(uri, values);
			return uri;
		case USER_ID:
		case STATUS_ID:
		case MESSAGE_ID:
		case RECORD_ID:
			throw new UnsupportedOperationException("Cannot insert URI: " + uri);
		default:
			throw new IllegalArgumentException("insert() Unknown URI " + uri);
		}
	}

	private void insertItem(Uri uri, ContentValues values) {
		if (values == null || values.size() == 0) {
			throw new NullPointerException("插入数据不能为空.");
		}
		SQLiteDatabase db = dbHelper.getWritableDatabase();
		String table = uri.getPathSegments().get(0);
		long rowId = db.insert(table, null, values);
		if (rowId > 0) {
//			getContext().getContentResolver().notifyChange(uri, null);
			Uri resultUri = ContentUris.withAppendedId(uri, rowId);
			if (App.DEBUG) {
				log("insert() resultUri=" + resultUri + " id="
						+ values.getAsString(ID) + " rowId=" + rowId);
			}
		}
	}

	@Override
	public int bulkInsert(Uri uri, ContentValues[] values) {
		int numInserted = 0;
		String table = uri.getPathSegments().get(0);
		SQLiteDatabase db = dbHelper.getWritableDatabase();
		db.beginTransaction();
		try {
			for (ContentValues value : values) {
				long id = db.insert(table, null, value);
				if (id > 0) {
					++numInserted;
				}
			}
			db.setTransactionSuccessful();
			getContext().getContentResolver().notifyChange(uri, null);
		} finally {
			db.endTransaction();
		}
		return numInserted;
	}

	private int deleteByCondition(Uri uri, String where, String[] whereArgs) {
		String table = uri.getPathSegments().get(0);
		SQLiteDatabase db = dbHelper.getWritableDatabase();
		int count = db.delete(table, where, whereArgs);
		return count;
	}

	private int deleteItemById(Uri uri) {
		List<String> path = uri.getPathSegments();
		String table = path.get(0);
		String id = path.get(2);
		String where = IBaseColumns.ID + " =? ";
		String[] whereArgs = new String[] { id };
		return dbHelper.getWritableDatabase().delete(table, where, whereArgs);
	}

	private int deleteRecordById(Uri uri) {
		String id = uri.getPathSegments().get(1);
		String table = RecordColumns.TABLE_NAME;
		String where = RecordColumns._ID + " = " + id;
		return dbHelper.getWritableDatabase().delete(table, where, null);
	}

	@Override
	public int delete(Uri uri, String where, String[] whereArgs) {
		if (DEBUG) {
			Log.d(TAG, "delete() uri: " + uri + " where: " + where
					+ " whereArgs: " + whereArgs);
			List<String> paths = uri.getPathSegments();
			for (int i = 0; i < paths.size(); i++) {
				Log.d(TAG,
						"getPathSegments() path[" + i + "] --> " + paths.get(i));
			}
		}
		int count;
		switch (sUriMatcher.match(uri)) {
		case USERS:
		case STATUSES:
		case RECORDS:
		case MESSAGES:
			count = deleteByCondition(uri, where, whereArgs);
			break;
		case USER_ID:
		case STATUS_ID:
		case MESSAGE_ID:
			count = deleteItemById(uri);
			break;
		case RECORD_ID:
			count = deleteRecordById(uri);
			break;
		default:
			throw new IllegalArgumentException("delete() Unknown URI " + uri);
		}
		getContext().getContentResolver().notifyChange(uri, null);
		return count;
	}

	private int updateById(Uri uri, ContentValues values) {
		List<String> path = uri.getPathSegments();
		String table = path.get(0);
		String id = path.get(2);
		return dbHelper.getWritableDatabase().update(table, values,
				UserColumns.ID + "=?", new String[] { id });
	}

	private int updateRecordById(Uri uri, ContentValues values) {
		List<String> path = uri.getPathSegments();
		String table = path.get(0);
		String id = path.get(2);
		return dbHelper.getWritableDatabase().update(table, values,
				UserColumns._ID + "=?", new String[] { id });
	}

	private int updateByCondition(Uri uri, ContentValues values, String where,
			String[] whereArgs) {
		List<String> path = uri.getPathSegments();
		String table = path.get(0);
		return dbHelper.getWritableDatabase().update(table, values, where,
				whereArgs);
	}

	@Override
	public int update(Uri uri, ContentValues values, String where,
			String[] whereArgs) {

		if (DEBUG) {
			Log.d(TAG, "update() uri: " + uri + " values: " + values
					+ " where: " + where + " whereArgs: " + whereArgs);
			List<String> paths = uri.getPathSegments();
			for (int i = 0; i < paths.size(); i++) {
				Log.d(TAG,
						"getPathSegments() path[" + i + "] --> " + paths.get(i));
			}
		}
		int count;
		switch (sUriMatcher.match(uri)) {
		case USER_ID:
		case STATUS_ID:
		case MESSAGE_ID:
			count = updateById(uri, values);
			break;
		case RECORD_ID:
			count = updateRecordById(uri, values);
			break;
		case USERS:
		case STATUSES:
		case MESSAGES:
		case RECORDS:
			count = updateByCondition(uri, values, where, whereArgs);
			break;
		default:
			throw new IllegalArgumentException("update() Unknown URI " + uri);
		}
		if (App.DEBUG) {
			if (count > 0) {
				log("update() result uri=" + uri + " count=" + count);
			}
		}
		getContext().getContentResolver().notifyChange(uri, null);
		return count;
	}

}
