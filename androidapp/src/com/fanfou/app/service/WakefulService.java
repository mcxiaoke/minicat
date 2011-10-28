package com.fanfou.app.service;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;
import android.os.PowerManager;

/**
 * @author mcxiaoke
 * @version 1.0 2011.10.28
 *
 */
public abstract class WakefulService extends Service {
	public static final String LOCK_NAME_STATIC = "WakeLock.Static";
	public static final String LOCK_NAME_LOCAL = "WakeLock.Local";

	private static PowerManager.WakeLock mLockStatic = null;
	private PowerManager.WakeLock mLockLocal = null;

	public static void acquireStaticLock(Context context) {
		getLock(context).acquire();
	}

	private static synchronized PowerManager.WakeLock getLock(Context context) {
		if (mLockStatic == null) {
			PowerManager mgr = (PowerManager) context.getSystemService(Context.POWER_SERVICE);
			mLockStatic = mgr.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, LOCK_NAME_STATIC);
			mLockStatic.setReferenceCounted(true);
		}
		return mLockStatic;
	}

	@Override
	public void onCreate() {
		super.onCreate();
		PowerManager mgr = (PowerManager) getSystemService(Context.POWER_SERVICE);
		mLockLocal = mgr.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, LOCK_NAME_LOCAL);
		mLockLocal.setReferenceCounted(true);
	}

	@Override
	public void onStart(Intent intent, final int startId) {
		mLockLocal.acquire();
		super.onStart(intent, startId);
		getLock(this).release();
	}
	
	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		return super.onStartCommand(intent, flags, startId);
	}

	/**
	 * Releases the local lock. Must be called when the work is done.
	 */
	protected void releaseLocalLock() {
		mLockLocal.release();
	}
}
