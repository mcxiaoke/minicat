package com.fanfou.app.hd.cache;

import android.graphics.Bitmap;
import android.os.Handler;
import android.widget.ImageView;

/**
 * @author mcxiaoke
 * @version 1.0 2011.09.23
 * @version 1.1 2011.09.27
 * @version 1.2 2011.12.13
 * 
 */
public interface IImageLoader {

	Bitmap getImage(String key, final Handler handler);

	void displayImage(String key, ImageView imageView, int stubId);

	void shutdown();

	void clearCache();

	void clearQueue();

}
