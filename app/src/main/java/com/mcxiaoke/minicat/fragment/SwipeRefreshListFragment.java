package com.mcxiaoke.minicat.fragment;

import android.app.Activity;
import android.app.LoaderManager.LoaderCallbacks;
import android.content.Loader;
import android.database.Cursor;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.Parcelable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v4.widget.SwipeRefreshLayout.OnRefreshListener;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.CursorAdapter;
import android.widget.ListView;
import android.widget.TextView;
import butterknife.ButterKnife;
import butterknife.InjectView;
import com.mcxiaoke.commons.view.endless.EndlessListView;
import com.mcxiaoke.minicat.AppContext;
import com.mcxiaoke.minicat.Cache;
import com.mcxiaoke.minicat.R;
import com.mcxiaoke.minicat.adapter.BaseCursorAdapter;
import com.mcxiaoke.minicat.controller.PopupController;
import com.mcxiaoke.minicat.dao.model.StatusModel;
import com.mcxiaoke.minicat.service.SyncService;
import com.mcxiaoke.minicat.ui.UIHelper;
import com.mcxiaoke.minicat.util.NetworkHelper;
import com.mcxiaoke.minicat.util.Utils;
import com.nostra13.universalimageloader.core.ImageLoader;

/**
 * @author mcxiaoke
 * @version 1.8 2012.03.19
 */
