package com.fanfou.app.service;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

public class WebService extends Service {

	public static final int STATUS_CREATE = 0;
	public static final int STATUS_SHOW = 1;
	public static final int STATUS_DELETE = 2;
	public static final int STATUS_FAVORITE = 3;
	public static final int STATUS_UNFAVORITE = 4;

	public static final int USER_FOLLOW = 21;
	public static final int USER_UNFOLLOW = 22;
	public static final int USER_BLOCK = 23;
	public static final int USER_UNBLOCK = 24;

	public static final int MESSAGE_CREATE = 31;
	public static final int MESSAGE_DELETE = 32;

	public static final int TIMELINE_HOME = 51;
	public static final int TIMELINE_MENTION = 52;
	public static final int TIMELINE_PUBLIC = 53;
	public static final int TIMELINE_USER = 54;
	public static final int TIMELINE_FAVORITES = 55;
	public static final int TIMELINE_CONTEXT = 56;

	public static final int MESSAGES_INBOX = 71;
	public static final int MESSAGES_OUTBOX = 72;
	public static final int MESSAGES_ALL = 73;

	public WebService() {
	}

	@Override
	public void onCreate() {
		super.onCreate();
	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		return super.onStartCommand(intent, flags, startId);
	}

	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
	}

}
