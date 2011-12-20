package com.fanfou.app.cache;

import java.io.IOException;
import java.util.Collections;
import java.util.Map;
import java.util.WeakHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.os.Handler;
import android.util.Log;
import android.widget.ImageView;

import com.fanfou.app.App;
import com.fanfou.app.R;
import com.fanfou.app.http.NetClient;

public class ImageLoaderNew implements IImageLoader {
	private static final String TAG = ImageLoaderNew.class.getSimpleName();
	private Map<ImageView, String> mViewsMap = Collections
			.synchronizedMap(new WeakHashMap<ImageView, String>());

	private final ImageCache mCache;
	private final NetClient mClient;
	private final ExecutorService mExecutorService;

	// private final Context mContext;
	// private final Handler mHandler;

	public ImageLoaderNew(Context context) {
		// this.mContext=context;
		this.mClient = new NetClient();
		this.mCache = ImageCache.getInstance();
		this.mExecutorService = Executors.newFixedThreadPool(2);
	}

	final int stub_id = R.drawable.default_head;

	@Override
	public Bitmap getImage(String key, final Handler handler) {
		return null;
	}

	@Override
	public void displayImage(String key, ImageView imageView, int stubId) {
		mViewsMap.put(imageView, key);
		Bitmap bitmap = mCache.get(key);
		if (bitmap == null) {
			queuePhoto(key, imageView);
			imageView.setImageResource(stubId);
		} else {
			imageView.setImageBitmap(bitmap);
		}
	}

	@Override
	public void shutdown() {
	}

	@Override
	public void clearCache() {
		mCache.clear();
	}

	@Override
	public void clearQueue() {
	}

	public void set(String url, ImageView imageView) {
		mViewsMap.put(imageView, url);
		Bitmap bitmap = mCache.get(url);
		if (bitmap != null)
			imageView.setImageBitmap(bitmap);
		else {
			queuePhoto(url, imageView);
			imageView.setImageResource(stub_id);
		}
	}

	public void load(String url, ImageView imageView) {
	}

	private void queuePhoto(String url, ImageView imageView) {
		ImageTask p = new ImageTask(url, imageView);
		mExecutorService.submit(new ImageDownloader(p));
	}

	private Bitmap getBitmap(String url) {
		Bitmap bitmap = mCache.get(url);
		if (bitmap != null) {
			return bitmap;
		}
		try {
			return mClient.getBitmap(url);
		} catch (IOException e) {
			if (App.DEBUG) {
				Log.d(TAG, e.toString());
			}
			return null;
		}
	}

	private static class ImageTask {
		public String url;
		public ImageView view;

		public ImageTask(String u, ImageView i) {
			url = u;
			view = i;
		}
	}

	private class ImageDownloader implements Runnable {
		ImageTask task;

		ImageDownloader(ImageTask task) {
			this.task = task;
		}

		@Override
		public void run() {
			if (isExpired(mViewsMap, task))
				return;
			Bitmap bitmap = getBitmap(task.url);
			mCache.put(task.url, bitmap);
			if (isExpired(mViewsMap, task))
				return;
			BitmapDisplayer bd = new BitmapDisplayer(bitmap, task);
			Activity a = (Activity) task.view.getContext();
			a.runOnUiThread(bd);
		}
	}

	private class BitmapDisplayer implements Runnable {
		Bitmap bitmap;
		ImageTask task;

		public BitmapDisplayer(Bitmap b, ImageTask t) {
			bitmap = b;
			task = t;
		}

		@Override
		public void run() {
			if (isExpired(mViewsMap, task))
				return;
			if (bitmap != null)
				task.view.setImageBitmap(bitmap);
			else
				task.view.setImageResource(stub_id);
		}
	}

	private static boolean isExpired(Map<ImageView, String> map, ImageTask task) {
		String tag = map.get(task.view);
		return tag == null || !tag.equals(task.url);
	}

}
