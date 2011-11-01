package com.fanfou.app;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.Parcelable;
import android.os.ResultReceiver;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.ListView;
import com.fanfou.app.adapter.StatusCursorAdapter;
import com.fanfou.app.api.Status;
import com.fanfou.app.api.User;
import com.fanfou.app.config.Commons;
import com.fanfou.app.ui.ActionBar;
import com.fanfou.app.ui.ActionBar.Action;
import com.fanfou.app.ui.UIManager;
import com.fanfou.app.ui.widget.EndlessListView;
import com.fanfou.app.ui.widget.EndlessListView.OnRefreshListener;
import com.fanfou.app.util.StringHelper;
import com.fanfou.app.util.Utils;

/**
 * @author mcxiaoke
 * @version 1.0 2011.07.10
 * @version 2.0 2011.10.19
 * @version 3.0 2011.10.21
 * @version 3.1 2011.10.24
 * @version 3.2 2011.10.29
 * 
 */
public abstract class BaseTimelineActivity extends BaseActivity implements
		OnRefreshListener, Action, OnItemLongClickListener {

	protected ActionBar mActionBar;
	protected EndlessListView mListView;
	protected ViewGroup mEmptyView;

	protected Cursor mCursor;
	protected StatusCursorAdapter mCursorAdapter;

	protected Handler mHandler;

	protected String userId;
	protected String userName;
	protected User user;

	protected boolean isInitialized = false;

	private static final String tag = BaseTimelineActivity.class
			.getSimpleName();

	private void log(String message) {
		Log.e(tag, message);
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		if (App.DEBUG)
			log("onCreate");
		if (parseIntent()) {
			initialize();
			setLayout();
			initCheckState();
		} else {
			finish();
		}
	}

	protected void initialize() {
		mHandler = new ResultHandler();
		mCursor = getCursor();
		mCursorAdapter = new StatusCursorAdapter(this, mCursor);

	}

	private void setLayout() {
		setContentView(R.layout.list);
		setActionBar();

		mEmptyView = (ViewGroup) findViewById(R.id.empty);
		mListView = (EndlessListView) findViewById(R.id.list);
		mListView.setOnRefreshListener(this);
		mListView.setOnItemLongClickListener(this);
		mListView.setAdapter(mCursorAdapter);
	}

	protected void initCheckState() {
		if (mCursor.getCount() > 0) {
			showContent();
		} else {
			doRefresh();
			showProgress();
		}
	}

	private void showProgress() {
		mListView.setVisibility(View.GONE);
		mEmptyView.setVisibility(View.VISIBLE);
	}

	private void showContent() {
		isInitialized = true;
		mEmptyView.setVisibility(View.GONE);
		mListView.setVisibility(View.VISIBLE);
	}

	/**
	 * 初始化和设置ActionBar
	 */
	private void setActionBar() {
		mActionBar = (ActionBar) findViewById(R.id.actionbar);
		mActionBar.setTitleClickListener(this);
		mActionBar.setRightAction(this);
		mActionBar.setLeftAction(new ActionBar.BackAction(mContext));

		if (user != null) {
			mActionBar.setTitle(user.screenName + "的" + getPageTitle());
		}
	}

	protected boolean parseIntent() {
		Intent intent = getIntent();
		user = (User) intent.getSerializableExtra(Commons.EXTRA_USER);
		if (user == null) {
			userId = intent.getStringExtra(Commons.EXTRA_ID);
		} else {
			userId = user.id;
			userName = user.screenName;
		}
		return !StringHelper.isEmpty(userId);
	}

	protected void doRefresh() {
		doRetrieve(false);
	}

	protected void doGetMore() {
		doRetrieve(true);
	}

	protected void doRetrieve(boolean isGetMore) {
		if (!App.me.isLogin) {
			Utils.notify(this, "未通过验证，请登录");
			return;
		}
		Bundle b = new Bundle();
		b.putString(Commons.EXTRA_ID, userId);
		b.putBoolean(Commons.EXTRA_FORMAT, true);
		MyResultHandler receiver = new MyResultHandler(mHandler, isGetMore);
		doRetrieveImpl(b, receiver);
	}

	protected abstract void doRetrieveImpl(Bundle bundle,
			MyResultHandler receiver);

	protected abstract Cursor getCursor();

	protected abstract String getPageTitle();

	protected void updateUI() {
		if (mCursor != null) {
			mCursor.requery();
		}
		// mCursorAdapter.getFilter().filter("今天");

	}

	private static final String LIST_STATE = "listState";
	private Parcelable mState = null;

	@Override
	protected void onResume() {
		super.onResume();
		if (mState != null && mListView != null) {
			mListView.onRestoreInstanceState(mState);
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
		if (mListView != null) {
			mState = mListView.onSaveInstanceState();
			outState.putParcelable(LIST_STATE, mState);
		}
	}

	@Override
	protected void onPause() {
		super.onPause();
	}

	protected class ResultHandler extends Handler {

		@Override
		public void handleMessage(Message msg) {
		}

	}

	protected class MyResultHandler extends ResultReceiver {
		public final boolean doGetMore;

		@Override
		protected void onReceiveResult(int resultCode, Bundle resultData) {
			switch (resultCode) {
			case Commons.RESULT_CODE_START:
				break;
			case Commons.RESULT_CODE_FINISH:
				if (!isInitialized) {
					showContent();
				}

				if (doGetMore) {
					int count = resultData.getInt(Commons.EXTRA_COUNT);
					if (count < 20) {
						mListView.onNoLoadMore();
					} else {
						mListView.onLoadMoreComplete();
					}
				} else {
					mListView.onRefreshComplete();
				}
				updateUI();
				break;
			case Commons.RESULT_CODE_ERROR:
				String msg = resultData.getString(Commons.EXTRA_ERROR_MESSAGE);
				Utils.notify(mContext, msg);
				if (!isInitialized) {
					showContent();
						mListView.onNoLoadMore();
						mListView.onNoRefresh();
				} else {
					if (doGetMore) {
						mListView.onLoadMoreComplete();
					} else {
						mListView.onRefreshComplete();
					}
				}
				break;
			default:
				break;
			}
		}

		public MyResultHandler(Handler handler, boolean doGetMore) {
			super(handler);
			this.doGetMore = doGetMore;
		}

	}

	@Override
	public void onRefresh(ListView view) {
		doRefresh();
	}

	@Override
	public void onLoadMore(ListView viw) {
		doGetMore();
	}

	@Override
	public void onItemClick(ListView view, View row, int position) {
		final Cursor c = (Cursor) view.getItemAtPosition(position);
		if (c != null) {
			final Status s = Status.parse(c);
			Utils.goStatusPage(mContext, s);
		}
	}

	@Override
	public int getDrawable() {
		return R.drawable.i_write;
	}

	@Override
	public void performAction(View view) {
		Intent intent = new Intent(this, WritePage.class);
		intent.putExtra(Commons.EXTRA_TYPE, WritePage.TYPE_NORMAL);
		if (user != null) {
			intent.putExtra(Commons.EXTRA_TEXT, "@" + user.name + " "); // 此时设置会报空指针
		}
		startActivity(intent);
	}

	@Override
	public boolean onItemLongClick(AdapterView<?> parent, View view,
			int position, long id) {
		final Cursor c = (Cursor) parent.getItemAtPosition(position);
		showPopup(view, c);
		return true;
	}

	private void showPopup(final View view, final Cursor c) {
		if (c == null) {
			return;
		}
		final Status s = Status.parse(c);
		if (s == null) {
			return;
		}
		UIManager.showPopup(mContext, c, view, s);
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.actionbar_title:
			goTop();
			break;
		default:
			break;
		}
	}

	private void goTop() {
		if (mListView != null) {
			mListView.setSelection(0);
		}
	}

}
