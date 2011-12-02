package com.fanfou.app.util;

import java.util.Calendar;
import java.util.Date;

import android.app.AlarmManager;
import android.app.Notification;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;
import android.net.Uri;
import android.os.SystemClock;
import android.text.TextUtils;
import android.util.Log;

import com.fanfou.app.App;
import com.fanfou.app.R;
import com.fanfou.app.config.Commons;
import com.fanfou.app.service.AutoCompleteService;
import com.fanfou.app.service.CleanService;
import com.fanfou.app.service.DownloadService;
import com.fanfou.app.service.NotificationService;

/**
 * @author mcxiaoke
 * @version 1.0 2011.09.22
 * @version 1.1 2011.10.21
 * @version 1.1 2011.10.28
 * @version 2.0 2011.11.24
 * @version 2.5 2011.11.25
 * @version 3.0 2011.12.02
 * 
 */
public final class AlarmHelper {
	private static final String TAG = AlarmHelper.class.getSimpleName();

	public final static void clearAlarms(Context context) {
		if (App.DEBUG) {
			Log.d(TAG, "clearAlarms");
		}
		AlarmManager am = (AlarmManager) context
				.getSystemService(Context.ALARM_SERVICE);
		am.cancel(getAutoCompletePendingIntent(context));
		am.cancel(getAutoUpdatePendingIntent(context));
	}
	
	public final static void setAlarms(Context context) {
		if (App.DEBUG) {
			Log.d(TAG, "setAlarms");
		}
		AlarmHelper.checkAutoCompleteSet(context);
		AlarmHelper.checkAutoUpdateSet(context);
		NotificationService.setIfNot(context);
	}

	@SuppressWarnings("unused")
	private final static void checkAutoCleanSet(Context context) {
		boolean isSet = OptionHelper.readBoolean(context,
				R.string.option_set_auto_clean, false);
		if (App.DEBUG) {
			Log.d(TAG, "checkAutoCleanSet flag=" + isSet);
		}
		if (!isSet) {
			AlarmHelper.setCleanTask(context);
			OptionHelper.saveBoolean(context, R.string.option_set_auto_clean,
					true);
		}
	}

	private final static void setCleanTask(Context context) {
		Calendar c = Calendar.getInstance();
		c.set(c.get(Calendar.YEAR), c.get(Calendar.MONTH),
				c.get(Calendar.DAY_OF_MONTH), 12, 0);
		c.add(Calendar.DATE, 10);
		AlarmManager am = (AlarmManager) context
				.getSystemService(Context.ALARM_SERVICE);
		am.setInexactRepeating(AlarmManager.RTC_WAKEUP, c.getTimeInMillis(),
				10 * 24 * 3600 * 1000, getCleanPendingIntent(context));
		if (App.DEBUG) {
			Log.d(TAG,
					"setCleanTask first time="
							+ DateTimeHelper.formatDate(c.getTime()));
		}
	}

	public final static void checkAutoUpdateSet(Context context) {
		boolean isSet = OptionHelper.readBoolean(context,
				R.string.option_set_auto_update, false);
		if (App.DEBUG) {
			Log.d(TAG, "checkAutoUpdateSet flag=" + isSet);
		}
		if (!isSet) {
			boolean auto=OptionHelper.readBoolean(context, R.string.option_autoupdate, true);
			if(auto){
				AlarmHelper.setAutoUpdateTask(context);
			}else{
				AlarmHelper.removeAutoUpdateTask(context);
			}
			OptionHelper.saveBoolean(context, R.string.option_set_auto_update,
					true);
		}
	}
	
	

	public final static void setAutoUpdateTask(Context context) {
		Calendar c = Calendar.getInstance();
		c.set(c.get(Calendar.YEAR), c.get(Calendar.MONTH),
				c.get(Calendar.DAY_OF_MONTH), 11, 0);
		c.add(Calendar.DATE, 1);
		long interval = 3 * 24 * 3600 * 1000;
		AlarmManager am = (AlarmManager) context
				.getSystemService(Context.ALARM_SERVICE);
		am.setInexactRepeating(AlarmManager.RTC, c.getTimeInMillis(), interval,
				getAutoUpdatePendingIntent(context));
		if (App.DEBUG) {
			Log.d(TAG,
					"setAutoUpdateTask first time="
							+ DateTimeHelper.formatDate(c.getTime()));
		}

	}

