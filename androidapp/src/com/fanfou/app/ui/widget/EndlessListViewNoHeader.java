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
import com.fanfou.app.util.OptionHelper;

/**
 * @author mcxiaoke
 * 
 */
public class EndlessListViewNoHeader extends ListView implements
		OnItemClickListener {
	private static final String TAG = EndlessListViewNoHeader.class
			.getSimpleName();

	protected static final int FOOTER_NONE = 0;
	protected static final int FOOTER_HIDE = 1;
	protected static final int FOOTER_NORMAL = 2;
	protected static final int FOOTER_LOADING = 3;

	protected static final int MAX_OVERSCROLL_Y = 240;

	Context mContext;
	LayoutInflater mInflater;

	ViewGroup mLoadMoreView;
	ProgressBar mLoadMoreProgressView;
	TextView mLoadMoreTextView;

	OnLoadDataListener mOnRefreshListener;

	protected boolean isLoading;
	protected boolean isRefresh;

	protected View curPosView;
	protected int curPos;
	protected int curPosTop;

	public EndlessListViewNoHeader(Context context) {
		super(context);
		init(context);
	}

	public EndlessListViewNoHeader(Context context, AttributeSet attrs) {
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

		boolean fastScroll = OptionHelper.readBoolean(
				R.string.option_fast_scroll_on, false);
		if (fastScroll) {
			setFastScrollEnabled(true);
		}

		initHeaderAndFooter();
	}

	private void initHeaderAndFooter() {
		mInflater = LayoutInflater.from(mContext);
		mLoadMoreView = (ViewGroup) mInflater.inflate(R.layout.list_footer,
				null);
		mLoadMoreProgressView = (ProgressBar) mLoadMoreView
				.findViewById(R.id.list_footer_progress);
		mLoadMoreTextView = (TextView) mLoadMoreView
				.findViewById(R.id.list_footer_text);
		addFooterView(mLoadMoreView);

		setCacheColorHint(0);
		setSelector(getResources().getDrawable(R.drawable.list_selector));
		setDivider(getResources().getDrawable(R.drawable.separator));

	}

	public void removeFooter() {
		if (getFooterViewsCount() == 1) {
			removeFooterView(mLoadMoreView);
		}
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

	public void addFooter() {
		if (noFooter()) {
			addFooterView(mLoadMoreView);
		}
	}

	public boolean noFooter() {
		return getFooterViewsCount() == 0;
	}

	public void savePosition() {
		curPos = getFirstVisiblePosition();
		View v = getChildAt(curPos);
		curPosTop = (v == null) ? 0 : v.getTop();
	}

	public void restorePosition() {
		setSelectionFromTop(curPos, curPosTop);
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
			if (position == parent.getCount() - 1) {
				setLoading();
			}
		} else {
			if (mOnRefreshListener != null) {
				mOnRefreshListener.onItemClick(this, position);
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

	public void setOnRefreshListener(OnLoadDataListener li) {
		mOnRefreshListener = li;
	}

	public interface OnLoadDataListener {

		public void onLoadMore(EndlessListViewNoHeader view);

		public void onItemClick(EndlessListViewNoHeader view, int position);
	}
}
