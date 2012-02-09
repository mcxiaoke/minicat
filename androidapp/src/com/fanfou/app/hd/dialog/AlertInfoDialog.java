package com.fanfou.app.hd.dialog;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.text.TextPaint;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;

import com.fanfou.app.hd.R;

/**
 * @author mcxiaoke
 * @version 1.0 2011.10.26
 * 
 */
public class AlertInfoDialog extends Dialog implements View.OnClickListener {

	private Context mContext;

	private TextView mTitleView;
	private TextView mTextView;
	private Button mButtonOk;

	private CharSequence mTitle;
	private CharSequence mText;

	private OnOKClickListener mClickListener;

	public AlertInfoDialog(Context context, String title, String text) {
		super(context, R.style.Dialog);
		this.mContext = context;
		this.mTitle = title;
		this.mText = text;
	}

	private void init() {
		setContentView(R.layout.dialog_alert);

		mTitleView = (TextView) findViewById(R.id.title);
		TextPaint tp = mTitleView.getPaint();
		tp.setFakeBoldText(true);
		mTitleView.setText(mTitle);

		mTextView = (TextView) findViewById(R.id.text);
		mTextView.setText(mText);

		mButtonOk = (Button) findViewById(R.id.button_ok);
		mButtonOk.setOnClickListener(this);

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

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setBlurEffect();
		init();
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

	public void setMessage(int resId) {
		mText = mContext.getResources().getText(resId);
		mTextView.setText(mText);
	}

	public void setOnClickListener(OnOKClickListener clickListener) {
		this.mClickListener = clickListener;
	}

	@Override
	public void onClick(View v) {
		int id = v.getId();
		switch (id) {
		case R.id.button_ok:
			cancel();
			if (mClickListener != null) {
				mClickListener.onOKClick();
			}
			break;
		default:
			break;
		}
	}

	public static interface OnOKClickListener {
		public void onOKClick();
	}

}
