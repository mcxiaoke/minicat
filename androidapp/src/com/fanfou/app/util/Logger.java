package com.fanfou.app.util;

import java.util.HashMap;

import android.util.Log;

/**
 * @author mcxiaoke
 * @version 1.0 2011.12.12
 *
 */
public final class Logger {
	private static final String TAG=Logger.class.getSimpleName(); 
	private static HashMap<String, Long> times=new HashMap<String, Long>();
	
	public static void start(String tag, long time){
		times.put(tag, time);
	}
	
	public static long stop(String tag, long time){
		long now=time;
		long last=times.remove(tag);
		long interval=now-last;
		String str=String.format("[%s] Time: %s",tag, interval);
		Log.e(TAG, str);
		return now;
	}

}
