package com.fanfou.app.dialog;

import com.fanfou.app.R;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.text.TextPaint;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

/**
 * @author mcxiaoke
 * @version 1.0 2011.10.25
 * 
 */
public class ConfirmDialog extends Dialog implements
		View.OnClickListener {

	private Context mContext;

	private TextView mTitleView;
	private TextView mTextView;
	private Button mButtonOk;
	private Button mButtonCancel;
	
	private CharSequence mTitle;
	private CharSequence mText;

	private OnOKClickListener mClickListener;

	public ConfirmDialog(Context context, String title, String text) {
		super(context, R.style.Dialog);
		this.mContext = context;
		this.mTitle=title;
		this.mText=text;
	}
	
	private void init(){
		setContentView(R.layout.dialog);
		
		mTitleView = (TextView) findViewById(R.id.title);
		TextPaint tp=mTitleView.getPaint();
		tp.setFakeBoldText(true);
		mTitleView.setText(mTitle);
		
		mTextView = (TextView) findViewById(R.id.text);
		mTextView.setText(mText);
		
		mButtonOk = (Button) findViewById(R.id.button_ok);
		mButtonOk.setOnClickListener(this);
		
		mButtonCancel = (Button) findViewById(R.id.button_cancel);
		mButtonCancel.setOnClickListener(this);
		
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		init();
	}

	@Override
	public void setTitle(CharSequence title) {
		mTitle=title;
		mTitleView.setText(mTitle);
	}

	@Override
	public void setTitle(int resId) {
		mTitle=mContext.getResources().getText(resId);
		mTitleView.setText(mTitle);
	}

	public void setMessage(CharSequence message) {
		mText=message;
		mTextView.setText(mText);
	}

	public void setMessage(int resId) {
		mText=mContext.getResources().getText(resId);
		mTextView.setText(mText);
	}

	public void setOnClickListener(OnOKClickListener clickListener) {
		this.mClickListener = clickListener;
	}

	@Override
	public void onClick(View v) {
		int id=v.getId();
		switch (id) {
		case R.id.button_ok:
			cancel();
			if(mClickListener!=null){
				mClickListener.onOKClick();
			}
			break;
		case R.id.button_cancel:
			cancel();
			break;
		default:
			break;
		}
	}
	
	
	public static interface OnOKClickListener{
		public void onOKClick();
	}
	
}
