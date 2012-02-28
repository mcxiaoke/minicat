package com.fanfou.app.hd;

import com.fanfou.app.hd.controller.UIController;

import android.app.Activity;
import android.os.Bundle;

/**
 * @author mcxiaoke
 * @version 1.0 2011.10.25
 * @version 1.1 2011.11.02
 * @version 1.2 2011.11.03
 * @version 1.3 2011.11.11
 * @version 1.4 2011.12.05
 * @version 1.5 2012.02.27
 * 
 */
public class UIStart extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		checkLogin();

	}

	private void checkLogin() {
		if (App.isVerified()) {
			UIController.goUIHome(this);
		} else {
			UIController.goUILogin(this);
		}
		finish();
	}

}
