package com.fanfou.app.hd;

import android.os.Bundle;

import com.fanfou.app.hd.util.Utils;

public class WebviewPage extends BaseActivity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Utils.initScreenConfig(this);
	}

}
