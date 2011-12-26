package com.fanfou.app.service;

import java.util.Calendar;
import java.util.List;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.fanfou.app.App;
import com.fanfou.app.R;
import com.fanfou.app.api.Api;
import com.fanfou.app.api.ApiException;
import com.fanfou.app.api.FanFouApi;
import com.fanfou.app.api.Parser;
import com.fanfou.app.api.User;
import com.fanfou.app.db.Contents.UserInfo;
import com.fanfou.app.util.DateTimeHelper;
import com.fanfou.app.util.OptionHelper;

/**
 * @author mcxiaoke
 * @version 1.0 2011.10.10
 * @version 1.1 2011.11.17
 * @version 2.0 2011.11.18
 * @version 2.1 2011.11.24
 * @version 2.2 2011.11.28
 * @version 2.3 2011.11.29
 * @version 2.4 2011.12.19
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

	public static void start(Context context) {
		context.startService(new Intent(context, AutoCompleteService.class));
	}

	public final static void startInMinutes(Context context) {
		Calendar c = Calendar.getInstance();
		c.add(Calendar.MINUTE, 5);
		AlarmManager am = (AlarmManager) context
				.getSystemService(Context.ALARM_SERVICE);
		am.set(AlarmManager.RTC_WAKEUP, c.getTimeInMillis(),
				getPendingIntent(context));
	}

	public static void set(Context context) {
		Calendar c = Calendar.getInstance();
		c.set(c.get(Calendar.YEAR), c.get(Calendar.MONTH),
				c.get(Calendar.DAY_OF_MONTH), 20, 0);
		c.add(Calendar.MINUTE, 30);
		long interval = 5 * 24 * 3600 * 1000;
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
			OptionHelper.saveBoolean(context,R.string.option_set_auto_complete, true);
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
		Api api =FanFouApi.newInstance();
		int page = 1;
		boolean more = true;
		while (more) {
			List<User> result = null;
			try {
				result = api.usersFriends(null, Constants.MAX_USERS_COUNT,
						page, Constants.MODE);
			} catch (ApiException e) {
				if (App.DEBUG) {
					Log.e(TAG, e.toString());
				}
			}
			if (result != null && result.size() > 0) {
				int size = result.size();

				int insertedNums = getContentResolver().bulkInsert(
						UserInfo.CONTENT_URI,
						Parser.toContentValuesArray(result));
				if (App.DEBUG) {
					log("doFetchAutoComplete page==" + page + " size=" + size
							+ " insert rows=" + insertedNums);
				}
				if (size < Constants.MAX_USERS_COUNT || page >= 20) {
					more = false;
				}
			} else {
				more = false;
			}
			page++;
		}
	}

}
