package com.mcxiaoke.minicat.app;

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
import com.mcxiaoke.commons.view.endless.EndlessListView;
import com.mcxiaoke.minicat.AppContext;
import com.mcxiaoke.minicat.R;
import com.mcxiaoke.minicat.adapter.StatusThreadAdapter;
import com.mcxiaoke.minicat.api.Api;
import com.mcxiaoke.minicat.controller.UIController;
import com.mcxiaoke.minicat.dao.model.StatusModel;
import com.mcxiaoke.minicat.ui.UIHelper;

import java.util.List;

/**
 * @author mcxiaoke
 * @version 4.2 2012.03.30
 *          <p/>
 *          Statuses Conversation List Page
 */
public class UIThread extends UIBaseSupport implements EndlessListView.OnFooterRefreshListener, OnItemClickListener {

    private static final String tag = UIThread.class.getSimpleName();
    protected StatusThreadAdapter mStatusAdapter;
    private EndlessListView mListView;
    private String id;

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

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
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

    @Override
    protected int getMenuResourceId() {
        return super.getMenuResourceId();
    }

    @Override
    protected void onMenuHomeClick() {
        onBackPressed();
    }

    @Override
    protected void startRefresh() {
        super.startRefresh();
        new FetchTask().execute();
        showProgressIndicator();
    }

    protected void setLayout() {
        setContentView(R.layout.list);
        setProgressBarIndeterminateVisibility(false);

        setTitle("对话");

        mListView = (EndlessListView) findViewById(R.id.list);
        mListView.setLongClickable(false);
        mListView.setOnItemClickListener(this);
        UIHelper.setListView(mListView);
        mListView.setAdapter(mStatusAdapter);
        mListView.setOnFooterRefreshListener(this);
        mListView.setRefreshMode(EndlessListView.RefreshMode.CLICK);
        mListView.showFooterEmpty();

        if (!TextUtils.isEmpty(id)) {
            startRefresh();
        } else {
            finish();
        }
    }

    private void showFooterText() {
        if (mListView != null) {
            mListView.showFooterText(R.string.endless_footer_load_more);
        }
    }

    @Override
    public void onFooterRefresh(EndlessListView endlessListView) {
        startRefresh();
    }

    @Override
    public void onFooterIdle(EndlessListView endlessListView) {

    }

    private void parseIntent() {
        Intent intent = getIntent();
        id = intent.getStringExtra("id");
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position,
                            long id) {
        final StatusModel s = (StatusModel) parent.getItemAtPosition(position);
        if (s != null) {
            UIController.goStatusPage(mContext, s);
        }
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
                mStatusAdapter.setData(result);
            } else {
                mListView.setRefreshMode(EndlessListView.RefreshMode.NONE);
            }
            mListView.showFooterEmpty();
            hideProgressIndicator();
        }

    }

}
