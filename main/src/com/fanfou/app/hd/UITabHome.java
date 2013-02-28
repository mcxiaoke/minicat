/*
 * Copyright (C) 2011 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.fanfou.app.hd;

import java.util.HashMap;

import com.fanfou.app.hd.controller.SimpleDialogListener;
import com.fanfou.app.hd.controller.UIController;
import com.fanfou.app.hd.dialog.ConfirmDialog;
import com.fanfou.app.hd.fragments.ColumnsFragment;
import com.fanfou.app.hd.fragments.ConversationListFragment;
import com.fanfou.app.hd.fragments.HomeTimelineFragment;
import com.fanfou.app.hd.fragments.MentionTimelineFragment;
import com.fanfou.app.hd.fragments.PublicTimelineFragment;

import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TabHost;

/**
 * This demonstrates how you can implement switching between the tabs of a
 * TabHost through fragments. It uses a trick (see the code below) to allow the
 * tabs to switch between fragments instead of simple views.
 */
public class UITabHome extends UIBaseSupport {
	TabHost mTabHost;
	TabManager mTabManager;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.fm_tabs);
		mTabHost = (TabHost) findViewById(android.R.id.tabhost);
		mTabHost.setup();

		Bundle args = new Bundle();
		args.putBoolean("refresh", true);
		args.putString("id", "mcxiaoke");

		mTabManager = new TabManager(this, mTabHost, R.id.realtabcontent);

		mTabManager.addTab(
				mTabHost.newTabSpec("home").setIndicator(getIndicator(0)),
				HomeTimelineFragment.class, args);
		mTabManager.addTab(
				mTabHost.newTabSpec("mention").setIndicator(getIndicator(1)),
				MentionTimelineFragment.class, args);
		mTabManager.addTab(
				mTabHost.newTabSpec("dm").setIndicator(getIndicator(2)),
				ConversationListFragment.class, args);
		mTabManager.addTab(
				mTabHost.newTabSpec("public").setIndicator(getIndicator(3)),
				PublicTimelineFragment.class, args);
		mTabManager.addTab(
				mTabHost.newTabSpec("column").setIndicator(getIndicator(4)),
				ColumnsFragment.class, args);

		if (savedInstanceState != null) {
			mTabHost.setCurrentTabByTag(savedInstanceState.getString("tab"));
		}
	}

	@Override
	protected void setActionBar() {
	}

	@Override
	protected int getMenuResourceId() {
		return R.menu.home_menu;
	}

	@Override
	public boolean onOptionsItemSelected(
			com.actionbarsherlock.view.MenuItem item) {
		switch (item.getItemId()) {
		case R.id.menu_write:
			onMenuWriteClick();
			break;
		case R.id.menu_logout:
			onMenuLogoutClick();
			break;
		case R.id.menu_option:
			onMenuOptionClick();
			break;
		case R.id.menu_search:
			onMenuSearchClick();
			break;
		case R.id.menu_about:
			onMenuAboutClick();
			break;
		case R.id.menu_feedback:
			onMenuFeedbackClick();
			break;
		case R.id.menu_profile:
			onMenuProfileClick();
			break;
		default:
			super.onOptionsItemSelected(item);
			break;
		}
		return true;
	}

	private void onMenuOptionClick() {
		Intent intent = new Intent(this, UISetting.class);
		startActivity(intent);
	}

	private void onMenuProfileClick() {
		UIController.showProfile(this, App.getAccount());
	}

	private void onMenuSearchClick() {
		Intent intent = new Intent(this, UISearch.class);
		startActivity(intent);
	}

	private void onMenuAboutClick() {
		UIController.showAbout(this);
	}

	private void onMenuFeedbackClick() {
		String text = getString(R.string.config_feedback_account) + " ("
				+ Build.MODEL + "-" + Build.VERSION.RELEASE + " "
				+ App.versionName + ") ";
		UIController.showWrite(this, text);
	}

	private void onMenuLogoutClick() {
		final ConfirmDialog dialog = new ConfirmDialog(this);
		dialog.setTitle("提示");
		dialog.setMessage("确定注销当前登录帐号吗？");
		dialog.setClickListener(new SimpleDialogListener() {

			@Override
			public void onPositiveClick() {
				super.onPositiveClick();
				App.doLogin(mContext);
				finish();
			}
		});
		dialog.show();
	}

	private View getIndicator(int id) {
		LinearLayout view = (LinearLayout) LayoutInflater.from(mContext)
				.inflate(R.layout.tab_item, null);
		ImageView icon = (ImageView) view.findViewById(R.id.icon);
		// TextView text = (TextView) view.findViewById(R.id.text);
		switch (id) {
		case 0:
			icon.setImageResource(R.drawable.ic_tab_home_1);
			// text.setText("首页");
			break;
		case 1:
			icon.setImageResource(R.drawable.ic_tab_mention_1);
			// text.setText("提及");
			break;
		case 2:
			icon.setImageResource(R.drawable.ic_tab_dm_1);
			// text.setText("私信");
			break;
		case 3:
			icon.setImageResource(R.drawable.ic_tab_browse_1);
			// text.setText("公共");
			break;
		case 4:
			icon.setImageResource(R.drawable.ic_more);
			// text.setText("更多");
			break;
		default:
			break;
		}

		return view;
	}

	@Override
	protected void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		outState.putString("tab", mTabHost.getCurrentTabTag());
	}

	/**
	 * This is a helper class that implements a generic mechanism for
	 * associating fragments with the tabs in a tab host. It relies on a trick.
	 * Normally a tab host has a simple API for supplying a View or Intent that
	 * each tab will show. This is not sufficient for switching between
	 * fragments. So instead we make the content part of the tab host 0dp high
	 * (it is not shown) and the TabManager supplies its own dummy view to show
	 * as the tab content. It listens to changes in tabs, and takes care of
	 * switch to the correct fragment shown in a separate content area whenever
	 * the selected tab changes.
	 */
	public static class TabManager implements TabHost.OnTabChangeListener {
		private final FragmentActivity mActivity;
		private final TabHost mTabHost;
		private final int mContainerId;
		private final HashMap<String, TabInfo> mTabs = new HashMap<String, TabInfo>();
		TabInfo mLastTab;

		static final class TabInfo {
			private final String tag;
			private final Class<?> clss;
			private final Bundle args;
			private Fragment fragment;

			TabInfo(String _tag, Class<?> _class, Bundle _args) {
				tag = _tag;
				clss = _class;
				args = _args;
			}
		}

		static class DummyTabFactory implements TabHost.TabContentFactory {
			private final Context mContext;

			public DummyTabFactory(Context context) {
				mContext = context;
			}

			@Override
			public View createTabContent(String tag) {
				View v = new View(mContext);
				v.setMinimumWidth(0);
				v.setMinimumHeight(0);
				return v;
			}
		}

		public TabManager(FragmentActivity activity, TabHost tabHost,
				int containerId) {
			mActivity = activity;
			mTabHost = tabHost;
			mContainerId = containerId;
			mTabHost.setOnTabChangedListener(this);
		}

		public void addTab(TabHost.TabSpec tabSpec, Class<?> clss, Bundle args) {
			tabSpec.setContent(new DummyTabFactory(mActivity));
			String tag = tabSpec.getTag();

			TabInfo info = new TabInfo(tag, clss, args);

			// Check to see if we already have a fragment for this tab, probably
			// from a previously saved state. If so, deactivate it, because our
			// initial state is that a tab isn't shown.
			info.fragment = mActivity.getSupportFragmentManager()
					.findFragmentByTag(tag);
			if (info.fragment != null && !info.fragment.isDetached()) {
				FragmentTransaction ft = mActivity.getSupportFragmentManager()
						.beginTransaction();
				ft.detach(info.fragment);
				ft.commit();
			}

			mTabs.put(tag, info);
			mTabHost.addTab(tabSpec);
		}

		@Override
		public void onTabChanged(String tabId) {
			TabInfo newTab = mTabs.get(tabId);
			if (mLastTab != newTab) {
				FragmentTransaction ft = mActivity.getSupportFragmentManager()
						.beginTransaction();
				if (mLastTab != null) {
					if (mLastTab.fragment != null) {
						ft.detach(mLastTab.fragment);
					}
				}
				if (newTab != null) {
					if (newTab.fragment == null) {
						newTab.fragment = Fragment.instantiate(mActivity,
								newTab.clss.getName(), newTab.args);
						ft.add(mContainerId, newTab.fragment, newTab.tag);
					} else {
						ft.attach(newTab.fragment);
					}
				}

				mLastTab = newTab;
				ft.commit();
				mActivity.getSupportFragmentManager()
						.executePendingTransactions();
			}
		}
	}

	@Override
	protected void initialize() {
		// TODO Auto-generated method stub

	}

	@Override
	protected void setLayout() {
		// TODO Auto-generated method stub

	}
}
