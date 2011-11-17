package com.fanfou.app.ui;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.TextView;

public class LinkedTextView extends TextView {

	@Override
	public void setText(CharSequence text, BufferType type) {
		// TODO Auto-generated method stub
		super.setText(text, type);
	}

	public LinkedTextView(Context context) {
		super(context);
	}
	
	public LinkedTextView(Context context, AttributeSet attrs) {
		super(context, attrs);
	}
	
	public LinkedTextView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
	}

}
