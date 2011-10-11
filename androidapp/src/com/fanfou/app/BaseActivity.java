package com.fanfou.app;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.ActivityInfo;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;

import com.fanfou.app.config.Actions;
import com.fanfou.app.config.Commons;
import com.fanfou.app.http.ApnType;
import com.fanfou.app.service.NotificationService;
import com.fanfou.app.ui.ActionBar.OnRefreshClickListener;
import com.fanfou.app.ui.ActionManager;
import com.fanfou.app.util.OptionHelper;
import com.fanfou.app.util.Utils;

/**
 * @author mcxiaoke
 * @version 1.0 2011.05.30
 * @version 2.0 2011.09.25
 * 
 */
public abstract class BaseActivity extends Activity implements
		OnRefreshClickListener, OnClickListener {

	public static final int STATE_INIT = 0;
	public static final int STATE_NORMAL = 1;
	public static final int STATE_EMPTY = 2;

	protected static final int REQUEST_CODE_OPTION = 0;

	Activity mContext;
	LayoutInflater mInflater;
	boolean isActive = false;

	// NetworkReceiver mNetworkReceiver;
	// BroadcastReceiver mNotificationReceiver;
	// IntentFilter mNotificationFilter;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Utils.setScreenOrientation(this);
		this.mContext = this;
		this.mInflater = LayoutInflater.from(this);

		// initReceiver();
	}

	private void initReceiver() {
		// this.mNetworkReceiver=new NetworkReceiver();
		// IntentFilter filter = new IntentFilter();
		// filter.addAction(ConnectivityManager.CONNECTIVITY_ACTION);
		// filter.addAction(WifiManager.WIFI_STATE_CHANGED_ACTION);
		// this.mNotificationReceiver = new NotifyReceiver();
		// this.mNotificationFilter = new
		// IntentFilter(Actions.ACTION_NOTIFICATION);
		// mNotificationFilter.setPriority(1000);
	}

	private class NotifyReceiver extends BroadcastReceiver {

		@Override
		public void onReceive(Context context, Intent intent) {
			if (App.DEBUG) {
				Log.d("NotificationReceiver", "active, broadcast received: "
						+ intent.toString());
			}
			if (onReceived(intent)) {
				abortBroadcast();
			}
		}

	}

	protected boolean onReceived(Intent intent) {
		return false;
	};

	@Override
	protected void onResume() {
		App.active = isActive=true;
		super.onResume();

		// registerReceiver(mNetworkReceiver, filter);
		// registerReceiver(mNotificationReceiver, mNotificationFilter);
	}

	//
	@Override
	protected void onPause() {
		App.active = isActive=false;
		super.onPause();
		// unregisterReceiver(mNetworkReceiver);
		// unregisterReceiver(mNotificationReceiver);
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
	protected static final int MENU_ID_EXIT = 5; // 退出
	protected static final int MENU_ID_HOME = 6; // 返回首页

	@Override
	public boolean onMenuItemSelected(int featureId, MenuItem item) {
		int id = item.getItemId();
		switch (id) {
		case MENU_ID_OPTION:
			goOptionPage();
			break;
		case MENU_ID_PROFILE:
			goProfilePage();
			break;
		case MENU_ID_SEARCH:
			goSearchPage();
			break;
		case MENU_ID_EXIT:
			doExit();
			break;
		case MENU_ID_ABOUT:
			goAboutPage();
			break;
		case MENU_ID_FEEDBACK:
			doFeedback();
			break;
		case MENU_ID_HOME:
			goBackHome();
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
			menu.removeItem(MENU_ID_EXIT);
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
			menu.removeItem(MENU_ID_EXIT);
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

		profile = menu.add(0, MENU_ID_PROFILE, MENU_ID_PROFILE, "个人");
		profile.setIcon(R.drawable.i_menu_profile);

		search = menu.add(0, MENU_ID_SEARCH, MENU_ID_SEARCH, "搜索");
		search.setIcon(R.drawable.i_menu_search);

		logout = menu.add(0, MENU_ID_EXIT, MENU_ID_EXIT, "退出");
		logout.setIcon(R.drawable.i_menu_logout);

		about = menu.add(0, MENU_ID_ABOUT, MENU_ID_ABOUT, "关于");
		about.setIcon(R.drawable.i_menu_about);

		feedback = menu.add(0, MENU_ID_FEEDBACK, MENU_ID_FEEDBACK, "反馈");
		feedback.setIcon(R.drawable.i_menu_feedback);

		home = menu.add(0, MENU_ID_HOME, MENU_ID_HOME, "首页");
		home.setIcon(R.drawable.i_menu_home);

		return true;
	}

	@Override
	public void onRefreshClick() {
	}

	@Override
	public void onClick(View v) {
	}

	protected void goBackHome() {
		Intent intent = new Intent(mContext, HomePage.class);
		intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
		startActivity(intent);
	}

	protected void goOptionPage() {
		Intent intent = new Intent(this, OptionsPage.class);
		startActivity(intent);
		// startActivityForResult(intent, REQUEST_CODE_OPTION);
	}

	protected void goProfilePage() {
		ActionManager.doMyProfile(this);
	}

	protected void goSearchPage() {
		Intent intent = new Intent(this, SearchPage.class);
		startActivity(intent);
	}

	protected void goAboutPage() {
		Utils.goAboutPage(this);
	}

	protected void doFeedback() {
		// IntentHelper.sendFeedback(this, "");

		Intent intent = new Intent(this, WritePage.class);
		intent.putExtra(Commons.EXTRA_TYPE, WritePage.TYPE_NORMAL);
		intent.putExtra(Commons.EXTRA_TEXT,
				getString(R.string.config_feedback_account) + " ("
						+ Build.MODEL + "-" + Build.VERSION.RELEASE + ") ");
		startActivity(intent);
	}

	private void doExit() {
		finish();
		// Process.killProcess(Process.myPid());
	}

	protected void startRefreshAnimation() {
	}

	protected void stopRefreshAnimation() {
	}

}
