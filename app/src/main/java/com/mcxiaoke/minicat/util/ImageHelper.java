package com.mcxiaoke.minicat.util;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.Bitmap.Config;
import android.graphics.BitmapFactory;
import android.graphics.BitmapFactory.Options;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.PorterDuff.Mode;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.BaseColumns;
import android.provider.MediaStore.Images;
import android.provider.MediaStore.Images.ImageColumns;
import android.provider.MediaStore.MediaColumns;
import android.util.Log;
import android.view.OrientationEventListener;
import android.view.View;
import com.mcxiaoke.minicat.AppContext;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;


/**
 * @author mcxiaoke
 * @version 3.2 2011.12.26
 */
final public class ImageHelper {
    public static final int IMAGE_QUALITY_HIGH = 90;
    public static final int IMAGE_QUALITY_MEDIUM = 85;
    public static final int IMAGE_QUALITY_LOW = 70;
    public static final int IMAGE_MAX_WIDTH = 2400;// 640 596
    public static final int IMAGE_MAX_WIDTH_2 = 1200;// 640 596
    public static final int IMAGE_MAX_SIZE = 1024 * 1024 * 2;
    public static final int OUTPUT_BUFFER_SIZE = 8196;
    private static final String TAG = ImageHelper.class.getSimpleName();

    /**
     * @param path
     * @param sampleSize 1 = 100%, 2 = 50%(1/2), 4 = 25%(1/4), ...
     * @return
     */
    public static Bitmap getBitmapFromPath(String path, int sampleSize) {
        try {
            BitmapFactory.Options options = new BitmapFactory.Options();
            options.inSampleSize = sampleSize;
            return BitmapFactory.decodeFile(path, options);
        } catch (Exception e) {
            if (AppContext.DEBUG)
                e.printStackTrace();
        }

        return null;
    }

    /**
     * @param bytes
     * @return
     */
    public static Bitmap getBitmapFromBytes(byte[] bytes) {
        try {
            return BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
        } catch (Exception e) {
            if (AppContext.DEBUG)
                e.printStackTrace();
        }

        return null;
    }

    /**
     * @param bitmap
     * @param quality 1 ~ 100
     * @return
     */
    public static byte[] compressBitmap(Bitmap bitmap, int quality) {
        try {
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.PNG, quality, baos);
            return baos.toByteArray();
        } catch (Exception e) {
            if (AppContext.DEBUG)
                e.printStackTrace();
        }

