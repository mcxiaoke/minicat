package com.mcxiaoke.fanfouapp.app;

import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.ListView;
import com.mcxiaoke.fanfouapp.AppContext;
import com.mcxiaoke.fanfouapp.R;
import com.mcxiaoke.fanfouapp.adapter.SearchAdapter;
import com.mcxiaoke.fanfouapp.api.Api;
import com.mcxiaoke.fanfouapp.api.ApiException;
import com.mcxiaoke.fanfouapp.controller.EmptyViewController;
import com.mcxiaoke.fanfouapp.controller.UIController;
import com.mcxiaoke.fanfouapp.dao.model.Search;

import java.util.ArrayList;
import java.util.List;

/**
 * @author mcxiaoke
 * @version 2.0 2011.10.21
 */
public class UISearch extends UIBaseSupport implements OnItemClickListener {
    private static final String TAG = UISearch.class.getSimpleName();

    private ListView mListView;
    private View vEmpty;
    private EmptyViewController emptyController;
    private BaseAdapter mAdapter;
    private ArrayList<Search> mHotwords = new ArrayList<Search>(20);

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setLayout();
    }

    protected void setLayout() {
        setContentView(R.layout.search);
        setProgressBarIndeterminateVisibility(false);

        setTitle("热门话题");

        mListView = (ListView) findViewById(android.R.id.list);
        mListView.setOnItemClickListener(this);
        int dividerHeight = getResources().getDimensionPixelSize(R.dimen.list_divider_height);
        mListView.setDivider(getResources().getDrawable(R.drawable.divider));
        mListView.setDividerHeight(dividerHeight);
        mListView.setSelector(getResources().getDrawable(R.drawable.selector));
        mAdapter = new SearchAdapter(this, mHotwords);
        mListView.setAdapter(mAdapter);

        vEmpty = findViewById(android.R.id.empty);
        emptyController = new EmptyViewController(vEmpty);

        startRefresh();

    }

    @Override
    protected void startRefresh() {
        super.startRefresh();
        new TrendsTask().execute();
        showProgressIndicator();
        showProgress();
    }

    private void showHotwords() {
        showContent();
        mAdapter.notifyDataSetChanged();
    }

    private void showEmptyView(String text) {
        mListView.setVisibility(View.GONE);
        emptyController.showEmpty(text);
    }

    private void showProgress() {
        mListView.setVisibility(View.GONE);
        emptyController.showProgress();
        if (AppContext.DEBUG) {
            Log.d(TAG, "showProgress");
        }
    }

    private void showContent() {
        emptyController.hideProgress();
        mListView.setVisibility(View.VISIBLE);
        if (AppContext.DEBUG) {
            Log.d(TAG, "showContent");
        }
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
            hideProgressIndicator();
            switch (result) {
                case 0:
                case 1:
                    showHotwords();
                    break;
                case -1:
                    showEmptyView("暂时无法载入热词");
                    break;
                default:
                    break;
            }
        }

        @Override
        protected Integer doInBackground(Void... params) {
            Api api = AppContext.getApi();
            try {
                List<Search> savedSearches = api.getSavedSearches();
                if (savedSearches != null && savedSearches.size() > 0) {
                    mHotwords.addAll(savedSearches);
                }

                List<Search> trends = api.getTrends();

                if (trends != null && trends.size() > 0) {
                    mHotwords.addAll(trends);
                }

                if (mHotwords.size() > 0) {
                    return 1;
                } else {
                    return 0;
                }

            } catch (Exception e) {
                return -1;
            }
        }

    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position,
                            long id) {
        Search s = (Search) parent.getAdapter().getItem(position);
        if (s != null) {
            UIController.showSearchResults(mContext, s.query);
        }
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

}
