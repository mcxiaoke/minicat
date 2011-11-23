package com.pullToRefresh;

import android.view.MotionEvent;

public class PullingDownState implements ScrollingState {

	private float firstY;

	public PullingDownState(MotionEvent event) {
		this.firstY = event.getY();
	}

	@Override
	public boolean touchStopped(MotionEvent event,
			PullToRefreshComponent pullToRefreshComponent) {
		if (pullToRefreshComponent.getListTop() > PullToRefreshComponent.MIN_PULL_ELEMENT_HEIGHT) {
			pullToRefreshComponent.beginPullDownRefresh();
		} else {
			pullToRefreshComponent.refreshFinished(pullToRefreshComponent
					.getOnUpperRefreshListener());
		}
		return true;
	}

	@Override
	public boolean handleMovement(MotionEvent event,
			PullToRefreshComponent pullToRefreshComponent) {
		pullToRefreshComponent.updateEventStates(event);
		pullToRefreshComponent.pullDown(event, this.firstY);
		pullToRefreshComponent.readyToReleaseUpper(pullToRefreshComponent
				.getListTop() > PullToRefreshComponent.MIN_PULL_ELEMENT_HEIGHT);
		return true;
	}

}
