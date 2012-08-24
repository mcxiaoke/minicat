package com.fanfou.app.hd.imagecache;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Collections;
import java.util.Map;
import java.util.WeakHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.util.Log;
import android.widget.ImageView;

import com.fanfou.app.hd.cache.ImageCache;

public class ImageLoader {

	public static final String TAG = ImageLoader.class.getSimpleName();

	private static final int CORE_POOL_SIZE = 3;

	private static final String ERROR_WRONG_ARGUMENTS = "Wrong arguments were passed to displayImage() method (ImageView reference are required)";
	private static final String ERROR_NOT_INIT = "ImageLoader must be init with configuration before using";
	private static final String ERROR_INIT_CONFIG_WITH_NULL = "ImageLoader configuration can not be initialized with null";
	private static final String ERROR_IMAGEVIEW_CONTEXT = "ImageView context must be of Activity type"
			+ "If you create ImageView in code you must pass your current activity in ImageView constructor (e.g. new ImageView(MyActivity.this); or new ImageView(getActivity())).";

	private static final String LOG_START_DISPLAY_IMAGE_TASK = "Start display image task [%s]";
	private static final String LOG_LOAD_IMAGE_FROM_INTERNET = "Load image from Internet [%s]";
	private static final String LOG_LOAD_IMAGE_FROM_MEMORY_CACHE = "Load image from memory cache [%s]";
	private static final String LOG_LOAD_IMAGE_FROM_DISC_CACHE = "Load image from disc cache [%s]";
	private static final String LOG_CACHE_IMAGE_IN_MEMORY = "Cache image in memory [%s]";
	private static final String LOG_CACHE_IMAGE_ON_DISC = "Cache image on disc [%s]";
	private static final String LOG_DISPLAY_IMAGE_IN_IMAGEVIEW = "Display image in ImageView [%s]";

	private ExecutorService imageLoadingExecutor;
	private ExecutorService cachedImageLoadingExecutor;
	private ImageLoaderListener emptyListener = new EmptyListener();

	private ImageCache memoryCache;

	private Map<ImageView, String> cacheKeyForImageView = Collections
			.synchronizedMap(new WeakHashMap<ImageView, String>());

	private boolean loggingEnabled = false;

	private volatile static ImageLoader instance;

	/** Returns singletone class instance */
	public static ImageLoader getInstance() {
		if (instance == null) {
			synchronized (ImageLoader.class) {
				if (instance == null) {
					instance = new ImageLoader();
				}
			}
		}
		return instance;
	}

	private ImageLoader() {
	}

	/**
	 * Initializes ImageLoader's singletone instance with configuration. Method
	 * shoiuld be called <b>once</b> (each following call will have no effect)<br />
	 * 
	 * @param configuration
	 *            {@linkplain ImageLoaderConfiguration ImageLoader
	 *            configuration}
	 * @throws IllegalArgumentException
	 *             if <b>configuration</b> parameter is null
	 */
	public synchronized void init() {
	}

	/**
	 * Adds display image task to execution pool. Image will be set to ImageView
	 * when it's turn. <br/>
	 * Default {@linkplain DisplayImageOptions display image options} from
	 * {@linkplain ImageLoaderConfiguration configuration} will be used.<br />
	 * <b>NOTE:</b> {@link #init(ImageLoaderConfiguration)} method must be
	 * called before this method call
	 * 
	 * @param url
	 *            Image URL (i.e. "http://site.com/image.png",
	 *            "file:///mnt/sdcard/image.png")
	 * @param imageView
	 *            {@link ImageView} which should display image
	 * @throws RuntimeException
	 *             if {@link #init(ImageLoaderConfiguration)} method wasn't
	 *             called before
	 */
	public void displayImage(String url, ImageView imageView) {
		displayImage(url, imageView, null);
	}

