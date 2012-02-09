package com.fanfou.app.hd.preferences;

import android.app.Activity;
import android.content.DialogInterface;
import android.util.AttributeSet;
import com.fanfou.app.hd.App;
import com.fanfou.app.hd.util.IntentHelper;

/**
 * @author mcxiaoke
 * @version 1.0 2011.09.10
 * @version 1.1 2011.10.25
 * 
 */
public class LogoutDialogPreference extends DialogPreference {

	public LogoutDialogPreference(Activity context, AttributeSet attrs) {
		super(context, attrs);
		setSummary("当前登录帐号:" + App.getUserName() + "(" + App.getUserId() + ")");
	}

	@Override
	public void onClick(DialogInterface dialog, int which) {
		super.onClick(dialog, which);
		if (which == DialogInterface.BUTTON_POSITIVE) {
			doLogout();
		}
	}

	private void doLogout() {
		IntentHelper.goLoginPage(context);
	}
}
