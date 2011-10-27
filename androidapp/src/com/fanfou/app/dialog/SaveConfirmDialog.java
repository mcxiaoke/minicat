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
 * @version 1.0 2011.10.27
 * 
 */
public class SaveConfirmDialog extends Dialog implements View.OnClickListener {

	private Context mContext;

	private TextView mTitleView;
	private TextView mTextView;
	private Button mButtonSave;
	private Button mButtonDisCard;
	private Button mButtonCancel;

	private CharSequence mTitle;
	private CharSequence mText;

	private OnButtonClickListener mClickListener;

	public SaveConfirmDialog(Context context, String title, String text) {
		super(context, R.style.Dialog);
		this.mContext = context;
		this.mTitle = title;
		this.mText = text;
	}

	protected void setBlurEffect() {
		Window window = getWindow();
		WindowManager.LayoutParams lp = window.getAttributes();
//		 lp.alpha=0.8f;
		lp.dimAmount = 0.6f;
		window.setAttributes(lp);
		window.addFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);
//		window.addFlags(WindowManager.LayoutParams.FLAG_BLUR_BEHIND);
	}

	private void init() {
		setContentView(R.layout.dialog_save_confirm);

		mTitleView = (TextView) findViewById(R.id.title);
		TextPaint tp = mTitleView.getPaint();
		tp.setFakeBoldText(true);
		mTitleView.setText(mTitle);

		mTextView = (TextView) findViewById(R.id.text);
		mTextView.setText(mText);

		mButtonSave = (Button) findViewById(R.id.button_save);
		mButtonSave.setOnClickListener(this);
		
		mButtonDisCard=(Button) findViewById(R.id.button_discard);
		mButtonDisCard.setOnClickListener(this);

		mButtonCancel = (Button) findViewById(R.id.button_cancel);
		mButtonCancel.setOnClickListener(this);

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

	public void setOnClickListener(OnButtonClickListener clickListener) {
		this.mClickListener = clickListener;
	}

	@Override
	public void onClick(View v) {
		int id = v.getId();
		switch (id) {
		case R.id.button_save:
			if (mClickListener != null) {
				mClickListener.onSaveClick();
			}
			cancel();
			break;
		case R.id.button_discard:
			if (mClickListener != null) {
				mClickListener.onDiscardClick();
			}
			cancel();
			break;
		case R.id.button_cancel:
			cancel();
			break;
		default:
			break;
		}
	}

	public static interface OnButtonClickListener {
		public void onSaveClick();
		public void onDiscardClick();
	}

}
