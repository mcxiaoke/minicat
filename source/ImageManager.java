package com.ifixit.android.imagemanager;

import java.io.File;
import java.io.FileOutputStream;
import java.lang.ref.SoftReference;
import java.net.URL;
import java.net.URLConnection;
import java.util.HashMap;
import java.util.LinkedList;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;
import android.widget.ImageView;

/**
 * Based largely on cacois's example:
 * http://codehenge.net/blog/2011/06/android-development-tutorial-
 * asynchronous-lazy-loading-and-caching-of-listview-images/
 */
public class ImageManager {
   public interface Controller {
      public boolean overrideDisplay(String url, ImageView imageView);

      public void loading(ImageView imageView);

      public boolean displayImage(ImageView imageView, Bitmap bitmap,
       String url);

      public void fail(ImageView imageView);
   }

   private static final int IMAGE_THREAD_PRIORITY = Thread.NORM_PRIORITY - 1;
   private static final int DEFAULT_MAX_STORED_IMAGES = 10;
   private static final int DEFAULT_MAX_LOADING_IMAGES = 9;
   private static final int DEFAULT_MAX_WRITING_IMAGES = 10;
   private static final int DEFAULT_NUM_DOWNLOAD_THREADS = 5;
   private static final int DEFAULT_NUM_WRITE_THREADS = 2;

   private Controller mController;
   private HashMap<String, ImageRef> mLoadingImages;
   private LinkedList<SoftReference<StoredBitmap>> mWriteQueue;
   private LinkedList<StoredBitmap> mRecentImages;
   private File mCacheDir;
   private ImageQueue mImageQueue;
   private Thread[] mDownloadThreads;
   private Thread[] mWriteThreads;
   private int mMaxLoadingImages;
   private int mMaxStoredImages;
   private int mMaxWritingImages;
   private final int mNumDownloadThreads;
   private final int mNumWriteThreads;

   public ImageManager(Context context) {
      this(context, DEFAULT_NUM_DOWNLOAD_THREADS, DEFAULT_NUM_WRITE_THREADS);
   }

   public ImageManager(Context context, int downloadThreads, int writeThreads) {
      mNumDownloadThreads = downloadThreads;
      mNumWriteThreads = writeThreads;
      mWriteQueue = new LinkedList<SoftReference<StoredBitmap>>();
      mRecentImages = new LinkedList<StoredBitmap>();
      mImageQueue = new ImageQueue();
      mDownloadThreads = new Thread[mNumDownloadThreads];
      mWriteThreads = new Thread[mNumWriteThreads];
      mLoadingImages = new HashMap<String, ImageRef>();
      mMaxLoadingImages = DEFAULT_MAX_LOADING_IMAGES;
      mMaxStoredImages = DEFAULT_MAX_STORED_IMAGES;
      mMaxWritingImages = DEFAULT_MAX_WRITING_IMAGES;

      for (int i = 0; i < mDownloadThreads.length; i++) {
         mDownloadThreads[i] = new Thread(new ImageQueueManager());
         mDownloadThreads[i].setPriority(IMAGE_THREAD_PRIORITY);
         mDownloadThreads[i].start();
      }

      for (int i = 0; i < mWriteThreads.length; i ++) {
         mWriteThreads[i] = new Thread(new BitmapWriter());
         mWriteThreads[i].setPriority(IMAGE_THREAD_PRIORITY);
         mWriteThreads[i].start();
      }

      mCacheDir = context.getCacheDir();

      if (!mCacheDir.exists()) {
         mCacheDir.mkdirs();
      }
   }

   public void setController(Controller controller) {
      mController = controller;
   }

   public void displayImage(String url, Activity activity,
    ImageView imageView) {
      if (mController != null && mController.overrideDisplay(url, imageView)) {
         return;
      }

      StoredBitmap storedBitmap = new StoredBitmap(null, url);
      int index = mRecentImages.indexOf(storedBitmap);

      if (index != -1) {
         Bitmap bitmap = mRecentImages.get(index).mBitmap;

         if (bitmap != null) {
            displayImage(imageView, bitmap, url);
         } else if (mController != null) {
            mController.fail(imageView);
         }
      } else {
         if (mController != null) {
            mController.loading(imageView);
         }

         imageView.setTag(R.id.image_tag, url);
         queueImage(url, activity, imageView);
      }
   }

