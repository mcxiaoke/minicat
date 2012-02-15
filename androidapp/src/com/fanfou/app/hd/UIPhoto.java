package com.fanfou.app.hd;

import java.io.File;
import java.io.IOException;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import com.fanfou.app.hd.R;
import com.fanfou.app.hd.fragments.imagezoom.ImageViewTouch;
import com.fanfou.app.hd.service.Constants;
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
 * 
 */
public class UIPhoto extends UIBaseSupport {

	private static final String TAG = UIPhoto.class.getSimpleName();
	private String mPhotoPath;
	private Bitmap bitmap;

	private ImageViewTouch mImageView;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
	}
	
	@Override
	protected void initialize(){
		parseIntent(getIntent());
	}
	
	@Override
	protected void setLayout(){
		setContentView(R.layout.photoview);
		

		if (TextUtils.isEmpty(mPhotoPath)) {
			finish();
			return;
		}
		if (App.DEBUG) {
			Log.d(TAG, "mPhotoPath=" + mPhotoPath);
		}

		try {
			bitmap = ImageHelper.loadFromPath(this, mPhotoPath, 1200, 1200);
			if (App.DEBUG) {
				Log.d(TAG, "Bitmap width=" + bitmap.getWidth() + " height="
						+ bitmap.getHeight());
			}
			mImageView.setImageBitmapReset(bitmap, true);
		} catch (IOException e) {
			if (App.DEBUG) {
				Log.e(TAG, "" + e);
			}
		}
	}

	@Override
	public void onContentChanged() {
		super.onContentChanged();
		mImageView = (ImageViewTouch) findViewById(R.id.photoview_pic);
	}

	private void parseIntent(Intent intent) {
		String action = intent.getAction();
		if (action == null) {
			mPhotoPath = intent.getStringExtra(Constants.EXTRA_URL);
		} else if (action.equals(Intent.ACTION_VIEW)) {
			Uri uri = intent.getData();
			if (uri.getScheme().equals("content")) {
				mPhotoPath = IOHelper.getRealPathFromURI(this, uri);
			} else {
				mPhotoPath = uri.getPath();
			}
		}

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
		File file = new File(mPhotoPath);
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
