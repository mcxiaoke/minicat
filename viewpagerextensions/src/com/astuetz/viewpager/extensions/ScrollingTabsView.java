/*
 * Copyright (C) 2011 Andreas Stuetz <andreas.stuetz@gmail.com>
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

package com.astuetz.viewpager.extensions;

import java.util.ArrayList;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.view.View;
import android.widget.HorizontalScrollView;
import android.widget.LinearLayout;

public class ScrollingTabsView extends HorizontalScrollView implements ViewPager.OnPageChangeListener {
	
	@SuppressWarnings("unused")
	private static final String TAG = "com.astuetz.viewpager.extensions";
	
	private Context mContext;
	
	private ViewPager mPager;
	
	private TabsAdapter mAdapter;
	
	private LinearLayout mContainer;
	
	private ArrayList<View> mTabs = new ArrayList<View>();
	
	private Drawable mDividerDrawable;
	
	private int mDividerColor = 0xFF636363;
	private int mDividerMarginTop = 12;
	private int mDividerMarginBottom = 12;
	private int mDividerWidth = 1;
	
	public ScrollingTabsView(Context context) {
		this(context, null);
	}
	
	public ScrollingTabsView(Context context, AttributeSet attrs) {
		this(context, attrs, 0);
	}
	
	public ScrollingTabsView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs);
		
		this.mContext = context;

		mDividerMarginTop = (int) (getResources().getDisplayMetrics().density * mDividerMarginTop);
		mDividerMarginBottom = (int) (getResources().getDisplayMetrics().density * mDividerMarginBottom);
		mDividerWidth = (int) (getResources().getDisplayMetrics().density * mDividerWidth); 
		
		final TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.ViewPagerExtensions, defStyle, 0);
		
		mDividerColor = a.getColor(R.styleable.ViewPagerExtensions_dividerColor, mDividerColor);
		
		mDividerMarginTop = a.getDimensionPixelSize(R.styleable.ViewPagerExtensions_dividerMarginTop, mDividerMarginTop);
		mDividerMarginBottom = a.getDimensionPixelSize(R.styleable.ViewPagerExtensions_dividerMarginBottom,
		    mDividerMarginBottom);
		
		mDividerDrawable = a.getDrawable(R.styleable.ViewPagerExtensions_dividerDrawable);
		
		a.recycle();
		
		this.setHorizontalScrollBarEnabled(false);
		this.setHorizontalFadingEdgeEnabled(false);
		
		mContainer = new LinearLayout(context);
		LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.FILL_PARENT);
		mContainer.setLayoutParams(params);
		mContainer.setOrientation(LinearLayout.HORIZONTAL);
		
		this.addView(mContainer);
		
	}
	
	/**
	 * Sets the data behind this ScrollingTabsView.
	 * 
	 * @param adapter
	 *          The {@link TabsAdapter} which is responsible for maintaining the
	 *          data backing this FixedTabsView and for producing a view to
	 *          represent an item in that data set.
	 */
	public void setAdapter(TabsAdapter adapter) {
		this.mAdapter = adapter;
		
		if (mPager != null && mAdapter != null) initTabs();
	}
	
	/**
	 * Binds the {@link ViewPager} to this View
	 * 
	 */
	public void setViewPager(ViewPager pager) {
		this.mPager = pager;
		mPager.setOnPageChangeListener(this);
		
		if (mPager != null && mAdapter != null) initTabs();
	}
	
	/**
	 * Initialize and add all tabs to the layout
	 */
	private void initTabs() {
		
		mContainer.removeAllViews();
		mTabs.clear();
		
		if (mAdapter == null) return;
		
		for (int i = 0; i < mPager.getAdapter().getCount(); i++) {
			
			final int index = i;
			
			View tab = mAdapter.getView(i);
			mContainer.addView(tab);
			
			tab.setFocusable(true);
			
			mTabs.add(tab);
			
			if (i != mPager.getAdapter().getCount() - 1) {
				mContainer.addView(getSeparator());
			}
			
			tab.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					if (mPager.getCurrentItem() == index) selectTab(index);
					else mPager.setCurrentItem(index);
				}
			});
			
		}
		
		selectTab(mPager.getCurrentItem());
	}
	
	@Override
	public void onPageScrollStateChanged(int state) {}
	
	@Override
	public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {}
	
	@Override
	public void onPageSelected(int position) {
		selectTab(position);
	}
	
	
	@Override
	protected void onLayout(boolean changed, int l, int t, int r, int b) {
		super.onLayout(changed, l, t, r, b);
		
		if (changed) selectTab(mPager.getCurrentItem());
	}
	
	/**
	 * Creates and returns a new Separator View
	 * 
	 * @return
	 */
	private View getSeparator() {
		View v = new View(mContext);
		
		LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(mDividerWidth, LayoutParams.FILL_PARENT);
		params.setMargins(0, mDividerMarginTop, 0, mDividerMarginBottom);
		v.setLayoutParams(params);
		
		if (mDividerDrawable != null) v.setBackgroundDrawable(mDividerDrawable);
		else v.setBackgroundColor(mDividerColor);
		
		return v;
	}
	
	
	/**
	 * Runs through all tabs and sets if they are currently selected.
	 * 
	 * @param position
	 *          The position of the currently selected tab.
	 */
	private void selectTab(int position) {
		
		for (int i = 0, pos = 0; i < mContainer.getChildCount(); i += 2, pos++) {
			View tab = mContainer.getChildAt(i);
			tab.setSelected(pos == position);
		}
		
		View selectedTab = mContainer.getChildAt(position * 2);
		
		final int w = selectedTab.getMeasuredWidth();
		final int l = selectedTab.getLeft();
		
		final int x = l - this.getWidth() / 2 + w / 2;
		
		smoothScrollTo(x, this.getScrollY());
		
	}
	
}
