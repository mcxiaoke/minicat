package com.fanfou.app;

import com.fanfou.app.util.IntentHelper;
import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;

/**
 * @author mcxiaoke
 * @version 1.0 2011.10.25
 * @version 1.1 2011.11.02
 * @version 1.2 2011.11.03
 * @version 1.3 2011.11.11
 * @version 1.4 2011.12.05
 * 
 */
public class SplashPage extends Activity {
	private Handler mHandler = new Handler();

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		checkLogin();
		// boolean showSplash = OptionHelper.readBoolean(this,
		// R.string.option_show_splash_screen, true);
		// if (showSplash) {
		// setContentView(R.layout.splash);
		// mHandler.postDelayed(new Runnable() {
		//
		// @Override
		// public void run() {
		// checkLogin();
		// }
		// }, 2000);
		// } else {
		// checkLogin();
		// }

	}

	private void checkLogin() {
		// 可以将大部分的初始化工作从App中转移到这里
		if (App.verified) {
			IntentHelper.goHomePage(this, 0);
		} else {
			IntentHelper.goLoginPage(this);
		}
		finish();
	}

}
