package com.mcxiaoke.minicat.service;

import android.app.IntentService;
import android.content.Intent;

public abstract class BaseIntentService extends IntentService {

    public BaseIntentService(String name) {
        super(name);
    }

    @Override
    protected void onHandleIntent(Intent intent) {
    }

}
