package com.mcxiaoke.minicat.ui.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.mcxiaoke.minicat.R;

public class ItemTextView extends LinearLayout {
    private static final LinearLayout.LayoutParams LAYOUT_PARAMS = new LinearLayout.LayoutParams(
            LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);

    private static final int PADDING_TOP_DP = 5;
    private static final int PADDING_LEFT_DP = 10;

    private TextView mTitleTextView;
    private TextView mTextView;

    public ItemTextView(Context context) {
        this(context, null, 0);
    }

    public ItemTextView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public ItemTextView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);

        LayoutInflater.from(getContext())
                .inflate(R.layout.view_item_text, this);
        setLayoutParams(LAYOUT_PARAMS);
        setOrientation(LinearLayout.VERTICAL);
        setGravity(Gravity.CENTER_HORIZONTAL);
        setClickable(true);

        DisplayMetrics metrics = getResources().getDisplayMetrics();
        final int paddingTop = (int) TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_DIP, PADDING_TOP_DP, metrics);
        final int paddingLeft = (int) TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_DIP, PADDING_LEFT_DP, metrics);
        setPadding(paddingLeft, paddingTop, paddingLeft, paddingTop);

        mTitleTextView = (TextView) findViewById(R.id.title);
        mTextView = (TextView) findViewById(R.id.text);

    }

    public void setContent(String title, String text) {
        mTitleTextView.setText(title);
        mTextView.setText(text);
    }

}