   private void displayImage(ImageView imageView, Bitmap bitmap, String url) {
      if (mController != null && !mController.displayImage(imageView, bitmap,
       url)) {
         imageView.setImageBitmap(bitmap);
      }
   }

   private void queueImage(String url, Activity activity,
    ImageView imageView) {
      ImageRef imageRef;

      synchronized (mLoadingImages) {
         imageRef = mLoadingImages.get(url);
         if (imageRef != null) {
            imageRef.addImage(imageView);
            return;
         }
      }

      synchronized (mImageQueue.imageRefs) {
         for (ImageRef image : mImageQueue.imageRefs) {
            if (image.getUrl().equals(url)) {
               image.addImage(imageView);
               return;
            }
         }

         imageRef = new ImageRef(url, imageView);

         while (mImageQueue.imageRefs.size() >= mMaxLoadingImages) {
            mImageQueue.imageRefs.removeLast();
         }

         mImageQueue.imageRefs.addFirst(imageRef);
         mImageQueue.imageRefs.notify();
      }
   }

   public void setMaxWritingImages(int max) {
      mMaxWritingImages = max;

      synchronized (mWriteQueue) {
         while (mWriteQueue.size() >= mMaxWritingImages) {
            mWriteQueue.removeLast();
         }
      }
   }

   public void setMaxStoredImages(int max) {
      mMaxStoredImages = max;

      synchronized (mRecentImages) {
         while (mRecentImages.size() >= mMaxStoredImages) {
            mRecentImages.removeLast();
         }
      }
   }

   public void setMaxLoadingImages(int max) {
      mMaxLoadingImages = max;

      synchronized (mImageQueue.imageRefs) {
         while (mImageQueue.imageRefs.size() >= mMaxLoadingImages) {
            mImageQueue.imageRefs.removeLast();
         }
      }
   }

   public String getFilePath(String url) {
      File file = new File(mCacheDir, getFileName(url));

      if (file.exists()) {
         return file.getAbsolutePath();
      } else {
         return null;
      }
   }

   private String getFileName(String url) {
      return String.valueOf(url.hashCode()) + ".png";
   }

   private Bitmap getBitmap(String url) {
      String filename = getFileName(url);
      File file = new File(mCacheDir, filename);
      URLConnection connection;
      Bitmap bitmap = BitmapFactory.decodeFile(file.getPath());

      if (bitmap != null) {
         return bitmap;
      }

      try {
         connection = new URL(url).openConnection();
         bitmap = BitmapFactory.decodeStream(connection.getInputStream());
         addToWriteQueue(new StoredBitmap(bitmap, url));

         return bitmap;
      } catch (Exception e) {
         return null;
      }
   }

   private void addToWriteQueue(StoredBitmap storedBitmap) {
      synchronized (mWriteQueue) {
         while (mWriteQueue.size() >= mMaxWritingImages) {
            mWriteQueue.removeLast();
         }

         mWriteQueue.addFirst(new SoftReference<StoredBitmap>(storedBitmap));
         mWriteQueue.notify();
      }
   }

   private void writeFile(Bitmap bitmap, String url) {
      FileOutputStream out = null;
      File file = new File(mCacheDir, getFileName(url));

      try {
         out = new FileOutputStream(file);
         bitmap.compress(Bitmap.CompressFormat.PNG, 80, out);
      } catch (Exception e) {
         Log.e("ImageManager", "writeFile: " + e.getMessage());
      } finally {
         try {
            if (out != null)
               out.close();
         } catch (Exception ex) {}
      }
   }

   private void storeImage(String url, Bitmap bitmap) {
      StoredBitmap storedBitmap = new StoredBitmap(null, url);
      int index = mRecentImages.indexOf(storedBitmap);

      // Already in list, lets move it to the front
      if (index != -1) {
         storedBitmap = mRecentImages.get(index);
         mRecentImages.remove(index);
      } else {
         storedBitmap.mBitmap = bitmap;
      }

      while (mRecentImages.size() >= mMaxStoredImages) {
         mRecentImages.removeLast();
      }

      mRecentImages.addFirst(new StoredBitmap(bitmap, url));
   }

