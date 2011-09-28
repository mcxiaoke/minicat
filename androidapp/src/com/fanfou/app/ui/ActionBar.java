package com.fanfou.app.ui;

import com.fanfou.app.R;

import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.AnimationDrawable;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
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
	private ImageView mRightButton;// 右边的动作图标
	private ImageView mRefreshButton;// 右侧第二个图标，刷新
	private TextView mTitle;// 居中标题

	private OnRefreshClickListener mOnRefreshClickListener = null;

	// private boolean mRefreshable=false;

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

	public void setTitleClickListener(OnClickListener li) {
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

	public void hideLeftIcon() {
		mLeftButton.setVisibility(View.INVISIBLE);
	}

	public void hideRightIcon() {
		mRightButton.setVisibility(View.INVISIBLE);
	}

	private void setRefreshAction(Action action) {
		mRefreshButton.setImageResource(action.getDrawable());
		mRefreshButton.setTag(action);
	}

	public void setRefreshEnabled(OnRefreshClickListener onRefreshClickListener) {
		if (onRefreshClickListener != null) {
			mOnRefreshClickListener = onRefreshClickListener;
			setRefreshAction(new RefreshAction(this));
		}
	}

	private void onRefreshClick() {
		if (mOnRefreshClickListener != null) {
			mOnRefreshClickListener.onRefreshClick();
		}
	}

	public void startAnimation() {
		mRefreshButton.setOnClickListener(null);
		mRefreshButton.setImageDrawable(null);
		mRefreshButton.setBackgroundResource(R.drawable.animation_refresh);
		AnimationDrawable frameAnimation = (AnimationDrawable) mRefreshButton
				.getBackground();
		frameAnimation.start();
	}
	
	public void stopAnimation(){
		AnimationDrawable frameAnimation = (AnimationDrawable) mRefreshButton
		.getBackground();
		if(frameAnimation!=null){
			frameAnimation.stop();
			mRefreshButton.setBackgroundDrawable(null);
			mRefreshButton.setImageResource(R.drawable.i_refresh);
			mRefreshButton.setOnClickListener(this);
		}
	}

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
		this.mRefreshButton = (ImageView) findViewById(R.id.actionbar_refresh);
		this.mTitle = (TextView) mActionBar.findViewById(R.id.actionbar_title);

		mLeftButton.setOnClickListener(this);
		mRightButton.setOnClickListener(this);
		mRefreshButton.setOnClickListener(this);
	}

	public interface Action {
		public int getDrawable();

		public void performAction(View view);
	}

	public interface OnRefreshClickListener {
		public void onRefreshClick();
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

	public static class RefreshAction extends AbstractAction {
		private ActionBar ab;

		public RefreshAction(ActionBar ab) {
			super(R.drawable.i_refresh);
			this.ab = ab;
		}

		@Override
		public void performAction(View view) {
			ab.onRefreshClick();
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
