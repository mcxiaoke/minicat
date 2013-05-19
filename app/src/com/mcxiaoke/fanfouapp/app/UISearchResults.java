package com.mcxiaoke.fanfouapp.app;

import android.app.SearchManager;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Parcelable;
import android.text.TextUtils;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.ScrollView;
import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshBase.Mode;
import com.handmark.pulltorefresh.library.PullToRefreshBase.OnRefreshListener2;
import com.handmark.pulltorefresh.library.PullToRefreshListView;
import com.mcxiaoke.fanfouapp.adapter.SearchResultsAdapter;
import com.mcxiaoke.fanfouapp.api.Api;
import com.mcxiaoke.fanfouapp.api.ApiException;
import com.mcxiaoke.fanfouapp.api.Paging;
import com.mcxiaoke.fanfouapp.controller.UIController;
import com.mcxiaoke.fanfouapp.dao.model.StatusModel;
import com.mcxiaoke.fanfouapp.service.FanFouService;
import com.mcxiaoke.fanfouapp.util.NetworkHelper;
import com.mcxiaoke.fanfouapp.R;

import java.util.List;

/**
 * @author mcxiaoke
 * @version 1.0 2011.08.05
 * @version 1.1 2011.10.12
 * @version 1.5 2011.10.24
 * @version 1.6 2011.11.21
 * @version 1.7 2011.11.25
 * @version 2.0 2012.01.31
 * @version 2.1 2012.02.01
 * @version 2.2 2012.03.02
 * @version 2.3 2012.03.30
 * 
 */
public class UISearchResults extends UIBaseSupport implements
		OnRefreshListener2<ListView>, OnItemClickListener {
	private static final String TAG = UISearchResults.class.getSimpleName();
	private PullToRefreshListView mPullToRefreshView;
	private ListView mListView;

	private SearchResultsAdapter mStatusAdapter;

	private String keyword;
	private String maxId;
	private int highlightColor;

	private Api api;

	private void log(String message) {
		Log.d(TAG, message);
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		highlightColor = getResources().getColor(R.color.holo_red_light);
		mStatusAdapter = new SearchResultsAdapter(this, highlightColor);
		api = AppContext.getApi();
		setLayout();
	}

	@Override
	protected void onNewIntent(Intent intent) {
		setIntent(intent);
		newSearch();
	}

	protected void setLayout() {
		setContentView(R.layout.list_pull);

		int padding = getResources().getDimensionPixelSize(R.dimen.card_margin);

		mPullToRefreshView = (PullToRefreshListView) findViewById(R.id.pull_list);
		mPullToRefreshView.setPullToRefreshOverScrollEnabled(false);
		mPullToRefreshView.setShowIndicator(false);
		mPullToRefreshView.setMode(Mode.BOTH);
		mPullToRefreshView.setOnRefreshListener(this);
		mListView = mPullToRefreshView.getRefreshableView();
		mListView.setPadding(padding, padding, padding, padding);
		mListView.setDivider(getResources()
				.getDrawable(R.drawable.list_divider));
		mListView.setDividerHeight(padding);
		mListView.setHeaderDividersEnabled(true);
		mListView.setFooterDividersEnabled(true);
		mListView.setCacheColorHint(0);
		mListView.setDrawSelectorOnTop(true);
		mListView.setScrollBarStyle(ScrollView.SCROLLBARS_OUTSIDE_OVERLAY);
		mListView.setBackgroundResource(R.drawable.general_background);
        mListView.setSelector(getResources().getDrawable(R.drawable.list_selector));
		mListView.setLongClickable(false);
		mListView.setOnItemClickListener(this);
		mListView.setAdapter(mStatusAdapter);
		newSearch();
	}

	protected void newSearch() {
		parseIntent();
		maxId = null;
		mStatusAdapter.clear();
		doSearch(true);
		mPullToRefreshView.setRefreshing();

	}

	protected void parseIntent() {
		Intent intent = getIntent();
		if (Intent.ACTION_SEARCH.equals(intent.getAction())) {
			keyword = intent.getStringExtra(SearchManager.QUERY);
			if (AppContext.DEBUG) {
				log("parseIntent() keyword=" + keyword);
			}
		} else if (Intent.ACTION_VIEW.equals(intent.getAction())) {
			Uri data = intent.getData();
			if (data != null) {
				keyword = data.getLastPathSegment();
				log("parseIntent() keyword=" + keyword);
			}
		}

		setTitle("搜索  \"" + keyword + "\"");
	}

	private void doSearch(boolean reset) {
		if (keyword != null) {
			if (AppContext.DEBUG) {
				log("doSearch() keyword=" + keyword);
			}
			if (reset) {
				maxId = null;
			}
			new SearchTask().execute();
			mPullToRefreshView.setRefreshing();
		}

	}

	protected void onRefreshComplete(List<StatusModel> ss) {
		if (ss != null && ss.size() > 0) {
			if (AppContext.DEBUG) {
				Log.d(TAG, "onRefreshComplete() size=" + ss.size());
			}
			mStatusAdapter.updateDataAndUI(ss, keyword);
		}
	}

	private static final String LIST_STATE = "listState";
	private Parcelable mState = null;

	@Override
	protected void onResume() {
		super.onResume();
		if (mState != null && mListView != null) {
			mListView.onRestoreInstanceState(mState);
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
		if (mListView != null) {
			mState = mListView.onSaveInstanceState();
			outState.putParcelable(LIST_STATE, mState);
		}
	}

	@Override
	protected void onPause() {
		super.onPause();
	}

	@Override
	protected int getMenuResourceId() {
		return R.menu.menu_search;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		if (item.getItemId() == R.id.menu_search) {
			onMenuSearchClick();
			return true;
		}
		return super.onOptionsItemSelected(item);
	}

	private class SearchTask extends AsyncTask<Void, Void, List<StatusModel>> {

		@Override
		protected void onPreExecute() {
			if (maxId == null) {
				mStatusAdapter.clear();
			}
		}

		@Override
		protected void onPostExecute(List<StatusModel> result) {
			mPullToRefreshView.onRefreshComplete();
			if (result != null && result.size() > 0) {

				int size = result.size();
				maxId = result.get(size - 1).getId();

				log("result size=" + size);
				log("maxId=" + maxId + " status=" + result.get(size - 1));

				for (StatusModel s : result) {
					Log.d(TAG, s.toString());
				}
				onRefreshComplete(result);
			}
		}

		@Override
		protected List<StatusModel> doInBackground(Void... params) {
			if (TextUtils.isEmpty(keyword)) {
				return null;
			}
			List<StatusModel> result = null;

			Paging p = new Paging();
			p.maxId = maxId;

			if (NetworkHelper.isWifi(mContext)) {
				p.count = FanFouService.MAX_TIMELINE_COUNT;
			} else {
				p.count = FanFouService.DEFAULT_TIMELINE_COUNT;
			}

			try {
				result = api.search(keyword, p);
			} catch (ApiException e) {
				if (AppContext.DEBUG)
					e.printStackTrace();
			}
			return result;
		}

	}

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position,
			long id) {
		final StatusModel s = (StatusModel) parent.getItemAtPosition(position);
		if (s != null) {
			UIController.goStatusPage(mContext, s);
		}
	}

	@Override
	public void onPullDownToRefresh(PullToRefreshBase<ListView> refreshView) {
		doSearch(true);
	}

	@Override
	public void onPullUpToRefresh(PullToRefreshBase<ListView> refreshView) {
		doSearch(false);
	}

}
