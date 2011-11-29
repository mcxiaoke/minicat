package com.fanfou.app.service;

import java.util.List;

import android.app.IntentService;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.fanfou.app.App;
import com.fanfou.app.api.Api;
import com.fanfou.app.api.ApiException;
import com.fanfou.app.api.FanFouApi;
import com.fanfou.app.api.FanFouApiConfig;
import com.fanfou.app.api.Parser;
import com.fanfou.app.api.User;
import com.fanfou.app.db.FanFouProvider;
import com.fanfou.app.db.Contents.UserInfo;

/**
 * @author mcxiaoke
 * @version 1.0 2011.10.10
 * @version 1.1 2011.11.17
 * @version 2.0 2011.11.18
 * @version 2.1 2011.11.24
 * @version 2.2 2011.11.28
 * @version 2.3 2011.11.29
 * 
 */
public class AutoCompleteService extends BaseIntentService {
	private static final String TAG = AutoCompleteService.class.getSimpleName();

	public void log(String message) {
		Log.d(TAG, message);
	}

	public AutoCompleteService() {
		super("AutoCompleteService");
	}

	public static void start(Context context) {
		context.startService(new Intent(context, AutoCompleteService.class));
	}

	@Override
	protected void onHandleIntent(Intent intent) {
		doFetchAutoComplete();
	}

	private void doFetchAutoComplete() {
		if (!App.verified) {
			return;
		}
		if (App.noConnection) {
			return;
		}
		Api api = App.api;
		int page = 1;
		boolean more = true;
		while (more) {
			List<User> result = null;
			try {
				result = api.usersFriends(null,
						FanFouApiConfig.MAX_USERS_COUNT, page,
						FanFouApiConfig.MODE_LITE);
			} catch (ApiException e) {
			}
			if (result != null && result.size() > 0) {
				int size = result.size();

				int insertedNums=getContentResolver().bulkInsert(UserInfo.CONTENT_URI,
						Parser.toContentValuesArray(result));
				if (App.DEBUG) {
					log("doFetchAutoComplete page==" + page + " size=" + size+" insert rows="+insertedNums);
				}
				if (size < FanFouApiConfig.MAX_USERS_COUNT || page >= 20) {
					more = false;
				}
			} else {
				more = false;
			}
			page++;
		}
	}

}
