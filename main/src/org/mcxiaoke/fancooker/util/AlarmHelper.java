package org.mcxiaoke.fancooker.util;

import org.mcxiaoke.fancooker.App;
import org.mcxiaoke.fancooker.R;
import org.mcxiaoke.fancooker.service.AutoCompleteService;
import org.mcxiaoke.fancooker.service.DownloadService;
import org.mcxiaoke.fancooker.service.NotificationService;

import android.app.Notification;
import android.content.Context;
import android.content.SharedPreferences.Editor;
import android.media.AudioManager;
import android.net.Uri;
import android.text.TextUtils;
import android.util.Log;

/**
 * @author mcxiaoke
 * @version 1.0 2011.09.22
 * @version 1.1 2011.10.21
 * @version 1.1 2011.10.28
 * @version 2.0 2011.11.24
 * @version 2.5 2011.11.25
 * @version 3.0 2011.12.02
 * @version 3.1 2011.12.05
 * @version 3.2 2011.12.26
 * 
 */
public final class AlarmHelper {
	private static final String TAG = AlarmHelper.class.getSimpleName();

	public static void cleanAlarmFlags(Context context) {
		if (App.DEBUG) {
			Log.d("App", "cleanAlarmFlags");
		}
		Editor editor = App.getPreferences().edit();
		editor.remove(App.getApp().getString(R.string.option_set_auto_clean));
		editor.remove(App.getApp().getString(R.string.option_set_auto_update));
		editor.remove(App.getApp().getString(R.string.option_set_auto_complete));
		editor.remove(App.getApp().getString(R.string.option_set_notification));
		editor.commit();
	}

	public final static void unsetScheduledTasks(Context context) {
		if (App.DEBUG) {
			Log.d(TAG, "unsetScheduledTasks");
		}
		DownloadService.unset(context);
		NotificationService.unset(context);
		AutoCompleteService.unset(context);
	}

	public final static void setScheduledTasks(Context context) {
		if (App.DEBUG) {
			Log.d(TAG, "setScheduledTasks");
		}
		DownloadService.set(context);
		NotificationService.set(context);
		AutoCompleteService.set(context);
	}

	public final static void setAlarmsIfNot(Context context) {
		if (App.DEBUG) {
			Log.d(TAG, "checkScheduledTasks");
		}
		DownloadService.setIfNot(context);
		NotificationService.setIfNot(context);
		AutoCompleteService.setIfNot(context);
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
