package com.fanfou.app.cache;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.Serializable;
import java.lang.ref.SoftReference;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.PriorityBlockingQueue;

import org.apache.http.HttpResponse;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.widget.ImageView;

import com.fanfou.app.App;
import com.fanfou.app.http.ConnectionManager;
import com.fanfou.app.util.IOHelper;
import com.fanfou.app.util.ImageHelper;
import com.fanfou.app.util.StringHelper;

/**
 * @author mcxiaoke
 * @version 1.0 2011.09.23
 * @version 2.0 2011.09.27
 * @version 2.1 2011.11.04
 * @version 2.5 2011.11.23
 * 
 */
public class ImageLoader implements Runnable, IImageLoader {

	public static final String TAG = ImageLoader.class.getSimpleName();

	private static final String EXTRA_TASK = "task";
	private static final String EXTRA_BITMAP = "bitmap";
	private static final int MESSAGE_FINISH = 0;
	private static final int MESSAGE_ERROR = 1;
	private static final int CORE_POOL_SIZE = 2;

	private final ExecutorService mExecutorService = Executors
			.newFixedThreadPool(CORE_POOL_SIZE);
	private final BlockingQueue<ImageLoaderTask> mTaskQueue = new PriorityBlockingQueue<ImageLoaderTask>(
			20, new ImageLoaderTaskComparator());
	// private final BlockingQueue<ImageLoaderTask> mTaskQueue = new
	// LinkedBlockingQueue<ImageLoader.ImageLoaderTask>();
	private final ConcurrentHashMap<ImageLoaderTask, ImageLoaderCallback> mCallbackMap = new ConcurrentHashMap<ImageLoaderTask, ImageLoaderCallback>();
	public final ImageCache mCache;
	private final Handler mHandler;

	private static ImageLoader INSTANCE = null;

	private ImageLoader(Context context) {
		this.mCache = ImageCache.getInstance(context);
		this.mHandler = new ImageDownloadHandler();
		new Thread(this).start();
	}

	public static ImageLoader getInstance(Context context) {
		if (INSTANCE == null) {
			INSTANCE = new ImageLoader(context.getApplicationContext());
		}
		return INSTANCE;
	}

	@Override
	public Bitmap load(String key, ImageLoaderCallback callback) {
		if (key != null) {
			if (App.DEBUG) {
				Log.d(TAG,
						"load() key=" + key + " callback="
								+ callback.hashCode());
			}
			return loadAndFetch(key, callback);
		}
		return null;

	}

	private Bitmap loadAndFetch(String key, ImageLoaderCallback callback) {
		Bitmap bitmap = null;
		if (mCache.containsKey(key)) {
			bitmap = mCache.get(key);
			if (bitmap == null) {
				if (App.DEBUG) {
					Log.d(TAG, "loadAndFetch() key=" + key + " callback="
							+ callback.hashCode());
				}
			}
		}
		if (bitmap == null) {
			ImageLoaderTask task = new ImageLoaderTask(key, null);
			addToQueue(task, callback);
		}

		return bitmap;
	}

	@Override
	public Bitmap load(String key) {
		if (key != null) {
			return loadFromLocal(key);
		}
		return null;

	}

	@Override
	public File loadFile(String key) {
		return null;
	}

	private Bitmap loadFromLocal(String key) {
		Bitmap bitmap = null;
		if (mCache.containsKey(key)) {
			bitmap = mCache.get(key);
		}
		return bitmap;
	}

	@Override
	public void set(final String url, final ImageView imageView,
			final int iconId) {
		if (url == null || imageView == null) {
			return;
		}
		final ImageLoaderTask task = new ImageLoaderTask(url, imageView);
		Bitmap bitmap = null;
		if (mCache.containsKey(task.url)) {
			bitmap = mCache.get(task.url);
		}
		if (bitmap != null) {
			task.imageView.setImageBitmap(ImageHelper.getRoundedCornerBitmap(
					bitmap, 6));
		} else {
			task.imageView.setImageResource(iconId);
			task.imageView.setTag(url);
			addToQueue(task, new InternelCallback(task.imageView));
		}
	}

	/**
	 * 不设置默认图片
	 * 
	 * @param key
	 * @param imageView
	 */
	@Override
	public void set(final String url, final ImageView imageView) {
		if (url == null || imageView == null) {
			return;
		}
		final ImageLoaderTask task = new ImageLoaderTask(url, imageView);
		Bitmap bitmap = null;
		if (mCache.containsKey(task.url)) {
			bitmap = mCache.get(task.url);
		}
		if (bitmap != null) {
			task.imageView.setImageBitmap(bitmap);
		} else {
			task.imageView.setTag(url);
			addToQueue(task, new InternelCallback(task.imageView));
		}

	}

