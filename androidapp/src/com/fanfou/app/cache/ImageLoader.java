package com.fanfou.app.cache;

import java.io.IOException;
import java.io.Serializable;
import java.util.Comparator;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.PriorityBlockingQueue;

import org.apache.http.HttpResponse;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;
import android.widget.ImageView;

import com.fanfou.app.App;
import com.fanfou.app.App.ApnType;
import com.fanfou.app.http.NetClient;
import com.fanfou.app.util.ImageHelper;

/**
 * @author mcxiaoke
 * @version 1.0 2011.09.23
 * @version 2.0 2011.09.27
 * @version 2.1 2011.11.04
 * @version 2.5 2011.11.23
 * @version 2.6 2011.11.28
 * @version 3.0 2011.11.29
 * @version 4.0 2011.12.02
 * 
 */
public class ImageLoader implements IImageLoader {

	public static final String TAG = ImageLoader.class.getSimpleName();

	private static final String EXTRA_TASK = "task";
	private static final String EXTRA_BITMAP = "bitmap";
	private static final int MESSAGE_FINISH = 0;
	private static final int MESSAGE_ERROR = 1;
	private static final int CORE_POOL_SIZE = 2;

//	private final ExecutorService mExecutorService = Executors.newSingleThreadExecutor();
	private final BlockingQueue<ImageLoaderTask> mTaskQueue = new PriorityBlockingQueue<ImageLoaderTask>(
			20, new ImageLoaderTaskComparator());
	// private final BlockingQueue<ImageLoaderTask> mTaskQueue = new
	// LinkedBlockingQueue<ImageLoader.ImageLoaderTask>();
	private final ConcurrentHashMap<ImageLoaderTask, ImageLoaderCallback> mCallbackMap = new ConcurrentHashMap<ImageLoaderTask, ImageLoaderCallback>();
	public final ImageCache mCache;
	private final Handler mHandler;

	private NetClient mClient;

	private Daemon mDaemon;

	private static ImageLoader INSTANCE = null;

	private ImageLoader(Context context) {
		if(App.DEBUG){
			Log.d(TAG, "ImageLoader new instance.");
		}
		this.mCache = ImageCache.getInstance(context);
		this.mClient = NetClient.newInstance();
		this.mHandler = new ImageDownloadHandler(mCallbackMap);
		this.mDaemon = new Daemon();
		this.mDaemon.start();
	}

	public static ImageLoader getInstance(Context context) {
		if (INSTANCE == null) {
			INSTANCE = new ImageLoader(context.getApplicationContext());
		}
		return INSTANCE;
	}

	@Override
	public Bitmap load(String key, ImageLoaderCallback callback) {
		Bitmap bitmap = null;
		if (!TextUtils.isEmpty(key)) {
			if (mCache.containsKey(key)) {
				bitmap = mCache.get(key);
			}
			if (bitmap == null) {
				if (callback != null) {
					addToQueue(new ImageLoaderTask(key, null), callback);
				}
			}
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

	private final class Daemon extends Thread {

		public Daemon() {
			super("ImageLoader Daemon");
			setPriority(MIN_PRIORITY);
		}

		@Override
		public synchronized void start() {
			super.start();
			if(App.DEBUG){
				Log.d(TAG, "Daemon Thread start().");
			}
		}

		public void run() {
			while (true) {
				try {
					if (App.DEBUG) {
						Log.d(TAG,"Daemon Thread isRunning");
					}
					final ImageLoaderTask task = mTaskQueue.take();
						 handleDownloadTask(mCache, task, mClient, mHandler);
				} catch (InterruptedException e) {
					if (App.DEBUG) {
						Log.d(TAG, "Daemon Thread is interrupted:" + e.getMessage());
					}
//					break;
				}
			}
		}
	}

	@SuppressWarnings("unused")
	private static final class Worker implements Runnable {
		private final ImageLoaderTask task;
		private final Handler handler;
		private final ImageCache cache;
		private final NetClient conn;

		public Worker(final ImageLoaderTask task, final Handler handler,
				final ImageCache cache, final NetClient conn) {
			this.task = task;
			this.handler = handler;
			this.cache = cache;
			this.conn = conn;
		}

		public void run() {
			handleDownloadTask(cache, task, conn, handler);
		}
	}

	private static void handleDownloadTask(final ImageCache cache,
			final ImageLoaderTask task, final NetClient conn,
			final Handler handler) {
		if (!cache.containsKey(task.url)) {
			Bitmap bitmap = null;
			try {
				bitmap = downloadImage(conn, task.url);
			} catch (IOException e) {
				if (App.DEBUG) {
					e.printStackTrace();
				}
			}
			final Message message = handler.obtainMessage();
			if (bitmap != null) {
				cache.put(task.url, bitmap);
				message.what = MESSAGE_FINISH;
				message.getData().putParcelable(EXTRA_BITMAP, bitmap);
			} else {
				message.what = MESSAGE_ERROR;
			}
			message.getData().putSerializable(EXTRA_TASK, task);
			handler.sendMessage(message);
		}
	}

	private static Bitmap downloadImage(NetClient conn, String url)
			throws IOException {
		HttpResponse response = conn.get(url);
		int statusCode = response.getStatusLine().getStatusCode();
		if (App.DEBUG) {
			Log.d(TAG, "downloadImage() statusCode=" + statusCode + " [" + url
					+ "]");
		}
		Bitmap bitmap = null;
		if (statusCode == 200) {
			bitmap = BitmapFactory.decodeStream(response.getEntity()
					.getContent());
		}
		return bitmap;
	}

	private static class ImageDownloadHandler extends Handler {
		private ConcurrentHashMap<ImageLoaderTask, ImageLoaderCallback> map;

		public ImageDownloadHandler(
				final ConcurrentHashMap<ImageLoaderTask, ImageLoaderCallback> map) {
			this.map = map;
		}

		@Override
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case MESSAGE_FINISH:
				final ImageLoaderTask task = (ImageLoaderTask) msg.getData()
						.getSerializable(EXTRA_TASK);
				final ImageLoaderCallback callback = map.get(task);
				final Bitmap bitmap = (Bitmap) msg.getData().getParcelable(
						EXTRA_BITMAP);
				if (callback != null) {
					callback.onFinish(task.url, bitmap);
				}
				map.remove(task);
				break;
			case MESSAGE_ERROR:
				final ImageLoaderTask task2 = (ImageLoaderTask) msg.getData()
						.getSerializable(EXTRA_TASK);
				map.remove(task2);
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
						Log.d(TAG, "InternelCallback.onFinish() url=" + url);
					}
					imageView.setImageBitmap(bitmap);
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
		mTaskQueue.clear();
		mCallbackMap.clear();
		mCache.clear();
		if (App.DEBUG) {
			Log.d(TAG, "shutdown()");
			Log.d(TAG, "mTaskQueue.isEmpty = " + mTaskQueue.isEmpty());
			Log.d(TAG, "mCallbackMap.isEmpty = " + mCallbackMap.isEmpty());
			Log.d(TAG, "mCache.isEmpty = " + mCache.isEmpty());
			Log.d(TAG, "mDaemon.isAlive = " + mDaemon.isAlive());
		}
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
