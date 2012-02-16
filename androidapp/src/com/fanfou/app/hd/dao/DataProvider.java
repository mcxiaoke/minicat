package com.fanfou.app.hd.dao;

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
import com.fanfou.app.hd.service.Constants;
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
 * 
 */
public class DataProvider extends ContentProvider implements IBaseColumns {

	private static final String TAG = DataProvider.class.getSimpleName();

	private void log(String message) {
		Log.d(TAG, message);
	}

	private SQLiteHelper dbHelper;

	public static final String ORDERBY_DATE = IBaseColumns.TIME;
	public static final String ORDERBY_DATE_DESC = IBaseColumns.TIME + " DESC";

	public static final int USERS = 1;// 查询全部用户信息，可附加条件参数
	public static final int USERS_FRIENDS = 2;// 查询某个用户的好友， //friends/userId
	public static final int USERS_FOLLOWERS = 3;// 查询某个用户的关注者 //followers/userId
	public static final int USERS_SEARCH = 4; // 搜索用户，未实现
	public static final int USER_ID = 5; // 根据ID查询单个用户

	public static final int STATUSES = 21;
	public static final int STATUS_ID = 22;

	public static final int MESSAGES_CONVERSATION = 41;
	public static final int MESSAGES_INBOX = 42;// 每个人的私信对话列表
	public static final int MESSAGES_OUTBOX = 43;
	public static final int MESSAGES_THREAD = 44;// 对话列表，每个人最新的一条，收件箱为准
	public static final int MESSAGE_ID = 45;

	public static final int RECORDS = 61;
	public static final int RECORD_ID = 62;

	private static final UriMatcher sUriMatcher;

	static {
		sUriMatcher = new UriMatcher(UriMatcher.NO_MATCH);

		sUriMatcher.addURI(AUTHORITY, UserColumns.URI_PATH, USERS);
		sUriMatcher.addURI(AUTHORITY, UserColumns.URI_PATH + "/friends/*",
				USERS_FRIENDS);
		sUriMatcher.addURI(AUTHORITY, UserColumns.URI_PATH + "/followers/*",
				USERS_FOLLOWERS);
		sUriMatcher.addURI(AUTHORITY, UserColumns.URI_PATH + "/search/*",
				USERS_SEARCH);
		sUriMatcher.addURI(AUTHORITY, UserColumns.URI_PATH + "/id/*", USER_ID);

		sUriMatcher.addURI(AUTHORITY, StatusColumns.URI_PATH, STATUSES);
		sUriMatcher.addURI(AUTHORITY, StatusColumns.URI_PATH + "/id/*",
				STATUS_ID);

		sUriMatcher.addURI(AUTHORITY, DirectMessageColumns.URI_PATH
				+ "/conversation", MESSAGES_CONVERSATION);
		sUriMatcher.addURI(AUTHORITY, DirectMessageColumns.URI_PATH + "/id/*",
				MESSAGE_ID);

		sUriMatcher.addURI(AUTHORITY, DirectMessageColumns.URI_PATH + "/inbox",
				MESSAGES_INBOX);
		sUriMatcher.addURI(AUTHORITY,
				DirectMessageColumns.URI_PATH + "/outbox", MESSAGES_OUTBOX);
		sUriMatcher.addURI(AUTHORITY, DirectMessageColumns.URI_PATH
				+ "/thread/*", MESSAGES_THREAD);

		sUriMatcher.addURI(AUTHORITY, RecordColumns.URI_PATH, RECORDS);
		sUriMatcher.addURI(AUTHORITY, RecordColumns.URI_PATH + "/#", RECORD_ID);

	}

	public static Uri userFriendsUriForId(String id) {
		return Uri.withAppendedPath(UserColumns.CONTENT_URI, "friends/" + id);
	}

	public static Uri userFollowersUriForId(String id) {
		return Uri.withAppendedPath(UserColumns.CONTENT_URI, "followers/" + id);
	}

	public static Uri userUriForId(String id) {
		return Uri.withAppendedPath(UserColumns.CONTENT_URI, "id/" + id);
	}

