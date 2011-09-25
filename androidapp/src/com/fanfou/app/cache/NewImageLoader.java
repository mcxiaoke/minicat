package com.fanfou.app.cache;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.LinkedBlockingQueue;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.widget.ImageView;

import com.fanfou.app.App;
import com.fanfou.app.R;
import com.fanfou.app.http.ResponseCode;
import com.fanfou.app.util.ImageHelper;
import com.fanfou.app.util.StringHelper;

/**
 * @author mcxiaoke
 * @version 1.0 2011.09.23
 * 
 */
public class NewImageLoader {

	public static final String TAG = NewImageLoader.class.getSimpleName();

	private static final String PARAM_URL = "url";
	private static final int MESSAGE_FINISH = 0;
	private static final int MESSAGE_ERROR = 1;

	private final ExecutorService executor;
	private final BlockingQueue<String> queue = new LinkedBlockingQueue<String>();
	public ImageCache cache;
	private Handler handler;
	private Callbacks callbacks;

	private QueueRunnable queuePool;

	public interface LoaderCallback {
		void onFinish(String url, Bitmap bitmap);

		void onError(String message);
	}

	public NewImageLoader(Context context) {
		this.cache = new ImageCache(context);
		this.callbacks = new Callbacks();
		this.handler = new ImageDownloadHandler(cache, callbacks);
		this.executor = App.me.executor;
		this.queuePool = new QueueRunnable(queue, executor, handler);
		this.executor.submit(queuePool);
	}

	// @Override
	public Bitmap load(String key, LoaderCallback callback) {
		if (key != null) {
			return loadAndFetch(key, callback);
		}
		return null;

	}

	private Bitmap loadAndFetch(String key, LoaderCallback callback) {
		Bitmap bitmap = null;
		if (cache.containsKey(key)) {
			bitmap = cache.get(key);
			if (bitmap == null) {
				callbacks.put(key, callback);
				addToQueue(key);
			}
		}
		return bitmap;
	}

	// @Override
	public Bitmap load(String key) {
		if (key != null) {
			return loadFromLocal(key);
		}
		return null;

	}

	private Bitmap loadFromLocal(String key) {
		Bitmap bitmap = null;
		if (cache.containsKey(key)) {
			bitmap = cache.get(key);
		}
		return bitmap;
	}

	public void set(final ImageLoaderTask task, final int iconId) {
		if (task != null) {
			Bitmap bitmap = null;
			if (cache.containsKey(task.url)) {
				bitmap = cache.get(task.url);
			}
			if (bitmap != null) {
				task.imageView.setImageBitmap(ImageHelper
						.getRoundedCornerBitmap(bitmap, 6));
			} else {
				task.imageView.setImageResource(iconId);
				LoaderCallback callback = new LoaderCallback() {

					@Override
					public void onFinish(String url, Bitmap bitmap) {
						if (bitmap != null) {
							String tag = (String) task.imageView.getTag();
							if (tag != null && tag.equals(url)) {
								task.imageView.setImageBitmap(ImageHelper
										.getRoundedCornerBitmap(bitmap, 6));
							}
						} else {
							task.imageView.setImageResource(iconId);
						}
						task.imageView.postInvalidate();
					}

					@Override
					public void onError(String message) {
					}
				};
				callbacks.put(task.url, callback);
				addToQueue(task.url);
			}
		} else {
			Log.d(TAG, "set() key is null.");
		}

	}

	/**
	 * 不设置默认图片
	 * 
	 * @param key
	 * @param imageView
	 */
	// @Override
	public void set(final ImageLoaderTask task) {
		if (task != null) {
			Bitmap bitmap = null;
			if (cache.containsKey(task.url)) {
				bitmap = cache.get(task.url);
			}
			if (bitmap != null) {
				task.imageView.setImageBitmap(bitmap);
			} else {
				LoaderCallback callback = new LoaderCallback() {

					@Override
					public void onFinish(String url, Bitmap bitmap) {
						if (bitmap != null) {
							String tag = (String) task.imageView.getTag();
							if (tag != null && tag.equals(url)) {
								task.imageView.setImageBitmap(bitmap);
							}
						} else {
							task.imageView
									.setImageResource(R.drawable.photo_icon);
						}
						task.imageView.postInvalidate();
					}

					@Override
					public void onError(String message) {
						task.imageView.setImageResource(R.drawable.photo_icon);
						task.imageView.postInvalidate();
					}
				};
				callbacks.put(task.url, callback);
				addToQueue(task.url);
			}
		} else {
			Log.d(TAG, "set() key is null.");
		}

	}

