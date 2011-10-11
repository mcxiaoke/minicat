package com.fanfou.app.preferences;

import android.content.Context;
import android.content.DialogInterface;
import android.util.AttributeSet;

import com.fanfou.app.LoginPage;

public class LogoutDialogPreference extends DialogPreference {

	public LogoutDialogPreference(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	public LogoutDialogPreference(Context context, AttributeSet attrs,
			int defStyle) {
		super(context, attrs, defStyle);
	}

	@Override
	public void onClick(DialogInterface dialog, int which) {
		super.onClick(dialog, which);
		if (which == DialogInterface.BUTTON_POSITIVE) {
			LoginPage.doLogin(context);
		}
	}
}
