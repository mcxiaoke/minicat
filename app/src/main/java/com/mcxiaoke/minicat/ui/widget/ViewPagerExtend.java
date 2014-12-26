package com.mcxiaoke.minicat.ui.widget;

import android.content.Context;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.view.MotionEvent;

/**
 * Project: fanfouapp
 * Package: com.mcxiaoke.fanfouapp.ui.widget
 * User: mcxiaoke
 * Date: 13-7-20
 * Time: 上午10:56
 */
public class ViewPagerExtend extends ViewPager {

    public ViewPagerExtend(Context context) {
        super(context);
    }

    public ViewPagerExtend(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        try {
            return super.onInterceptTouchEvent(ev);
        } catch (Exception e) {
            return true;
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent ev) {
        try {
            return super.onTouchEvent(ev);
        } catch (Exception e) {
            return true;
        }
    }
}
