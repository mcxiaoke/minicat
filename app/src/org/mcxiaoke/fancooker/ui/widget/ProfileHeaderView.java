package org.mcxiaoke.fancooker.ui.widget;

import org.mcxiaoke.fancooker.R;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

public class ProfileHeaderView extends LinearLayout implements
		View.OnClickListener {

	public interface OnCountClickListener {
		public void onCountClick(int position);
	}

	private static final LinearLayout.LayoutParams LAYOUT_PARAMS = new LinearLayout.LayoutParams(
			LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);

	private ViewGroup mInfoViewGroup;
	private ImageView mIconImageView;
	private TextView mNameTextView;
	private TextView mStateTextView;
	private ImageView mLockImageView;
	private TextView mIntroTextView;
	private TextView mExpandTextView;

	private ViewGroup mItemViewGroup;
	private ItemTextView mStatusCountView;
	private ItemTextView mFollowingCountView;
	private ItemTextView mFollowersCountView;

	private OnCountClickListener mOnCountClickListener;

	private boolean mExpand;

	public ProfileHeaderView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		initialize(context);
	}

	public ProfileHeaderView(Context context, AttributeSet attrs) {
		super(context, attrs);
		initialize(context);
	}

	public ProfileHeaderView(Context context) {
		super(context);
		initialize(context);
	}

	private void initialize(Context context) {
		setLayoutParams(LAYOUT_PARAMS);
		setOrientation(LinearLayout.VERTICAL);

		LayoutInflater.from(context).inflate(R.layout.profile_header, this);

		mInfoViewGroup = (ViewGroup) findViewById(R.id.info);
		mIconImageView = (ImageView) findViewById(R.id.icon);
		mNameTextView = (TextView) findViewById(R.id.name);
		mStateTextView = (TextView) findViewById(R.id.state);
		mLockImageView = (ImageView) findViewById(R.id.lock);
		mIntroTextView = (TextView) findViewById(R.id.intro);
		mExpandTextView = (TextView) findViewById(R.id.expand);

		mItemViewGroup = (ViewGroup) findViewById(R.id.items);
		mStatusCountView = (ItemTextView) findViewById(R.id.item_view_1);
		mFollowingCountView = (ItemTextView) findViewById(R.id.item_view_2);
		mFollowersCountView = (ItemTextView) findViewById(R.id.item_view_3);

		mStatusCountView.setOnClickListener(this);
		mFollowingCountView.setOnClickListener(this);
		mFollowersCountView.setOnClickListener(this);

		mIntroTextView.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				toggle();
			}
		});
	}

	private void toggle() {
		mExpand = !mExpand;
		mExpandTextView.setText(mExpand ? "收起" : "展开");
		mIntroTextView.setMaxLines(mExpand ? Integer.MAX_VALUE : 2);
	}

	public ImageView getIconImageView() {
		return mIconImageView;
	}

	public void setNameText(CharSequence text) {
		mNameTextView.setText(text);
	}

	public void setStateText(CharSequence text) {
		mStateTextView.setText(text);
	}

	public void setIntroText(CharSequence text) {
		mIntroTextView.setText(text);
	}

	public void setStatusCountText(String title, String text) {
		mStatusCountView.setContent(title, text);
	}

	public void setFollowingCountText(String title, String text) {
		mFollowingCountView.setContent(title, text);
	}

	public void setFollowersCountText(String title, String text) {
		mFollowersCountView.setContent(title, text);
	}

	public void showLockIcon(boolean show) {
		mLockImageView.setVisibility(show ? View.VISIBLE : View.INVISIBLE);
	}

	private void onCountClick(int position) {
		if (mOnCountClickListener != null) {
			mOnCountClickListener.onCountClick(position);
		}
	}

	public void setOnCountClickListener(OnCountClickListener li) {
		mOnCountClickListener = li;
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.item_view_1:
			onCountClick(0);
			break;
		case R.id.item_view_2:
			onCountClick(1);
			break;
		case R.id.item_view_3:
			onCountClick(2);
			break;
		default:
			break;
		}

	}

}
