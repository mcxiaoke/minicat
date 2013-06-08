package com.mcxiaoke.fanfouapp.preference;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import com.mcxiaoke.fanfouapp.R;

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
    private static PreferenceHelper sPreferenceHelper;


    private Context mAppContext;
    private SharedPreferences mPreferences;

    public static PreferenceHelper getInstance(Context context) {
        if (sPreferenceHelper == null) {
            synchronized (context) {
                sPreferenceHelper = new PreferenceHelper(context);
            }
        }
        return sPreferenceHelper;
    }

    private PreferenceHelper(Context context) {
        mAppContext = context.getApplicationContext();
        mPreferences = PreferenceManager.getDefaultSharedPreferences(mAppContext);
    }

    public boolean isPushNotificationEnabled() {
        boolean defaultValue = mAppContext.getResources().getBoolean(R.bool.option_push_notification_default_value);
        String key = mAppContext.getResources().getString(R.string.option_push_notification_key);
        return mPreferences.getBoolean(key, defaultValue);
    }

    public void setPushNotificationEnabled(boolean value) {
        String key = mAppContext.getResources().getString(R.string.option_push_notification_key);
        mPreferences.edit().putBoolean(key, value).commit();
    }

    public boolean isRefreshOnStart() {
        boolean defaultValue = mAppContext.getResources().getBoolean(R.bool.option_refresh_on_start_default_value);
        String key = mAppContext.getString(R.string.option_refresh_on_start_key);
        return mPreferences.getBoolean(key, defaultValue);
    }

    public void setRefreshOnStart(boolean value) {
        String key = mAppContext.getString(R.string.option_refresh_on_start_key);
        mPreferences.edit().putBoolean(key, value).commit();
    }

    public boolean isActionBarRefreshButtonEnabled() {
        boolean defaultValue = mAppContext.getResources().getBoolean(R.bool.option_actionbar_refresh_button_default_value);
        String key = mAppContext.getResources().getString(R.string.option_actionbar_refresh_button_key);
        return mPreferences.getBoolean(key, defaultValue);
    }

    public void setActionBarRefreshButtonEnabled(boolean value) {
        String key = mAppContext.getResources().getString(R.string.option_actionbar_refresh_button_key);
        mPreferences.edit().putBoolean(key, value).commit();
    }

    public String getLastPushStatusId() {
        return mPreferences.getString(KEY_LAST_PUSH_STATUS_ID, "");
    }

    public String getKeyLastPushDmId() {
        return mPreferences.getString(KEY_LAST_PUSH_DM_ID, "");
    }

    public void setLastPushStatusId(String id) {
        mPreferences.edit().putString(KEY_LAST_PUSH_STATUS_ID, id).commit();
    }

    public void setLastPushDmId(String id) {
        mPreferences.edit().putString(KEY_LAST_PUSH_DM_ID, id).commit();
    }


}
