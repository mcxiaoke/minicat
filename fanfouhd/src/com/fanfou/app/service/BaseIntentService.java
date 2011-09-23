package com.fanfou.app.service;

import android.app.IntentService;
import android.content.Intent;
import android.os.ResultReceiver;

public abstract class BaseIntentService extends IntentService{

	public BaseIntentService(String name) {
		super(name);
	}

	@Override
	protected abstract void onHandleIntent(Intent intent);

}
