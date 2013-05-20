package com.mcxiaoke.fanfouapp.ui.widget;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewStub;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import com.mcxiaoke.fanfouapp.R;

/**
 * @author mcxiaoke
 * @version 1.1 2012.03.29
 */
public class ItemView extends RelativeLayout {
    private ImageView mImageView;
    private TextView mTitleTextView;
    private TextView mContentTextView;
    private TextView mMetaATextView;
    private TextView mMetaBTextView;

    private ViewStub mViewStub;
    private View mIconsView;

    private ImageView mIconFavorite;
    private ImageView mIconThread;
    private ImageView mIconPhoto;
    private ImageView mIconRetweet;
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
        mMetaATextView = (TextView) findViewById(R.id.meta_a);
        mMetaBTextView = (TextView) findViewById(R.id.meta_b);

        mViewStub = (ViewStub) findViewById(R.id.stub);

        final Resources res = getResources();
        mPadding = res.getDimensionPixelSize(R.dimen.list_card_padding);
        mViewMode = ViewMode.StatusMode;

        setPadding(mPadding, mPadding, mPadding, mPadding);

        checkViewMode();
    }

    private void checkViewMode() {
        if (!ViewMode.MessageMode.equals(mViewMode)) {
            mIconsView = mViewStub.inflate();
            // mIconsView = findViewById(R.id.icons);
            mIconFavorite = (ImageView) findViewById(R.id.ic_favorite);
            mIconThread = (ImageView) findViewById(R.id.ic_thread);
            mIconPhoto = (ImageView) findViewById(R.id.ic_photo);
            mIconRetweet = (ImageView) findViewById(R.id.ic_retweet);
            mIconLock = (ImageView) findViewById(R.id.ic_lock);
        }
    }

    public void setTitle(CharSequence text) {
        mTitleTextView.setText(text);
    }

    public void setContent(CharSequence text) {
        mContentTextView.setText(text);
    }

    public void setMeta(CharSequence textA, CharSequence textB) {
        mMetaATextView.setText(textA);
        mMetaBTextView.setText(textB);
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

    public void showIconRetweet(boolean show) {
        mIconRetweet.setVisibility(show ? View.VISIBLE : View.GONE);
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

    public void showMeta(boolean showA, boolean showB) {
        mMetaATextView.setVisibility(showA ? View.VISIBLE : View.GONE);
        mMetaBTextView.setVisibility(showB ? View.VISIBLE : View.GONE);
    }

    public void setTitleTextSize(float size) {
        mTitleTextView.setTextSize(size);
    }

    public void setContentTextSize(float size) {
        mContentTextView.setTextSize(size);
    }

    public void setMetaTextSize(float sizeA, float sizeB) {
        mMetaATextView.setTextSize(sizeA);
        mMetaBTextView.setTextSize(sizeB);
    }

    public void setTitleTextColor(int color) {
        mTitleTextView.setTextColor(color);
    }

    public void setContentTextColor(int color) {
        mContentTextView.setTextColor(color);
    }

    public void setMetaTextColor(int colorA, int colorB) {
        mMetaATextView.setTextColor(colorA);
        mMetaBTextView.setTextColor(colorB);
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

    public TextView getMetaATextView() {
        return mMetaATextView;
    }

    public TextView getMetaBTextView() {
        return mMetaBTextView;
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
