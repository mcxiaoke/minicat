package com.fanfou.app.ui.widget;

import android.content.Context;
import android.text.Editable;
import android.util.AttributeSet;
import android.widget.MultiAutoCompleteTextView;

/**
 * @author mcxiaoke
 * @version 1.0 2011.10.24
 *
 */
public class MyAutoCompleteTextView extends MultiAutoCompleteTextView {

	private Tokenizer mTokenizer;

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
		mTokenizer = t;
	}

	@Override
	public boolean enoughToFilter() {
		Editable text = getText();

		int end = getSelectionEnd();
		if (end < 0 || mTokenizer == null) {
			return false;
		}

		int start = mTokenizer.findTokenStart(text, end);

		if (end - start >= getThreshold() && text.toString().startsWith("@")) {
			return true;
		} else {
			return false;
		}
	}

}
