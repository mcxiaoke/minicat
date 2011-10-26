package com.fanfou.app.cache;

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
			sUserCache.put(user.id, user);
		}
	}
	
	public static void put(Status status) {
		if (status != null) {
			sStatusCache.put(status.id, status);
		}
	}

	public static User getUser(String key) {
		return sUserCache.get(key);
	}
	
	public static Status getStatus(String key) {
		return sStatusCache.get(key);
	}
}
