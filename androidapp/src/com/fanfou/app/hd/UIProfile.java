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
import com.fanfou.app.App;
import com.fanfou.app.R;
import com.fanfou.app.api.User;
import com.fanfou.app.cache.CacheManager;
import com.fanfou.app.hd.ui.AbstractFragment;
import com.fanfou.app.hd.ui.FollowersListFragment;
import com.fanfou.app.hd.ui.FriendsListFragment;
import com.fanfou.app.hd.ui.ProfileContentFragment;
import com.fanfou.app.hd.ui.PullToRefreshListFragment;
import com.fanfou.app.hd.ui.UserFavoritesFragment;
import com.fanfou.app.hd.ui.UserTimelineFragment;
import com.fanfou.app.service.Constants;
import com.fanfou.app.ui.ActionBar;
import com.fanfou.app.ui.ActionManager;
import com.fanfou.app.ui.viewpager.TitleProvider;

/**
 * @author mcxiaoke
 * @version 1.0 2012.02.07
 * 
 */
public class UIProfile extends UIBase {

	public static final String TAG = UIProfile.class.getSimpleName();

	private static enum Page {FRIENDS,PROFILE ,TIMELINE 
	};

	public static final int NUMS_OF_PAGE = Page.values().length;
	private static final HashMap<Page, String> sTitles = new HashMap<UIProfile.Page, String>(
			NUMS_OF_PAGE);

	static {
//		sTitles.put(Page.FAVORITES, "收藏列表");
		sTitles.put(Page.FRIENDS, "好友");
		sTitles.put(Page.PROFILE, "简介");
		sTitles.put(Page.TIMELINE, "消息");
//		sTitles.put(Page.FOLLOWERS, "关注者列表");
	}

	private ActionBar mActionBar;
	private ViewPager mViewPager;
	private PagesAdapter mPagesAdapter;
	private FixedTabsView mTabsView;

	private User user;
	private String userId;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
	}

	@Override
	protected void initialize() {
		parseIntent();
	}

	private void parseIntent() {
		Intent intent = getIntent();
		String action = intent.getAction();
		if (action == null) {
			userId = intent.getStringExtra(Constants.EXTRA_ID);
			user = (User) intent.getParcelableExtra(Constants.EXTRA_DATA);
			if (user != null) {
				userId = user.id;
			}
		} else if (action.equals(Intent.ACTION_VIEW)) {
			Uri data = intent.getData();
			if (data != null) {
				userId = data.getLastPathSegment();
			}
		}
		if (user == null && userId != null) {
			user = CacheManager.getUser(this, userId);
		}

		if (user != null) {
			userId = user.id;
		}

		if (App.getUserId().equals(userId)) {
			ActionManager.doMyProfile(this);
			finish();
		}
	}

	@Override
	protected void setLayout() {
		setContentView(R.layout.ui_profile);

		mActionBar = (ActionBar) findViewById(R.id.actionbar);
		mActionBar.setRightAction(new ActionBar.WriteAction(this, null));
		setActionBarSwipe(mActionBar);

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

	private static class PagesAdapter extends FragmentPagerAdapter implements
			TitleProvider {

		private final AbstractFragment[] fragments = new AbstractFragment[NUMS_OF_PAGE];
		private String userId;

		public PagesAdapter(FragmentManager fm, String uid) {
			super(fm);
			this.userId = uid;
//			fragments[Page.FAVORITES.ordinal()] = UserFavoritesFragment
//					.newInstance(userId);
			fragments[Page.FRIENDS.ordinal()] = FriendsListFragment
					.newInstance(userId);
			fragments[Page.PROFILE.ordinal()] = ProfileContentFragment
					.newInstance(userId);
			fragments[Page.TIMELINE.ordinal()] = UserTimelineFragment
					.newInstance(userId);
//			fragments[Page.FOLLOWERS.ordinal()] = FollowersListFragment
//					.newInstance(userId);
		}

		@Override
		public AbstractFragment getItem(int position) {
			return fragments[position % NUMS_OF_PAGE];
		}

		@Override
		public int getCount() {
			return NUMS_OF_PAGE;
		}

		public void updateUI(int position) {
			getItem(position).updateUI();
		}

		@Override
		public String getTitle(int position) {
			return sTitles.get(Page.values()[position % NUMS_OF_PAGE]);
			// return PAGE_TITLES[position % NUMS_OF_PAGE];
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
