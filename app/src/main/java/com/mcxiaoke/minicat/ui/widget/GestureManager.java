package com.mcxiaoke.minicat.ui.widget;

import android.util.Log;
import android.view.GestureDetector.SimpleOnGestureListener;
import android.view.MotionEvent;
import com.mcxiaoke.minicat.AppContext;


/**
 * @author mcxiaoke
 * @version 1.0 2012.02.01
 */
public class GestureManager {

    private static final String TAG = SwipeGestureListener.class
            .getSimpleName();

    public interface SwipeListener {

        boolean onSwipeLeft();

        boolean onSwipeRight();

    }

    public static class SwipeGestureListener extends SimpleOnGestureListener {

        private static final int SWIPE_MIN_DISTANCE = 150;
        private static final int SWIPE_MAX_OFF_PATH = 100;
        private static final int SWIPE_THRESHOLD_VELOCITY = 250;

        private SwipeListener mSwipeListener;

        public SwipeGestureListener(SwipeListener listener) {
            this.mSwipeListener = listener;
        }

        @Override
        public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX,
                               float velocityY) {
            if (AppContext.DEBUG) {
                Log.d(TAG, "velocityX=" + velocityX);
            }
            if (Math.abs(e1.getY() - e2.getY()) < SWIPE_MAX_OFF_PATH) {
                if (e1.getX() - e2.getX() > SWIPE_MIN_DISTANCE
                        && Math.abs(velocityX) > SWIPE_THRESHOLD_VELOCITY) {
                    return mSwipeListener.onSwipeLeft();
                } else if (e2.getX() - e1.getX() > SWIPE_MIN_DISTANCE
                        && Math.abs(velocityX) > SWIPE_THRESHOLD_VELOCITY) {
                    return mSwipeListener.onSwipeRight();
                }
            }
            return super.onFling(e1, e2, velocityX, velocityY);
        }

    }

}
