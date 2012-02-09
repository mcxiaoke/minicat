package com.fanfou.app.hd.util;

/*
 * Copyright (C) 2010 The Android Open Source Project
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

import android.os.SystemClock;
import android.util.Log;

/**
 * A simple class to measure elapsed time.
 *
 * <code>
 *   StopWatch s = StopWatch.start();
 *   // Do your stuff
 *   s.split();
 *   // More stuff
 *   s.split();
 *   // More stuff
 *   s.stop();
 * </code>
 */
/**
 * @author mcxiaoke
 * @version 1.0 2011.11.22
 * 
 */
public class StopWatch {
	private static final String TAG = StopWatch.class.getSimpleName();
	private final String mName;
	private final long mStart;
	private long mLastSplit;

	private StopWatch(String name) {
		mName = name;
		mStart = getCurrentTime();
		mLastSplit = mStart;
		Log.d(TAG, "StopWatch(" + mName + ") start");
	}

	public static StopWatch start(String name) {
		return new StopWatch(name);
	}

	public void split(String label) {
		long now = getCurrentTime();
		long elapse = now - mLastSplit;
		Log.d(TAG, "StopWatch(" + mName + ") split(" + label + ") " + elapse);
		mLastSplit = now;
	}

	public void stop() {
		long now = getCurrentTime();
		long elapse = now - mLastSplit;
		Log.d(TAG, "StopWatch(" + mName + ") stop: " + (now - mLastSplit)
				+ "  (total " + (now - mStart) + ")");
	}

	private static long getCurrentTime() {
		return SystemClock.elapsedRealtime();
	}
}