	private void addToQueue(String url) {
		if (!queue.contains(url)) {
			try {
				queue.put(url);
				if (App.DEBUG) {
					Log.d(TAG, "addToQueue queue.put(url) =" + url);
				}
			} catch (InterruptedException e) {
				if (App.DEBUG)
					e.printStackTrace();
			}
		}
	}

	static class QueueRunnable implements Runnable {
		private BlockingQueue<String> queue;
		private ExecutorService executor;
		private Handler handler;
		private volatile boolean running = true;

		public QueueRunnable(BlockingQueue<String> queue,
				ExecutorService executor, Handler handler) {
			this.queue = queue;
			this.executor = executor;
			this.handler = handler;
		}

		@Override
		public void run() {
			while (true) {
				String url = null;
				try {
					url = queue.take();
					if (url != null) {

						if (App.DEBUG) {
							Log.e(TAG, "take a url fro queue: " + url
									+ ", add to download queue");
						}
						executor.submit(new ImageDownloadThread(url, handler));
					}
				} catch (InterruptedException e) {
					e.printStackTrace();
				} finally {
					running = false;
				}
			}
		}
	}

	static class ImageDownloadThread extends Thread {
		private String url;
		private Handler handler;

		public ImageDownloadThread(String url, Handler handler) {
			setPriority(MIN_PRIORITY);
			this.url = url;
			this.handler = handler;
		}

		@Override
		public void run() {
			if (url != null) {
				Bitmap bitmap = downloadImage(url);
				final Message message = handler.obtainMessage(MESSAGE_FINISH);
				message.getData().putString(PARAM_URL, url);
				message.obj = bitmap;
				handler.sendMessage(message);
			} else {
			}
		}

		private Bitmap downloadImage(String url) {
			HttpClient client = App.me.getHttpClient();
			try {
				HttpGet request = new HttpGet(url);
				HttpResponse response = client.execute(request);
				int statusCode = response.getStatusLine().getStatusCode();
				if (App.DEBUG) {
					Log.d(TAG, "downloadImage response.statusCode="
							+ statusCode);
				}
				if (statusCode == ResponseCode.HTTP_OK) {
					return BitmapFactory.decodeStream(response.getEntity()
							.getContent());
				}
			} catch (IOException e) {
				if (App.DEBUG) {
					Log.d(TAG, e.getMessage());
				}
			}
			return null;
		}
	}

	static class ImageDownloadHandler extends Handler {
		private ImageCache cache;
		private Callbacks callbacks;

		public ImageDownloadHandler(ImageCache cache, Callbacks callbacks) {
			this.cache = cache;
			this.callbacks = callbacks;
		}

		@Override
		public void handleMessage(Message msg) {
			final Bundle bundle = msg.getData();
			String url = bundle.getString(PARAM_URL);
			switch (msg.what) {
			case MESSAGE_FINISH:
				Bitmap bitmap = (Bitmap) msg.obj;
				cache.put(url, bitmap);
				callbacks.call(url, bitmap);
				break;
			case MESSAGE_ERROR:
				break;
			default:
				break;
			}

		}

	}

	static class Callbacks {
		private static final String TAG = "ImageLoaded";
		private ConcurrentHashMap<String, LoaderCallback> mCallbackMap;

		public Callbacks() {
			mCallbackMap = new ConcurrentHashMap<String, LoaderCallback>();
		}

		public void put(String url, LoaderCallback callback) {
			if (StringHelper.isEmpty(url) || callback == null) {
				if (App.DEBUG)
					Log.d(TAG, "url or callback is null");
				return;
			}
			if (App.DEBUG) {
				Log.d(TAG, "ImageLoaded.put url=" + url);
			}
			mCallbackMap.put(url, callback);
		}

		public void call(String url, Bitmap bitmap) {
			LoaderCallback callback = mCallbackMap.get(url);
			if (callback != null) {
				if (url != null) {
					callback.onFinish(url, bitmap);
				} else {
					callback.onError("load image error.");
				}
			}
			mCallbackMap.remove(url);
		}
	}

}
