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

package com.fanfou.app.hd.analytics;

import android.content.Context;

import com.google.android.apps.analytics.Item;
import com.google.android.apps.analytics.Transaction;

/**
 * Delegate for GoogleAnalyticsTracker.  This interface allows for mocking
 * GoogleAnalyticsTracker.  GoogleAnalyticsTracker is a singleton with a
 * private constructor, so cannot be extended.  Note that this delegate will
 * only expose those public methods of GoogleAnalyticsTracker that are used
 * by EasyTracker.
 */
public interface GoogleAnalyticsTrackerDelegate {

  public void startNewSession(String accountId, int dispatchPeriod, Context ctx);
  
  public void trackEvent(String category, String action, String label, int value);
  
  public void trackPageView(String pageUrl);
  
  public boolean dispatch();
  
  public void stopSession();
  
  public boolean setCustomVar(int index, String name, String value, int scope);
  
  public boolean setCustomVar(int index, String name, String value);
  
  public void addTransaction(Transaction transaction);
  
  public void addItem(Item item);
  
  public void trackTransactions();
  
  public void clearTransactions();
  
  public void setAnonymizeIp(boolean anonymizeIp);
  
  public void setSampleRate(int sampleRate);
  
  public boolean setReferrer(String referrer);
  
  public void setDebug(boolean debug);
  
  public void setDryRun(boolean dryRun);
}
