package com.mcxiaoke.minicat.ui.widget;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import com.mcxiaoke.minicat.R;
import com.mcxiaoke.minicat.controller.UIController;
import com.mcxiaoke.minicat.util.StringHelper;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;

/**
 * @author mcxiaoke
 * @version 1.1 2012.03.29
 */
public class ItemView extends RelativeLayout {

    public static final DisplayImageOptions DISPLAY_OPTIONS = new DisplayImageOptions.Builder()
            .cacheOnDisc(true).cacheInMemory(true)
            .showImageOnFail(R.drawable.photo_placeholder_small)
            .imageScaleType(ImageScaleType.IN_SAMPLE_INT)
            .bitmapConfig(Bitmap.Config.RGB_565).build();
    private ImageView mImageView;
    private TextView mUserNameTextView;
    private TextView mUserIdTextView;
    private TextView mContentTextView;
    private TextView mTimeTextView;
    private TextView mMetaTextView;
    private ImageView mPhotoView;

    private LinearLayout mViewStub;
    private View mIconsView;

    private ImageView mIconFavorite;
    private ImageView mIconThread;
    private ImageView mIconPhoto;
    private ImageView mIconRetweet;
    private ImageView mIconLock;

    private Context mContext;
    private LayoutInflater mInflater;

    private ViewMode mViewMode;

    private OnImageClickListener mListener;

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
        setDescendantFocusability(ViewGroup.FOCUS_BLOCK_DESCENDANTS);
        mContext = context;
        mInflater = LayoutInflater.from(mContext);
        mInflater.inflate(R.layout.item_view, this, true);
        mImageView = (ImageView) findViewById(R.id.image);
        mUserNameTextView = (TextView) findViewById(R.id.user_name);
        mUserIdTextView = (TextView) findViewById(R.id.user_id);
        mContentTextView = (TextView) findViewById(R.id.text);
        mTimeTextView = (TextView) findViewById(R.id.time);
        mMetaTextView = (TextView) findViewById(R.id.meta);
        mPhotoView = (ImageView) findViewById(R.id.photo);

        mViewStub = (LinearLayout) findViewById(R.id.stub);
        mIconsView = findViewById(R.id.icons);
        mIconFavorite = (ImageView) findViewById(R.id.ic_favorite);
        mIconThread = (ImageView) findViewById(R.id.ic_thread);
        mIconPhoto = (ImageView) findViewById(R.id.ic_photo);
        mIconRetweet = (ImageView) findViewById(R.id.ic_retweet);
        mIconLock = (ImageView) findViewById(R.id.ic_lock);

        mMetaTextView.setVisibility(View.GONE);

        final Resources res = getResources();
        mViewMode = ViewMode.StatusMode;
        checkViewMode();

        mImageView.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mListener != null) {
                    mListener.onImageClick(mImageView);
                }
            }
        });
    }

    private void checkViewMode() {
        if (ViewMode.MessageMode.equals(mViewMode)) {

        }
    }

    public void setOnImageClickListener(OnImageClickListener listener) {
        this.mListener = listener;
    }

    public void setUserName(CharSequence text) {
        mUserNameTextView.setText(text);
    }

    public void setUserId(CharSequence text) {
        mUserIdTextView.setText(text);
    }

    private void updatePhoto(final String photoUrl) {
        final boolean hasPhoto = !StringHelper.isEmpty(photoUrl);
        mPhotoView.setVisibility(hasPhoto ? View.VISIBLE : View.GONE);
        loadBigImage(photoUrl, mPhotoView);
    }

    private void loadBigImage(final String imageUri, final ImageView imageView) {
        ImageLoader.getInstance().displayImage(imageUri, imageView, DISPLAY_OPTIONS);
    }

    public void setContent(CharSequence text) {
        mContentTextView.setText(text);
    }

    public void setPhoto(final String photoUrl) {
        setPhoto(photoUrl, null);
    }

    public void setPhoto(final String photoUrl, final String largeUrl) {
        updatePhoto(photoUrl);
        if (!StringHelper.isEmpty(largeUrl)) {
            mPhotoView.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(final View v) {
                    UIController.showPhoto((android.app.Activity) getContext(), largeUrl);
                }
            });
        } else {
            mPhotoView.setOnClickListener(null);
        }
    }

    public void setTime(CharSequence text) {
        mTimeTextView.setText(text);
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

    public void showIconRetweet(boolean show) {
        mIconRetweet.setVisibility(show ? View.VISIBLE : View.GONE);
    }

    public void showIconLock(boolean show) {
        mIconLock.setVisibility(show ? View.VISIBLE : View.GONE);
    }

    public void showTitle(boolean show) {
        mUserNameTextView.setVisibility(show ? View.VISIBLE : View.GONE);
    }

    public void showContent(boolean show) {
        mContentTextView.setVisibility(show ? View.VISIBLE : View.GONE);
    }

    public void showMeta(boolean showA, boolean showB) {
        mTimeTextView.setVisibility(showA ? View.VISIBLE : View.GONE);
        mMetaTextView.setVisibility(showB ? View.VISIBLE : View.GONE);
    }

    public void setTitleTextSize(float size) {
        mUserNameTextView.setTextSize(TypedValue.COMPLEX_UNIT_PX, size);
    }

    public void setContentTextSize(float size) {
        mContentTextView.setTextSize(TypedValue.COMPLEX_UNIT_PX, size);
    }

    public void setMetaTextSize(float sizeA, float sizeB) {
        mTimeTextView.setTextSize(TypedValue.COMPLEX_UNIT_PX, sizeA);
        mMetaTextView.setTextSize(TypedValue.COMPLEX_UNIT_PX, sizeB);
    }

    public void setUserNameTextColor(int color) {
        mUserNameTextView.setTextColor(color);
    }

    public void setContentTextColor(int color) {
        mContentTextView.setTextColor(color);
    }

    public void setContentMaxLines(int maxLines) {
        mContentTextView.setMaxLines(maxLines);
    }

    public void setMetaTextColor(int color) {
        mMetaTextView.setTextColor(color);
    }

    public ImageView getImageView() {
        return mImageView;
    }

    public TextView getContentTextView() {
        return mContentTextView;
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

    public interface OnImageClickListener {
        public void onImageClick(ImageView view);
    }

}
