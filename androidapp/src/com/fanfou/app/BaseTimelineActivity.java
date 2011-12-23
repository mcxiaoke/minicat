package com.fanfou.app;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.Messenger;
import android.os.Parcelable;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.ListView;

import com.fanfou.app.adapter.StatusCursorAdapter;
import com.fanfou.app.api.Status;
import com.fanfou.app.api.User;
import com.fanfou.app.service.Constants;
import com.fanfou.app.ui.ActionBar;
import com.fanfou.app.ui.ActionBar.AbstractAction;
import com.fanfou.app.ui.ActionManager;
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
 * @version 3.3 2011.11.18
 * @version 3.4 2011.12.13
 * @version 3.5 2011.12.23
 * 
 */
public abstract class BaseTimelineActivity extends BaseActivity implements
		OnRefreshListener, OnItemLongClickListener {

	protected ActionBar mActionBar;
	protected EndlessListView mListView;
	protected ViewGroup mEmptyView;

	protected Cursor mCursor;
	protected StatusCursorAdapter mCursorAdapter;

	protected String userId;
	protected String userName;
	protected User user;

	protected boolean isInitialized = false;

	private static final String tag = BaseTimelineActivity.class
			.getSimpleName();

	private void log(String message) {
		Log.d(tag, message);
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
		mCursor = getCursor();
		mCursorAdapter = new StatusCursorAdapter(true, this, mCursor);

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
		mActionBar.setRightAction(new WriteAction(this));
		if (user != null) {
			mActionBar.setTitle(user.screenName + "的" + getPageTitle());
		}
	}

	public class WriteAction extends AbstractAction {

		public WriteAction(Context context) {
			super(R.drawable.i_write);
		}

		@Override
		public void performAction(View view) {
			String text = null;
			if (user != null) {
				text = "@" + user.screenName + " ";
			}
			ActionManager.doWrite(mContext, text);
		}
	}

	protected boolean parseIntent() {
		Intent intent = getIntent();
		user = (User) intent.getParcelableExtra(Constants.EXTRA_DATA);
		if (user == null) {
			userId = intent.getStringExtra(Constants.EXTRA_ID);
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

	protected void doRetrieve(boolean doGetMore) {
		doRetrieveImpl(new Messenger(new ResultHandler(doGetMore)), doGetMore);
	}

	protected abstract void doRetrieveImpl(final Messenger messenger,
			boolean isGetMore);

	protected abstract Cursor getCursor();

	protected abstract String getPageTitle();

	protected void updateUI() {
		if (mCursor != null) {
			mCursor.requery();
		}
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

	private class ResultHandler extends Handler {
		private final boolean doGetMore;

		public ResultHandler(boolean doGetMore) {
			this.doGetMore = doGetMore;
		}

		@Override
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case Constants.RESULT_SUCCESS:
				int type=msg.arg1;
				if (!isInitialized) {
					showContent();
				}
				if (doGetMore) {
					mListView.onLoadMoreComplete();
				} else {
					mListView.onRefreshComplete();
				}
				updateUI();
				break;
			case Constants.RESULT_ERROR:
				String errorMessage = msg.getData().getString(
						Constants.EXTRA_ERROR);
				int errorCode = msg.getData().getInt(Constants.EXTRA_CODE);

				if (!isInitialized) {
					showContent();
				}
				if (doGetMore) {
					mListView.onLoadMoreComplete();
				} else {
					mListView.onRefreshComplete();
				}

				Utils.notify(mContext, errorMessage);
				Utils.checkAuthorization(mContext, errorCode);
				break;
			default:
				break;
			}
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
