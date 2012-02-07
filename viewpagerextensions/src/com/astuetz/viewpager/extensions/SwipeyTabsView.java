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
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.widget.RelativeLayout;


public class SwipeyTabsView extends RelativeLayout implements OnPageChangeListener, OnTouchListener {
	
  @SuppressWarnings("unused")
  private static final String TAG = "com.astuetz.viewpager.extensions";
	
	// Scrolling direction
	private enum Direction {
		None, Left, Right
	}
	
	private int mPosition;
	
	// This ArrayList stores the positions for each tab.
	private ArrayList<TabPosition> mPositions = new ArrayList<TabPosition>();
	
	
	// Length of the horizontal fading edges
	private static final int SHADOW_WIDTH = 20;
	
	private ViewPager mPager;
	
	private TabsAdapter mAdapter;
	
	
	private int mTabsCount = 0;
	
	private int mWidth = 0;
	
	private int mCenter = 0;
	
	private int mHighlightOffset = 0;
	
	
	// The offset at which tabs are going to
	// be moved, if they are outside the screen
	private int mOutsideOffset = -1;
	
	
	public SwipeyTabsView(Context context) {
		this(context, null);
	}
	
	public SwipeyTabsView(Context context, AttributeSet attrs) {
		this(context, attrs, 0);
	}
	
	public SwipeyTabsView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		
		setHorizontalFadingEdgeEnabled(false);
		setFadingEdgeLength((int) (getResources().getDisplayMetrics().density * SHADOW_WIDTH));
		setWillNotDraw(false);
		
