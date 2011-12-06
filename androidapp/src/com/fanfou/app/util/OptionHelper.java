package com.fanfou.app.util;

import com.fanfou.app.App;
import com.fanfou.app.R;
import com.fanfou.app.api.User;
import com.fanfou.app.auth.OAuthToken;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.preference.PreferenceManager;
import android.util.Log;

/**
 * @author mcxiaoke
 * @version 1.0 2011.06.01
 * @version 1.1 2011.10.10
 * @version 1.2 2011.10.26
 * @version 2.0 2011.12.06
 * 
 */
public final class OptionHelper {

	public final static void saveInt(String key, int value) {
		Editor sp = App.getPreferences().edit();
		sp.putInt(key, value);
		sp.commit();
	}

	public final static void saveBoolean(String key, boolean value) {
		Editor sp = App.getPreferences().edit();
		sp.putBoolean(key, value);
		sp.commit();
	}

	public final static void saveString(String key, String value) {
		Editor sp = App.getPreferences().edit();
		sp.putString(key, value);
		sp.commit();
	}

	public final static void saveInt(int resId, int value) {
		Editor sp = App.getPreferences().edit();
		sp.putInt(App.getApp().getString(resId), value);
		sp.commit();
	}

	public final static void saveBoolean(int resId, boolean value) {
		Editor sp = App.getPreferences().edit();
		sp.putBoolean(App.getApp().getString(resId), value);
		sp.commit();
	}

	public final static void saveString(int resId, String value) {
		Editor sp = App.getPreferences().edit();
		sp.putString(App.getApp().getString(resId), value);
		sp.commit();
	}

	public final static int readInt(String key, int defValue) {
		int res = App.getPreferences().getInt(key, defValue);
		return res;
	}

	public final static boolean readBoolean(String key,
			boolean defValue) {
		boolean res = App.getPreferences().getBoolean(key, defValue);
		return res;
	}

	public final static String readString(String key, String defValue) {
		String res = App.getPreferences().getString(key, defValue);
		return res;
	}

	public final static int parseInt(String key) {
		String res = App.getPreferences().getString(key, "-1");
		return Integer.parseInt(res);
	}

	public final static int parseInt(int resId) {
		String res = App.getPreferences().getString(App.getApp().getString(resId), "-1");
		return Integer.parseInt(res);
	}

	public final static int parseInt(String key, String defaultValue) {
		String res = App.getPreferences().getString(key, defaultValue);
		return Integer.parseInt(res);
	}

	public final static int parseInt(int resId, String defaultValue) {
		String res = App.getPreferences().getString(App.getApp().getString(resId), defaultValue);
		return Integer.parseInt(res);
	}

	public final static int readInt(int resId, int defValue) {
		int res = App.getPreferences().getInt(App.getApp().getString(resId), defValue);
		return res;
	}

	public final static boolean readBoolean(int resId,
			boolean defValue) {
		boolean res = App.getPreferences().getBoolean(App.getApp().getString(resId), defValue);
		return res;
	}

	public final static String readString(int resId, String defValue) {
		String res = App.getPreferences().getString(App.getApp().getString(resId), defValue);
		return res;
	}

	public final static void remove(String key) {
		Editor sp = App.getPreferences().edit();
		sp.remove(key);
		sp.commit();
	}

	public final static void remove(int resId) {
		Editor sp = App.getPreferences().edit();
		sp.remove(App.getApp().getString(resId));
		sp.commit();
	}

	public final static void clearSettings() {
		Editor sp = App.getPreferences().edit();
		sp.clear();
		sp.commit();
	}
	
	public final static void cleanAlarmFlags() {
		if (App.DEBUG) {
			Log.d("App", "cleanAlarmFlags");
		}
		Editor editor = App.getPreferences().edit();
		editor.remove(App.getApp().getString(R.string.option_set_auto_clean));
		editor.remove(App.getApp().getString(R.string.option_set_auto_update));
		editor.remove(App.getApp().getString(R.string.option_set_auto_complete));
		editor.remove(App.getApp().getString(R.string.option_set_notification));
		editor.commit();
	}
	
	public final static void updateAccountInfo(final User u,
			final OAuthToken otoken) {
		Editor editor = App.getPreferences().edit();
		editor.putString(App.getApp().getString(R.string.option_userid), u.id);
		editor.putString(App.getApp().getString(R.string.option_username), u.screenName);
		editor.putString(App.getApp().getString(R.string.option_profile_image),
				u.profileImageUrl);
		editor.putString(App.getApp().getString(R.string.option_oauth_token),
				otoken.getToken());
		editor.putString(App.getApp().getString(R.string.option_oauth_token_secret),
				otoken.getTokenSecret());
		editor.commit();
	}
	
	public final static void removeAccountInfo() {
		Editor editor = App.getPreferences().edit();
		editor.remove(App.getApp().getString(R.string.option_userid));
		editor.remove(App.getApp().getString(R.string.option_username));
		editor.remove(App.getApp().getString(R.string.option_profile_image));
		editor.remove(App.getApp().getString(R.string.option_oauth_token));
		editor.remove(App.getApp().getString(R.string.option_oauth_token_secret));
		editor.commit();
	}
	
	public final static void updateUserInfo(final User u) {
		Editor editor = App.getPreferences().edit();
		editor.putString(App.getApp().getString(R.string.option_userid), u.id);
		editor.putString(App.getApp().getString(R.string.option_username), u.screenName);
		editor.putString(App.getApp().getString(R.string.option_profile_image),
				u.profileImageUrl);
		editor.commit();
	}

}
