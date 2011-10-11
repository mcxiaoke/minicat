package com.fanfou.app.util;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;

import com.fanfou.app.App;

/**
 * @author mcxiaoke
 * @version 1.0 2011.06.01
 * @version 1.1 2011.10.10
 * 
 */
public class OptionHelper {

	public static void saveInt(Context context, String key, int value) {
		Editor sp = App.me.sp.edit();
		sp.putInt(key, value);
		sp.commit();
	}

	public static void saveBoolean(Context context, String key, boolean value) {
		Editor sp = App.me.sp.edit();
		sp.putBoolean(key, value);
		sp.commit();
	}

	public static void saveString(Context context, String key, String value) {
		Editor sp = App.me.sp.edit();
		sp.putString(key, value);
		sp.commit();
	}

	public static void saveInt(Context context, int resId, int value) {
		Editor sp = App.me.sp.edit();
		sp.putInt(context.getString(resId), value);
		sp.commit();
	}

	public static void saveBoolean(Context context, int resId, boolean value) {
		Editor sp = App.me.sp.edit();
		sp.putBoolean(context.getString(resId), value);
		sp.commit();
	}

	public static void saveString(Context context, int resId, String value) {
		Editor sp = App.me.sp.edit();
		sp.putString(context.getString(resId), value);
		sp.commit();
	}

	public static int readInt(Context context, String key, int defValue) {
		SharedPreferences sp = App.me.sp;
		int res = sp.getInt(key, defValue);
		return res;
	}

	public static boolean readBoolean(Context context, String key,
			boolean defValue) {
		SharedPreferences sp = App.me.sp;
		boolean res = sp.getBoolean(key, defValue);
		return res;
	}

	public static String readString(Context context, String key, String defValue) {
		SharedPreferences sp = App.me.sp;
		String res = sp.getString(key, defValue);
		return res;
	}

	public static int parseInt(Context context, String key) {
		SharedPreferences sp = App.me.sp;
		String res = sp.getString(key, "-1");
		return Integer.parseInt(res);
	}

	public static int parseInt(Context context, int resId) {
		SharedPreferences sp = App.me.sp;
		String res = sp.getString(context.getString(resId), "-1");
		return Integer.parseInt(res);
	}

	public static int parseInt(Context context, String key, String defaultValue) {
		SharedPreferences sp = App.me.sp;
		String res = sp.getString(key, defaultValue);
		return Integer.parseInt(res);
	}

	public static int parseInt(Context context, int resId, String defaultValue) {
		SharedPreferences sp = App.me.sp;
		String res = sp.getString(context.getString(resId), defaultValue);
		return Integer.parseInt(res);
	}

	public static int readInt(Context context, int resId, int defValue) {
		SharedPreferences sp = App.me.sp;
		int res = sp.getInt(context.getString(resId), defValue);
		return res;
	}

	public static boolean readBoolean(Context context, int resId,
			boolean defValue) {
		SharedPreferences sp = App.me.sp;
		boolean res = sp.getBoolean(context.getString(resId), defValue);
		return res;
	}

	public static String readString(Context context, int resId, String defValue) {
		SharedPreferences sp = App.me.sp;
		String res = sp.getString(context.getString(resId), defValue);
		return res;
	}

	public static void remove(Context context, String key) {
		Editor sp = App.me.sp.edit();
		sp.remove(key);
		sp.commit();
	}

	public static void remove(Context context, int resId) {
		Editor sp = App.me.sp.edit();
		sp.remove(context.getString(resId));
		sp.commit();
	}

}
