package com.mcxiaoke.minicat.preference;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import com.mcxiaoke.minicat.R;

/**
 * Project: fanfouapp
 * Package: com.mcxiaoke.fanfouapp.preference
 * User: mcxiaoke
 * Date: 13-5-26
 * Time: 下午9:47
 */
public class PreferenceHelper {
    private static final String KEY_LAST_PUSH_STATUS_ID = "push_last_status_id";
    private static final String KEY_LAST_PUSH_DM_ID = "push_last_dm_id";
    private static final String KEY_LAST_UPDATE_TIME = "last_update_time";
    private static PreferenceHelper sPreferenceHelper;


    private Context mAppContext;
    private SharedPreferences mPreferences;

    private PreferenceHelper(Context context) {
        mAppContext = context.getApplicationContext();
        mPreferences = PreferenceManager.getDefaultSharedPreferences(mAppContext);
    }

    public static PreferenceHelper getInstance(Context context) {
        if (sPreferenceHelper == null) {
            synchronized (context) {
                sPreferenceHelper = new PreferenceHelper(context);
            }
        }
        return sPreferenceHelper;
    }

    public boolean isPushNotificationEnabled() {
        boolean defaultValue = mAppContext.getResources().getBoolean(R.bool.option_push_notification_default_value);
        String key = mAppContext.getResources().getString(R.string.option_push_notification_key);
        return mPreferences.getBoolean(key, defaultValue);
    }

    public void setPushNotificationEnabled(boolean value) {
        String key = mAppContext.getResources().getString(R.string.option_push_notification_key);
        mPreferences.edit().putBoolean(key, value).apply();
    }

    public boolean isAutoUpdate() {
        boolean defaultValue = mAppContext.getResources().getBoolean(R.bool.option_auto_update_default_value);
        String key = mAppContext.getResources().getString(R.string.option_auto_update_key);
        return mPreferences.getBoolean(key, defaultValue);
    }

    public void setAutoUpdate(boolean value) {
        String key = mAppContext.getResources().getString(R.string.option_auto_update_key);
        mPreferences.edit().putBoolean(key, value).apply();
    }

    public String getLastPushStatusId() {
        return mPreferences.getString(KEY_LAST_PUSH_STATUS_ID, "");
    }

    public void setLastPushStatusId(String id) {
        mPreferences.edit().putString(KEY_LAST_PUSH_STATUS_ID, id).apply();
    }

    public String getKeyLastPushDmId() {
        return mPreferences.getString(KEY_LAST_PUSH_DM_ID, "");
    }

    public void setLastPushDmId(String id) {
        mPreferences.edit().putString(KEY_LAST_PUSH_DM_ID, id).apply();
    }

    public long getLastUpdateTime() {
        return mPreferences.getLong(KEY_LAST_UPDATE_TIME, 0);
    }

    public void setKeyLastUpdateTime(long time) {
        mPreferences.edit().putLong(KEY_LAST_UPDATE_TIME, time).apply();
    }


}
