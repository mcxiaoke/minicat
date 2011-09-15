package com.fanfou.app;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.ResultReceiver;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Adapter;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.CursorAdapter;
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;
import com.fanfou.app.adapter.StatusCursorAdapter;
import com.fanfou.app.api.Status;
import com.fanfou.app.api.User;
import com.fanfou.app.config.Commons;
import com.fanfou.app.db.Contents.StatusInfo;
import com.fanfou.app.ui.ActionBar;
import com.fanfou.app.ui.UIManager;
import com.fanfou.app.ui.ActionBar.Action;
import com.fanfou.app.ui.widget.EndlessListView;
import com.fanfou.app.ui.widget.EndlessListView.OnRefreshListener;
import com.fanfou.app.util.StringHelper;
import com.fanfou.app.util.Utils;

public class TimelinePage extends BaseActivity implements OnRefreshListener,
		Action, OnItemLongClickListener, OnClickListener {

	protected ActionBar mActionBar;
	protected EndlessListView mListView;
	protected ViewGroup mEmptyView;

	protected Cursor mCursor;
	protected CursorAdapter mCursorAdapter;

	protected Handler mHandler;
	protected ResultReceiver mResultReceiver;

	protected String userId;
	protected String userName;
	protected User user;
	protected int type;

	private boolean isInitialized = false;

	private static final String tag = TimelinePage.class.getSimpleName();

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
		mResultReceiver = new MyResultHandler(mHandler);
		initCursor();

		mCursorAdapter = new StatusCursorAdapter(mContext, mCursor);
	}

	protected void initCursor() {
		if (type == Status.TYPE_USER) {
			String where = StatusInfo.TYPE + "=? AND " + StatusInfo.USER_ID
					+ "=?";
			String[] whereArgs = new String[] { String.valueOf(type), userId };
			mCursor = managedQuery(StatusInfo.CONTENT_URI, StatusInfo.COLUMNS,
					where, whereArgs, null);
		} else if (type == Status.TYPE_FAVORITES) {
			String where = StatusInfo.TYPE + "=? AND " + StatusInfo.OWNER_ID
					+ "=?";
			String[] whereArgs = new String[] { String.valueOf(type), userId };
			mCursor = managedQuery(StatusInfo.CONTENT_URI, StatusInfo.COLUMNS,
					where, whereArgs, null);
		}
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

	private void setLayout() {
		setContentView(R.layout.list);
		setActionBar();

		mEmptyView = (ViewGroup) findViewById(R.id.empty);

		mListView = (EndlessListView) findViewById(R.id.list);
		mListView.setOnRefreshListener(this);
		mListView.setOnItemLongClickListener(this);
		mListView.setAdapter(mCursorAdapter);
	}

	/**
	 * 初始化和设置ActionBar
	 */
	private void setActionBar() {
		mActionBar = (ActionBar) findViewById(R.id.actionbar);
		String title = "";
		if (!StringHelper.isEmpty(userName)) {
			title = userName + "的";
		}
		if (type == Status.TYPE_USER) {
			mActionBar.setTitle(title + "消息");
		} else if (type == Status.TYPE_FAVORITES) {
			mActionBar.setTitle(title + "收藏");
		}
		mActionBar.setTitleClickListener(this);
		mActionBar.setRightAction(this);
		mActionBar.setLeftAction(new ActionBar.BackAction(mContext));
	}

	protected boolean parseIntent() {
		Intent intent = getIntent();
		type = intent.getIntExtra(Commons.EXTRA_TYPE, Status.TYPE_USER);
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
		Bundle b = new Bundle();
		b.putString(Commons.EXTRA_ID, userId);
		b.putBoolean(Commons.EXTRA_FORMAT, false);
		if (isGetMore) {
			String maxId = Utils.getMaxId(mCursor);
			b.putString(Commons.EXTRA_MAX_ID, maxId);
		} else {
			String sinceId = Utils.getSinceId(mCursor);
			b.putString(Commons.EXTRA_SINCE_ID, sinceId);
		}
		Utils.startFetchService(this, type, mResultReceiver, b);
	}

	protected void updateUI() {
		if (mCursor != null) {
			mCursor.requery();
		}
	}

	@Override
	protected void onResume() {
		super.onResume();
		if (mListView != null) {
			mListView.restorePosition();
		}
	}

	@Override
	protected void onPause() {
		super.onPause();
		if (mListView != null) {
			mListView.savePosition();
		}
	}

	protected class ResultHandler extends Handler {

		@Override
		public void handleMessage(Message msg) {
		}

	}

	protected class MyResultHandler extends ResultReceiver {

		@Override
		protected void onReceiveResult(int resultCode, Bundle resultData) {
			switch (resultCode) {
			case Commons.RESULT_CODE_START:
				break;
			case Commons.RESULT_CODE_FINISH:
				if (!isInitialized) {
					showContent();
				}
				mListView.onRefreshComplete();
				int count = resultData.getInt(Commons.EXTRA_COUNT);
				if (count < 20) {
					mListView.onNoLoadMore();
				} else {
					mListView.onLoadMoreComplete();
				}
				updateUI();
				break;
			case Commons.RESULT_CODE_ERROR:
				if (!isInitialized) {
					showContent();
					mListView.onNoRefresh();
					mListView.onNoLoadMore();
				} else {
					mListView.onRefreshComplete();
					mListView.onLoadMoreComplete();
				}
				String msg = resultData.getString(Commons.EXTRA_ERROR_MESSAGE);
				Toast.makeText(mContext, msg, Toast.LENGTH_SHORT).show();
				break;
			default:
				break;
			}
		}

		public MyResultHandler(Handler handler) {
			super(handler);
		}

	}

	@Override
	public void onRefresh(EndlessListView view) {
		doRefresh();
	}

	@Override
	public void onLoadMore(EndlessListView view) {
		doGetMore();
	}

	@Override
	public void onItemClick(EndlessListView view, int position) {
		final Cursor c = (Cursor) view.getItemAtPosition(position);
		final Status s = Status.parse(c);
		Utils.goStatusPage(mContext, s);
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
