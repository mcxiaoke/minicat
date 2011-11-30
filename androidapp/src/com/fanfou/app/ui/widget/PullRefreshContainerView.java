/**
 * Use this however you want, but it might not work how you'd like.
 * If you fix it or make it better, share the wealth.
 * If you find a problem, let it be known.
 * 
 * https://github.com/timahoney/Android-Pull-To-Refresh
 * https://github.com/wdx700/Android-Pull-To-Refresh
 */
package com.fanfou.app.ui.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

/**
 * A container for a ListView that can be pulled to refresh.
 * This will create a ListView and refresh header automatically, but you can
 * customize them by using {@link #setList(ListView)} and {@link #setRefreshHeader(View, int)}
 * <p>
 * To use, put this where you would normally put your ListView. Since this does not extend
 * ListView, you must use {@link #getList()} to modify the list or provide your own.
 * <p>
 * To get the actions of the list, use a {@link OnChangeStateListener} with {@link #setOnChangeStateListener(OnChangeStateListener)}.
 * If you want to change how the refresh header looks, you should do it during these state changes.   
 */
public class PullRefreshContainerView extends LinearLayout {	
	/**
	 * Interface for listening to when the refresh container changes state.
	 */
	public interface OnChangeStateListener {
		/**
		 * Notifies a listener when the refresh view's state changes.
		 * @param container The container that contains the header
		 * @param state The state of the header. May be STATE_IDLE, STATE_READY,
		 * 		or STATE_REFRESHING.
		 */
		public void onChangeState(PullRefreshContainerView container, int state);
	}

	/**
	 * State of the refresh header when it is doing nothing or being pulled down slightly.
	 */
	public static final int STATE_IDLE = 0;

	/**
	 * State of the refresh header when it has been pulled down but not enough to start refreshing, and
	 * has not yet been released.
	 */
	public static final int STATE_PULL = 1;
	
	/**
	 * State of the refresh header when it has been pulled down enough to start refreshing, but
	 * has not yet been released.
	 */
	public static final int STATE_RELEASE = 2;

	/**
	 * State of the refresh header when the list should be refreshing.
	 */
	public static final int STATE_LOADING = 3;

	private LinearLayout mHeaderContainer;
	private View mHeaderView;
	private ListView mList;
	private int mState;
	private OnChangeStateListener mOnChangeStateListener;
	
	private int REFRESH_VIEW_HEIGHT = 60;
	
	/**
	 * Creates a new pull to refresh container.
	 * 
	 * @param context the application context
	 */
	public PullRefreshContainerView(Context context) {
		super(context);
		init(context);
	}

	/**
	 * Creates a new pull to refresh container.
	 * 
	 * @param context the application context
	 * @param attrs the XML attribute set
	 */
	public PullRefreshContainerView(Context context, AttributeSet attrs) {
		super(context, attrs);
		init(context);
	}

