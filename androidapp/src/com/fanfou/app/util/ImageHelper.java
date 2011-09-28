package com.fanfou.app.util;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Random;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.BitmapFactory;
import android.graphics.BitmapFactory.Options;
import android.graphics.Canvas;
import android.graphics.LinearGradient;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.PorterDuff.Mode;
import android.graphics.BitmapRegionDecoder;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.Shader;
import android.net.Uri;
import android.provider.MediaStore.Images;
import android.util.Log;
import android.view.View;

import com.fanfou.app.App;

/**
 * @author mcxiaoke
 * @version 1.0 2011.06.05
 * @version 2.0 2011.09.23
 * 
 */
final public class ImageHelper {
	private static final String TAG = ImageHelper.class.getSimpleName();
	public static final int IMAGE_QUALITY_HIGH = 95;
	public static final int IMAGE_QUALITY_MEDIUM = 80;
	public static final int IMAGE_QUALITY_LOW = 70;
	public static final int IMAGE_MAX_WIDTH = 500;// 640 596
	public static final int IMAGE_MAX_HEIGHT = 1192;// 1320 1192
	public static final int IMAGE_ORIGINAL_WIDTH = 800;
	public static final int IMAGE_ORIGINAL_HEIGHT = 1600;

	public static final int READ_BUFFER_SIZE = 32 * 1024; // 32KB

	private static final float EDGE_START = 0.0f;
	private static final float EDGE_END = 4.0f;
	private static final int EDGE_COLOR_START = 0x7F000000;
	private static final int EDGE_COLOR_END = 0x00000000;
	private static final Paint EDGE_PAINT = new Paint();

	private static final int END_EDGE_COLOR_START = 0x00000000;
	private static final int END_EDGE_COLOR_END = 0x4F000000;
	private static final Paint END_EDGE_PAINT = new Paint();

	private static final float FOLD_START = 5.0f;
	private static final float FOLD_END = 13.0f;
	private static final int FOLD_COLOR_START = 0x00000000;
	private static final int FOLD_COLOR_END = 0x26000000;
	private static final Paint FOLD_PAINT = new Paint();

	private static final float SHADOW_RADIUS = 12.0f;
	private static final int SHADOW_COLOR = 0x99000000;
	private static final Paint SHADOW_PAINT = new Paint();

	private static final float PHOTO_BORDER_WIDTH = 3.0f;
	private static final int PHOTO_BORDER_COLOR = 0xffffffff;

	private static final float ROTATION_ANGLE_MIN = 2.5f;
	private static final float ROTATION_ANGLE_EXTRA = 5.5f;

	private static final Random sRandom = new Random();
	private static final Paint sPaint = new Paint(Paint.ANTI_ALIAS_FLAG
			| Paint.FILTER_BITMAP_FLAG);
	private static final Paint sStrokePaint = new Paint(Paint.ANTI_ALIAS_FLAG);

	static {

		sStrokePaint.setStrokeWidth(PHOTO_BORDER_WIDTH);
		sStrokePaint.setStyle(Paint.Style.STROKE);
		sStrokePaint.setColor(PHOTO_BORDER_COLOR);

		Shader shader = new LinearGradient(EDGE_START, 0.0f, EDGE_END, 0.0f,
				EDGE_COLOR_START, EDGE_COLOR_END, Shader.TileMode.CLAMP);
		EDGE_PAINT.setShader(shader);

		shader = new LinearGradient(EDGE_START, 0.0f, EDGE_END, 0.0f,
				END_EDGE_COLOR_START, END_EDGE_COLOR_END, Shader.TileMode.CLAMP);
		END_EDGE_PAINT.setShader(shader);

		shader = new LinearGradient(
				FOLD_START,
				0.0f,
				FOLD_END,
				0.0f,
				new int[] { FOLD_COLOR_START, FOLD_COLOR_END, FOLD_COLOR_START },
				new float[] { 0.0f, 0.5f, 1.0f }, Shader.TileMode.CLAMP);
		FOLD_PAINT.setShader(shader);

		SHADOW_PAINT.setShadowLayer(SHADOW_RADIUS / 2.0f, 0.0f, 0.0f,
				SHADOW_COLOR);
		SHADOW_PAINT.setAntiAlias(true);
		SHADOW_PAINT.setFilterBitmap(true);
		SHADOW_PAINT.setColor(0xFF000000);
		SHADOW_PAINT.setStyle(Paint.Style.FILL);
	}

