package com.fanfou.app.hd.cache;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.lang.ref.SoftReference;
import java.util.concurrent.ConcurrentHashMap;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;

import com.fanfou.app.hd.App;
import com.fanfou.app.hd.util.IOHelper;
import com.fanfou.app.hd.util.ImageHelper;
import com.fanfou.app.hd.util.StringHelper;

/**
 * @author mcxiaoke
 * @version 1.0 2011.06.01
 * @version 1.1 2011.09.23
 * @version 1.5 2011.11.23
 * @version 1.6 2011.11.24
 * @version 1.7 2011.12.05
 * @version 1.8 2011.12.09
 * 
 */
public final class ImageCache implements ICache<Bitmap> {
	private static final String TAG = ImageCache.class.getSimpleName();

	public static final int IMAGE_QUALITY = 100;

	public static ImageCache INSTANCE = null;

	final ConcurrentHashMap<String, SoftReference<Bitmap>> memoryCache;

	private File mCacheDir = null;

	Context mContext;

	private ImageCache(Context context) {
		this.mContext = context;
		this.memoryCache = new ConcurrentHashMap<String, SoftReference<Bitmap>>();
		this.mCacheDir = IOHelper.getImageCacheDir(mContext);
	}

	public static ImageCache getInstance() {
		if (INSTANCE == null) {
			INSTANCE = new ImageCache(App.getApp());
		}
		return INSTANCE;
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
				if (App.DEBUG) {
					Log.d(TAG, "get() bitmap from disk, put to memory cache");
				}
				memoryCache.put(key, new SoftReference<Bitmap>(bitmap));
			}
		}
		return bitmap;
	}

	@Override
	public boolean put(String key, Bitmap bitmap) {
		if (key == null || bitmap == null) {
			return false;
		}
		memoryCache.put(key, new SoftReference<Bitmap>(bitmap));
		boolean result = writeToFile(key, bitmap);
		if (App.DEBUG) {
			Log.d(TAG, "put() put to cache, write to disk result=" + result);
		}
		return result;
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
		String filename = StringHelper.md5(key) + ".jpg";
		File file = new File(mCacheDir, filename);
		FileInputStream fis = null;
		Bitmap bitmap = null;
		try {
			fis = new FileInputStream(file);
			bitmap = BitmapFactory.decodeStream(fis);
			if (App.DEBUG) {
				Log.d(TAG, "loadFromFile() key is " + key);
			}
		} catch (FileNotFoundException e) {
			if (App.DEBUG) {
				Log.d(TAG, "loadFromFile: " + e.getMessage());
			}
		} finally {
			IOHelper.forceClose(fis);
		}
		return bitmap;
	}

	private boolean writeToFile(String key, Bitmap bitmap) {
		String filename = StringHelper.md5(key) + ".jpg";
		return ImageHelper.writeToFile(new File(mCacheDir, filename), bitmap);
	}

	@Override
	public boolean isEmpty() {
		return memoryCache.isEmpty();
	}

}