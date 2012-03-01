package com.fanfou.app.hd;

import java.util.HashMap;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;

import com.astuetz.viewpager.extensions.FixedTabsView;
import com.astuetz.viewpager.extensions.TabsAdapter;
import com.astuetz.viewpager.extensions.ViewPagerTabButton;
import com.fanfou.app.hd.dao.model.UserModel;
import com.fanfou.app.hd.fragments.AbstractFragment;
import com.fanfou.app.hd.fragments.ProfileContentFragment;
import com.fanfou.app.hd.fragments.UserFavoritesFragment;
import com.fanfou.app.hd.fragments.UserTimelineFragment;

/**
 * @author mcxiaoke
 * @version 1.0 2012.02.07
 * @version 1.1 2012.02.08
 * @version 1.2 2012.02.09
 * @version 1.3 2012.02.22
 * 
 */
public class UIProfile extends UIBaseSupport {

	public static final String TAG = UIProfile.class.getSimpleName();

	public static enum Page {FAVORITES,PROFILE, TIMELINE
	};

	public static final int NUMS_OF_PAGE = Page.values().length;
	private static final HashMap<Page, String> sTitles = new HashMap<UIProfile.Page, String>(
			NUMS_OF_PAGE);

	static {
//		sTitles.put(Page.FOLLOWERS, "关注者");
//		sTitles.put(Page.FRIENDS, "好友");
		sTitles.put(Page.FAVORITES, "收藏");
		sTitles.put(Page.PROFILE, "简介");
		sTitles.put(Page.TIMELINE, "消息");
	}

	private ViewPager mViewPager;
	private PagesAdapter mPagesAdapter;
	private FixedTabsView mTabsView;

	private UserModel user;
	private String userId;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
	}

	@Override
	protected void onNewIntent(Intent intent) {
		super.onNewIntent(intent);
		setIntent(intent);
	}

	@Override
	protected void initialize() {
		parseIntent();
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
	}

	@Override
	protected void setLayout() {
		setContentView(R.layout.ui_profile);

		mPagesAdapter = new PagesAdapter(getSupportFragmentManager(), userId);
		mViewPager = (ViewPager) findViewById(R.id.viewpager);
		mViewPager.setAdapter(mPagesAdapter);
		mViewPager.setCurrentItem(Page.PROFILE.ordinal());

		mTabsView = (FixedTabsView) findViewById(R.id.viewindicator);
		mTabsView.setAdapter(new PageTabsAdapter(this));
		mTabsView.setViewPager(mViewPager);
	}

	@Override
	protected void onResume() {
		super.onResume();
	}

	@Override
	protected void onPause() {
		super.onPause();
	}

	private static class PagesAdapter extends FragmentPagerAdapter {

		private final AbstractFragment[] fragments = new AbstractFragment[NUMS_OF_PAGE];
		private String userId;

		public PagesAdapter(FragmentManager fm, String uid) {
			super(fm);
			this.userId = uid;
			fragments[Page.FAVORITES.ordinal()] = UserFavoritesFragment
					.newInstance(userId);
			fragments[Page.PROFILE.ordinal()] = ProfileContentFragment
					.newInstance(userId);
			fragments[Page.TIMELINE.ordinal()] = UserTimelineFragment
					.newInstance(userId);
		}

		@Override
		public AbstractFragment getItem(int position) {
			return fragments[position];
		}

		@Override
		public int getCount() {
			return NUMS_OF_PAGE;
		}

		public void updateUI(int position) {
			getItem(position).updateUI();
		}

	}

	private static class PageTabsAdapter implements TabsAdapter {

		private Activity mContext;

		public PageTabsAdapter(Activity ctx) {
			this.mContext = ctx;
		}

		@Override
		public View getView(int position) {
			ViewPagerTabButton tab;

			LayoutInflater inflater = mContext.getLayoutInflater();
			tab = (ViewPagerTabButton) inflater.inflate(R.layout.tab_fixed,
					null);

			if (position < NUMS_OF_PAGE)
				tab.setText(sTitles.get(Page.values()[position]));

			return tab;
		}

	}

}
