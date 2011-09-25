/**
 * 
 */
package com.fanfou.app.cache;

import android.graphics.Bitmap;
import android.widget.ImageView;

/**
 * @author mcxiaoke
 * @version 1.0 2011.09.23
 *
 */
public interface IImageLoader {
	
	Bitmap load(String key);
	
	Bitmap load(String key, ImageLoaderListener listener);
	
	void set(String key, ImageView imageView);
	
	void set(String key, ImageView imageView, int defaultImageResId);
	

}
