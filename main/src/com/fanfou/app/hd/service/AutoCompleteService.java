package com.fanfou.app.hd.service;

import java.util.Calendar;
import java.util.List;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.fanfou.app.hd.App;
import com.fanfou.app.hd.R;
import com.fanfou.app.hd.api.Api;
import com.fanfou.app.hd.api.Paging;
import com.fanfou.app.hd.controller.DataController;
import com.fanfou.app.hd.dao.model.UserColumns;
import com.fanfou.app.hd.dao.model.UserModel;
import com.fanfou.app.hd.util.DateTimeHelper;
import com.fanfou.app.hd.util.OptionHelper;

/**
 * @author mcxiaoke
 * @version 1.0 2011.10.10
 * @version 1.1 2011.11.17
 * @version 2.0 2011.11.18
 * @version 2.1 2011.11.24
 * @version 2.2 2011.11.28
 * @version 2.3 2011.11.29
 * @version 2.4 2011.12.19
 * @version 2.5 2011.12.29
 * @version 2.6 2011.12.30
 * @version 2.7 2012.01.16
 * @version 2.8 2012.02.23
 * @version 2.9 2012.02.24
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

	public static void set(Context context) {
		Calendar c = Calendar.getInstance();
		c.set(c.get(Calendar.YEAR), c.get(Calendar.MONTH),
				c.get(Calendar.DAY_OF_MONTH), 20, 0);
		c.add(Calendar.MINUTE, 30);
		long interval = 7 * 24 * 3600 * 1000;
		AlarmManager am = (AlarmManager) context
				.getSystemService(Context.ALARM_SERVICE);
		am.setInexactRepeating(AlarmManager.RTC_WAKEUP, c.getTimeInMillis(),
				interval, getPendingIntent(context));
		if (App.DEBUG) {
			Log.d(TAG,
					"set repeat interval=3day first time="
							+ DateTimeHelper.formatDate(c.getTime()));
		}
	}

	public static void unset(Context context) {
		AlarmManager am = (AlarmManager) context
				.getSystemService(Context.ALARM_SERVICE);
		am.cancel(getPendingIntent(context));
		if (App.DEBUG) {
			Log.d(TAG, "unset");
		}
	}

	public static void setIfNot(Context context) {
		boolean set = OptionHelper.readBoolean(context,
				R.string.option_set_auto_complete, false);
		if (App.DEBUG) {
			Log.d(TAG, "setIfNot flag=" + set);
		}
		if (!set) {
			OptionHelper.saveBoolean(context,
					R.string.option_set_auto_complete, true);
			set(context);
		}
	}

	private final static PendingIntent getPendingIntent(Context context) {
		Intent intent = new Intent(context, AutoCompleteService.class);
		PendingIntent pi = PendingIntent.getService(context, 0, intent,
				PendingIntent.FLAG_UPDATE_CURRENT);
		return pi;
	}

	@Override
	protected void doWakefulWork(Intent intent) {
		doFetchAutoComplete();
	}

	private void doFetchAutoComplete() {
		if (App.isDisconnected() || !App.isVerified()) {
			return;
		}
		Api api = App.getApi();
		Paging p = new Paging();
		p.count = FanFouService.MAX_USERS_COUNT;
		p.page = 1;
		boolean more = true;
		while (more) {
			List<UserModel> result = null;
			try {
				result = api.getFriends(App.getAccount(), p);
			} catch (Exception e) {
				if (App.DEBUG) {
					Log.e(TAG, e.toString());
				}
			}
			if (result != null && result.size() > 0) {
				int size = result.size();
				int insertedNums = getContentResolver().bulkInsert(
						UserColumns.CONTENT_URI,
						DataController.toContentValues(result));
				if (App.DEBUG) {
					log("doFetchAutoComplete page==" + p.page + " size=" + size
							+ " insert rows=" + insertedNums);
				}
				if (size < FanFouService.MAX_USERS_COUNT || p.page >= 20) {
					more = false;
				}
			} else {
				more = false;
			}
			p.page++;
		}
	}

}
