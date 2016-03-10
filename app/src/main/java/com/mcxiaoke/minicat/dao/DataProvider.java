package com.mcxiaoke.minicat.dao;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.net.Uri;
import android.provider.BaseColumns;
import android.util.Log;
import com.mcxiaoke.minicat.AppContext;
import com.mcxiaoke.minicat.dao.model.DirectMessageColumns;
import com.mcxiaoke.minicat.dao.model.IBaseColumns;
import com.mcxiaoke.minicat.dao.model.StatusColumns;
import com.mcxiaoke.minicat.dao.model.StatusUpdateInfoColumns;
import com.mcxiaoke.minicat.dao.model.UserColumns;

import java.util.List;


/**
 * @author mcxiaoke
 * @version 7.0 2012.03.19
 */
public final class DataProvider extends ContentProvider implements IBaseColumns {

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
    private static final boolean DEBUG = AppContext.DEBUG;
    private static final String TAG = DataProvider.class.getSimpleName();
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
        sUriMatcher.addURI(AUTHORITY, StatusUpdateInfoColumns.TABLE_NAME, RECORDS);
        sUriMatcher.addURI(AUTHORITY, StatusUpdateInfoColumns.TABLE_NAME + "/#",
                RECORD_ID);

    }
    private SQLiteHelper dbHelper;

    private void log(String message) {
        Log.d(TAG, message);
    }

    @Override
    public boolean onCreate() {
        dbHelper = new SQLiteHelper(getContext());
        return true;
    }

    @Override
    public Cursor query(Uri uri, String[] columns, String where,
                        String[] whereArgs, String orderBy) {
        if (AppContext.DEBUG) {
//            Log.d(TAG, "query() uri = " + uri + " where = (" + where
//                    + ") whereArgs = " + StringHelper.toString(whereArgs)
//                    + " orderBy = " + orderBy);

//            List<String> paths = uri.getPathSegments();
//            for (int i = 0; i < paths.size(); i++) {
//                Log.d(TAG,
//                        "getPathSegments() path[" + i + "] --> " + paths.get(i));
//            }

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
                return queryItemById(uri);
            case RECORD_ID:
                throw new UnsupportedOperationException("unsupported operation: "
                        + uri);
            default:
                throw new IllegalArgumentException("query() Unknown URI " + uri);
        }
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
                return StatusUpdateInfoColumns.CONTENT_TYPE;
            case RECORD_ID:
                return StatusUpdateInfoColumns.CONTENT_ITEM_TYPE;
            default:
                throw new IllegalArgumentException("getType() Unknown URI " + uri);
        }
    }

    @Override
    public Uri insert(Uri uri, ContentValues values) {

        if (DEBUG) {
//            Log.d(TAG, "insert() uri: " + uri);
//            List<String> paths = uri.getPathSegments();
//            for (int i = 0; i < paths.size(); i++) {
//                Log.d(TAG,
//                        "getPathSegments() path[" + i + "] --> " + paths.get(i));
//            }
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

    @Override
    public int bulkInsert(Uri uri, ContentValues[] values) {
        int numInserted = 0;
        String table = uri.getPathSegments().get(0);
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        db.beginTransaction();
        try {
            for (ContentValues value : values) {
//                if (AppContext.DEBUG) {
//                    Log.d(TAG, "bulkInsert() " + value);
//                }
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

    @Override
    public int delete(Uri uri, String where, String[] whereArgs) {
        if (DEBUG) {
//            Log.d(TAG, "delete() uri: " + uri + " where: " + where
//                    + " whereArgs: " + whereArgs);
//            List<String> paths = uri.getPathSegments();
//            for (int i = 0; i < paths.size(); i++) {
//                Log.d(TAG,
//                        "getPathSegments() path[" + i + "] --> " + paths.get(i));
//            }
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

    @Override
    public int update(Uri uri, ContentValues values, String where,
                      String[] whereArgs) {

        if (DEBUG) {
//            Log.d(TAG, "update() uri: " + uri + " values: " + values
//                    + " where: " + where + " whereArgs: " + whereArgs);
//            List<String> paths = uri.getPathSegments();
//            for (int i = 0; i < paths.size(); i++) {
//                Log.d(TAG,
//                        "getPathSegments() path[" + i + "] --> " + paths.get(i));
//            }
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
        if (AppContext.DEBUG) {
//            if (count > 0) {
//                log("update() result uri=" + uri + " count=" + count);
//            }
        }
        getContext().getContentResolver().notifyChange(uri, null);
        return count;
    }

    private Cursor queryWithNotify(Uri uri, Cursor cursor) {
        if (cursor == null) {
//            if (AppContext.DEBUG) {
//                log("query() uri " + uri + " failed.");
//            }
        } else {
            cursor.setNotificationUri(getContext().getContentResolver(), uri);
        }

        return cursor;
    }

    private Cursor queryCollection(Uri uri, String[] columns, String where,
                                   String[] whereArgs, String orderBy) {
        String table = uri.getPathSegments().get(0);
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        try {
            Cursor cursor = db.query(table, null, where, whereArgs, null, null,
                    orderBy);
            return queryWithNotify(uri, cursor);
        } catch (Exception ignored) {
            return null;
        }
    }

    private Cursor queryItemById(Uri uri) {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        final List<String> path = uri.getPathSegments();
        String table = path.get(0);
        String id = path.get(2);
        String selection = IBaseColumns.ID + " =? ";
        String[] selectionArgs = new String[]{id};
        Cursor cursor = db.query(table, null, selection, selectionArgs, null,
                null, null);
        return queryWithNotify(uri, cursor);
    }

    private void insertItem(Uri uri, ContentValues values) {
        if (values == null || values.size() == 0) {
            throw new NullPointerException("插入数据不能为空.");
        }
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        String table = uri.getPathSegments().get(0);
        long rowId = db.insert(table, null, values);
        getContext().getContentResolver().notifyChange(uri, null);
        if (rowId > 0) {
            // getContext().getContentResolver().notifyChange(uri, null);
            Uri resultUri = ContentUris.withAppendedId(uri, rowId);
//            if (AppContext.DEBUG) {
//                log("insert() resultUri=" + resultUri + " id="
//                        + values.getAsString(ID) + " rowId=" + rowId);
//            }
        }
    }

    private int deleteByCondition(Uri uri, String where, String[] whereArgs) {
        int count = 0;
        try {
            String table = uri.getPathSegments().get(0);
            SQLiteDatabase db = dbHelper.getWritableDatabase();
            count = db.delete(table, where, whereArgs);
        } catch (SQLiteException e) {
            e.printStackTrace();
        }
        return count;
    }

    private int deleteItemById(Uri uri) {
        List<String> path = uri.getPathSegments();
        String table = path.get(0);
        String id = path.get(2);
        String where = IBaseColumns.ID + " =? ";
        String[] whereArgs = new String[]{id};
        return dbHelper.getWritableDatabase().delete(table, where, whereArgs);
    }

    private int deleteRecordById(Uri uri) {
        String id = uri.getPathSegments().get(1);
        String table = StatusUpdateInfoColumns.TABLE_NAME;
        String where = BaseColumns._ID + " = " + id;
        return dbHelper.getWritableDatabase().delete(table, where, null);
    }

    private int updateById(Uri uri, ContentValues values) {
        List<String> path = uri.getPathSegments();
        String table = path.get(0);
        String id = path.get(2);
        return dbHelper.getWritableDatabase().update(table, values,
                IBaseColumns.ID + "=?", new String[]{id});
    }

    private int updateRecordById(Uri uri, ContentValues values) {
        List<String> path = uri.getPathSegments();
        String table = path.get(0);
        String id = path.get(2);
        return dbHelper.getWritableDatabase().update(table, values,
                BaseColumns._ID + "=?", new String[]{id});
    }

    private int updateByCondition(Uri uri, ContentValues values, String where,
                                  String[] whereArgs) {
        List<String> path = uri.getPathSegments();
        String table = path.get(0);
        return dbHelper.getWritableDatabase().update(table, values, where,
                whereArgs);
    }

}
