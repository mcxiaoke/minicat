package com.mcxiaoke.minicat.controller;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.CursorLoader;
import android.content.Loader;
import android.database.Cursor;
import android.net.Uri;
import android.provider.BaseColumns;
import android.text.TextUtils;
import android.util.Log;
import com.mcxiaoke.minicat.AppContext;
import com.mcxiaoke.minicat.dao.DataProvider;
import com.mcxiaoke.minicat.dao.model.BaseModel;
import com.mcxiaoke.minicat.dao.model.DirectMessageColumns;
import com.mcxiaoke.minicat.dao.model.DirectMessageModel;
import com.mcxiaoke.minicat.dao.model.IBaseColumns;
import com.mcxiaoke.minicat.dao.model.Model;
import com.mcxiaoke.minicat.dao.model.StatusColumns;
import com.mcxiaoke.minicat.dao.model.StatusModel;
import com.mcxiaoke.minicat.dao.model.StatusUpdateInfoColumns;
import com.mcxiaoke.minicat.dao.model.UserColumns;
import com.mcxiaoke.minicat.dao.model.UserModel;

import java.util.ArrayList;
import java.util.List;


/**
 * @author mcxiaoke
 * @version 3.1 2012.03.26
 */
public class DataController {
    private static final String TAG = DataController.class.getSimpleName();

    private static Uri withAppendedId(Uri baseUri, String id) {
        return Uri.withAppendedPath(baseUri, "id/" + id);
    }

    public static int parseInt(Cursor c, String columnName) {
        return c.getInt(c.getColumnIndexOrThrow(columnName));
    }

    public static long parseLong(Cursor c, String columnName) {
        return c.getLong(c.getColumnIndexOrThrow(columnName));
    }

    public static String parseString(Cursor c, String columnName) {
        return c.getString(c.getColumnIndexOrThrow(columnName));
    }

    public static boolean parseBoolean(Cursor c, String columnName) {
        return c.getInt(c.getColumnIndexOrThrow(columnName)) != 0;
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
        cr.delete(StatusUpdateInfoColumns.CONTENT_URI, null, null);
    }

    public static void clear(Context context, Uri uri) {
        context.getContentResolver().delete(uri, null, null);
    }

    public static int storeStatusesWithUsers(Context context,
                                             List<StatusModel> statuses) {
        if (statuses == null || statuses.size() == 0) {
            return -1;
        }

        int size = statuses.size();
        List<UserModel> users = new ArrayList<UserModel>(size);
        for (StatusModel status : statuses) {
            users.add(status.getUser());
        }

        store(context, statuses);
        return store(context, users);
    }

    public static int store(Context context, List<? extends BaseModel> models) {
        if (models == null || models.size() == 0) {
            return -1;
        }

        if (AppContext.DEBUG) {
            Log.d(TAG, "store models.size=" + models.size() + " table="
                    + models.get(0).getContentUri());
        }

        Uri uri = models.get(0).getContentUri();
        return context.getContentResolver().bulkInsert(uri,
                DataController.toContentValues(models));
    }

    public static Uri store(Context context, Model model) {
        if (model == null) {
            return null;
        }
        return context.getContentResolver().insert(model.getContentUri(),
                model.values());
    }

    public static boolean update(Context context, BaseModel model) {
        if (model == null) {
            return false;
        }
        String where = IBaseColumns.ID + " =? ";
        String[] whereArgs = new String[]{model.getId()};
        return context.getContentResolver().update(model.getContentUri(), model.values(), where, whereArgs) != -1;
    }

