package com.pullToRefresh;

import android.view.MotionEvent;

public class PullToRefreshState implements ScrollingState {

	@Override
	public boolean touchStopped(MotionEvent event,
			PullToRefreshComponent onTouchListener) {
		return false;
	}

	@Override
	public boolean handleMovement(MotionEvent event,
			PullToRefreshComponent pullToRefreshComponent) {
		pullToRefreshComponent.updateEventStates(event);
		if (pullToRefreshComponent.isPullingDownToRefresh()) {
			pullToRefreshComponent.setPullingDown(event);
			return true;
		} else if (pullToRefreshComponent.isPullingUpToRefresh()) {
			pullToRefreshComponent.setPullingUp(event);
			return true;
		}
		return false;
	}

}
