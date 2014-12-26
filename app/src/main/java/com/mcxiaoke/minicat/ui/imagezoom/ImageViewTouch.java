package com.mcxiaoke.minicat.ui.imagezoom;

import android.content.Context;
import android.util.AttributeSet;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;
import android.view.ViewConfiguration;


/**
 * @author mcxiaoke
 * @version 1.1 2011.11.22
 */
public class ImageViewTouch extends ImageViewTouchBase {

    static final float MIN_ZOOM = 0.9f;
    protected ScaleGestureDetector mScaleDetector;
    protected GestureDetector mGestureDetector;
    protected int mTouchSlop;
    protected float mCurrentScaleFactor;
    protected float mScaleFactor;
    protected int mDoubleTapDirection;
    protected GestureListener mGestureListener;
    protected ScaleListener mScaleListener;

    public ImageViewTouch(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    protected void init() {
        super.init();
        mTouchSlop = ViewConfiguration.getTouchSlop();
        mGestureListener = new GestureListener();
        mScaleListener = new ScaleListener();

        // compatibility for api 7
        mScaleDetector = new ScaleGestureDetector(getContext(), mScaleListener);
        // mGestureDetector = new GestureDetector( getContext(),
        // mGestureListener, null, true );// api>=8
        mGestureDetector = new GestureDetector(getContext(), mGestureListener,
                null);
        mCurrentScaleFactor = 1f;
        mDoubleTapDirection = 1;
    }

    @Override
    public void setImageRotateBitmapReset(RotateBitmap bitmap, boolean reset) {
        super.setImageRotateBitmapReset(bitmap, reset);
        mScaleFactor = getMaxZoom() / 3;
    }

    @Override
    protected void onZoom(float scale) {
        super.onZoom(scale);
        if (!mScaleDetector.isInProgress())
            mCurrentScaleFactor = scale;
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        mScaleDetector.onTouchEvent(event);
        if (!mScaleDetector.isInProgress())
            mGestureDetector.onTouchEvent(event);
        int action = event.getAction();
        switch (action & MotionEvent.ACTION_MASK) {
            case MotionEvent.ACTION_UP:
                if (getScale() < 1f) {
                    zoomTo(1f, 50);
                }
                break;
        }
        return true;
    }

    protected float onDoubleTapPost(float scale, float maxZoom) {
        if (mDoubleTapDirection == 1) {
            if ((scale + (mScaleFactor * 2)) <= maxZoom) {
                return scale + mScaleFactor;
            } else {
                mDoubleTapDirection = -1;
                return maxZoom;
            }
        } else {
            mDoubleTapDirection = 1;
            return 1f;
        }
    }

    class GestureListener extends GestureDetector.SimpleOnGestureListener {

        @Override
        public boolean onScroll(MotionEvent e1, MotionEvent e2,
                                float distanceX, float distanceY) {
            if (e1 == null || e2 == null)
                return false;
            if (e1.getPointerCount() > 1 || e2.getPointerCount() > 1)
                return false;
            if (mScaleDetector.isInProgress())
                return false;
            if (getScale() == 1f)
                return false;
            scrollBy(-distanceX, -distanceY);
            invalidate();
            return super.onScroll(e1, e2, distanceX, distanceY);
        }

        @Override
        public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX,
                               float velocityY) {
            if (e1.getPointerCount() > 1 || e2.getPointerCount() > 1)
                return false;
            if (mScaleDetector.isInProgress())
                return false;

            float diffX = e2.getX() - e1.getX();
            float diffY = e2.getY() - e1.getY();

            if (Math.abs(velocityX) > 800 || Math.abs(velocityY) > 800) {
                scrollBy(diffX / 2, diffY / 2, 300);
                invalidate();
            }
            return super.onFling(e1, e2, velocityX, velocityY);
        }

        @Override
        public boolean onDoubleTap(MotionEvent e) {
            float scale = getScale();
            float targetScale = scale;
            targetScale = onDoubleTapPost(scale, getMaxZoom());
            targetScale = Math.min(getMaxZoom(),
                    Math.max(targetScale, MIN_ZOOM));
            mCurrentScaleFactor = targetScale;
            zoomTo(targetScale, e.getX(), e.getY(), 200);
            invalidate();
            return super.onDoubleTap(e);
        }
    }

    class ScaleListener extends
            ScaleGestureDetector.SimpleOnScaleGestureListener {

        @SuppressWarnings("unused")
        @Override
        public boolean onScale(ScaleGestureDetector detector) {
            float span = detector.getCurrentSpan() - detector.getPreviousSpan();
            float targetScale = mCurrentScaleFactor * detector.getScaleFactor();
            if (true) {
                targetScale = Math.min(getMaxZoom(),
                        Math.max(targetScale, MIN_ZOOM));
                zoomTo(targetScale, detector.getFocusX(), detector.getFocusY());
                mCurrentScaleFactor = Math.min(getMaxZoom(),
                        Math.max(targetScale, MIN_ZOOM));
                mDoubleTapDirection = 1;
                invalidate();
                return true;
            }
            return false;
        }
    }
}
