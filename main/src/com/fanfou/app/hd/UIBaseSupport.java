package com.fanfou.app.hd;

import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;

import com.actionbarsherlock.app.ActionBar;
import com.actionbarsherlock.app.SherlockFragmentActivity;
import com.fanfou.app.hd.controller.UIController;
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
 * @version 3.4 2012.03.09
 * @version 3.5 2012.03.16
 * 
 */
abstract class UIBaseSupport extends SherlockFragmentActivity implements
		OnClickListener {

	public static final int STATE_INIT = 0;
	public static final int STATE_NORMAL = 1;
	public static final int STATE_EMPTY = 2;

	protected UIBaseSupport mContext;
	protected LayoutInflater mInflater;

	protected DisplayMetrics mDisplayMetrics;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		setTheme(App.themeId);
		super.onCreate(savedInstanceState);
		init();
		initialize();
		setLayout();
		setActionBar();
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
		App.setActiveContext(getClass().getCanonicalName(), this);
		this.mContext = this;
		this.mInflater = LayoutInflater.from(this);
		this.mDisplayMetrics = new DisplayMetrics();
		getWindowManager().getDefaultDisplay().getMetrics(mDisplayMetrics);
		Utils.initScreenConfig(this);

	}

	protected abstract void initialize();

	protected abstract void setLayout();

	protected void setActionBar() {
		ActionBar ab = getSupportActionBar();
		ab.setDisplayHomeAsUpEnabled(true);
	}

	protected int getMenuResourceId() {
		return R.menu.base_menu;
	}

	@Override
	public boolean onCreateOptionsMenu(com.actionbarsherlock.view.Menu menu) {
		int id = getMenuResourceId();
		if (id > 0) {
			getSupportMenuInflater().inflate(id, menu);
		}
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(
			com.actionbarsherlock.view.MenuItem item) {
		switch (item.getItemId()) {
		case android.R.id.home:
			onHomeLogoClick();
			return true;
			// break;
		case R.id.menu_write:
			onMenuWriteClick();
			return true;
			// break;
		case R.id.menu_home:
			onMenuHomeClick();
			return true;
			// break;
		default:
			return super.onOptionsItemSelected(item);
			// break;
		}
	}

	protected void onHomeLogoClick() {
		finish();
	}

	protected void onMenuWriteClick() {
		UIController.showWrite(mContext);
	}

	protected void onMenuHomeClick() {
		UIController.showHome(mContext);
	}

	@Override
	protected void onResume() {
		super.onResume();
		App.active = true;
	}

	@Override
	protected void onPause() {
		App.active = false;
		super.onPause();
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

	protected int getPixelInt(int dpi) {
		return (int) (dpi * mDisplayMetrics.density);
	}

	protected int getPixelInt(float dpi) {
		return (int) (dpi * mDisplayMetrics.density);
	}

	protected float getPixel(int dpi) {
		return (dpi * mDisplayMetrics.density);
	}

	protected float getPixel(float dpi) {
		return (dpi * mDisplayMetrics.density);
	}

	@Override
	public void onClick(View v) {
	}

}
