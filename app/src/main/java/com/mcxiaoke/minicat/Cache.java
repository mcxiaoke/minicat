package com.mcxiaoke.minicat;

import com.mcxiaoke.minicat.dao.model.StatusModel;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Author: mcxiaoke
 * Date:  2016/3/10 8:40
 */
public final class Cache {

    public static volatile long sLastHomeRefresh;

    private static Map<String, List<StatusModel>> sCache = new HashMap<String, List<StatusModel>>();

    public static void put(final String key, final List<StatusModel> value) {
        if (key == null || value == null) {
            return;
        }
        sCache.put(key, value);
    }

    public static List<StatusModel> get(final String key) {
        return sCache.get(key);
    }

    public static void remove(final String key) {
        sCache.remove(key);
    }

    public static void clear() {
        sCache.clear();
    }

}
