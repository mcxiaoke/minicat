package com.pullToRefresh;

import android.view.MotionEvent;

public class PullingUpState implements ScrollingState {

	private float firstY;

	public PullingUpState(MotionEvent event) {
		this.firstY = event.getY();
	}

	@Override
	public boolean touchStopped(MotionEvent event,
			PullToRefreshComponent pullToRefreshComponent) {
		if (pullToRefreshComponent.getBottomViewHeight() > PullToRefreshComponent.MIN_PULL_ELEMENT_HEIGHT) {
			pullToRefreshComponent.beginPullUpRefresh();
		} else {
			pullToRefreshComponent.refreshFinished(pullToRefreshComponent
					.getOnLowerRefreshListener());
		}
		return true;
	}

	@Override
	public boolean handleMovement(MotionEvent event,
			PullToRefreshComponent pullToRefreshComponent) {
		pullToRefreshComponent.updateEventStates(event);
		pullToRefreshComponent.pullUp(event, this.firstY);
		pullToRefreshComponent
				.readyToReleaseLower(pullToRefreshComponent
						.getBottomViewHeight() > PullToRefreshComponent.MIN_PULL_ELEMENT_HEIGHT);
		return true;
	}

}
