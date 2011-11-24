/**
 * Use this however you want, but it might not work how you'd like.
 * If you fix it or make it better, share the wealth.
 * If you find a problem, let it be known.
 * 
 * https://github.com/timahoney/Android-Pull-To-Refresh
 * https://github.com/wdx700/Android-Pull-To-Refresh
 * 
 * modified by mcxiaoke at 2011.11.23
 * 
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
/**
 * @author mcxiaoke
 * @version 2.0 2011.11.23
 * @version 2.0 2011.11.24
 *
 */
public class PullRefreshView extends LinearLayout {	
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
		public void onChangeState(PullRefreshView container, int state);
	}
	
	public interface OnRefreshListener{
		public void onRefresh(PullRefreshView container);
	}
	
	public interface OnLoadMoreListener{
		public void onLoadMore(PullRefreshView container);
	}
	
	
	private static final LinearLayout.LayoutParams MATCH_PARENT=new LinearLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);

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
	
	
	public static final int POSITION_NONE=100;
	public static final int POSITION_HEADER=101;
	public static final int POSITION_FOOTER=102;

	private LinearLayout mHeaderContainer;
	private View mHeaderView;
	private LinearLayout mFooterContainer;
	private View mFooterView;
	private ListView mListView;
	private int[] mState=new int[2];
	private OnChangeStateListener mOnChangeStateListener;
	
	private OnRefreshListener mOnRefreshListener;
	private OnLoadMoreListener mOnLoadMoreListener;
	
	private int position=POSITION_NONE;
	
	private int REFRESH_VIEW_HEIGHT = 100;
	
	/**
	 * Creates a new pull to refresh container.
	 * 
	 * @param context the application context
	 */
	public PullRefreshView(Context context) {
		super(context);
		init(context);
	}

	/**
	 * Creates a new pull to refresh container.
	 * 
	 * @param context the application context
	 * @param attrs the XML attribute set
	 */
	public PullRefreshView(Context context, AttributeSet attrs) {
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
	public PullRefreshView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs);
		init(context);
	}

	private void init(Context context) {
		mState[0] = STATE_IDLE; // Start out as idle.
		mState[1] = STATE_IDLE;
		
		float densityFactor = context.getResources().getDisplayMetrics().density;
    	REFRESH_VIEW_HEIGHT *= densityFactor;

		// We don't want to see the fading edge on the container.
		setVerticalFadingEdgeEnabled(false);
		setVerticalScrollBarEnabled(false);
		setHorizontalScrollBarEnabled(false);
		
		setOrientation(LinearLayout.VERTICAL);

		// Set the default header.
		mHeaderContainer = new LinearLayout(context);
		addView(mHeaderContainer);
		setHeaderViewHeight(1);
		TextView headerView = new TextView(context);
		headerView.setText("Default refresh header.");
		setHeader(headerView);
		
		// set default list
		ListView list = new ListView(context);
		setListView(list);
		
		// Set the default footer.
		mFooterContainer=new LinearLayout(context);
		addView(mFooterContainer);
		setFooterViewHeight(1);
		TextView footerView = new TextView(context);
		headerView.setText("Default refresh footer.");
		setFooter(footerView);
	}

	private boolean mScrollingList = true;
	private float mInterceptY1;
	private int mLastMotionY1;
	
	@Override
	public boolean dispatchTouchEvent (MotionEvent ev) {
		float oldLastY1 = mInterceptY1;
		mInterceptY1 = ev.getY();

		if (mState[0] == STATE_LOADING) {
    		return super.dispatchTouchEvent(ev);
    	}
		
		switch (ev.getAction()) {
		case MotionEvent.ACTION_DOWN:
			mLastMotionY1 = (int) ev.getY();
			mScrollingList = true;
			return super.dispatchTouchEvent(ev);
			
		case MotionEvent.ACTION_MOVE:
			if (mListView.getFirstVisiblePosition() == 0
					&& (mListView.getChildCount() == 0 || mListView.getChildAt(0).getTop() == 0)) {
				if ((mInterceptY1 - oldLastY1 > 5) || (mState[0] == STATE_PULL) || (mState[0] == STATE_RELEASE)) {
					mScrollingList = false;
					applyHeaderHeight(ev);
					return true;
				} else {
					mScrollingList = true;
//					return super.dispatchTouchEvent(ev);
				}
			} 
//			else if (mScrollingList) {
//				return super.dispatchTouchEvent(ev);
//			} else {
//				return super.dispatchTouchEvent(ev);
//			}
			return super.dispatchTouchEvent(ev);
		case MotionEvent.ACTION_UP:
		case MotionEvent.ACTION_CANCEL:
        	if (mState[0] == STATE_RELEASE) {
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
                updateHeaderView(historicalY - mLastMotionY1);
	        }
        } else {
        	int historicalY = (int) ev.getY();
        	updateHeaderView(historicalY - mLastMotionY1);
        }
    }
	
	private void updateHeaderView(int height) {
		if (height <= 0) {
        	return;
        }

        if ((REFRESH_VIEW_HEIGHT/4 <= mCurHeaderViewHeight) && (mCurHeaderViewHeight < REFRESH_VIEW_HEIGHT)) {
        	setHeaderViewHeight(height);
        	changeState(STATE_PULL);
        } else if (mCurHeaderViewHeight >= REFRESH_VIEW_HEIGHT) {
        	if (height > REFRESH_VIEW_HEIGHT) {
        		height = (int) (REFRESH_VIEW_HEIGHT + (height - REFRESH_VIEW_HEIGHT) *  REFRESH_VIEW_HEIGHT * 1.0f/height);
        	}
        	
        	setHeaderViewHeight(height);
        	changeState(STATE_RELEASE);
        } else {
        	setHeaderViewHeight(height);
        }
	}
	
	private void updateFooterView(int height) {
		if (height <= 0) {
        	return;
        }

        if ((REFRESH_VIEW_HEIGHT/4 <= mCurFooterViewHeight) && (mCurFooterViewHeight < REFRESH_VIEW_HEIGHT)) {
        	setFooterViewHeight(height);
        	changeState(STATE_PULL);
        } else if (mCurFooterViewHeight >= REFRESH_VIEW_HEIGHT) {
        	if (height > REFRESH_VIEW_HEIGHT) {
        		height = (int) (REFRESH_VIEW_HEIGHT + (height - REFRESH_VIEW_HEIGHT) *  REFRESH_VIEW_HEIGHT * 1.0f/height);
        	}
        	
        	setFooterViewHeight(height);
        	changeState(STATE_RELEASE);
        } else {
        	setFooterViewHeight(height);
        }
	}
	
	private int mCurHeaderViewHeight = 60;
    private void setHeaderViewHeight(int height) {
    	if (mCurHeaderViewHeight == height) {
    		return;
    	}

    	if (height == 1) {
    		mHeaderContainer.setLayoutParams(new LayoutParams(1, 1));
    	} else {
    		mHeaderContainer.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT, height));
    	}
    	mCurHeaderViewHeight = height;
    }
    
	private int mCurFooterViewHeight = 60;
    private void setFooterViewHeight(int height) {
    	if (mCurFooterViewHeight == height) {
    		return;
    	}

    	if (height == 1) {
    		mFooterContainer.setLayoutParams(new LayoutParams(1, 1));
    	} else {
    		mFooterContainer.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT, height));
    	}
    	mCurFooterViewHeight = height;
    }
	
	private void changeState(int state) {
		switch (state) {
		case STATE_IDLE:
			if(position==POSITION_HEADER){
				setHeaderViewHeight(1);
			}else if(position==POSITION_FOOTER){
				setFooterViewHeight(1);
			}
			break;
		case STATE_PULL:
			break;
		case STATE_RELEASE:
			break;
		case STATE_LOADING:
			if(position==POSITION_HEADER){
				setHeaderViewHeight(REFRESH_VIEW_HEIGHT);
				if(mOnRefreshListener!=null){
					mOnRefreshListener.onRefresh(this);
				}
			}else if(position==POSITION_FOOTER){
				setFooterViewHeight(REFRESH_VIEW_HEIGHT);
				if(mOnLoadMoreListener!=null){
					mOnLoadMoreListener.onLoadMore(this);
				}
			}

			break;
		}
		
		mState[0] = state;
		notifyStateChanged();
	}

	/**
	 * Sets the list to be used in this pull to refresh container.
	 * @param list the list to use
	 */

	public void setListView(ListView list) {
		if (mListView != null) {
			removeView(mListView);
		}
		mListView = list;
		if (mListView.getParent() != null) {
			ViewGroup parent = (ViewGroup) mListView.getParent();
			parent.removeView(mListView);
		}
		
		mListView.setLayoutParams(MATCH_PARENT);
		addView(mListView);
	}

	/**
	 * @return the list inside this pull to refresh container
	 */
	public ListView getListView() {
		return mListView;
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
	public void setHeader(View header) {
		if (mHeaderView != null) {
			mHeaderContainer.removeView(mHeaderView);
		}		

		if (header == null) {
			throw new RuntimeException("Please supply a non-null header container.");
		}

		mHeaderContainer.addView(header, 0);
		mHeaderView = header;
	}
	
	public void setFooter(View header) {
		if (mFooterView != null) {
			mFooterContainer.removeView(mFooterView);
		}		

		if (header == null) {
			throw new RuntimeException("Please supply a non-null footer container.");
		}

		mFooterContainer.addView(header, 0);
		mFooterView = header;
	}

	public void refresh() {	
		changeState(STATE_LOADING);			
	}

	/**
	 * Notifies the pull-to-refresh view that the refreshing is complete.
	 * This will hide the refreshing header.
	 */
	public void refreshComplete() {
		changeState(STATE_IDLE);
	}

	/**
	 * Notifies the listener that the state has changed.
	 */
	private void notifyStateChanged() {
		if (mOnChangeStateListener != null) {
			mOnChangeStateListener.onChangeState(this, mState[0]);
		}
	}

	/**
	 * @param listener the listener to be notified when the header state should change
	 */
	public void setOnChangeStateListener(OnChangeStateListener listener) {
		mOnChangeStateListener = listener;
	}
	
	public void setOnRefreshListener(OnRefreshListener li){
		mOnRefreshListener=li;
	}
	
	
	public void setOnLoadMoreListener(OnLoadMoreListener li){
		mOnLoadMoreListener=li;
	}
	
	
	
	
	
}
