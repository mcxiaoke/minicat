package org.mcxiaoke.fancooker;

import java.util.ArrayList;
import java.util.List;

import org.mcxiaoke.fancooker.dao.model.UserModel;
import org.mcxiaoke.fancooker.fragments.AbstractFragment;
import org.mcxiaoke.fancooker.fragments.ProfileFragment;
import org.mcxiaoke.fancooker.fragments.UserFavoritesFragment;
import org.mcxiaoke.fancooker.fragments.UserTimelineFragment;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;

import com.viewpagerindicator.TitlePageIndicator;

/**
 * @author mcxiaoke
 * @version 1.0 2012.02.07
 * @version 1.1 2012.02.08
 * @version 1.2 2012.02.09
 * @version 1.3 2012.02.22
 * @version 2.0 2012.03.06
 * @version 2.5 2012.03.23
 * @version 2.6 2012.03.27
 * 
 */
public class UIProfile extends UIBaseSupport implements OnPageChangeListener {

	public static final String TAG = UIProfile.class.getSimpleName();

	private ViewPager mViewPager;
	private PagesAdapter mPagesAdapter;
	private TitlePageIndicator mIndicator;

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
		setContentView(R.layout.ui_profile_viewpager);

		mPagesAdapter = new PagesAdapter(getSupportFragmentManager(), userId);
		mViewPager = (ViewPager) findViewById(R.id.viewpager);
		mViewPager.setAdapter(mPagesAdapter);
		mViewPager.setCurrentItem(1);

		mIndicator = (TitlePageIndicator) findViewById(R.id.indicator);
		mIndicator.setViewPager(mViewPager);
		mIndicator.setOnPageChangeListener(this);
	}

	@Override
	protected void onResume() {
		super.onResume();
	}

	@Override
	protected void onPause() {
		super.onPause();
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

		private final List<AbstractFragment> fragments = new ArrayList<AbstractFragment>();
		private String userId;

		public PagesAdapter(FragmentManager fm, String uid) {
			super(fm);
			this.userId = uid;
			addFragments();
		}

		@Override
		public AbstractFragment getItem(int position) {
			return fragments.get(position);
		}

		@Override
		public int getCount() {
			return fragments.size();
		}

		@Override
		public CharSequence getPageTitle(int position) {
			return getItem(position).getTitle();
		}

		private void addFragments() {
			fragments.add(UserFavoritesFragment.newInstance(userId));
			fragments.add(ProfileFragment.newInstance(userId));
			fragments.add(UserTimelineFragment.newInstance(userId));
		}

	}

}
