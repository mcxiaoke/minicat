package com.fanfou.app.service;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

public class EmptyService extends Service {
	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}
}
