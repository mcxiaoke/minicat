package com.fanfou.app;

import java.util.ArrayList;
import java.util.List;

import android.app.SearchManager;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Parcelable;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.ListView;

import com.fanfou.app.App.ApnType;
import com.fanfou.app.adapter.SearchResultsAdapter;
import com.fanfou.app.api.Api;
import com.fanfou.app.api.ApiException;
import com.fanfou.app.api.FanFouApi;
import com.fanfou.app.api.Status;
import com.fanfou.app.service.Constants;
import com.fanfou.app.ui.ActionBar;
import com.fanfou.app.ui.UIManager;
import com.fanfou.app.util.StringHelper;
import com.fanfou.app.util.Utils;
import com.handmark.pulltorefresh.library.PullToRefreshBase.OnRefreshListener;
import com.handmark.pulltorefresh.library.PullToRefreshListView;

/**
 * @author mcxiaoke
 * @version 1.0 2011.08.05
 * @version 1.1 2011.10.12
 * @version 1.5 2011.10.24
 * @version 1.6 2011.11.21
 * @version 1.7 2011.11.25
 * @version 2.0 2012.01.31
 * @version 2.1 2012.02.01
 * 
 */
public class SearchResultsPage extends BaseActivity implements
		OnRefreshListener, OnItemClickListener, OnItemLongClickListener {
	private static final String TAG = SearchResultsPage.class.getSimpleName();

	protected ActionBar mActionBar;
	private PullToRefreshListView mPullToRefreshListView;
	private ListView mList;

	protected SearchResultsAdapter mStatusAdapter;

	private List<Status> mStatuses;

	protected String keyword;
	protected String maxId;

	private Api api;

	private void log(String message) {
		Log.d(TAG, message);
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		initialize();
		setLayout();
		search();
	}

	@Override
	protected void onNewIntent(Intent intent) {
		setIntent(intent);
		search();
	}

	protected void initialize() {
		mStatuses = new ArrayList<Status>();
		api = FanFouApi.newInstance();
	}

	private void setLayout() {
		setContentView(R.layout.list_pull);
		setActionBar();
		mPullToRefreshListView = (PullToRefreshListView) findViewById(R.id.list);
		mPullToRefreshListView.setOnRefreshListener(this);
		mList = mPullToRefreshListView.getRefreshableView();
		mList.setOnItemClickListener(this);
		mList.setOnItemLongClickListener(this);
		mStatusAdapter = new SearchResultsAdapter(this, mStatuses);
		mList.setAdapter(mStatusAdapter);
	}

	protected void search() {
		parseIntent();
		mStatuses.clear();
		doSearch(true);
		mPullToRefreshListView.setRefreshing();

	}

	protected void parseIntent() {
		Intent intent = getIntent();
		if (Intent.ACTION_SEARCH.equals(intent.getAction())) {
			keyword = intent.getStringExtra(SearchManager.QUERY);
			if (App.DEBUG) {
				log("parseIntent() keyword=" + keyword);
			}
		} else if (Intent.ACTION_VIEW.equals(intent.getAction())) {
			Uri data = intent.getData();
			if (data != null) {
				keyword = data.getLastPathSegment();
				log("parseIntent() keyword=" + keyword);
			}
		}
	}

	/**
	 * 初始化和设置ActionBar
	 */
	private void setActionBar() {
		mActionBar = (ActionBar) findViewById(R.id.actionbar);
		mActionBar.setTitle("搜索结果");
		mActionBar.setTitleClickListener(this);
		mActionBar.setRightAction(new ActionBar.SearchAction(this));
	}

	private void doSearch(boolean reset) {
		if (keyword != null) {
			if (App.DEBUG) {
				log("doSearch() keyword=" + keyword);
			}
			mActionBar.setTitle(keyword);
			if(reset){
				maxId=null;
			}
			new SearchTask().execute();
			mPullToRefreshListView.setRefreshing();
		}

	}

	protected void updateUI(boolean noMore) {
		mStatusAdapter.updateDataAndUI(mStatuses, keyword);
		mPullToRefreshListView.onRefreshComplete();
	}

	private static final String LIST_STATE = "listState";
	private Parcelable mState = null;

	@Override
	protected void onResume() {
		super.onResume();
		if (mState != null && mList != null) {
			mList.onRestoreInstanceState(mState);
			mState = null;
		}
	}

	@Override
	protected void onRestoreInstanceState(Bundle savedInstanceState) {
		super.onRestoreInstanceState(savedInstanceState);
		mState = savedInstanceState.getParcelable(LIST_STATE);
	}

	@Override
	protected void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		if (mList != null) {
			mState = mList.onSaveInstanceState();
			outState.putParcelable(LIST_STATE, mState);
		}
	}

	@Override
	protected void onPause() {
		super.onPause();
	}

	private class SearchTask extends AsyncTask<Void, Void, List<Status>> {

		@Override
		protected void onPreExecute() {
			if(maxId==null){
				mStatuses.clear();
				mStatusAdapter.notifyDataSetChanged();
			}
		}

		@Override
		protected void onPostExecute(List<com.fanfou.app.api.Status> result) {
			mPullToRefreshListView.onRefreshComplete();
			if (result != null && result.size() > 0) {

				int size = result.size();
				log("result size=" + size);
				maxId = result.get(size - 1).id;
				log("maxId=" + maxId);

				mStatuses.addAll(result);
				updateUI(size < 20);
			}
			mPullToRefreshListView.onRefreshComplete();
		}

		@Override
		protected List<com.fanfou.app.api.Status> doInBackground(Void... params) {
			if (StringHelper.isEmpty(keyword)) {
				return null;
			}
			List<com.fanfou.app.api.Status> result = null;

			int count = Constants.DEFAULT_TIMELINE_COUNT;
			if (App.getApnType() == ApnType.WIFI) {
				count = Constants.MAX_TIMELINE_COUNT;
			}
			try {
				result = api.search(keyword, null, maxId, count,
						Constants.FORMAT, Constants.MODE);
			} catch (ApiException e) {
				if (App.DEBUG)
					e.printStackTrace();
			}

			return result;
		}

	}

	@Override
	public boolean onItemLongClick(AdapterView<?> parent, View view,
			int position, long id) {
		final Status s = (Status) parent.getItemAtPosition(position);
		showPopup(view, s);
		return true;
	}

	private void showPopup(final View view, final Status s) {
		if (s == null || s.isNull()) {
			return;
		}
		UIManager.showPopup(this, view, s, mStatusAdapter, mStatuses);
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.actionbar_title:
			goTop();
			break;
		default:
			break;
		}
	}

	private void goTop() {
		if (mList != null) {
			mList.setSelection(0);
		}
	}

	@Override
	public void onRefresh() {
		boolean fromTop = mPullToRefreshListView.hasPullFromTop();
		if (App.DEBUG) {
			Log.d(TAG, "onRefresh() top=" + fromTop);
		}

		doSearch(fromTop);
	}

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position,
			long id) {
		final Status s = (Status) parent.getItemAtPosition(position);
		if (s != null) {
			Utils.goStatusPage(mContext, s);
		}
	}

}
