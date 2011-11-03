package com.fanfou.app;

import com.fanfou.app.hd.HomeScreen;
import com.fanfou.app.util.IntentHelper;
import com.fanfou.app.util.OptionHelper;
import com.fanfou.app.util.Utils;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.preference.PreferenceManager;

/**
 * @author mcxiaoke
 * @version 1.0 2011.10.25
 * @version 1.1 2011.11.02
 * @version 1.2 2011.11.03
 * 
 */
public class SplashPage extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		PreferenceManager.setDefaultValues(this, R.xml.options, false);
		// TODO
		// 可以将大部分的初始化工作从App中转移到这里
		if (App.me.isLogin) {
			if (App.DEBUG) {
				if (OptionHelper.readBoolean(this, R.string.option_debug_on,
						false)) {
					startActivity(new Intent(this, HomeScreen.class));
				} else {
					IntentHelper.goHomePage(this, 0);
				}
			}else{
				IntentHelper.goHomePage(this, 0);
			}
		} else {
			IntentHelper.goLoginPage(this);
		}
		finish();
	}

}
