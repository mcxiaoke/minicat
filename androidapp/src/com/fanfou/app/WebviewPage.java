package com.fanfou.app;

import android.os.Bundle;

import com.fanfou.app.util.Utils;

public class WebviewPage extends BaseActivity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Utils.initScreenConfig(this);
	}

}
