package com.fanfou.app.ui.widget;

import com.fanfou.app.App;

import android.content.Context;
import android.text.Editable;
import android.util.AttributeSet;
import android.util.Log;
import android.widget.MultiAutoCompleteTextView;

/**
 * @author mcxiaoke
 * @version 1.0 20110829
 * 
 */
public class NameAutoCompleteTextView extends MultiAutoCompleteTextView {
	private static final String TAG = NameAutoCompleteTextView.class
			.getSimpleName();

	public NameAutoCompleteTextView(Context context) {
		super(context);
	}

	public NameAutoCompleteTextView(Context context, AttributeSet attrs,
			int defStyle) {
		super(context, attrs, defStyle);
	}

	public NameAutoCompleteTextView(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	private Tokenizer mTokenizer;

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

		int len = end - start;

		if (len > 0 && text.charAt(start) != '@') {
			return false;
		}

		if (len >= getThreshold()) {
			return true;
		} else {
			return false;
		}
	}

}
