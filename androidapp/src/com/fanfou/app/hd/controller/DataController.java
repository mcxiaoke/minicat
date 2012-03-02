package com.fanfou.app.hd.controller;

import java.util.List;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.support.v4.content.CursorLoader;
import android.util.Log;

import com.fanfou.app.hd.App;
import com.fanfou.app.hd.dao.DataProvider;
import com.fanfou.app.hd.dao.model.BaseModel;
import com.fanfou.app.hd.dao.model.DirectMessageColumns;
import com.fanfou.app.hd.dao.model.DirectMessageModel;
import com.fanfou.app.hd.dao.model.Model;
import com.fanfou.app.hd.dao.model.RecordColumns;
import com.fanfou.app.hd.dao.model.StatusColumns;
import com.fanfou.app.hd.dao.model.UserColumns;

/**
 * @author mcxiaoke
 * @version 1.0 2012.02.16
 * @version 2.0 2012.02.24
 * @version 2.1 2012.02.28
 * 
 */
public class DataController {
	private static final String TAG=DataController.class.getSimpleName();

	private static Uri buildFriendsUri(String id) {
		return Uri.withAppendedPath(UserColumns.CONTENT_URI, "friends/" + id);
	}

	private static Uri buildFollowersUri(String id) {
		return Uri.withAppendedPath(UserColumns.CONTENT_URI, "followers/" + id);
	}

	private static Uri buildUserUri(String id) {
		return withAppendedId(UserColumns.CONTENT_URI, id);
	}

	private static Uri buildStatusUri(String id) {
		return withAppendedId(StatusColumns.CONTENT_URI, id);
	}

	private static Uri buildConversationListUri() {
		return Uri.withAppendedPath(DirectMessageColumns.CONTENT_URI,
				"conversation_list");
	}

	private static Uri buildInBoxUri() {
		return Uri.withAppendedPath(DirectMessageColumns.CONTENT_URI, "inbox");
	}

	private static Uri buildOutBoxUri() {
		return Uri.withAppendedPath(DirectMessageColumns.CONTENT_URI, "outbox");
	}

	private static Uri buildConversationUri(String id) {
		return Uri.withAppendedPath(DirectMessageColumns.CONTENT_URI,
				"conversation/" + id);
	}

	private static Uri buildDirectMessageUri(String id) {
		return withAppendedId(DirectMessageColumns.CONTENT_URI, id);
	}

	private static Uri buildRecordUri(String id) {
		return withAppendedId(RecordColumns.CONTENT_URI, id);
	}

	private static Uri withAppendedId(Uri baseUri, String id) {
		return Uri.withAppendedPath(baseUri, "id/" + id);
	}

	public static int parseInt(Cursor c, String columnName) {
		return c.getInt(c.getColumnIndex(columnName));
	}

	public static long parseLong(Cursor c, String columnName) {
		return c.getLong(c.getColumnIndex(columnName));
	}

	public static String parseString(Cursor c, String columnName) {
		return c.getString(c.getColumnIndex(columnName));
	}

	public static boolean parseBoolean(Cursor c, String columnName) {
		return c.getInt(c.getColumnIndex(columnName)) != 0;
	}

	public static ContentValues[] toContentValues(
			List<? extends BaseModel> models) {
		if (models == null || models.size() == 0) {
			return null;
		}
		int size = models.size();
		ContentValues[] values = new ContentValues[size];

		for (int i = 0; i < size; i++) {
			values[i] = models.get(i).values();
		}
		return values;
	}

	public static void clearDatabase(Context context) {
		ContentResolver cr = context.getContentResolver();
		cr.delete(StatusColumns.CONTENT_URI, null, null);
		cr.delete(UserColumns.CONTENT_URI, null, null);
		cr.delete(DirectMessageColumns.CONTENT_URI, null, null);
		cr.delete(RecordColumns.CONTENT_URI, null, null);
	}
	
	public static void clear(Context context, Uri uri){
		context.getContentResolver().delete(uri, null, null);
	}

	public static int store(Context context, List<? extends BaseModel> models) {
		if (models == null || models.size() == 0) {
			return -1;
		}
		
		if(App.DEBUG){
			Log.d(TAG, "store models.size="+models.size());
		}
		
		Uri uri = models.get(0).getContentUri();
		int result = context.getContentResolver().bulkInsert(uri,
				DataController.toContentValues(models));
		context.getContentResolver().notifyChange(uri, null, false);
		return result;
	}

	public static Uri store(Context context, Model model) {
		if (model == null) {
			return null;
		}
		return context.getContentResolver().insert(model.getContentUri(),
				model.values());
	}

	public static int update(Context context, BaseModel model,
			ContentValues values) {
		if (model == null || values == null) {
			return -1;
		}
		Uri uri = withAppendedId(model.getContentUri(), model.getId());
		return context.getContentResolver().update(uri, values, null, null);
	}

	public static int delete(Context context, Uri baseUri, String id) {
		Uri uri = withAppendedId(baseUri, id);
		return context.getContentResolver().delete(uri, null, null);
	}

	public static int delete(Context context, BaseModel model) {
		if (model == null) {
			return -1;
		}
		Uri uri = withAppendedId(model.getContentUri(), model.getId());
		return context.getContentResolver().delete(uri, null, null);
	}
	
	public static int deleteRecord(Context context, long id) {
		Uri uri = ContentUris.withAppendedId(RecordColumns.CONTENT_URI, id);
		return context.getContentResolver().delete(uri, null, null);
	}

	public static CursorLoader getConversationListLoader(Activity activity) {
		Uri uri = buildConversationListUri();
		return new CursorLoader(activity, uri, null, null, null, null);
	}

	public static CursorLoader getConversationLoader(Activity activity, String id) {
		Uri uri = buildConversationUri(id);
		String orderBy = DataProvider.ORDERBY_TIME;
		return new CursorLoader(activity, uri, null, null, null, orderBy);
	}

	public static Cursor getConversationCursor(Activity activity, String id) {
		Uri uri = buildConversationUri(id);
		String orderBy = DataProvider.ORDERBY_TIME;
		return activity.managedQuery(uri, null, null, null, orderBy);
	}
	
	public static Cursor getFriendsCursor(Context context, String[] columns, String id, String orderBy){
		Uri uri=buildFriendsUri(id);
		return context.getContentResolver().query(uri, columns, null, null, orderBy);
	}
	
	public static Cursor getFriendsCursor(Context context, String id, String orderBy){
		return getFriendsCursor(context, null, id, orderBy);
	}

}
