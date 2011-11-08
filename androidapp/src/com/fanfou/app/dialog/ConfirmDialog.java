package com.fanfou.app.dialog;

import com.fanfou.app.R;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.text.TextPaint;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;

/**
 * @author mcxiaoke
 * @version 1.0 2011.10.25
 * @version 2.0 2011.10.27
 * 
 */
public class ConfirmDialog extends Dialog implements View.OnClickListener {

	private Context mContext;

	private TextView mTitleView;
	private TextView mTextView;
	private Button mButton1;
	private Button mButton2;

	private CharSequence mTitle;
	private CharSequence mText;

	private ClickHandler mClickListener;

	public ConfirmDialog(Context context, String title, String text) {
		super(context, R.style.Dialog);
		this.mContext = context;
		this.mTitle = title;
		this.mText = text;

		init();
	}

	protected void setBlurEffect() {
		Window window = getWindow();
		WindowManager.LayoutParams lp = window.getAttributes();
		// lp.alpha=0.8f;
		lp.dimAmount = 0.6f;
		window.setAttributes(lp);
		window.addFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);
		// window.addFlags(WindowManager.LayoutParams.FLAG_BLUR_BEHIND);
	}

	private void init() {
		setContentView(R.layout.dialog_confirm);

		mTitleView = (TextView) findViewById(R.id.title);
		TextPaint tp = mTitleView.getPaint();
		tp.setFakeBoldText(true);
		mTitleView.setText(mTitle);

		mTextView = (TextView) findViewById(R.id.text);
		mTextView.setText(mText);

		mButton1 = (Button) findViewById(R.id.button1);
		mButton1.setOnClickListener(this);

		mButton2 = (Button) findViewById(R.id.button2);
		mButton2.setOnClickListener(this);

	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setBlurEffect();

	}

	@Override
	public void setTitle(CharSequence title) {
		mTitle = title;
		mTitleView.setText(mTitle);
	}

	@Override
	public void setTitle(int resId) {
		mTitle = mContext.getResources().getText(resId);
		mTitleView.setText(mTitle);
	}

	public void setMessage(CharSequence message) {
		mText = message;
		mTextView.setText(mText);
	}

	public void setButton1Text(CharSequence text) {
		mButton1.setText(text);
	}

	public void setButton2Text(CharSequence text) {
		mButton2.setText(text);
	}

	public void setMessage(int resId) {
		mText = mContext.getResources().getText(resId);
		mTextView.setText(mText);
	}

	public void setClickListener(ClickHandler clickListener) {
		this.mClickListener = clickListener;
	}

	@Override
	public void onClick(View v) {
		int id = v.getId();
		switch (id) {
		case R.id.button1:
			if (mClickListener != null) {
				mClickListener.onButton1Click();
			}
			cancel();
			break;
		case R.id.button2:
			if (mClickListener != null) {
				mClickListener.onButton2Click();
			}
			cancel();
			break;
		default:
			break;
		}
	}

	public static interface ClickHandler {
		public void onButton1Click();

		public void onButton2Click();
	}

	public abstract static class AbstractClickHandler implements ClickHandler {

		@Override
		public void onButton1Click() {
		}

		@Override
		public void onButton2Click() {
		}

	}

}
