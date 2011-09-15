package com.fanfou.app.util;

import java.io.File;
import java.net.URL;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Map;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager.NameNotFoundException;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.ResultReceiver;
import android.util.Log;
import android.view.inputmethod.InputMethodManager;
import android.webkit.MimeTypeMap;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.Toast;
import android.widget.RelativeLayout.LayoutParams;

import com.fanfou.app.AboutPage;
import com.fanfou.app.App;
import com.fanfou.app.MyProfilePage;
import com.fanfou.app.PhotoViewPage;
import com.fanfou.app.R;
import com.fanfou.app.SendPage;
import com.fanfou.app.StatusPage;
import com.fanfou.app.ProfilePage;
import com.fanfou.app.api.DirectMessage;
import com.fanfou.app.api.Status;
import com.fanfou.app.api.User;
import com.fanfou.app.config.ACTION;
import com.fanfou.app.config.Commons;
import com.fanfou.app.db.Contents.UserInfo;
import com.fanfou.app.receiver.AlarmReceiver;
import com.fanfou.app.service.CleanService;
import com.fanfou.app.service.FetchService;
import com.google.android.apps.analytics.GoogleAnalyticsTracker;

/**
 * 
 * 网络连接包需要用到的一些静态工具函数
 * 
 * @author mcxiaoke
 * 
 */
public final class Utils {

	/**
	 * @param c
	 *            集合
	 * @return 判断集合对象是否为空
	 */
	public static boolean isEmpty(Collection<?> c) {
		return c == null || c.size() == 0;
	}

	public static boolean isEmpty(String str) {
		return str == null || str == "";
	}

	public static String getViersionInfo() {
		try {
			PackageInfo pkg = App.me.getPackageManager().getPackageInfo(
					App.me.getPackageName(), 0);
			String versionName = pkg.versionName;
			String versionCode = String.valueOf(pkg.versionCode);
			return versionName + " (Build" + versionCode + ")";
		} catch (NameNotFoundException e) {
			if (App.DEBUG)
				e.printStackTrace();
		}

		return "1.0";

	}

	public static void hideKeyboard(final Context context, final EditText input) {
		InputMethodManager imm = (InputMethodManager) context
				.getSystemService(Context.INPUT_METHOD_SERVICE);
		imm.hideSoftInputFromWindow(input.getWindowToken(), 0);
	}

	public static void startFetchService(Context context, int type,
			ResultReceiver receiver, Bundle bundle) {
		Intent serviceIntent = new Intent(context, FetchService.class);
		serviceIntent.putExtra(Commons.EXTRA_TYPE, type);
		serviceIntent.putExtra(Commons.EXTRA_BUNDLE, bundle);
		serviceIntent.putExtra(Commons.EXTRA_RECEIVER, receiver);
		context.startService(serviceIntent);
	}

	public static void goStatusPage(Context context, String id) {
		if (!StringHelper.isEmpty(id)) {
			Intent intent = new Intent(context, StatusPage.class);
			intent.putExtra(Commons.EXTRA_STATUS_ID, id);
			context.startActivity(intent);
		}
	}

	public static void goStatusPage(Context context, Status s) {
		if (s != null) {
			Intent intent = new Intent(context, StatusPage.class);
			intent.putExtra(Commons.EXTRA_STATUS, s);
			context.startActivity(intent);
		}
	}

	public static void goSendPage(Context context, Cursor c) {
		if (c != null) {
			final DirectMessage dm = DirectMessage.parse(c);
			if (dm != null) {
				final Intent intent = new Intent(context, SendPage.class);
				intent.putExtra(Commons.EXTRA_MESSAGE, dm);
				context.startActivity(intent);
			}
		}
	}

	public static void goAboutPage(Context context) {
		Intent intent = new Intent(context, AboutPage.class);
		context.startActivity(intent);
	}

	public static void goPhotoViewPage(Context context, String photoUrl) {
		Intent intent = new Intent(context, PhotoViewPage.class);
		intent.putExtra(Commons.EXTRA_PHOTO_URL, photoUrl);
		context.startActivity(intent);
	}

