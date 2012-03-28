package com.fanfou.app.hd.ui.widget;

import com.fanfou.app.hd.R;
import com.fanfou.app.hd.util.Utils;

import android.content.Context;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

/**
 * @author mcxiaoke
 * @version 1.0 2012.03.28
 * 
 */
public class StatusView extends RelativeLayout {
	private ImageView mImageView;
	private TextView mTitleTextView;
	private TextView mContentTextView;
	private TextView mMetaTextView;
	private LinearLayout mIconsLayout;

	private ImageView mIconFavorite;
	private ImageView mIconThread;
	private ImageView mIconPhoto;

	private Context mContext;
	private LayoutInflater mInflater;

	private float mTitleTextSize;
	private float mContentTextSize;
	private float mMetaTextSize;

	private int mTitleTextColor;
	private int mContentTextColor;
	private int mMetaTextColor;

	private boolean mTitleTextBold;
	private boolean mShowImage;

	public StatusView(Context context) {
		this(context, null);
	}

	public StatusView(Context context, AttributeSet attrs) {
		this(context, attrs, R.attr.statusViewStyle);
	}

	public StatusView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		initialize(context, attrs, defStyle);
	}

	private void initialize(Context context, AttributeSet attrs, int defStyle) {
		mContext = context;
		mInflater = LayoutInflater.from(mContext);
		mInflater.inflate(R.layout.custom_status_view, this, true);
		mImageView = (ImageView) findViewById(R.id.image);
		mTitleTextView = (TextView) findViewById(R.id.title);
		mContentTextView = (TextView) findViewById(R.id.text);
		mMetaTextView = (TextView) findViewById(R.id.meta);
		mIconsLayout = (LinearLayout) findViewById(R.id.icons);
		mIconFavorite = (ImageView) findViewById(R.id.ic_favorite);
		mIconThread = (ImageView) findViewById(R.id.ic_thread);
		mIconPhoto = (ImageView) findViewById(R.id.ic_photo);

		final Resources res = getResources();
		final float defaultTitleTextSize = res
				.getDimension(R.dimen.status_view_default_title_text_size);
		final float defaultContentTextSize = res
				.getDimension(R.dimen.status_view_default_content_text_size);
		final float defaultMetaTextSize = res
				.getDimension(R.dimen.status_view_default_meta_text_size);
		final int defaultTitleTextColor = res
				.getColor(R.color.status_view_default_title_text_color);
		final int defaultContentTextColor = res
				.getColor(R.color.status_view_default_content_text_color);
		final int defaultMetaTextColor = res
				.getColor(R.color.status_view_default_meta_text_color);

		final boolean defaultTitleTextBold = res
				.getBoolean(R.bool.status_view_default_title_text_bold);
		final boolean defaultShowImage = res
				.getBoolean(R.bool.status_view_default_show_head_image);

		TypedArray a = context.obtainStyledAttributes(attrs,
				R.styleable.StatusView, defStyle, 0);
		mTitleTextSize = a.getDimension(R.styleable.StatusView_titleTextSize,
				defaultTitleTextSize);
		mTitleTextColor = a.getColor(R.styleable.StatusView_titleTextColor,
				defaultTitleTextColor);
		mTitleTextBold = a.getBoolean(R.styleable.StatusView_titleTextBold,
				defaultTitleTextBold);

		mContentTextSize = a.getDimension(
				R.styleable.StatusView_contentTextSize, defaultContentTextSize);
		mContentTextColor = a.getColor(R.styleable.StatusView_contentTextColor,
				defaultContentTextColor);

		mMetaTextSize = a.getDimension(R.styleable.StatusView_metaTextSize,
				defaultMetaTextSize);
		mMetaTextColor = a.getColor(R.styleable.StatusView_metaTextColor,
				defaultMetaTextColor);

		mShowImage = a.getBoolean(R.styleable.StatusView_showImage,
				defaultShowImage);

		mTitleTextView.setTextSize(mTitleTextSize);
		mTitleTextView.setTextColor(mTitleTextColor);
		if (mTitleTextBold) {
			Utils.setBoldText(mTitleTextView);
		}

		mContentTextView.setTextSize(mContentTextSize);
		mContentTextView.setTextColor(mContentTextColor);
		mMetaTextView.setTextSize(mMetaTextSize);
		mMetaTextView.setTextColor(mMetaTextColor);

		mImageView.setVisibility(mShowImage ? View.VISIBLE : View.GONE);

		a.recycle();
		a = null;
	}

	public void setTitle(CharSequence text) {
		mTitleTextView.setText(text);
	}

	public void setContent(CharSequence text) {
		mContentTextView.setText(text);
	}

	public void setMeta(CharSequence text) {
		mMetaTextView.setText(text);
	}

	public void setImage(Bitmap bitmap) {
		mImageView.setImageBitmap(bitmap);
	}

	public void setImage(Drawable drawable) {
		mImageView.setImageDrawable(drawable);
	}

	public void setImage(int resId) {
		mImageView.setImageResource(resId);
	}

	public void showImage(boolean show) {
		mImageView.setVisibility(show ? View.VISIBLE : View.GONE);
	}

	public void showIconFavorite(boolean show) {
		mIconFavorite.setVisibility(show ? View.VISIBLE : View.GONE);
	}

	public void showIconThread(boolean show) {
		mIconThread.setVisibility(show ? View.VISIBLE : View.GONE);
	}

	public void showIconPhoto(boolean show) {
		mIconPhoto.setVisibility(show ? View.VISIBLE : View.GONE);
	}

	public void setTitleTextSize(float size) {
		mTitleTextView.setTextSize(size);
	}

	public void setContentTextSize(float size) {
		mContentTextView.setTextSize(size);
	}

	public void setMetaTextSize(float size) {
		mMetaTextView.setTextSize(size);
	}

	public void setTitleTextColor(int color) {
		mTitleTextView.setTextColor(color);
	}

	public void setContentTextColor(int color) {
		mContentTextView.setTextColor(color);
	}

	public void setMetaTextColor(int color) {
		mMetaTextView.setTextColor(color);
	}

}
