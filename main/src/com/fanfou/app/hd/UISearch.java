package com.fanfou.app.hd;

import java.util.ArrayList;
import java.util.List;

import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.ListView;

import com.actionbarsherlock.view.MenuItem;
import com.fanfou.app.hd.adapter.SearchAdapter;
import com.fanfou.app.hd.api.Api;
import com.fanfou.app.hd.api.ApiException;
import com.fanfou.app.hd.controller.EmptyViewController;
import com.fanfou.app.hd.dao.model.Search;

/**
 * @author mcxiaoke
 * @version 1.0 2011.08.02
 * @version 2.0 2011.10.21
 * 
 */
public class UISearch extends UIBaseSupport implements OnItemClickListener {
	private static final String TAG = UISearch.class.getSimpleName();

	private ListView mListView;
	private View vEmpty;
	private EmptyViewController emptyController;
	private BaseAdapter mAdapter;
	private ArrayList<Search> mHotwords = new ArrayList<Search>(20);

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
	}

	@Override
	protected int getMenuResourceId() {
		return R.menu.topic_menu;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		if (item.getItemId() == R.id.menu_search) {
			onSearchRequested();
			return true;
		}
		return super.onOptionsItemSelected(item);
	}

	@Override
	protected void initialize() {
		parseIntent();
	}

	@Override
	protected void setLayout() {
		setContentView(R.layout.search);

		mListView = (ListView) findViewById(android.R.id.list);
		mListView.setOnItemClickListener(this);
		mAdapter = new SearchAdapter(this, mHotwords);
		mListView.setAdapter(mAdapter);

		vEmpty = findViewById(android.R.id.empty);
		emptyController = new EmptyViewController(vEmpty);

		fetchHotwords();
		showProgress();
	}

	private void parseIntent() {
	}

	private void fetchHotwords() {
		new TrendsTask().execute();
	}

	private void showHotwords() {
		showContent();
		mAdapter.notifyDataSetChanged();
	}

	private void showEmptyView(String text) {
		mListView.setVisibility(View.GONE);
		emptyController.showEmpty(text);
	}

	private void showProgress() {
		mListView.setVisibility(View.GONE);
		emptyController.showProgress();
		if (App.DEBUG) {
			Log.d(TAG, "showProgress");
		}
	}

	private void showContent() {
		emptyController.hideProgress();
		mListView.setVisibility(View.VISIBLE);
		if (App.DEBUG) {
			Log.d(TAG, "showContent");
		}
	}

	@Override
	protected void onResume() {
		super.onResume();
	}

	@Override
	protected void onPause() {
		super.onPause();
	}

	private class TrendsTask extends AsyncTask<Void, Void, Integer> {

		@Override
		protected void onPreExecute() {
		}

		@Override
		protected void onPostExecute(Integer result) {
			switch (result) {
			case 0:
			case 1:
				showHotwords();
				break;
			case -1:
				showEmptyView("暂时无法载入热词");
				break;
			default:
				break;
			}
		}

		@Override
		protected Integer doInBackground(Void... params) {
			Api api = App.getApi();
			try {
				List<Search> savedSearches = api.getSavedSearches();
				if (savedSearches != null && savedSearches.size() > 0) {
					mHotwords.addAll(savedSearches);
				}

				List<Search> trends = api.getTrends();

				if (trends != null && trends.size() > 0) {
					mHotwords.addAll(trends);
				}

				if (mHotwords.size() > 0) {
					return 1;
				} else {
					return 0;
				}

			} catch (ApiException e) {
				return -1;
			}
		}

	}

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position,
			long id) {
		Search s = (Search) parent.getAdapter().getItem(position);
		if (s != null) {
			goSearch(mContext, s.query);
		}
	}

	private void goSearch(Context context, String query) {
		Intent intent = new Intent(context, UISearchResults.class);
		intent.setAction(Intent.ACTION_SEARCH);
		intent.putExtra(SearchManager.QUERY, query);
		startActivity(intent);
	}

}
