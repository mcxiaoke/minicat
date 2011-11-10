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

	protected boolean noConnection() {
		return App.me.apnType == ApnType.NONE;
	}

	protected static final int MENU_ID_PROFILE = 0; //
	protected static final int MENU_ID_OPTION = 1; // 设置
	protected static final int MENU_ID_SEARCH = 2;
	protected static final int MENU_ID_ABOUT = 3; // 关于
	protected static final int MENU_ID_FEEDBACK = 4; //
	protected static final int MENU_ID_LOGOUT = 5; // 退出
	protected static final int MENU_ID_HOME = 6; // 返回首页

	protected static final int MENU_ID_REFRESH = 7; // 返回首页
	protected static final int MENU_ID_CLEAR = 8; // 返回首页

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
			// startActivity(new Intent(this, NewVersionPage.class));
			onAboutClick();
			break;
		case MENU_ID_FEEDBACK:
			onFeedbackClick();
			break;
		case MENU_ID_HOME:
			onHomeClick();
			break;
		case MENU_ID_CLEAR:
			onClearClick();
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
			menu.removeItem(MENU_ID_CLEAR);
			break;
		case PAGE_HOME:
			menu.removeItem(MENU_ID_HOME);
			menu.removeItem(MENU_ID_CLEAR);
			break;
		case PAGE_LOGIN:
			menu.clear();
			break;
		case PAGE_DRAFTS:
			menu.removeItem(MENU_ID_OPTION);
			menu.removeItem(MENU_ID_PROFILE);
			menu.removeItem(MENU_ID_SEARCH);
			menu.removeItem(MENU_ID_LOGOUT);
			menu.removeItem(MENU_ID_ABOUT);
			menu.removeItem(MENU_ID_FEEDBACK);
			menu.removeItem(MENU_ID_HOME);
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
	MenuItem clear;

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {

		option = menu.add(0, MENU_ID_OPTION, MENU_ID_OPTION, "功能设置");
		option.setIcon(R.drawable.i_menu_option);

		profile = menu.add(0, MENU_ID_PROFILE, MENU_ID_PROFILE, "我的空间");
		profile.setIcon(R.drawable.i_menu_profile);

		search = menu.add(0, MENU_ID_SEARCH, MENU_ID_SEARCH, "热词搜索");
		search.setIcon(R.drawable.i_menu_search);

		logout = menu.add(0, MENU_ID_LOGOUT, MENU_ID_LOGOUT, "注销登录");
		logout.setIcon(R.drawable.i_menu_logout);

		about = menu.add(0, MENU_ID_ABOUT, MENU_ID_ABOUT, "关于饭否");
		about.setIcon(R.drawable.i_menu_about);

		feedback = menu.add(0, MENU_ID_FEEDBACK, MENU_ID_FEEDBACK, "意见反馈");
		feedback.setIcon(R.drawable.i_menu_feedback);

		home = menu.add(0, MENU_ID_HOME, MENU_ID_HOME, "返回首页");
		home.setIcon(R.drawable.i_menu_home);

		clear = menu.add(0, MENU_ID_CLEAR, MENU_ID_CLEAR, "清空草稿");
		clear.setIcon(R.drawable.i_menu_clear);
		return true;
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
		IntentHelper.goHomePage(this, -1);
		finish();
	}

	protected void onClearClick() {
	};

	protected void onLogoutClick() {
		final ConfirmDialog dialog = new ConfirmDialog(this, "注销",
				"确定注销当前登录帐号吗？");
		dialog.setClickListener(new ConfirmDialog.AbstractClickHandler() {

			@Override
			public void onButton1Click() {
				IntentHelper.goLoginPage(mContext);
				finish();
			}
		});
		dialog.show();
	}
}
