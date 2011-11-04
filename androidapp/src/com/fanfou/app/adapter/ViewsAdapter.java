package com.fanfou.app.adapter;

import android.os.Parcelable;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.View;

/**
 * @author mcxiaoke
 * @version 1.1 2011.11.04
 *
 */
public class ViewsAdapter extends PagerAdapter {
	private View[] mViews;

	public ViewsAdapter(View[] views) {
		this.mViews = views;
	}

	@Override
	public int getCount() {
//		return mViews.length;
		return Integer.MAX_VALUE;
	}

	@Override
	public Object instantiateItem(View container, int position) {
		View view = mViews[position%mViews.length];
		((ViewPager) container).addView(view);
		return view;
	}

	@Override
	public void destroyItem(View container, int position, Object view) {
		((ViewPager) container).removeView((View) view);
	}

	@Override
	public void startUpdate(View container) {
	}

	@Override
	public void finishUpdate(View container) {
	}

	@Override
	public boolean isViewFromObject(View view, Object object) {
		return view == (View) object;
	}

	@Override
	public Parcelable saveState() {
		return null;
	}

	@Override
	public void restoreState(Parcelable state, ClassLoader loader) {
	}

}