	public final static void removeAutoUpdateTask(Context context) {
		AlarmManager am = (AlarmManager) context
				.getSystemService(Context.ALARM_SERVICE);
		PendingIntent pi = getAutoUpdatePendingIntent(context);
		am.cancel(pi);
		pi.cancel();
	}

	public final static void checkAutoCompleteSet(Context context) {
		boolean isSet = OptionHelper.readBoolean(context,
				R.string.option_set_auto_complete, false);
		if (App.DEBUG) {
			Log.d(TAG, "checkAutoCompleteSet flag=" + isSet);
		}
		if (!isSet) {
			AlarmHelper.setAutoCompleteTask(context);
			OptionHelper.saveBoolean(context,
					R.string.option_set_auto_complete, true);
		}
	}

	public final static void startAutoComplete(Context context) {
		Calendar c = Calendar.getInstance();
		c.set(c.get(Calendar.YEAR), c.get(Calendar.MONTH),
				c.get(Calendar.DAY_OF_MONTH), 8, 0);
		c.add(Calendar.MINUTE, 5);
		AlarmManager am = (AlarmManager) context
				.getSystemService(Context.ALARM_SERVICE);
		am.set(AlarmManager.RTC, c.getTimeInMillis(),
				getAutoCompletePendingIntent(context));
		if (App.DEBUG) {
			Log.d(TAG,
					"startAutoComplete time="
							+ DateTimeHelper.formatDate(c.getTime()));
		}
	}

	public final static void setAutoCompleteTask(Context context) {
		Calendar c = Calendar.getInstance();
		c.set(c.get(Calendar.YEAR), c.get(Calendar.MONTH),
				c.get(Calendar.DAY_OF_MONTH), 8, 0);
		c.add(Calendar.HOUR_OF_DAY, 1);
		long interval = 3 * 24 * 3600 * 1000;
		AlarmManager am = (AlarmManager) context
				.getSystemService(Context.ALARM_SERVICE);
		am.setInexactRepeating(AlarmManager.RTC, c.getTimeInMillis(), interval,
				getAutoCompletePendingIntent(context));
		if (App.DEBUG) {
			Log.d(TAG,
					"setAutoCompleteTask first time="
							+ DateTimeHelper.formatDate(c.getTime()));
		}
	}

	private final static PendingIntent getAutoUpdatePendingIntent(
			Context context) {
		Intent intent = new Intent(context, DownloadService.class);
		intent.putExtra(Commons.EXTRA_TYPE, DownloadService.TYPE_CHECK);
		PendingIntent pi = PendingIntent.getService(context, 0, intent,
				PendingIntent.FLAG_UPDATE_CURRENT);
		return pi;
	}

	private final static PendingIntent getAutoCompletePendingIntent(
			Context context) {
		Intent intent = new Intent(context, AutoCompleteService.class);
		PendingIntent pi = PendingIntent.getService(context, 0, intent,
				PendingIntent.FLAG_UPDATE_CURRENT);
		return pi;
	}

	private final static PendingIntent getCleanPendingIntent(Context context) {
		return PendingIntent.getService(context, 0, new Intent(context,
				CleanService.class), PendingIntent.FLAG_UPDATE_CURRENT);
	}

	public final static long setTestTime() {
		Calendar c = Calendar.getInstance();
		c.add(Calendar.SECOND, 10);
		long result = c.getTimeInMillis();
		if (App.DEBUG)
			Log.d("AlarmHelper",
					"Alarm test Time:"
							+ DateTimeHelper.formatDate(new Date(result)));
		return result;
	}

	public final static void setNotificationType(Context context,
			Notification notification) {
		AudioManager am = (AudioManager) context
				.getSystemService(Context.AUDIO_SERVICE);
		if (am.getRingerMode() == AudioManager.RINGER_MODE_NORMAL) {
			String ringFile = OptionHelper.readString(context,
					R.string.option_notification_ringtone, null);
			Uri ringTone = null;
			if (!TextUtils.isEmpty(ringFile)) {
				ringTone = Uri.parse(ringFile);
				notification.sound = ringTone;
			}
		}

		boolean vibrate = OptionHelper.readBoolean(context,
				R.string.option_notification_vibrate, false);
		if (vibrate) {
			notification.defaults |= Notification.DEFAULT_VIBRATE;
		} else {
			notification.vibrate = null;
		}

		boolean led = OptionHelper.readBoolean(context,
				R.string.option_notification_led, false);
		if (led) {
			notification.defaults |= Notification.DEFAULT_LIGHTS;
		} else {
			notification.ledOnMS = 0;
			notification.ledOffMS = 0;
		}
	}

}