	public static Uri statusUriForId(String id) {
		return Uri.withAppendedPath(StatusColumns.CONTENT_URI, "id/" + id);
	}

	public static Uri dmConversationUri() {
		return Uri.withAppendedPath(DirectMessageColumns.CONTENT_URI,
				"conversation");
	}

	public static Uri dmInboxUri() {
		return Uri.withAppendedPath(DirectMessageColumns.CONTENT_URI, "inbox");
	}

	public static Uri dmOutboxUri() {
		return Uri.withAppendedPath(DirectMessageColumns.CONTENT_URI, "outbox");
	}

	public static Uri dmThreadUriForId(String id) {
		return Uri.withAppendedPath(DirectMessageColumns.CONTENT_URI, "thread/"
				+ id);
	}

	public static Uri dmUriForId(String id) {
		return Uri.withAppendedPath(DirectMessageColumns.CONTENT_URI, "id/"
				+ id);
	}

	public static Uri recordUriForId(int id) {
		return ContentUris.withAppendedId(RecordColumns.CONTENT_URI, id);
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
		case USERS_FRIENDS:
		case USERS_FOLLOWERS:
		case USERS_SEARCH:
			return UserColumns.CONTENT_TYPE;
		case USER_ID:
			return UserColumns.CONTENT_ITEM_TYPE;
		case STATUSES:
			return StatusColumns.CONTENT_TYPE;
		case STATUS_ID:
			return StatusColumns.CONTENT_ITEM_TYPE;
		case MESSAGES_CONVERSATION:
		case MESSAGES_INBOX:
		case MESSAGES_OUTBOX:
		case MESSAGES_THREAD:
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

	@Override
	public Cursor query(Uri uri, String[] columns, String where,
			String[] whereArgs, String orderBy) {
		if (App.DEBUG) {
			log("query() uri = " + uri + " where = (" + where
					+ ") whereArgs = " + StringHelper.toString(whereArgs)
					+ " orderBy = " + orderBy);
		}
		SQLiteDatabase db = dbHelper.getWritableDatabase();
		SQLiteQueryBuilder qb = new SQLiteQueryBuilder();
		String order = orderBy;
		String selection = where;
		String[] selectionArgs = whereArgs;
		switch (sUriMatcher.match(uri)) {
		case USERS:
			qb.setTables(UserColumns.TABLE_NAME);
			break;
		case USERS_FRIENDS:
		case USERS_FOLLOWERS:
			// TODO
			break;
		case USER_ID:
			qb.setTables(UserColumns.TABLE_NAME);
			qb.appendWhere(ID + "=");
			qb.appendWhere("'" + uri.getPathSegments().get(2) + "'");
			break;
		case STATUSES:
			qb.setTables(StatusColumns.TABLE_NAME);
			// if (order == null) {
			// order = ORDERBY_DATE_DESC;
			// }
			break;
		case STATUS_ID:
			qb.setTables(StatusColumns.TABLE_NAME);
			qb.appendWhere(ID + "=");
			qb.appendWhere("'" + uri.getPathSegments().get(2) + "'");
			break;

		case MESSAGES_INBOX:
		case MESSAGES_OUTBOX:
		case MESSAGES_THREAD:
			qb.setTables(DirectMessageColumns.TABLE_NAME);
			if (order == null) {
				order = ORDERBY_DATE_DESC;
			}
			break;
		case MESSAGES_CONVERSATION:
			// TODO
			// String sql =
			// "select * from message m1 where type= 21 and created_at =
			// (select max(created_at) from message m2 where type= 21 and
			// m1.sender_id = m2.sender_id group by (sender_id))
			// order by created_at desc;";
			String typeStr = DirectMessageColumns.TYPE + "= "
					+ Constants.TYPE_DIRECT_MESSAGES_INBOX;
			String orderStr = " order by " + DirectMessageColumns.TIME
					+ " desc";
			String subQuery = "(select max(" + DirectMessageColumns.TIME
					+ ") from " + DirectMessageColumns.TABLE_NAME
					+ " m2 where " + typeStr
					+ " and m1.sender_id = m2.sender_id group by ("
					+ DirectMessageColumns.SENDER_ID + "))";
			String querySql = "select * from "
					+ DirectMessageColumns.TABLE_NAME + " m1 where " + typeStr
					+ " and " + DirectMessageColumns.TIME + " = " + subQuery
					+ orderStr + " ;";
			Cursor cursor = db.rawQuery(querySql, null);
			if (App.DEBUG) {
				log("query() uri MESSAGE_LIST " + uri + " cursor=" + cursor);
			}
			if (cursor == null) {
				if (App.DEBUG) {
					log("query() uri MESSAGE_LIST " + uri + " failed.");
				}
			} else {
				cursor.setNotificationUri(getContext().getContentResolver(),
						uri);
			}
			return cursor;
			// break;
		case MESSAGE_ID:
			qb.setTables(DirectMessageColumns.TABLE_NAME);
			qb.appendWhere(DirectMessageColumns._ID + "=");
			qb.appendWhere(uri.getPathSegments().get(2));
			break;
		case RECORDS:
			qb.setTables(RecordColumns.TABLE_NAME);
			if (order == null) {
				order = ORDERBY_DATE_DESC;
			}
			break;
		case RECORD_ID:
			throw new UnsupportedOperationException("unsupported operation: "
					+ uri);
			// break;
		default:
			throw new IllegalArgumentException("query() Unknown URI " + uri);
		}

		Cursor c = qb.query(db, columns, selection, selectionArgs, null, null,
				order);

		if (c == null) {
			if (App.DEBUG) {
				log("query() uri " + uri + " failed.");
			}
		} else {
			c.setNotificationUri(getContext().getContentResolver(), uri);
		}

		return c;
	}

	@Override
	public Uri insert(Uri uri, ContentValues values) {
		if (values == null || values.size() == 0) {
			throw new IllegalArgumentException("插入数据不能为空.");
		}

		SQLiteDatabase db = dbHelper.getWritableDatabase();
		String table;
		Uri contentUri;
		switch (sUriMatcher.match(uri)) {
		case USERS:
		case USERS_FRIENDS:
		case USERS_FOLLOWERS:
			table = UserColumns.TABLE_NAME;
			contentUri = UserColumns.CONTENT_URI;
			break;
		case STATUSES:
			table = StatusColumns.TABLE_NAME;
			contentUri = StatusColumns.CONTENT_URI;
			break;
		case MESSAGES_INBOX:
		case MESSAGES_OUTBOX:
		case MESSAGES_THREAD:
			table = DirectMessageColumns.TABLE_NAME;
			contentUri = DirectMessageColumns.CONTENT_URI;
			break;
		case RECORDS:
			table = RecordColumns.TABLE_NAME;
			contentUri = RecordColumns.CONTENT_URI;
			break;
		case USERS_SEARCH:
		case USER_ID:
		case STATUS_ID:
		case MESSAGE_ID:
		case RECORD_ID:
			throw new UnsupportedOperationException("Cannot insert URI: " + uri);
		default:
			throw new IllegalArgumentException("insert() Unknown URI " + uri);
		}

		long rowId = db.insert(table, null, values);
		if (App.DEBUG) {
			log("insert() uri=" + uri.toString() + " id="
					+ values.getAsString(ID) + " rowId=" + rowId);
		}
		getContext().getContentResolver().notifyChange(uri, null);
		return uri;
	}

	@Override
	public int delete(Uri uri, String where, String[] whereArgs) {
		SQLiteDatabase db = dbHelper.getWritableDatabase();
		int count;
		String id;
		switch (sUriMatcher.match(uri)) {
		case USERS:
			count = db.delete(UserColumns.TABLE_NAME, where, whereArgs);
			break;
		case USERS_FRIENDS:
		case USERS_FOLLOWERS:
			// TODO
			count = db.delete(UserColumns.TABLE_NAME, TYPE + "=?",
					new String[] { uri.getPathSegments().get(2) });
		case USER_ID:
			id = uri.getPathSegments().get(2);
			count = db.delete(UserColumns.TABLE_NAME, ID + "=?",
					new String[] { id });
			break;
		case STATUSES:
			count = db.delete(StatusColumns.TABLE_NAME, where, whereArgs);
			break;
		case STATUS_ID:
			id = uri.getPathSegments().get(2);
			count = db.delete(StatusColumns.TABLE_NAME, ID + "=?",
					new String[] { id });
			break;
		case MESSAGES_CONVERSATION:
		case MESSAGES_INBOX:
		case MESSAGES_OUTBOX:
		case MESSAGES_THREAD:
			count = db
					.delete(DirectMessageColumns.TABLE_NAME, where, whereArgs);
			break;
		case MESSAGE_ID:
			id = uri.getPathSegments().get(2);
			count = db.delete(DirectMessageColumns.TABLE_NAME,
					DirectMessageColumns.ID + "=?", new String[] { id });
			break;
		case RECORDS:
			count = db.delete(RecordColumns.TABLE_NAME, where, whereArgs);
			break;
		case RECORD_ID:
			id = uri.getPathSegments().get(1);
			count = db.delete(RecordColumns.TABLE_NAME,
					RecordColumns.ID + "=?", new String[] { id });
			break;
		default:
			throw new IllegalArgumentException("delete() Unknown URI " + uri);
		}
		getContext().getContentResolver().notifyChange(uri, null);
		return count;
	}

	@Override
	public int update(Uri uri, ContentValues values, String where,
			String[] whereArgs) {
		// log("update() uri = " + uri + " where= (" + where + ") whereArgs = "
		// + StringHelper.toString(whereArgs));
		SQLiteDatabase db = dbHelper.getWritableDatabase();
		int count;
		String id;
		switch (sUriMatcher.match(uri)) {
		case USER_ID:
			id = uri.getPathSegments().get(2);
			count = db.update(UserColumns.TABLE_NAME, values, UserColumns.ID
					+ "=?", new String[] { id });
			break;
		case STATUS_ID:
			id = uri.getPathSegments().get(2);
			count = db.update(StatusColumns.TABLE_NAME, values,
					StatusColumns.ID + "=?", new String[] { id });
			// count = db.update(StatusInfo.TABLE_NAME, values,
			// StatusInfo.ID
			// + "="
			// + statusId
			// + (!TextUtils.isEmpty(where) ? " AND (" + where
			// + ')' : ""), whereArgs);
			break;
		case MESSAGE_ID:
			id = uri.getPathSegments().get(2);
			count = db.update(DirectMessageColumns.TABLE_NAME, values,
					DirectMessageColumns.ID + "=?", new String[] { id });
			break;
		case USERS:
			id = "";
			count = db.update(UserColumns.TABLE_NAME, values, where, whereArgs);
			break;
		case USERS_FRIENDS:
		case USERS_FOLLOWERS:
			// TODO
			id = "";
			count = db.update(UserColumns.TABLE_NAME, values, UserColumns.TYPE
					+ "=?", new String[] { uri.getPathSegments().get(2) });
			break;
		case STATUSES:
			id = "";
			count = db.update(StatusColumns.TABLE_NAME, values, where,
					whereArgs);
			break;
		// case PUBLIC:
		// id = "";
		// count = db.update(StatusInfo.PUBLIC_TABLE_NAME, values, where,
		// whereArgs);
		// break;
		case MESSAGES_CONVERSATION:
		case MESSAGES_INBOX:
		case MESSAGES_OUTBOX:
		case MESSAGES_THREAD:
			id = "";
			count = db.update(DirectMessageColumns.TABLE_NAME, values, where,
					whereArgs);
			break;
		case USERS_SEARCH:
		case RECORDS:
		case RECORD_ID:
			throw new UnsupportedOperationException(
					"unsupported update action: " + uri);
		default:
			throw new IllegalArgumentException("update() Unknown URI " + uri);
		}
		if (App.DEBUG) {
			if (count > 0) {
				log("update() result uri=" + uri + " id=" + id + " count="
						+ count);
			}
		}
		getContext().getContentResolver().notifyChange(uri, null);
		return count;
	}

}
