package com.mcxiaoke.minicat.app;

import android.app.ActionBar;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import com.mcxiaoke.minicat.AppContext;
import com.mcxiaoke.minicat.R;
import com.mcxiaoke.minicat.controller.EmptyViewController;
import com.mcxiaoke.minicat.util.IOHelper;
import com.mcxiaoke.minicat.util.Utils;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.assist.ImageLoadingListener;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;
import pl.droidsonroids.gif.GifDrawable;
import pl.droidsonroids.gif.GifImageView;
import uk.co.senab.photoview.PhotoView;

import java.io.File;
import java.io.IOException;

/**
 * @author mcxiaoke
 * @version 5.0 2012.03.27
 */
public class UIPhoto extends Activity implements OnClickListener {

    private static final String TAG = UIPhoto.class.getSimpleName();
    private String url;

    private PhotoView mImageView;
    private GifImageView mGifImageView;

    private View vEmpty;
    private EmptyViewController emptyViewController;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        requestWindowFeature(Window.FEATURE_ACTION_BAR_OVERLAY);
        super.onCreate(savedInstanceState);
        getActionBar().setBackgroundDrawable(new ColorDrawable(0x66333333));
        getActionBar().setDisplayHomeAsUpEnabled(true);
        getActionBar().setTitle("查看照片");
        initialize();
        setLayout();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_photo, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                break;
            case R.id.menu_save:
                doSave();
                break;
        }
        return true;
    }

    @Override
    public void finish() {
        super.finish();
        overridePendingTransition(R.anim.zoom_enter_2, R.anim.zoom_exit_2);
    }

    protected void initialize() {
        parseIntent(getIntent());
    }

    protected void setLayout() {
        setContentView(R.layout.ui_photo);
        findViews();

        if (AppContext.DEBUG) {
            Log.d(TAG, "mPhotoPath=" + url);
        }

        displayImage();

    }

    private void findViews() {
        mImageView = (PhotoView) findViewById(R.id.photo);
        mGifImageView = (GifImageView) findViewById(R.id.image);
        vEmpty = findViewById(android.R.id.empty);
        emptyViewController = new EmptyViewController(vEmpty);
    }

    private void toggleActionBar() {
        ActionBar actionBar = getActionBar();
        if (actionBar.isShowing()) {
            actionBar.hide();
        } else {
            actionBar.show();
        }
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.photo) {
            toggleActionBar();
        }
    }

    private void showProgress() {
        mImageView.setVisibility(View.GONE);
        mGifImageView.setVisibility(View.GONE);
        emptyViewController.showProgress();
    }

    private void showEmptyText(String text) {
        mImageView.setVisibility(View.GONE);
        mGifImageView.setVisibility(View.GONE);
        emptyViewController.showEmpty(text);
    }

    private void showContent(String imageUri, Bitmap bitmap) {
        emptyViewController.hideProgress();
        if (bitmap == null) {
            return;
        }
        if (imageUri.endsWith(".gif")) {
            mImageView.setVisibility(View.GONE);
            mGifImageView.setVisibility(View.VISIBLE);
            try {
                final File file = ImageLoader.getInstance().getDiscCache().get(imageUri);
                final GifDrawable drawable = new GifDrawable(file);
                mGifImageView.setImageDrawable(drawable);
            } catch (IOException e) {
                showEmptyText("IOException");
            }
        } else {
            mGifImageView.setVisibility(View.GONE);
            mImageView.setVisibility(View.VISIBLE);
            mImageView.setImageBitmap(bitmap);
        }
    }

    private void displayImage() {
        final String imageUrl = url;
        final ImageLoadingListener listener = new ImageLoadingListener() {

            @Override
            public void onLoadingStarted(String imageUri, View view) {
                showProgress();
            }

            @Override
            public void onLoadingFailed(String imageUri, View view,
                                        FailReason failReason) {
                showEmptyText(failReason.getType() + ":" + failReason.getCause());
            }

            @Override
            public void onLoadingComplete(String imageUri, View view,
                                          Bitmap loadedImage) {
                showContent(imageUri, loadedImage);
            }

            @Override
            public void onLoadingCancelled(String imageUri, View view) {
                showEmptyText("Cancelled");
            }
        };
        ImageLoader.getInstance().loadImage(imageUrl, getDisplayImageOptions(), listener);
    }

    private DisplayImageOptions getDisplayImageOptions() {
        DisplayImageOptions.Builder builder = new DisplayImageOptions.Builder();
        builder.cacheInMemory(true).cacheOnDisc(true);
        builder.bitmapConfig(Config.ARGB_8888);
        builder.showImageOnFail(R.drawable.photo_error);
        builder.showImageOnLoading(R.drawable.photo_loading);
        builder.imageScaleType(ImageScaleType.IN_SAMPLE_INT);
        return builder.build();
    }

    private void parseIntent(Intent intent) {
        String action = intent.getAction();
        if (action == null) {
            url = intent.getStringExtra("url");
        } else if (action.equals(Intent.ACTION_VIEW)) {
            Uri uri = intent.getData();
            if (uri.getScheme().equals("content")) {
                url = IOHelper.getRealPathFromURI(this, uri);
            } else {
                url = uri.getPath();
            }
        }
        if (!TextUtils.isEmpty(url)) {
            url = url.split("@")[0];
        }
        if (AppContext.DEBUG) {
            Log.d(TAG, "parseIntent() " + url);
        }


    }

    private void doSave() {
        File file = ImageLoader.getInstance().getDiscCache().get(url);
        if (file == null || !file.isFile()) {
            return;
        }
        String ext = url.toLowerCase().endsWith(".gif") ? ".gif" : ".jpg";
        final String fileName = "IMG_FANFOU_" + System.currentTimeMillis() + ext;
        File dest = new File(IOHelper.getPictureDir(this), fileName);
        if (dest.exists() || IOHelper.copyFile(file, dest)) {
            Utils.mediaScan(this, Uri.fromFile(dest));
            Utils.notify(this, "图片已保存到存储卡的 Pictures 目录");
        }
    }

}
