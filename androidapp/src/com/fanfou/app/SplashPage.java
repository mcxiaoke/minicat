package com.fanfou.app;

import com.fanfou.app.util.IntentHelper;
import android.app.Activity;
import android.os.Bundle;

/**
 * @author mcxiaoke
 * @version 1.0 2011.10.25
 *
 */
public class SplashPage extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		// TODO
		// 可以将大部分的初始化工作从App中转移到这里
		if (App.me.isLogin) {
			IntentHelper.goHomePage(this, 0);
		} else {
			IntentHelper.goLoginPage(this);
		}
		finish();
	}

}
