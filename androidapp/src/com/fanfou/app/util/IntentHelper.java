package com.fanfou.app.util;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import com.fanfou.app.App;
import com.fanfou.app.HomePage;
import com.fanfou.app.LoginPage;
import com.fanfou.app.R;
import com.fanfou.app.config.Commons;

public final class IntentHelper {

	public static void goHomePage(Context context, final int page) {
		Intent intent = new Intent(context, HomePage.class);
		intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
		intent.putExtra(Commons.EXTRA_PAGE, page);
		context.startActivity(intent);
	}

	public static void goLoginPage(Context context) {
		App.me.removeAccountInfo();
		App.me.clearImageTasks();
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
		// emailIntent.setType("text/plain");
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
			Log.d(tag, "intent is null.");
		}
		StringBuffer sb = new StringBuffer();
		sb.append("Action:" + intent.getAction());
		sb.append("Data:" + intent.getData());
		sb.append("DataStr:" + intent.getDataString());
		sb.append("Scheme:" + intent.getScheme());
		sb.append("Type:" + intent.getType());
		Bundle extras = intent.getExtras();
		if (extras != null && !extras.isEmpty()) {
			for (String key : extras.keySet()) {
				Object value = extras.get(key);
				sb.append(" EXTRA: {" + key + "::" + value + "}");
			}
		} else {
			sb.append(" NO EXTRAS");
		}
		Log.i(tag, sb.toString());
	}

}
