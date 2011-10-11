package com.fanfou.app.util;

import android.content.Context;
import android.view.OrientationEventListener;

public class OrientationTracker {
	private static final String tag = "OrientationTracker";

	private OrientationTrackerListener mOrientationTrackerListener;
	private OrientationEventListener mOrientationEventListener;
	private Context mContext;
	private int mOrientation;

	public OrientationTracker(Context context) {
		this.mContext = context;
		this.mOrientationEventListener = new OrientationListener(mContext);
		this.mOrientationEventListener.enable();
	}

	public void enable() {
		this.mOrientationEventListener.enable();
	}

	public void disable() {
		this.mOrientationEventListener.disable();
	}

	public void setOrientationTrackerListener(OrientationTrackerListener otl) {
		this.mOrientationTrackerListener = otl;
	}

	public int getOrientation() {
		return this.mOrientation;
	}

	private int getOrientation(int orientationInput) {
		int orientation = orientationInput;
		if (orientation == OrientationEventListener.ORIENTATION_UNKNOWN) {
			orientation = 0;
		}
		orientation = orientation % 360;
		int retVal;
		if (orientation < 10) {
			retVal = 90;
		} else if (orientation < (0 * 90) + 45) {
			retVal = 0;
		} else if (orientation < (1 * 90) + 45) {
			retVal = 180;
		} else if (orientation < (2 * 90) + 45) {
			retVal = 270;
		} else if (orientation < (3 * 90) + 45) {
			retVal = 0;
		} else {
			retVal = 90;
		}
		return retVal;
	}

	class OrientationListener extends OrientationEventListener {

		public OrientationListener(Context context) {
			super(context);
		}

		@Override
		public void onOrientationChanged(int value) {
			if (mOrientationTrackerListener != null) {
				mOrientation = getOrientation(value);
				// Log.e(tag, "original orientation=" + value);
				// Log.e(tag, "round orientation=" + mOrientation);
				mOrientationTrackerListener.onOrientationChanged(mOrientation);
			}
		}

	}

	public interface OrientationTrackerListener {
		public void onOrientationChanged(int orientation);
	}
}