	private void addToQueue(final ImageLoaderTask task,
			final ImageLoaderCallback callback) {
		if (!mTaskQueue.contains(task)) {
			if (App.DEBUG) {
				Log.d(TAG, "addToQueue " + task.url);
			}
			mTaskQueue.add(task);
			mCallbackMap.put(task, callback);
		}
	}

	@Override
	public void run() {
		while (true) {
			try {
				final ImageLoaderTask task = mTaskQueue.take();
				if (App.DEBUG) {
					Log.d(TAG, "mTaskQueue.take() task=" + task.toString());
				}
				if (!mCache.containsKey(task.url)) {
					final Bitmap bitmap = downloadImage(task.url);
					final Message message = mHandler.obtainMessage();
					if (bitmap != null) {
						message.what = MESSAGE_FINISH;
						message.getData().putParcelable(EXTRA_BITMAP, bitmap);
					} else {
						message.what = MESSAGE_ERROR;
					}
					message.getData().putSerializable(EXTRA_TASK, task);
					mHandler.sendMessage(message);
				}
			} catch (IOException e) {
				if (App.DEBUG) {
					Log.d(TAG, "ImageLoaderTask.run() error:" + e.getMessage());
				}
			} catch (InterruptedException e) {
				if (App.DEBUG) {
					Log.d(TAG, "ImageLoaderTask.run() error:" + e.getMessage());
				}
			}
		}
	}

	private Bitmap downloadImage(String url) throws IOException {
		HttpResponse response = ConnectionManager.get(url);
		int statusCode = response.getStatusLine().getStatusCode();
		if (App.DEBUG) {
			Log.d(TAG, "downloadImage() statusCode=" + statusCode + " [" + url
					+ "]");
		}
		Bitmap bitmap = null;
		if (statusCode == 200) {
			bitmap = BitmapFactory.decodeStream(response.getEntity()
					.getContent());
			if (bitmap != null) {
				mCache.put(url, bitmap);
			}
		}
		return bitmap;
	}

	private class ImageDownloadHandler extends Handler {

		@Override
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case MESSAGE_FINISH:
				final ImageLoaderTask task = (ImageLoaderTask) msg.getData()
						.getSerializable(EXTRA_TASK);
				final ImageLoaderCallback callback = mCallbackMap.get(task);
				final Bitmap bitmap = (Bitmap) msg.getData().getParcelable(
						EXTRA_BITMAP);
				if (callback != null) {
					callback.onFinish(task.url, bitmap);
				}
				mCallbackMap.remove(task);
				break;
			case MESSAGE_ERROR:
				final ImageLoaderTask task2 = (ImageLoaderTask) msg.getData().getSerializable(EXTRA_TASK);
				mCallbackMap.remove(task2);
				break;
			default:
				break;
			}

		}

	}

	private static class InternelCallback implements ImageLoaderCallback {
		private ImageView imageView;

		public InternelCallback(final ImageView imageView) {
			this.imageView = imageView;
		}

		@Override
		public void onFinish(String url, Bitmap bitmap) {
			if (bitmap != null) {
				String tag = (String) imageView.getTag();
				if (tag != null && tag.equals(url)) {
					if (App.DEBUG) {
						Log.d(TAG, "InternelCallback.onFinish() invalidate");
					}
					imageView.setImageBitmap(bitmap);
					imageView.postInvalidate();
				}
			}
		}

		@Override
		public void onError(String message) {
		}

	}

	private static class ImageLoaderTaskComparator implements
			Comparator<ImageLoaderTask> {

		@Override
		public int compare(ImageLoaderTask a, ImageLoaderTask b) {
			if (a.timestamp > b.timestamp) {
				return -1;
			} else if (a.timestamp < b.timestamp) {
				return 1;
			} else {
				return 0;
			}
		}

	}

	private class ImageLoaderTask implements Serializable {

		private static final long serialVersionUID = 8580178675788143663L;
		public final long timestamp;
		public final String url;
		public final ImageView imageView;

		public ImageLoaderTask(String url, ImageView imageView) {
			this.timestamp = System.currentTimeMillis();
			this.url = url;
			this.imageView = imageView;
		}

		@Override
		public boolean equals(Object o) {
			if (o instanceof ImageLoaderTask) {
				if (((ImageLoaderTask) o).url.equals(url)) {
					return true;
				}
			}
			return false;
		}

		@Override
		public int hashCode() {
			return url.hashCode();
		}

		@Override
		public String toString() {
			return "time:" + timestamp + " url:" + url;
		}

	}

	@Override
	public void shutdown() {
		mExecutorService.shutdown();
		mTaskQueue.clear();
		mCallbackMap.clear();
		mCache.clear();
	}

	@Override
	public void clearCache() {
		mCache.clear();
	}

	@Override
	public void clearQueue() {
		mTaskQueue.clear();
		mCallbackMap.clear();
	}

}
