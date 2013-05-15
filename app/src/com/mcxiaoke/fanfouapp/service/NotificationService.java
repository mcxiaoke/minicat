package com.mcxiaoke.fanfouapp.service;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.util.Log;
import com.mcxiaoke.fanfouapp.api.Api;
import com.mcxiaoke.fanfouapp.api.Paging;
import com.mcxiaoke.fanfouapp.app.AppContext;
import com.mcxiaoke.fanfouapp.controller.DataController;
import com.mcxiaoke.fanfouapp.dao.DataProvider;
import com.mcxiaoke.fanfouapp.dao.model.*;
import com.mcxiaoke.fanfouapp.util.DateTimeHelper;
import com.mcxiaoke.fanfouapp.util.IntentHelper;
import com.mcxiaoke.fanfouapp.util.OptionHelper;
import com.mcxiaoke.fanfouapp.util.Utils;
import com.mcxiaoke.fanfouapp.R;

import java.util.Calendar;
import java.util.List;

/**
 * @author mcxiaoke
 * @version 1.0 2011.09.20
 * @version 1.1 2011.11.02
 * @version 1.2 2011.11.03
 * @version 1.3 2011.11.04
 * @version 2.0 2011.11.18
 * @version 2.1 2011.11.23
 * @version 2.2 2011.11.25
 * @version 2.5 2011.12.02
 * @version 2.6 2011.12.09
 * @version 2.7 2011.12.19
 * @version 2.8 2011.12.23
 * @version 2.9 2011.12.26
 * @version 3.0 2011.12.27
 * @version 3.1 2011.12.30
 * @version 3.2 2012.01.16
 * @version 3.9 2012.02.22
 * @version 4.0 2012.02.24
 * 
 */
public class NotificationService extends WakefulIntentService {
	private static final String TAG = NotificationService.class.getSimpleName();

	private Api mApi;

	public NotificationService() {
		super("NotificationService");
	}

	public static void set(Context context, boolean set) {
		if (set) {
			set(context);
		} else {
			unset(context);
		}
	}

	public static void set(Context context) {
		boolean need = OptionHelper.readBoolean(context,
				R.string.option_notification, true);
		if (!need) {
			return;
		}
		int interval = OptionHelper.parseInt(context,
				R.string.option_notification_interval, "5");
		Calendar c = Calendar.getInstance();
		c.add(Calendar.MINUTE, interval);
		AlarmManager am = (AlarmManager) context
				.getSystemService(Context.ALARM_SERVICE);
		am.set(AlarmManager.RTC_WAKEUP, c.getTimeInMillis(),
				getPendingIntent(context));

		if (AppContext.DEBUG) {
			Log.d(TAG, "set interval=" + interval + " next time="
					+ DateTimeHelper.formatDate(c.getTime()));
		}
	}

	public static void unset(Context context) {
		AlarmManager am = (AlarmManager) context
				.getSystemService(Context.ALARM_SERVICE);
		am.cancel(getPendingIntent(context));
		if (AppContext.DEBUG) {
			Log.d(TAG, "unset");
		}
	}

	public static void setIfNot(Context context) {
		boolean set = OptionHelper.readBoolean(context,
				R.string.option_set_notification, false);
		if (AppContext.DEBUG) {
			Log.d(TAG, "setIfNot flag=" + set);
		}
		if (!set) {
			OptionHelper.saveBoolean(context, R.string.option_set_notification,
					true);
			set(context);
		}
	}

	private final static PendingIntent getPendingIntent(Context context) {
		Intent intent = new Intent(context, NotificationService.class);
		PendingIntent pi = PendingIntent.getService(context, 0, intent,
				PendingIntent.FLAG_UPDATE_CURRENT);
		return pi;
	}

	@Override
	public void onCreate() {
		super.onCreate();
		mApi = AppContext.getApi();
	}

	@Override
	protected void doWakefulWork(Intent intent) {
		boolean dm = OptionHelper.readBoolean(this,
				R.string.option_notification_dm, true);
		boolean mention = OptionHelper.readBoolean(this,
				R.string.option_notification_mention, true);
		boolean home = OptionHelper.readBoolean(this,
				R.string.option_notification_home, false);

		int count = FanFouService.DEFAULT_TIMELINE_COUNT;

		if (dm) {
			handleDm(count);
		}
		if (mention) {
			handleMention(count);
		}
		if (home) {
			handleHome(count);
		}
		set(this);
	}

