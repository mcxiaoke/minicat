package com.fanfou.app.cache;

import android.graphics.Bitmap;
import android.os.Handler;
import android.widget.ImageView;

/**
 * @author mcxiaoke
 * @version 1.0 2011.09.23
 * @version 1.1 2011.09.27
 * 
 */
public interface IImageLoader {

	Bitmap getImage(String key, final Handler handler);

	void displayImage(String key, ImageView imageView, int stubId);

	void shutdown();

	void clearCache();

	void clearQueue();

	public interface ImageLoaderCallback {
		void onFinish(String url, Bitmap bitmap);

		void onError(String url, String message);
	}

}