	public static ProgressBar createProgress(Context context) {
		ProgressBar p = new ProgressBar(context);
		p.setIndeterminate(true);
		LayoutParams lp = new LayoutParams(40, 40);
		lp.addRule(RelativeLayout.CENTER_IN_PARENT);
		p.setLayoutParams(lp);
		return p;
	}

	/**
	 * 获取SinceId
	 * 
	 * @param c
	 * @return
	 */
	public static String getSinceId(Cursor c) {
		if (c != null && c.getCount() > 0) {
			c.moveToFirst();
			Status first = Status.parse(c);
			if (first != null) {
				return first.id;
			}
		}
		return null;
	}

	/**
	 * 获取MaxId
	 * 
	 * @param c
	 * @return
	 */
	public static String getMaxId(Cursor c) {
		if (c != null && c.getCount() > 0) {
			c.moveToLast();
			Status first = Status.parse(c);
			if (first != null) {
				return first.id;
			}
		}
		return null;
	}

	public static void notify(Context context, String text) {
		Toast.makeText(context, text, Toast.LENGTH_SHORT).show();
	}

	public static PendingIntent getCleanPendingIntent(Context context) {
		return PendingIntent.getService(context, 0, new Intent(context,
				CleanService.class), 0);
	}
	
	public static void addCleanTask(Context context){
		AlarmManager am = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
		am.setRepeating(AlarmManager.RTC_WAKEUP, Utils.setAlarmTime(),
				6 * 3600 * 1000, getCleanPendingIntent(context));
	}
	
	public static void removeCleanTask(Context context){
		AlarmManager am = (AlarmManager) context
		.getSystemService(Context.ALARM_SERVICE);
		am.cancel(getCleanPendingIntent(context));
	}

	public static long setAlarmTime() {
		int hour = 7;
		int minute = 0;
		Calendar c = Calendar.getInstance();
		c.set(c.get(Calendar.YEAR), c.get(Calendar.MONTH),
				c.get(Calendar.DAY_OF_MONTH), hour, minute);
		c.add(Calendar.DATE, 1);
		long result = c.getTimeInMillis();
		if (App.DEBUG)
			Log.d("setAlarmTime()",
					"Alarm Time:" + DateTimeHelper.formatDate(new Date(result)));
		return result;
	}

	public static long setTestTime() {
		Calendar c = Calendar.getInstance();
		c.add(Calendar.SECOND, 10);
		long result = c.getTimeInMillis();
		if (App.DEBUG)
			Log.e("setTestTime()",
					"Alarm Time:" + DateTimeHelper.formatDate(new Date(result)));
		return result;
	}

	public static void open(Context context, final String fileName) {
		String mimeType = MimeTypeMap.getSingleton().getMimeTypeFromExtension(
				getExtension(fileName));
		if (mimeType != null) {
			Intent intent = new Intent(Intent.ACTION_VIEW);
			intent.setDataAndType(Uri.fromFile(new File(fileName)), mimeType);
			intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
			context.startActivity(intent);
		}
	}

	public static String getExtension(String filename) {
		// int slashIndex = url.lastIndexOf('/');
		// int dotIndex = url.lastIndexOf('.', slashIndex);
		// String filenameWithoutExtension;
		// if (dotIndex == -1)
		// {
		// filenameWithoutExtension = url.substring(slashIndex + 1);
		// }
		// else
		// {
		// filenameWithoutExtension = url.substring(slashIndex + 1, dotIndex);
		// }
		String filenameArray[] = filename.split("\\.");
		return filenameArray[filenameArray.length - 1].toLowerCase();
	}

	public static void logTime(String event, long time) {
		Log.e("Timer", event + " use time: " + time);
	}

	public static float easeOut(float time, float start, float end,
			float duration) {
		return end * ((time = time / duration - 1) * time * time + 1) + start;
	}

}
