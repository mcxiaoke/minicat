package com.mcxiaoke.fanfouapp.app;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.ScrollView;
import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshBase.Mode;
import com.handmark.pulltorefresh.library.PullToRefreshBase.OnRefreshListener;
import com.handmark.pulltorefresh.library.PullToRefreshListView;
import com.mcxiaoke.fanfouapp.adapter.StatusThreadAdapter;
import com.mcxiaoke.fanfouapp.api.Api;
import com.mcxiaoke.fanfouapp.api.ApiException;
import com.mcxiaoke.fanfouapp.controller.UIController;
import com.mcxiaoke.fanfouapp.dao.model.StatusModel;
import com.mcxiaoke.fanfouapp.util.StringHelper;
import com.mcxiaoke.fanfouapp.R;

import java.util.List;

/**
 * @author mcxiaoke
 * @version 1.0 2011.10.25
 * @version 1.1 2011.10.26
 * @version 1.2 2011.10.28
 * @version 1.3 2011.11.07
 * @version 2.0 2011.11.11
 * @version 3.0 2011.11.18
 * @version 3.5 2012.01.31
 * @version 3.6 2012.02.01
 * @version 3.7 2012.02.09
 * @version 3.8 2012.02.22
 * @version 3.9 2012.02.24
 * @version 4.0 2012.02.28
 * @version 4.1 2012.03.02
 * @version 4.2 2012.03.30
 * 
 *          Statuses Conversation List Page
 * 
 */
public class UIThread extends UIBaseSupport implements
		OnRefreshListener<ListView>, OnItemClickListener {

	private PullToRefreshListView mPullToRefreshView;
	private ListView mListView;

	protected StatusThreadAdapter mStatusAdapter;

	private String id;

	private static final String tag = UIThread.class.getSimpleName();

	private void log(String message) {
		Log.d(tag, message);
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		log("onCreate");
		parseIntent();
		mStatusAdapter = new StatusThreadAdapter(this, null);
		setLayout();
	}

	protected void setLayout() {
		setContentView(R.layout.list_pull);
		int padding = getResources().getDimensionPixelSize(R.dimen.card_margin);
		setTitle("对话");
		mPullToRefreshView = (PullToRefreshListView) findViewById(R.id.pull_list);
		mPullToRefreshView.setPullToRefreshOverScrollEnabled(false);
		mPullToRefreshView.setShowIndicator(false);
		mPullToRefreshView.setMode(Mode.PULL_FROM_START);
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
		mListView.setLongClickable(false);
		mListView.setOnItemClickListener(this);
		mListView.setAdapter(mStatusAdapter);

		if (!TextUtils.isEmpty(id)) {
			doFetchThreads();
		} else {
			finish();
		}
	}

	private void parseIntent() {
		Intent intent = getIntent();
		id = intent.getStringExtra("id");
	}

	private void doFetchThreads() {
		new FetchTask().execute();
		mPullToRefreshView.setRefreshing();
	}

	@Override
	protected void onResume() {
		super.onResume();
	}

	@Override
	protected void onPause() {
		super.onPause();
	}

	private class FetchTask extends AsyncTask<Void, Void, List<StatusModel>> {

		@Override
		protected List<StatusModel> doInBackground(Void... params) {
			Api api = AppContext.getApi();
			try {
				if (!StringHelper.isEmpty(id)) {
					return api.getContextTimeline(id);
				}
			} catch (ApiException e) {
				if (AppContext.DEBUG) {
					e.printStackTrace();
				}
			}
			return null;
		}

		@Override
		protected void onPostExecute(List<StatusModel> result) {
			if (result != null && result.size() > 0) {
				mStatusAdapter.addData(result);
			}
			mPullToRefreshView.onRefreshComplete();
			mPullToRefreshView.setMode(Mode.DISABLED);
			mStatusAdapter.notifyDataSetChanged();
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
	public void onRefresh(PullToRefreshBase<ListView> refreshView) {
	}

}
