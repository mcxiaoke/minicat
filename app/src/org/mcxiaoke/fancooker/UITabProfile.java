package org.mcxiaoke.fancooker;

import java.util.HashMap;

import org.mcxiaoke.fancooker.controller.UIController;
import org.mcxiaoke.fancooker.dao.model.UserModel;
import org.mcxiaoke.fancooker.fragments.ProfileFragment;
import org.mcxiaoke.fancooker.fragments.UserFavoritesFragment;
import org.mcxiaoke.fancooker.fragments.UserTimelineFragment;
import org.mcxiaoke.fancooker.ui.widget.OnActionClickListener;

import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentTransaction;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TabHost;

public class UITabProfile extends UIBaseSupport implements
		OnActionClickListener {

	TabHost mTabHost;
	TabManager mTabManager;

	private UserModel user;
	private String userId;
	private boolean permission;
	private boolean self;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		if (savedInstanceState != null) {
			mTabHost.setCurrentTabByTag(savedInstanceState.getString("tab"));
		} else {
			mTabHost.setCurrentTabByTag("profile");
		}

		parseIntent();
		setLayout();
	}

	@Override
	protected void onNewIntent(Intent intent) {
		super.onNewIntent(intent);
		setIntent(intent);
	}

	@Override
	protected int getMenuResourceId() {
		return self ? R.menu.myprofile_menu : R.menu.base_menu;
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		return super.onCreateOptionsMenu(menu);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		int id = item.getItemId();
		if (id == R.id.menu_refresh) {
			doRefreshProfile();
			return true;
		}
		return super.onOptionsItemSelected(item);
	}

	private void doRefreshProfile() {

	}

	private void doEditProfile() {
		UIController.showEditProfile(mContext, user);
	}

	protected void setLayout() {
		setContentView(R.layout.fm_tabs);
		mTabHost = (TabHost) findViewById(android.R.id.tabhost);
		mTabHost.setup();

		Bundle args = new Bundle();
		args.putBoolean("refresh", true);
		args.putString("id", userId);
		args.putBoolean("permission", permission);

		mTabManager = new TabManager(this, mTabHost, R.id.realtabcontent);

		mTabManager.addTab(
				mTabHost.newTabSpec("favorites").setIndicator(getIndicator(0)),
				UserFavoritesFragment.class, args);
		mTabManager.addTab(
				mTabHost.newTabSpec("profile").setIndicator(getIndicator(1)),
				ProfileFragment.class, args);
		mTabManager.addTab(
				mTabHost.newTabSpec("timeline").setIndicator(getIndicator(2)),
				UserTimelineFragment.class, args);
	}

	private void parseIntent() {
		Intent intent = getIntent();
		String action = intent.getAction();
		if (action == null) {
			userId = intent.getStringExtra("id");
			user = (UserModel) intent.getParcelableExtra("data");
			if (user != null) {
				userId = user.getId();
			}
		} else if (action.equals(Intent.ACTION_VIEW)) {
			Uri data = intent.getData();
			if (data != null) {
				userId = data.getLastPathSegment();
			}
		}

		if (user != null) {
			userId = user.getId();
		}

		self = AppContext.getAccount().equals(userId);
	}

	private View getIndicator(int id) {
		LinearLayout view = (LinearLayout) LayoutInflater.from(mContext)
				.inflate(R.layout.tab_item, null);
		ImageView icon = (ImageView) view.findViewById(R.id.icon);
		// TextView text = (TextView) view.findViewById(R.id.text);
		switch (id) {
		case 0:
			icon.setImageResource(R.drawable.ic_tab_favorites_1);
			// text.setText("收藏");
			break;
		case 1:
			icon.setImageResource(R.drawable.ic_tab_profile_1);
			// text.setText("资料");
			break;
		case 2:
			icon.setImageResource(R.drawable.ic_tab_timeline_1);
			// text.setText("消息");
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
		private final Activity mActivity;
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

		public TabManager(Activity activity, TabHost tabHost, int containerId) {
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
			info.fragment = mActivity.getFragmentManager().findFragmentByTag(
					tag);
			if (info.fragment != null && !info.fragment.isDetached()) {
				FragmentTransaction ft = mActivity.getFragmentManager()
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
				FragmentTransaction ft = mActivity.getFragmentManager()
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
				mActivity.getFragmentManager().executePendingTransactions();
			}
		}
	}

	@Override
	public void onActionClick(int position, String tag) {
		mTabHost.setCurrentTabByTag(tag);
		// mTabManager.onTabChanged(tag);
	}

}
