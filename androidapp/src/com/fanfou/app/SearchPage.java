package com.fanfou.app;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import com.fanfou.app.adapter.SearchAdapter;
import com.fanfou.app.api.Api;
import com.fanfou.app.api.ApiException;
import com.fanfou.app.api.Search;
import com.fanfou.app.config.Commons;
import com.fanfou.app.ui.ActionBar;
import com.fanfou.app.ui.ActionBar.Action;

import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.AdapterView.OnItemClickListener;

/**
 * @author mcxiaoke
 * @version 1.0 20110802
 * 
 */
public class SearchPage extends BaseActivity implements OnItemClickListener,Action{
	private ActionBar mActionBar;
	
	private ListView mListView;
	private BaseAdapter mAdapter;
	private ArrayList<Search> mSearches = new ArrayList<Search>(20);

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		parseIntent();
		inflateLayout();
		setActionBar();
		onSearchRequested();
		fetchTrends();
	}

	private void setActionBar() {
		mActionBar = (ActionBar) findViewById(R.id.actionbar);
		mActionBar.setTitle("搜索");
		mActionBar.setLeftAction(new ActionBar.BackAction(mContext));
		mActionBar.setRightAction(this);
		mActionBar.setLeftAction(new ActionBar.BackAction(mContext));
	}

	private void inflateLayout() {
		setContentView(R.layout.search);
		mListView = (ListView) findViewById(R.id.list);
		mListView.setOnItemClickListener(this);
	}

	private void parseIntent() {
	}
	
	private void fetchTrends(){
		new TrendsTask().execute();
	}

	private void updateUI() {
		mAdapter=new SearchAdapter(this, mSearches);
		mListView.setAdapter(mAdapter);
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
				updateUI();
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
			Api api = App.me.api;
			try {
				List<Search> savedSearches = api.savedSearches();
				if (savedSearches != null && savedSearches.size() > 0) {
					mSearches.addAll(savedSearches);
				}

				List<Search> trends = api.trends();

				if (trends != null && trends.size() > 0) {
					mSearches.addAll(trends);
				}

				if (mSearches.size() > 0) {
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
	public int getDrawable() {
		return R.drawable.i_write;
	}

	@Override
	public void performAction(View view) {
		Intent intent = new Intent(this, WritePage.class);
		intent.putExtra(Commons.EXTRA_TYPE, WritePage.TYPE_NORMAL);
		startActivity(intent);
	}

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position,
			long id) {
		Search s=(Search) parent.getAdapter().getItem(position);
		if(s!=null){
			goSearch(mContext, s.query);
		}
	}
	
	private void goSearch(Context context,String query){
		Intent intent=new Intent(context,SearchResultsPage.class);
		intent.setAction(Intent.ACTION_SEARCH);
		intent.putExtra(SearchManager.QUERY,query);
		startActivity(intent);
	}

}
