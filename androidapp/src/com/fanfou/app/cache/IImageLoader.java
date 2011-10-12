/**
 * 
 */
package com.fanfou.app.cache;

import java.io.File;

import android.graphics.Bitmap;
import android.widget.ImageView;

/**
 * @author mcxiaoke
 * @version 1.0 2011.09.23
 * @version 1.1 2011.09.27
 * 
 */
public interface IImageLoader {

	Bitmap load(String key);

	Bitmap load(String key, ImageLoaderCallback callback);
	
	File loadFile(String key);

	void set(String key, ImageView imageView);

	void set(String key, ImageView imageView, int defaultImageResId);

	void shutdown();

	void clearCache();

	public interface ImageLoaderCallback {
		void onFinish(String url, Bitmap bitmap);

		void onError(String message);
	}

}
