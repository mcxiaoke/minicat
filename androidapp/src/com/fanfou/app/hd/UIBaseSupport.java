package com.fanfou.app.hd;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;

import com.fanfou.app.hd.controller.UIController;
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
 * @version 3.2 2012.02.09
 * @version 3.3 2012.02.10
 * 
 */
abstract class UIBaseSupport extends UIActionBarSupport implements
		OnClickListener {

	public static final int STATE_INIT = 0;
	public static final int STATE_NORMAL = 1;
	public static final int STATE_EMPTY = 2;

	protected UIBaseSupport mContext;
	protected LayoutInflater mInflater;
	protected boolean isActive = false;

	protected DisplayMetrics mDisplayMetrics;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		init();
		initialize();
		setLayout();
	}

	@Override
	protected void onPostCreate(Bundle savedInstanceState) {
		super.onPostCreate(savedInstanceState);
	}

	@Override
	protected void onTitleChanged(CharSequence title, int color) {
		super.onTitleChanged(title, color);
	}

	private void init() {
		this.mContext = this;
		this.mInflater = LayoutInflater.from(this);
		this.mDisplayMetrics = new DisplayMetrics();
		getWindowManager().getDefaultDisplay().getMetrics(mDisplayMetrics);
		Utils.initScreenConfig(this);
	}

	protected abstract void initialize();

	protected abstract void setLayout();

	protected static class MyBroadcastReceiver extends BroadcastReceiver {
		private UIBaseSupport mUIBase;

		public MyBroadcastReceiver(UIBaseSupport base) {
			this.mUIBase = base;
		}

		@Override
		public void onReceive(Context context, Intent intent) {
			if (App.DEBUG) {
				Log.d("NotificationReceiver", "active, broadcast received: "
						+ intent.toString());
			}
			// if (mUIBase.onBroadcastReceived(intent)) {
			// abortBroadcast();
			// }
		}

	}
	
	protected int getMenuResourceId(){
		return R.menu.base_menu;
	}

	@Override
	public final boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(getMenuResourceId(), menu);
		return super.onCreateOptionsMenu(menu);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		int id = item.getItemId();
		switch (id) {
		case R.id.menu_home:
			onMenuHomeClick();
			break;
		case R.id.menu_write:
			onMenuWriteClick();
			break;
		default:
			break;
		}
		return super.onOptionsItemSelected(item);
	}

	protected void onMenuHomeClick() {
		UIController.goUIHome(mContext);
		finish();
	}

	protected void onMenuWriteClick() {
//		ActionManager.doWrite(this);
	}

	@Override
	protected void onResume() {
		super.onResume();
//		App.active = isActive = true;
	}

	@Override
	protected void onPause() {
//		App.active = isActive = false;
		super.onPause();
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

	@Override
	public void onClick(View v) {
	}

}
