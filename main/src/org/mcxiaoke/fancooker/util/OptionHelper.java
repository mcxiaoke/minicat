package org.mcxiaoke.fancooker.util;

import org.mcxiaoke.fancooker.App;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.preference.PreferenceManager;


/**
 * @author mcxiaoke
 * @version 1.0 2011.06.01
 * @version 1.1 2011.10.10
 * @version 1.2 2011.10.26
 * @version 2.0 2011.12.06
 * @version 3.0 2011.12.26
 * @version 4.0 2012.02.24
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

	public final static void saveInt(Context context, int resId, int value) {
		Editor sp = App.getPreferences().edit();
		sp.putInt(context.getString(resId), value);
		sp.commit();
	}

	public final static void saveBoolean(Context context, int resId,
			boolean value) {
		Editor sp = App.getPreferences().edit();
		sp.putBoolean(context.getString(resId), value);
		sp.commit();
	}

	public final static void saveString(Context context, int resId, String value) {
		Editor sp = App.getPreferences().edit();
		sp.putString(context.getString(resId), value);
		sp.commit();
	}

	public final static int readInt(String key, int defValue) {
		int res = App.getPreferences().getInt(key, defValue);
		return res;
	}

	public final static boolean readBoolean(String key, boolean defValue) {
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

	public final static int parseInt(Context context, int resId) {
		String res = App.getPreferences().getString(context.getString(resId),
				"-1");
		return Integer.parseInt(res);
	}

	public final static int parseInt(String key, String defaultValue) {
		String res = App.getPreferences().getString(key, defaultValue);
		return Integer.parseInt(res);
	}

	public final static int parseInt(Context context, int resId,
			String defaultValue) {
		String res = App.getPreferences().getString(context.getString(resId),
				defaultValue);
		return Integer.parseInt(res);
	}

	public final static int readInt(Context context, int resId, int defValue) {
		int res = App.getPreferences().getInt(context.getString(resId),
				defValue);
		return res;
	}

	public final static boolean readBoolean(Context context, int resId,
			boolean defValue) {
		boolean res = App.getPreferences().getBoolean(context.getString(resId),
				defValue);
		return res;
	}

	public final static String readString(Context context, int resId,
			String defValue) {
		String res = App.getPreferences().getString(context.getString(resId),
				defValue);
		return res;
	}

	public final static void remove(String key) {
		Editor sp = App.getPreferences().edit();
		sp.remove(key);
		sp.commit();
	}

	public final static void remove(Context context, int resId) {
		Editor sp = App.getPreferences().edit();
		sp.remove(context.getString(resId));
		sp.commit();
	}

	public final static void clearSettings() {
		Editor sp = App.getPreferences().edit();
		sp.clear();
		sp.commit();
	}

	public final static void clearSettings(Context context) {
		SharedPreferences sp = PreferenceManager
				.getDefaultSharedPreferences(context);
		Editor editor = sp.edit();
		editor.clear();
		editor.commit();
	}

}
