package com.mcxiaoke.fanfouapp.app;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshBase.Mode;
import com.handmark.pulltorefresh.library.PullToRefreshBase.OnRefreshListener;
import com.handmark.pulltorefresh.library.PullToRefreshListView;
import com.mcxiaoke.fanfouapp.R;
import com.mcxiaoke.fanfouapp.adapter.StatusThreadAdapter;
import com.mcxiaoke.fanfouapp.api.Api;
import com.mcxiaoke.fanfouapp.controller.UIController;
import com.mcxiaoke.fanfouapp.dao.model.StatusModel;

import java.util.List;

/**
 * @author mcxiaoke
 * @version 4.2 2012.03.30
 *          <p/>
 *          Statuses Conversation List Page
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
        mListView.setScrollBarStyle(View.SCROLLBARS_OUTSIDE_OVERLAY);
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
        showProgressIndicator();
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


    @Override
    protected int getMenuResourceId() {
        return super.getMenuResourceId();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        return super.onPrepareOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        return super.onOptionsItemSelected(item);
    }

    private class FetchTask extends AsyncTask<Void, Void, List<StatusModel>> {

        @Override
        protected List<StatusModel> doInBackground(Void... params) {
            Api api = AppContext.getApi();
            try {
                List<StatusModel> ss = api.getContextTimeline(id);
                log("fetch task result id=" + id + " result=" + (ss == null ? "null" : ss.size()));
                return ss;
            } catch (Exception e) {
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
            hideProgressIndicator();
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
