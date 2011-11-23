package com.pullToRefresh;

import java.util.Date;

import android.os.Handler;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.widget.ListView;

import com.pullToRefresh.utils.Pixel;

/**
 * @author mcxiaoke
 * @version 1.1 2011.11.23
 *
 */
public class PullToRefreshComponent {

	private static final int EVENT_COUNT = 3;
	protected static final float MIN_PULL_ELEMENT_HEIGHT = 100;
	protected static final float MAX_PULL_ELEMENT_HEIGHT = 200;
	private static final float PULL_ELEMENT_STANDBY_HEIGHT = 100;
	protected static int firstVisibleItem = 0;

	private ViewGroup upperView;
	private ViewGroup lowerView;
	private ListView listView;
	private Handler uiThreadHandler;

	private float[] lastYs = new float[EVENT_COUNT];

	private RefreshListener onPullDownRefreshListener;
	private RefreshListener onPullUpRefreshListener;

	protected ScrollingState state;
	private boolean mayPullUpToRefresh = true;
	private boolean mayPullDownToRefresh;
	private OnReleaseReady onReleaseLowerReady;
	private OnReleaseReady onReleaseUpperReady;

	public PullToRefreshComponent(ViewGroup upperView, ViewGroup lowerView,
			ListView listView, Handler uiThreadHandler) {
		this.upperView = upperView;
		this.lowerView = lowerView;
		this.listView = listView;
		this.uiThreadHandler = uiThreadHandler;
		this.initialize();
	}

	private void initialize() {
		this.state = new PullToRefreshState();
		this.listView.setOnTouchListener(new OnTouchListener() {

			@Override
			public boolean onTouch(View v, MotionEvent event) {
				if (event.getAction() == MotionEvent.ACTION_UP) {
					PullToRefreshComponent.this.initializeYsHistory();
					return PullToRefreshComponent.this.state.touchStopped(
							event, PullToRefreshComponent.this);
				} else if (event.getAction() == MotionEvent.ACTION_MOVE) {
					return PullToRefreshComponent.this.state.handleMovement(
							event, PullToRefreshComponent.this);
				}
				return false;
			}
		});
	}

	protected float average(float[] ysArray) {
		float avg = 0;
		for (int i = 0; i < EVENT_COUNT; i++) {
			avg += ysArray[i];
		}
		return avg / EVENT_COUNT;
	}

	public void beginPullDownRefresh() {
		this.beginRefresh(this.upperView, this.onPullDownRefreshListener);
	}

	private void beginRefresh(ViewGroup viewToUpdate,
			final RefreshListener li) {
		android.view.ViewGroup.LayoutParams params = viewToUpdate
				.getLayoutParams();
		params.height = (int) PULL_ELEMENT_STANDBY_HEIGHT;
		viewToUpdate.setLayoutParams(params);
		Log.i("PullDown", "refreshing");
		this.state = new RefreshingState();
		new Thread(new Runnable() {
			@Override
			public void run() {
				try {
					Date start = new Date();
					li.doRefresh();
					Date finish = new Date();
					long difference = finish.getTime() - start.getTime();
					try {
						Thread.sleep(Math.max(difference, 1500));
					} catch (InterruptedException e) {
					}
				} catch (RuntimeException e) {
					Log.e("Error", e.getMessage(), e);
					throw e;
				} finally {
					PullToRefreshComponent.this.runOnUiThread(new Runnable() {
						@Override
						public void run() {
							PullToRefreshComponent.this
									.refreshFinished(li);
						}
					});
				}
			}
		}).start();
	}

	public void beginPullUpRefresh() {
		this.beginRefresh(this.lowerView, this.onPullUpRefreshListener);
	}

	/**************************************************************/
	// Listeners
	/**************************************************************/

	public void setOnPullDownRefreshListener(RefreshListener li) {
		this.enablePullDownToRefresh();
		this.onPullDownRefreshListener = li;
	}

	public void setOnPullUpRefreshListener(RefreshListener li) {
		this.enablePullUpToRefresh();
		this.onPullUpRefreshListener = li;
	}

	protected void refreshFinished(final RefreshListener refreshAction) {
		Log.i("PullDown", "ready");
		this.state = new PullToRefreshState();
		this.initializeYsHistory();
		this.runOnUiThread(new Runnable() {
			@Override
			public void run() {
				float dp = new Pixel(0, PullToRefreshComponent.this.listView
						.getResources()).toDp();
				PullToRefreshComponent.this.setUpperButtonHeight(dp);
				PullToRefreshComponent.this.setLowerButtonHeight(dp);
				refreshAction.refreshFinished();
			}
		});

	}

	private void runOnUiThread(Runnable runnable) {
		this.uiThreadHandler.post(runnable);
	}

	synchronized void setUpperButtonHeight(float height) {
		this.setHeight(height, this.upperView);
	}

	synchronized void setLowerButtonHeight(float height) {
		this.setHeight(height, this.lowerView);
	}

	private void setHeight(float height, View view) {
		if (view == null) {
			return;
		}
		android.view.ViewGroup.LayoutParams params = view.getLayoutParams();
		if (params == null) {
			return;
		}
		params.height = (int) height;
		view.setLayoutParams(params);
		view.getParent().requestLayout();
	}

