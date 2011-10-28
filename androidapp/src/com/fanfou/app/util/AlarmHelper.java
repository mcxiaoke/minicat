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
import android.text.TextUtils;
import android.util.Log;

import com.fanfou.app.App;
import com.fanfou.app.R;
import com.fanfou.app.service.AutoCompleteService;
import com.fanfou.app.service.CleanService;
import com.fanfou.app.service.NotificationService;

/**
 * @author mcxiaoke
 * @version 1.0 2011.09.22
 * @version 1.1 2011.10.21
 * @version 1.1 2011.10.28
 * 
 */
public final class AlarmHelper {
	
	public static void clearAlarms(Context context){
		AlarmManager am = (AlarmManager) context
		.getSystemService(Context.ALARM_SERVICE);
		am.cancel(getAutoCompletePendingIntent(context));
		am.cancel(getCleanPendingIntent(context));
		am.cancel(getNotificationPendingIntent(context));
	}

	public static void setCleanTask(Context context) {
		int hour = 4;
		int minute = 0;
		Calendar c = Calendar.getInstance();
		c.set(c.get(Calendar.YEAR), c.get(Calendar.MONTH),
				c.get(Calendar.DAY_OF_MONTH), hour, minute);
		c.add(Calendar.DATE, 10);
		AlarmManager am = (AlarmManager) context
				.getSystemService(Context.ALARM_SERVICE);
		am.setInexactRepeating(AlarmManager.RTC_WAKEUP, c.getTimeInMillis(),
				10 * 24 * 3600 * 1000, getCleanPendingIntent(context));
	}
	

	public static void setUpdateTask(Context context) {
		// TODO Auto-generated method stub
		
	}

	public static void setNotificationTaskOn(Context context) {
		if (App.DEBUG) {
			if (App.DEBUG) {
				Log.i("AlarmHelper", "NotificationService On");
			}
		}
		boolean notificationOn = OptionHelper.readBoolean(context,
				R.string.option_notification, true);
		if (notificationOn) {
			int interval = OptionHelper.parseInt(context,
					R.string.option_notification_interval, "5");

			if (App.DEBUG) {
				interval = 3;
				Log.d("AlarmHelper", "interval=" + interval);
			}

			Calendar c = Calendar.getInstance();
			c.add(Calendar.MINUTE, interval);
			if (c.getTimeInMillis() < System.currentTimeMillis()) {
				c.add(Calendar.DAY_OF_YEAR, 1);
			}
			AlarmManager am = (AlarmManager) context
					.getSystemService(Context.ALARM_SERVICE);
			am.setInexactRepeating(AlarmManager.RTC, c.getTimeInMillis(),
					interval * 60 * 1000, getNotificationPendingIntent(context));
		} else {
			AlarmManager am = (AlarmManager) context
					.getSystemService(Context.ALARM_SERVICE);
			PendingIntent pi = getNotificationPendingIntent(context);
			am.cancel(pi);
			pi.cancel();
		}
	}

	public static void setNotificationTaskOff(Context context) {
		if (App.DEBUG) {
			Log.i("AlarmHelper", "NotificationService Off");
		}
		AlarmManager am = (AlarmManager) context
				.getSystemService(Context.ALARM_SERVICE);
		PendingIntent pi = getNotificationPendingIntent(context);
		am.cancel(pi);
		pi.cancel();
	}

	public static void setAutoCompleteTask(Context context) {
		if (App.DEBUG) {
			Log.i("AlarmHelper", "setAutoCompleteAlarm");
		}
		Calendar c = Calendar.getInstance();
		c.set(c.get(Calendar.YEAR), c.get(Calendar.MONTH),
				c.get(Calendar.DAY_OF_MONTH), 10, 0);
		c.add(Calendar.DATE, 1);
		long interval = 3 * 24 * 3600 * 1000;
		AlarmManager am = (AlarmManager) context
				.getSystemService(Context.ALARM_SERVICE);
		am.setInexactRepeating(AlarmManager.RTC_WAKEUP, c.getTimeInMillis(),
				interval, getAutoCompletePendingIntent(context));
	}

	private static PendingIntent getAutoCompletePendingIntent(Context context) {
		Intent intent = new Intent(context, AutoCompleteService.class);
		PendingIntent pi = PendingIntent.getService(context, 0, intent,
				PendingIntent.FLAG_UPDATE_CURRENT);
		return pi;
	}

	private static PendingIntent getNotificationPendingIntent(Context context) {
		Intent intent = new Intent(context, NotificationService.class);
		PendingIntent pi = PendingIntent.getService(context, 0, intent,
				PendingIntent.FLAG_UPDATE_CURRENT);
		return pi;
	}

	private static PendingIntent getCleanPendingIntent(Context context) {
		return PendingIntent.getService(context, 0, new Intent(context,
				CleanService.class), PendingIntent.FLAG_UPDATE_CURRENT);
	}
	
	

	public static long setTestTime() {
		Calendar c = Calendar.getInstance();
		c.add(Calendar.SECOND, 10);
		long result = c.getTimeInMillis();
		if (App.DEBUG)
			Log.e("AlarmHelper",
					"Alarm test Time:"
							+ DateTimeHelper.formatDate(new Date(result)));
		return result;
	}

	public static void setNotificationType(Context context,
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
	}


}