	/**
	 * 
	 * @param path
	 * @param sampleSize
	 *            1 = 100%, 2 = 50%(1/2), 4 = 25%(1/4), ...
	 * @return
	 */
	public static Bitmap getBitmapFromPath(String path, int sampleSize) {
		try {
			BitmapFactory.Options options = new BitmapFactory.Options();
			options.inSampleSize = sampleSize;
			return BitmapFactory.decodeFile(path, options);
		} catch (Exception e) {
			if (App.DEBUG)
				e.printStackTrace();
		}

		return null;
	}

	/**
	 * 
	 * @param bytes
	 * @return
	 */
	public static Bitmap getBitmapFromBytes(byte[] bytes) {
		try {
			return BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
		} catch (Exception e) {
			if (App.DEBUG)
				e.printStackTrace();
		}

		return null;
	}

	/**
	 * 
	 * @param bitmap
	 * @param quality
	 *            1 ~ 100
	 * @return
	 */
	public static byte[] compressBitmap(Bitmap bitmap, int quality) {
		try {
			ByteArrayOutputStream baos = new ByteArrayOutputStream();
			bitmap.compress(Bitmap.CompressFormat.PNG, quality, baos);
			return baos.toByteArray();
		} catch (Exception e) {
			if (App.DEBUG)
				e.printStackTrace();
		}

		return null;
	}

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

	public static Bitmap getThumb(File file, int size) {
		InputStream input = null;

		try {
			input = new FileInputStream(file);
			BitmapFactory.Options options = new BitmapFactory.Options();
			options.inJustDecodeBounds = true;
			BitmapFactory.decodeStream(input, null, options);
			input.close();
			int scale = 1;
			while ((options.outWidth / scale > size)
					|| (options.outHeight / scale > size)) {
				scale *= 2;
			}

			options.inJustDecodeBounds = false;
			options.inSampleSize = scale;

			input = new FileInputStream(file);

			return BitmapFactory.decodeStream(input, null, options);
		} catch (IOException e) {
			return null;
		} finally {
			if (input != null) {
				try {
					input.close();
				} catch (IOException e) {
				}
			}
		}
	}

	public static Bitmap getThumb(Context context, Uri uri, int width,
			int height) {
		InputStream input = null;
		try {
			input = context.getContentResolver().openInputStream(uri);
			BitmapFactory.Options options = new BitmapFactory.Options();
			options.inJustDecodeBounds = true;
			BitmapFactory.decodeStream(input, null, options);
			input.close();
			int scale = 1;
			while ((options.outHeight / scale > height)) {
				scale *= 2;
			}
			options.inJustDecodeBounds = false;
			options.inSampleSize = scale;
			input = context.getContentResolver().openInputStream(uri);

			Bitmap src = BitmapFactory.decodeStream(input, null, options);
			Bitmap bitmap = Bitmap
					.createScaledBitmap(src, width, height, false);
			src.recycle();
			return bitmap;
		} catch (IOException e) {
			return null;
		} finally {
			IOHelper.forceClose(input);
		}
	}

