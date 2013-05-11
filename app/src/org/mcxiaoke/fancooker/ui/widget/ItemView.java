package org.mcxiaoke.fancooker.ui.widget;

import org.mcxiaoke.fancooker.AppContext;
import org.mcxiaoke.fancooker.R;
import org.mcxiaoke.fancooker.util.Utils;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewStub;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

/**
 * @author mcxiaoke
 * @version 1.0 2012.03.28
 * @version 1.1 2012.03.29
 * 
 */
public class ItemView extends RelativeLayout {
	private ImageView mImageView;
	private TextView mTitleTextView;
	private TextView mContentTextView;
	private TextView mMetaTextView;

	private ViewStub mViewStub;
	private View mIconsView;

	private ImageView mIconFavorite;
	private ImageView mIconThread;
	private ImageView mIconPhoto;
	private ImageView mIconLock;

	private Context mContext;
	private LayoutInflater mInflater;

	private ViewMode mViewMode;

	private int mPadding;

	public ItemView(Context context) {
		this(context, null);
	}

	public ItemView(Context context, AttributeSet attrs) {
		this(context, attrs, 0);
	}

	public ItemView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		initialize(context, attrs, defStyle);
	}

	private void initialize(Context context, AttributeSet attrs, int defStyle) {
		mContext = context;
		mInflater = LayoutInflater.from(mContext);
		mInflater.inflate(R.layout.item_view, this, true);
		mImageView = (ImageView) findViewById(R.id.image);
		mTitleTextView = (TextView) findViewById(R.id.title);
		mContentTextView = (TextView) findViewById(R.id.text);
		mMetaTextView = (TextView) findViewById(R.id.meta);

		mViewStub = (ViewStub) findViewById(R.id.stub);

		final Resources res = getResources();
		mPadding = res.getDimensionPixelSize(R.dimen.list_item_padding);
		mViewMode = ViewMode.StatusMode;

		setPadding(mPadding, mPadding, mPadding, mPadding);
		setBackgroundColor(Color.WHITE);

		checkViewMode();
	}

	private void checkViewMode() {
		if (!ViewMode.MessageMode.equals(mViewMode)) {
			mIconsView = mViewStub.inflate();
			// mIconsView = findViewById(R.id.icons);
			mIconFavorite = (ImageView) findViewById(R.id.ic_favorite);
			mIconThread = (ImageView) findViewById(R.id.ic_thread);
			mIconPhoto = (ImageView) findViewById(R.id.ic_photo);
			mIconLock = (ImageView) findViewById(R.id.ic_lock);
		}
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

	public void showIcons(boolean show) {
		mIconsView.setVisibility(show ? View.VISIBLE : View.GONE);
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

	public void showIconLock(boolean show) {
		mIconLock.setVisibility(show ? View.VISIBLE : View.GONE);
	}

	public void showTitle(boolean show) {
		mTitleTextView.setVisibility(show ? View.VISIBLE : View.GONE);
	}

	public void showContent(boolean show) {
		mContentTextView.setVisibility(show ? View.VISIBLE : View.GONE);
	}

	public void showMeta(boolean show) {
		mMetaTextView.setVisibility(show ? View.VISIBLE : View.GONE);
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

	public ImageView getImageView() {
		return mImageView;
	}

	public TextView getTitleTextView() {
		return mTitleTextView;
	}

	public TextView getContentTextView() {
		return mContentTextView;
	}

	public TextView getMetaTextView() {
		return mMetaTextView;
	}

	public enum ViewMode {
		StatusMode(0), UserMode(1), MessageMode(2);

		private final int mode;

		private ViewMode(int mode) {
			this.mode = mode;
		}

		public static ViewMode of(int mode) {
			for (ViewMode viewMode : ViewMode.values()) {
				if (viewMode.mode == mode) {
					return viewMode;
				}
			}
			return StatusMode;
		}
	}

}
