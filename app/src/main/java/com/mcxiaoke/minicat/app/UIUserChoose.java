package com.mcxiaoke.minicat.app;

import android.app.LoaderManager.LoaderCallbacks;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.util.SparseBooleanArray;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewStub;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FilterQueryProvider;
import android.widget.ListView;
import com.mcxiaoke.minicat.AppContext;
import com.mcxiaoke.minicat.R;
import com.mcxiaoke.minicat.adapter.UserChooseCursorAdapter;
import com.mcxiaoke.minicat.api.Paging;
import com.mcxiaoke.minicat.controller.DataController;
import com.mcxiaoke.minicat.controller.EmptyViewController;
import com.mcxiaoke.minicat.dao.model.UserModel;
import com.mcxiaoke.minicat.service.Constants;
import com.mcxiaoke.minicat.service.SyncService;
import com.mcxiaoke.minicat.ui.widget.TextChangeListener;
import com.mcxiaoke.minicat.util.StringHelper;
import com.mcxiaoke.minicat.util.Utils;

import java.util.ArrayList;
import java.util.List;

/**
 * @author mcxiaoke
 * @version 3.2 2012.03.26
 */
public class UIUserChoose extends UIBaseSupport implements FilterQueryProvider,
        OnItemClickListener, LoaderCallbacks<Cursor> {
    private static final String TAG = UIUserChoose.class.getSimpleName();

    private static final int LOADER_ID = 1;
    private static final String tag = UIUserChoose.class.getSimpleName();
    private ListView mListView;
    private EditText mEditText;
    private ViewGroup vEmpty;
    private EmptyViewController emptyController;
    private ViewStub mViewStub;
    private View mButtonGroup;
    private Button okButton;
    private Button cancelButton;
    private UserChooseCursorAdapter mCursorAdapter;
    private List<String> mUserNames;
    private int page = 1;
    private boolean isInitialized = false;

    private void log(String message) {
        Log.d(tag, message);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mUserNames = new ArrayList<String>();
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
    protected void onMenuHomeClick() {
        onBackPressed();
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        switch (id) {
            case R.id.button_ok:
                selectUserNames();
                break;
            case R.id.button_cancel:
                finish();
                break;
            default:
                break;
        }
    }

    private void initCheckState() {
        if (mCursorAdapter.getCount() > 0) {
            showContent();
        } else {
            doRefresh();
            showProgress();
        }
    }

    private void showEmptyView(String text) {
        mListView.setVisibility(View.GONE);
        emptyController.showEmpty(text);
    }

    private void showProgress() {
        mListView.setVisibility(View.GONE);
        mEditText.setVisibility(View.GONE);
        emptyController.showProgress();
        if (AppContext.DEBUG) {
            Log.d(TAG, "showProgress");
        }
    }

    private void showContent() {
        isInitialized = true;
        emptyController.hideProgress();
        mListView.setVisibility(View.VISIBLE);
        mEditText.setVisibility(View.VISIBLE);
        if (AppContext.DEBUG) {
            Log.d(TAG, "showContent");
        }
    }

    protected void setLayout() {
        setProgressBarIndeterminateVisibility(false);
        getActionBar().setTitle("选择发送对象");
        setContentView(R.layout.user_choose);
        mViewStub = (ViewStub) findViewById(R.id.stub);
        mEditText = (EditText) findViewById(R.id.input);
        mEditText.addTextChangedListener(new MyTextWatcher());
        vEmpty = (ViewGroup) findViewById(android.R.id.empty);
        emptyController = new EmptyViewController(vEmpty);
        setListView();
    }

    private void setListView() {
        mListView = (ListView) findViewById(android.R.id.list);
        mListView.setOnItemClickListener(this);
        mListView.setItemsCanFocus(false);
        mListView.setSelector(getResources().getDrawable(R.drawable.selector));
        mListView.setChoiceMode(AbsListView.CHOICE_MODE_MULTIPLE);

        mCursorAdapter = new UserChooseCursorAdapter(mContext, null);
        mCursorAdapter.setFilterQueryProvider(this);
        mListView.setAdapter(mCursorAdapter);
        getLoaderManager().initLoader(LOADER_ID, null, this);
    }

    private void initViewStub() {

        mButtonGroup = mViewStub.inflate();
        mViewStub = null;

        okButton = (Button) findViewById(R.id.button_ok);
        okButton.setText(android.R.string.ok);
        okButton.setOnClickListener(this);

        cancelButton = (Button) findViewById(R.id.button_cancel);
        cancelButton.setText(android.R.string.cancel);
        cancelButton.setOnClickListener(this);
    }

    private void doRefresh() {
        page = 1;
        doRetrieve(false);
    }

    private void doGetMore() {
        page++;
        doRetrieve(true);
    }

    private void doRetrieve(boolean isGetMore) {
        Paging paging = new Paging();
        paging.page = page;
        SyncService.getUsers(mContext, AppContext.getAccount(),
                UserModel.TYPE_FRIENDS, paging, new ResultHandler());
    }

    private void updateUI() {
        if (AppContext.DEBUG) {
            log("updateUI()");
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    private void selectUserNames() {
        if (!mUserNames.isEmpty()) {
            StringBuilder sb = new StringBuilder();
            for (String screenName : mUserNames) {
                sb.append("@").append(screenName).append(" ");
            }
            if (AppContext.DEBUG) {
                log("User Names: " + sb.toString());
            }
            Intent intent = new Intent();
            intent.putExtra("text", sb.toString());
            setResult(RESULT_OK, intent);
        }
        finish();
    }

    private void resetChoices() {
        SparseBooleanArray sba = mListView.getCheckedItemPositions();
        for (int i = 0; i < sba.size(); i++) {
            mCursorAdapter.setItemChecked(sba.keyAt(i), false);
        }
        mListView.clearChoices();
    }

    @Override
    public Cursor runQuery(CharSequence constraint) {
        return DataController.getUserListCursor(this, UserModel.TYPE_FRIENDS,
                AppContext.getAccount());
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position,
                            long id) {

        SparseBooleanArray sba = mListView.getCheckedItemPositions();
        mUserNames.clear();
        for (int i = 0; i < sba.size(); i++) {
            int key = sba.keyAt(i);
            boolean value = sba.valueAt(i);
            mCursorAdapter.setItemChecked(key, value);
            log("onItemClick key= " + key + " value=" + value + " position="
                    + position);
            if (value) {
                final Cursor cursor = (Cursor) mCursorAdapter.getItem(key);
                final UserModel u = UserModel.from(cursor);
                mUserNames.add(u.getScreenName());
            }
        }

        if (AppContext.DEBUG) {
            log(StringHelper.toString(mUserNames));
        }

        if (mViewStub != null) {
            initViewStub();
        }

        if (mUserNames.isEmpty()) {
            mButtonGroup.setVisibility(View.GONE);
        } else {
            mButtonGroup.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public Loader<Cursor> onCreateLoader(int arg0, Bundle arg1) {
        return DataController.getAutoCompleteCursorLoader(mContext,
                AppContext.getAccount());
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor newCursor) {
        mCursorAdapter.swapCursor(newCursor);
        if (AppContext.DEBUG) {
            Log.d(TAG,
                    "onLoadFinished() adapter.size="
                            + mCursorAdapter.getCount());
        }
        initCheckState();
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        mCursorAdapter.swapCursor(null);
    }

    private class MyTextWatcher extends TextChangeListener {
        @Override
        public void onTextChanged(CharSequence s, int start, int before,
                                  int count) {
            resetChoices();
            mCursorAdapter.getFilter().filter(s.toString().trim());
        }
    }

    private class ResultHandler extends Handler {

        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case SyncService.RESULT_SUCCESS:
                    if (!isInitialized) {
                        showContent();
                    }
                    int count = msg.getData().getInt("count");
                    if (count > 0) {
                        updateUI();
                    }
                    break;
                case SyncService.RESULT_ERROR:
                    int code = msg.getData().getInt("error_code");
                    String errorMessage = msg.getData().getString("error_message");
                    Utils.notify(mContext, errorMessage);
                    if (!isInitialized) {
                        showEmptyView(errorMessage);
                    }
                    break;
                default:
                    break;
            }
        }

    }

}
