package com.fanfou.app.preferences;

import android.content.Context;
import android.content.DialogInterface;
import android.util.AttributeSet;

import com.fanfou.app.App;
import com.fanfou.app.util.IntentHelper;

/**
 * @author mcxiaoke
 * @version 1.0 2011.09.10
 * @version 1.1 2011.10.25
 *
 */
public class LogoutDialogPreference extends DialogPreference {

	public LogoutDialogPreference(Context context, AttributeSet attrs) {
		super(context, attrs);
		init();
	}
	
	private void init(){
		setSummary("当前登录帐号:"+App.me.userId);
	}

	@Override
	public void onClick(DialogInterface dialog, int which) {
		super.onClick(dialog, which);
		if (which == DialogInterface.BUTTON_POSITIVE) {
			IntentHelper.goLoginPage(context);
		}
	}
}
