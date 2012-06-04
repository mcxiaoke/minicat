package com.jfwang213.PullUpdateListViewDemo;



import com.jfwang213.PullUpdateListViewDemo.R;
import android.content.Context;
import android.graphics.Rect;
import android.os.AsyncTask;
import android.os.Handler;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.MeasureSpec;
import android.view.animation.LinearInterpolator;
import android.view.animation.RotateAnimation;
import android.widget.AbsListView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.AbsListView.OnScrollListener;


public class PullUpdateListView extends ListView implements OnScrollListener{

	
	private static final int TAP_TO_REFRESH = 1;
    private static final int PULL_TO_REFRESH = 2;
    private static final int RELEASE_TO_REFRESH = 3;
    private static final int REFRESHING = 4;
    
    private static final int gapHeight = 50;
	private int screenY = 0;
    
	private RotateAnimation mFlipAnimation;

	private RotateAnimation mReverseFlipAnimation;

	private LayoutInflater mInflater;

	private RelativeLayout mRefreshView;

	private TextView mRefreshViewText;

	private ImageView mRefreshViewImage;

	private ProgressBar mRefreshViewProgress;
	
	private int mRefreshState;
	private int mScrollState;
	private int mFirstVisibleItem;
	private int mFirstVisibleItemTop;
	private float mChangeMouseY;
	private float mRefreshViewHeight;
	
	private OnRefreshListener mRefreshListener;
	
	public PullUpdateListView(Context context) {
		super(context);
		InitView(context);
	}
	
	public PullUpdateListView(Context context, AttributeSet attrs) {
        super(context, attrs);
        InitView(context);
    }

