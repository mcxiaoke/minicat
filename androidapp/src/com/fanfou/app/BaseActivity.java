package com.fanfou.app;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;

import com.fanfou.app.ui.ActionBar.OnRefreshClickListener;
import com.fanfou.app.util.IntentHelper;
import com.fanfou.app.util.Utils;

/**
 * @author mcxiaoke
 * @version 1.0 2011.05.30
 * @version 2.0 2011.09.25
 * @version 2.1 2011.10.19
 * @version 2.1 2011.10.25
 * @version 2.2 2011.10.27
 * @version 2.3 2011.11.07
 * @version 2.4 2011.11.11
 * @version 2.5 2011.11.15
 * @version 2.6 2011.11.22
 * @version 2.7 2011.12.07
 * 
 */
public abstract class BaseActivity extends Activity implements OnClickListener {

	public static final int STATE_INIT = 0;
	public static final int STATE_NORMAL = 1;
	public static final int STATE_EMPTY = 2;

	protected BaseActivity mContext;
	protected LayoutInflater mInflater;
	protected boolean isActive = false;

	protected DisplayMetrics mDisplayMetrics;

	private BroadcastReceiver mBroadcastReceiver;
	private IntentFilter mIntentFilter;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Utils.initScreenConfig(this);

		this.mContext = this;
		this.mInflater = LayoutInflater.from(this);

		initialize();
		initReceiver();
	}

	private void initialize() {
		mDisplayMetrics = new DisplayMetrics();
		getWindowManager().getDefaultDisplay().getMetrics(mDisplayMetrics);
	}

	private void initReceiver() {
		this.mBroadcastReceiver = new MyBroadcastReceiver();
		this.mIntentFilter = getIntentFilter();
		mIntentFilter.setPriority(1000);
	}

	private class MyBroadcastReceiver extends BroadcastReceiver {

		@Override
		public void onReceive(Context context, Intent intent) {
			if (App.DEBUG) {
				Log.d("NotificationReceiver", "active, broadcast received: "
						+ intent.toString());
			}
			if (onBroadcastReceived(intent)) {
				abortBroadcast();
			}
		}

	}

	protected IntentFilter getIntentFilter() {
		return new IntentFilter();
	}

	protected boolean onBroadcastReceived(Intent intent) {
		return true;
	};

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
	}

	// public void doLogout(){
	// if(isTaskRoot()){
	// IntentHelper.goLoginPage(this);
	// }else{
	// setResult(RESULT_LOGOUT);
	// }
	// finish();
	// }

	@Override
	protected void onResume() {
		super.onResume();
		App.active = isActive = true;
		registerReceiver(mBroadcastReceiver, mIntentFilter);
	}

	@Override
	protected void onPause() {
		App.active = isActive = false;
		unregisterReceiver(mBroadcastReceiver);
		super.onPause();
	}

	@Override
	protected void onStart() {
		super.onStart();
	}

	@Override
	protected void onStop() {
		super.onStop();
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
	}

	protected boolean isActive() {
		return isActive;
	}

	protected static final int PAGE_NORMAL = 0;
	protected static final int PAGE_HOME = 1;
	protected static final int PAGE_LOGIN = 2;
	protected static final int PAGE_STATUS = 3;
	protected static final int PAGE_USER = 4;
	protected static final int PAGE_TIMELINE = 5;
	protected static final int PAGE_FRIENDS = 6;
	protected static final int PAGE_FOLLOWERS = 7;
	protected static final int PAGE_DRAFTS = 8;

	protected int getPageType() {
		return PAGE_NORMAL;
	}

	protected boolean isHomeScreen() {
		return false;
	}

	protected static final int MENU_ID_HOME = 0;
	protected static final int MENU_ID_SAVE = 1;
	protected static final int MENU_ID_CLEAR=2;

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		super.onCreateOptionsMenu(menu);
		MenuItem home = menu.add(0, MENU_ID_HOME, MENU_ID_HOME, "返回首页");
		home.setIcon(R.drawable.ic_menu_home);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		if(item.getItemId()==MENU_ID_HOME){
			onMenuHomeClick();
			return true;
		}
		return super.onOptionsItemSelected(item);
	}

	@Override
	public void onClick(View v) {
	}

	protected void onMenuHomeClick() {
		IntentHelper.goHomePage(this, -1);
		finish();
	}

}
