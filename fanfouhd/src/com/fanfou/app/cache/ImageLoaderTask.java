package com.fanfou.app.cache;

import android.widget.ImageView;

/**
 * @author mcxiaoke
 * @version 1.0 2011.09.23
 *
 */
public class ImageLoaderTask {
	
	public final String url;
	public final ImageView imageView;
	
	public ImageLoaderTask(String url, ImageView imageView){
		this.url=url;
		this.imageView=imageView;
	}

}