	/**
	 * Adds display image task to execution pool. Image will be set to ImageView
	 * when it's turn.<br />
	 * <b>NOTE:</b> {@link #init(ImageLoaderConfiguration)} method must be
	 * called before this method call
	 * 
	 * @param url
	 *            Image URL (i.e. "http://site.com/image.png",
	 *            "file:///mnt/sdcard/image.png")
	 * @param imageView
	 *            {@link ImageView} which should display image
	 * @param options
	 *            {@linkplain DisplayImageOptions Display image options} for
	 *            image displaying. If <b>null</b> - default display image
	 *            options
	 *            {@linkplain ImageLoaderConfiguration.Builder#defaultDisplayImageOptions(DisplayImageOptions)
	 *            from configuration} will be used.
	 * @param listener
	 *            {@linkplain ImageLoadingListener Listener} for image loading
	 *            process. Listener fires events only if there is no image for
	 *            loading in memory cache. If there is image for loading in
	 *            memory cache then image is displayed at ImageView but listener
	 *            does not fire any event. Listener fires events on UI thread.
	 * @throws RuntimeException
	 *             if {@link #init(ImageLoaderConfiguration)} method wasn't
	 *             called before
	 */
	public void displayImage(String url, ImageView imageView,
			ImageLoaderListener listener) {

		if (imageView == null) {
			Log.w(TAG, ERROR_WRONG_ARGUMENTS);
			return;
		}
		if (listener == null) {
			listener = emptyListener;
		}

		if (url == null || url.length() == 0) {
			cacheKeyForImageView.remove(imageView);
			imageView.setImageBitmap(null);
			return;
		}

		cacheKeyForImageView.put(imageView, url);

		Bitmap bmp = memoryCache.get(url);
		if (bmp != null && !bmp.isRecycled()) {
			if (loggingEnabled)
				Log.i(TAG, String.format(LOG_LOAD_IMAGE_FROM_MEMORY_CACHE, url));
			listener.onLoadStarted();
			imageView.setImageBitmap(bmp);
			listener.onLoadComplete(bmp);
		} else {
			listener.onLoadStarted();
			checkExecutors();

			ImageLoadingInfo imageLoadingInfo = new ImageLoadingInfo(url,
					imageView, listener);
			DisplayImageTask displayImageTask = new DisplayImageTask(
					imageLoadingInfo);
			if (displayImageTask.isImageCachedOnDisc()) {
				cachedImageLoadingExecutor.submit(displayImageTask);
			} else {
				imageLoadingExecutor.submit(displayImageTask);
			}

			imageView.setImageBitmap(null);
		}
	}

	private void checkExecutors() {
		if (imageLoadingExecutor == null || imageLoadingExecutor.isShutdown()) {
			imageLoadingExecutor = Executors.newSingleThreadExecutor();
		}
		if (cachedImageLoadingExecutor == null
				|| cachedImageLoadingExecutor.isShutdown()) {
			cachedImageLoadingExecutor = Executors
					.newFixedThreadPool(CORE_POOL_SIZE);
		}
	}

	/**
	 * Cancel the task of loading and displaying image for passed
	 * {@link ImageView}.
	 * 
	 * @param imageView
	 *            {@link ImageView} for which display task will be cancelled
	 */
	public void cancelDisplayTask(ImageView imageView) {
		cacheKeyForImageView.remove(imageView);
	}

	/**
	 * Stops all running display image tasks, discards all other scheduled tasks
	 */
	public void stop() {
		if (imageLoadingExecutor != null) {
			imageLoadingExecutor.shutdown();
		}
		if (cachedImageLoadingExecutor != null) {
			cachedImageLoadingExecutor.shutdown();
		}
	}

	/**
	 * Clear memory cache.<br />
	 * Do nothing if {@link #init(ImageLoaderConfiguration)} method wasn't
	 * called before.
	 */
	public void clearMemoryCache() {
		memoryCache.clear();
	}

	/**
	 * Clear disc cache.<br />
	 * Do nothing if {@link #init(ImageLoaderConfiguration)} method wasn't
	 * called before.
	 */
	public void clearDiscCache() {
		// TODO
	}

	/** Enables logging of loading image process (in LogCat) */
	public void enableLogging() {
		loggingEnabled = true;
	}

	/** Information about display image task */
	private final class ImageLoadingInfo {
		private final String url;
		private final ImageView imageView;
		private final ImageLoaderListener listener;

		public ImageLoadingInfo(String url, ImageView imageView,
				ImageLoaderListener listener) {
			this.url = url;
			this.imageView = imageView;
			this.listener = listener;
		}

		/**
		 * Whether image URL of this task matches to URL which corresponds to
		 * current ImageView
		 */
		boolean isConsistent() {
			String currentCacheKey = cacheKeyForImageView.get(imageView);
			// Check whether memory cache key (image URL) for current ImageView
			// is actual.
			return url.equals(currentCacheKey);
		}
	}

	/**
	 * Presents display image task. Used to load image from Internet or file
	 * system, decode it to {@link Bitmap}, and display it in {@link ImageView}
	 * through {@link DisplayBitmapTask}.
	 */
	private class DisplayImageTask implements Runnable {

		private final ImageLoadingInfo imageLoadingInfo;

		public DisplayImageTask(ImageLoadingInfo imageLoadingInfo) {
			this.imageLoadingInfo = imageLoadingInfo;
		}

