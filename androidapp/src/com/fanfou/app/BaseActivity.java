package com.fanfou.app;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.SharedPreferences.OnSharedPreferenceChangeListener;
import android.content.pm.ActivityInfo;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;

import com.fanfou.app.config.Actions;
import com.fanfou.app.config.Commons;
import com.fanfou.app.dialog.ConfirmDialog;
import com.fanfou.app.http.ApnType;
import com.fanfou.app.service.NotificationService;
import com.fanfou.app.ui.ActionBar.OnRefreshClickListener;
import com.fanfou.app.ui.ActionManager;
import com.fanfou.app.util.IntentHelper;
import com.fanfou.app.util.OptionHelper;
import com.fanfou.app.util.Utils;

import com.fanfou.app.R;

/**
 * @author mcxiaoke
 * @version 1.0 2011.05.30
 * @version 2.0 2011.09.25
 * @version 2.1 2011.10.19
 * @version 2.1 2011.10.25
 * 
 */
public abstract class BaseActivity extends Activity implements
		OnRefreshClickListener, OnClickListener {

	public static final int STATE_INIT = 0;
	public static final int STATE_NORMAL = 1;
	public static final int STATE_EMPTY = 2;

	protected static final int REQUEST_CODE_OPTION = 0;

	protected BaseActivity mContext;
	protected LayoutInflater mInflater;
	protected boolean isActive = false;

	private BroadcastReceiver mBroadcastReceiver;
	private IntentFilter mIntentFilter;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Utils.initScreenConfig(this);

		this.mContext = this;
		this.mInflater = LayoutInflater.from(this);

		initReceiver();
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
	
//	public void doLogout(){
//		if(isTaskRoot()){
//			IntentHelper.goLoginPage(this);
//		}else{
//			setResult(RESULT_LOGOUT);
//		}
//		finish();
//	}

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

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK && Build.VERSION.SDK_INT < 7
				&& event.getRepeatCount() == 0) {
			onBackPressed();
		}
		return super.onKeyDown(keyCode, event);
	}

	@Override
	public void onBackPressed() {
		super.onBackPressed();
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

	protected int getPageType() {
		return PAGE_NORMAL;
	}

	protected boolean isRootScreen() {
		return false;
	}

	protected boolean noConnection() {
		return App.me.apnType != ApnType.NONE;
		// if (App.me.connected) {
		// return false;
		// } else {
		// Utils.notify(this, "无可用的网络连接，请稍后重试");
		// return true;
		// }
	}

	protected static final int MENU_ID_PROFILE = 0; //
	protected static final int MENU_ID_OPTION = 1; // 设置
	protected static final int MENU_ID_SEARCH = 2;
	protected static final int MENU_ID_ABOUT = 3; // 关于
	protected static final int MENU_ID_FEEDBACK = 4; //
	protected static final int MENU_ID_LOGOUT = 5; // 退出
	protected static final int MENU_ID_HOME = 6; // 返回首页

	@Override
	public boolean onMenuItemSelected(int featureId, MenuItem item) {
		int id = item.getItemId();
		switch (id) {
		case MENU_ID_OPTION:
			onOptionClick();
			break;
		case MENU_ID_PROFILE:
			onProfileClick();
			break;
		case MENU_ID_SEARCH:
			onSearchClick();
			break;
		case MENU_ID_LOGOUT:
			onLogoutClick();
			break;
		case MENU_ID_ABOUT:
			onAboutClick();
			break;
		case MENU_ID_FEEDBACK:
			onFeedbackClick();
			break;
		case MENU_ID_HOME:
			onHomeClick();
			break;
		default:
			break;
		}

		return super.onMenuItemSelected(featureId, item);
	}

	@Override
	public boolean onPrepareOptionsMenu(Menu menu) {
		int type = getPageType();
		switch (type) {
		case PAGE_NORMAL:
			menu.removeItem(MENU_ID_OPTION);
			menu.removeItem(MENU_ID_PROFILE);
			menu.removeItem(MENU_ID_SEARCH);
			menu.removeItem(MENU_ID_LOGOUT);
			menu.removeItem(MENU_ID_ABOUT);
			menu.removeItem(MENU_ID_FEEDBACK);
			break;
		case PAGE_HOME:
			menu.removeItem(MENU_ID_HOME);
			break;
		case PAGE_LOGIN:
			menu.removeItem(MENU_ID_OPTION);
			menu.removeItem(MENU_ID_PROFILE);
			menu.removeItem(MENU_ID_SEARCH);
			menu.removeItem(MENU_ID_LOGOUT);
			menu.removeItem(MENU_ID_ABOUT);
			menu.removeItem(MENU_ID_FEEDBACK);
			menu.removeItem(MENU_ID_HOME);
			break;
		default:
			break;
		}
		return true;
	}

	MenuItem option;
	MenuItem profile;
	MenuItem search;
	MenuItem logout;
	MenuItem about;
	MenuItem feedback;
	MenuItem home;

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {

		option = menu.add(0, MENU_ID_OPTION, MENU_ID_OPTION, "选项");
		option.setIcon(R.drawable.i_menu_option);

		profile = menu.add(0, MENU_ID_PROFILE, MENU_ID_PROFILE, "我的空间");
		profile.setIcon(R.drawable.i_menu_profile);

		search = menu.add(0, MENU_ID_SEARCH, MENU_ID_SEARCH, "热词和搜索");
		search.setIcon(R.drawable.i_menu_search);

		logout = menu.add(0, MENU_ID_LOGOUT, MENU_ID_LOGOUT, "注销");
		logout.setIcon(R.drawable.i_menu_logout);

		about = menu.add(0, MENU_ID_ABOUT, MENU_ID_ABOUT, "关于饭否");
		about.setIcon(R.drawable.i_menu_about);

		feedback = menu.add(0, MENU_ID_FEEDBACK, MENU_ID_FEEDBACK, "意见反馈");
		feedback.setIcon(R.drawable.i_menu_feedback);

		home = menu.add(0, MENU_ID_HOME, MENU_ID_HOME, "返回首页");
		home.setIcon(R.drawable.i_menu_home);

		return true;
	}

	@Override
	public void onRefreshClick() {
	}

	@Override
	public void onClick(View v) {
	}

	protected void onOptionClick() {
		Intent intent = new Intent(this, OptionsPage.class);
		startActivity(intent);
	}

	protected void onProfileClick() {
		ActionManager.doMyProfile(this);
	}

	protected void onSearchClick() {
		Intent intent = new Intent(this, SearchPage.class);
		startActivity(intent);
	}

	protected void onAboutClick() {
		Utils.goAboutPage(this);
	}

	protected void onFeedbackClick() {
		Intent intent = new Intent(this, WritePage.class);
		intent.putExtra(Commons.EXTRA_TYPE, WritePage.TYPE_NORMAL);
		intent.putExtra(Commons.EXTRA_TEXT,
				getString(R.string.config_feedback_account) + " ("
						+ Build.MODEL + "-" + Build.VERSION.RELEASE + ") ");
		startActivity(intent);
	}

	protected void onHomeClick() {
		IntentHelper.goHomePage(this, 0);
		finish();
	}

	protected void onLogoutClick() {
		final ConfirmDialog dialog=new ConfirmDialog(this, "提示", "确定注销当前登录帐号吗？");
		dialog.setOnClickListener(new ConfirmDialog.OnOKClickListener() {
			@Override
			public void onOKClick() {
				IntentHelper.goLoginPage(mContext);
				finish();
			}
		});
		dialog.show();
	}

	protected void startRefreshAnimation() {
	}

	protected void stopRefreshAnimation() {
	}

}
