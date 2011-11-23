package com.fanfou.app.service;

import java.util.List;

import android.content.Intent;
import android.util.Log;

import com.fanfou.app.App;
import com.fanfou.app.App.ApnType;
import com.fanfou.app.api.Api;
import com.fanfou.app.api.ApiException;
import com.fanfou.app.api.FanFouApi;
import com.fanfou.app.api.FanFouApiConfig;
import com.fanfou.app.api.Parser;
import com.fanfou.app.api.User;
import com.fanfou.app.db.Contents.UserInfo;

/**
 * @author mcxiaoke
 * @version 1.0 2011.10.10
 * @version 1.1 2011.11.17
 * @version 2.0 2011.11.18
 * 
 */
public class AutoCompleteService extends WakefulIntentService {
	private static final String TAG = AutoCompleteService.class.getSimpleName();

	public void log(String message) {
		Log.d(TAG, message);
	}

	public AutoCompleteService() {
		super("AutoCompleteService");
	}

	@Override
	protected void onHandleIntent(Intent intent) {
		doFetchAutoComplete();
	}

	private void doFetchAutoComplete() {
		if (App.DEBUG)
			log("doFetchAutoComplete");
		if (!App.me.isLogin) {
			return;
		}
		if(App.me.noConnection){
			return;
		}
		Api api = FanFouApi.getInstance();
		
		int count = FanFouApiConfig.MAX_USERS_COUNT;
		try {
			int nums = 0;
			boolean hasNext = true;
			for (int page = 1; hasNext; page++) {
				List<User> result = api.usersFriends(null, count,page,FanFouApiConfig.MODE_LITE);
				if (result != null && result.size() > 0) {
					int size=result.size();
					for (User u : result) {
						u.type = User.TYPE_FRIENDS;
					}
					if (App.DEBUG)
						log("doFetchAutoComplete page==" + page
								+ " result.size=" + size);
					getContentResolver().bulkInsert(UserInfo.CONTENT_URI,
							Parser.toContentValuesArray(result));
					nums += result.size();
					if (size<count||page >= 20) {
						hasNext = false;
					}
				} else {
					hasNext = false;
				}
			}
		} catch (ApiException e) {
			if (App.DEBUG) {
				Log.e(TAG, ""+e.toString());
				e.printStackTrace();
			}
		}
	}

}
