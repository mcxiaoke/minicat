package com.pullToRefresh.utils;

import android.content.res.Resources;

public class Pixel {

	private Resources resources;
	private int value;

	public Pixel(int value, Resources resources) {
		this.value = value;
		this.resources = resources;
	}

	public float toDp() {
		return this.value * this.resources.getDisplayMetrics().density + 0.5f;
	}
}
