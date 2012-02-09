package com.fanfou.app.hd;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.Parcelable;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.FilterQueryProvider;
import android.widget.ListView;
import android.widget.AdapterView.OnItemClickListener;

import com.fanfou.app.hd.R;
import com.fanfou.app.hd.adapter.UserCursorAdapter;
import com.fanfou.app.hd.api.User;
import com.fanfou.app.hd.db.Contents.BasicColumns;
import com.fanfou.app.hd.db.Contents.UserInfo;
import com.fanfou.app.hd.service.Constants;
import com.fanfou.app.hd.service.FanFouService;
import com.fanfou.app.hd.ui.widget.TextChangeListener;
import com.fanfou.app.hd.util.Utils;
import com.handmark.pulltorefresh.library.PullToRefreshListView;
import com.handmark.pulltorefresh.library.PullToRefreshBase.OnRefreshListener;

/**
 * @author mcxiaoke
 * @version 1.0 2011.11.09
 * @version 1.1 2011.11.21
 * @version 2.0 2012.01.31
 * 
 */

// select direct message target
public class UserSelectPage extends UIBaseSupport implements
		OnItemClickListener, OnRefreshListener, FilterQueryProvider {

	private static final String TAG = UserSelectPage.class.getSimpleName();

	private PullToRefreshListView mPullToRefreshListView;
	private ListView mList;

	protected EditText mEditText;

	protected Cursor mCursor;
	protected UserCursorAdapter mCursorAdapter;

	private boolean initialized = false;

	private int page = 1;

	private void log(String message) {
		Log.i(TAG, message);
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		if (App.DEBUG)
			log("onCreate");

	}

	@Override
	protected void initialize() {
		initCursor();
	}

	protected void initCursor() {
		String where = BasicColumns.TYPE + "=? AND " + BasicColumns.OWNER_ID
				+ "=?";
		String[] whereArgs = new String[] {
				String.valueOf(Constants.TYPE_USERS_FRIENDS), App.getUserId() };
		mCursor = managedQuery(UserInfo.CONTENT_URI, UserInfo.COLUMNS, where,
				whereArgs, null);
	}

	protected void initCheckState() {
		if (mCursor.getCount() == 0) {
			onRefresh();
			mPullToRefreshListView.setRefreshing();
		} else {
			initialized = true;
			mEditText.setVisibility(View.VISIBLE);
		}
	}

	@Override
	protected void setLayout() {
		setContentView(R.layout.list_users);

		mEditText = (EditText) findViewById(R.id.choose_input);
		mEditText.addTextChangedListener(new MyTextWatcher());

		mPullToRefreshListView = (PullToRefreshListView) findViewById(R.id.list);
		mPullToRefreshListView.setOnRefreshListener(this);

		mList = mPullToRefreshListView.getRefreshableView();
		mList.setOnItemClickListener(this);

		mCursorAdapter = new UserCursorAdapter(mContext, mCursor);
		mCursorAdapter.setFilterQueryProvider(this);

		mList.setAdapter(mCursorAdapter);

		initCheckState();
	}

	private class MyTextWatcher extends TextChangeListener {
		@Override
		public void onTextChanged(CharSequence s, int start, int before,
				int count) {
			mCursorAdapter.getFilter().filter(s.toString());
		}
	}

	protected void doRefresh() {
		page = 1;
		doRetrieve(false);
	}

	protected void doGetMore() {
		page++;
		doRetrieve(true);
	}

	protected void doRetrieve(boolean isGetMore) {
		FanFouService.doFetchFriends(this, new ResultHandler(isGetMore), page,
				App.getUserId());
	}

	protected void updateUI() {
		if (App.DEBUG)
			log("updateUI()");
		if (mCursor != null) {
			mCursor.requery();
		}
	}

	@Override
	protected void onPause() {
		super.onPause();
	}

	@Override
	protected void onStop() {
		super.onStop();
		App.getImageLoader().clearQueue();
	}

	private static final String LIST_STATE = "listState";
	private Parcelable mState = null;

	@Override
	protected void onResume() {
		super.onResume();
		if (mState != null && mList != null) {
			mList.onRestoreInstanceState(mState);
			mState = null;
		}
	}

	@Override
	protected void onRestoreInstanceState(Bundle savedInstanceState) {
		super.onRestoreInstanceState(savedInstanceState);
		mState = savedInstanceState.getParcelable(LIST_STATE);
	}

	@Override
	protected void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		if (mList != null) {
			mState = mList.onSaveInstanceState();
			outState.putParcelable(LIST_STATE, mState);
		}
	}

	protected class ResultHandler extends Handler {
		private boolean doGetMore;

		public ResultHandler(boolean doGetMore) {
			this.doGetMore = doGetMore;
		}

		@Override
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case Constants.RESULT_SUCCESS:
				int count = msg.getData().getInt(Constants.EXTRA_COUNT);
				updateUI();
				mPullToRefreshListView.onRefreshComplete();
				if (!initialized) {
					mEditText.setVisibility(View.VISIBLE);
				}
				break;
			case Constants.RESULT_ERROR:
				if (!initialized) {
					mEditText.setVisibility(View.VISIBLE);
				}
				String errorMessage = msg.getData().getString(
						Constants.EXTRA_ERROR);
				int errorCode = msg.getData().getInt(Constants.EXTRA_CODE);
				mPullToRefreshListView.onRefreshComplete();
				Utils.notify(mContext, errorMessage);
				Utils.checkAuthorization(mContext, errorCode);
				break;
			default:
				break;
			}
		}

	}

	@Override
	public void onRefresh() {
		boolean fromTop = mPullToRefreshListView.hasPullFromTop();
		if (App.DEBUG) {
			Log.d(TAG, "onRefresh() top=" + fromTop);
		}

		if (fromTop) {
			doRefresh();
		} else {
			doGetMore();
		}
	}

	private void onSelected(User user) {
		Intent intent = new Intent();
		intent.putExtra(Constants.EXTRA_ID, user.id);
		intent.putExtra(Constants.EXTRA_USER_NAME, user.screenName);
		setResult(RESULT_OK, intent);
		finish();
	}

	@Override
	public Cursor runQuery(CharSequence constraint) {
		String where = BasicColumns.TYPE + " = " + Constants.TYPE_USERS_FRIENDS
				+ " AND " + BasicColumns.OWNER_ID + " = '" + App.getUserId()
				+ "' AND (" + UserInfo.SCREEN_NAME + " like '%" + constraint
				+ "%' OR " + BasicColumns.ID + " like '%" + constraint + "%' )";
		;
		return managedQuery(UserInfo.CONTENT_URI, UserInfo.COLUMNS, where,
				null, null);
	}

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position,
			long id) {
		final Cursor c = (Cursor) parent.getItemAtPosition(position);
		final User u = User.parse(c);
		if (u != null) {
			if (App.DEBUG)
				log("userId=" + u.id + " username=" + u.screenName);
			onSelected(u);
		}
	}

}
