package com.fanfou.app.hd;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.Log;

import com.fanfou.app.R;
import com.fanfou.app.hd.module.TimelineFragment;

/**
 * @author mcxiaoke
 * @version 1.0 2011.11.02
 * 
 */
public class HomeScreen extends BaseFragmentActivity {
	
	private static final String TAG=HomeScreen.class.getSimpleName();
	
	private static final int NUMS_OF_PAGE=4;
	
	private ViewPager mViewPager;
	private PagerViewAdapter mViewAdapter;

	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.screen_home);
		
		
		mViewAdapter=new PagerViewAdapter(getSupportFragmentManager());
		mViewPager=(ViewPager) findViewById(R.id.viewpager);
		mViewPager.setAdapter(mViewAdapter);
		

//		ListFragment fragment = new TimelineFragment();
//
//		FragmentManager manager = getSupportFragmentManager();
//		FragmentTransaction transaction = manager.beginTransaction();
//		transaction.add(R.id.home_container, fragment);
//		transaction.commit();

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
	protected int getPageType() {
		return PAGE_HOME;
	}
	
	private void log(String message){
		Log.d(TAG, message);
	}
	
	private static class PagerViewAdapter extends FragmentPagerAdapter{

		public PagerViewAdapter(FragmentManager fm) {
			super(fm);
		}

		@Override
		public Fragment getItem(int position) {
			return TimelineFragment.newInstance(position);
		}

		@Override
		public int getCount() {
			return NUMS_OF_PAGE;
		}
		
	}

}
