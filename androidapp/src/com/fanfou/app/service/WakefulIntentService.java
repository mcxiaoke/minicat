package com.fanfou.app.service;

import android.app.IntentService;
import android.content.Context;
import android.os.PowerManager;

/**
 * @author mcxiaoke
 * @version 1.0 2011.09.20
 * 
 */
public abstract class WakefulIntentService extends IntentService {
	private static final String TAG = WakefulIntentService.class
			.getSimpleName();

	private PowerManager.WakeLock mWakeLock;

	public WakefulIntentService(String name) {
		super(name);
	}

	@Override
	public void onCreate() {
		super.onCreate();
		PowerManager mgr = (PowerManager) getSystemService(Context.POWER_SERVICE);
		mWakeLock = mgr.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, TAG);
		try {
			mWakeLock.acquire();
		} catch (SecurityException e) {
		}

	}

	@Override
	public void onDestroy() {
		if (mWakeLock != null) {
			mWakeLock.release();
		}
		super.onDestroy();
	}

}
