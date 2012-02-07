package com.fanfou.app.ui.viewpager;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.text.TextUtils.TruncateAt;
import android.util.AttributeSet;
import android.widget.TextView;


public class ViewPagerTab extends TextView {
	
	@SuppressWarnings("unused")
	private static final String TAG = "ViewPagerTabs";
	
	private int mTextColorNormal = 0xFF999999;
	private int mTextColorCenter = 0xFF91A438;
	
	private int mLineColorNormal = 0xFF3B3B3B;
	private int mLineColorCenter = 0xFF91A438;
	
	private int mBackgroundColorPressed = 0x9943797F;
	
	private int mLineHeight = 4;
	
	
	
	private int mCenterPercent = 0;
	
	private int mIndex = -1;
	
	public int currentPos;
	public int prevPos;
	public int nextPos;
	
	public int layoutPos;
	
	
	public ViewPagerTab(Context context) {
		this(context, null);
	}
	
	public ViewPagerTab(Context context, AttributeSet attrs) {
		this(context, attrs, 0);
	}
	
	public ViewPagerTab(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		
		this.currentPos = 0;
		this.prevPos = 0;
		this.nextPos = 0;
		this.layoutPos = 0;
		
		this.setSingleLine(true);
		this.setEllipsize(TruncateAt.END);
		
		this.setPadding(0, 0, 0, 0);
		
		this.setFocusable(true);
		
	}
	
	public void setIndex(int i) {
		mIndex = i;
	}
	
	public int getIndex() {
		return mIndex;
	}
	
	
	
	public void setTextColors(int textColorNormal, int textColorCenter) {
		this.mTextColorNormal = textColorNormal;
		this.mTextColorCenter = textColorCenter;
	}
	
	public void setLineColors(int lineColorNormal, int lineColorCenter) {
		this.mLineColorNormal = lineColorNormal;
		this.mLineColorCenter = lineColorCenter;
	}
	
	public void setLineHeight(int lineHeight) {
		this.mLineHeight = lineHeight;
	}
	
	public void setBackgroundColorPressed(int backgroundColorPressed) {
		this.mBackgroundColorPressed = backgroundColorPressed;
	}
	
	
	
	@Override
	protected void drawableStateChanged() {
		invalidate();
		super.drawableStateChanged();
	}
	
	
	/**
	 * Sets how close is the current tab to the center
	 * 
	 * @param percent
	 *          0% is away from center, 100% is exactly at center
	 */
	public void setCenterPercent(int percent) {
		if (percent < 0) percent = 0;
		if (percent > 100) percent = 100;
		mCenterPercent = percent;
	}
	
	
	
	private Paint mLinePaint = new Paint();
	private Paint mSelectedPaint = new Paint();
	
	protected synchronized void onDraw(Canvas canvas) {
		
		final Paint linePaint = mLinePaint;
		final Paint selectedPaint = mSelectedPaint;
		
		selectedPaint.setColor(mBackgroundColorPressed);
		
		// interpolate text color
		final int textColors[] = new int[] { mTextColorNormal, mTextColorCenter };
		setTextColor(interpColor(textColors, mCenterPercent / 100.0f));
		
		// interpolate line color
		final int lineColors[] = new int[] { mLineColorNormal, mLineColorCenter };
		linePaint.setColor(interpColor(lineColors, mCenterPercent / 100.0f));
		
		// draw the line
		canvas.drawRect(0, getHeight() - mLineHeight, getWidth(), getHeight(), linePaint);
		
		// draw background
		if (this.isFocused() || this.isPressed()) canvas.drawRect(0, 0, getWidth(), getHeight(), selectedPaint);
		
		super.onDraw(canvas);
		
	}
	
	
	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		
		sb.append(this.getText()).append(": ");
		sb.append(prevPos);
		sb.append(" <- ").append(currentPos);
		sb.append(" -> ").append(nextPos);
		sb.append(" (").append(layoutPos).append(")");
		
		return sb.toString();
	}
	
	
	/**
	 * Used for interpolating between some colors
	 */
	private int interpColor(int colors[], float unit)
	{
		if (unit <= 0)
		{
			return colors[0];
		}
		if (unit >= 1)
		{
			return colors[colors.length - 1];
		}
		
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
	
}
