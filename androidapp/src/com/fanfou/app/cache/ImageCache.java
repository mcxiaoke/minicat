package com.fanfou.app.cache;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.lang.ref.SoftReference;
import java.util.HashMap;
import java.util.Map;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;

import com.fanfou.app.App;
import com.fanfou.app.util.IOHelper;
import com.fanfou.app.util.ImageHelper;
import com.fanfou.app.util.StringHelper;

/**
 * @author mcxiaoke
 * @version 1.0 2011.06.01
 * @version 1.1 2011.09.23
 * 
 */
public class ImageCache implements ICache<Bitmap> {
	private static final String TAG = ImageCache.class.getSimpleName();

	public static final int IMAGE_QUALITY = 100;

	final Map<String, SoftReference<Bitmap>> memoryCache;

	Context mContext;

	public ImageCache(Context context) {
		this.mContext = context;
		this.memoryCache = new HashMap<String, SoftReference<Bitmap>>();
	}

	@Override
	public int getCount() {
		return memoryCache.size();
	}

	@Override
	public Bitmap get(String key) {
		if (StringHelper.isEmpty(key)) {
			return null;
		}
		Bitmap bitmap = null;

		final SoftReference<Bitmap> reference = memoryCache.get(key);
		if (reference != null) {
			bitmap = reference.get();
		}

		if (bitmap == null) {
			bitmap = loadFromFile(key);
			if (bitmap == null) {
				memoryCache.remove(key);
			} else {
				synchronized (this) {
					memoryCache.put(key, new SoftReference<Bitmap>(bitmap));
				}
			}
		}
		return bitmap;
	}

	@Override
	public boolean put(String key, Bitmap bitmap) {
		synchronized (this) {
			memoryCache.put(key, new SoftReference<Bitmap>(bitmap));
		}
		return writeToFile(key, bitmap);
	}

	@Override
	public boolean containsKey(String key) {
		return get(key) != null;
	}

	@Override
	public void clear() {
		String[] files = mContext.fileList();
		for (String file : files) {
			mContext.deleteFile(file);
		}
		synchronized (this) {
			memoryCache.clear();
		}
	}

	protected boolean replace(String oldKey, String key, Bitmap bitmap) {
		boolean result = false;
		put(key, bitmap);
		synchronized (this) {
			result = memoryCache.put(key, new SoftReference<Bitmap>(bitmap)) != null;
			memoryCache.remove(oldKey);
		}
		mContext.deleteFile(StringHelper.md5(oldKey));
		return result;
	}

	private Bitmap loadFromFile(String key) {

		Bitmap bitmap = null;
		String filename = StringHelper.md5(key) + ".jpg";
		File file = new File(IOHelper.getCacheDir(mContext), filename);
		if (!file.exists()) {
			return null;
		}
		FileInputStream fis = null;
		try {
			fis = new FileInputStream(file);
			bitmap = BitmapFactory.decodeStream(fis);
		} catch (FileNotFoundException e) {
			if (App.DEBUG) {
				Log.e(TAG, e.getMessage());
			}
			memoryCache.remove(key);
		} finally {
			IOHelper.forceClose(fis);
		}
		return bitmap;
	}

	private boolean writeToFile(String key, Bitmap bitmap) {
		if (bitmap == null || StringHelper.isEmpty(key)) {
			return false;
		}
		String filename = StringHelper.md5(key) + ".jpg";
		File file = new File(IOHelper.getCacheDir(mContext), filename);
		if (App.DEBUG) {
			Log.i(TAG, "save image: " + filename);
		}
		return ImageHelper.writeToFile(file, bitmap);
	}

}
