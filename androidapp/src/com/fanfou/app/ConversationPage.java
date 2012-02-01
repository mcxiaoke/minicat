package com.fanfou.app;

import java.util.ArrayList;
import java.util.List;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.ListView;

import com.fanfou.app.adapter.ConversationAdapter;
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
 * @version 1.0 2011.10.25
 * @version 1.1 2011.10.26
 * @version 1.2 2011.10.28
 * @version 1.3 2011.11.07
 * @version 2.0 2011.11.11
 * @version 3.0 2011.11.18
 * @version 3.5 2012.01.31
 * @version 3.6 2012.02.01
 * 
 */
public class ConversationPage extends BaseActivity implements
		OnRefreshListener,OnItemClickListener, OnItemLongClickListener {

	protected ActionBar mActionBar;
	private PullToRefreshListView mPullToRefreshListView;
	private ListView mList;
	protected ViewGroup mEmptyView;

	protected ConversationAdapter mStatusAdapter;

	private List<Status> mThread;

	private Status mStatus;

	private static final String tag = ConversationPage.class.getSimpleName();

	private void log(String message) {
		Log.d(tag, message);
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		log("onCreate");
		if (parseIntent()) {
			initialize();
			setLayout();
			doFetchThreads();
		} else {
			finish();
		}

	}

	protected void initialize() {
		mThread = new ArrayList<Status>();
		mStatusAdapter = new ConversationAdapter(this, mThread);
	}

	private void setLayout() {
		setContentView(R.layout.list_pull);

		setActionBar();

		mEmptyView = (ViewGroup) findViewById(R.id.empty);
		mEmptyView.setVisibility(View.GONE);

		mPullToRefreshListView = (PullToRefreshListView) findViewById(R.id.list);
		mPullToRefreshListView.setOnRefreshListener(this);
		mList=mPullToRefreshListView.getRefreshableView();
		mList.setAdapter(mStatusAdapter);
		configListView(mList);
	}
	
	private void configListView(final ListView list) {
		list.setHorizontalScrollBarEnabled(false);
		list.setVerticalScrollBarEnabled(false);
		list.setCacheColorHint(0);
		list.setSelector(getResources().getDrawable(R.drawable.list_selector));
		list.setDivider(getResources().getDrawable(R.drawable.separator));

		list.setOnItemClickListener(this);
		list.setOnItemClickListener(this);
	}

	protected boolean parseIntent() {
		Intent intent = getIntent();
		mStatus = (Status) intent.getParcelableExtra(Constants.EXTRA_DATA);
		return mStatus != null;
	}

	/**
	 * 初始化和设置ActionBar
	 */
	private void setActionBar() {
		mActionBar = (ActionBar) findViewById(R.id.actionbar);
		mActionBar.setTitle("对话");
		setActionBarSwipe(mActionBar);
	}

	private void doFetchThreads() {
		new FetchTask().execute();
		mPullToRefreshListView.setRefreshing();
	}

	@Override
	protected void onResume() {
		super.onResume();
		App.active = true;
	}

	@Override
	protected void onPause() {
		App.active = false;
		super.onPause();
	}

	@Override
	public boolean onItemLongClick(AdapterView<?> parent, View view,
			int position, long id) {
		final Status s = (Status) parent.getItemAtPosition(position);
		showPopup(view, s);
		return true;
	}

	private void showPopup(final View view, final Status s) {
		if (s != null) {
			UIManager.showPopup(mContext, view, s, mStatusAdapter);
		}
	}

	private class FetchTask extends
			AsyncTask<Void, com.fanfou.app.api.Status, List<Status>> {

		@Override
		protected List<com.fanfou.app.api.Status> doInBackground(Void... params) {
			Api api = FanFouApi.newInstance();
			String id = mStatus.id;
			try {
				if (!StringHelper.isEmpty(id)) {
					return api.contextTimeline(id, Constants.FORMAT,
							Constants.MODE);
				}
			} catch (ApiException e) {
				if (App.DEBUG) {
					e.printStackTrace();
				}
			}
			return null;
		}

		@Override
		protected void onPostExecute(List<com.fanfou.app.api.Status> result) {
			if (result != null && result.size() > 0) {
				mThread.addAll(result);
			}
			mPullToRefreshListView.onRefreshComplete();
		}

	}

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position,
			long id) {
		final Status s = (Status) parent.getItemAtPosition(position);
		if (s != null) {
			Utils.goStatusPage(mContext, s);
		}
	}

	@Override
	public void onRefresh() {
		// TODO Auto-generated method stub
		
	}

}
