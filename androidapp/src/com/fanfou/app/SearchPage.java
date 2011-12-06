package com.fanfou.app;

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

import com.fanfou.app.adapter.SearchAdapter;
import com.fanfou.app.api.Api;
import com.fanfou.app.api.ApiException;
import com.fanfou.app.api.Search;
import com.fanfou.app.ui.ActionBar;

/**
 * @author mcxiaoke
 * @version 1.0 2011.08.02
 * @version 2.0 2011.10.21
 * 
 */
public class SearchPage extends BaseActivity implements OnItemClickListener {
	private ActionBar mActionBar;

	private ListView mListView;
	private View mEmptyView;
	private BaseAdapter mAdapter;
	private ArrayList<Search> mHotwords = new ArrayList<Search>(20);

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		parseIntent();
		setLayout();
		// onSearchRequested();
		fetchHotwords();
	}

	private void setActionBar() {
		mActionBar = (ActionBar) findViewById(R.id.actionbar);
		mActionBar.setTitle("搜索");
		mActionBar.setRightAction(new ActionBar.SearchAction(this));
	}

	private void setLayout() {
		setContentView(R.layout.search);
		setActionBar();
		mEmptyView = findViewById(R.id.empty);
		TextView tv = (TextView) findViewById(R.id.empty_text);
		tv.setText("热词载入中...");

		mListView = (ListView) findViewById(R.id.list);
		mListView.setOnItemClickListener(this);
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
				List<Search> savedSearches = api.savedSearchesList();
				if (savedSearches != null && savedSearches.size() > 0) {
					mHotwords.addAll(savedSearches);
				}

				List<Search> trends = api.trends();

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
		Intent intent = new Intent(context, SearchResultsPage.class);
		intent.setAction(Intent.ACTION_SEARCH);
		intent.putExtra(SearchManager.QUERY, query);
		startActivity(intent);
	}

}
