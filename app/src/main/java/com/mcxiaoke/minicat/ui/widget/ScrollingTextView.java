package com.mcxiaoke.minicat.ui.widget;

import android.content.Context;
import android.graphics.Rect;
import android.text.TextUtils.TruncateAt;
import android.util.AttributeSet;
import android.view.Gravity;
import android.widget.TextView;

/**
 * @author mcxiaoke
 * @version 1.0 2011.10.28
 */
public class ScrollingTextView extends TextView {

    public ScrollingTextView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init();
    }

    public ScrollingTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public ScrollingTextView(Context context) {
        super(context);
        init();
    }

    protected void init() {
        setEllipsize(TruncateAt.MARQUEE);
        setGravity(Gravity.CENTER);
        setSingleLine();
    }

    @Override
    protected void onFocusChanged(boolean focused, int direction,
                                  Rect previouslyFocusedRect) {
        if (focused) {
            super.onFocusChanged(focused, direction, previouslyFocusedRect);
        }
    }

    @Override
    public void onWindowFocusChanged(boolean focused) {
        if (focused) {
            super.onWindowFocusChanged(focused);
        }
    }

    @Override
    public boolean isFocused() {
        return true;
    }
}
