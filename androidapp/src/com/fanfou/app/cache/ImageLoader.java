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
 * @version 1.0 20110601
 * 
 */
public class ImageLoader {

	public static final String TAG = ImageLoader.class.getSimpleName();

	private static final String PARAM_URL = "url";
	private static final String PARAM_BITMAP = "image";
	private static final int MESSAGE_FINISH = 0;
	private static final int MESSAGE_ERROR = 1;

	private final ExecutorService executor;
	private final BlockingQueue<String> queue = new LinkedBlockingQueue<String>();
	private ImageCacheManager cache;
	private Handler handler;
	private ImageLoaded loaded;

	private QueueRunnable queuePool;

	public ImageLoader(Context context) {
		this.cache = new ImageCacheManager(context);
		this.loaded = new ImageLoaded();
		this.handler = new ImageDownloadHandler();
		this.queuePool = new QueueRunnable();
		this.executor = App.me.executor;
		this.executor.submit(queuePool);
	}

	public Bitmap getImage(String key, ImageLoaderCallback callback) {
		if (key == null) {
			if (App.DEBUG)
				throw new NullPointerException("图片URL不能为空");
			return null;
		}
		Bitmap bitmap = null;
		if (cache.containsKey(key)) {
			bitmap = cache.get(key);
		} else {
			loaded.put(key, callback);
			addToQueue(key);
		}
		return bitmap;
	}

	public void setHeadImage(String key, final ImageView imageView) {
		if (key == null) {
			if (App.DEBUG)
				throw new NullPointerException("图片URL不能为空");
			return;
		}
//		if (App.DEBUG) {
//			Log.e(TAG, "setHeadImage key=" + key);
//		}
		Bitmap bitmap = null;
		if (cache.containsKey(key)) {
			bitmap = cache.get(key);
		}
		if (bitmap != null) {
			imageView.setImageBitmap(ImageHelper.getRoundedCornerBitmap(bitmap,
					6));
		} else {
			imageView.setImageResource(R.drawable.default_head);
			ImageLoaderCallback callback = new ImageLoaderCallback() {

				@Override
				public void onFinish(String key, final Bitmap bitmap) {

					imageView.setImageBitmap(ImageHelper
							.getRoundedCornerBitmap(bitmap, 6));
					imageView.invalidate();
				}

				@Override
				public void onError(String message) {
				}
			};
			loaded.put(key, callback);
			addToQueue(key);
		}
	}

	/**
	 * 不设置默认图片
	 * 
	 * @param key
	 * @param imageView
	 */
	public void setPhoto(String key, final ImageView imageView) {
		if (key == null) {
			if (App.DEBUG)
				throw new NullPointerException("图片URL不能为空");
			return;
		}
		Bitmap bitmap = null;
		if (cache.containsKey(key)) {
			bitmap = cache.get(key);
		}
		if (bitmap != null) {
			imageView.setImageBitmap(bitmap);
		} else {
			ImageLoaderCallback callback = new ImageLoaderCallback() {

				@Override
				public void onFinish(String key, final Bitmap bitmap) {
					imageView.setImageBitmap(bitmap);
					imageView.invalidate();
				}

				@Override
				public void onError(String message) {
					imageView.setImageResource(R.drawable.photo_icon);
					imageView.invalidate();
				}
			};
			loaded.put(key, callback);
			addToQueue(key);
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

	class QueueRunnable implements Runnable {
		private volatile boolean running = true;

		@Override
		public void run() {
			while (true) {
				String url = null;
				try {
					url = queue.take();
					if(App.DEBUG){
						Log.e(TAG, "take a url fro queue: "+url+", add to download queue");
					}
					executor.submit(new ImageDownloadThread(url));
				} catch (InterruptedException e) {
					e.printStackTrace();
				} finally {
					running = false;
				}
			}
		}
	}

	class ImageDownloadThread extends Thread {
		private String url;

		public ImageDownloadThread(String url) {
			this.url = url;
			setPriority(MIN_PRIORITY);
		}

		@Override
		public void run() {
			if (url != null) {
				Bitmap bitmap = downloadImage(url);
				final Message message = handler.obtainMessage(MESSAGE_FINISH);
				message.getData().putString(PARAM_URL, url);
				message.getData().putParcelable(PARAM_BITMAP, bitmap);
				handler.sendMessage(message);
			} else {
			}
		}
	}

	private Bitmap downloadImage(String url) {
		// Request request = new Request(url);
		// NoAuthClient client = new NoAuthClient();
		// OAuthClient client=new OAuthClient();
//		HttpClient client = new DefaultHttpClient();
		HttpClient client=App.me.client;
		try {
			HttpGet request = new HttpGet(url);
			HttpResponse response = client.execute(request);
			int statusCode = response.getStatusLine().getStatusCode();
			if (App.DEBUG) {
				Log.d(TAG, "downloadImage response.statusCode=" + statusCode);
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

	class ImageDownloadHandler extends Handler {

		@Override
		public void handleMessage(Message msg) {
			final Bundle bundle = msg.getData();
			String url = bundle.getString(PARAM_URL);
			switch (msg.what) {
			case MESSAGE_FINISH:
				Bitmap bitmap = bundle.getParcelable(PARAM_BITMAP);
				cache.put(url, bitmap);
				loaded.call(url, bitmap);
				break;
			case MESSAGE_ERROR:
				break;
			default:
				break;
			}

		}

	}

	public interface ImageLoaderCallback {
		void onFinish(String key, Bitmap bitmap);

		void onError(String message);
	}

	static class ImageLoaded {
		private static final String TAG = "ImageLoaded";
		private ConcurrentHashMap<String, List<ImageLoaderCallback>> mCallbackMap;

		public ImageLoaded() {
			mCallbackMap = new ConcurrentHashMap<String, List<ImageLoaderCallback>>();
		}

		public void put(String url, ImageLoaderCallback callback) {
			if(StringHelper.isEmpty(url)||callback==null){
				if(App.DEBUG)
					Log.d(TAG, "url or callback is null");
				return;
			}
			if (App.DEBUG)
				 Log.d(TAG, "CallbackManager.put url=" + url);
				if (!mCallbackMap.containsKey(url)) {
					mCallbackMap.put(url, new ArrayList<ImageLoaderCallback>());
				}

			List<ImageLoaderCallback> callbacks=mCallbackMap.get(url);
			if(callbacks!=null){
				callbacks.add(callback);
			}
		}

		public void call(String url, Bitmap bitmap) {
			List<ImageLoaderCallback> callbackList = mCallbackMap.get(url);
			if (callbackList != null) {
				for (ImageLoaderCallback callback : callbackList) {
					if (callback != null) {
						if (url != null && bitmap != null) {
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

}
