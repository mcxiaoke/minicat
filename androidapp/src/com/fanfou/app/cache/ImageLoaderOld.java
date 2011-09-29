package com.fanfou.app.cache;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.atomic.AtomicInteger;

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
 * @version 1.0 20110601
 * 
 */
public class ImageLoaderOld implements IImageLoader {

	public static final String TAG = ImageLoaderOld.class.getSimpleName();

	private static final String PARAM_URL = "url";
	private static final int MESSAGE_FINISH = 0;
	private static final int MESSAGE_ERROR = 1;

	public static final int CORE_POOL_SIZE = 5;

	public static final ThreadFactory sThreadFactory = new ThreadFactory() {
		private final AtomicInteger mCount = new AtomicInteger(1);

		@Override
		public Thread newThread(Runnable r) {
			return new Thread(r, "fanfouapp thread #"
					+ mCount.getAndIncrement());
		}
	};

	public final ExecutorService mExecutorService = Executors
			.newFixedThreadPool(CORE_POOL_SIZE, sThreadFactory);

	private final BlockingQueue<String> queue = new LinkedBlockingQueue<String>();
	public ImageCache cache;
	private Handler handler;
	private Callbacks callbacks;

	private QueueRunnable queuePool;

	public ImageLoaderOld(Context context) {
		this.cache = new ImageCache(context);
		this.callbacks = new Callbacks();
		this.handler = new ImageDownloadHandler(cache, callbacks);
		this.queuePool = new QueueRunnable(queue, mExecutorService, handler);
		this.mExecutorService.submit(queuePool);
	}

	@Override
	public Bitmap load(String key, ImageLoaderCallback callback) {
		if (key != null) {
			return loadAndFetch(key, callback);
		}
		return null;

	}

	private Bitmap loadAndFetch(String key, ImageLoaderCallback callback) {
		Bitmap bitmap = null;
		if (cache.containsKey(key)) {
			bitmap = cache.get(key);
		} else {
			callbacks.put(key, callback);
			addToQueue(key);
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

	private Bitmap loadFromLocal(String key) {
		Bitmap bitmap = null;
		if (cache.containsKey(key)) {
			bitmap = cache.get(key);
		}
		return bitmap;
	}

	@Override
	public void set(String key, final ImageView imageView, int iconId) {
		if (key != null) {
			Bitmap bitmap = null;
			if (cache.containsKey(key)) {
				bitmap = cache.get(key);
			}
			if (bitmap != null) {
				imageView.setImageBitmap(ImageHelper.getRoundedCornerBitmap(
						bitmap, 6));
			} else {
				imageView.setImageResource(iconId);
				ImageLoaderCallback callback = new ImageLoaderCallback() {

					@Override
					public void onFinish(String key, Bitmap bitmap) {
						if (bitmap != null) {
							imageView.setImageBitmap(ImageHelper
									.getRoundedCornerBitmap(bitmap, 6));
						}
						imageView.postInvalidate();
					}

					@Override
					public void onError(String message) {
					}
				};
				callbacks.put(key, callback);
				addToQueue(key);
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
	@Override
	public void set(String key, final ImageView imageView) {
		if (key != null) {
			Bitmap bitmap = null;
			if (cache.containsKey(key)) {
				bitmap = cache.get(key);
			}
			if (bitmap != null) {
				imageView.setImageBitmap(bitmap);
			} else {
				ImageLoaderCallback callback = new ImageLoaderCallback() {

					@Override
					public void onFinish(String key, Bitmap bitmap) {
						if (bitmap != null) {
							imageView.setImageBitmap(bitmap);
						} else {
							imageView.setImageResource(R.drawable.photo_icon);
						}
						imageView.postInvalidate();
					}

					@Override
					public void onError(String message) {
						imageView.setImageResource(R.drawable.photo_icon);
						imageView.postInvalidate();
					}
				};
				callbacks.put(key, callback);
				addToQueue(key);
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
					if (App.DEBUG) {
						Log.e(TAG, "take a url fro queue: " + url
								+ ", add to download queue");
					}
					executor.submit(new ImageDownloadThread(url, handler));
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
			} catch (ClientProtocolException e) {
				if (App.DEBUG)
					e.printStackTrace();
			} catch (IOException e) {
				if (App.DEBUG)
					e.printStackTrace();
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
		private ConcurrentHashMap<String, List<ImageLoaderCallback>> mCallbackMap;

		public Callbacks() {
			mCallbackMap = new ConcurrentHashMap<String, List<ImageLoaderCallback>>();
		}
		
		public void clear(){
			mCallbackMap.clear();
		}

		public void put(String url, ImageLoaderCallback callback) {
			if (StringHelper.isEmpty(url) || callback == null) {
				if (App.DEBUG)
					Log.d(TAG, "url or callback is null");
				return;
			}
			if (App.DEBUG) {
				Log.d(TAG, "ImageLoaded.put url=" + url);
			}
			if (!mCallbackMap.containsKey(url)) {
				mCallbackMap.put(url, new ArrayList<ImageLoaderCallback>());
			}

			List<ImageLoaderCallback> callbacks = mCallbackMap.get(url);
			if (callbacks != null) {
				callbacks.add(callback);
			}
		}

		public void call(String url, Bitmap bitmap) {
			List<ImageLoaderCallback> callbackList = mCallbackMap.get(url);
			if (callbackList != null) {
				for (ImageLoaderCallback callback : callbackList) {
					if (callback != null) {
						if (url != null) {
							callback.onFinish(url, bitmap);
						} else {
							callback.onError("load image error.");
						}
					}
				}
				callbackList.clear();
				mCallbackMap.remove(url);
			} else {
				if (App.DEBUG) {
					Log.d(TAG, "callbackList is null");
				}
			}
		}

	}

	@Override
	public void shutdown() {
		mExecutorService.shutdown();
		queue.clear();
		callbacks.clear();
		cache.clear();
	}

	@Override
	public void clearCache() {
		cache.clear();
	}

}
