package com.fanfou.app.ui;

import com.fanfou.app.R;

import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

public class ActionBar extends RelativeLayout implements OnClickListener {

	public static final int TYPE_HOME = 0; // 左侧LOGO，中间标题文字，右侧编辑图标
	public static final int TYPE_NORMAL = 1; // 左侧LOGO，中间标题文字，右侧编辑图标
	public static final int TYPE_EDIT = 2; // 左侧LOGO，中间标题文字，右侧发送图标

	private Context mContext;
	private LayoutInflater mInflater;
	private RelativeLayout mActionBar;// 标题栏
	private ImageView mLeftButton;// 饭否标志
//	private ProgressBar mProgressView;// 最右边的动作图标
	private ImageView mRightButton;// 次右边的动作图标
	private TextView mTitle;// 居中标题

	public ActionBar(Context context) {
		super(context);
		initViews(context);
	}

	public ActionBar(Context context, AttributeSet attrs) {
		super(context, attrs);
		initViews(context);
	}

	public void setTitle(CharSequence text) {
		mTitle.setText(text);
	}

	public void setTitle(int resId) {
		mTitle.setText(resId);
	}
	
	public void setTitleClickListener(OnClickListener li){
		mTitle.setOnClickListener(li);
	}

	public void setLeftIcon(int resId) {
		mLeftButton.setImageResource(resId);
	}

	public void setRightIcon(int resId) {
		mRightButton.setImageResource(resId);
	}

	public void setLeftAction(Action action) {
		mLeftButton.setImageResource(action.getDrawable());
		mLeftButton.setTag(action);
	}

	public void setRightAction(Action action) {
		mRightButton.setImageResource(action.getDrawable());
		mRightButton.setTag(action);
	}
	
	public void hideLeftIcon(){
		mLeftButton.setVisibility(View.INVISIBLE);
	}
	
	public void hideRightIcon(){
		mRightButton.setVisibility(View.INVISIBLE);
	}

//	public void showProgress() {
//		mProgressView.setVisibility(View.VISIBLE);
//	}
//
//	public void hideProgress() {
//		mProgressView.setVisibility(View.GONE);
//	}

	@Override
	public void onClick(View view) {
		final Object tag = view.getTag();
		if (tag instanceof Action) {
			final Action action = (Action) tag;
			action.performAction(view);
		}
	}

	public void setType() {

	}

	private void initViews(Context context) {
		this.mContext = context;
		this.mInflater = LayoutInflater.from(mContext);

		this.mActionBar = (RelativeLayout) mInflater.inflate(
				R.layout.actionbar, null);
		addView(mActionBar);
		this.mLeftButton = (ImageView) mActionBar
				.findViewById(R.id.actionbar_left);
		this.mRightButton = (ImageView) mActionBar
				.findViewById(R.id.actionbar_right);
//		this.mProgressView = (ProgressBar) mActionBar
//				.findViewById(R.id.actionbar_progress);
		this.mTitle = (TextView) mActionBar.findViewById(R.id.actionbar_title);

		mLeftButton.setOnClickListener(this);
		mRightButton.setOnClickListener(this);
//		hideProgress();
	}

	public interface Action {
		public int getDrawable();

		public void performAction(View view);
	}

	public static abstract class AbstractAction implements Action {
		final private int mDrawable;

		public AbstractAction(int drawable) {
			mDrawable = drawable;
		}

		@Override
		public int getDrawable() {
			return mDrawable;
		}
	}

	public static class BackAction extends AbstractAction {
		private Activity context;

		public BackAction(Activity mContext) {
			super(R.drawable.i_back);
			this.context = mContext;
		}

		@Override
		public void performAction(View view) {
			context.finish();
		}

	}

	public static class IntentAction extends AbstractAction {
		private Context mContext;
		private Intent mIntent;

		public IntentAction(Context context, Intent intent, int drawable) {
			super(drawable);
			mContext = context;
			mIntent = intent;
		}

		@Override
		public void performAction(View view) {
			try {
				mContext.startActivity(mIntent);
			} catch (ActivityNotFoundException e) {
				Toast.makeText(
						mContext,
						mContext.getText(R.string.actionbar_activity_not_found),
						Toast.LENGTH_SHORT).show();
			}
		}
	}

	public static class ToastAction extends AbstractAction {
		private Context mContext;
		private String mText;

		public ToastAction(Context context, String text, int drawable) {
			super(drawable);
			this.mContext = context;
			this.mText = text;
		}

		@Override
		public void performAction(View view) {
			Toast.makeText(mContext, mText, Toast.LENGTH_SHORT).show();
		}

	}

	public static class WriteAction extends AbstractAction {

		public WriteAction(int drawable) {
			super(drawable);
		}

		@Override
		public void performAction(View view) {
		}

	}

}
