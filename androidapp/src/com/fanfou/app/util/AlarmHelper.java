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
import com.fanfou.app.api.User;
import com.fanfou.app.config.Commons;
import com.fanfou.app.service.CleanService;
import com.fanfou.app.service.FetchService;
import com.fanfou.app.service.NotificationService;

/**
 * @author mcxiaoke
 * @version 1.0 20110922
 * 
 */
public final class AlarmHelper {

	public static void setCleanTask(Context context) {
		AlarmManager am = (AlarmManager) context
				.getSystemService(Context.ALARM_SERVICE);
		PendingIntent pi = getCleanPendingIntent(context);
		am.cancel(pi);
		am.setInexactRepeating(AlarmManager.RTC_WAKEUP, setAlarmTime(),
				6 * 3600 * 1000, pi);
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
				interval = 1;
				Log.i("AlarmHelper", "interval=" + interval);
			}

			Calendar c = Calendar.getInstance();
			c.add(Calendar.MINUTE, interval);
			if (c.getTimeInMillis() < System.currentTimeMillis()) {
				c.add(Calendar.DAY_OF_YEAR, 1);
			}
			AlarmManager am = (AlarmManager) context
					.getSystemService(Context.ALARM_SERVICE);
			PendingIntent pi = getNotificationPendingIntent(context);
			am.cancel(pi);
			am.setInexactRepeating(AlarmManager.RTC_WAKEUP, c.getTimeInMillis(),
					interval * 60 * 1000, pi);
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
				c.get(Calendar.DAY_OF_MONTH), 4, 0);
		c.add(Calendar.DATE, 1);
		long interval = 48 * 3600 * 1000;
		AlarmManager am = (AlarmManager) context
				.getSystemService(Context.ALARM_SERVICE);
		PendingIntent pi = getAutoCompletePendingIntent(context);
		am.cancel(pi);
		am.setInexactRepeating(AlarmManager.RTC_WAKEUP, c.getTimeInMillis(), interval,
				pi);
	}

	private static PendingIntent getAutoCompletePendingIntent(Context context) {
		Intent intent = new Intent(context, FetchService.class);
		intent.putExtra(Commons.EXTRA_TYPE, User.AUTO_COMPLETE);
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

	public static long setAlarmTime() {
		int hour = 6;
		int minute = 0;
		Calendar c = Calendar.getInstance();
		c.set(c.get(Calendar.YEAR), c.get(Calendar.MONTH),
				c.get(Calendar.DAY_OF_MONTH), hour, minute);
		c.add(Calendar.DATE, 1);
		long result = c.getTimeInMillis();
		if (App.DEBUG)
			Log.d("AlarmHelper",
					"Alarm Time:" + DateTimeHelper.formatDate(new Date(result)));
		return result;
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
		AudioManager audioManager = (AudioManager) context
				.getSystemService(Context.AUDIO_SERVICE);
		switch (audioManager.getRingerMode()) {// 获取系统设置的铃声模式
		case AudioManager.RINGER_MODE_SILENT:// 静音模式，值为0，这时候不震动，不响铃
			notification.sound = null;
			notification.vibrate = null;
			break;
		case AudioManager.RINGER_MODE_VIBRATE:// 震动模式，值为1，这时候震动，不响铃
			notification.sound = null;
			notification.defaults |= Notification.DEFAULT_VIBRATE;
			break;
		case AudioManager.RINGER_MODE_NORMAL:
			notification.defaults |= Notification.DEFAULT_VIBRATE;
			notification.defaults |= Notification.DEFAULT_SOUND;

			Uri ringTone = null;
			String ringFile = OptionHelper.readString(context,
					R.string.option_notification_ringtone, null);
			if (!TextUtils.isEmpty(ringFile)) {
				ringTone = Uri.parse(ringFile);
			}
			notification.sound = ringTone;

			boolean vibrate = OptionHelper.readBoolean(context,
					R.string.option_notification_vibrate, false);
			if (vibrate
					&& audioManager
							.getVibrateSetting(AudioManager.VIBRATE_TYPE_RINGER) == AudioManager.VIBRATE_SETTING_ON) {
				notification.defaults |= Notification.DEFAULT_VIBRATE;
			} else {
				notification.vibrate = null;
			}

			break;
		default:
			break;
		}
	}

}
