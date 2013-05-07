package org.mcxiaoke.fancooker;

import java.util.List;

import org.mcxiaoke.fancooker.adapter.SearchResultsAdapter;
import org.mcxiaoke.fancooker.api.Api;
import org.mcxiaoke.fancooker.api.ApiException;
import org.mcxiaoke.fancooker.api.Paging;
import org.mcxiaoke.fancooker.controller.PopupController;
import org.mcxiaoke.fancooker.dao.model.StatusModel;
import org.mcxiaoke.fancooker.service.FanFouService;
import org.mcxiaoke.fancooker.util.NetworkHelper;
import org.mcxiaoke.fancooker.util.Utils;

import android.app.SearchManager;
import android.content.Intent;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Parcelable;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.ListView;

import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshBase.Mode;
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
 * @version 2.2 2012.03.02
 * @version 2.3 2012.03.30
 * 
 */
public class UISearchResults extends UIBaseSupport implements
		OnRefreshListener<ListView>, OnItemClickListener,
		OnItemLongClickListener {
	private static final String TAG = UISearchResults.class.getSimpleName();
	private PullToRefreshListView mPullToRefreshListView;
	private ListView mList;

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
		readHighlightColor();
		mStatusAdapter = new SearchResultsAdapter(this, highlightColor);
		api = AppContext.getApi();
		setLayout();
	}

	@Override
	protected void onNewIntent(Intent intent) {
		setIntent(intent);
		newSearch();
	}

	private void readHighlightColor() {
		TypedArray a = getTheme().obtainStyledAttributes(R.styleable.AppTheme);
		highlightColor = a.getColor(R.styleable.AppTheme_searchTextColor,
				Color.BLACK);
		a.recycle();
		a = null;
	}

	protected void setLayout() {

		setContentView(R.layout.list_pull);
		mPullToRefreshListView = (PullToRefreshListView) findViewById(R.id.pull_list);
		mPullToRefreshListView.setOnRefreshListener(this);
		mList = mPullToRefreshListView.getRefreshableView();
		mList.setOnItemClickListener(this);
		mList.setOnItemLongClickListener(this);
		mList.setAdapter(mStatusAdapter);

		newSearch();
	}

	protected void newSearch() {
		parseIntent();
		maxId = null;
		mStatusAdapter.clear();
		doSearch(true);
		mPullToRefreshListView.setRefreshing();

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
			mPullToRefreshListView.setRefreshing();
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

	private class SearchTask extends AsyncTask<Void, Void, List<StatusModel>> {

		@Override
		protected void onPreExecute() {
			if (maxId == null) {
				mStatusAdapter.clear();
			}
		}

		@Override
		protected void onPostExecute(List<StatusModel> result) {
			mPullToRefreshListView.onRefreshComplete();
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
	public boolean onItemLongClick(AdapterView<?> parent, View view,
			int position, long id) {
		final StatusModel s = (StatusModel) parent.getItemAtPosition(position);
		showPopup(view, s);
		return true;
	}

	private void showPopup(final View view, final StatusModel s) {
		if (s == null) {
			return;
		}
		PopupController.showPopup(view, s, mStatusAdapter);
	}

	@Override
	public void onClick(View v) {
	}

	private void goTop() {
		if (mList != null) {
			mList.setSelection(0);
		}
	}

	@Override
	public void onRefresh(PullToRefreshBase<ListView> refreshView) {
		boolean fromTop = Mode.PULL_FROM_START.equals(refreshView
				.getCurrentMode());
		if (AppContext.DEBUG) {
			Log.d(TAG, "onRefresh() top=" + fromTop);
		}

		doSearch(fromTop);
	}

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position,
			long id) {
		final StatusModel s = (StatusModel) parent.getItemAtPosition(position);
		if (s != null) {
			Utils.goStatusPage(mContext, s);
		}
	}

}
