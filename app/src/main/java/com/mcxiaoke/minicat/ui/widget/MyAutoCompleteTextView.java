package com.mcxiaoke.minicat.ui.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.MultiAutoCompleteTextView;

/**
 * @author mcxiaoke
 * @version 1.1 2011.11.22
 */
public class MyAutoCompleteTextView extends MultiAutoCompleteTextView {
    private static final String TAG = MyAutoCompleteTextView.class
            .getSimpleName();

    public MyAutoCompleteTextView(Context context) {
        super(context);
    }

    public MyAutoCompleteTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public MyAutoCompleteTextView(Context context, AttributeSet attrs,
                                  int defStyle) {
        super(context, attrs, defStyle);
    }

    @Override
    public void setTokenizer(Tokenizer t) {
        super.setTokenizer(t);
    }

    @Override
    public boolean enoughToFilter() {
        return super.enoughToFilter();
    }

}
