package com.pullToRefresh;

import android.view.MotionEvent;

public class RefreshingState implements ScrollingState {

	@Override
	public boolean touchStopped(MotionEvent event,
			PullToRefreshComponent pullToRefreshComponent) {
		return true;
	}

	@Override
	public boolean handleMovement(MotionEvent event,
			PullToRefreshComponent pullToRefreshComponent) {
		return true;
	}

}
