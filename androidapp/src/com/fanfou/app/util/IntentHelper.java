package com.fanfou.app.util;

import com.fanfou.app.App;
import com.fanfou.app.config.Commons;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

public final class IntentHelper {

	public static void sendFeedback(Context context, String content) {
		final Intent emailIntent = new Intent(
				android.content.Intent.ACTION_SEND);
		emailIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		// emailIntent.setType("text/plain");
		emailIntent.setType("message/rfc822");
		String subject = "饭否客户端意见反馈";
		emailIntent.putExtra(android.content.Intent.EXTRA_SUBJECT, subject);
		emailIntent.putExtra(android.content.Intent.EXTRA_TEXT, content);
		emailIntent.putExtra(android.content.Intent.EXTRA_EMAIL,
				new String[] { Commons.FEEDBACK_EMAIL });
		context.startActivity(Intent.createChooser(emailIntent, "发送邮件"));
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