    public static int updateUserModel(Context context, final UserModel u) {
        ContentValues values = new ContentValues();
        values.put(IBaseColumns.TYPE, u.getType());
        values.put(UserColumns.FOLLOWING, u.isFollowing());
        values.put(UserColumns.STATUSES_COUNT, u.getStatusesCount());
        values.put(UserColumns.FAVORITES_COUNT, u.getFavouritesCount());
        values.put(UserColumns.FRIENDS_COUNT, u.getFriendsCount());
        values.put(UserColumns.FOLLOWERS_COUNT, u.getFollowersCount());
        values.put(UserColumns.DESCRIPTION, u.getDescription());
        values.put(UserColumns.PROFILE_IMAGE_URL, u.getProfileImageUrl());
        values.put(UserColumns.PROFILE_IMAGE_URL_LARGE,
                u.getProfileImageUrlLarge());
        return DataController.update(context, u, values);
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

    public static int deleteStatusByType(Context context, int type) {
        String where = IBaseColumns.TYPE + " = ? ";
        String[] whereArgs = new String[]{String.valueOf(type)};
        return context.getContentResolver().delete(StatusColumns.CONTENT_URI,
                where, whereArgs);
    }

    public static int deleteUserTimeline(Context context, String userId) {
        String where = IBaseColumns.TYPE + " = ? AND " + StatusColumns.USER_ID
                + " =? ";
        String[] whereArgs = new String[]{
                String.valueOf(StatusModel.TYPE_USER), userId};
        return context.getContentResolver().delete(StatusColumns.CONTENT_URI,
                where, whereArgs);
    }

    public static int deleteUserFavorites(Context context, String userId) {
        String where = IBaseColumns.TYPE + " = ? AND " + IBaseColumns.OWNER
                + " =? ";
        String[] whereArgs = new String[]{
                String.valueOf(StatusModel.TYPE_FAVORITES), userId};
        return context.getContentResolver().delete(StatusColumns.CONTENT_URI,
                where, whereArgs);
    }

    public static int deleteStatusByUserId(Context context, String userId) {
        return context.getContentResolver().delete(StatusColumns.CONTENT_URI,
                StatusColumns.USER_ID + " =? ", new String[]{userId});
    }

    public static int deleteRecord(Context context, long id) {
        Uri uri = ContentUris.withAppendedId(StatusUpdateInfoColumns.CONTENT_URI, id);
        return context.getContentResolver().delete(uri, null, null);
    }

    public static UserModel getUser(Context context, String id) {
        Uri uri = withAppendedId(UserColumns.CONTENT_URI, id);
        Cursor cursor = context.getContentResolver().query(uri, null, null,
                null, null);
        if (AppContext.DEBUG) {
            Log.d(TAG,
                    "getUser() cursor=" + cursor + " cursor.size="
                            + cursor.getCount());
        }
        if (cursor != null && cursor.moveToFirst()) {
            UserModel um = UserModel.from(cursor);
            cursor.close();
            return um;
        }
        return null;
    }

    public static StatusModel getStatus(Context context, String id) {
        Uri uri = withAppendedId(StatusColumns.CONTENT_URI, id);
        Cursor cursor = context.getContentResolver().query(uri, null, null,
                null, null);
        if (cursor != null && cursor.moveToFirst()) {
            StatusModel sm = StatusModel.from(cursor);
            cursor.close();
            return sm;
        }
        return null;
    }

    public static Cursor getHomeTimelineCursor(Context context) {
        String orderBy = DataProvider.ORDERBY_RAWID_DESC;
        String where = IBaseColumns.TYPE + " =? OR " + IBaseColumns.TYPE + " =? ";
        String[] whereArgs = new String[]{String.valueOf(StatusModel.TYPE_HOME), String.valueOf(StatusModel.TYPE_MENTIONS)};
        return context.getContentResolver().query(StatusColumns.CONTENT_URI, null, where, whereArgs, orderBy);
    }

    public static Cursor getDirectMessageCursor(Context context) {
        String orderBy = DataProvider.ORDERBY_RAWID_DESC;
        return context.getContentResolver().query(DirectMessageColumns.CONTENT_URI, null, null, null, orderBy);
    }

    public static CursorLoader getConversationListLoader(Activity activity) {
        String where = IBaseColumns.TYPE + " =? ";
        String[] whereArgs = new String[]{String
                .valueOf(DirectMessageModel.TYPE_CONVERSATION_LIST)};
        String orderBy = DataProvider.ORDERBY_TIME_DESC;
        return new CursorLoader(activity, DirectMessageColumns.CONTENT_URI,
                null, where, whereArgs, orderBy);
    }

    public static CursorLoader getConversationLoader(Activity activity,
                                                     String id) {
        String where = IBaseColumns.TYPE + " !=? AND "
                + DirectMessageColumns.CONVERSATION_ID + " =? ";
        String[] whereArgs = new String[]{
                String.valueOf(DirectMessageModel.TYPE_CONVERSATION_LIST), id};
        String orderBy = DataProvider.ORDERBY_TIME;
        return new CursorLoader(activity, DirectMessageColumns.CONTENT_URI,
                null, where, whereArgs, orderBy);
    }

    public static Loader<Cursor> getTimelineCursorLoader(Context context,
                                                         int type) {
        String where = IBaseColumns.TYPE + " =? ";
        String[] whereArgs = new String[]{String.valueOf(type)};
        return new CursorLoader(context, StatusColumns.CONTENT_URI, null,
                where, whereArgs, DataProvider.ORDERBY_RAWID_DESC);
    }

    public static Loader<Cursor> getUserTimelineCursorLoader(Context context,
                                                             String userId) {
        String where = IBaseColumns.TYPE + " =? AND " + StatusColumns.USER_ID
                + " =? ";
        String[] whereArgs = new String[]{
                String.valueOf(StatusModel.TYPE_USER), userId};
        return new CursorLoader(context, StatusColumns.CONTENT_URI, null,
                where, whereArgs, DataProvider.ORDERBY_RAWID_DESC);
    }

    public static Loader<Cursor> getUserFavoritesCursorLoader(Context context,
                                                              String userId) {
        String where = IBaseColumns.TYPE + " =? AND " + IBaseColumns.OWNER
                + " =? ";
        String[] whereArgs = new String[]{
                String.valueOf(StatusModel.TYPE_FAVORITES), userId};
        return new CursorLoader(context, StatusColumns.CONTENT_URI, null,
                where, whereArgs, DataProvider.ORDERBY_RAWID_DESC);
    }

    public static Loader<Cursor> getAutoCompleteCursorLoader(Context context,
                                                             String id) {
        final String[] projection = new String[]{BaseColumns._ID,
                IBaseColumns.ID, UserColumns.SCREEN_NAME, IBaseColumns.TYPE,
                IBaseColumns.OWNER};
        final String where = IBaseColumns.TYPE + " =? AND " + IBaseColumns.OWNER
                + " =? ";
        final String[] whereArgs = new String[]{
                String.valueOf(UserModel.TYPE_FRIENDS), id};
        return new CursorLoader(context, UserColumns.CONTENT_URI, projection,
                where, whereArgs, null);
    }

    public static Loader<Cursor> getFriendsCursorLoader(Context context,
                                                        String id) {
        return getUserListCursorLoader(context, UserModel.TYPE_FRIENDS, id);
    }

    public static Loader<Cursor> getFollowersCursorLoader(Context context,
                                                          String id) {
        return getUserListCursorLoader(context, UserModel.TYPE_FOLLOWERS, id);
    }

    public static Loader<Cursor> getUserListCursorLoader(Context context,
                                                         int type, String id) {
        final String where = IBaseColumns.TYPE + " =? AND " + IBaseColumns.OWNER
                + " =? ";
        final String[] whereArgs = new String[]{String.valueOf(type), id};
        return new CursorLoader(context, UserColumns.CONTENT_URI, null, where,
                whereArgs, null);
    }

    public static Cursor getUserListCursor(Context context, int type, String id) {
        final String where = IBaseColumns.TYPE + " =? AND " + IBaseColumns.OWNER
                + " =? ";
        final String[] whereArgs = new String[]{String.valueOf(type), id};
        return context.getContentResolver().query(UserColumns.CONTENT_URI,
                null, where, whereArgs, null);
    }

    public static Cursor getUserListSearchCursor(Context context, int type,
                                                 String id, CharSequence constraint) {
        if (TextUtils.isEmpty(constraint)) {
            return getUserListCursor(context, type, id);
        }
        String where = IBaseColumns.TYPE + " =? AND " + IBaseColumns.OWNER
                + " =? AND (" + UserColumns.SCREEN_NAME + " like ? OR "
                + IBaseColumns.ID + " like ? )";
        String query = new StringBuilder().append("%").append(constraint)
                .append("%").toString();
        String[] whereArgs = new String[]{String.valueOf(type), id, query,
                query};
        return context.getContentResolver().query(UserColumns.CONTENT_URI,
                null, where, whereArgs, null);
    }

}
