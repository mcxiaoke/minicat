package com.fanfou.app.util;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.util.Log;

/**
 * @author mcxiaoke
 * @version 1.0 2012.02.03
 *
 */
public class ShakeListener implements SensorEventListener {
	private static final int FORCE_THRESHOLD = 350;
	private static final int TIME_THRESHOLD = 100;
	private static final int SHAKE_TIMEOUT = 500;
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

	public interface OnShakeListener {
		public void onShake();
	}

	// This will return null if sensors are not supported
	public static ShakeListener Create(Context context) {
		SensorManager sensorMgr = (SensorManager) context
				.getSystemService(Context.SENSOR_SERVICE);
		if (sensorMgr == null) {
			Log.d("ShakeListener", "Sensors not supported");
			return null;
		}
		Sensor sensor = sensorMgr.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
		if (sensor == null) {
			Log.d("ShakeListener", "Accelerometer not supported");
			return null;
		}
		ShakeListener shakeListener = new ShakeListener(sensorMgr, sensor);
		try {
			shakeListener.resume();
		} catch (UnsupportedOperationException e) {
			Log.d("ShakeListener", e.getMessage());
			return null;
		}
		return shakeListener;
	}

	private ShakeListener(SensorManager sensorMgr, Sensor sensor) {
		mSensorMgr = sensorMgr;
		mSensor = sensor;
	}

	public void setOnShakeListener(OnShakeListener listener) {
		mShakeListener = listener;
	}

	public void resume() {
		boolean supported = mSensorMgr.registerListener(this, mSensor,
				SensorManager.SENSOR_DELAY_GAME);
		if (!supported) {
			mSensorMgr.unregisterListener(this);
			throw new UnsupportedOperationException(
					"Accelerometer not supported");
		}
	}

	public void pause() {
		if (mSensorMgr != null) {
			mSensorMgr.unregisterListener(this);
			mSensorMgr = null;
		}
	}

	public void onAccuracyChanged(Sensor sensor, int accuracy) {
	}

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
}