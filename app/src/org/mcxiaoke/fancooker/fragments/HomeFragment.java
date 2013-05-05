/**
 * 
 */
package org.mcxiaoke.fancooker.fragments;

import org.mcxiaoke.fancooker.R;
import org.mcxiaoke.fancooker.adapter.HomePagesAdapter;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.support.v4.view.PagerTabStrip;
import android.support.v4.view.PagerTitleStrip;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

/**
 * @author mcxiaoke
 * 
 */
public class HomeFragment extends AbstractFragment {
	private static final String TAG = HomeFragment.class.getSimpleName();
	private ViewPager mViewPager;
	private PagerTabStrip mPagerTabStrip;
	private HomePagesAdapter mPagesAdapter;

	public static HomeFragment newInstance() {
		return new HomeFragment();
	}

	@Override
	public String getTitle() {
		return null;
	}

	@Override
	public void updateUI() {

	}

	@SuppressLint("NewApi")
	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		mPagesAdapter = new HomePagesAdapter(getChildFragmentManager());
		mViewPager.setAdapter(mPagesAdapter);
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View root = inflater.inflate(R.layout.viewpager, null);
		mViewPager = (ViewPager) root.findViewById(R.id.viewpager);
		mPagerTabStrip=(PagerTabStrip) root.findViewById(R.id.viewpager_strip);
		mPagerTabStrip.setDrawFullUnderline(false);
		return root;
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
	}

	@Override
	public void onDestroyView() {
		super.onDestroyView();
	}

}
