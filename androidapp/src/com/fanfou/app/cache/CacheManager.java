package com.fanfou.app.cache;


/**
 * @author mcxiaoke
 * @version 1.0 2011.06.01
 * @param <T>
 *
 */
public interface CacheManager<T> {
	
	int getCount();
	
	T get(String key);
	
	boolean put(String key , T t);
	
	boolean containsKey(String key);
	
	void clear();

}
