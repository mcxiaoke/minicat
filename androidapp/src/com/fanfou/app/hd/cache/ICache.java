package com.fanfou.app.hd.cache;

/**
 * @author mcxiaoke
 * @version 1.0 2011.06.01
 * @version 1.1 2011.12.02
 * @param <T>
 * 
 */
public interface ICache<T> {

	int getCount();

	T get(String key);

	boolean put(String key, T t);

	boolean containsKey(String key);

	void clear();

	boolean isEmpty();

}
