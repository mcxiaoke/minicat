package com.fanfou.app.ui.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.fanfou.app.App;
import com.fanfou.app.R;

/**
 * @author mcxiaoke
 * 
 */
public class EndlessListView extends ListView implements OnItemClickListener {
	private static final String TAG = EndlessListView.class.getSimpleName();

	protected static final int FOOTER_NONE = 0;
	protected static final int FOOTER_HIDE = 1;
	protected static final int FOOTER_NORMAL = 2;
	protected static final int FOOTER_LOADING = 3;

	protected static final int HEADER_NONE = 10;
	protected static final int HEADER_HIDE = 11;
	protected static final int HEADER_NORMAL = 12;
	protected static final int HEADER_LOADING = 13;

	protected static final int MAX_OVERSCROLL_Y = 240;

	Context mContext;
	LayoutInflater mInflater;

	ViewGroup mRefershView;
	ProgressBar mRefreshProgressView;
	TextView mRefreshTextView;

	ViewGroup mLoadMoreView;
	ProgressBar mLoadMoreProgressView;
	TextView mLoadMoreTextView;

	// protected int mScrollState;
	// protected int mFirstVisible;
	// protected int mLastFirstVisible;
	// protected int mVisibleItemCount;
	// protected int mTotalItemCount;
	// protected int mMaxOverScrollY;

	OnRefreshListener mOnRefreshListener;

	protected boolean isLoading;
	protected boolean isRefresh;

	public EndlessListView(Context context) {
		super(context);
		init(context);
	}

	public EndlessListView(Context context, AttributeSet attrs) {
		super(context, attrs);
		init(context);
	}

	void log(String message) {
		Log.d(TAG, message);
	}

	private void init(Context context) {
		mContext = context;
		setHorizontalScrollBarEnabled(false);
		setVerticalScrollBarEnabled(false);
		setOnItemClickListener(this);

		setCacheColorHint(0);
		setSelector(getResources().getDrawable(R.drawable.list_selector));
		setDivider(getResources().getDrawable(R.drawable.separator));

		initHeaderAndFooter();
	}

	private void initHeaderAndFooter() {
		mInflater = LayoutInflater.from(mContext);

		mRefershView = (ViewGroup) mInflater
				.inflate(R.layout.list_header, null);
		mRefreshProgressView = (ProgressBar) mRefershView
				.findViewById(R.id.list_header_progress);
		mRefreshTextView = (TextView) mRefershView
				.findViewById(R.id.list_header_text);
		addHeaderView(mRefershView);

		mLoadMoreView = (ViewGroup) mInflater.inflate(R.layout.list_footer,
				null);
		mLoadMoreProgressView = (ProgressBar) mLoadMoreView
				.findViewById(R.id.list_footer_progress);
		mLoadMoreTextView = (TextView) mLoadMoreView
				.findViewById(R.id.list_footer_text);
		addFooterView(mLoadMoreView);
	}

	public void removeHeader() {
		if (getHeaderViewsCount() == 1) {
			removeHeaderView(mRefershView);
		}
	}

	public void removeFooter() {
		if (getFooterViewsCount() == 1) {
			removeFooterView(mLoadMoreView);
		}
	}

	protected void setHeaderStatus(int status) {
		if (status == HEADER_NONE) {
			isRefresh = false;
			removeHeaderView(mRefershView);
			return;
		}

		if (status == HEADER_HIDE) {
			isRefresh = false;
			mRefershView.setVisibility(View.GONE);
		} else if (status == HEADER_NORMAL) {
			isRefresh = false;
			mRefershView.setVisibility(View.VISIBLE);
			mRefreshProgressView.setVisibility(View.GONE);
			mRefreshTextView.setVisibility(View.VISIBLE);
		} else if (status == HEADER_LOADING) {
			// isRefresh = true;
			mRefershView.setVisibility(View.VISIBLE);
			mRefreshProgressView.setVisibility(View.VISIBLE);
			mRefreshTextView.setVisibility(View.GONE);
		}
	}

	public boolean isRefreshing() {
		return isRefresh;
	}

	public void setRefreshing() {
		if (isRefresh) {
			return;
		}
		if (App.DEBUG)
			log("setHeaderStatus(HEADER_LOADING);");
		isRefresh = true;
		setHeaderStatus(HEADER_LOADING);
		if (mOnRefreshListener != null) {
			if (App.DEBUG)
				log("onRefresh()");
			mOnRefreshListener.onRefresh(this);
		}
	}

	public void onRefreshComplete() {
		if (isRefresh) {
			if (App.DEBUG)
				log("onRefreshComplete()");
			setListSelection(1);
			setHeaderStatus(HEADER_NORMAL);
		}
	}

	public void onNoRefresh() {
		setHeaderStatus(HEADER_NONE);
	}