	public int getListTop() {
		return this.listView.getTop();
	}

	public void initializeYsHistory() {
		for (int i = 0; i < EVENT_COUNT; i++) {
			PullToRefreshComponent.this.lastYs[i] = 0;
		}
	}

	/**************************************************************/
	// HANDLE PULLING
	/**************************************************************/

	public void pullDown(MotionEvent event, float firstY) {
		float averageY = PullToRefreshComponent.this
				.average(PullToRefreshComponent.this.lastYs);

		int height = (int) Math.max(
				Math.min(averageY - firstY, MAX_PULL_ELEMENT_HEIGHT), 0);
		PullToRefreshComponent.this.setUpperButtonHeight(height);
	}

	public void pullUp(MotionEvent event, float firstY) {
		float averageY = PullToRefreshComponent.this

		.average(PullToRefreshComponent.this.lastYs);

		int height = (int) Math.max(
				Math.min(firstY - averageY, MAX_PULL_ELEMENT_HEIGHT), 0);
		PullToRefreshComponent.this.setLowerButtonHeight(height);
	}

	public boolean isPullingDownToRefresh() {
		return this.mayPullDownToRefresh && this.isIncremental()
				&& this.isFirstVisible();
	}

	public boolean isPullingUpToRefresh() {
		return this.mayPullUpToRefresh && this.isDecremental()
				&& this.isLastVisible();
	}

	private boolean isFirstVisible() {
		if (this.listView.getCount() == 0) {
			return true;
		} else if (this.listView.getFirstVisiblePosition() == 0) {
			return this.listView.getChildAt(0).getTop() >= this.listView
					.getTop();
		} else {
			return false;
		}
	}

	private boolean isLastVisible() {
		if (this.listView.getCount() == 0) {
			return true;
		} else if (this.listView.getLastVisiblePosition() + 1 == this.listView
				.getCount()) {
			return this.listView.getChildAt(this.listView.getChildCount() - 1)
					.getBottom() <= this.listView.getBottom();
		} else {
			return false;
		}
	}

	private boolean isIncremental(int from, int to, int step) {
		float realFrom = this.lastYs[from];
		float realTo = this.lastYs[to];
		Log.i("pull to refresh", "scrolling from " + String.valueOf(realFrom));
		Log.i("pull to refresh", "scrolling to " + String.valueOf(realTo));
		return this.lastYs[from] != 0 && realTo != 0
				&& Math.abs(realFrom - realTo) > 50 && realFrom * step < realTo;
	}

	private boolean isIncremental() {
		return this.isIncremental(0, EVENT_COUNT - 1, +1);
	}

	private boolean isDecremental() {
		return this.isIncremental(0, EVENT_COUNT - 1, -1);
	}

	public void updateEventStates(MotionEvent event) {
		for (int i = 0; i < EVENT_COUNT - 1; i++) {
			PullToRefreshComponent.this.lastYs[i] = PullToRefreshComponent.this.lastYs[i + 1];
		}

		float y = event.getY();
		int top = PullToRefreshComponent.this.listView.getTop();
		Log.i("Pulltorefresh", "event y:" + String.valueOf(y));
		Log.i("Pulltorefresh", "list top:" + String.valueOf(top));
		PullToRefreshComponent.this.lastYs[EVENT_COUNT - 1] = y + top;
	}

	/**************************************************************/
	// State Change
	/**************************************************************/

	public void setPullingDown(MotionEvent event) {
		Log.i("PullDown", "pulling down");
		this.state = new PullingDownState(event);
	}

	public void setPullingUp(MotionEvent event) {
		Log.i("PullDown", "pulling up");
		this.state = new PullingUpState(event);
	}

	public float getBottomViewHeight() {
		return this.lowerView.getHeight();
	}

	public RefreshListener getOnUpperRefreshListener() {
		return this.onPullDownRefreshListener;
	}

	public RefreshListener getOnLowerRefreshListener() {
		return this.onPullUpRefreshListener;
	}

	public void disablePullUpToRefresh() {
		this.mayPullUpToRefresh = false;
	}

	public void disablePullDownToRefresh() {
		this.mayPullDownToRefresh = false;
	}

	public void enablePullUpToRefresh() {
		this.mayPullUpToRefresh = true;
	}

	public void enablePullDownToRefresh() {
		this.mayPullDownToRefresh = true;
	}

	public void setOnReleaseUpperReady(OnReleaseReady onReleaseUpperReady) {
		this.onReleaseUpperReady = onReleaseUpperReady;
	}

	public void setOnReleaseLowerReady(OnReleaseReady onReleaseUpperReady) {
		this.onReleaseLowerReady = onReleaseUpperReady;
	}

	public void readyToReleaseUpper(boolean ready) {
		if (this.onReleaseUpperReady != null) {
			this.onReleaseUpperReady.releaseReady(ready);
		}
	}

	public void readyToReleaseLower(boolean ready) {
		if (this.onReleaseLowerReady != null) {
			this.onReleaseLowerReady.releaseReady(ready);
		}
	}
}
