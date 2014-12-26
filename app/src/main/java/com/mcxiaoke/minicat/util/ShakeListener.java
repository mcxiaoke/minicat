package com.mcxiaoke.minicat.util;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;

/**
 * @author mcxiaoke
 * @version 1.1 2012.02.03
 */
public class ShakeListener implements SensorEventListener {
    private static final int FORCE_THRESHOLD = 500;
    private static final int TIME_THRESHOLD = 100;
    private static final int SHAKE_TIMEOUT = 250;
    private static final int SHAKE_DURATION = 1000;
    private static final int SHAKE_COUNT = 3;

    private SensorManager mSensorMgr;
    private Sensor mSensor;
    private float mLastX = -1.0f, mLastY = -1.0f, mLastZ = -1.0f;
    private long mLastTime;
    private OnShakeListener mShakeListener;
    private int mShakeCount = 0;
    private long mLastShake;
    private long mLastForce;

    public ShakeListener(Context context) {
        mSensorMgr = (SensorManager) context
                .getSystemService(Context.SENSOR_SERVICE);
        mSensor = mSensorMgr.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
    }

    public ShakeListener(Context context, OnShakeListener listener) {
        this(context);
        mShakeListener = listener;
    }

    public void setOnShakeListener(OnShakeListener listener) {
        mShakeListener = listener;
    }

    public void onResume() {
        if (mSensorMgr != null) {
            boolean supported = mSensorMgr.registerListener(this, mSensor,
                    SensorManager.SENSOR_DELAY_UI);
            if (!supported) {
                mSensorMgr.unregisterListener(this);
            }
        }

    }

    public void onPause() {
        if (mSensorMgr != null) {
            mSensorMgr.unregisterListener(this);
        }

    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        if (event.sensor.getType() != Sensor.TYPE_ACCELEROMETER)
            return;
        long now = System.currentTimeMillis();

        if ((now - mLastForce) > SHAKE_TIMEOUT) {
            mShakeCount = 0;
        }

        if ((now - mLastTime) > TIME_THRESHOLD) {
            float[] values = event.values;
            long diff = now - mLastTime;
            float speed = Math.abs(values[SensorManager.DATA_X]
                    + values[SensorManager.DATA_Y]
                    + values[SensorManager.DATA_Z] - mLastX - mLastY - mLastZ)
                    / diff * 10000;
            if (speed > FORCE_THRESHOLD) {
                if ((++mShakeCount >= SHAKE_COUNT)
                        && (now - mLastShake > SHAKE_DURATION)) {
                    mLastShake = now;
                    mShakeCount = 0;
                    if (mShakeListener != null) {
                        mShakeListener.onShake();
                    }
                }
                mLastForce = now;
            }
            mLastTime = now;
            mLastX = values[SensorManager.DATA_X];
            mLastY = values[SensorManager.DATA_Y];
            mLastZ = values[SensorManager.DATA_Z];
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
    }

    public interface OnShakeListener {
        public void onShake();
    }
}