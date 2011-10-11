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
			List<User> users = new ArrayList<User>();
			boolean hasNext = true;
			for (int page = 1; hasNext; page++) {
				List<User> result = api.usersFriends(null, page);
				if (result != null && result.size() > 0) {
					for (User u : result) {
						u.type = User.AUTO_COMPLETE;
					}
					if (App.DEBUG)
						log("doFetchAutoComplete page==" + page
								+ " result.size=" + result.size());
					users.addAll(result);
					if (page >= 10) {
						hasNext = false;
					}
				} else {
					hasNext = false;
				}
			}
			insertAutoComplete(users);
		} catch (ApiException e) {
			if (App.DEBUG) {
				e.printStackTrace();
			}
		}
	}

	private void insertAutoComplete(List<User> users) {
		if (users != null && users.size() > 0) {
			ContentResolver cr = getContentResolver();
			String where = BasicColumns.TYPE + "='" + User.AUTO_COMPLETE + "'";
			int size = users.size();
			if (App.DEBUG)
				log("insertAutoComplete size=" + size);
			// cr.delete(UserInfo.CONTENT_URI, where, null);
			cr.bulkInsert(UserInfo.CONTENT_URI,
					Parser.toContentValuesArray(users));
		}
	}

}
