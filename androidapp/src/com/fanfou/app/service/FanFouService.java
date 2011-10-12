package com.fanfou.app.service;

import android.app.NotificationManager;
import android.app.Service;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.Process;
import android.os.RemoteCallbackList;
import android.os.RemoteException;

/**
 * @author mcxiaoke
 * @version 1.0 2011.06.15
 * 
 */
public class FanFouService extends Service {

	public static final int TYPE_HOME = 1;
	public static final int TYPE_MENTION = 2;
	public static final int TYPE_MESSAGE = 3;

	private static final String packageName = "com.fanfou.app";
	private static final String ACTION_PREFIX = "com.fanfou.app.action.";

	private final RemoteCallbackList<IFanFouServiceCallback> mCallbacks = new RemoteCallbackList<IFanFouServiceCallback>();

	private final Handler mHandler = new ServiceHandler();

	private NotificationManager nm;

	/**
	 * 
	 */
	public FanFouService() {
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.app.Service#onBind(android.content.Intent)
	 */
	@Override
	public IBinder onBind(Intent intent) {
		if (IFanFouService.class.getName().equals(intent.getAction())) {
			return mBinder;
		} else {
			return null;
		}
	}

	@Override
	public void onCreate() {
		super.onCreate();
		this.nm = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		Bundle extras = intent.getExtras();
		if (extras != null) {
			// String action = extras.getString(EXTRA_MSGTYPE);
			// handleIntent(action);
		}
		return super.onStartCommand(intent, flags, startId);
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		mCallbacks.kill();
	}

	@Override
	public void onConfigurationChanged(Configuration newConfig) {
		super.onConfigurationChanged(newConfig);
	}

	private void handlerIntent(String action) {

	}

	private class ServiceHandler extends Handler {

		public ServiceHandler() {
			super();
		}

		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
		}

	}

	private final IFanFouService.Stub mBinder = new IFanFouService.Stub() {

		@Override
		public void unregisterCallback(IFanFouServiceCallback callback)
				throws RemoteException {
			if (callback != null) {
				mCallbacks.unregister(callback);
			}
		}

		@Override
		public void registerCallback(IFanFouServiceCallback callback)
				throws RemoteException {
			if (callback != null) {
				mCallbacks.register(callback);
			}
		}

		@Override
		public boolean isRunning() throws RemoteException {
			return true;
		}

		@Override
		public int getPid() throws RemoteException {
			return Process.myPid();
		}
	};

}
