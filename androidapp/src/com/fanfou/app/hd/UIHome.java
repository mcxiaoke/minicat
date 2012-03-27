package com.fanfou.app.hd;

import java.util.ArrayList;
import java.util.List;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.util.Log;
import android.view.View;

import com.actionbarsherlock.app.ActionBar;
import com.fanfou.app.hd.App.ApnType;
import com.fanfou.app.hd.cache.ImageLoader;
import com.fanfou.app.hd.controller.SimpleDialogListener;
import com.fanfou.app.hd.controller.UIController;
import com.fanfou.app.hd.dialog.ConfirmDialog;
import com.fanfou.app.hd.fragments.AbstractListFragment;
import com.fanfou.app.hd.fragments.ConversationListFragment;
import com.fanfou.app.hd.fragments.HomeTimelineFragment;
import com.fanfou.app.hd.fragments.MentionTimelineFragment;
import com.fanfou.app.hd.fragments.PublicTimelineFragment;
import com.viewpagerindicator.TitlePageIndicator;

/**
 * @author mcxiaoke
 * @version 1.0 2011.05.31
 * @version 2.0 2011.07.16
 * @version 3.0 2011.09.24
 * @version 3.2 2011.10.25
 * @version 3.3 2011.10.27
 * @version 3.5 2011.10.29
 * @version 3.6 2011.11.02
 * @version 3.7 2011.11.04
 * @version 4.0 2011.11.04
 * @version 4.1 2011.11.07
 * @version 4.2 2011.11.08
 * @version 4.3 2011.11.09
 * @version 4.4 2011.11.11
 * @version 4.5 2011.11.16
 * @version 4.6 2011.11.21
 * @version 4.7 2011.11.22
 * @version 4.8 2011.11.30
 * @version 4.9 2011.12.02
 * @version 5.0 2011.12.05
 * @version 5.1 2011.12.06
 * @version 5.2 2011.12.09
 * @version 5.3 2011.12.13
 * @version 6.0 2011.12.19
 * @version 6.1 2011.12.23
 * @version 6.2 2011.12.26
 * @version 7.0 2012.01.30
 * @version 7.1 2012.01.31
 * @version 8.0 2012.02.06
 * @version 8.1 2012.02.07
 * @version 8.2 2012.02.08
 * @version 8.3 2012.02.09
 * @version 8.4 2012.02.10
 * @version 8.5 2012.02.27
 * @version 8.6 2012.02.28
 * @version 8.7 2012.03.09
 * @version 9.0 2012.03.23
 * 
 */
public class UIHome extends UIBaseSupport implements OnPageChangeListener {

	public static final String TAG = UIHome.class.getSimpleName();

	private ViewPager mViewPager;
	private PagesAdapter mPagesAdapter;
	private TitlePageIndicator mIndicator;

	private void log(String message) {
		Log.d(TAG, message);
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		if (App.DEBUG) {
			log("onCreate()");
		}
	}

	@Override
	protected void setActionBar() {
		ActionBar ab = getSupportActionBar();
		ab.setDisplayOptions(0, ActionBar.DISPLAY_SHOW_HOME);
	}

	@Override
	protected void onMenuHomeClick() {
	}

	@Override
	protected void initialize() {
		ImageLoader.getInstance();
	}

	@Override
	protected void setLayout() {
		setContentView(R.layout.ui_home);
		setViewPager();

	}

	private void setViewPager() {
		mPagesAdapter = new PagesAdapter(getSupportFragmentManager());
		mViewPager = (ViewPager) findViewById(R.id.viewpager);
		mViewPager.setAdapter(mPagesAdapter);
//		mViewPager.setOnPageChangeListener(this);
		mViewPager.setCurrentItem((mPagesAdapter.getCount()-1) / 2);
		
		mIndicator=(TitlePageIndicator) findViewById(R.id.indicator);
		mIndicator.setViewPager(mViewPager);
		mIndicator.setOnPageChangeListener(this);
		
	}

	@Override
	protected void onResume() {
		super.onResume();
		if (App.DEBUG) {
			Log.d(TAG, "onResume()");
		}
	}

	@Override
	protected void onPause() {
		super.onPause();
		if (App.DEBUG) {
			Log.d(TAG, "onPause()");
		}
	}

	@Override
	protected void onStop() {
		super.onStop();
		if (App.DEBUG) {
			Log.d(TAG, "onStop()");
		}
		if (App.getApnType() != ApnType.WIFI) {
			App.getImageLoader().clearQueue();
		}
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		if (App.DEBUG) {
			Log.d(TAG, "onDestroy()");
		}
		App.getImageLoader().shutdown();
	}

	@Override
	protected int getPageType() {
		return PAGE_HOME;
	}

	@Override
	protected boolean isHomeScreen() {
		return true;
	}

	@Override
	protected void onNewIntent(Intent intent) {
		super.onNewIntent(intent);
		setIntent(intent);
		// int page = getIntent().getIntExtra(Constants.EXTRA_PAGE, HOME);
		// mViewPager.setCurrentItem(page);
		if (App.DEBUG) {
			// log("onNewIntent page=" + page);
		}
	}

	@Override
	protected int getMenuResourceId() {
		return R.menu.home_menu;
	}

	// @Override
	// public boolean onCreateOptionsMenu(Menu menu) {
	// getSupportMenuInflater().inflate(R.menu.home_menu, menu);
	// return true;
	// }

	@Override
	public boolean onOptionsItemSelected(
			com.actionbarsherlock.view.MenuItem item) {
		// return super.onOptionsItemSelected(item);
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

	@Override
	public void onClick(View v) {
	}

	@Override
	public void onPageScrollStateChanged(int arg0) {
	}

	@Override
	public void onPageScrolled(int arg0, float arg1, int arg2) {
	}

	@Override
	public void onPageSelected(int position) {
		setTitle(mPagesAdapter.getPageTitle(position));
	}

	private static class PagesAdapter extends FragmentPagerAdapter {

		private final List<AbstractListFragment> fragments = new ArrayList<AbstractListFragment>();

		public PagesAdapter(FragmentManager fm) {
			super(fm);
			addFragments();
		}

		@Override
		public CharSequence getPageTitle(int position) {
			return getItem(position).getTitle();
		}

		@Override
		public AbstractListFragment getItem(int position) {
			return fragments.get(position);
		}

		@Override
		public int getCount() {
			return fragments.size();
		}

		private void addFragments() {
			fragments.add(PublicTimelineFragment.newInstance(true));
			fragments.add(HomeTimelineFragment.newInstance(true));
			fragments.add(MentionTimelineFragment.newInstance(true));
			fragments.add(ConversationListFragment.newInstance(true));
		}
	}
}
