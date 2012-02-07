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
import android.graphics.Paint;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.widget.Button;

public class ViewPagerTabButton extends Button {
	
	@SuppressWarnings("unused")
	private static final String TAG = "com.astuetz.viewpager.extensions";
	
	private int mLineColor = 0xFF6F8FC7;
	private int mLineColorSelected = 0xFF6F8FC7;
	
	private int mLineHeight = 2;
	private int mLineHeightSelected = 6;
	
	public ViewPagerTabButton(Context context) {
		this(context, null);
	}
	
	public ViewPagerTabButton(Context context, AttributeSet attrs) {
		this(context, attrs, 0);
	}
	
	public ViewPagerTabButton(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		
		mLineHeight = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, mLineHeight, context.getResources().getDisplayMetrics());
		mLineHeightSelected = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, mLineHeightSelected, context.getResources().getDisplayMetrics());
		
		final TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.ViewPagerExtensions, defStyle, 0);
		
		mLineColor = a.getColor(R.styleable.ViewPagerExtensions_lineColor, mLineColor);
		mLineColorSelected = a.getColor(R.styleable.ViewPagerExtensions_lineColorSelected, mLineColorSelected);
		
		mLineHeight = a.getDimensionPixelSize(R.styleable.ViewPagerExtensions_lineHeight, mLineHeight);
		mLineHeightSelected = a.getDimensionPixelSize(R.styleable.ViewPagerExtensions_lineHeightSelected,
		    mLineHeightSelected);
		
		a.recycle();
		
	}
	
	
	private Paint mLinePaint = new Paint();
	
	protected synchronized void onDraw(Canvas canvas) {
		
		super.onDraw(canvas);
		
		final Paint linePaint = mLinePaint;
		
		linePaint.setColor(isSelected() ? mLineColorSelected : mLineColor);
		
		final int height = isSelected() ? mLineHeightSelected : mLineHeight;
		
		// draw the line
		canvas.drawRect(0, getMeasuredHeight() - height, getMeasuredWidth(), getMeasuredHeight(), linePaint);
		
	}
	
	
	public void setLineColorSelected(int color) {
		this.mLineColorSelected = color;
		invalidate();
	}
	
	public int getLineColorSelected() {
		return this.mLineColorSelected;
	}
	
	public void setLineColor(int color) {
		this.mLineColor = color;
		invalidate();
	}
	
	public int getLineColor() {
		return this.mLineColor;
	}
	
	public void setLineHeight(int height) {
		this.mLineHeight = height;
		invalidate();
	}
	
	public int getLineHeight() {
		return this.mLineHeight;
	}
	
	public void setLineHeightSelected(int height) {
		this.mLineHeightSelected = height;
		invalidate();
	}
	
	public int getLineHeightSelected() {
		return this.mLineHeightSelected;
	}
	
}
