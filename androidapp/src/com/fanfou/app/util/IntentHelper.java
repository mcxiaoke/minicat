package com.fanfou.app.util;

import com.fanfou.app.config.Commons;

import android.content.Context;
import android.content.Intent;

public final class IntentHelper {
	
	public static void sendFeedback(Context context, String content){
        final Intent emailIntent = new Intent(android.content.Intent.ACTION_SEND);
        emailIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//        emailIntent.setType("text/plain");
        emailIntent.setType("message/rfc822");
        String subject = "饭否客户端意见反馈";
        emailIntent.putExtra(android.content.Intent.EXTRA_SUBJECT, subject);
        emailIntent.putExtra(android.content.Intent.EXTRA_TEXT, content);
        emailIntent.putExtra(android.content.Intent.EXTRA_EMAIL, new String[] { Commons.FEEDBACK_EMAIL });
        context.startActivity(Intent.createChooser(emailIntent, "发送邮件"));
	}

}
