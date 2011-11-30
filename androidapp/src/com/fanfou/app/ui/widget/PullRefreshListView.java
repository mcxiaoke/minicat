package com.fanfou.app.ui.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.fanfou.app.App;
import com.fanfou.app.R;
import com.fanfou.app.util.OptionHelper;
import com.markupartist.android.widget.PullToRefreshListView;

/**
 * @author mcxiaoke
 * @version 2.0 2011.11.30
 * 
 */
public class PullRefreshListView extends PullToRefreshListView implements
		OnItemClickListener {
	private static final String TAG = PullRefreshListView.class
			.getSimpleName();

	protected static final int FOOTER_HIDE = 1;
	protected static final int FOOTER_NORMAL = 2;
	protected static final int FOOTER_LOADING = 3;

	Context mContext;
	LayoutInflater mInflater;

	ViewGroup mLoadMoreView;
	ProgressBar mLoadMoreProgressView;
	TextView mLoadMoreTextView;

	OnLoadDataListener onLoadDataListener;

	protected boolean isLoading;
	protected boolean isRefresh;

	protected View curPosView;
	protected int curPos;
	protected int curPosTop;

	public PullRefreshListView(Context context) {
		super(context);
		init(context);
	}

	public PullRefreshListView(Context context, AttributeSet attrs) {
		super(context, attrs);
		init(context);
	}

	void log(String message) {
		Log.d(TAG, message);
	}
	
	@Override
	protected void init(Context context) {
		super.init(context);
		init2(context);

	}
	
	private void init2(Context context){
		mContext=context;
		mInflater = LayoutInflater.from(mContext);
		
		setCacheColorHint(0);
		setHorizontalScrollBarEnabled(false);
		setVerticalScrollBarEnabled(false);
		setSelector(getResources().getDrawable(R.drawable.list_selector));
		setDivider(getResources().getDrawable(R.drawable.separator));

		boolean fastScroll = OptionHelper.readBoolean(mContext,
				R.string.option_fast_scroll_on, false);
		if (fastScroll) {
			setFastScrollEnabled(true);
		}
		setOnItemClickListener(this);
		initFooter();
	}

	private void initFooter() {
		mLoadMoreView = (ViewGroup) mInflater.inflate(R.layout.list_footer,
				null);
		mLoadMoreProgressView = (ProgressBar) mLoadMoreView
				.findViewById(R.id.list_footer_progress);
		mLoadMoreTextView = (TextView) mLoadMoreView
				.findViewById(R.id.list_footer_text);
		addFooterView(mLoadMoreView);
	}

	public void removeFooter() {
		if (getFooterViewsCount() == 1) {
			removeFooterView(mLoadMoreView);
		}
	}

	protected void setFooterStatus(int status) {
		if (noFooter()) {
			return;
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
			 isLoading = true;
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
		setFooterStatus(FOOTER_LOADING);
		if (onLoadDataListener != null) {
			if (App.DEBUG)
				log("onLoadMore()");
			onLoadDataListener.onLoadMore(this);
		}
	}

	public void onLoadMoreComplete() {
		setFooterStatus(FOOTER_NORMAL);
	}

	public void onNoLoadMore() {
		setFooterStatus(FOOTER_HIDE);
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
			if (position == parent.getCount() - 1) {
				setLoading();
			}
		} else {
			if (onLoadDataListener != null) {
				onLoadDataListener.onItemClick(this, view,position);
			}
		}
	}

	public void setListSelection(final int pos) {
		post(new Runnable() {
			@Override
			public void run() {
				setSelection(pos);
			}
		});
	}

	public void setOnLoadDataListener(OnLoadDataListener li) {
		onLoadDataListener = li;
	}

	public interface OnLoadDataListener {

		public void onLoadMore(PullRefreshListView view);

		public void onItemClick(PullRefreshListView listView, View view,int position);
	}
}
