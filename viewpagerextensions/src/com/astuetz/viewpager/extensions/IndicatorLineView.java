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

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.AsyncTask;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.util.AttributeSet;
import android.view.View;


public class IndicatorLineView extends View implements OnPageChangeListener {
	
	@SuppressWarnings("unused")
	private static final String TAG = "com.astuetz.viewpager.extensions";
	
	private ViewPager mPager;
	
	private int mLineColor = 0xFF34B5E8;
	
	private float mLineLeft = 0.0f;
	private float mLineWidth = 0.0f;
	
	private int mFadeOutTime = 500;
	private int mFadingDuration = 200;
	
	private int mAlpha = 0xFF;
	
	private FadeTimer mTimer;
	
	
	public IndicatorLineView(Context context) {
		this(context, null);
	}
	
	public IndicatorLineView(Context context, AttributeSet attrs) {
		this(context, attrs, 0);
	}
	
	public IndicatorLineView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		
		final TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.ViewPagerExtensions, defStyle, 0);
		
		mLineColor = a.getColor(R.styleable.ViewPagerExtensions_lineColor, mLineColor);
		
		mFadeOutTime = a.getInt(R.styleable.ViewPagerExtensions_fadeOutDelay, mFadeOutTime);
		
		mFadingDuration = a.getInt(R.styleable.ViewPagerExtensions_fadeOutDuration, mFadingDuration);
		
		a.recycle();
	}
	
	@Override
	protected void onSizeChanged(int w, int h, int oldw, int oldh) {
		if (mPager != null) {
			mLineWidth = w / mPager.getAdapter().getCount();
			mLineLeft = mLineWidth * mPager.getCurrentItem();
			invalidate();
			resetTimer();
		}
	}
	
	
	
	public void setViewPager(ViewPager pager) {
		this.mPager = pager;
		mPager.setOnPageChangeListener(this);
	}
	
	
	private Paint mLinePaint = new Paint();
	
	protected synchronized void onDraw(Canvas canvas) {
		
		super.onDraw(canvas);
		
		final Paint linePaint = mLinePaint;
		
		final int color = Color.argb(mAlpha, Color.red(mLineColor), Color.green(mLineColor), Color.blue(mLineColor));
		
		linePaint.setColor(color);
		
		// draw the line
		canvas.drawRect(mLineLeft, 0, mLineLeft + mLineWidth, getMeasuredHeight(), linePaint);
		
	}
	
	
	
	@Override
	public void onPageScrollStateChanged(int state) {}
	
	@Override
	public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
		
		final float currentX = mPager.getScrollX();
		final float fullX = (mPager.getWidth() + mPager.getPageMargin()) * (mPager.getAdapter().getCount());
		
		mLineLeft = getMeasuredWidth() * currentX / fullX;
		
		mLineWidth = getMeasuredWidth() / mPager.getAdapter().getCount();
		
		resetTimer();
		
		invalidate();
		
	}
	
	@Override
	public void onPageSelected(int position) {}
	
	
	
	public void setLineColor(int lineColor) {
		this.mLineColor = lineColor;
		invalidate();
	}
	
	public int getLineColor() {
		return this.mLineColor;
	}
	
	public void setFadeOutDelay(int milliseconds) {
		this.mFadeOutTime = milliseconds;
		invalidate();
	}
	
	public int getFadeOutDelay() {
		return this.mFadeOutTime;
	}
	
	public void setFadeOutDuration(int milliseconds) {
		this.mFadingDuration = milliseconds;
		invalidate();
	}
	
	public int getFadeOutDuration() {
		return this.mFadingDuration;
	}
	
	
	
	private void setAlpha(int alpha) {
		this.mAlpha = alpha;
		invalidate();
	}
	
	private void resetTimer() {
		
		if (mFadeOutTime > 0) {
			
			if (mTimer == null || mTimer.isRunning == false) {
				mTimer = new FadeTimer();
				mTimer.execute();
			} else {
				mTimer.reset();
			}
			
			mAlpha = 0xFF;
			
		}
		
	}
	
	private class FadeTimer extends AsyncTask<Void, Integer, Void> {
		
		private int elapsed = 0;
		private boolean isRunning = true;
		
		public void reset() {
			elapsed = 0;
		}
		
		@Override
		protected Void doInBackground(Void... args) {
			while (isRunning) {
				try {
					Thread.sleep(1);
					elapsed++;
					
					if (elapsed >= mFadeOutTime && elapsed < mFadeOutTime + mFadingDuration) {
						
						int x0 = mFadeOutTime;
						int x1 = mFadeOutTime + mFadingDuration;
						int x = elapsed;
						int y0 = 0xFF;
						int y1 = 0x00;
						
						int a = y0 + ((x - x0) * y1 - (x - x0) * y0) / (x1 - x0);
						publishProgress(a);
					}
					else if (elapsed >= mFadeOutTime + mFadingDuration) {
						isRunning = false;
					}
					
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
			return null;
		}
		
		protected void onProgressUpdate(Integer... alpha) {
			setAlpha(alpha[0]);
		}
		
		@Override
		protected void onPostExecute(Void result) {
			setAlpha(0x00);
		}
	}
	
}