	protected void setFooterStatus(int status) {
		if (status == FOOTER_NONE) {
			isLoading = false;
			removeFooterView(mLoadMoreView);
			return;
		}

		if (noFooter()) {
			addFooterView(mLoadMoreView);
		}

		if (status == FOOTER_HIDE) {
			isLoading = false;
			mLoadMoreView.setVisibility(View.GONE);
		} else if (status == FOOTER_NORMAL) {
			isLoading = false;
			mLoadMoreView.setVisibility(View.VISIBLE);
			mLoadMoreProgressView.setVisibility(View.GONE);
			mLoadMoreTextView.setVisibility(View.VISIBLE);
		} else if (status == FOOTER_LOADING) {
			// isLoading = true;
			mLoadMoreView.setVisibility(View.VISIBLE);
			mLoadMoreProgressView.setVisibility(View.VISIBLE);
			mLoadMoreTextView.setVisibility(View.GONE);
		}
	}

	public boolean isLoading() {
		return isLoading;
	}

	public void setLoading() {
		if (isLoading) {
			return;
		}
		if (App.DEBUG)
			log("setFooterStatus(FOOTER_LOADING);");
		isLoading = true;
		setFooterStatus(FOOTER_LOADING);
		if (mOnRefreshListener != null) {
			if (App.DEBUG)
				log("onLoadMore()");
			mOnRefreshListener.onLoadMore(this);
		}
	}

	public void onLoadMoreComplete() {
		if (App.DEBUG)
			log("onLoadMoreComplete()");
		setFooterStatus(FOOTER_NORMAL);
	}

	public void onNoLoadMore() {
		setFooterStatus(FOOTER_NONE);
	}

	public boolean noHeader() {
		return getHeaderViewsCount() == 0;
	}

	public void addHeader() {
		if (noHeader()) {
			addHeaderView(mRefershView);
		}
	}

	public void addFooter() {
		if (noFooter()) {
			addFooterView(mLoadMoreView);
		}
	}

	public boolean noFooter() {
		return getFooterViewsCount() == 0;
	}

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position,
			long id) {
		if (App.DEBUG)
			log("onItemClick() list.size=" + parent.getCount()
					+ " adapter.size=" + parent.getAdapter().getCount()
					+ " position=" + position + " id=" + id);
		Object o = parent.getItemAtPosition(position);
		if (o == null) {
			if (position == 0) {
				setRefreshing();
			} else if (position == parent.getCount() - 1) {
				setLoading();
			}
		} else {
			if (mOnRefreshListener != null) {
				mOnRefreshListener.onItemClick(this, view, position);
			}
		}
	}

	public void setListSelection(final int pos) {
		post(new Runnable() {
			@Override
			public void run() {
				setSelection(pos);
				// View v = getChildAt(pos);
				// if (v != null) {
				// v.requestFocus();
				// }
			}
		});
	}

	protected void reachTop() {
		if (App.DEBUG) {
			Log.d(TAG, "reachTop()");
		}
		setRefreshing();
	}

	protected void reachBottom() {
		if (App.DEBUG) {
			Log.d(TAG, "readBottom()");
		}
		if (!noFooter()) {
			setLoading();
		}
	}

	// @Override
	// public void onScrollStateChanged(AbsListView view, int scrollState) {
	// if(Build.VERSION.SDK_INT<9){
	// return;
	// }
	// mScrollState = scrollState;
	// switch (scrollState) {
	// case SCROLL_STATE_FLING:
	// if(App.DEBUG){
	// Log.d(TAG, "FLING mFirstVisible=" + mFirstVisible);
	// }
	// break;
	// case SCROLL_STATE_IDLE:
	// if(App.DEBUG){
	// Log.d(TAG, "IDLE mFirstVisible=" + mFirstVisible
	// + " mLastFirstVisible=" + mLastFirstVisible);
	// }
	// if ( mLastFirstVisible + mVisibleItemCount>= mTotalItemCount
	// && mFirstVisible + mVisibleItemCount>= mTotalItemCount) {
	// reachBottom();
	// } else if (mLastFirstVisible <3 && mFirstVisible == 0) {
	// reachTop();
	// }
	// break;
	// case SCROLL_STATE_TOUCH_SCROLL:
	// mLastFirstVisible = mFirstVisible;
	// if(App.DEBUG){
	// Log.i(TAG, "TOUCH_SCROLL mFirstVisible=" + mFirstVisible);
	// }
	// break;
	// default:
	// break;
	// }
	// }

	// @Override
	// public void onScroll(AbsListView view, int firstVisibleItem,
	// int visibleItemCount, int totalItemCount) {
	// mFirstVisible = firstVisibleItem;
	// mVisibleItemCount = visibleItemCount;
	// mTotalItemCount = totalItemCount;
	// }

	// @Override
	// protected boolean overScrollBy(int deltaX, int deltaY, int scrollX,
	// int scrollY, int scrollRangeX, int scrollRangeY,
	// int maxOverScrollX, int maxOverScrollY, boolean isTouchEvent) {
	// return super.overScrollBy(deltaX, deltaY, scrollX, scrollY,
	// scrollRangeX, scrollRangeY, maxOverScrollX, mMaxOverScrollY,
	// isTouchEvent);
	// }

	public void setOnRefreshListener(OnRefreshListener li) {
		mOnRefreshListener = li;
	}

	public interface OnRefreshListener {
		public void onRefresh(ListView view);

		public void onLoadMore(ListView view);

		public void onItemClick(ListView view, View row, int position);
	}
}
