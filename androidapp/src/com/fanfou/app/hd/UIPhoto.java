package com.fanfou.app.hd;

import java.io.File;
import java.io.IOException;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;

import com.actionbarsherlock.app.ActionBar;
import com.fanfou.app.hd.cache.ImageLoader;
import com.fanfou.app.hd.controller.EmptyViewController;
import com.fanfou.app.hd.ui.imagezoom.ImageViewTouch;
import com.fanfou.app.hd.util.IOHelper;
import com.fanfou.app.hd.util.ImageHelper;
import com.fanfou.app.hd.util.Utils;

/**
 * @author mcxiaoke
 * @version 1.0 2011.08.28
 * @version 2.0 2011.10.12
 * @version 2.1 2011.10.27
 * @version 2.2 2011.11.09
 * @version 3.0 2011.11.16
 * @version 3.1 2011.11.17
 * @version 3.2 2011.11.22
 * @version 4.0 2012.03.13
 * 
 */
public class UIPhoto extends UIBaseSupport {

	private static final String TAG = UIPhoto.class.getSimpleName();
	private String url;
	private Bitmap bitmap;

	private ImageViewTouch mImageView;

	private View vEmpty;
	private EmptyViewController emptyViewController;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
	}

	@Override
	protected void initialize() {
		parseIntent(getIntent());
	}

	@Override
	protected void setLayout() {
		if (TextUtils.isEmpty(url)) {
			finish();
			return;
		}

		setContentView(R.layout.photoview);
		findViews();

		if (App.DEBUG) {
			Log.d(TAG, "mPhotoPath=" + url);
		}

		if (url.startsWith("http")) {
			loadFromWeb(url);
		} else {
			loadFromLocal(url);
		}

	}

	@Override
	protected void setActionBar() {
		super.setActionBar();
		setTitle("查看图片");
	}

	@Override
	protected void onMenuHomeClick() {
		finish();
	}

	private void findViews() {
		mImageView = (ImageViewTouch) findViewById(R.id.photoview_pic);
		vEmpty = findViewById(android.R.id.empty);
		emptyViewController = new EmptyViewController(vEmpty);
	}

	private void showProgress() {
		mImageView.setVisibility(View.GONE);
		emptyViewController.showProgress();
	}

	private void showContent(Bitmap bitmap) {
		emptyViewController.hideProgress();
		mImageView.setVisibility(View.VISIBLE);
		if (bitmap != null) {
			mImageView.setImageBitmapReset(bitmap, true);
		}
	}

	private void loadFromLocal(String path) {
		try {
			bitmap = ImageHelper.loadFromPath(this, url, 1000, 1000);
			if (App.DEBUG) {
				Log.d(TAG, "Bitmap width=" + bitmap.getWidth() + " height="
						+ bitmap.getHeight());
			}
			showContent(bitmap);
		} catch (IOException e) {
			if (App.DEBUG) {
				Log.e(TAG, "" + e);
			}
		}
	}

	private void loadFromWeb(String url) {
		final Handler handler = new Handler() {
			@Override
			public void handleMessage(Message msg) {
				switch (msg.what) {
				case ImageLoader.MESSAGE_FINISH:
					Bitmap bitmap = (Bitmap) msg.obj;
					showContent(bitmap);
					break;
				case ImageLoader.MESSAGE_ERROR:
					break;
				default:
					break;
				}
			}
		};
		Bitmap bitmap = App.getImageLoader().getImage(url, handler);
		if (bitmap == null) {
			showProgress();
		} else {
			showContent(bitmap);
		}
	}

	@Override
	public void onContentChanged() {
		super.onContentChanged();
		// mImageView = (ImageViewTouch) findViewById(R.id.photoview_pic);
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

	}

	@Override
	protected int getMenuResourceId() {
		return R.menu.photo_menu;
	}

	@Override
	public void finish() {
		super.finish();
		overridePendingTransition(R.anim.zoom_enter_2, R.anim.zoom_exit_2);
	}

	@Override
	protected void onDestroy() {
		ImageHelper.releaseBitmap(bitmap);
		super.onDestroy();
	}

	private void doSave() {
		File file = new File(url);
		if (file.exists()) {
			File dest = new File(IOHelper.getPhotoDir(this), file.getName());
			if (dest.exists()) {
				Utils.notify(this, "照片已保存到 " + dest.getAbsolutePath());
			} else {
				try {
					IOHelper.copyFile(file, dest);
					Utils.notify(this, "照片已保存到 " + dest.getAbsolutePath());
				} catch (IOException e) {
					if (App.DEBUG) {
						e.printStackTrace();
					}
				}
			}
		}
	}

}
