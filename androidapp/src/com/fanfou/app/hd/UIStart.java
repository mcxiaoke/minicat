package com.fanfou.app.hd;

import android.app.Activity;
import android.os.Bundle;

import com.fanfou.app.hd.util.IntentHelper;

/**
 * @author mcxiaoke
 * @version 1.0 2011.10.25
 * @version 1.1 2011.11.02
 * @version 1.2 2011.11.03
 * @version 1.3 2011.11.11
 * @version 1.4 2011.12.05
 * 
 */
public class UIStart extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		checkLogin();

	}

	private void checkLogin() {
		if (App.verified) {
			IntentHelper.goHomePage(this, 0);
		} else {
			IntentHelper.goLoginPage(this);
		}
		finish();
	}

}
