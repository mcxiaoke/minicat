package com.fanfou.app.util;

import java.io.File;
import java.util.Collection;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager.NameNotFoundException;
import android.content.res.Configuration;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.ResultReceiver;
import android.text.TextUtils;
import android.util.Log;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.webkit.MimeTypeMap;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.RelativeLayout.LayoutParams;
import android.widget.Toast;

import com.fanfou.app.AboutPage;
import com.fanfou.app.App;
import com.fanfou.app.MessageChatPage;
import com.fanfou.app.PhotoViewPage;
import com.fanfou.app.R;
import com.fanfou.app.StatusPage;
import com.fanfou.app.api.DirectMessage;
import com.fanfou.app.api.Status;
import com.fanfou.app.config.Commons;
import com.fanfou.app.service.AutoCompleteService;
import com.fanfou.app.service.FetchService;

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

	public static void goMessageChatPage(Context context, Cursor c) {
		if (c != null) {
			final DirectMessage dm = DirectMessage.parse(c);
			if (dm != null) {
				final Intent intent = new Intent(context, MessageChatPage.class);
				intent.putExtra(Commons.EXTRA_USER_ID, dm.senderId);
				intent.putExtra(Commons.EXTRA_USER_NAME, dm.senderScreenName);
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

	public static String getDmSinceId(Cursor c) {
		if (c != null && c.moveToFirst()) {
			final DirectMessage first = DirectMessage.parse(c);
			if (first != null) {
				return first.id;
			}
		}
		return null;
	}

	public static String getDmMaxId(Cursor c) {
		if (c != null && c.moveToLast()) {
			final DirectMessage last = DirectMessage.parse(c);
			if (last != null) {
				return last.id;
			}
		}
		return null;
	}

	/**
	 * 获取SinceId
	 * 
	 * @param c
	 * @return
	 */
	public static String getSinceId(Cursor c) {
		if (c != null && c.moveToFirst()) {
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
		if (c != null && c.moveToLast()) {
			Status first = Status.parse(c);
			if (first != null) {
				return first.id;
			}
		}
		return null;
	}

	public static void notify(Context context, CharSequence text) {
		if (TextUtils.isEmpty(text)) {
			return;
		}
		if (App.active) {
			Toast.makeText(context, text, Toast.LENGTH_SHORT).show();
		}
	}

	public static void notify(Context context, int resId) {
		if (App.active) {
			Toast.makeText(context, context.getText(resId), Toast.LENGTH_SHORT)
					.show();
		}
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

	// public static boolean checkMultiTouch(Context context) {
	// if (Build.VERSION.SDK_INT > Build.VERSION_CODES.DONUT) {
	// return context.getPackageManager().hasSystemFeature(
	// PackageManager.FEATURE_TOUCHSCREEN_MULTITOUCH);
	// } else {
	// return false;
	// }
	// }

	public static void setAutoClean(Context context) {
		boolean isSet = OptionHelper.readBoolean(context,
				R.string.option_set_auto_clean, false);
		if (!isSet) {
			OptionHelper.saveBoolean(context, R.string.option_set_auto_clean,
					true);
			AlarmHelper.setCleanTask(context);
		}
	}

	public static void setAutoComplete(Context context) {
		boolean isSet = OptionHelper.readBoolean(context,
				R.string.option_set_auto_complete, false);
		if (!isSet) {
			OptionHelper.saveBoolean(context,
					R.string.option_set_auto_complete, true);
			context.startService(new Intent(context, AutoCompleteService.class));
			AlarmHelper.setAutoCompleteTask(context);
		}
	}

	public static void setAutoNotification(Context context) {
		boolean isSet = OptionHelper.readBoolean(context,
				R.string.option_set_notification, false);
		if (!isSet) {
			OptionHelper.saveBoolean(context, R.string.option_set_notification,
					true);
			AlarmHelper.setNotificationTaskOn(context);
		}
	}

	public static void initScreenConfig(final Activity activity) {
		boolean fullscreen = OptionHelper.readBoolean(activity,
				R.string.option_force_fullscreen, false);
		if (fullscreen) {
			activity.getWindow().setFlags(
					WindowManager.LayoutParams.FLAG_FULLSCREEN,
					WindowManager.LayoutParams.FLAG_FULLSCREEN);
		}

		boolean portrait = OptionHelper.readBoolean(activity,
				R.string.option_force_portrait, false);
		if (portrait) {
			activity.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
		}
	}

	public static void setFullScreen(final Activity activity,
			final boolean fullscreen) {
		if (fullscreen) {
			activity.getWindow().addFlags(
					WindowManager.LayoutParams.FLAG_FULLSCREEN);
			activity.getWindow().clearFlags(
					WindowManager.LayoutParams.FLAG_FORCE_NOT_FULLSCREEN);
		} else {
			activity.getWindow().addFlags(
					WindowManager.LayoutParams.FLAG_FORCE_NOT_FULLSCREEN);
			activity.getWindow().clearFlags(
					WindowManager.LayoutParams.FLAG_FULLSCREEN);
		}
	}

	public static void setPortraitOrientation(final Activity activity,
			final boolean portrait) {
		if (portrait) {
			activity.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
		} else {
			activity.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED);
		}
	}

	public static void lockScreenOrientation(final Activity activity) {
		boolean portrait = OptionHelper.readBoolean(activity,
				R.string.option_force_portrait, false);
		if (portrait) {
			activity.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
		} else {
			if (activity.getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) {
				activity.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
			}
			if (activity.getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT) {
				activity.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
			}
		}
	}

	public static void unlockScreenOrientation(final Activity activity) {
		boolean portrait = OptionHelper.readBoolean(activity,
				R.string.option_force_portrait, false);
		if (!portrait) {
			activity.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED);
		}
	}

}
