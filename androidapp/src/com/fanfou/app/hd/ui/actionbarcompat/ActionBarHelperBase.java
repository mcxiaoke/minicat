/*
 * Copyright 2011 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.fanfou.app.hd.ui.actionbarcompat;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.fanfou.app.hd.R;

/**
 * A class that implements the action bar pattern for pre-Honeycomb devices.
 */
/**
 * @author mcxiaoke
 * @version 1.0 2012.02.10
 * 
 */
class ActionBarHelperBase extends ActionBarHelper {

	protected ActionBarHelperBase(Activity activity) {
		super(activity);
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		mActivity.requestWindowFeature(Window.FEATURE_CUSTOM_TITLE);
	}

	@Override
	public void onPostCreate(Bundle savedInstanceState) {
		mActivity.getWindow().setFeatureInt(Window.FEATURE_CUSTOM_TITLE,
				R.layout.actionbar_compat);
		setupActionBar();
	}

	/**
	 * Sets up the compatibility action bar with the given title.
	 */
	private void setupActionBar() {
		final ViewGroup actionBarCompat = getActionBarCompat();
		if (actionBarCompat == null) {
			return;
		}

		LinearLayout.LayoutParams springLayoutParams = new LinearLayout.LayoutParams(
				0, ViewGroup.LayoutParams.FILL_PARENT);
		springLayoutParams.weight = 1;

		setupHomeAction();
		setupRefreshAction();

		TextView titleText = new TextView(mActivity, null,
				R.attr.actionbarCompatTitleStyle);
		titleText.setLayoutParams(springLayoutParams);
		titleText.setText(mActivity.getTitle());
		actionBarCompat.addView(titleText);
	}

	@Override
	public void setRefreshActionItemState(boolean refreshing) {
		View refreshButton = mActivity
				.findViewById(R.id.actionbar_compat_item_refresh);
		View refreshIndicator = mActivity
				.findViewById(R.id.actionbar_compat_item_refresh_progress);

		if (refreshButton != null) {
			refreshButton.setVisibility(refreshing ? View.GONE : View.VISIBLE);
		}
		if (refreshIndicator != null) {
			refreshIndicator.setVisibility(refreshing ? View.VISIBLE
					: View.GONE);
		}
	}

	@Override
	public void onTitleChanged(CharSequence title, int color) {
		TextView titleView = (TextView) mActivity
				.findViewById(R.id.actionbar_compat_title);
		if (titleView != null) {
			titleView.setText(title);
		}
	}

	/**
	 * Returns the {@link android.view.ViewGroup} for the action bar on phones
	 * (compatibility action bar). Can return null, and will return null on
	 * Honeycomb.
	 */
	private ViewGroup getActionBarCompat() {
		return (ViewGroup) mActivity.findViewById(R.id.actionbar_compat);
	}

	private View setupHomeAction() {
		final ViewGroup actionBar = getActionBarCompat();
		if (actionBar == null) {
			return null;
		}

		// Create the button
		ImageButton actionButton = new ImageButton(mActivity, null,
				R.attr.actionbarCompatItemHomeStyle);
		actionButton.setId(R.id.actionbar_compat_item_home);
		actionButton.setLayoutParams(new ViewGroup.LayoutParams((int) mActivity
				.getResources().getDimension(
						R.dimen.actionbar_compat_button_home_width),
				ViewGroup.LayoutParams.FILL_PARENT));
		actionButton.setImageResource(R.drawable.ic_menu_home);
		actionButton.setScaleType(ImageView.ScaleType.CENTER);
		actionButton.setContentDescription("ActionBar");
		// actionButton.setOnClickListener(l);
		actionBar.addView(actionButton);
		return actionButton;
	}

	private View setupRefreshAction() {
		final ViewGroup actionBar = getActionBarCompat();
		if (actionBar == null) {
			return null;
		}

		// Create the button
		ImageButton actionButton = new ImageButton(mActivity, null,
				R.attr.actionbarCompatItemStyle);
		actionButton.setId(R.id.actionbar_compat_item_refresh);
		actionButton.setLayoutParams(new ViewGroup.LayoutParams((int) mActivity
				.getResources().getDimension(
						R.dimen.actionbar_compat_button_width),
				ViewGroup.LayoutParams.FILL_PARENT));
		actionButton.setImageResource(R.drawable.ic_menu_refresh);
		actionButton.setScaleType(ImageView.ScaleType.CENTER);
		actionButton.setContentDescription("ActionBar");
		// actionButton.setOnClickListener(l);
		actionBar.addView(actionButton);

		// Refresh buttons should be stateful, and allow for indeterminate
		// progress indicators,
		// so add those.
		ProgressBar indicator = new ProgressBar(mActivity, null,
				R.attr.actionbarCompatProgressIndicatorStyle);

		final int buttonWidth = mActivity.getResources().getDimensionPixelSize(
				R.dimen.actionbar_compat_button_width);
		final int buttonHeight = mActivity.getResources()
				.getDimensionPixelSize(R.dimen.actionbar_compat_height);
		final int progressIndicatorWidth = buttonWidth / 2;

		LinearLayout.LayoutParams indicatorLayoutParams = new LinearLayout.LayoutParams(
				progressIndicatorWidth, progressIndicatorWidth);
		indicatorLayoutParams.setMargins(
				(buttonWidth - progressIndicatorWidth) / 2,
				(buttonHeight - progressIndicatorWidth) / 2,
				(buttonWidth - progressIndicatorWidth) / 2, 0);
		indicator.setLayoutParams(indicatorLayoutParams);
		indicator.setVisibility(View.GONE);
		indicator.setId(R.id.actionbar_compat_item_refresh_progress);
		actionBar.addView(indicator);

		return actionButton;
	}

}
