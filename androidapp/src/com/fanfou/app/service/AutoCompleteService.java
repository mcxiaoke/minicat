package com.fanfou.app.service;

import java.util.ArrayList;
import java.util.List;

import android.content.ContentResolver;
import android.content.Intent;
import android.util.Log;

import com.fanfou.app.App;
import com.fanfou.app.api.Api;
import com.fanfou.app.api.ApiException;
import com.fanfou.app.api.Parser;
import com.fanfou.app.api.User;
import com.fanfou.app.db.Contents.BasicColumns;
import com.fanfou.app.db.Contents.UserInfo;

/**
 * @author mcxiaoke
 * @version 1.0 2011.10.10
 * 
 */
public class AutoCompleteService extends WakefulIntentService {
	private static final String TAG = AutoCompleteService.class.getSimpleName();

	public void log(String message) {
		Log.i(TAG, message);
	}

	public AutoCompleteService() {
		super("AutoCompleteService");
	}

	@Override
	protected void onHandleIntent(Intent intent) {
		doFetchAutoComplete();
	}

	private void doFetchAutoComplete() {
		if (!App.me.isLogin) {
			return;
		}
		if (App.DEBUG)
			log("doFetchAutoComplete");
		Api api = App.me.api;
		try {
			int size = 0;
			boolean hasNext = true;
			for (int page = 1; hasNext; page++) {
				List<User> result = api.usersFriends(null, page);
				if (result != null && result.size() > 0) {
					for (User u : result) {
						u.type = User.TYPE_FRIENDS;
					}
					if (App.DEBUG)
						log("doFetchAutoComplete page==" + page
								+ " result.size=" + result.size());
					getContentResolver().bulkInsert(UserInfo.CONTENT_URI, Parser.toContentValuesArray(result));
					size += result.size();
					if (page >= 10) {
						hasNext = false;
					}
				} else {
					hasNext = false;
				}
			}
		} catch (ApiException e) {
			if (App.DEBUG) {
				Log.e(TAG, e.getMessage());
				e.printStackTrace();
			}
		}
	}

}
