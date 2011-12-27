package com.fanfou.app.service;

import java.util.Calendar;
import java.util.List;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.SystemClock;
import android.util.Log;

import com.fanfou.app.App;
import com.fanfou.app.App.ApnType;
import com.fanfou.app.R;
import com.fanfou.app.api.Api;
import com.fanfou.app.api.ApiException;
import com.fanfou.app.api.DirectMessage;
import com.fanfou.app.api.FanFouApi;
import com.fanfou.app.api.Parser;
import com.fanfou.app.api.Status;
import com.fanfou.app.db.Contents.BasicColumns;
import com.fanfou.app.db.Contents.DirectMessageInfo;
import com.fanfou.app.db.Contents.StatusInfo;
import com.fanfou.app.db.FanFouProvider;
import com.fanfou.app.util.DateTimeHelper;
import com.fanfou.app.util.IntentHelper;
import com.fanfou.app.util.OptionHelper;
import com.fanfou.app.util.Utils;

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
 * 
 */
public class NotificationService extends WakefulIntentService {
	private static final String TAG = NotificationService.class.getSimpleName();

	public static final int NOTIFICATION_TYPE_HOME = Constants.TYPE_STATUSES_HOME_TIMELINE;
	public static final int NOTIFICATION_TYPE_MENTION = Constants.TYPE_STATUSES_MENTIONS; // @消息
	public static final int NOTIFICATION_TYPE_DM = Constants.TYPE_DIRECT_MESSAGES_INBOX; // 私信

	private static final int DEFAULT_COUNT = Constants.DEFAULT_TIMELINE_COUNT;
	private static final int MAX_COUNT = Constants.MAX_TIMELINE_COUNT;
	private static final int DEFAULT_PAGE = 0;
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

