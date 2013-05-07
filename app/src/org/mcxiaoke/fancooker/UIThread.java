package org.mcxiaoke.fancooker;

import java.util.List;

import org.mcxiaoke.fancooker.adapter.StatusThreadAdapter;
import org.mcxiaoke.fancooker.api.Api;
import org.mcxiaoke.fancooker.api.ApiException;
import org.mcxiaoke.fancooker.controller.PopupController;
import org.mcxiaoke.fancooker.dao.model.StatusModel;
import org.mcxiaoke.fancooker.util.StringHelper;
import org.mcxiaoke.fancooker.util.Utils;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.ListView;

import com.handmark.pulltorefresh.library.PullToRefreshBase;
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
 * @version 4.2 2012.03.30
 * 
 *          Statuses Conversation List Page
 * 
 */
public class UIThread extends UIBaseSupport implements
		OnRefreshListener<ListView>, OnItemClickListener,
		OnItemLongClickListener {

	private PullToRefreshListView mPullToRefreshListView;
	private ListView mList;

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

		mPullToRefreshListView = (PullToRefreshListView) findViewById(R.id.pull_list);
		mPullToRefreshListView.setOnRefreshListener(this);
		mList = mPullToRefreshListView.getRefreshableView();
		mList.setAdapter(mStatusAdapter);
		mList.setOnItemClickListener(this);
		mList.setOnItemLongClickListener(this);

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
			mPullToRefreshListView.onRefreshComplete();
			mPullToRefreshListView.setPullToRefreshEnabled(false);
			mStatusAdapter.notifyDataSetChanged();
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
	public void onRefresh(PullToRefreshBase<ListView> refreshView) {
	}

}
