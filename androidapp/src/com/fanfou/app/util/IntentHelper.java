package com.fanfou.app.util;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.fanfou.app.App;
import com.fanfou.app.UIHome;
import com.fanfou.app.LoginPage;
import com.fanfou.app.R;
import com.fanfou.app.service.Constants;

public final class IntentHelper {
	private static final String TAG = IntentHelper.class.getSimpleName();

	public static void goHomePage(Context context, final int page) {
		Intent intent = new Intent(context, UIHome.class);
		intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
		intent.putExtra(Constants.EXTRA_PAGE, page);
		context.startActivity(intent);
	}

	public static void goLoginPage(Context context) {
		AlarmHelper.unsetScheduledTasks(context);
		App.removeAccountInfo(context);
		App.getImageLoader().clearQueue();
		Intent intent = new Intent(context, LoginPage.class);
		intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
		context.startActivity(intent);
	}

	public static Intent getLauncherIntent() {
		Intent intent = new Intent(Intent.ACTION_MAIN);
		intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		intent.addCategory(Intent.CATEGORY_HOME);
		return intent;
	}

	public static void sendFeedback(Context context, String content) {
		final Intent emailIntent = new Intent(Intent.ACTION_SEND);
		emailIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		emailIntent.setType("message/rfc822");
		String subject = "饭否Android客户端意见反馈";
		emailIntent.putExtra(android.content.Intent.EXTRA_SUBJECT, subject);
		emailIntent.putExtra(android.content.Intent.EXTRA_TEXT, content);
		emailIntent.putExtra(android.content.Intent.EXTRA_EMAIL,
				new String[] { context
						.getString(R.string.config_feedback_email) });
		context.startActivity(Intent.createChooser(emailIntent, "发送反馈"));
	}

	public static void logIntent(String tag, Intent intent) {
		if (intent == null) {
			return;
		}
		StringBuffer sb = new StringBuffer();
		sb.append("\nAction:" + intent.getAction());
		sb.append("\nData:" + intent.getData());
		sb.append("\nDataStr:" + intent.getDataString());
		sb.append("\nScheme:" + intent.getScheme());
		sb.append("\nType:" + intent.getType());
		Bundle extras = intent.getExtras();
		if (extras != null && !extras.isEmpty()) {
			for (String key : extras.keySet()) {
				Object value = extras.get(key);
				sb.append("\nEXTRA: {" + key + "::" + value + "}");
			}
		} else {
			sb.append("\nNO EXTRAS");
		}
		Log.i(tag, sb.toString());
	}

	public static int sdkVersion() {
		return new Integer(Build.VERSION.SDK).intValue();
	}

	public static void startDialer(Context context, String phoneNumber) {
		try {
			Intent dial = new Intent();
			dial.setAction(Intent.ACTION_DIAL);
			dial.setData(Uri.parse("tel:" + phoneNumber));
			context.startActivity(dial);
		} catch (Exception ex) {
			Log.e(TAG, "Error starting phone dialer intent.", ex);
			Toast.makeText(context,
					"Sorry, we couldn't find any app to place a phone call!",
					Toast.LENGTH_SHORT).show();
		}
	}

	public static void startSmsIntent(Context context, String phoneNumber) {
		try {
			Uri uri = Uri.parse("sms:" + phoneNumber);
			Intent intent = new Intent(Intent.ACTION_VIEW, uri);
			intent.putExtra("address", phoneNumber);
			intent.setType("vnd.android-dir/mms-sms");
			context.startActivity(intent);
		} catch (Exception ex) {
			Log.e(TAG, "Error starting sms intent.", ex);
			Toast.makeText(context,
					"Sorry, we couldn't find any app to send an SMS!",
					Toast.LENGTH_SHORT).show();
		}
	}

	public static void startEmailIntent(Context context, String emailAddress) {
		try {
			Intent intent = new Intent(android.content.Intent.ACTION_SEND);
			intent.setType("plain/text");
			intent.putExtra(android.content.Intent.EXTRA_EMAIL,
					new String[] { emailAddress });
			context.startActivity(intent);
		} catch (Exception ex) {
			Log.e(TAG, "Error starting email intent.", ex);
			Toast.makeText(context,
					"Sorry, we couldn't find any app for sending emails!",
					Toast.LENGTH_SHORT).show();
		}
	}

	public static void startWebIntent(Context context, String url) {
		try {
			Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
			context.startActivity(intent);
		} catch (Exception ex) {
			Log.e(TAG, "Error starting url intent.", ex);
			Toast.makeText(context,
					"Sorry, we couldn't find any app for viewing this url!",
					Toast.LENGTH_SHORT).show();
		}
	}

}
