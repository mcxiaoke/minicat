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

import android.content.Context;

import com.google.android.apps.analytics.GoogleAnalyticsTracker;
import com.google.android.apps.analytics.Item;
import com.google.android.apps.analytics.Transaction;

public class GoogleAnalyticsTrackerDelegateImpl implements GoogleAnalyticsTrackerDelegate {

  private GoogleAnalyticsTracker tracker = GoogleAnalyticsTracker.getInstance();

  @Override
  public void startNewSession(String accountId, int dispatchPeriod, Context ctx) {
    tracker.startNewSession(accountId, dispatchPeriod, ctx);
  }

  @Override
  public void trackEvent(String category, String action, String label, int value) {
    tracker.trackEvent(category, action, label, value);
  }

  @Override
  public void trackPageView(String pageUrl) {
    tracker.trackPageView(pageUrl);
  }

  @Override
  public boolean dispatch() {
    return tracker.dispatch();
  }

  @Override
  public void stopSession() {
    tracker.stopSession();
  }

  @Override
  public boolean setCustomVar(int index, String name, String value, int scope) {
    return tracker.setCustomVar(index, name, value, scope);
  }

  @Override
  public boolean setCustomVar(int index, String name, String value) {
    return tracker.setCustomVar(index, name, value);
  }

  @Override
  public void addTransaction(Transaction transaction) {
    tracker.addTransaction(transaction);
  }

  @Override
  public void addItem(Item item) {
    tracker.addItem(item);
  }

  @Override
  public void trackTransactions() {
    tracker.trackTransactions();
  }

  @Override
  public void clearTransactions() {
    tracker.clearTransactions();
  }

  @Override
  public void setAnonymizeIp(boolean anonymizeIp) {
    tracker.setAnonymizeIp(anonymizeIp);
  }

  @Override
  public void setSampleRate(int sampleRate) {
    tracker.setSampleRate(sampleRate);
  }

  @Override
  public boolean setReferrer(String referrer) {
    return tracker.setReferrer(referrer);
  }

  @Override
  public void setDebug(boolean debug) {
    tracker.setDebug(debug);
  }

  @Override
  public void setDryRun(boolean dryRun) {
    tracker.setDryRun(dryRun);
  }
}