	private void handleDm(int count) {
		Cursor mc = initCursor(DirectMessageModel.TYPE_INBOX);
		List<DirectMessageModel> dms = null;
		Paging p = new Paging();
		p.count = count;
		try {
			dms = mApi.getDirectMessagesInbox(p);
		} catch (Exception e) {
			if (AppContext.DEBUG) {
				Log.e(TAG, e.getMessage());
			}
		}
		mc.close();
		if (dms != null) {
			int size = dms.size();
			if (size > 0) {
				if (AppContext.DEBUG) {
					Log.d(TAG, "handleDm() size=" + size);
				}
				DataController.store(this, dms);
				if (size == 1) {
					notifyDmOne(DirectMessageModel.TYPE_INBOX, dms.get(0));
				} else {
					notifyDmList(DirectMessageModel.TYPE_INBOX, size);
				}
			}
		}

	}

	private void handleMention(int count) {
		Cursor mc = initCursor(StatusModel.TYPE_MENTIONS);
		List<StatusModel> ss = null;
		try {
			Paging p = new Paging();
			p.count = count;
			ss = mApi.getMentions(p);
		} catch (Exception e) {
			if (AppContext.DEBUG) {
				Log.e(TAG, e.getMessage());
			}
		}
		mc.close();
		if (ss != null) {
			notifyStatus(ss, StatusModel.TYPE_MENTIONS);
		}

	}

	private void notifyStatus(List<StatusModel> ss, int type) {
		int size = ss.size();
		if (size > 0) {
			if (AppContext.DEBUG) {
				Log.d(TAG, "notifyStatus() size=" + size);
			}
			DataController.store(this, ss);
			if (size == 1) {
				notifyStatusOne(type, ss.get(0));
			} else {

				notifyStatusList(type, size);
			}

		}
	}

	private void handleHome(int count) {
		Cursor mc = initCursor(StatusModel.TYPE_HOME);
		List<StatusModel> ss = null;
		try {
			Paging p = new Paging();
			p.count = count;
			p.sinceId = Utils.getSinceId(mc);
			ss = mApi.getHomeTimeline(p);
		} catch (Exception e) {
			if (AppContext.DEBUG) {
				Log.e(TAG, e.getMessage());
			}
		}
		mc.close();
		if (ss != null) {
			notifyStatus(ss, StatusModel.TYPE_HOME);
		}

	}

	private void notifyStatusOne(int type, StatusModel status) {
		sendStatusNotification(type, 1, status);
	}

	private void notifyStatusList(int type, int count) {
		sendStatusNotification(type, count, null);
	}

	private void notifyDmOne(int type, DirectMessageModel dm) {
		sendMessageNotification(type, 1, dm);
	}

	private void notifyDmList(int type, int count) {
		sendMessageNotification(type, count, null);
	}

	private Cursor initCursor(int type) {
		String where = IBaseColumns.TYPE + " =? ";
		String[] whereArgs = new String[] { String.valueOf(type) };
		String orderBy = DataProvider.ORDERBY_TIME_DESC;
		Uri uri;
		if (type == DirectMessageModel.TYPE_INBOX) {
			uri = DirectMessageColumns.CONTENT_URI;
		} else {
			uri = StatusColumns.CONTENT_URI;
		}
		return getContentResolver().query(uri, null, where, whereArgs, orderBy);
	}

	private void sendStatusNotification(int type, int count, StatusModel status) {
		if (AppContext.DEBUG) {
			Log.d(TAG, "sendStatusNotification type=" + type + " count="
					+ count + " status=" + (status == null ? "null" : status));
		}
		Intent intent = new Intent();
		intent.putExtra("type", type);
		intent.putExtra("count", count);
		intent.putExtra("data", status);
		intent.setAction(AppContext.packageName + ".NotificationService");
		broadcast(intent);
	}

	private void sendMessageNotification(int type, int count,
			DirectMessageModel dm) {
		if (AppContext.DEBUG) {
			Log.d(TAG, "sendMessageNotification type=" + type + " count="
					+ count + " dm=" + dm);
		}
		Intent intent = new Intent();
		intent.putExtra("type", type);
		intent.putExtra("count", count);
		intent.putExtra("data", dm);
		intent.setAction(AppContext.packageName + ".NotificationService");
		broadcast(intent);
	}

	private void broadcast(Intent intent) {
		intent.setPackage(getPackageName());
		sendOrderedBroadcast(intent, null);
		if (AppContext.DEBUG) {
			Log.d(TAG, "broadcast() ");
			IntentHelper.logIntent(TAG, intent);
		}
	}

}
