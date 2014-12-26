package com.mcxiaoke.minicat.adapter;

import android.os.Parcelable;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.View;

/**
 * @author mcxiaoke
 * @version 1.2 2011.11.11
 */
public class ViewsAdapter extends PagerAdapter {
    private View[] mViews;
    private boolean endless;

    public ViewsAdapter(View[] views) {
        this.mViews = views;
        this.endless = false;
    }

    public ViewsAdapter(View[] views, boolean endless) {
        this.mViews = views;
        this.endless = endless;
    }

    @Override
    public int getCount() {
        return endless ? Integer.MAX_VALUE : mViews.length;
    }

    @Override
    public void startUpdate(View container) {
    }

    @Override
    public Object instantiateItem(View container, int position) {
        View view = mViews[position % mViews.length];
        ((ViewPager) container).addView(view);
        return view;
    }

    @Override
    public void destroyItem(View container, int position, Object view) {
        ((ViewPager) container).removeView((View) view);
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
