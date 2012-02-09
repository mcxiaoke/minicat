package com.fanfou.app.hd;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import com.fanfou.app.hd.ui.widget.ActionBar;
import com.fanfou.app.hd.ui.widget.GestureManager.SwipeGestureListener;
import com.fanfou.app.hd.ui.widget.GestureManager.SwipeListener;
import com.fanfou.app.hd.util.IntentHelper;
import com.fanfou.app.hd.util.Utils;

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
 * @version 2.8 2012.02.01
 * @version 3.0 2012.02.06
 * @version 3.1 2012.02.07
 * 
 */
abstract class UIBase extends FragmentActivity implements OnClickListener,SwipeListener {

	public static final int STATE_INIT = 0;
	public static final int STATE_NORMAL = 1;
	public static final int STATE_EMPTY = 2;

	protected UIBase mContext;
	protected LayoutInflater mInflater;
	protected boolean isActive = false;

	protected DisplayMetrics mDisplayMetrics;

	private BroadcastReceiver mBroadcastReceiver;
	private IntentFilter mIntentFilter;
	
//	private final UICompatHelper mUiCompatHelper=UICompatHelper.newInstance(this);

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
//		this.mUiCompatHelper.onCreate(savedInstanceState);
		Utils.initScreenConfig(this);

		this.mContext = this;
		this.mInflater = LayoutInflater.from(this);
		this.mDisplayMetrics = new DisplayMetrics();
		getWindowManager().getDefaultDisplay().getMetrics(mDisplayMetrics);
		initReceiver();
		initialize();
		setLayout();
	}
	
	@Override
	protected void onPostCreate(Bundle savedInstanceState) {
		super.onPostCreate(savedInstanceState);
//		this.mUiCompatHelper.onPostCreate(savedInstanceState);
	}

//	@Override
//	public MenuInflater getMenuInflater() {
//		return this.mUiCompatHelper.getMenuInflater(super.getMenuInflater());
//	}

	@Override
	protected void onTitleChanged(CharSequence title, int color) {
//		this.mUiCompatHelper.onTitleChanged(title, color);
		super.onTitleChanged(title, color);
	}

//	protected UICompatHelper getUICompatHelper(){
//		return mUiCompatHelper;
//	}

	protected abstract void initialize();
	
	protected abstract void setLayout();

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

	@Override
	public boolean onSwipeLeft() {
		finish();
		return false;
	}

	@Override
	public boolean onSwipeRight() {
		return false;
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
//        boolean retValue = false;
//        retValue |= this.mUiCompatHelper.onCreateOptionsMenu ( menu );
//        retValue |= super.onCreateOptionsMenu ( menu );
//        return retValue;
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
	
	protected void setActionBarSwipe(final ActionBar actionBar){
		final GestureDetector detector=new GestureDetector(new SwipeGestureListener(this));
		actionBar.setOnTouchListener(new View.OnTouchListener() {
			
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				detector.onTouchEvent(event);
				return true;
			}
		});
	}

}
