// Copyright 2011 Google Inc. All Rights Reserved.
//
// Licensed under the Apache License, Version 2.0 (the "License");
// you may not use this file except in compliance with the License.
// You may obtain a copy of the License at
//
//     http://www.apache.org/licenses/LICENSE-2.0
//
// Unless required by applicable law or agreed to in writing, software
// distributed under the License is distributed on an "AS IS" BASIS,
// WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// See the License for the specific language governing permissions and
// limitations under the License.

package org.mcxiaoke.fancooker.analytics;

import android.app.Activity;
import android.os.Bundle;

/**
 * Extend this class instead of Activity to enable Google Analytics Tracking for
 * an Activity.  Note that for accurate application-level tracking,
 * <emph>all</emph> Activities in an application must either extend one of the
 * TrackedActivity classes (i.e. TrackedActivity, TrackedListActivity) or
 * implement the calls found in this class.  This is necessary as the
 * EasyTracking library maintains a count of active Activities in order to
 * determine when a session starts and ends and relies on the fact that a new
 * Activity's onStart method is called before the old Activity's onStop method
 * is called.
 *
 * The one exception to this flow is when an Activity is being restarted because
 * of a configuration change (i.e. orientation change) where the
 * onRetainNonConfigurationInstance method is called after the Activity's onStop
 * method is called and before the new Activity's onStart is called.
 * 
 * See the file ReadMe.txt for details on setting up tracking using the
 * EasyTracker library.
 */
public class TrackedActivity extends Activity {
  @Override
  protected void onCreate(Bundle savedInstanceState) {
      super.onCreate(savedInstanceState);
      
      // Only one call to setContext is needed, but additional calls don't hurt
      // anything, so we'll always make the call to ensure EasyTracker gets
      // setup properly.
      EasyTracker.getTracker().setContext(this);
  }

  @Override
  protected void onStart() {
    super.onStart();
    
    // This call will ensure that the Activity in question is tracked properly,
    // based on the setting of ga_auto_activity_tracking parameter.  It will
    // also ensure that startNewSession is called appropriately.
    EasyTracker.getTracker().trackActivityStart(this);
  }

  /**
   * This method is deprecated in Android 3.0 (Honeycomb) and later, but
   * GoogleAnalytics support goes back to Android 1.5 and therefore cannot use
   * the Fragment API.
   */
  @Override
  public Object onRetainNonConfigurationInstance() {
    Object o = super.onRetainNonConfigurationInstance();
    
    // This call is needed to ensure that configuration changes (like
    // orientation) don't result in new sessions.  Remove this line if you want
    // configuration changes to for a new session in Google Analytics.
    EasyTracker.getTracker().trackActivityRetainNonConfigurationInstance();
    return o;
  }

  @Override
  protected void onStop() {
    super.onStop();
    
    // This call is needed to ensure time spent in an Activity and an
    // Application are measured accurately.
    EasyTracker.getTracker().trackActivityStop(this);
  }
}