		@Override
		public void run() {
			if (loggingEnabled)
				Log.i(TAG, String.format(LOG_START_DISPLAY_IMAGE_TASK,
						imageLoadingInfo.url));
			if (!imageLoadingInfo.isConsistent()) {
				return;
			}

			Bitmap bmp = loadBitmap();
			if (bmp == null) {
				return;
			}
			if (!imageLoadingInfo.isConsistent()) {
				return;
			}

			memoryCache.put(imageLoadingInfo.url, bmp);

			DisplayBitmapTask displayBitmapTask = new DisplayBitmapTask(
					imageLoadingInfo, bmp);
			tryRunOnUiThread(displayBitmapTask);
		}

		private Bitmap loadBitmap() {
			// File f = discCache.get(imageLoadingInfo.url);
			File f = null;

			Bitmap bitmap = null;
			try {
				// Try to load image from disc cache
				if (f.exists()) {
					if (loggingEnabled)
						Log.i(TAG, String.format(
								LOG_LOAD_IMAGE_FROM_DISC_CACHE,
								imageLoadingInfo.url));
					Bitmap b = decodeImage(f.toURL());
					if (b != null) {
						return b;
					}
				}

				// Load image from Web
				if (loggingEnabled)
					Log.i(TAG, String.format(LOG_LOAD_IMAGE_FROM_INTERNET,
							imageLoadingInfo.url));
				URL imageUrlForDecoding = null;
				if (isImageCachedOnDisc()) {
					if (loggingEnabled)
						Log.i(TAG, String.format(LOG_CACHE_IMAGE_ON_DISC,
								imageLoadingInfo.url));
					saveImageOnDisc(f);
					imageUrlForDecoding = f.toURL();
				} else {
					// imageUrlForDecoding = new URL(imageLoadingInfo.url);
				}

				bitmap = decodeImage(imageUrlForDecoding);

				// diskCache.put(imageLoadingInfo.url, f);
			} catch (IOException e) {
				Log.e(TAG, e.getMessage(), e);
				fireImageLoadingFailedEvent();
				if (f.exists()) {
					f.delete();
				}
			} catch (Throwable e) {
				Log.e(TAG, e.getMessage(), e);
				fireImageLoadingFailedEvent();
			}
			return bitmap;
		}

		private boolean isImageCachedOnDisc() {
			// File f = discCache.get(imageLoadingInfo.url);
			File f = null;
			return f.exists();
		}

		private Bitmap decodeImage(URL imageUrl) throws IOException {
			Bitmap bmp = null;
			// bmp = decoder.decodeFile();
			return bmp;
		}

		private void saveImageOnDisc(File targetFile)
				throws MalformedURLException, IOException {
			// URLConnection conn = new
			// URL(imageLoadingInfo.url).openConnection();
			// conn.setConnectTimeout(configuration.httpConnectTimeout);
			// conn.setReadTimeout(configuration.httpReadTimeout);
			// BufferedInputStream is = new
			// BufferedInputStream(conn.getInputStream());
			// try {
			// OutputStream os = new FileOutputStream(targetFile);
			// try {
			// FileUtils.copyStream(is, os);
			// } finally {
			// os.close();
			// }
			// } finally {
			// is.close();
			// }
		}

		private void fireImageLoadingFailedEvent() {
			tryRunOnUiThread(new Runnable() {
				@Override
				public void run() {
					imageLoadingInfo.listener.onLoadFailed(null);
				}
			});
		}

		private void tryRunOnUiThread(Runnable runnable) {
			Context context = imageLoadingInfo.imageView.getContext();
			if (context instanceof Activity) {
				((Activity) context).runOnUiThread(runnable);
			} else {
				Log.e(TAG, ERROR_IMAGEVIEW_CONTEXT);
				imageLoadingInfo.listener.onLoadFailed(null);
			}
		}
	}

	/**
	 * Used to display bitmap in {@link ImageView}. Must be called on UI thread.
	 */
	private class DisplayBitmapTask implements Runnable {
		private final Bitmap bitmap;
		private final ImageLoadingInfo imageLoadingInfo;

		public DisplayBitmapTask(ImageLoadingInfo imageLoadingInfo,
				Bitmap bitmap) {
			this.bitmap = bitmap;
			this.imageLoadingInfo = imageLoadingInfo;
		}

		public void run() {
			if (imageLoadingInfo.isConsistent()) {
				if (loggingEnabled)
					Log.i(TAG, String.format(LOG_DISPLAY_IMAGE_IN_IMAGEVIEW,
							imageLoadingInfo.url));
				imageLoadingInfo.imageView.setImageBitmap(bitmap);
				imageLoadingInfo.listener.onLoadComplete(bitmap);
			}
		}
	}

	private class EmptyListener implements ImageLoaderListener {
		@Override
		public void onLoadStarted() {
		}

		@Override
		public void onLoadFailed(String message) {
		}

		@Override
		public void onLoadComplete(final Bitmap bitmap) {
		}
	}
}
