package com.mcxiaoke.fanfouapp.ui.widget;

import android.content.Context;
import android.content.res.Resources;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.LinearLayout;
import com.mcxiaoke.fanfouapp.R;

/**
 * Project: fanfouapp
 * Package: com.mcxiaoke.fanfouapp.ui.widget
 * User: mcxiaoke
 * Date: 13-5-18
 * Time: 下午2:10
 */
public class HorizontalTextGroupView extends LinearLayout {
    private Context mContext;
    private Resources mRes;
    private LayoutInflater mInflater;

    public HorizontalTextGroupView(Context context) {
        super(context);
    }

    public HorizontalTextGroupView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public HorizontalTextGroupView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    private void initialize(Context context) {
        this.mContext = context;
        this.mRes = getResources();
        this.mInflater = LayoutInflater.from(mContext);

        this.mInflater.inflate(R.layout.view_horizontal_text_group, this);


    }
}
