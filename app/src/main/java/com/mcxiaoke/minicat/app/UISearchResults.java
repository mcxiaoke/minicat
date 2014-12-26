package com.mcxiaoke.minicat.app;

import android.app.SearchManager;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Parcelable;
import android.text.TextUtils;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import butterknife.ButterKnife;
import butterknife.InjectView;
import com.mcxiaoke.commons.view.endless.EndlessListView;
import com.mcxiaoke.minicat.AppContext;
import com.mcxiaoke.minicat.R;
import com.mcxiaoke.minicat.adapter.SearchResultsArrayAdapter;
import com.mcxiaoke.minicat.api.Api;
import com.mcxiaoke.minicat.api.ApiException;
import com.mcxiaoke.minicat.api.Paging;
import com.mcxiaoke.minicat.controller.UIController;
import com.mcxiaoke.minicat.dao.model.StatusModel;
import com.mcxiaoke.minicat.service.SyncService;
import com.mcxiaoke.minicat.ui.UIHelper;
import com.mcxiaoke.minicat.util.NetworkHelper;
import com.mcxiaoke.minicat.util.StringHelper;

import java.util.List;

/**
 * @author mcxiaoke
 * @version 2.3 2012.03.30
 */
public class UISearchResults extends UIBaseSupport implements EndlessListView.OnFooterRefreshListener, OnItemClickListener {
    private static final String TAG = UISearchResults.class.getSimpleName();
    private static final String LIST_STATE = "listState";
    @InjectView(R.id.list)
    EndlessListView mListView;
    private SearchResultsArrayAdapter mStatusAdapter;
    private String keyword;
    private String maxId;
    private int highlightColor;
    private Api api;
    private Parcelable mState = null;

    private void log(String message) {
        Log.d(TAG, message);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        highlightColor = getResources().getColor(R.color.search_text_highlight);
        mStatusAdapter = new SearchResultsArrayAdapter(this, highlightColor);
        api = AppContext.getApi();
        setLayout();
    }

    @Override
    protected int getMenuResourceId() {
        return R.menu.menu_search;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.menu_search) {
            onMenuSearchClick();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (mState != null && mListView != null) {
            mListView.onRestoreInstanceState(mState);
            mState = null;
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    protected void setLayout() {
        setContentView(R.layout.list_pull);
        setProgressBarIndeterminateVisibility(false);
        ButterKnife.inject(this);
        mListView = (EndlessListView) findViewById(R.id.list);
        mListView.setLongClickable(false);
        mListView.setOnItemClickListener(this);
        mListView.setAdapter(mStatusAdapter);
        mListView.setOnFooterRefreshListener(this);
        UIHelper.setListView(mListView);
        mListView.setRefreshMode(EndlessListView.RefreshMode.CLICK);
        mListView.showFooterEmpty();
        newSearch();
    }

    private void showFooterText() {
        if (mListView != null) {
            mListView.showFooterText(R.string.endless_footer_load_more);
        }
    }

    @Override
    public void onFooterRefresh(EndlessListView endlessListView) {
        doSearch(false);
    }

    @Override
    public void onFooterIdle(EndlessListView endlessListView) {

    }

    protected void newSearch() {
        parseIntent();
        maxId = null;
        mStatusAdapter.clear();


        if (StringHelper.isEmpty(keyword)) {
            onSearchRequested();

        } else {
            doSearch(true);
            showProgressIndicator();
        }

    }

    protected void parseIntent() {
        Intent intent = getIntent();
        if (Intent.ACTION_SEARCH.equals(intent.getAction())) {
            keyword = intent.getStringExtra(SearchManager.QUERY);
            if (AppContext.DEBUG) {
                log("parseIntent() keyword=" + keyword);
            }
        } else if (Intent.ACTION_VIEW.equals(intent.getAction())) {
            Uri data = intent.getData();
            if (data != null) {
                keyword = data.getLastPathSegment();
                log("parseIntent() keyword=" + keyword);
            }
        }

        if (!StringHelper.isEmpty(keyword)) {
            setTitle("搜索  \"" + keyword + "\"");
        } else {
            setTitle("搜索");
        }

    }

    private void doSearch(boolean reset) {
        if (keyword != null) {
            if (AppContext.DEBUG) {
                log("doSearch() keyword=" + keyword);
            }
            if (reset) {
                mListView.showFooterEmpty();
                maxId = null;
            }
            new SearchTask().execute();
        }

    }

    protected void onRefreshComplete(List<StatusModel> ss) {
        if (ss != null && ss.size() > 0) {
            if (AppContext.DEBUG) {
                Log.d(TAG, "onRefreshComplete() size=" + ss.size());
            }
            mStatusAdapter.updateDataAndUI(ss, keyword);
        }
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        mState = savedInstanceState.getParcelable(LIST_STATE);
    }

    @Override
    protected void onNewIntent(Intent intent) {
        setIntent(intent);
        newSearch();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        if (mListView != null) {
            mState = mListView.onSaveInstanceState();
            outState.putParcelable(LIST_STATE, mState);
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

    private class SearchTask extends AsyncTask<Void, Void, List<StatusModel>> {

        @Override
        protected List<StatusModel> doInBackground(Void... params) {
            if (TextUtils.isEmpty(keyword)) {
                return null;
            }
            List<StatusModel> result = null;

            Paging p = new Paging();
            p.maxId = maxId;

            if (NetworkHelper.isWifi(mContext)) {
                p.count = SyncService.MAX_TIMELINE_COUNT;
            } else {
                p.count = SyncService.DEFAULT_TIMELINE_COUNT;
            }

            try {
                result = api.search(keyword, p);
            } catch (ApiException e) {
                if (AppContext.DEBUG)
                    e.printStackTrace();
            }
            return result;
        }

        @Override
        protected void onPreExecute() {
            if (maxId == null) {
                mStatusAdapter.clear();
            }
        }

        @Override
        protected void onPostExecute(List<StatusModel> result) {
            showFooterText();
            hideProgressIndicator();
            if (result != null && result.size() > 0) {

                int size = result.size();
                maxId = result.get(size - 1).getId();

                log("result size=" + size);
                log("maxId=" + maxId + " status=" + result.get(size - 1));

                for (StatusModel s : result) {
                    Log.d(TAG, s.toString());
                }
                onRefreshComplete(result);
            }
        }

    }

}