        return null;
    }

    // public static Bitmap resampleImage(String path, int maxDim)
    // throws Exception {
    //
    // BitmapFactory.Options bfo = new BitmapFactory.Options();
    // bfo.inJustDecodeBounds = true;
    // BitmapFactory.decodeFile(path, bfo);
    //
    // BitmapFactory.Options optsDownSample = new BitmapFactory.Options();
    // optsDownSample.inSampleSize = getClosestResampleSize(bfo.outWidth,
    // bfo.outHeight, maxDim);
    //
    // Bitmap bmpt = BitmapFactory.decodeFile(path, optsDownSample);
    //
    // Matrix m = new Matrix();
    //
    // if (bmpt.getWidth() > maxDim || bmpt.getHeight() > maxDim) {
    // BitmapFactory.Options optsScale = getResampling(bmpt.getWidth(),
    // bmpt.getHeight(), maxDim);
    // m.postScale((float) optsScale.outWidth / (float) bmpt.getWidth(),
    // (float) optsScale.outHeight / (float) bmpt.getHeight());
    // }
    //
    // int sdk = new Integer(Build.VERSION.SDK).intValue();
    // if (sdk > 4) {
    // int rotation = getExifOrientation(path);
    // if (rotation != 0) {
    // m.postRotate(rotation);
    // }
    // }
    // return Bitmap.createBitmap(bmpt, 0, 0, bmpt.getWidth(),
    // bmpt.getHeight(), m, true);
    // }

    public static Bitmap resizeBitmap(String filePath, int width, int height) {
        Bitmap bitmap = null;
        Options options = new Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(filePath, options);
        double sampleSize = 0;
        Boolean scaleByHeight = Math.abs(options.outHeight - height) >= Math
                .abs(options.outWidth - width);

        if (options.outHeight * options.outWidth * 2 >= 16384) {
            sampleSize = scaleByHeight ? options.outHeight / height
                    : options.outWidth / width;
            sampleSize = (int) Math.pow(2d,
                    Math.floor(Math.log(sampleSize) / Math.log(2d)));
        }

        options.inJustDecodeBounds = false;
        options.inTempStorage = new byte[128];
        while (true) {
            try {
                options.inSampleSize = (int) sampleSize;
                bitmap = BitmapFactory.decodeFile(filePath, options);
                break;
            } catch (Exception ex) {
                sampleSize = sampleSize * 2;
            }
        }
        return bitmap;
    }

    public static int roundOrientation(int orientationInput) {
        // landscape mode
        int orientation = orientationInput;

        if (orientation == OrientationEventListener.ORIENTATION_UNKNOWN) {
            orientation = 0;
        }

        orientation = orientation % 360;
        int retVal;
        if (orientation < (0 * 90) + 45) {
            retVal = 0;
        } else if (orientation < (1 * 90) + 45) {
            retVal = 90;
        } else if (orientation < (2 * 90) + 45) {
            retVal = 180;
        } else if (orientation < (3 * 90) + 45) {
            retVal = 270;
        } else {
            retVal = 0;
        }

        return retVal;
    }

    public static Uri insertImage(ContentResolver cr, File file, int degree) {
        long size = file.length();
        String name = file.getName();
        ContentValues values = new ContentValues(9);
        values.put(MediaColumns.TITLE, name);
        values.put(MediaColumns.DISPLAY_NAME, name);
        values.put(ImageColumns.DATE_TAKEN, System.currentTimeMillis());
        values.put(MediaColumns.MIME_TYPE, "image/jpeg");
        values.put(ImageColumns.ORIENTATION, degree);
        values.put(MediaColumns.DATA, file.getAbsolutePath());
        values.put(MediaColumns.SIZE, size);
        // return cr.insert(STORAGE_URI, values);
        return null;
    }

    public static Bitmap resampleImage(File file, int maxDim) throws Exception {
        return resampleImage(file.getAbsolutePath(), maxDim);
    }

    public static Bitmap resampleImage(Context context, Uri uri, int maxDim)
            throws Exception {
        String path = IOHelper.getRealPathFromURI(context, uri);
        return resampleImage(path, maxDim);
    }

    public static Bitmap resampleImage(String path, int maxDim)
            throws Exception {

        BitmapFactory.Options bfo = new BitmapFactory.Options();
        bfo.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(path, bfo);

        BitmapFactory.Options optsDownSample = new BitmapFactory.Options();
        optsDownSample.inSampleSize = getClosestResampleSize(bfo.outWidth,
                bfo.outHeight, maxDim);

        Bitmap bmpt = BitmapFactory.decodeFile(path, optsDownSample);

        Matrix m = new Matrix();

        if (bmpt.getWidth() > maxDim || bmpt.getHeight() > maxDim) {
            BitmapFactory.Options optsScale = getResampling(bmpt.getWidth(),
                    bmpt.getHeight(), maxDim);
            m.postScale((float) optsScale.outWidth / (float) bmpt.getWidth(),
                    (float) optsScale.outHeight / (float) bmpt.getHeight());
        }

        int sdk = new Integer(Build.VERSION.SDK).intValue();
        if (sdk > 4) {
            int rotation = getExifOrientation(path);
            if (rotation != 0) {
                m.postRotate(rotation);
            }
        }
        return Bitmap.createBitmap(bmpt, 0, 0, bmpt.getWidth(),
                bmpt.getHeight(), m, true);
    }

    private static BitmapFactory.Options getResampling(int cx, int cy, int max) {
        float scaleVal = 1.0f;
        BitmapFactory.Options bfo = new BitmapFactory.Options();
        if (cx > cy) {
            scaleVal = (float) max / (float) cx;
        } else if (cy > cx) {
            scaleVal = (float) max / (float) cy;
        } else {
            scaleVal = (float) max / (float) cx;
        }
        bfo.outWidth = (int) (cx * scaleVal + 0.5f);
        bfo.outHeight = (int) (cy * scaleVal + 0.5f);
        return bfo;
    }

    private static int getClosestResampleSize(int cx, int cy, int maxDim) {
        int max = Math.max(cx, cy);

        int resample = 1;
        for (resample = 1; resample < Integer.MAX_VALUE; resample++) {
            if (resample * maxDim > max) {
                resample--;
                break;
            }
        }

        if (resample > 0) {
            return resample;
        }
        return 1;
    }

    private static boolean checkFsWritable() {
        // Create a temporary file to see whether a volume is really writeable.
        // It's important not to put it in the root directory which may have a
        // limit on the number of files.
        String directoryName = Environment.getExternalStorageDirectory()
                .toString() + "/DCIM";
        File directory = new File(directoryName);
        if (!directory.isDirectory()) {
            if (!directory.mkdirs()) {
                return false;
            }
        }
        return directory.canWrite();
    }

    public static boolean hasStorage() {
        return hasStorage(true);
    }

    public static boolean hasStorage(boolean requireWriteAccess) {
        String state = Environment.getExternalStorageState();

        if (Environment.MEDIA_MOUNTED.equals(state)) {
            if (requireWriteAccess) {
                boolean writable = checkFsWritable();
                return writable;
            } else {
                return true;
            }
        } else if (!requireWriteAccess
                && Environment.MEDIA_MOUNTED_READ_ONLY.equals(state)) {
            return true;
        }
        return false;
    }

    public static int getExifOrientation(String filepath) {
        int degree = 0;
        ExifInterface exif = null;
        try {
            exif = new ExifInterface(filepath);
        } catch (IOException ex) {
            Log.e(TAG, "cannot read exif", ex);
        }
        if (exif != null) {
            int orientation = exif.getAttributeInt(
                    ExifInterface.TAG_ORIENTATION, -1);
            if (orientation != -1) {
                // We only recognize a subset of orientation tag values.
                switch (orientation) {
                    case ExifInterface.ORIENTATION_ROTATE_90:
                        degree = 90;
                        break;
                    case ExifInterface.ORIENTATION_ROTATE_180:
                        degree = 180;
                        break;
                    case ExifInterface.ORIENTATION_ROTATE_270:
                        degree = 270;
                        break;
                }

            }
        }
        return degree;
    }

    public static Bitmap captureViewToBitmap(View view) {
        Bitmap result = null;

        try {
            result = Bitmap.createBitmap(view.getWidth(), view.getHeight(),
                    Bitmap.Config.RGB_565);
            view.draw(new Canvas(result));
        } catch (Exception e) {
            if (AppContext.DEBUG)
                e.printStackTrace();
        }

        return result;
    }

    public static boolean saveBitmap(Bitmap original,
                                     Bitmap.CompressFormat format, int quality, String outputFilePath) {
        if (original == null)
            return false;
        try {
            return original.compress(format, quality, new FileOutputStream(
                    outputFilePath));
        } catch (Exception e) {
            if (AppContext.DEBUG)
                e.printStackTrace();
        }

        return false;
    }

    public static boolean saveBitmap(Bitmap original,
                                     Bitmap.CompressFormat format, int quality, File outputFile) {
        if (original == null)
            return false;

        try {
            return original.compress(format, quality, new FileOutputStream(
                    outputFile));
        } catch (Exception e) {
            if (AppContext.DEBUG)
                e.printStackTrace();
        }

        return false;
    }

    public static boolean writeToFile(String filePath, Bitmap bitmap) {
        if (bitmap == null || StringHelper.isEmpty(filePath)) {
            return false;
        }
        File file = new File(filePath);
        return writeToFile(file, bitmap);
    }

    public static boolean writeToFile(File file, Bitmap bitmap) {
        if (bitmap == null || file == null || file.exists()) {
            return false;
        }
        BufferedOutputStream bos = null;
        try {
            bos = new BufferedOutputStream(new FileOutputStream(file),
                    OUTPUT_BUFFER_SIZE);
            return bitmap.compress(Bitmap.CompressFormat.JPEG, 100, bos);
        } catch (IOException e) {
            if (AppContext.DEBUG) {
                Log.d(TAG, "writeToFile:" + e.getMessage());
            }
        } finally {
            IOHelper.forceClose(bos);
        }
        return false;
    }

    public static File prepareUploadFile(Context context, File file, int quality, int maxWidth) {
        File destFile = new File(IOHelper.getImageCacheDir(context),
                System.currentTimeMillis() + "_fanfouupload.jpg");
        return compressForUpload(file.getPath(), destFile.getPath(),
                maxWidth, quality);
    }

    public static File prepareProfileImage(Context context, File file) {
        File destFile = new File(IOHelper.getImageCacheDir(context),
                "fanfouprofileimage.jpg");
        return compressForUpload(file.getPath(), destFile.getPath(), 100,
                IMAGE_QUALITY_MEDIUM);
    }

    private static File compressForUpload(String srcFileName,
                                          String destFileName, int maxWidth, int quality) {
        Bitmap bitmap = compressImage(srcFileName, maxWidth);
        if (bitmap == null) {
            return null;
        }
        if (AppContext.DEBUG) {
            Log.d(TAG, "compressForUpload bitmap=(" + bitmap.getWidth() + ","
                    + bitmap.getHeight() + ")");
        }
        FileOutputStream fos = null;
        try {
            Bitmap.CompressFormat format = CompressFormat.JPEG;
            if (srcFileName.toLowerCase().lastIndexOf("png") > -1) {
                format = CompressFormat.PNG;
            }
            if (quality > IMAGE_QUALITY_HIGH) {
                quality = IMAGE_QUALITY_HIGH;
            } else if (quality < IMAGE_QUALITY_LOW) {
                quality = IMAGE_QUALITY_LOW;
            }
            fos = new FileOutputStream(destFileName);
            bitmap.compress(format, quality, fos);
            return new File(destFileName);
        } catch (FileNotFoundException e) {
            if (AppContext.DEBUG) {
                e.printStackTrace();
            }
            return null;
        } finally {
            IOHelper.forceClose(fos);
        }
    }

    public static Bitmap compressImage(String path, int maxDim) {
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(path, options);
        int inSampleSize = 1;
        for (int w = options.outWidth; w > maxDim * 2; w /= 2) {
            inSampleSize += 1;
        }
        if (AppContext.DEBUG) {
            Log.d(TAG, "compressImage original=(" + options.outWidth + ","
                    + options.outHeight + ")");
            Log.d(TAG, "compressImage inSampleSize=" + inSampleSize);
        }
        options.inSampleSize = inSampleSize;
        options.inJustDecodeBounds = false;
        Bitmap bitmap = BitmapFactory.decodeFile(path, options);
        if (bitmap != null) {
            int bw = bitmap.getWidth();
            int bh = bitmap.getHeight();
            Matrix m = new Matrix();
            if (bw > maxDim) {
                float scale = (float) maxDim / (float) bw;
                m.postScale(scale, scale);
                if (AppContext.DEBUG) {
                    Log.d(TAG, "compressImage matrix scale=" + scale);
                }
            }
            int rotation = getExifOrientation(path);
            if (getExifOrientation(path) != 0) {
                m.postRotate(rotation);
            }
            if (AppContext.DEBUG) {
                Log.d(TAG, "compressImage matrix rotation=" + rotation);
                Log.d(TAG, "compressImage bitmap=(" + bw + "," + bh + ")");
            }
            return Bitmap.createBitmap(bitmap, 0, 0, bw, bh, m, true);
        }
        return null;

    }

    /**
     * apply filter to a bitmap
     *
     * @param original
     * @param filter
     * @return filtered bitmap
     */
    // public static Bitmap applyFilter(Bitmap original, FilterBase filter) {
    // return filter.filter(original);
    // }

    /**
     * generate a blurred bitmap from given one
     * <p/>
     * referenced: http://incubator.quasimondo.com/processing/superfastblur.pde
     *
     * @param original
     * @param radius
     * @return
     */
    public static Bitmap getBlurredBitmap(Bitmap original, int radius) {
        if (radius < 1)
            return null;

        int width = original.getWidth();
        int height = original.getHeight();
        int wm = width - 1;
        int hm = height - 1;
        int wh = width * height;
        int div = radius + radius + 1;
        int r[] = new int[wh];
        int g[] = new int[wh];
        int b[] = new int[wh];
        int rsum, gsum, bsum, x, y, i, p, p1, p2, yp, yi, yw;
        int vmin[] = new int[Math.max(width, height)];
        int vmax[] = new int[Math.max(width, height)];
        int dv[] = new int[256 * div];
        for (i = 0; i < 256 * div; i++)
            dv[i] = i / div;

        int[] blurredBitmap = new int[wh];
        original.getPixels(blurredBitmap, 0, width, 0, 0, width, height);

        yw = 0;
        yi = 0;

        for (y = 0; y < height; y++) {
            rsum = 0;
            gsum = 0;
            bsum = 0;
            for (i = -radius; i <= radius; i++) {
                p = blurredBitmap[yi + Math.min(wm, Math.max(i, 0))];
                rsum += (p & 0xff0000) >> 16;
                gsum += (p & 0x00ff00) >> 8;
                bsum += p & 0x0000ff;
            }
            for (x = 0; x < width; x++) {
                r[yi] = dv[rsum];
                g[yi] = dv[gsum];
                b[yi] = dv[bsum];

                if (y == 0) {
                    vmin[x] = Math.min(x + radius + 1, wm);
                    vmax[x] = Math.max(x - radius, 0);
                }
                p1 = blurredBitmap[yw + vmin[x]];
                p2 = blurredBitmap[yw + vmax[x]];

                rsum += ((p1 & 0xff0000) - (p2 & 0xff0000)) >> 16;
                gsum += ((p1 & 0x00ff00) - (p2 & 0x00ff00)) >> 8;
                bsum += (p1 & 0x0000ff) - (p2 & 0x0000ff);
                yi++;
            }
            yw += width;
        }

        for (x = 0; x < width; x++) {
            rsum = gsum = bsum = 0;
            yp = -radius * width;
            for (i = -radius; i <= radius; i++) {
                yi = Math.max(0, yp) + x;
                rsum += r[yi];
                gsum += g[yi];
                bsum += b[yi];
                yp += width;
            }
            yi = x;
            for (y = 0; y < height; y++) {
                blurredBitmap[yi] = 0xff000000 | (dv[rsum] << 16)
                        | (dv[gsum] << 8) | dv[bsum];
                if (x == 0) {
                    vmin[y] = Math.min(y + radius + 1, hm) * width;
                    vmax[y] = Math.max(y - radius, 0) * width;
                }
                p1 = x + vmin[y];
                p2 = x + vmax[y];

                rsum += r[p1] - r[p2];
                gsum += g[p1] - g[p2];
                bsum += b[p1] - b[p2];

                yi += width;
            }
        }

        return Bitmap.createBitmap(blurredBitmap, width, height,
                Bitmap.Config.RGB_565);
    }

    /**
     * 圆角图片
     *
     * @param bitmap
     * @param pixels
     * @return
     */
    public static Bitmap getRoundedCornerBitmap(Bitmap bitmap, int pixels) {
        Bitmap output = Bitmap.createBitmap(bitmap.getWidth(),
                bitmap.getHeight(), Config.ARGB_8888);
        Canvas canvas = new Canvas(output);

        final int color = 0xff424242;
        final Paint paint = new Paint();
        final Rect rect = new Rect(0, 0, bitmap.getWidth(), bitmap.getHeight());
        final RectF rectF = new RectF(rect);
        final float roundPx = pixels;

        paint.setAntiAlias(true);
        canvas.drawARGB(0, 0, 0, 0);
        paint.setColor(color);
        canvas.drawRoundRect(rectF, roundPx, roundPx, paint);

        paint.setXfermode(new PorterDuffXfermode(Mode.SRC_IN));
        canvas.drawBitmap(bitmap, rect, rect, paint);

        return output;
    }

    public static Bitmap loadFromUri(Context context, String uri, int maxW,
                                     int maxH) throws IOException {
        final BitmapFactory.Options options = new BitmapFactory.Options();
        Bitmap bitmap = null;
        options.inScaled = false;
        options.inPreferredConfig = Bitmap.Config.RGB_565;
        BufferedInputStream stream = null;
        if (uri.startsWith(ContentResolver.SCHEME_CONTENT)
                || uri.startsWith(ContentResolver.SCHEME_FILE))
            stream = new BufferedInputStream(context.getContentResolver()
                    .openInputStream(Uri.parse(uri)), 16384);
        if (stream != null) {
            options.inSampleSize = computeSampleSize(stream, maxW, maxH);
            stream = null;
            stream = new BufferedInputStream(context.getContentResolver()
                    .openInputStream(Uri.parse(uri)), 16384);
        } else {
            return null;
        }
        options.inDither = false;
        options.inJustDecodeBounds = false;
        options.inPurgeable = true;
        bitmap = BitmapFactory.decodeStream(stream, null, options);
        return bitmap;
    }

    public static Bitmap loadFromPath(Context context, String path, int maxW,
                                      int maxH) throws IOException {
        final BitmapFactory.Options options = new BitmapFactory.Options();
        options.inScaled = false;
        // options.inPreferredConfig = Bitmap.Config.RGB_565;
        options.inSampleSize = computeSampleSize(path, maxW, maxH);
        // options.inDither = false;
        options.inJustDecodeBounds = false;
        // options.inPurgeable = true;
        Bitmap bitmap = BitmapFactory.decodeFile(path, options);
        return bitmap;
    }

    private static int computeSampleSize(InputStream stream, int maxW, int maxH) {
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeStream(stream, null, options);
        double w = options.outWidth;
        double h = options.outHeight;
        int sampleSize = (int) Math.ceil(Math.max(w / maxW, h / maxH));
        return sampleSize;
    }

    private static int computeSampleSize(String path, int maxW, int maxH) {
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(path, options);
        double w = options.outWidth;
        double h = options.outHeight;
        int sampleSize = (int) Math.ceil(Math.max(w / maxW, h / maxH));
        return sampleSize;
    }

    private static int computeSampleSize(File file, int maxW, int maxH) {
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(file.getPath(), options);
        double w = options.outWidth;
        double h = options.outHeight;
        int sampleSize = (int) Math.ceil(Math.max(w / maxW, h / maxH));
        return sampleSize;
    }

    /**
     * Store a picture that has just been saved to disk in the MediaStore.
     *
     * @param imageFile The File of the picture
     * @return The Uri provided by the MediaStore.
     */
    public static Uri storePicture(Context ctx, File imageFile, String imageName) {
        ContentResolver cr = ctx.getContentResolver();
        imageName = imageName.substring(imageName.lastIndexOf('/') + 1);
        ContentValues values = new ContentValues(7);
        values.put(MediaColumns.TITLE, imageName);
        values.put(MediaColumns.DISPLAY_NAME, imageName);
        values.put(ImageColumns.DESCRIPTION, "");
        values.put(ImageColumns.DATE_TAKEN, System.currentTimeMillis());
        values.put(MediaColumns.MIME_TYPE, "image/jpeg");
        values.put(ImageColumns.ORIENTATION, 0);
        File parentFile = imageFile.getParentFile();
        String path = parentFile.toString().toLowerCase();
        String name = parentFile.getName().toLowerCase();
        values.put(Images.ImageColumns.BUCKET_ID, path.hashCode());
        values.put(Images.ImageColumns.BUCKET_DISPLAY_NAME, name);
        values.put("_data", imageFile.toString());

        Uri uri = cr.insert(Images.Media.EXTERNAL_CONTENT_URI, values);

        return uri;
    }

    public static Uri getContentUriFromFile(Context ctx, File imageFile) {
        Uri uri = null;
        ContentResolver cr = ctx.getContentResolver();
        // Columns to return
        String[] projection = {BaseColumns._ID, MediaColumns.DATA};
        // Look for a picture which matches with the requested path
        // (MediaStore stores the path in column Images.Media.DATA)
        String selection = MediaColumns.DATA + " = ?";
        String[] selArgs = {imageFile.toString()};

        Cursor cursor = cr.query(Images.Media.EXTERNAL_CONTENT_URI, projection,
                selection, selArgs, null);

        if (cursor.moveToFirst()) {

            String id;
            int idColumn = cursor.getColumnIndex(BaseColumns._ID);
            id = cursor.getString(idColumn);
            uri = Uri.withAppendedPath(Images.Media.EXTERNAL_CONTENT_URI, id);
        }
        cursor.close();
        if (uri != null) {
            Log.d(TAG, "Found picture in MediaStore : " + imageFile.toString()
                    + " is " + uri.toString());
        } else {
            Log.d(TAG,
                    "Did not find picture in MediaStore : "
                            + imageFile.toString());
        }
        return uri;
    }

    /**
     * Rotate a bitmap.
     *
     * @param bmp     A Bitmap of the picture.
     * @param degrees Angle of the rotation, in degrees.
     * @return The rotated bitmap, constrained in the source bitmap dimensions.
     */
    public static Bitmap rotate(Bitmap bmp, float degrees) {
        if (degrees % 360 != 0) {
            Log.d(TAG, "Rotating bitmap " + degrees + "°");
            Matrix rotMat = new Matrix();
            rotMat.postRotate(degrees);

            if (bmp != null) {
                Bitmap dst = Bitmap.createBitmap(bmp, 0, 0, bmp.getWidth(),
                        bmp.getHeight(), rotMat, false);

                return dst;
            }
        } else {
            return bmp;
        }
        return null;
    }

    public static boolean checkStorageWritable() {
        String directoryName = Environment.getExternalStorageDirectory()
                .toString();
        File directory = new File(directoryName);
        if (!directory.isDirectory()) {
            if (!directory.mkdirs()) {
                return false;
            }
        }
        File f = new File(directoryName, ".probe");
        try {
            if (!f.createNewFile())
                return false;
            f.delete();
            return true;
        } catch (IOException ex) {
            return false;
        }
    }

    public static boolean isStorageWritable(Context context) {
        boolean mExternalStorageAvailable = false;
        boolean mExternalStorageWriteable = false;
        String state = Environment.getExternalStorageState();

        if (Environment.MEDIA_MOUNTED.equals(state)) {
            // can read and write
            mExternalStorageAvailable = mExternalStorageWriteable = true;
        } else if (Environment.MEDIA_MOUNTED_READ_ONLY.equals(state)) {
            // can only read
            mExternalStorageAvailable = true;
            mExternalStorageWriteable = false;
        } else {
            // can neither read nor write
            mExternalStorageAvailable = mExternalStorageWriteable = false;
        }
        boolean goodmount = mExternalStorageAvailable
                && mExternalStorageWriteable;
        return goodmount;
    }

    public static Bitmap scaleImageFile(Context context, File file, int size) {
        Uri uri = Uri.fromFile(file);
        return scaleImageFromUri(context, uri, size);
    }

    public static Bitmap scaleImageFromUri(Context context, Uri uri, int size) {
        Bitmap bitmap = null;
        InputStream is = null;
        try {
            is = context.getContentResolver().openInputStream(uri);
            BitmapFactory.Options options = new BitmapFactory.Options();
            options.inJustDecodeBounds = true;
            BitmapFactory.decodeStream(is, null, options);
            is.close();
            int scale = 1;
            while ((options.outWidth / scale > size)
                    || (options.outHeight / scale > size)) {
                scale *= 2;
            }
            options.inJustDecodeBounds = false;
            options.inSampleSize = scale;
            is = context.getContentResolver().openInputStream(uri);
            bitmap = BitmapFactory.decodeStream(is, null, options);
        } catch (IOException e) {
        } finally {
            if (is != null) {
                try {
                    is.close();
                } catch (IOException e) {
                }
            }
        }
        return bitmap;
    }

    public static Bitmap rotateImageFile(String filePath) {
        int orientation = getExifOrientation(filePath);
        Bitmap source = BitmapFactory.decodeFile(filePath);
        int sw = source.getWidth();
        int sh = source.getHeight();
        Matrix matrix = new Matrix();
        matrix.setRotate(orientation);
        Bitmap bitmap = Bitmap.createBitmap(source, 0, 0, sw, sh, matrix, true);
        releaseBitmap(source);
        return bitmap;
    }

    public static void releaseBitmap(Bitmap bitmap) {
        if (bitmap != null && !bitmap.isRecycled()) {
            bitmap.recycle();
            bitmap = null;
        }
    }

    public static Bitmap resizeBitmap(Bitmap input, int destWidth,
                                      int destHeight) {
        int srcWidth = input.getWidth();
        int srcHeight = input.getHeight();
        boolean needsResize = false;
        float p;
        if (srcWidth > destWidth || srcHeight > destHeight) {
            needsResize = true;
            if (srcWidth > srcHeight && srcWidth > destWidth) {
                p = (float) destWidth / (float) srcWidth;
                destHeight = (int) (srcHeight * p);
            } else {
                p = (float) destHeight / (float) srcHeight;
                destWidth = (int) (srcWidth * p);
            }
        } else {
            destWidth = srcWidth;
            destHeight = srcHeight;
        }
        if (needsResize) {
            Bitmap output = Bitmap.createScaledBitmap(input, destWidth,
                    destHeight, true);
            return output;
        } else {
            return input;
        }
    }

}
