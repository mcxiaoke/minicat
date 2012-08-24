package com.fanfou.app.hd.service;

import android.app.IntentService;
import android.content.Intent;

public abstract class BaseIntentService extends IntentService {

	@Override
	protected void onHandleIntent(Intent intent) {
	}

	public BaseIntentService(String name) {
		super(name);
	}

}
