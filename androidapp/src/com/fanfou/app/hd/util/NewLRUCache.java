package com.fanfou.app.hd.util;

/**
 * Copyright(c) 2008 Nicolas Martignole � Le Touilleur Express
 * Modified by Kevin Gaudin: renamed methods with get / put to allow easy switching with Map implementations/
 * http://touilleur-express.fr
 * Distributed under Creative Commons License 2.0
 * See http://creativecommons.org/licenses/by-sa/2.0/fr/deed.fr
 */

import java.lang.ref.SoftReference;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * New LRU Cache constructed with a LinkedHashMap, a Map with a predictable
 * iteration order that stores a key/value pair, the value is decorated with a
 * SoftReference so that we can release memory when the system is overloaded. I
 * also use a special constructor from LinkedHashMap so that I can have an order
 * in this map provided by access
 * 
 * @author Nicolas Martignole
 * @version created Sep 16, 2008
 */
public class NewLRUCache<K, V> {
	private LinkedHashMap<K, SoftReference<V>> cache;
	private int cacheSize;
	private int numberOfElements = 0;

	/**
	 * Creates a cache with a fixed size of 100.
	 */
	public NewLRUCache() {
		this(100);
	}

	/**
	 * Create a new LRU Cache with a LinkedHashMap, override the
	 * removeEldestEntry so that we can inject the current cacheSize.
	 * 
	 * @param cacheSize
	 *            is a positive integer.
	 */
	public NewLRUCache(int cacheSize) {
		this.cacheSize = (cacheSize < 1) ? 1000 : cacheSize;
		int initialCapacity = (int) (cacheSize * 0.75);
		cache = new LinkedHashMap<K, SoftReference<V>>(initialCapacity, 0.75f,
				true) {
			private static final long serialVersionUID = 7787935142078995459L;

			@Override
			/**
			 * Returns true if the current map size is greater than NewLRUCache, wich means
			 * that the cache is full and we should drop the oldest entry.
			 */
			protected boolean removeEldestEntry(
					Map.Entry<K, SoftReference<V>> eldest) {
				return size() > NewLRUCache.this.cacheSize;
			}
		};
	}

	/**
	 * Returns the cache size.
	 * 
	 * @return the cache size.
	 */
	public final int getSize() {
		return cacheSize;
	}

	/**
	 * Returns the number of elements currently stored into the cache.
	 * 
	 * @return a number of elements.
	 */
	public final int getCurrentUsage() {
		return numberOfElements;
	}

	/**
	 * Stores into the cache the specified entry, remove the less recently used
	 * entry if the cache is full.
	 * 
	 * @param key
	 *            is the new unique key to store.
	 * @param entry
	 *            is the object to put into the cache.
	 */
	public void put(K key, V entry) {
		if (key == null)
			return;
		if (entry == null)
			return;
		synchronized (this) {
			cache.put(key, new SoftReference<V>(entry));
			numberOfElements++;
		}
	}

	/**
	 * Lookup for the specified key, update the list of less recently used
	 * items.
	 * 
	 * @param key
	 *            is the key to lookup
	 * @return the associated object or null if it was not found.
	 */
	public V get(K key) {
		if (key == null) {
			return null;
		}
		SoftReference<V> ref;
		synchronized (this) {
			ref = cache.get(key);
			if (ref == null) {
				// The value has been garbage collected so we must delete the
				// key
				cache.remove(key);
				numberOfElements--;
				return null;
			}
		}
		return ref.get();
	}

}