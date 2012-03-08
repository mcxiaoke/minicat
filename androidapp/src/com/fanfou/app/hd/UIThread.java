package com.fanfou.app.hd;

import java.util.ArrayList;
import java.util.List;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.ListView;

import com.fanfou.app.hd.adapter.StatusThreadAdapter;
import com.fanfou.app.hd.api.Api;
import com.fanfou.app.hd.api.ApiException;
import com.fanfou.app.hd.controller.PopupController;
import com.fanfou.app.hd.dao.model.StatusModel;
import com.fanfou.app.hd.util.StringHelper;
import com.fanfou.app.hd.util.Utils;
import com.handmark.pulltorefresh.library.PullToRefreshBase.OnRefreshListener;
import com.handmark.pulltorefresh.library.PullToRefreshListView;

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
 * 
 * Statuses Conversation List Page
 * 
 */
public class UIThread extends UIBaseSupport implements
		OnRefreshListener, OnItemClickListener, OnItemLongClickListener {

	private PullToRefreshListView mPullToRefreshListView;
	private ListView mList;

	protected StatusThreadAdapter mStatusAdapter;

	private List<StatusModel> mThread;

	private StatusModel mStatus;

	private static final String tag = UIThread.class.getSimpleName();

	private void log(String message) {
		Log.d(tag, message);
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		log("onCreate");
		doFetchThreads();
	}

	@Override
	protected void initialize() {
		parseIntent();
		mThread = new ArrayList<StatusModel>();
		mStatusAdapter = new StatusThreadAdapter(this, mThread);
	}

	@Override
	protected void setLayout() {
		setContentView(R.layout.list_pull);

		mPullToRefreshListView = (PullToRefreshListView) findViewById(R.id.pull_list);
		mPullToRefreshListView.setOnRefreshListener(this);
		mList = mPullToRefreshListView.getRefreshableView();
		mList.setAdapter(mStatusAdapter);
		configListView(mList);
	}

	private void configListView(final ListView list) {
		list.setOnItemClickListener(this);
		list.setOnItemLongClickListener(this);
	}

	protected boolean parseIntent() {
		Intent intent = getIntent();
		mStatus = (StatusModel) intent.getParcelableExtra("data");
		return mStatus != null;
	}

	private void doFetchThreads() {
		new FetchTask().execute();
		mPullToRefreshListView.setRefreshing();
	}

	@Override
	protected void onResume() {
		super.onResume();
	}

	@Override
	protected void onPause() {
		super.onPause();
	}

	@Override
	public boolean onItemLongClick(AdapterView<?> parent, View view,
			int position, long id) {
		final StatusModel s = (StatusModel) parent.getItemAtPosition(position);
		showPopup(view, s);
		return true;
	}

	private void showPopup(final View view, final StatusModel s) {
		if (s != null) {
			PopupController.showPopup(view, s, mStatusAdapter);
		}
	}

	private class FetchTask extends
			AsyncTask<Void, StatusModel, List<StatusModel>> {

		@Override
		protected List<StatusModel> doInBackground(
				Void... params) {
			Api api = App.getApi();
			String id = mStatus.getId();
			try {
				if (!StringHelper.isEmpty(id)) {
					return api.getContextTimeline(id);
				}
			} catch (ApiException e) {
				if (App.DEBUG) {
					e.printStackTrace();
				}
			}
			return null;
		}

		@Override
		protected void onPostExecute(List<StatusModel> result) {
			if (result != null && result.size() > 0) {
				mThread.addAll(result);
			}
			mPullToRefreshListView.onRefreshComplete();
			mPullToRefreshListView.setPullToRefreshEnabled(false);
		}

	}

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position,
			long id) {
		final StatusModel s = (StatusModel) parent.getItemAtPosition(position);
		if (s != null) {
			Utils.goStatusPage(mContext, s);
		}
	}

	@Override
	public void onRefresh() {

	}

}