	/**
	 * Creates a new pull to refresh container.
	 * 
	 * @param context the application context
	 * @param attrs the XML attribute set
	 * @param defStyle the style for this view
	 */
	public PullRefreshContainerView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs);
		init(context);
	}

	private void init(Context context) {
		mState = STATE_IDLE; // Start out as idle.
		
		float densityFactor = context.getResources().getDisplayMetrics().density;
    	REFRESH_VIEW_HEIGHT *= densityFactor;

		// We don't want to see the fading edge on the container.
		setVerticalFadingEdgeEnabled(false);
		setVerticalScrollBarEnabled(false);
		
		setOrientation(LinearLayout.VERTICAL);

		// Set the default list and header.
		mHeaderContainer = new LinearLayout(context);
		addView(mHeaderContainer);
		setRefreshViewHeight(1);
		
		TextView headerView = new TextView(context);
		headerView.setText("Default refresh header.");
		setRefreshHeader(headerView);
		
		ListView list = new ListView(context);
		setList(list);
	}

	private boolean mScrollingList = true;
	private float mInterceptY;
	private int mLastMotionY;
	
	@Override
	public boolean dispatchTouchEvent (MotionEvent ev) {
		float oldLastY = mInterceptY;
		mInterceptY = ev.getY();

		if (mState == STATE_LOADING) {
    		return super.dispatchTouchEvent(ev);
    	}
		
		switch (ev.getAction()) {
		case MotionEvent.ACTION_DOWN:
			mLastMotionY = (int) ev.getY();
			mScrollingList = true;
			return super.dispatchTouchEvent(ev);
			
		case MotionEvent.ACTION_MOVE:
			if (mList.getFirstVisiblePosition() == 0
					&& (mList.getChildCount() == 0 || mList.getChildAt(0).getTop() == 0)) {
				if ((mInterceptY - oldLastY > 5) || (mState == STATE_PULL) || (mState == STATE_RELEASE)) {
					mScrollingList = false;
					applyHeaderHeight(ev);
					return true;
				} else {
					mScrollingList = true;
					return super.dispatchTouchEvent(ev);
				}
			} else if (mScrollingList) {
				return super.dispatchTouchEvent(ev);
			} else {
				return super.dispatchTouchEvent(ev);
			}
		
		case MotionEvent.ACTION_UP:
		case MotionEvent.ACTION_CANCEL:
        	if (mState == STATE_RELEASE) {
				refresh();
			} else {
				changeState(STATE_IDLE);
			}
        	
        	if (mScrollingList) {
        		return super.dispatchTouchEvent(ev);
        	} else {
        		return true;
        	}
			
		default:
			return super.dispatchTouchEvent(ev);
		}
	}
	
	private void applyHeaderHeight(MotionEvent ev) {
    	final int historySize = ev.getHistorySize();

        if (historySize > 0) {
	        for (int h = 0; h < historySize; h++) {
                int historicalY = (int) (ev.getHistoricalY(h));
                updateRefreshView(historicalY - mLastMotionY);
	        }
        } else {
        	int historicalY = (int) ev.getY();
        	updateRefreshView(historicalY - mLastMotionY);
        }
    }
	
	private void updateRefreshView(int height) {
		if (height <= 0) {
        	return;
        }

        if ((REFRESH_VIEW_HEIGHT/4 <= mCurRefreshViewHeight) && (mCurRefreshViewHeight < REFRESH_VIEW_HEIGHT)) {
        	setRefreshViewHeight(height);
        	changeState(STATE_PULL);
        } else if (mCurRefreshViewHeight >= REFRESH_VIEW_HEIGHT) {
        	if (height > REFRESH_VIEW_HEIGHT) {
        		height = (int) (REFRESH_VIEW_HEIGHT + (height - REFRESH_VIEW_HEIGHT) *  REFRESH_VIEW_HEIGHT * 1.0f/height);
        	}
        	
        	setRefreshViewHeight(height);
        	changeState(STATE_RELEASE);
        } else {
        	setRefreshViewHeight(height);
        }
	}
	
	private int mCurRefreshViewHeight = 60;
    private void setRefreshViewHeight(int height) {
    	if (mCurRefreshViewHeight == height) {
    		return;
    	}

    	if (height == 1) {
    		mHeaderContainer.setLayoutParams(new LayoutParams(1, 1));
    	} else {
    		mHeaderContainer.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT, height));
    	}
    	mCurRefreshViewHeight = height;
    }
	
	private void changeState(int state) {
		switch (state) {
		case STATE_IDLE:
			setRefreshViewHeight(1);
			break;
		case STATE_PULL:
			break;
		case STATE_RELEASE:
			break;
		case STATE_LOADING:
			setRefreshViewHeight(REFRESH_VIEW_HEIGHT);
			break;
		}
		
		mState = state;

		notifyStateChanged();
	}

	/**
	 * Sets the list to be used in this pull to refresh container.
	 * @param list the list to use
	 */
	public void setList(ListView list) {
		if (mList != null) {
			removeView(mList);
		}
		mList = list;
		if (mList.getParent() != null) {
			ViewGroup parent = (ViewGroup) mList.getParent();
			parent.removeView(mList);
		}
		
		mList.setLayoutParams(new LinearLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));
		addView(mList);
	}

	/**
	 * @return the list inside this pull to refresh container
	 */
	public ListView getList() {
		return mList;
	}

	/**
	 * Sets the view to use as the refresh header. 
	 * <p />
	 * The header view is the view at the top that will show while the list
	 * is refreshing. Usually, this will be a simple rectangle that says "refreshing" and the like.
	 * <p />
	 * 
	 * @param headerView the view to use as the whole header.
	 */
	public void setRefreshHeader(View header) {
		if (mHeaderView != null) {
			mHeaderContainer.removeView(mHeaderView);
		}		

		if (header == null) {
			throw new RuntimeException("Please supply a non-null header container.");
		}

		mHeaderContainer.addView(header, 0);
		mHeaderView = header;
	}

	public void refresh() {	
		changeState(STATE_LOADING);			
	}

	/**
	 * Notifies the pull-to-refresh view that the refreshing is complete.
	 * This will hide the refreshing header.
	 */
	public void completeRefresh() {
		changeState(STATE_IDLE);
	}

	/**
	 * Notifies the listener that the state has changed.
	 */
	private void notifyStateChanged() {
		if (mOnChangeStateListener != null) {
			mOnChangeStateListener.onChangeState(this, mState);
		}
	}

	/**
	 * @param listener the listener to be notified when the header state should change
	 */
	public void setOnChangeStateListener(OnChangeStateListener listener) {
		mOnChangeStateListener = listener;
	}
}
