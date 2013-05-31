package com.mcxiaoke.fanfouapp.service;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import com.mcxiaoke.fanfouapp.api.Api;
import com.mcxiaoke.fanfouapp.api.Paging;
import com.mcxiaoke.fanfouapp.app.AppContext;
import com.mcxiaoke.fanfouapp.controller.DataController;
import com.mcxiaoke.fanfouapp.dao.model.UserColumns;
import com.mcxiaoke.fanfouapp.dao.model.UserModel;
import com.mcxiaoke.fanfouapp.util.DateTimeHelper;
import com.mcxiaoke.fanfouapp.util.NetworkHelper;

import java.util.Calendar;
import java.util.List;

/**
 * @author mcxiaoke
 * @version 2.9 2012.02.24
 */
public class AutoCompleteService extends WakefulIntentService {
    private static final String TAG = AutoCompleteService.class.getSimpleName();

    public void log(String message) {
        Log.d(TAG, message);
    }

    public AutoCompleteService() {
        super("AutoCompleteService");
    }

    public static void set(Context context) {
        Calendar c = Calendar.getInstance();
        c.set(c.get(Calendar.YEAR), c.get(Calendar.MONTH),
                c.get(Calendar.DAY_OF_MONTH), 20, 0);
        c.add(Calendar.MINUTE, 30);
        long interval = 3 * 24 * 3600 * 1000;
        AlarmManager am = (AlarmManager) context
                .getSystemService(Context.ALARM_SERVICE);
        am.setInexactRepeating(AlarmManager.RTC_WAKEUP, c.getTimeInMillis(),
                interval, getPendingIntent(context));

        Intent intent = new Intent(context, AutoCompleteService.class);
        context.startService(intent);
        if (AppContext.DEBUG) {
            Log.d(TAG,
                    "set repeat interval=3day first time="
                            + DateTimeHelper.formatDate(c.getTime()));
        }
    }

    public static void unset(Context context) {
        AlarmManager am = (AlarmManager) context
                .getSystemService(Context.ALARM_SERVICE);
        am.cancel(getPendingIntent(context));
        if (AppContext.DEBUG) {
            Log.d(TAG, "unset");
        }
    }

    private final static PendingIntent getPendingIntent(Context context) {
        Intent intent = new Intent(context, AutoCompleteService.class);
        PendingIntent pi = PendingIntent.getService(context, 0, intent,
                PendingIntent.FLAG_UPDATE_CURRENT);
        return pi;
    }

    @Override
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
