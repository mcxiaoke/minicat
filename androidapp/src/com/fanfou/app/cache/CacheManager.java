package com.fanfou.app.cache;

import com.fanfou.app.api.User;

/**
 * @author mcxiaoke
 * @version 1.0 2011.09.29
 *
 */
public final class CacheManager {
	private static UserCache sUserCache;
	
	static {
		sUserCache=new UserCache();
	}

	public static void put(User user){
		if(user!=null){
			sUserCache.put(user.id, user);
		}
	}
	
	public static User get(String key){
		return sUserCache.get(key);
	}
}
