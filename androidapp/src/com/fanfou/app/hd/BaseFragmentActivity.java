package com.fanfou.app.hd;

import android.app.Activity;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import com.fanfou.app.App;
import com.fanfou.app.OptionsPage;
import com.fanfou.app.R;
import com.fanfou.app.SearchPage;
import com.fanfou.app.WritePage;
import com.fanfou.app.config.Commons;
import com.fanfou.app.dialog.ConfirmDialog;
import com.fanfou.app.App.ApnType;
import com.fanfou.app.ui.ActionManager;
import com.fanfou.app.util.IntentHelper;
import com.fanfou.app.util.Utils;

/**
 * @author mcxiaoke
 * @version 1.0 2011.09.24
 * @version 1.1 2011.11.22
 * 
 */
public abstract class BaseFragmentActivity extends FragmentActivity {

	public static final int STATE_INIT = 0;
	public static final int STATE_NORMAL = 1;
	public static final int STATE_EMPTY = 2;

	Activity mContext;
	LayoutInflater mInflater;
	boolean isActive = false;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.mContext = this;
		this.mInflater = LayoutInflater.from(this);
	}

	@Override
	protected void onResume() {
		super.onResume();
		isActive = true;
		App.active = true;
	}

	@Override
	protected void onPause() {
		isActive = false;
		App.active = false;
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
}
