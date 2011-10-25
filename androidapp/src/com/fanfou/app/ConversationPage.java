package com.fanfou.app;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.TextUtils.TruncateAt;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.fanfou.app.adapter.ConversationAdapter;
import com.fanfou.app.api.Api;
import com.fanfou.app.api.ApiException;
import com.fanfou.app.api.Status;
import com.fanfou.app.config.Commons;
import com.fanfou.app.ui.ActionBar;
import com.fanfou.app.ui.widget.EndlessListView;
import com.fanfou.app.ui.widget.EndlessListView.OnRefreshListener;
import com.fanfou.app.util.StringHelper;

/**
 * @author mcxiaoke
 * @version 1.0 2011.10.25
 * 
 */
public class ConversationPage extends BaseActivity implements OnRefreshListener {

	protected ActionBar mActionBar;
	protected EndlessListView mListView;
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
		mThread.add(mStatus);
		mStatusAdapter = new ConversationAdapter(this, mThread);
	}

	private void setLayout() {
		setContentView(R.layout.list);
		setActionBar();

		mEmptyView = (ViewGroup) findViewById(R.id.empty);
		mEmptyView.setVisibility(View.GONE);

		mListView = (EndlessListView) findViewById(R.id.list);
		mListView.setOnRefreshListener(this);
		mListView.setAdapter(mStatusAdapter);
		mListView.removeHeader();
	}

	protected boolean parseIntent() {
		Intent intent = getIntent();
		mStatus = (Status) intent.getSerializableExtra(Commons.EXTRA_STATUS);
		return mStatus != null;
	}

	/**
	 * 初始化和设置ActionBar
	 */
	private void setActionBar() {
		mActionBar = (ActionBar) findViewById(R.id.actionbar);
		mActionBar.setTitle("对话");
		mActionBar.setLeftAction(new ActionBar.BackAction(this));
	}

	private void doFetchThreads() {
		new FetchTask().execute();
		mListView.setLoading();
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
	public void onRefresh(ListView view) {
	}

	@Override
	public void onLoadMore(ListView view) {
	}

	@Override
	public void onItemClick(ListView view, View row, int position) {
		// final Status s = (Status) view.getItemAtPosition(position);
		// if (s != null) {
		// Utils.goStatusPage(mContext, s);
		// }
	}

	private class FetchTask extends AsyncTask<Void, com.fanfou.app.api.Status, Boolean> {

		@Override
		protected Boolean doInBackground(Void... params) {
			Api api=App.me.api;
			boolean flag=true;
			String inReplyToStatusId=mStatus.inReplyToStatusId;
			try {
				while(flag){
					if(!StringHelper.isEmpty(inReplyToStatusId)){
						com.fanfou.app.api.Status result=api.statusShow(inReplyToStatusId);
						if(result!=null&&!result.isNull()){
							publishProgress(result);
							if(StringHelper.isEmpty(result.inReplyToStatusId)){
								flag=false;
							}else{
								inReplyToStatusId=result.inReplyToStatusId;
							}
						}
					}
					
				}
			} catch (ApiException e) {
				if(App.DEBUG){
					e.printStackTrace();
				}
				return false;
			}

			return true;
		}

		@Override
		protected void onProgressUpdate(com.fanfou.app.api.Status... values) {
			com.fanfou.app.api.Status result=values[0];
			mThread.add(result);
//			Collections.sort(mThread);
			mStatusAdapter.notifyDataSetChanged();
		}

		@Override
		protected void onPostExecute(Boolean result) {
			mListView.onNoLoadMore();
			mListView.setFooterDividersEnabled(false);
		}

	}

}
