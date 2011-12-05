package com.fanfou.app.cache;

import android.content.Context;
import android.database.Cursor;
import android.util.Log;

import com.fanfou.app.App;
import com.fanfou.app.api.Status;
import com.fanfou.app.api.User;
import com.fanfou.app.db.FanFouProvider;

/**
 * @author mcxiaoke
 * @version 1.0 2011.09.29
 * @version 1.1 2011.10.26
 * @version 1.2 2011.10.28
 * @version 1.3 2011.11.18
 * 
 */
public final class CacheManager {

	private static final String TAG = CacheManager.class.getSimpleName();

	private static UserCache sUserCache;
	private static StatusCache sStatusCache;

	static {
		sUserCache = new UserCache();
		sStatusCache = new StatusCache();
	}

	public static void put(User user) {
		if (user != null) {
			sUserCache.put(user.id, user);
		}
	}

	public static void put(Status status) {
		if (status != null) {
			sStatusCache.put(status.id, status);
		}
	}

	public static User getUser(String key) {
		if (App.DEBUG) {
			Log.v("CacheManager", "get user from cache : " + key);
		}
		return sUserCache.get(key);
	}

	public static Status getStatus(String key) {
		if (App.DEBUG) {
			Log.v("CacheManager", "get status from cache : " + key);
		}
		return sStatusCache.get(key);
	}

	public static User getUser(Context context, String key) {
		User user = sUserCache.get(key);
		if (user == null) {
			if (App.DEBUG) {
				Log.v("CacheManager", "get user from cache : " + key);
			}
			user = queryUser(context, key);
			if (user != null) {
				if (App.DEBUG) {
					Log.v("CacheManager", "cache user from db : " + key);
				}
				put(user);
			}
		}
		return user;
	}

	public static Status getStatus(Context context, String key) {
		Status status = sStatusCache.get(key);

		if (status == null) {
			if (App.DEBUG) {
				Log.v("CacheManager", "get status from cache : " + key);
			}
			status = queryStatus(context, key);
			if (status != null) {
				if (App.DEBUG) {
					Log.v("CacheManager", "cache status from db : " + key);
				}
				put(status);
			}
		}
		return status;
	}

	public static User queryUser(Context context, final String id) {
		final Cursor cursor = context.getContentResolver().query(
				FanFouProvider.buildUriWithUserId(id), null, null, null, null);
		if (cursor != null && cursor.moveToFirst()) {
			if (App.DEBUG) {
				Log.d(TAG, "queryUser cursor.size=" + cursor.getCount());
			}
			return User.parse(cursor);
		}
		return null;
	}

	public static Status queryStatus(Context context, final String id) {
		final Cursor cursor = context.getContentResolver()
				.query(FanFouProvider.buildUriWithStatusId(id), null, null,
						null, null);
		if (cursor != null && cursor.moveToFirst()) {
			if (App.DEBUG) {
				Log.d(TAG, "queryStatus cursor.size=" + cursor.getCount());
			}
			return Status.parse(cursor);
		}
		return null;
	}

}
