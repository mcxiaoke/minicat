package org.mcxiaoke.fancooker.imagecache;

import android.graphics.Bitmap;
import android.widget.ImageView;

/**
 * Listener for image loading process
 * 
 * @author Sergey Tarasevich (nostra13[at]gmail[dot]com)
 */
public interface ImageLoaderListener {

	/** Is called when image loading task was started */
	void onLoadStarted();

	/** Is called when an error was occurred during image loading */
	void onLoadFailed(String message);

	/** Is called when image is loaded successfully and displayed in {@link ImageView} */
	void onLoadComplete(final Bitmap bitmap);
}
