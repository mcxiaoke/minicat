/*
 * Copyright (C) 2011 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.mcxiaoke.minicat.util;

import android.content.Context;
import android.location.Location;
import android.location.LocationProvider;
import android.os.Bundle;
import android.util.Log;

/**
 * A class that handles everything about location.
 */
public class LocationManager {
    private static final String TAG = "LocationManager";
    LocationListener[] mLocationListeners = new LocationListener[]{
            new LocationListener(android.location.LocationManager.GPS_PROVIDER),
            new LocationListener(
                    android.location.LocationManager.NETWORK_PROVIDER)};
    private Context mContext;
    private Listener mListener;
    private android.location.LocationManager mLocationManager;
    private boolean mRecordLocation;

    public LocationManager(Context context, Listener listener) {
        mContext = context;
        mListener = listener;
    }

    public Location getCurrentLocation() {
        if (!mRecordLocation)
            return null;

        // go in best to worst order
        for (int i = 0; i < mLocationListeners.length; i++) {
            Location l = mLocationListeners[i].current();
            if (l != null)
                return l;
        }
        Log.d(TAG, "No location received yet.");
        return null;
    }

    public void recordLocation(boolean recordLocation) {
        if (mRecordLocation != recordLocation) {
            mRecordLocation = recordLocation;
            if (recordLocation) {
                startReceivingLocationUpdates();
            } else {
                stopReceivingLocationUpdates();
            }
        }
    }

    private void startReceivingLocationUpdates() {
        if (mLocationManager == null) {
            mLocationManager = (android.location.LocationManager) mContext
                    .getSystemService(Context.LOCATION_SERVICE);
        }
        if (mLocationManager != null) {
            try {
                mLocationManager.requestLocationUpdates(
                        android.location.LocationManager.NETWORK_PROVIDER,
                        1000, 0F, mLocationListeners[1]);
            } catch (SecurityException ex) {
                Log.i(TAG, "fail to request location update, ignore", ex);
            } catch (IllegalArgumentException ex) {
                Log.d(TAG, "provider does not exist " + ex.getMessage());
            }
            try {
                mLocationManager.requestLocationUpdates(
                        android.location.LocationManager.GPS_PROVIDER, 1000,
                        0F, mLocationListeners[0]);
                if (mListener != null)
                    mListener.showGpsOnScreenIndicator(false);
            } catch (SecurityException ex) {
                Log.i(TAG, "fail to request location update, ignore", ex);
            } catch (IllegalArgumentException ex) {
                Log.d(TAG, "provider does not exist " + ex.getMessage());
            }
            Log.d(TAG, "startReceivingLocationUpdates");
        }
    }

    private void stopReceivingLocationUpdates() {
        if (mLocationManager != null) {
            for (int i = 0; i < mLocationListeners.length; i++) {
                try {
                    mLocationManager.removeUpdates(mLocationListeners[i]);
                } catch (Exception ex) {
                    Log.i(TAG, "fail to remove location listners, ignore", ex);
                }
            }
            Log.d(TAG, "stopReceivingLocationUpdates");
        }
        if (mListener != null)
            mListener.hideGpsOnScreenIndicator();
    }

    public interface Listener {
        public void showGpsOnScreenIndicator(boolean hasSignal);

        public void hideGpsOnScreenIndicator();
    }

    private class LocationListener implements android.location.LocationListener {
        Location mLastLocation;
        boolean mValid = false;
        String mProvider;

        public LocationListener(String provider) {
            mProvider = provider;
            mLastLocation = new Location(mProvider);
        }

        @Override
        public void onLocationChanged(Location newLocation) {
            if (newLocation.getLatitude() == 0.0
                    && newLocation.getLongitude() == 0.0) {
                // Hack to filter out 0.0,0.0 locations
                return;
            }
            // If GPS is available before check camera, we won't get status
            // update so update GPS indicator when we receive data.
            if (mListener != null
                    && mRecordLocation
                    && android.location.LocationManager.GPS_PROVIDER
                    .equals(mProvider)) {
                mListener.showGpsOnScreenIndicator(true);
            }
            if (!mValid) {
                Log.d(TAG, "Got first location.");
            }
            mLastLocation.set(newLocation);
            mValid = true;
        }

        @Override
        public void onStatusChanged(String provider, int status, Bundle extras) {
            switch (status) {
                case LocationProvider.OUT_OF_SERVICE:
                case LocationProvider.TEMPORARILY_UNAVAILABLE: {
                    mValid = false;
                    if (mListener != null
                            && mRecordLocation
                            && android.location.LocationManager.GPS_PROVIDER
                            .equals(provider)) {
                        mListener.showGpsOnScreenIndicator(false);
                    }
                    break;
                }
            }
        }

        @Override
        public void onProviderEnabled(String provider) {
        }

        @Override
        public void onProviderDisabled(String provider) {
            mValid = false;
        }

        public Location current() {
            return mValid ? mLastLocation : null;
        }
    }
}
