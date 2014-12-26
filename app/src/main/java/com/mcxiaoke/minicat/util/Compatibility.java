package com.mcxiaoke.minicat.util;

import android.content.Context;
import android.content.Intent;
import android.content.pm.ResolveInfo;
import android.os.Build;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;
import android.view.ViewConfiguration;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.List;


public class Compatibility {
    private static final String LOG_TAG = Compatibility.class.getSimpleName();
    private static final int MAXIMUM_FLING_VELOCITY = 4000;

    public static String getActionSendMultiple() {
        try {
            Field actionSendMultipleField = Intent.class
                    .getField("ACTION_SEND_MULTIPLE");
            String actionSendMultipleValue = (String) actionSendMultipleField
                    .get(null);

            return actionSendMultipleValue;
        } catch (SecurityException e) {
            Log.w(LOG_TAG, "Error : ", e);
            return null;
        } catch (NoSuchFieldException e) {
            Log.w(LOG_TAG, "Error : ", e);
            return null;
        } catch (IllegalArgumentException e) {
            Log.w(LOG_TAG, "Error : ", e);
            return null;
        } catch (IllegalAccessException e) {
            Log.w(LOG_TAG, "Error : ", e);
            return null;
        }
    }

    public static boolean isSendMultipleAppAvailable(Context ctx) {
        String action = getActionSendMultiple();
        if (action == null) {
            return false;
        } else {
            Intent i = new Intent(Compatibility.getActionSendMultiple());
            i.setType("image/jpeg");
            List<ResolveInfo> activities = ctx.getPackageManager()
                    .queryIntentActivities(i, 0);
            // If there is only 1 activity, it is EmailAlbum !
            if (activities.size() > 1) {
                return true;
            } else {
                return false;
            }
        }
    }

    public static ScaleGestureDetector getScaleGestureDetector(Context context,
                                                               ScaleGestureDetector.OnScaleGestureListener listener) {
        try {
            // If multitouch is supported, this field exist
            MotionEvent.class.getField("ACTION_POINTER_1_DOWN");
            Log.d(LOG_TAG, "Looks like multitouch is supported.");
            return new ScaleGestureDetector(context, listener);
        } catch (SecurityException e) {
            Log.w(LOG_TAG, "Error : ", e);
            return null;
        } catch (NoSuchFieldException e) {
            Log.w(LOG_TAG, "Error : ", e);
            return null;
        }
    }

    /**
     * http://code.google.com/p/android/issues/detail?id=6191
     *
     * @return
     */
    // public static int getShowPicsLayout() {
    // int apiLevel = 0;
    // apiLevel = getAPILevel();
    //
    // if (apiLevel >= 7) {
    // return com.kg.emailalbum.mobile.R.layout.slideshow_fix;
    // } else {
    // return com.kg.emailalbum.mobile.R.layout.slideshow;
    // }
    // }

    /**
     * @param apiLevel
     * @return
     */
    private static int getAPILevel() {
        int apiLevel;
        try {
            Field SDK_INT = Build.VERSION.class.getField("SDK_INT");
            apiLevel = SDK_INT.getInt(null);
        } catch (Exception e) {
            Log.e(LOG_TAG, "Error : ", e);
            apiLevel = Integer.parseInt(Build.VERSION.SDK);
        }
        return apiLevel;
    }

    public static int getScaledMaximumFlingVelocity(Context context) {

        try {
            Method getScaledMaximumFlingVelocity = ViewConfiguration.class
                    .getMethod("getScaledMaximumFlingVelocity", (Class[]) null);
            return (Integer) getScaledMaximumFlingVelocity.invoke(
                    ViewConfiguration.get(context), (Object[]) null);

        } catch (SecurityException e) {
            return computeScaledMaximumFlingVelocity(context);
        } catch (NoSuchMethodException e) {
            return computeScaledMaximumFlingVelocity(context);
        } catch (IllegalArgumentException e) {
            return computeScaledMaximumFlingVelocity(context);
        } catch (IllegalAccessException e) {
            return computeScaledMaximumFlingVelocity(context);
        } catch (InvocationTargetException e) {
            return computeScaledMaximumFlingVelocity(context);
        }

    }

    private static int computeScaledMaximumFlingVelocity(Context context) {
        final DisplayMetrics metrics = context.getResources()
                .getDisplayMetrics();
        final float density = metrics.density;
        return (int) (density * MAXIMUM_FLING_VELOCITY + 0.5f);
    }
}
