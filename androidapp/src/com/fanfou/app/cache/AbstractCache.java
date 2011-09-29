package com.fanfou.app.cache;

import java.lang.ref.SoftReference;
import java.util.HashMap;

/**
 * @author mcxiaoke
 * @version 1.0 2011.09.29
 * 
 * @param <T>
 */
public abstract class AbstractCache<T> implements ICache<T> {

	final HashMap<String, SoftReference<T>> memoryCache;
	final boolean onlyMemoryCache;

	public AbstractCache() {
		this.memoryCache = new HashMap<String, SoftReference<T>>();
		this.onlyMemoryCache = true;
	}

	public AbstractCache(boolean onlyMemoryCache) {
		this.memoryCache = new HashMap<String, SoftReference<T>>();
		this.onlyMemoryCache = onlyMemoryCache;
	}

	@Override
	public int getCount() {
		return memoryCache.size();
	}

	@Override
	public T get(String key) {
		if (key == null || key.equals("")) {
			return null;
		}
		T result = null;
		final SoftReference<T> reference = memoryCache.get(key);

		if (reference != null) {
			result = reference.get();
		} else {
			if (!onlyMemoryCache) {
				result = read(key);
			}
		}
		return result;
	}

	@Override
	public boolean put(String key, T t) {
		if(key==null||key.equals("")||t==null){
			return false;
		}
		boolean result = true;
		synchronized (this) {
			result = memoryCache.put(key, new SoftReference<T>(t)) != null;
		}
		if (!onlyMemoryCache) {
			result = write(key, t);
		}
		return result;
	}

	@Override
	public boolean containsKey(String key) {
		if(key==null||key.equals("")){
			return false;
		}
		if (onlyMemoryCache) {
			return memoryCache.containsKey(key);
		} else {
			return get(key) != null;
		}
	}

	@Override
	public void clear() {
		memoryCache.clear();
	}

	protected boolean write(String key, T t) {
		throw new NullPointerException(
				"file cache must override write() method.");
	};

	protected T read(String key) {
		throw new NullPointerException(
				"file cache must override read() method.");
	};

}
