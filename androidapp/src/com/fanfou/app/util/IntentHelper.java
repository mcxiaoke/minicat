package com.fanfou.app.util;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;

import com.fanfou.app.App;
import com.fanfou.app.R;

public final class IntentHelper {

	private static Intent getHomeIntent() {
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
		context.startActivity(Intent.createChooser(emailIntent, "给饭否发送反馈"));
	}

	public static void logIntent(String tag, Intent intent) {
		if (intent == null) {
			Log.i(tag, "intent is null.");
		}
		StringBuffer sb = new StringBuffer();
		sb.append(" intent.getAction():" + intent.getAction());
		sb.append(" intent.getData():" + intent.getData());
		sb.append(" intent.getDataString():" + intent.getDataString());
		sb.append(" intent.getScheme():" + intent.getScheme());
		sb.append(" intent.getType():" + intent.getType());
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
