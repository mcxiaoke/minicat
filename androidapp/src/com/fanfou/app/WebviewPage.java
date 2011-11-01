package com.fanfou.app;

import com.fanfou.app.util.Utils;

import android.os.Bundle;

public class WebviewPage extends BaseActivity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Utils.initScreenConfig(this);
	}

}
