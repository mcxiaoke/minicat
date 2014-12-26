package com.mcxiaoke.minicat.service;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.util.Log;
import com.mcxiaoke.minicat.AppContext;
import com.mcxiaoke.minicat.api.Api;
import com.mcxiaoke.minicat.api.Paging;
import com.mcxiaoke.minicat.controller.DataController;
import com.mcxiaoke.minicat.dao.model.UserColumns;
import com.mcxiaoke.minicat.dao.model.UserModel;
import com.mcxiaoke.minicat.util.NetworkHelper;

import java.util.List;

/**
 * @author mcxiaoke
 * @version 2.9 2012.02.24
 */
public class AutoCompleteService extends BaseIntentService {
    private static final String TAG = AutoCompleteService.class.getSimpleName();
    private static final String KEY_LAST_AUTO_COMPLETE_TIME = "auto_complete_last_sync_time";
    private static final long SYNC_INTERVAL = 1000 * 3600 * 3;


    public AutoCompleteService() {
        super("AutoCompleteService");
    }

    public static void check(Context context) {
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);
        long lastSync = sp.getLong(KEY_LAST_AUTO_COMPLETE_TIME, 0);
        long now = System.currentTimeMillis();
        if (now - lastSync > SYNC_INTERVAL) {
            Intent intent = new Intent(context, AutoCompleteService.class);
            context.startService(intent);
            sp.edit().putLong(KEY_LAST_AUTO_COMPLETE_TIME, now);
        }
    }

    public void log(String message) {
        Log.d(TAG, message);
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        doWakefulWork(intent);
    }

    protected void doWakefulWork(Intent intent) {
        doFetchAutoComplete();
    }

    private void doFetchAutoComplete() {
        if (NetworkHelper.isNotConnected(this) || !AppContext.isVerified()) {
            return;
        }
        Api api = AppContext.getApi();
        Paging p = new Paging();
        p.count = SyncService.MAX_USERS_COUNT;
        p.page = 1;
        boolean more = true;
        while (more) {
            List<UserModel> result = null;
            try {
                result = api.getFriends(AppContext.getAccount(), p);
            } catch (Exception e) {
                if (AppContext.DEBUG) {
                    Log.e(TAG, e.toString());
                }
            }
            if (result != null && result.size() > 0) {
                int size = result.size();
                int insertedNums = getContentResolver().bulkInsert(
                        UserColumns.CONTENT_URI,
                        DataController.toContentValues(result));
                if (AppContext.DEBUG) {
                    log("doFetchAutoComplete page==" + p.page + " size=" + size
                            + " insert rows=" + insertedNums);
                }
                if (size < SyncService.MAX_USERS_COUNT || p.page >= 20) {
                    more = false;
                }
            } else {
                more = false;
            }
            p.page++;
        }
    }

}