		if (App.DEBUG) {
			Log.d(TAG, "set interval=" + interval + " next time="
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
				R.string.option_set_notification, false);
		if (App.DEBUG) {
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
	protected void onHandleIntent(Intent intent) {
		boolean need = OptionHelper.readBoolean(this,
				R.string.option_notification, false);
		if (!need) {
			return;
		}

		mApi = FanFouApi.newInstance();

		boolean dm = OptionHelper.readBoolean(this,
				R.string.option_notification_dm, false);
		boolean mention = OptionHelper.readBoolean(this,
				R.string.option_notification_mention, false);
		boolean home = OptionHelper.readBoolean(this,
				R.string.option_notification_home, false);

		int count = DEFAULT_COUNT;
		if (App.getApnType() == ApnType.WIFI) {
			count = MAX_COUNT;
		}
		if (dm) {
			handleDm(count);
			SystemClock.sleep(500);
		}
		if (mention) {
			handleMention(count);
			SystemClock.sleep(500);
		}
		if (home) {
			handleHome(count);
		}
		set(this);
	}

	private void handleDm(int count) {
		Cursor mc = initCursor(Constants.TYPE_DIRECT_MESSAGES_INBOX);
		List<DirectMessage> dms = null;
		try {
			dms = mApi.directMessagesInbox(count, DEFAULT_PAGE,
					Utils.getDmSinceId(mc), null, Constants.MODE);
		} catch (ApiException e) {
			if (App.DEBUG) {
				Log.e(TAG,
						" code=" + e.statusCode + " message=" + e.getMessage());
			}
		} catch (Exception e) {
			if (App.DEBUG) {
				Log.e(TAG, e.getMessage());
			}
		}
		mc.close();
		if (dms != null) {
			int size = dms.size();
			if (size > 0) {
				if (App.DEBUG) {
					Log.d(TAG, "handleDm() size=" + size);
				}
				if (size == 1) {
					DirectMessage dm = dms.get(0);
					getContentResolver().insert(DirectMessageInfo.CONTENT_URI,
							dm.toContentValues());
					notifyDmOne(NOTIFICATION_TYPE_DM, dms.get(0));
				} else {
					getContentResolver().bulkInsert(
							DirectMessageInfo.CONTENT_URI,
							Parser.toContentValuesArray(dms));
					notifyDmList(NOTIFICATION_TYPE_DM, size);
				}
				getContentResolver().notifyChange(
						DirectMessageInfo.CONTENT_URI, null, false);
			}
		}

	}

	private void handleMention(int count) {
		Cursor mc = initCursor(Constants.TYPE_STATUSES_MENTIONS);
		List<Status> ss = null;
		try {
			ss = mApi.mentions(count, DEFAULT_PAGE, Utils.getSinceId(mc), null,
					Constants.FORMAT, Constants.MODE);
		} catch (ApiException e) {
			if (App.DEBUG) {
				Log.e(TAG,
						" code=" + e.statusCode + " message=" + e.getMessage());
			}
		} catch (Exception e) {
			if (App.DEBUG) {
				Log.e(TAG, e.getMessage());
			}
		}
		mc.close();
		if (ss != null) {
			int size = ss.size();
			if (size > 0) {
				if (App.DEBUG) {
					Log.d(TAG, "handleMention() size=" + size);
				}
				if (size == 1) {
					Status s = ss.get(0);
					getContentResolver().insert(StatusInfo.CONTENT_URI,
							s.toContentValues());
					notifyStatusOne(NOTIFICATION_TYPE_MENTION, s);
				} else {
					getContentResolver().bulkInsert(StatusInfo.CONTENT_URI,
							Parser.toContentValuesArray(ss));
					notifyStatusList(NOTIFICATION_TYPE_MENTION, size);
				}
				getContentResolver().notifyChange(StatusInfo.CONTENT_URI, null,
						false);
			}
		}

	}

	private void handleHome(int count) {
		Cursor mc = initCursor(Constants.TYPE_STATUSES_HOME_TIMELINE);
		List<Status> ss = null;
		try {
			ss = mApi.homeTimeline(count, DEFAULT_PAGE, Utils.getSinceId(mc),
					null, Constants.FORMAT, Constants.MODE);
		} catch (ApiException e) {
			if (App.DEBUG) {
				Log.e(TAG,
						" code=" + e.statusCode + " message=" + e.getMessage());
			}
		} catch (Exception e) {
			if (App.DEBUG) {
				Log.e(TAG, e.getMessage());
			}
		}
		mc.close();
		if (ss != null) {
			int size = ss.size();
			if (size > 0) {
				if (App.DEBUG) {
					Log.d(TAG, "handleHome() size=" + size);
				}
				if (size == 1) {
					Status s = ss.get(0);
					getContentResolver().insert(StatusInfo.CONTENT_URI,
							s.toContentValues());
					notifyStatusOne(NOTIFICATION_TYPE_HOME, ss.get(0));
				} else {
					getContentResolver().bulkInsert(StatusInfo.CONTENT_URI,
							Parser.toContentValuesArray(ss));
					notifyStatusList(NOTIFICATION_TYPE_HOME, size);
				}
				getContentResolver().notifyChange(StatusInfo.CONTENT_URI, null,
						false);
			}
		}

	}

	private void notifyStatusOne(int type, Status status) {
		sendStatusNotification(type, 1, status);
	}

	private void notifyStatusList(int type, int count) {
		sendStatusNotification(type, count, null);
	}

	private void notifyDmOne(int type, DirectMessage dm) {
		sendMessageNotification(type, 1, dm);
	}

	private void notifyDmList(int type, int count) {
		sendMessageNotification(type, count, null);
	}

	private Cursor initCursor(int type) {
		String where = BasicColumns.TYPE + " =? ";
		String[] whereArgs = new String[] { String.valueOf(type) };
		Uri uri = StatusInfo.CONTENT_URI;
		String[] columns = StatusInfo.COLUMNS;
		String orderBy = FanFouProvider.ORDERBY_DATE_DESC;
		if (type == Constants.TYPE_DIRECT_MESSAGES_INBOX) {
			uri = DirectMessageInfo.CONTENT_URI;
			columns = DirectMessageInfo.COLUMNS;
		}
		return getContentResolver().query(uri, columns, where, whereArgs,
				orderBy);
	}

	private void sendStatusNotification(int type, int count, Status status) {
		if (App.DEBUG) {
			Log.d(TAG, "sendStatusNotification type=" + type + " count="
					+ count + " status=" + (status == null ? "null" : status)
					+ " active=" + App.active);
		}
		Intent intent = new Intent();
		intent.putExtra(Constants.EXTRA_TYPE, type);
		intent.putExtra(Constants.EXTRA_COUNT, count);
		intent.putExtra(Constants.EXTRA_DATA, status);
		intent.setAction(Constants.ACTION_NOTIFICATION);
		broadcast(intent);
	}

	private void sendMessageNotification(int type, int count, DirectMessage dm) {
		if (App.DEBUG) {
			Log.d(TAG, "sendMessageNotification type=" + type + " count="
					+ count + " dm=" + dm);
		}
		Intent intent = new Intent();
		intent.putExtra(Constants.EXTRA_TYPE, type);
		intent.putExtra(Constants.EXTRA_COUNT, count);
		intent.putExtra(Constants.EXTRA_DATA, dm);
		intent.setAction(Constants.ACTION_NOTIFICATION);
		broadcast(intent);
	}

	private void broadcast(Intent intent) {
		intent.setPackage(getPackageName());
		sendOrderedBroadcast(intent, null);
		if (App.DEBUG) {
			Log.d(TAG, "broadcast() ");
			IntentHelper.logIntent(TAG, intent);
		}
	}

}
