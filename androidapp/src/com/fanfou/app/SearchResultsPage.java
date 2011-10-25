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
import android.widget.ListView;
import android.widget.AdapterView.OnItemLongClickListener;

import com.fanfou.app.adapter.SearchResultsAdapter;
import com.fanfou.app.api.ApiException;
import com.fanfou.app.api.Status;
import com.fanfou.app.config.Commons;
import com.fanfou.app.ui.ActionBar;
import com.fanfou.app.ui.ActionBar.Action;
import com.fanfou.app.ui.UIManager;
import com.fanfou.app.ui.widget.EndlessListView;
import com.fanfou.app.ui.widget.EndlessListView.OnRefreshListener;
import com.fanfou.app.util.StringHelper;
import com.fanfou.app.util.Utils;

/**
 * @author mcxiaoke
 * @version 1.0 2011.08.05
 * @version 1.1 2011.10.12
 * @version 1.5 2011.10.24
 * 
 */
public class SearchResultsPage extends BaseActivity implements
		OnRefreshListener, OnItemLongClickListener {

	protected ActionBar mActionBar;
	protected EndlessListView mListView;
	protected ViewGroup mEmptyView;

	protected SearchResultsAdapter mStatusAdapter;

	private List<Status> mStatuses;

	protected String keyword;
	protected String maxId;

	private boolean showListView = false;

	private static final String tag = SearchResultsPage.class.getSimpleName();

	private void log(String message) {
		Log.e(tag, message);
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		log("onCreate");
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
	}

	private void setLayout() {
		setContentView(R.layout.list);
		setActionBar();
		mEmptyView = (ViewGroup) findViewById(R.id.empty);
		mListView = (EndlessListView) findViewById(R.id.list);
		mListView.setOnItemLongClickListener(this);
		mListView.setOnRefreshListener(this);
	}

	protected void search() {
		parseIntent();
		mStatuses.clear();
		doSearch();
		showProgress();

	}

	protected void parseIntent() {
		Intent intent = getIntent();
		if (Intent.ACTION_SEARCH.equals(intent.getAction())) {
			keyword = intent.getStringExtra(SearchManager.QUERY);
			log("parseIntent() keyword=" + keyword);
		} else if (Intent.ACTION_VIEW.equals(intent.getAction())) {
			Uri data = intent.getData();
			if (data != null) {
				keyword = data.getLastPathSegment();
				log("parseIntent() keyword=" + keyword);
			}
		}
	}

	private void showProgress() {
		log("showProgress()");
		showListView = false;
		mListView.setVisibility(View.GONE);
		mEmptyView.setVisibility(View.VISIBLE);
	}

	private void showContent() {
		log("showContent()");
		showListView = true;

		mStatusAdapter = new SearchResultsAdapter(this, mStatuses);
		mListView.setAdapter(mStatusAdapter);

		mEmptyView.setVisibility(View.GONE);
		mListView.removeHeader();
		mListView.setVisibility(View.VISIBLE);
	}

	/**
	 * 初始化和设置ActionBar
	 */
	private void setActionBar() {
		mActionBar = (ActionBar) findViewById(R.id.actionbar);
		mActionBar.setTitle("搜索结果");
		mActionBar.setTitleClickListener(this);
		mActionBar.setLeftAction(new ActionBar.BackAction(this));
		mActionBar.setRightAction(new ActionBar.SearchAction(this));
	}

	private void doSearch() {
		if (keyword != null) {
			log("doSearch() keyword=" + keyword);
			mActionBar.setTitle(keyword);
			new SearchTask().execute();
		}

	}

	protected void updateUI(boolean noMore) {
		log("updateUI()");
		mStatusAdapter.updateDataAndUI(mStatuses, keyword);
		if (noMore) {
			mListView.onNoLoadMore();
		} else {
			mListView.onLoadMoreComplete();
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
	public void onRefresh(ListView view) {
	}

	@Override
	public void onLoadMore(ListView view) {
		doSearch();
	}

	@Override
	public void onItemClick(ListView view, View row, int position) {
		final Status s = (Status) view.getItemAtPosition(position);
		if (s != null) {
			Utils.goStatusPage(mContext, s);
		}
	}

	private class SearchTask extends AsyncTask<Void, Void, List<Status>> {

		@Override
		protected void onPreExecute() {
		}

		@Override
		protected void onPostExecute(List<com.fanfou.app.api.Status> result) {
			if (!showListView) {
				showContent();
			}
			if (result != null && result.size() > 0) {

				int size = result.size();
				log("result size=" + size);
				maxId = result.get(size - 1).id;
				log("maxId=" + maxId);

				mStatuses.addAll(result);
				updateUI(size < 20);
			}
		}

		@Override
		protected List<com.fanfou.app.api.Status> doInBackground(Void... params) {
			if (StringHelper.isEmpty(keyword)) {
				return null;
			}
			List<com.fanfou.app.api.Status> result = null;
			try {
				result = App.me.api.search(keyword, maxId, true);
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
		if (mListView != null) {
			mListView.setSelection(0);
		}
	}

}
