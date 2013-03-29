package org.mcxiaoke.fancooker.cache;

import java.io.InputStream;
import java.net.URL;
import java.util.Comparator;
import java.util.Map;
import java.util.WeakHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.PriorityBlockingQueue;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.atomic.AtomicInteger;

import org.mcxiaoke.fancooker.App;
import org.mcxiaoke.fancooker.util.IOHelper;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;
import android.widget.ImageView;


/**
 * @author mcxiaoke
 * @version 1.0 2011.09.23
 * @version 2.0 2011.09.27
 * @version 2.1 2011.11.04
 * @version 2.5 2011.11.23
 * @version 2.6 2011.11.28
 * @version 3.0 2011.11.29
 * @version 4.0 2011.12.02
 * @version 4.1 2011.12.06
 * @version 5.0 2011.12.08
 * @version 5.1 2011.12.09
 * @version 5.2 2011.12.13
 * @version 5.3 2012.02.27
 * 
 */
public final class ImageLoader implements Runnable {

	public static final String TAG = ImageLoader.class.getSimpleName();

	public static final int MESSAGE_FINISH = 0;
	public static final int MESSAGE_ERROR = 1;

	private static final int CORE_POOL_SIZE = 3;

	private final PriorityBlockingQueue<ImageLoaderInfo> mTaskQueue = new PriorityBlockingQueue<ImageLoaderInfo>(
			60, new TaskComparator());
	private final Map<String, ImageView> mViewsMap;
	private final ImageCache mCache;
	private final Handler mHandler;
	private ExecutorService mExecutorService;

	private static final class ImageLoaderHolder {
		private static final ImageLoader INSTANCE = new ImageLoader();
	}

	private ImageLoader() {
		if (App.DEBUG) {
			Log.d(TAG, "ImageLoader new instance.");
		}
		this.mExecutorService = Executors.newFixedThreadPool(CORE_POOL_SIZE,
				new NameCountThreadFactory());
		this.mCache = ImageCache.getInstance();
		this.mViewsMap = new WeakHashMap<String, ImageView>();
		this.mHandler = new InnerHandler();
		this.mExecutorService.execute(this);
	}

	public static ImageLoader getInstance() {
		return ImageLoaderHolder.INSTANCE;
	}

	private void download(final ImageLoaderInfo task) {
		String url = task.url;
		Handler handler = task.handler;
		Bitmap bitmap = mCache.get(url);
		InputStream is = null;
		if (bitmap == null) {
			try {
				// RestResponse res=mClient.get(url, false);
				is = new URL(url).openStream();
				bitmap = BitmapFactory.decodeStream(is);
			} catch (Exception e) {
				Log.e(TAG, "download error:" + e.getMessage());
			} finally {
				IOHelper.forceClose(is);
			}
			if (bitmap != null) {
				mCache.put(url, bitmap);
				if (App.DEBUG) {
					Log.d(TAG, "download put bitmap to cache ");
				}
			}
		}
		if (handler != null) {
			final Message message = handler.obtainMessage();
			message.getData().putString("url", url);
			message.what = bitmap == null ? MESSAGE_ERROR : MESSAGE_FINISH;
			message.obj = bitmap;
			handler.sendMessage(message);
			if (App.DEBUG) {
				Log.d(TAG, "download handle can use, bitmap= " + bitmap);
			}
		} else {
			if (App.DEBUG) {
				Log.d(TAG, "download handle is null, bitmap= " + bitmap);
			}
		}
	}

	public Bitmap getImage(String key, final Handler handler) {
		if (TextUtils.isEmpty(key)) {
			return null;
		}
		Bitmap bitmap = mCache.get(key);
		if (bitmap == null && handler != null) {
			addTask(key, handler);
		}
		return bitmap;
	}

	public void displayImage(String url, final ImageView view, final int iconId) {
		if (TextUtils.isEmpty(url) || view == null) {
			return;
		}
		Bitmap bitmap = mCache.get(url);
		if (bitmap == null) {
			if (iconId > 0) {
				view.setImageResource(iconId);
			}
			addInnerTask(url, view);
		} else {
			view.setImageBitmap(bitmap);
		}
	}

	private void addTask(String url, final Handler handler) {
		ImageLoaderInfo task = new ImageLoaderInfo(url, handler);
		if (mTaskQueue.contains(task)) {
			return;
		}
		mTaskQueue.add(task);
	}

	private void addInnerTask(String url, final ImageView view) {
		ImageLoaderInfo task = new ImageLoaderInfo(url, mHandler);
		if (mTaskQueue.contains(task)) {
			return;
		}
		mTaskQueue.add(task);
		mViewsMap.put(url, view);
	}

	private class InnerHandler extends Handler {

		@Override
		public void handleMessage(Message msg) {
			String url = msg.getData().getString("url");
			final ImageView view = mViewsMap.remove(url);
			if (App.DEBUG) {
				Log.d(TAG, "InnerHandler what=" + msg.what + " url=" + url
						+ " view=" + view);
			}
			switch (msg.what) {
			case MESSAGE_FINISH:
				final Bitmap bitmap = (Bitmap) msg.obj;
				if (bitmap != null && view != null) {
					if (!isExpired(view, url)) {
						if (App.DEBUG) {
							Log.d(TAG, "InnerHandler onFinish() url=" + url);
						}
						view.setImageBitmap(bitmap);
					}
				}
				break;
			case MESSAGE_ERROR:
				break;
			default:
				break;
			}
		}
	}

	private static boolean isExpired(final ImageView view, String url) {
		if (view == null) {
			return true;
		}
		String tag = (String) view.getTag();
		return tag == null || !tag.equals(url);
	}

	public void shutdown() {
		clearQueue();
		clearCache();
	}

	public void clearCache() {
		mCache.clear();
	}

	public void clearQueue() {
		mTaskQueue.clear();
		mViewsMap.clear();
	}

	@Override
	public void run() {
		while (true) {
			if (App.DEBUG) {
				Log.d(TAG, "Daemon is running.");
			}
			try {
				final ImageLoaderInfo task = mTaskQueue.take();
				download(task);
			} catch (InterruptedException e) {
				if (App.DEBUG) {
					e.printStackTrace();
				}
			}
		}
	}

	private static final class ImageLoaderInfo {
		public final String url;
		public final Handler handler;
		public final long timestamp;

		public ImageLoaderInfo(String url, final Handler handler) {
			this.url = url;
			this.handler = handler;
			this.timestamp = System.nanoTime();
		}
	}

	private static final class TaskComparator implements Comparator<ImageLoaderInfo> {
		@Override
		public int compare(ImageLoaderInfo t1, ImageLoaderInfo t2) {
			if (t1.timestamp > t2.timestamp) {
				return -1;
			} else if (t1.timestamp < t2.timestamp) {
				return 1;
			} else {
				return 0;
			}
		}
	}

	private static class NameCountThreadFactory implements ThreadFactory {
		private static AtomicInteger count = new AtomicInteger();

		@Override
		public Thread newThread(Runnable r) {
			Thread t = new Thread(r);
			t.setName("ImageLoaderThread #" + count.getAndIncrement());
			t.setPriority(Thread.MIN_PRIORITY);
			return t;
		}

	}
	
	public static interface ImageLoaderListener{
		public void onLoadComplete(final Bitmap bitmap);
		public void onLoadError(String errorMessage);
	}

}