	public static Bitmap captureViewToBitmap(View view) {
		Bitmap result = null;

		try {
			result = Bitmap.createBitmap(view.getWidth(), view.getHeight(),
					Bitmap.Config.RGB_565);
			view.draw(new Canvas(result));
		} catch (Exception e) {
			if (App.DEBUG)
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
			if (App.DEBUG)
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
			if (App.DEBUG)
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
		if (bitmap == null) {
			return false;
		}
		boolean result = false;
		BufferedOutputStream bos = null;
		try {
			if (!file.exists()) {
				bos = new BufferedOutputStream(new FileOutputStream(file));
				bitmap.compress(Bitmap.CompressFormat.JPEG, 100, bos);
			}
			result = true;
		} catch (IOException e) {
			if (App.DEBUG)
				e.printStackTrace();
		} finally {
			IOHelper.forceClose(bos);
		}
		return result;
	}

	public static File prepareUploadFile(Context context, File file, int quality) {
		File destFile = new File(IOHelper.getCacheDir(context),
				"fanfouupload.jpg");
		return getResizedBitmapFile(file, destFile, IMAGE_MAX_WIDTH,
				IMAGE_MAX_HEIGHT, quality);
	}

	public static File getResizedBitmapFile(File file, File destFile,
			int width, int height, int quality) {
		if (quality > 100) {
			quality = 100;
		} else if (quality < 60) {
			quality = 60;
		}
		try {
			BitmapFactory.Options opt = new BitmapFactory.Options();
			opt.inJustDecodeBounds = true;
			BitmapFactory.decodeFile(file.getAbsolutePath(), opt);

			int outWidth = opt.outWidth;
			int outHeight = opt.outHeight;
			int sampleSize = 1;
			if (outWidth > IMAGE_MAX_WIDTH * 2) {
				sampleSize = outWidth / IMAGE_MAX_WIDTH;
			}
			if (App.DEBUG) {
				Log.d(TAG, "getResizedBitmapFile() srcFile.width=" + outWidth
						+ " srcFile.height=" + outHeight + " inSampleSize="
						+ sampleSize);
			}
			opt.inJustDecodeBounds = false;
			opt.inSampleSize = sampleSize;
			Bitmap bitmap = BitmapFactory.decodeFile(file.getAbsolutePath(),
					opt);
			bitmap.compress(Bitmap.CompressFormat.JPEG, quality,
					new FileOutputStream(destFile));
			if (App.DEBUG) {
				Log.d(TAG,
						"getResizedBitmapFile() srcFile="
								+ file.getAbsolutePath() + " destFile="
								+ destFile.getAbsolutePath());
				Log.d(TAG,
						"getResizedBitmapFile() quality=" + quality + " width="
								+ bitmap.getWidth() + " height="
								+ bitmap.getHeight());
			}
			bitmap.recycle();
		} catch (Exception e) {
			if (App.DEBUG)
				e.printStackTrace();
		}
		return destFile;
	}

	public static Bitmap getResizedBitmap(File file, int width, int height) {
		try {
			BitmapFactory.Options getSizeOpt = new BitmapFactory.Options();
			getSizeOpt.inJustDecodeBounds = true;
			BitmapFactory.decodeStream(new FileInputStream(file), null,
					getSizeOpt);
			int sampleSize = 1;
			int outWidth = getSizeOpt.outWidth;
			int outHeight = getSizeOpt.outHeight;
			while ((outWidth / sampleSize) > width
					|| (outHeight / sampleSize) > height) {
				sampleSize *= 2;
			}

			BitmapFactory.Options resizeOpt = new BitmapFactory.Options();
			resizeOpt.inSampleSize = sampleSize;
			return BitmapFactory.decodeStream(new FileInputStream(file), null,
					resizeOpt);
		} catch (Exception e) {
			if (App.DEBUG)
				e.printStackTrace();
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
	 * 
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

	/**
	 * Rotate specified Bitmap by a random angle. The angle is either negative
	 * or positive, and ranges, in degrees, from 2.5 to 8. After rotation a
	 * frame is overlaid on top of the rotated image.
	 * 
	 * This method is not thread safe.
	 * 
	 * @param bitmap
	 *            The Bitmap to rotate and apply a frame onto.
	 * 
	 * @return A new Bitmap whose dimension are different from the original
	 *         bitmap.
	 */
	public static Bitmap rotateAndFrame(Bitmap bitmap) {
		final boolean positive = sRandom.nextFloat() >= 0.5f;
		final float angle = (ROTATION_ANGLE_MIN + sRandom.nextFloat()
				* ROTATION_ANGLE_EXTRA)
				* (positive ? 1.0f : -1.0f);
		final double radAngle = Math.toRadians(angle);

		final int bitmapWidth = bitmap.getWidth();
		final int bitmapHeight = bitmap.getHeight();

		final double cosAngle = Math.abs(Math.cos(radAngle));
		final double sinAngle = Math.abs(Math.sin(radAngle));

		final int strokedWidth = (int) (bitmapWidth + 2 * PHOTO_BORDER_WIDTH);
		final int strokedHeight = (int) (bitmapHeight + 2 * PHOTO_BORDER_WIDTH);

		final int width = (int) (strokedHeight * sinAngle + strokedWidth
				* cosAngle);
		final int height = (int) (strokedWidth * sinAngle + strokedHeight
				* cosAngle);

		final float x = (width - bitmapWidth) / 2.0f;
		final float y = (height - bitmapHeight) / 2.0f;

		final Bitmap decored = Bitmap.createBitmap(width, height,
				Bitmap.Config.ARGB_8888);
		final Canvas canvas = new Canvas(decored);

		canvas.rotate(angle, width / 2.0f, height / 2.0f);
		canvas.drawBitmap(bitmap, x, y, sPaint);
		canvas.drawRect(x, y, x + bitmapWidth, y + bitmapHeight, sStrokePaint);

		return decored;
	}

	/**
	 * Scales the specified Bitmap to fit within the specified dimensions. After
	 * scaling, a frame is overlaid on top of the scaled image.
	 * 
	 * This method is not thread safe.
	 * 
	 * @param bitmap
	 *            The Bitmap to scale to fit the specified dimensions and to
	 *            apply a frame onto.
	 * @param width
	 *            The maximum width of the new Bitmap.
	 * @param height
	 *            The maximum height of the new Bitmap.
	 * 
	 * @return A scaled version of the original bitmap, whose dimension are less
	 *         than or equal to the specified width and height.
	 */
	public static Bitmap scaleAndFrame(Bitmap bitmap, int width, int height) {
		final int bitmapWidth = bitmap.getWidth();
		final int bitmapHeight = bitmap.getHeight();

		final float scale = Math.min((float) width / (float) bitmapWidth,
				(float) height / (float) bitmapHeight);

		final int scaledWidth = (int) (bitmapWidth * scale);
		final int scaledHeight = (int) (bitmapHeight * scale);

		final Bitmap decored = Bitmap.createScaledBitmap(bitmap, scaledWidth,
				scaledHeight, true);
		final Canvas canvas = new Canvas(decored);

		final int offset = (int) (PHOTO_BORDER_WIDTH / 2);
		sStrokePaint.setAntiAlias(false);
		canvas.drawRect(offset, offset, scaledWidth - offset - 1, scaledHeight
				- offset - 1, sStrokePaint);
		sStrokePaint.setAntiAlias(true);
		return decored;
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

	private static int computeSampleSize(InputStream stream, int maxW, int maxH) {
		BitmapFactory.Options options = new BitmapFactory.Options();
		options.inJustDecodeBounds = true;
		BitmapFactory.decodeStream(stream, null, options);
		double w = options.outWidth;
		double h = options.outHeight;
		int sampleSize = (int) Math.ceil(Math.max(w / maxW, h / maxH));
		return sampleSize;
	}

	/**
	 * Store a picture that has just been saved to disk in the MediaStore.
	 * 
	 * @param imageFile
	 *            The File of the picture
	 * @return The Uri provided by the MediaStore.
	 */
	public static Uri storePicture(Context ctx, File imageFile, String imageName) {
		ContentResolver cr = ctx.getContentResolver();
		imageName = imageName.substring(imageName.lastIndexOf('/') + 1);
		ContentValues values = new ContentValues(7);
		values.put(Images.Media.TITLE, imageName);
		values.put(Images.Media.DISPLAY_NAME, imageName);
		values.put(Images.Media.DESCRIPTION, "");
		values.put(Images.Media.DATE_TAKEN, System.currentTimeMillis());
		values.put(Images.Media.MIME_TYPE, "image/jpeg");
		values.put(Images.Media.ORIENTATION, 0);
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
		String[] projection = { Images.Media._ID, Images.Media.DATA };
		// Look for a picture which matches with the requested path
		// (MediaStore stores the path in column Images.Media.DATA)
		String selection = Images.Media.DATA + " = ?";
		String[] selArgs = { imageFile.toString() };

		Cursor cursor = cr.query(Images.Media.EXTERNAL_CONTENT_URI, projection,
				selection, selArgs, null);

		if (cursor.moveToFirst()) {

			String id;
			int idColumn = cursor.getColumnIndex(Images.Media._ID);
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
     * @param bmp
     *            A Bitmap of the picture.
     * @param degrees
     *            Angle of the rotation, in degrees.
     * @return The rotated bitmap, constrained in the source bitmap dimensions.
     */
    public static Bitmap rotate(Bitmap bmp, float degrees) {
        if (degrees % 360 != 0) {
            Log.d(TAG, "Rotating bitmap " + degrees + "°");
            Matrix rotMat = new Matrix();
            rotMat.postRotate(degrees);

            if (bmp != null) {
                Bitmap dst = Bitmap.createBitmap(bmp, 0, 0, bmp.getWidth(), bmp
                        .getHeight(), rotMat, false);

                return dst;
            }
        } else {
            return bmp;
        }
        return null;
    }

}
