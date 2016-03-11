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
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.CursorAdapter;
import android.widget.ListView;
import butterknife.ButterKnife;
import butterknife.InjectView;
import com.mcxiaoke.commons.view.endless.EndlessListView;
import com.mcxiaoke.minicat.AppContext;
import com.mcxiaoke.minicat.R;
import com.mcxiaoke.minicat.adapter.UserCursorAdapter;
import com.mcxiaoke.minicat.api.Paging;
import com.mcxiaoke.minicat.controller.DataController;
import com.mcxiaoke.minicat.controller.UIController;
import com.mcxiaoke.minicat.dao.model.UserModel;
import com.mcxiaoke.minicat.service.SyncService;
import com.mcxiaoke.minicat.ui.UIHelper;
import com.mcxiaoke.minicat.util.Utils;

/**
 * @author mcxiaoke
 * @version 1.8 2012.03.19
 */
public abstract class UserListFragment extends AbstractListFragment
        implements EndlessListView.OnFooterRefreshListener,
        LoaderCallbacks<Cursor> {

    protected static final int LOADER_ID = 1;

    private static final String TAG = UserListFragment.class
            .getSimpleName();


    @InjectView(R.id.root)
    SwipeRefreshLayout mSwipeRefreshLayout;
    @InjectView(R.id.list)
    EndlessListView mListView;
    boolean mDataLoaded;
    volatile boolean busy;
    private Parcelable mParcelable;
    private UserCursorAdapter mAdapter;
    private boolean refreshOnStart;
    private int page;
    private String userId;

    private OnInitCompleteListener mListener;
/*
    public void setOnInitCompleteListener(OnInitCompleteListener listener) {
        this.mListener = listener;
    }*/

    public UserListFragment() {
        super();
        if (AppContext.DEBUG) {
            Log.d(TAG, "PullToRefreshListFragment() id=" + this);
        }
    }

    private void onInitComplete() {
        if (mListener != null) {
            mListener.onInitComplete(null);
        }
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        if (AppContext.DEBUG) {
            Log.d(TAG, "onAttach() isVisible=" + isVisible());
        }
    }

    @Override
    public void startRefresh() {
        if (AppContext.DEBUG) {
            Log.d(TAG, "startRefresh() busy=" + busy);
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
        final Cursor c = (Cursor) parent.getItemAtPosition(position);
        final UserModel u = UserModel.from(c);
        if (u != null) {
            if (AppContext.DEBUG) {
                Log.d(TAG,
                        "userId=" + u.getId() + " username="
                                + u.getScreenName());
            }
            UIController.showProfile(getActivity(), u);
        }
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
        mListView.setOnItemClickListener(this);
        mListView.setLongClickable(false);
        mListView.setOnFooterRefreshListener(this);
        UIHelper.setListView(mListView);
    }

    protected void showRefreshIndicator(final boolean show) {
        if (mSwipeRefreshLayout != null) {
            mSwipeRefreshLayout.setRefreshing(show);
        }
    }

    @Override
    public void onFooterRefresh(EndlessListView endlessListView) {
        doFetch(true);
    }

    @Override
    public void onFooterIdle(EndlessListView endlessListView) {

    }

    public void refreshData() {
        doFetch(false);
        showRefreshIndicator(true);
    }

    private void showFooterText() {
        if (mListView != null) {
            mListView.showFooterText(R.string.endless_footer_load_more);
        }
    }

    @Override
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);
        if (AppContext.DEBUG) {
            Log.d(TAG, "onHiddenChanged() hidden=" + hidden + " isVisible="
                    + isVisible());
        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (AppContext.DEBUG) {
            Log.d(TAG, "onCreate() isVisible=" + isVisible());
        }

        Bundle args = getArguments();
        if (args != null) {
            refreshOnStart = args.getBoolean("refresh");
            if (AppContext.DEBUG) {
                Log.d(TAG, "refreshOnStart=" + refreshOnStart);
            }
            parseArguments(args);
        }

        mDataLoaded = false;

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        if (AppContext.DEBUG) {
            Log.d(TAG, "onCreateView() isVisible=" + isVisible());
        }
        final View view = inflater.inflate(R.layout.fm_userlist_ptr, container, false);
        ButterKnife.inject(this, view);
        setUp();
        return view;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        if (AppContext.DEBUG) {
            Log.d(TAG, "onViewCreated() isVisible=" + isVisible());
        }
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        if (AppContext.DEBUG) {
            Log.d(TAG, "onActivityCreated() isVisible=" + isVisible());
        }

        parseArguments(getArguments());

        if (savedInstanceState != null) {
            mParcelable = savedInstanceState.getParcelable("state");
        }

        mAdapter = (UserCursorAdapter) onCreateAdapter();
        mListView.setAdapter(mAdapter);
        mListView.setOnScrollListener(mAdapter);
        mListView.setRefreshMode(EndlessListView.RefreshMode.CLICK);
        mListView.showFooterEmpty();
        getLoaderManager().initLoader(LOADER_ID, null, this);
    }

    @Override
    public void onStart() {
        super.onStart();
        if (AppContext.DEBUG) {
            Log.d(TAG, "onStart() isVisible=" + isVisible());
        }
    }

    @Override
    public void onResume() {
        super.onResume();

        if (mParcelable != null && mListView != null) {
            mListView.onRestoreInstanceState(mParcelable);
            mParcelable = null;
        }
        if (AppContext.DEBUG) {
            Log.d(TAG, "onResume() isVisible=" + isVisible());
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        if (mListView != null) {
            mParcelable = mListView.onSaveInstanceState();
            outState.putParcelable("state", mParcelable);
        }
    }

/*    @Override
    public Cursor runQuery(CharSequence constraint) {
        return DataController.getUserListSearchCursor(getActivity(), getType(), userId, constraint);

    }*/

    @Override
    public void onPause() {
        super.onPause();
        if (AppContext.DEBUG) {
            Log.d(TAG, "onPause() isVisible=" + isVisible());
        }
    }

    @Override
    public void onStop() {
        super.onStop();
        if (AppContext.DEBUG) {
            Log.d(TAG, "onStop() isVisible=" + isVisible());
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (AppContext.DEBUG) {
            Log.d(TAG, "onDestroyView()");
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mDataLoaded = false;
        if (AppContext.DEBUG) {
            Log.d(TAG, "onDestroy()");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        if (AppContext.DEBUG) {
            Log.d(TAG, "onDetach() isVisible=" + isVisible());
        }
    }

    protected void parseArguments(Bundle args) {
        if (args != null) {
            userId = args.getString("id");
        }
        if (TextUtils.isEmpty(userId)) {
            userId = AppContext.getAccount();
        }
    }

    protected CursorAdapter onCreateAdapter() {
        return new UserCursorAdapter(getActivity(), null);
    }

    protected void doFetch(boolean doGetMore) {
        Paging p = new Paging();

        if (doGetMore) {
            page++;
        } else {
            page = 1;
        }
        p.page = page;

        final ResultHandler handler = new ResultHandler(this);
        SyncService.getUsers(getActivity(), userId, getType(), p, handler);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        return DataController.getUserListCursorLoader(getActivity(), getType(),
                userId);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor newCursor) {
        getAdapter().swapCursor(newCursor);
        if (AppContext.DEBUG) {
            Log.d(TAG, "onLoadFinished() adapter=" + mAdapter.getCount());
        }
        showFooterText();
        checkRefresh();
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        if (AppContext.DEBUG) {
            Log.d(TAG, "onLoaderReset()");
        }
        getAdapter().swapCursor(null);
    }

    protected abstract int getType();

    public void filter(String text) {
        getAdapter().getFilter().filter(text);
    }

    protected void doRefresh() {
        if (AppContext.DEBUG) {
            Log.d(TAG, "doRefresh()");
        }
        doFetch(false);
    }

    protected void doGetMore() {
        if (AppContext.DEBUG) {
            Log.d(TAG, "doGetMore()");
        }
        doFetch(true);
    }

    @Override
    public CursorAdapter getAdapter() {
        return mAdapter;
    }

    @Override
    public ListView getListView() {
        return mListView;
    }

    private void onSuccess(Bundle data) {
        int count = data.getInt("count");
        if (AppContext.DEBUG) {
            Log.d(TAG, "onSuccess(data) count=" + count);
        }
    }

    private void onError(Bundle data) {
        if (AppContext.DEBUG) {
            Log.d(TAG, "onSuccess()");
        }
        showFooterText();
        String errorMessage = data.getString("error_message");
        int errorCode = data.getInt("error_code");
        if (!isAdded()) {
            return;
        }
        Utils.notify(getActivity(), errorMessage);
        Utils.checkAuthorization(getActivity(), errorCode);
    }

    private void onRefreshComplete() {
        showFooterText();
        showRefreshIndicator(false);
    }

    protected void checkRefresh() {
        if (AppContext.DEBUG) {
            Log.d(TAG, "checkRefresh() mDataLoaded=" + mDataLoaded
                    + " refreshOnStart=" + refreshOnStart + " adapter.count=" + mAdapter.getCount());
        }
        if (!mDataLoaded && (refreshOnStart || mAdapter.isEmpty())) {
            startRefresh();
        }
    }

    /**
     * FetchService返回数据处理 根据resultData里面的type信息分别处理
     */
    protected static class ResultHandler extends Handler {
        private UserListFragment mFragment;

        public ResultHandler(UserListFragment fragment) {
            this.mFragment = fragment;
        }

        @Override
        public void handleMessage(Message msg) {
            Bundle data = msg.getData();
            if (AppContext.DEBUG) {
                Log.d(TAG, "handleMessage() data=" + data + " msg=" + msg);
            }
            mFragment.mDataLoaded = true;
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
