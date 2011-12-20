package com.fanfou.app.util;

import java.io.File;
import java.util.Collection;
import java.util.List;

import android.app.Activity;
import android.app.ActivityManager;
import android.app.ActivityManager.RunningServiceInfo;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.ResultReceiver;
import android.text.TextUtils;
import android.util.Log;
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
import com.fanfou.app.PhotoViewPage;
import com.fanfou.app.R;
import com.fanfou.app.SendPage;
import com.fanfou.app.StatusPage;
import com.fanfou.app.api.ApiException;
import com.fanfou.app.api.DirectMessage;
import com.fanfou.app.api.Status;
import com.fanfou.app.http.ResponseCode;
import com.fanfou.app.service.Constants;

/**
 * 
 * 网络连接包需要用到的一些静态工具函数
 * 
 * @author mcxiaoke
 * @version 1.0 2011.06.01
 * @version 2.0 2011.09.10
 * @version 3.0 2011.09.28
 * @version 3.5 2011.10.28
 * 
 */
public final class Utils {

	private static final String TAG = "Utils";

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

	public static void hideKeyboard(final Context context, final EditText input) {
		InputMethodManager imm = (InputMethodManager) context
				.getSystemService(Context.INPUT_METHOD_SERVICE);
		imm.hideSoftInputFromWindow(input.getWindowToken(), 0);
	}

	public static void goStatusPage(Context context, String id) {
		if (!StringHelper.isEmpty(id)) {
			Intent intent = new Intent(context, StatusPage.class);
			intent.putExtra(Constants.EXTRA_ID, id);
			context.startActivity(intent);
		}
	}

	public static void goStatusPage(Context context, Status s) {
		if (s != null) {
			Intent intent = new Intent(context, StatusPage.class);
			intent.putExtra(Constants.EXTRA_DATA, s);
			context.startActivity(intent);
		}
	}

	public static void goMessageChatPage(Context context, Cursor c) {
		if (c != null) {
			final DirectMessage dm = DirectMessage.parse(c);
			if (dm != null) {
				final Intent intent = new Intent(context, SendPage.class);
				intent.putExtra(Constants.EXTRA_ID, dm.senderId);
				intent.putExtra(Constants.EXTRA_USER_NAME, dm.senderScreenName);
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
		intent.putExtra(Constants.EXTRA_URL, photoUrl);
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
				if (App.DEBUG) {
					Log.d(TAG, "getDmSinceId() id=" + first.id);
				}
				return first.id;
			}
		}
		return null;
	}

	public static String getDmMaxId(Cursor c) {
		if (c != null && c.moveToLast()) {
			final DirectMessage last = DirectMessage.parse(c);
			if (last != null) {
				if (App.DEBUG) {
					Log.d(TAG, "getDmMaxId() id=" + last.id);
				}
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
				if (App.DEBUG) {
					Log.d(TAG, "getSinceId() id=" + first.id);
				}
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
				if (App.DEBUG) {
					Log.d(TAG, "getMaxId() id=" + first.id);
				}
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

	public static void initScreenConfig(final Activity context) {
		boolean portrait = OptionHelper.readBoolean(
				R.string.option_force_portrait, false);
		if (portrait) {
			context.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
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
		boolean portrait = OptionHelper.readBoolean(
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
		boolean portrait = OptionHelper.readBoolean(
				R.string.option_force_portrait, false);
		if (!portrait) {
			activity.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED);
		}
	}

	/**
	 * Checks whether the recording service is currently running.
	 * 
	 * @param ctx
	 *            the current context
	 * @return true if the service is running, false otherwise
	 */
	public static boolean isServiceRunning(Context ctx, Class<?> cls) {
		ActivityManager activityManager = (ActivityManager) ctx
				.getSystemService(Context.ACTIVITY_SERVICE);
		List<RunningServiceInfo> services = activityManager
				.getRunningServices(Integer.MAX_VALUE);

		for (RunningServiceInfo serviceInfo : services) {
			ComponentName componentName = serviceInfo.service;
			String serviceName = componentName.getClassName();
			if (serviceName.equals(cls.getName())) {
				return true;
			}
		}
		return false;
	}

	public static void sendErrorMessage(Context context,
			ResultReceiver receiver, ApiException e) {

		if (receiver != null) {
			String message = e.getMessage();
			if (e.statusCode == ResponseCode.ERROR_IO_EXCEPTION) {
				message = context.getString(R.string.msg_connection_error);
			} else if (e.statusCode >= 500) {
				message = context.getString(R.string.msg_server_error);
			}
			Bundle b = new Bundle();
			b.putInt(Constants.EXTRA_CODE, e.statusCode);
			b.putString(Constants.EXTRA_ERROR, message);
			receiver.send(Constants.RESULT_ERROR, b);
		}
	}

	public static void checkAuthorization(Activity context, int statusCode) {
		if (statusCode == ResponseCode.HTTP_UNAUTHORIZED) {
			IntentHelper.goLoginPage(context);
			context.finish();
		}
	}

}
