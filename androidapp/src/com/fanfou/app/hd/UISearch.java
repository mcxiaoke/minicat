package com.fanfou.app.hd;

import java.util.ArrayList;
import java.util.List;

import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.fanfou.app.hd.adapter.SearchAdapter;
import com.fanfou.app.hd.api.Api;
import com.fanfou.app.hd.api.ApiException;
import com.fanfou.app.hd.dao.model.Search;

/**
 * @author mcxiaoke
 * @version 1.0 2011.08.02
 * @version 2.0 2011.10.21
 * 
 */
public class UISearch extends UIBaseSupport implements OnItemClickListener {

	private ListView mListView;
	private View mEmptyView;
	private BaseAdapter mAdapter;
	private ArrayList<Search> mHotwords = new ArrayList<Search>(20);

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
	}

	@Override
	protected void initialize() {
		parseIntent();
	}

	@Override
	protected void setLayout() {
		setContentView(R.layout.search);
		mEmptyView = findViewById(R.id.empty);
		TextView tv = (TextView) findViewById(R.id.empty_text);
		tv.setText("热词载入中...");

		mListView = (ListView) findViewById(android.R.id.list);
		mListView.setOnItemClickListener(this);

		// onSearchRequested();
		fetchHotwords();
	}

	private void parseIntent() {
	}

	private void fetchHotwords() {
		new TrendsTask().execute();
	}

	private void showHotwords() {
		mEmptyView.setVisibility(View.GONE);
		mListView.setVisibility(View.VISIBLE);

		mAdapter = new SearchAdapter(this, mHotwords);
		mListView.setAdapter(mAdapter);
		mAdapter.notifyDataSetChanged();
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
			case 1:
				showHotwords();
				break;
			case 0:
				break;
			case -1:
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
				e.printStackTrace();
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