		setOnTouchListener(this);
	}
	
	@Override
	protected float getLeftFadingEdgeStrength() {
		return 1.0f;
	}
	
	@Override
	protected float getRightFadingEdgeStrength() {
		return 1.0f;
	}
	
	
	/**
	 * Notify the view that new data is available.
	 */
	public void notifyDatasetChanged() {
		if (mPager != null && mAdapter != null) {
			initTabs();
			calculateNewPositions(true);
		}
	}
	
	public void setAdapter(TabsAdapter adapter) {
		this.mAdapter = adapter;
		
		if (mPager != null && mAdapter != null) initTabs();
	}
	
	/**
	 * Binds the {@link ViewPager} to this instance
	 * 
	 * @param pager
	 *          An instance of {@link ViewPager}
	 */
	public void setViewPager(ViewPager pager) {
		this.mPager = pager;
		mPager.setOnPageChangeListener(this);
		
		if (mPager != null && mAdapter != null) initTabs();
	}
	
	/**
	 * Initialize and add all tabs to the Layout
	 */
	private void initTabs() {
		
		// Remove all old child views
		removeAllViews();
		
		mPositions.clear();
		
		if (mAdapter == null || mPager == null) return;
		
		for (int i = 0; i < mPager.getAdapter().getCount(); i++) {
			addTab(mAdapter.getView(i), i);
			mPositions.add(new TabPosition());
		}
		
		mTabsCount = getChildCount();
		
		mPosition = mPager.getCurrentItem();
	}
	
	/**
	 * Adds a new {@link SwipeyTabButton} to the layout
	 * 
	 * @param index
	 *          The index from the Pagers adapter
	 * @param title
	 *          The title which should be used
	 */
	public void addTab(View tab, final int index) {
		if (tab == null) return;
		
		addView(tab);
		
		tab.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				mPager.setCurrentItem(index);
			}
		});
		
		tab.setOnTouchListener(this);
	}
	
	/**
	 * 
	 */
	@Override
	protected void onSizeChanged(int w, int h, int oldw, int oldh) {
		
		// Set a default outsideOffset
		if (mOutsideOffset < 0) mOutsideOffset = w;
		
		mWidth = w;
		mCenter = w / 2;
		mHighlightOffset = w / 5;
		
		calculateNewPositions(true);
		
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		
		int maxTabHeight = 0;
		
		final int childWidthMeasureSpec = MeasureSpec.makeMeasureSpec(
		    MeasureSpec.getSize(widthMeasureSpec), MeasureSpec.AT_MOST);
		final int childHeightMeasureSpec = MeasureSpec.makeMeasureSpec(
		    MeasureSpec.getSize(widthMeasureSpec), MeasureSpec.AT_MOST);
		
		for (int i = 0; i < mTabsCount; i++) {
			final View child = getChildAt(i);
			
			if (child.getVisibility() == GONE) continue;
			
			child.measure(childWidthMeasureSpec, childHeightMeasureSpec);
			
			mPositions.get(i).width = child.getMeasuredWidth();
			mPositions.get(i).height = child.getMeasuredHeight();
			
			maxTabHeight = Math.max(maxTabHeight, mPositions.get(i).height);
		}
		
		setMeasuredDimension(resolveSize(0, widthMeasureSpec),
		    resolveSize(maxTabHeight + getPaddingTop() + getPaddingBottom(), heightMeasureSpec));
		
	}
	
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void onLayout(boolean changed, int l, int t, int r, int b) {
		
		final int paddingTop = getPaddingTop();
		
		for (int i = 0; i < mTabsCount; i++) {
			
			final View tab = getChildAt(i);
			TabPosition pos = mPositions.get(i);
			
			if (tab instanceof SwipeyTab) {
				
				final int tabCenter = mPositions.get(i).currentPos + tab.getMeasuredWidth() / 2;
				final int diff = Math.abs(mCenter - tabCenter);
				final int p = (int) 100 * diff / mHighlightOffset;
				
				((SwipeyTab) tab).setHighlightPercentage(diff <= mHighlightOffset ? 100 - p : 0);
				
			}
			
			tab.layout(pos.currentPos, paddingTop, pos.currentPos + pos.width, paddingTop + pos.height);
			
		}
		
	}
	
	/**
	 * This method calculates the previous, current and next position for each tab
	 * 
	 * -5 -4 -3 /-2 |-1 0 +1| +2\ +3 +4 +5
	 * 
	 * There are the following cases:
	 * 
	 * [1] -5 to -3 are outside the screen [2] -2 is outside the screen, may come
	 * into the screen when swiping right [3] -1 is inside the screen, aligned at
	 * the left [4] 0 is inside the screen, aligned at the center [5] +1 is inside
	 * the screen, aligned at the right [6] +2 is outside the screen, may come
	 * into the screen when swiping left [7] +3 to +5 are outside the screen
	 * 
	 * @param layout
	 *          If true, all tabs will be aligned at their initial position
	 */
	private void calculateNewPositions(boolean layout) {
		
		if (mTabsCount == 0) return;
		
		final int currentItem = mPosition;
		
		for (int i = 0; i < mTabsCount; i++) {
			
			if (i < currentItem - 2) alignLeftOutside(i, false);
			else if (i == currentItem - 2) alignLeftOutside(i, true);
			else if (i == currentItem - 1) alignLeft(i);
			else if (i == currentItem) alignCenter(i);
			else if (i == currentItem + 1) alignRight(i);
			else if (i == currentItem + 2) alignRightOutside(i, true);
			else if (i > currentItem + 2) alignRightOutside(i, false);
			
		}
		
		preventFromOverlapping();
		
		if (layout) {
			for (TabPosition p : mPositions) {
				p.currentPos = p.oldPos;
			}
		}
		
	}
	
	
	private int leftOutside(int position) {
		View tab = getChildAt(position);
		final int width = tab.getMeasuredWidth();
		return width * (-1) - mOutsideOffset;
	}
	
	private int left(int position) {
		View tab = getChildAt(position);
		return 0 - tab.getPaddingLeft();
	}
	
	private int center(int position) {
		View tab = getChildAt(position);
		final int width = tab.getMeasuredWidth();
		return mWidth / 2 - width / 2;
	}
	
	private int right(int position) {
		View tab = getChildAt(position);
		final int width = tab.getMeasuredWidth();
		return mWidth - width + tab.getPaddingRight();
	}
	
	private int rightOutside(int position) {
		return mWidth + mOutsideOffset;
	}
	
	
	private void alignLeftOutside(int position, boolean canComeToLeft) {
		TabPosition pos = mPositions.get(position);
		
		pos.oldPos = leftOutside(position);
		pos.leftPos = pos.oldPos;
		pos.rightPos = canComeToLeft ? left(position) : pos.oldPos;
	}
	
	private void alignLeft(int position) {
		TabPosition pos = mPositions.get(position);
		
		pos.leftPos = leftOutside(position);
		pos.oldPos = left(position);
		pos.rightPos = center(position);
	}
	
	private void alignCenter(int position) {
		TabPosition pos = mPositions.get(position);
		
		pos.leftPos = left(position);
		pos.oldPos = center(position);
		pos.rightPos = right(position);
	}
	
	private void alignRight(int position) {
		TabPosition pos = mPositions.get(position);
		
		pos.leftPos = center(position);
		pos.oldPos = right(position);
		pos.rightPos = rightOutside(position);
	}
	
	private void alignRightOutside(int position, boolean canComeToRight) {
		TabPosition pos = mPositions.get(position);
		
		pos.oldPos = rightOutside(position);
		pos.rightPos = pos.oldPos;
		pos.leftPos = canComeToRight ? right(position) : pos.oldPos;
	}
	
	/**
	 * 
	 */
	private void preventFromOverlapping() {
		
		final int currentItem = mPosition;
		
		TabPosition leftOutside = currentItem > 1 ? mPositions.get(currentItem - 2) : null;
		TabPosition left = currentItem > 0 ? mPositions.get(currentItem - 1) : null;
		TabPosition center = mPositions.get(currentItem);
		TabPosition right = currentItem < mTabsCount - 1 ? mPositions.get(currentItem + 1) : null;
		TabPosition rightOutside = currentItem < mTabsCount - 2 ? mPositions.get(currentItem + 2) : null;
		
		if (leftOutside != null) {
			if (leftOutside.rightPos + leftOutside.width >= left.rightPos)
			{
				leftOutside.rightPos = left.rightPos - leftOutside.width;
			}
		}
		
		if (left != null) {
			if (left.oldPos + left.width >= center.oldPos) {
				left.oldPos = center.oldPos - left.width;
			}
			if (center.rightPos <= left.rightPos + left.width) {
				center.rightPos = left.rightPos + left.width;
			}
		}
		
		if (right != null) {
			if (right.oldPos <= center.oldPos + center.width) {
				right.oldPos = center.oldPos + center.width;
			}
			if (center.leftPos + center.width >= right.leftPos) {
				center.leftPos = right.leftPos - center.width;
			}
		}
		
		if (rightOutside != null) {
			if (rightOutside.leftPos <= right.leftPos + right.width) {
				rightOutside.leftPos = right.leftPos + right.width;
			}
		}
	}
	
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public void onPageScrollStateChanged(int state) {}
	
	/**
	 * At this point the scrolling direction is determined and every child is
	 * interpolated to its previous or next position
	 * 
	 * {@inheritDoc}
	 */
	@Override
	public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
		
		Direction dir = Direction.None;
		
		if (position != mPosition && positionOffset == 0.0f) {
			mPosition = position;
			calculateNewPositions(true);
		}
		
		final int currentScrollX = mPosition * (mPager.getWidth() + mPager.getPageMargin());
		
		// Check if the user is swiping to the left or to the right
		
		if (mPager.getScrollX() < currentScrollX) dir = Direction.Left;
		else if (mPager.getScrollX() > currentScrollX) dir = Direction.Right;
		
		float x = 0.0f;
		if (dir == Direction.Left) x = 1 - positionOffset;
		else if (dir == Direction.Right) x = positionOffset;
		
		// Iterate over all tabs and set their current positions
		
		for (int i = 0; i < mTabsCount; i++) {
			TabPosition pos = mPositions.get(i);
			
			final float y0 = pos.oldPos;
			float y1 = 0.0f;
			
			if (dir == Direction.Left) y1 = pos.rightPos;
			else if (dir == Direction.Right) y1 = pos.leftPos;
			else y1 = pos.oldPos;
			
			if (y1 != y0) pos.currentPos = (int) (y0 + (y1 * x - y0 * x));
		}
		
		requestLayout();
		
	}
	
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public void onPageSelected(int position) {
		
	}
	
	

	/**
	 * Helper class which holds different positions (and the width) for a tab
	 * 
	 */
	private class TabPosition {
		
		public int oldPos;
		
		public int leftPos;
		public int rightPos;
		
		public int currentPos;
		
		public int width;
		public int height;
		
		@Override
		public String toString() {
			StringBuilder sb = new StringBuilder();
			
			sb.append("oldPos: ").append(oldPos).append(", ");
			sb.append("leftPos: ").append(leftPos).append(", ");
			sb.append("rightPos: ").append(rightPos).append(", ");
			sb.append("currentPos: ").append(currentPos);
			
			return sb.toString();
		}
	}


	
	/**
	 * still testing this...
	 */
	private float mDragX = 0.0f; 

	@Override
  public boolean onTouch(View v, MotionEvent event) {
		float x = event.getRawX();
		
		switch (event.getAction())
		{
			case MotionEvent.ACTION_DOWN:
				mDragX = x;
				mPager.beginFakeDrag();
				break;
			case MotionEvent.ACTION_MOVE:
				mPager.fakeDragBy((mDragX - x) * (-1));
				mDragX = x;
				break;
			case MotionEvent.ACTION_UP:
				mPager.endFakeDrag();
				break;
		}
		
		return v.equals(this) ? true : super.onTouchEvent(event);
  }
	
}
