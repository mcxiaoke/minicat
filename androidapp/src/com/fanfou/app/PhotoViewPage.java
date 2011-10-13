package com.fanfou.app;

import java.io.File;
import java.io.IOException;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.fanfou.app.config.Commons;
import com.fanfou.app.ui.widget.TouchImageView;
import com.fanfou.app.util.IOHelper;
import com.fanfou.app.util.Utils;

/**
 * @author mcxiaoke
 * @version 1.0 2011.08.28
 * @version 2.0 2011.10.12
 * 
 */
public class PhotoViewPage extends BaseActivity {

	private static final String TAG = PhotoViewPage.class.getSimpleName();
	private String mPhotoPath;

	private TouchImageView mTouchImageView;
	private TextView mSave;
	private TextView mClose;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		mPhotoPath = getIntent().getStringExtra(Commons.EXTRA_URL);
		if (TextUtils.isEmpty(mPhotoPath)) {
			finish();
			return;
		}
		if (App.DEBUG) {
			Log.i(TAG, "mPhotoPath=" + mPhotoPath);
		}

		setContentView(R.layout.photoview);
		mTouchImageView = (TouchImageView) findViewById(R.id.photoview_pic);
		mSave = (TextView) findViewById(R.id.photoview_save);
		mClose = (TextView) findViewById(R.id.photoview_close);
		mSave.setOnClickListener(this);
		mClose.setOnClickListener(this);
		Bitmap bitmap = BitmapFactory.decodeFile(mPhotoPath);
		if (bitmap != null) {
			mTouchImageView.setImageBitmap(bitmap);
		}else{
			finish();
		}
	}

	@Override
	public void onClick(View v) {
		int id = v.getId();
		if (id == R.id.photoview_close) {
			finish();
		} else if (id == R.id.photoview_save) {
			savePhoto();
		}
	}

	@Override
	public void finish() {
		super.finish();
		overridePendingTransition(R.anim.zoom_enter_2, R.anim.zoom_exit_2);
	}

	private void savePhoto() {
		File file = new File(mPhotoPath);
		if (file.exists()) {
			File dest = new File(IOHelper.getPhotoDir(this), file.getName());
			if(dest.exists()){
				Utils.notify(this, "照片已保存到 " + dest.getAbsolutePath());
			}else{
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
