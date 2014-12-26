package com.mcxiaoke.minicat.controller;

import android.content.Context;
import com.mcxiaoke.minicat.dao.model.StatusModel;
import com.mcxiaoke.minicat.dao.model.UserModel;

import java.util.Map;
import java.util.WeakHashMap;


public final class CacheController {
    private static Map<String, UserModel> sUserCache = new WeakHashMap<String, UserModel>();
    private static Map<String, StatusModel> sStatusCache = new WeakHashMap<String, StatusModel>();

    public static void cache(UserModel user) {
        if (user != null) {
            sUserCache.put(user.getId(), user);
        }
    }

    public static void cache(StatusModel status) {
        if (status != null) {
            sStatusCache.put(status.getId(), status);
        }
    }

    public static void cacheAndStore(Context context, UserModel user) {
        if (user != null) {
            DataController.update(context, user);
            cache(user);
        }
    }

    public static void cacheAndStore(Context context, StatusModel status) {
        if (status != null) {
            DataController.update(context, status);
            cache(status);
        }
    }

    public static UserModel getUser(String key) {
        return sUserCache.get(key);
    }

    public static StatusModel getStatus(String key) {
        return sStatusCache.get(key);
    }

    public static UserModel getUserAndCache(String key, Context context) {
        UserModel um = getUser(key);
        if (um == null) {
            um = DataController.getUser(context, key);
        }

        if (um != null) {
            cache(um);
        } else {
            sUserCache.remove(key);
        }
        return um;
    }

    public static StatusModel getStatusAndCache(String key, Context context) {
        StatusModel sm = getStatus(key);
        if (sm == null) {
            sm = DataController.getStatus(context, key);
        }

        if (sm != null) {
            cache(sm);
        } else {
            sStatusCache.remove(key);
        }
        return sm;
    }

}