public abstract class SwipeRefreshListFragment extends AbstractListFragment
        implements EndlessListView.OnFooterRefreshListener, OnItemLongClickListener,
        LoaderCallbacks<Cursor> {

    protected static final int LOADER_ID = 1;

    private static final String TAG = SwipeRefreshListFragment.class
            .getSimpleName();

    @InjectView(R.id.root)
    SwipeRefreshLayout mSwipeRefreshLayout;
    @InjectView(R.id.list)
    EndlessListView mListView;
    boolean mDataLoaded;
    volatile boolean busy;
    private Parcelable mParcelable;
    private BaseCursorAdapter mAdapter;
    private Handler mHandler;

    public SwipeRefreshListFragment() {
        super();
//        if (AppContext.DEBUG) {
//            Log.v(TAG, "PullToRefreshListFragment() id=" + this);
//        }
        mHandler = new Handler();
    }

    protected static void showPopup(Activity context, final View view,
                                    final Cursor c) {
        if (c != null) {
            final StatusModel s = StatusModel.from(c);
            if (s != null) {
                PopupController.showPopup(view, s, c);
            }
        }
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
//        if (AppContext.DEBUG) {
//            Log.v(TAG, "onAttach() isVisible=" + isVisible());
//        }
    }

    @Override
    public void startRefresh() {
        if (AppContext.DEBUG) {
            Log.v(TAG, "startRefresh() busy=" + busy + " " + this);
        }
        if (!AppContext.isVerified()) {
            return;
        }
        if (NetworkHelper.isNotConnected(getActivity())) {
            return;
        }
        if (!busy) {
            busy = true;
            doRefresh();
            showRefreshIndicator(true);
        }
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position,
                            long id) {
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        return null;
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor newCursor) {
        getAdapter().swapCursor(newCursor);
        if (AppContext.DEBUG) {
            Log.v(TAG, "onLoadFinished() adapter=" + mAdapter.getCount());
        }
        showFooterText();
        checkRefresh();
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        if (AppContext.DEBUG) {
            Log.v(TAG, "onLoaderReset()");
        }
        getAdapter().swapCursor(null);
    }

    private void setUp() {
        mSwipeRefreshLayout.setColorSchemeResources(
                R.color.color1,
                R.color.color2,
                R.color.color3, R.color.color4);
        mSwipeRefreshLayout.setOnRefreshListener(new OnRefreshListener() {
            @Override
            public void onRefresh() {
                refreshData();
            }
        });
        mListView.setVerticalScrollBarEnabled(true);
        mListView.setHorizontalScrollBarEnabled(false);
        mListView.setFastScrollEnabled(false);
        mListView.setOnItemClickListener(this);
        mListView.setOverScrollMode(ListView.OVER_SCROLL_NEVER);
        mListView.setLongClickable(false);
        mListView.setOnFooterRefreshListener(this);
        mListView.setOnScrollListener(new OnScrollListener() {
            @Override
            public void onScrollStateChanged(final AbsListView view, final int scrollState) {
                if (scrollState == OnScrollListener.SCROLL_STATE_IDLE) {
                    ImageLoader.getInstance().resume();
                } else {
                    ImageLoader.getInstance().pause();
                }
            }

            @Override
            public void onScroll(final AbsListView view, final int firstVisibleItem, final int visibleItemCount, final int totalItemCount) {

            }
        });
        UIHelper.setListView(mListView);
    }

    @Override
    public void onFooterRefresh(EndlessListView endlessListView) {
        doFetch(true);
    }

    @Override
    public void onFooterIdle(EndlessListView endlessListView) {

    }

    private void refreshData() {
        doFetch(false);
        showRefreshIndicator(true);
    }

    protected void showRefreshIndicator(final boolean show) {
        if (mSwipeRefreshLayout != null) {
            mSwipeRefreshLayout.setRefreshing(show);
        }
    }

    private void showFooterText() {
        if (mListView != null) {
            if (getType() == StatusModel.TYPE_PUBLIC) {
                mListView.showFooterEmpty();
            } else {
                if (getAdapter().isEmpty()) {
                    mListView.showFooterEmpty();
                } else {
                    mListView.showFooterText(R.string.endless_footer_load_more);
                }
            }
        }
    }

    @Override
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);
//        if (AppContext.DEBUG) {
//            Log.v(TAG, "onHiddenChanged() hidden=" + hidden + " isVisible="
//                    + isVisible());
//        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        if (AppContext.DEBUG) {
//            Log.v(TAG, "onCreate() isVisible=" + isVisible());
//        }

        Bundle args = getArguments();
        if (args != null) {
            parseArguments(args);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
//        if (AppContext.DEBUG) {
//            Log.v(TAG, "onCreateView() isVisible=" + isVisible());
//        }
        final View view = inflater.inflate(R.layout.fm_pull_list, container, false);
        ButterKnife.inject(this, view);
        return view;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
//        if (AppContext.DEBUG) {
//            Log.v(TAG, "onViewCreated() isVisible=" + isVisible());
//        }
        setUp();
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
//        if (AppContext.DEBUG) {
//            Log.v(TAG, "onActivityCreated() isVisible=" + isVisible());
//        }

        parseArguments(getArguments());

        if (savedInstanceState != null) {
            mParcelable = savedInstanceState.getParcelable("state");
        }

        mAdapter = (BaseCursorAdapter) onCreateAdapter();
        mListView.setAdapter(mAdapter);
        mListView.setOnScrollListener(mAdapter);

        mListView.setRefreshMode(EndlessListView.RefreshMode.CLICK);
        mListView.showFooterEmpty();

        getLoaderManager().initLoader(LOADER_ID, null, this);
    }

    @Override
    public void onStart() {
        super.onStart();
//        if (AppContext.DEBUG) {
//            Log.v(TAG, "onStart() isVisible=" + isVisible());
//        }
    }

    @Override
    public void onResume() {
        super.onResume();

        if (mParcelable != null && mListView != null) {
            mListView.onRestoreInstanceState(mParcelable);
            mParcelable = null;
        }
//        if (AppContext.DEBUG) {
//            Log.v(TAG, "onResume() isVisible=" + isVisible());
//        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        if (mListView != null) {
            mParcelable = mListView.onSaveInstanceState();
            outState.putParcelable("state", mParcelable);
        }
    }

    @Override
    public void onPause() {
        super.onPause();
//        if (AppContext.DEBUG) {
//            Log.v(TAG, "onPause() isVisible=" + isVisible());
//        }
    }

    @Override
    public void onStop() {
        super.onStop();
//        if (AppContext.DEBUG) {
//            Log.v(TAG, "onStop() isVisible=" + isVisible());
//        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
//        if (AppContext.DEBUG) {
//            Log.v(TAG, "onDestroyView()");
//        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
//        if (AppContext.DEBUG) {
//            Log.v(TAG, "onDestroy()");
//        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
//        if (AppContext.DEBUG) {
//            Log.v(TAG, "onDetach() isVisible=" + isVisible());
//        }
    }

    protected abstract void parseArguments(Bundle args);

    protected abstract CursorAdapter onCreateAdapter();

    protected abstract void doFetch(boolean doGetMore);

    protected abstract int getType();

    protected boolean shouldDelayRefresh() {
        return false;
    }

    @Override
    public boolean onItemLongClick(AdapterView<?> parent, View view,
                                   int position, long id) {
        return true;
    }

    protected void doRefresh() {
        if (AppContext.DEBUG) {
            Log.v(TAG, "doRefresh()");
        }
        doFetch(false);
    }

    protected void doGetMore() {
        if (AppContext.DEBUG) {
            Log.v(TAG, "doGetMore()");
        }
        doFetch(true);
    }

    public Cursor getCursor() {
        if (mAdapter != null) {
            return mAdapter.getCursor();
        }
        return null;
    }

    @Override
    public CursorAdapter getAdapter() {
        return mAdapter;
    }

    @Override
    public EndlessListView getListView() {
        return mListView;
    }

    public void setSelection(int position) {
        mListView.setSelection(position);
    }

    public void setEmptyView(View emptyView) {
        mListView.setEmptyView(emptyView);
    }

    public void setEmptyText(CharSequence text) {
        final TextView tv = new TextView(getActivity());
        tv.setText(text);
        mListView.setEmptyView(tv);
    }

    public void goTop() {
        mListView.setSelection(0);
    }

    private void onSuccess(Bundle data) {
        int count = data.getInt("count");
        if (AppContext.DEBUG) {
            Log.v(TAG, "onSuccess(data) count=" + count);
        }
        if (getType() == StatusModel.TYPE_HOME) {
            Cache.sLastHomeRefresh = System.currentTimeMillis();
        }
    }

    private void onError(Bundle data) {
        if (AppContext.DEBUG) {
            Log.v(TAG, "onSuccess()");
        }
        showFooterText();
        String errorMessage = data.getString("error_message");
        int errorCode = data.getInt("error_code");
        if (!isAdded()) {
            return;
        }
        Utils.notify(getActivity(), errorMessage);
    }

    private void onRefreshComplete() {
        showFooterText();
        showRefreshIndicator(false);
    }

    protected void checkRefresh() {
        if (AppContext.DEBUG) {
            Log.v(TAG, "checkRefresh()  adapter.count=" + mAdapter.getCount() + " " + this);
        }
        if (mAdapter.isEmpty()) {
            startRefresh();
            return;
        }
        if (getType() == StatusModel.TYPE_HOME &&
                System.currentTimeMillis() - Cache.sLastHomeRefresh > 30 * 60 * 1000L) {
            startRefresh();
        }
    }

    /**
     * FetchService返回数据处理 根据resultData里面的type信息分别处理
     */
    protected static class ResultHandler extends Handler {
        private SwipeRefreshListFragment mFragment;

        public ResultHandler(SwipeRefreshListFragment fragment) {
            this.mFragment = fragment;
        }

        @Override
        public void handleMessage(Message msg) {
            Bundle data = msg.getData();
            if (AppContext.DEBUG) {
                Log.v(TAG, "handleMessage() data=" + data + " msg=" + msg);
            }
            mFragment.busy = false;
            switch (msg.what) {
                case SyncService.RESULT_SUCCESS:
                    mFragment.onSuccess(data);
                    break;
                case SyncService.RESULT_ERROR:
                    mFragment.onError(data);
                    break;
                default:
                    break;
            }
            mFragment.getBaseSupport().hideProgressIndicator();
            mFragment.onRefreshComplete();
        }

    }

}
