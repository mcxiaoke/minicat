package org.mcxiaoke.fancooker;

import java.io.File;
import java.io.IOException;

import org.mcxiaoke.fancooker.cache.ImageLoader;
import org.mcxiaoke.fancooker.controller.EmptyViewController;
import org.mcxiaoke.fancooker.ui.imagezoom.ImageViewTouch;
import org.mcxiaoke.fancooker.util.IOHelper;
import org.mcxiaoke.fancooker.util.ImageHelper;
import org.mcxiaoke.fancooker.util.Utils;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.View;
import android.view.View.OnClickListener;

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
 * @version 4.1 2012.03.22
 * @version 5.0 2012.03.27
 * 
 */
public class UIPhoto extends Activity implements OnClickListener{

	private static final String TAG = UIPhoto.class.getSimpleName();
	private String url;
	private Bitmap bitmap;

	private ImageViewTouch mImageView;

	private View vEmpty;
	private EmptyViewController emptyViewController;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		initialize();
		setLayout();
	}

	protected void initialize() {
		parseIntent(getIntent());
	}

	protected void setLayout() {
		if (TextUtils.isEmpty(url)) {
			finish();
			return;
		}

		setContentView(R.layout.ui_photo);
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

	private void findViews() {
		mImageView = (ImageViewTouch) findViewById(R.id.photo);
		findViewById(R.id.close).setOnClickListener(this);
		vEmpty = findViewById(android.R.id.empty);
		emptyViewController = new EmptyViewController(vEmpty);
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.close:
			finish();
			break;
		default:
			break;
		}
	}

	@Override
	public void onCreateContextMenu(ContextMenu menu, View v,
			ContextMenuInfo menuInfo) {
		super.onCreateContextMenu(menu, v, menuInfo);
		getMenuInflater().inflate(R.menu.photo_menu, menu);
	}

	@Override
	public boolean onContextItemSelected(android.view.MenuItem item) {
		switch (item.getItemId()) {
		case R.id.menu_save:
			doSave();
			return true;
		default:
			return super.onContextItemSelected(item);
		}
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
				Utils.notify(this, "照片已保存到 " + dest.getAbsolutePath());
			} else {
				try {
					IOHelper.copyFile(file, dest);
					Utils.notify(this, "照片已保存到 " + dest.getAbsolutePath());
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