   private class StoredBitmap {
      protected Bitmap mBitmap;
      protected String mUrl;

      public StoredBitmap(Bitmap bitmap, String url) {
         mBitmap = bitmap;
         mUrl = url;
      }

      public boolean equals(Object other) {
         if (other instanceof StoredBitmap) {
            return ((StoredBitmap)other).mUrl.equals(mUrl);
         } else {
            return false;
         }
      }
   }

   private class ImageRef {
      protected String mUrl;
      protected LinkedList<ImageView> mImageViews;

      public ImageRef(String url, ImageView imageView) {
         mUrl = url;
         mImageViews = new LinkedList<ImageView>();
         addImage(imageView);
      }

      public void addImage(ImageView imageView) {
         mImageViews.addFirst(imageView);
      }

      public LinkedList<ImageView> getImageViews() {
         return mImageViews;
      }

      public String getUrl() {
         return mUrl;
      }
   }

   private class ImageQueue {
      public LinkedList<ImageRef> imageRefs = new LinkedList<ImageRef>();
   }

   private class ImageQueueManager implements Runnable {
      @Override
      public void run() {
         ImageRef imageToLoad;
         Bitmap bitmap;
         BitmapDisplayer bitmapDisplayer;
         Activity activity;

         try {
            while (true) {
               synchronized (mImageQueue.imageRefs) {
                  if (mImageQueue.imageRefs.size() == 0) {
                     mImageQueue.imageRefs.wait();
                  }
               }

               synchronized (mImageQueue.imageRefs) {
                  if (mImageQueue.imageRefs.size() == 0) {
                     continue;
                  }

                  imageToLoad = mImageQueue.imageRefs.removeFirst();
                  synchronized (mLoadingImages) {
                     mLoadingImages.put(imageToLoad.getUrl(), imageToLoad);
                  }
               }

               bitmap = getBitmap(imageToLoad.getUrl());
               storeImage(imageToLoad.getUrl(), bitmap);

               bitmapDisplayer = new BitmapDisplayer(bitmap,
                imageToLoad.getImageViews(), imageToLoad.getUrl());
               activity = (Activity)imageToLoad.getImageViews().get(0).
                getContext();
               activity.runOnUiThread(bitmapDisplayer);

               imageToLoad = null;
               bitmap = null;
               activity = null;
               bitmapDisplayer = null;
            }
         }
         catch (InterruptedException e) {}
      }
   }

   private class BitmapWriter implements Runnable {
      @Override
      public void run() {
         StoredBitmap bitmapFile;

         try {
            while (true) {
               synchronized (mWriteQueue) {
                  if (mWriteQueue.size() == 0) {
                     mWriteQueue.wait();
                  }
               }

               synchronized (mWriteQueue) {
                  if (mWriteQueue.size() == 0) {
                     continue;
                  }

                  bitmapFile = mWriteQueue.removeFirst().get();
               }

               if (bitmapFile == null) {
                  continue;
               }

               writeFile(bitmapFile.mBitmap, bitmapFile.mUrl);

               bitmapFile = null;
            }
         } catch (InterruptedException e) {}
      }
   }

   private class BitmapDisplayer implements Runnable {
      Bitmap mBitmap;
      LinkedList<ImageView> mImageViews;
      String mUrl;

      public BitmapDisplayer(Bitmap bitmap, LinkedList<ImageView> imageViews,
       String url) {
         mBitmap = bitmap;
         mImageViews = imageViews;
         mUrl = url;
      }

      public void run() {
         if (mBitmap != null) {
            for (ImageView image : mImageViews) {
               if (image.getTag(R.id.image_tag).equals(mUrl)) {
                  displayImage(image, mBitmap, mUrl);
               }
            }
         } else {
            if (mController != null) {
               for (ImageView image : mImageViews) {
                  mController.fail(image);
               }
            }
         }

         synchronized (mLoadingImages) {
            mLoadingImages.remove(mUrl);
         }
      }
   }
}