    public PullUpdateListView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        InitView(context);
    }
    
    private void InitView(Context context) {
    	mRefreshState = TAP_TO_REFRESH;
    	AddRefreshView(this);
    	this.setOnScrollListener(this);
    }
    
    
    
    private void AddRefreshView(ListView view)
    {
    	Context context = view.getContext();
    	// Load all of the animations we need in code rather than through XML
        mFlipAnimation = new RotateAnimation(0, -180,
                RotateAnimation.RELATIVE_TO_SELF, 0.5f,
                RotateAnimation.RELATIVE_TO_SELF, 0.5f);
        mFlipAnimation.setInterpolator(new LinearInterpolator());
        mFlipAnimation.setDuration(250);
        mFlipAnimation.setFillAfter(true);
        mReverseFlipAnimation = new RotateAnimation(-180, 0,
                RotateAnimation.RELATIVE_TO_SELF, 0.5f,
                RotateAnimation.RELATIVE_TO_SELF, 0.5f);
        mReverseFlipAnimation.setInterpolator(new LinearInterpolator());
        mReverseFlipAnimation.setDuration(250);
        mReverseFlipAnimation.setFillAfter(true);

        mInflater = (LayoutInflater) context.getSystemService(
                Context.LAYOUT_INFLATER_SERVICE);

		mRefreshView = (RelativeLayout) mInflater.inflate(
				R.layout.refreshview, view, false);
        mRefreshViewText =
            (TextView) mRefreshView.findViewById(R.id.pull_to_refresh_text);
        mRefreshViewImage =
            (ImageView) mRefreshView.findViewById(R.id.pull_to_refresh_image);
        mRefreshViewProgress =
            (ProgressBar) mRefreshView.findViewById(R.id.pull_to_refresh_progress);

        mRefreshViewImage.setMinimumHeight(50);
        mRefreshView.setOnClickListener(new OnClickRefreshListener());

        mRefreshState = TAP_TO_REFRESH;
        
        view.addFooterView(mRefreshView);
        
        MeasureView(mRefreshView);
        mRefreshViewHeight = mRefreshView.getMeasuredHeight();
        Log.i(Constants.TAG, "refresh height " + mRefreshViewHeight);
    }
    
    private void MeasureView(View child) {
        ViewGroup.LayoutParams p = child.getLayoutParams();
        if (p == null) {
            p = new ViewGroup.LayoutParams(
                    ViewGroup.LayoutParams.FILL_PARENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT);
        }

        int childWidthSpec = ViewGroup.getChildMeasureSpec(0,
                0 + 0, p.width);
        int lpHeight = p.height;
        int childHeightSpec;
        if (lpHeight > 0) {
            childHeightSpec = MeasureSpec.makeMeasureSpec(lpHeight, MeasureSpec.EXACTLY);
        } else {
            childHeightSpec = MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED);
        }
        child.measure(childWidthSpec, childHeightSpec);
    }
    
    /**
     * Invoked when the refresh view is clicked on. This is mainly used when
     * there's only a few items in the list and it's not possible to drag the
     * list.
     */
    
	private class OnClickRefreshListener implements OnClickListener {

        @Override
        public void onClick(View v) {
            if (mRefreshState != REFRESHING) {
            	OnRefresh();
                Log.i(Constants.TAG, "click to refresh");
            }
        }

    }
	
	private void SetRefreshView() {
		switch(mRefreshState) {
		case TAP_TO_REFRESH:
            // Set refresh view text to the tap label
            mRefreshViewText.setText(R.string.pull_to_refresh_tap_label);
            // Replace refresh drawable with arrow drawable
            mRefreshViewImage.setImageResource(R.drawable.ic_pulltorefresh_arrow);
            // Clear the full rotation animation
            mRefreshViewImage.clearAnimation();
            // Hide progress bar and arrow.
            mRefreshViewImage.setVisibility(View.GONE);
            mRefreshViewProgress.setVisibility(View.GONE);
            break;
		case PULL_TO_REFRESH:
			// Set refresh view text to the pull label
            mRefreshViewText.setText(R.string.pull_to_refresh_pull_label);
            // Replace refresh drawable with arrow drawable
            mRefreshViewImage.setImageResource(R.drawable.ic_pulltorefresh_arrow);
            // Clear the full rotation animation
            mRefreshViewImage.clearAnimation();
            mRefreshViewImage.startAnimation(mFlipAnimation);
            // Hide progress bar and arrow.
            mRefreshViewImage.setVisibility(View.VISIBLE);
            mRefreshViewProgress.setVisibility(View.GONE);
            break;
		case RELEASE_TO_REFRESH:
			// Set refresh view text to the release label
            mRefreshViewText.setText(R.string.pull_to_refresh_release_label);
            // Replace refresh drawable with arrow drawable
            mRefreshViewImage.setImageResource(R.drawable.ic_pulltorefresh_arrow);
            // Clear the full rotation animation
            mRefreshViewImage.clearAnimation();
            mRefreshViewImage.startAnimation(mReverseFlipAnimation);
            // Hide progress bar and arrow.
            mRefreshViewImage.setVisibility(View.VISIBLE);
            mRefreshViewProgress.setVisibility(View.GONE);
            break;
		case REFRESHING:
			// Set refresh view text to the pull label
            mRefreshViewText.setText(R.string.pull_to_refresh_refreshing_label);
            // Replace refresh drawable with arrow drawable
            mRefreshViewImage.setImageResource(R.drawable.ic_pulltorefresh_arrow);
            // Clear the full rotation animation
            mRefreshViewImage.clearAnimation();
            // Hide progress bar and arrow.
            mRefreshViewImage.setVisibility(View.GONE);
            mRefreshViewProgress.setVisibility(View.VISIBLE);
            break;
		}
	}
	private boolean IsTooLessItem() {
		if (this.getFirstVisiblePosition() == 0 &&
				this.getLastVisiblePosition() == this.getAdapter().getCount() - 1) {
			return true;
		}
		return false;
	}
	private void MouseUpCheckScrollFly() {
		if (mScrollState != OnScrollListener.SCROLL_STATE_TOUCH_SCROLL) {
			Log.i(Constants.TAG, "scroll is flying skip it " + mScrollState);
			return;
		}
		if (IsRefreshViewVisible() &&
    			mRefreshView.getTop() < this.screenY) {
    		setSelectionFromTop(mFirstVisibleItem, mFirstVisibleItemTop);
    		Log.i(Constants.TAG, "top " + mRefreshView.getTop() + " " +this.screenY);
    	}
	}
	@Override
    public boolean onTouchEvent(MotionEvent event) {
		if(this.screenY != this.getBottom()) {
			this.screenY = this.getBottom();
			Log.i(Constants.TAG, "screen is " + screenY);
		}		
        switch (event.getAction()) {
            case MotionEvent.ACTION_UP:
            	//Log.i(Constants.Tag, "touch event up");
                if (mRefreshState == RELEASE_TO_REFRESH) {
                	mRefreshState = REFRESHING;
                	Log.i(Constants.TAG, "change to refresh state");
                	SetRefreshView();
                    setRefreshViewPad(0);
                    OnRefresh();
                } else if(mRefreshState == PULL_TO_REFRESH){
                	mRefreshState = TAP_TO_REFRESH;
                	Log.i(Constants.TAG, "change to tap state");
                	SetRefreshView();
                	setRefreshViewPad(0);
                	Handler handler = new Handler(); 
                    handler.postDelayed(new Runnable() { 
                         public void run() { 
                        	 MouseUpCheckScrollFly();
                         } 
                    }, 20);
                	//mRefreshView.setVisibility(View.GONE);
                	
                }
                break;
            case MotionEvent.ACTION_DOWN:
                break;
            case MotionEvent.ACTION_MOVE:
            	//Log.i(Constants.Tag, "touch event move");
            	if (!IsTooLessItem() && mRefreshState != REFRESHING) {
            		dealWithMouseMoveEvent(event);
            	}
                break;
        }
        return super.onTouchEvent(event);
    }
	private void setRefreshViewPad(float nowAddr) {
		if (mRefreshState == PULL_TO_REFRESH ||
				mRefreshState == RELEASE_TO_REFRESH) {
			float pad = mChangeMouseY - nowAddr - mRefreshViewHeight;
			if (pad < 0) {
				pad = 0;
			}
			Log.d(Constants.TAG, "set pad " + pad + "now mouse is " + nowAddr);
			mRefreshView.setPadding(
	                mRefreshView.getPaddingLeft(),
	                0,
	                mRefreshView.getPaddingRight(),
	                (int) pad);
		} else {
			mRefreshView.setPadding(
	                mRefreshView.getPaddingLeft(),
	                0,
	                mRefreshView.getPaddingRight(),
	                0);
		}
	}
	private boolean IsRefreshViewVisible() {
		int refreshViewIndex = this.getAdapter().getCount() - 1;
		Log.i(Constants.TAG, "refreshViewIndex is " + refreshViewIndex);
		return refreshViewIndex <= this.getLastVisiblePosition();
		
	}
	private void dealWithMouseMoveEvent(MotionEvent event) {
		Log.d(Constants.TAG, "top is " + mRefreshView.getTop() +
				" visible " + mRefreshView.getVisibility());
		
		Log.d(Constants.TAG, "listview top is " + this.getTop() +
				" bottom is " + this.getBottom());
		
		Log.d(Constants.TAG, "first view index is " + this.getFirstVisiblePosition());
		Rect rect = new Rect();
		mRefreshView.getWindowVisibleDisplayFrame(rect);
		Log.d(Constants.TAG, "refresh view lefttop is " + rect.left + "*" + rect.top +
				" rigthbottom is " + rect.right + "*" + rect.bottom);
		switch(this.mRefreshState) {
		case TAP_TO_REFRESH:
			if (IsRefreshViewVisible() &&
					mRefreshView.getTop() < this.screenY && mRefreshView.getTop() > 0) {
				mChangeMouseY = event.getY();
				this.computeScroll();
				mRefreshState = PULL_TO_REFRESH;
				Log.i(Constants.TAG, "change to pull state top " + mRefreshView.getTop());
				SetRefreshView();
				mFirstVisibleItem = this.getFirstVisiblePosition();
				
				View firstView = this.getChildAt(0);
				mFirstVisibleItemTop = firstView.getTop();
			} else {
				Log.d(Constants.TAG, " " + mRefreshView.getTop() + " screen is " +
						this.screenY + " scroll " + this.getScrollY());
			}
			break;
		case PULL_TO_REFRESH:
			if (mRefreshView.getTop() > this.screenY) {
				mChangeMouseY = -1;
				mRefreshState = TAP_TO_REFRESH;
				Log.i(Constants.TAG, "change to tap state " + mRefreshView.getTop());
				SetRefreshView();
			} else if (mRefreshView.getTop() < this.screenY - gapHeight) {
				mRefreshState = RELEASE_TO_REFRESH;
				Log.i(Constants.TAG, "change to release state");
				SetRefreshView();
			} else {
				setRefreshViewPad(event.getY());
			}
			break;
		case RELEASE_TO_REFRESH:
			if (mRefreshView.getTop() > this.screenY - gapHeight) {
				mRefreshState = PULL_TO_REFRESH;
				Log.i(Constants.TAG, "change to pull state");
				SetRefreshView();
			} else {
				setRefreshViewPad(event.getY());
			}
			break;
		case REFRESHING:
			break;
		}
	}
	@Override
	public void onScroll(AbsListView arg0, int firstVisibleItem,
            int visibleItemCount, int totalItemCount) {
		// TODO Auto-generated method stub
		Log.i(Constants.TAG, "List View OnScoll first " + firstVisibleItem + 
				" visible count " + visibleItemCount + " total count " + totalItemCount);
	}
	@Override
	public void onScrollStateChanged(AbsListView view, int scrollState) {
		// TODO Auto-generated method stub
		mScrollState = scrollState;
		switch (scrollState) {
        case OnScrollListener.SCROLL_STATE_IDLE:
            Log.i(Constants.TAG, "stop to scroll:SCROLL_STATE_IDLE");
            break;
        case OnScrollListener.SCROLL_STATE_FLING:
            Log.i(Constants.TAG, "start to scroll:SCROLL_STATE_FLING");
            break;
        case OnScrollListener.SCROLL_STATE_TOUCH_SCROLL:
        	Log.i(Constants.TAG, "are scrolling:SCROLL_STATE_TOUCH_SCROLL");
            break;
        }
		
		if (mScrollState == OnScrollListener.SCROLL_STATE_IDLE &&
				IsRefreshViewVisible() && mRefreshView.getTop() < this.screenY) {
			this.setSelectionFromTop(this.getLastVisiblePosition(), this.screenY);
		}
	}
	
	public void SetRefreshListener(OnRefreshListener l) {
		mRefreshListener = l;
	}
	private void OnRefresh() {
		ReadDataBaseTask task = new ReadDataBaseTask();
		task.execute();
	}
	public interface OnRefreshListener {
		public void OnRefresh();
	}
	
	private class ReadDataBaseTask extends AsyncTask<Void, Void, Void> {

		@Override
		protected void onPostExecute(Void result) {
			mRefreshState = TAP_TO_REFRESH;
			SetRefreshView();
			setSelectionFromTop(getLastVisiblePosition(), screenY);
		};
		@Override
		protected Void doInBackground(Void... arg0) {
			if(mRefreshListener != null) {
				mRefreshListener.OnRefresh();
			}
			return null;
		}
	}

}
