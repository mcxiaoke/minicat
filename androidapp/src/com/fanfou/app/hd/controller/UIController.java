/**
 * 
 */
package com.fanfou.app.hd.controller;

import android.content.Context;
import android.content.Intent;
import com.fanfou.app.hd.UIAbout;
import com.fanfou.app.hd.UIConversation;
import com.fanfou.app.hd.UIHome;
import com.fanfou.app.hd.UILogin;
import com.fanfou.app.hd.UIWrite;
import com.fanfou.app.hd.dao.model.DirectMessageModel;

/**
 * @author mcxiaoke
 * @version 1.0 2012-2-24 下午5:08:25
 * 
 */
public class UIController {

	private static void startUI(Context ctx, Class<?> cls) {
		ctx.startActivity(new Intent(ctx, cls));
	}

	public static void goUIAbout(Context context) {
		startUI(context, UIAbout.class);
	}

	public static void goUILogin(Context context) {
		Intent intent = new Intent(context, UILogin.class);
		intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK
				| Intent.FLAG_ACTIVITY_NEW_TASK);
		context.startActivity(intent);
	}

	public static void goUIHome(Context context) {
		Intent intent = new Intent(context, UIHome.class);
		intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
		context.startActivity(intent);
	}

	public static void goUIConversation(Context context, DirectMessageModel dm) {
		Intent intent = new Intent(context, UIConversation.class);
		if (dm.isIncoming()) {
			intent.putExtra("id", dm.getSenderId());
			intent.putExtra("screen_name", dm.getSenderScreenName());
			intent.putExtra("profile_image_url", dm.getSenderProfileImageUrl());
		} else {
			intent.putExtra("id", dm.getRecipientId());
			intent.putExtra("screen_name", dm.getRecipientScreenName());
			intent.putExtra("profile_image_url",
					dm.getRecipientProfileImageUrl());
		}
		context.startActivity(intent);
	}
	
	public static void goUIWrite(Context context){
		Intent intent = new Intent(context, UIWrite.class);
		context.startActivity(intent);
	}

}
