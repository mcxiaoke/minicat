package com.fanfou.app.cache;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.Serializable;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;

import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.widget.ImageView;

import com.fanfou.app.App;
import com.fanfou.app.util.IOHelper;
import com.fanfou.app.util.ImageHelper;
import com.fanfou.app.util.NetworkHelper;
import com.fanfou.app.util.StringHelper;

/**
 * @author mcxiaoke
 * @version 1.0 2011.09.23
 * @version 2.0 2011.09.27
 * 
 */
public class ImageLoader implements Runnable, IImageLoader {

	public static final String TAG = ImageLoader.class.getSimpleName();

	private static final String EXTRA_TASK = "task";
	private static final String EXTRA_BITMAP = "bitmap";
	private static final int MESSAGE_FINISH = 0;
	private static final int MESSAGE_ERROR = 1;
	public static final int CORE_POOL_SIZE = 4;

	public final ExecutorService mExecutorService = Executors
			.newFixedThreadPool(CORE_POOL_SIZE);

	private final BlockingQueue<ImageLoaderTask> mTaskQueue = new LinkedBlockingQueue<ImageLoaderTask>();
	private final ConcurrentHashMap<ImageLoaderTask, ImageLoaderCallback> mCallbackMap = new ConcurrentHashMap<ImageLoaderTask, ImageLoaderCallback>();
	public final ImageCache mCache;
	private final Handler mHandler;
	private final DefaultHttpClient mHttpClient;

	public ImageLoader(Context context) {
		this.mCache = new ImageCache(context);
		this.mHandler = new ImageDownloadHandler();
		this.mExecutorService.submit(this);
		this.mHttpClient = NetworkHelper.newHttpClient();
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
			addToQueue(task, new InternelCallback(task.imageView));
		}

	}

	private void addToQueue(final ImageLoaderTask task,
			final ImageLoaderCallback callback) {
		if (!mTaskQueue.contains(task)) {
			try {
				if (App.DEBUG) {
					Log.d(TAG, "addToQueue " + new URL(task.url).getFile());
				}
				mTaskQueue.put(task);
				mCallbackMap.put(task, callback);
			} catch (InterruptedException e) {
				if (App.DEBUG)
					e.printStackTrace();
			} catch (MalformedURLException e) {
				e.printStackTrace();
			}
		}
	}

	@Override
	public void run() {
		while (true) {
			try {
				ImageLoaderTask task = mTaskQueue.take();
				if (!mCache.containsKey(task.url)) {
					final Bitmap bitmap = downloadImage(task.url);
					mCache.put(task.url, bitmap);
					final Message message = mHandler
							.obtainMessage(MESSAGE_FINISH);
					message.getData().putSerializable(EXTRA_TASK, task);
					message.getData().putParcelable(EXTRA_BITMAP, bitmap);
					mHandler.sendMessage(message);
				}
			} catch (InterruptedException e) {
			} catch (IOException e) {
				if (App.DEBUG) {
					Log.d(TAG, "run() error:" + e.getMessage());
				}
			} finally {
			}
		}
	}

	private Bitmap downloadImage(String url) throws IOException {
		HttpGet request = new HttpGet(url);
		HttpResponse response = mHttpClient.execute(request);
		int statusCode = response.getStatusLine().getStatusCode();
		if (App.DEBUG) {
			Log.d(TAG, "downloadImage() statusCode=" + statusCode + " [" + url
					+ "]");
		}
		return BitmapFactory.decodeStream(response.getEntity().getContent());
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
				if (bitmap != null) {
					// mCache.put(task.url, bitmap);
					if (callback != null) {
						callback.onFinish(task.url, bitmap);
					}
				}
				mCallbackMap.remove(task);
				break;
			case MESSAGE_ERROR:
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
				if (App.DEBUG) {
					Log.d(TAG, "InternelCallback.onFinish() tag=" + tag);
					Log.d(TAG, "InternelCallback.onFinish() url=" + url);
				}
				if (tag != null && tag.equals(url)) {
					imageView.setImageBitmap(bitmap);
					imageView.postInvalidate();
				}
			}
		}

		@Override
		public void onError(String message) {
		}

	}

	private class ImageLoaderTask implements Serializable {
		private static final long serialVersionUID = 8580178675788143663L;
		public final String url;
		public final ImageView imageView;

		public ImageLoaderTask(String url, ImageView imageView) {
			this.url = url;
			this.imageView = imageView;
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
