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
import android.util.AttributeSet;
import android.util.TypedValue;
import android.widget.Button;


public class SwipeyTabButton extends Button implements SwipeyTab {
	
	@SuppressWarnings("unused")
	private static final String TAG = "com.astuetz.viewpager.extensions";
	
	@SuppressWarnings("unused")
	private Context mContext;
	
	private int mTextColorNormal = 0x00000000;
	private int mTextColorCenter = 0xFF96AA39;
	
	private int mLineColorNormal = 0x00000000;
	private int mLineColorCenter = 0xFF96AA39;
	
	private int mLineHeightSelected = 3;
	
	private int mCenterPercent = 0;
	
	
	public SwipeyTabButton(Context context) {
		this(context, null);
	}
	
	public SwipeyTabButton(Context context, AttributeSet attrs) {
		this(context, attrs, 0);
	}
	
	public SwipeyTabButton(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		
		mLineHeightSelected = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, mLineHeightSelected, context.getResources().getDisplayMetrics());
		
		mContext = context;
		
		final TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.ViewPagerExtensions, defStyle, 0);
		
		mTextColorCenter = a.getColor(R.styleable.ViewPagerExtensions_textColorSelected, mTextColorCenter);
		mLineColorCenter = a.getColor(R.styleable.ViewPagerExtensions_lineColorSelected, mLineColorCenter);
		mLineHeightSelected = a.getDimensionPixelSize(R.styleable.ViewPagerExtensions_lineHeightSelected, mLineHeightSelected);
		
		a.recycle();
		
		mTextColorNormal = this.getTextColors().getDefaultColor();
		
		this.setSingleLine(true);
	}
	
	
	
	public void setTextColorCenter(int textColorCenter) {
		this.mTextColorCenter = textColorCenter;
		invalidate();
	}
	
	public void setLineColorCenter(int lineColorCenter) {
		this.mLineColorCenter = lineColorCenter;
		invalidate();
	}
	
	public void setLineHeight(int lineHeight) {
		this.mLineHeightSelected = lineHeight;
		invalidate();
	}
	
	
	private Paint mLinePaint = new Paint();
	
	protected synchronized void onDraw(Canvas canvas) {
		
		final Paint linePaint = mLinePaint;
		
		// interpolate text color
		final int textColors[] = new int[] {
		    mTextColorNormal, mTextColorCenter
		};
		setTextColor(interpColor(textColors, mCenterPercent / 100.0f));
		
		// interpolate line color
		final int lineColors[] = new int[] {
		    mLineColorNormal, mLineColorCenter
		};
		linePaint.setColor(interpColor(lineColors, mCenterPercent / 100.0f));
		
		// draw the line
		canvas.drawRect(0, getHeight() - mLineHeightSelected, getWidth(), getHeight(), linePaint);
		
		super.onDraw(canvas);
		
	}
	
	/**
	 * Interpolate between some colors
	 */
	private int interpColor(int colors[], float unit)
	{
		if (unit <= 0) { return colors[0]; }
		if (unit >= 1) { return colors[colors.length - 1]; }
		
		float p = unit * (colors.length - 1);
		int i = (int) p;
		p -= i;
		
		// now p is just the fractional part [0...1) and i is the index
		int c0 = colors[i];
		int c1 = colors[i + 1];
		int a = ave(Color.alpha(c0), Color.alpha(c1), p);
		int r = ave(Color.red(c0), Color.red(c1), p);
		int g = ave(Color.green(c0), Color.green(c1), p);
		int b = ave(Color.blue(c0), Color.blue(c1), p);
		
		return Color.argb(a, r, g, b);
	}
	
	private int ave(int s, int d, float p)
	{
		return s + java.lang.Math.round(p * (d - s));
	}
	
	
	@Override
	public void setHighlightPercentage(int percent) {
		if (percent < 0) percent = 0;
		if (percent > 100) percent = 100;
		mCenterPercent = percent;
		
		invalidate();
	}
	
}
