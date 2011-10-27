package com.fanfou.app.cache;

import android.util.Log;

import com.fanfou.app.App;
import com.fanfou.app.api.Status;
import com.fanfou.app.api.User;

/**
 * @author mcxiaoke
 * @version 1.0 2011.09.29
 * @version 1.1 2011.10.26
 * 
 */
public final class CacheManager {
	private static UserCache sUserCache;
	private static StatusCache sStatusCache;

	static {
		sUserCache = new UserCache();
		sStatusCache=new StatusCache();
	}

	public static void put(User user) {
		if (user != null) {
			if(App.DEBUG){
				Log.d("CacheManager", "put user to cache: "+user.screenName);
			}
			sUserCache.put(user.id, user);
		}
	}
	
	public static void put(Status status) {
		if (status != null) {
			if(App.DEBUG){
				Log.d("CacheManager", "put status to cache: "+status.id);
			}
			sStatusCache.put(status.id, status);
		}
	}

	public static User getUser(String key) {
		if(App.DEBUG){
			Log.v("CacheManager", "get user from cache : "+key);
		}
		return sUserCache.get(key);
	}
	
	public static Status getStatus(String key) {
		if(App.DEBUG){
			Log.v("CacheManager", "get status from cache : "+key);
		}
		return sStatusCache.get(key);
	}
}
