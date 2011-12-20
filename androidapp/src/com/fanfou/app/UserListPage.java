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
import android.widget.EditText;
import android.widget.FilterQueryProvider;
import android.widget.ListView;

import com.fanfou.app.App.ApnType;
import com.fanfou.app.adapter.UserCursorAdapter;
import com.fanfou.app.api.User;
import com.fanfou.app.db.Contents.BasicColumns;
import com.fanfou.app.db.Contents.UserInfo;
import com.fanfou.app.service.Constants;
import com.fanfou.app.service.FanFouService;
import com.fanfou.app.ui.ActionBar;
import com.fanfou.app.ui.ActionManager;
import com.fanfou.app.ui.TextChangeListener;
import com.fanfou.app.ui.widget.EndlessListView;
import com.fanfou.app.ui.widget.EndlessListView.OnRefreshListener;
import com.fanfou.app.util.StringHelper;
import com.fanfou.app.util.Utils;

/**
 * @author mcxiaoke
 * @version 1.0 2011.06.10
 * @version 1.5 2011.10.29
 * @version 1.6 2011.11.07
 * @version 2.0 2011.11.07
 * @version 2.1 2011.11.09
 * @version 2.2 2011.11.18
 * @version 2.3 2011.11.21
 * @version 2.4 2011.12.13
 * 
 */
public class UserListPage extends BaseActivity implements OnRefreshListener,
		FilterQueryProvider {

	protected ActionBar mActionBar;
	protected EndlessListView mListView;
	protected ViewGroup mEmptyView;

	protected EditText mEditText;

	protected Cursor mCursor;
	protected UserCursorAdapter mCursorAdapter;

	protected Handler mHandler;

	protected String userId;
	protected String userName;
	protected User user;
	protected int type;

	protected int page = 1;

	private boolean isInitialized = false;

	private static final String tag = UserListPage.class.getSimpleName();

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
		mHandler = new ResultHandler();
		initCursor();
	}

	protected void initCursor() {
		String where = BasicColumns.TYPE + "=? AND " + BasicColumns.OWNER_ID
				+ "=?";
		String[] whereArgs = new String[] { String.valueOf(type), userId };
		mCursor = managedQuery(UserInfo.CONTENT_URI, UserInfo.COLUMNS, where,
				whereArgs, null);
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
		if (App.DEBUG) {
			log("showContent()");
		}
		isInitialized = true;
		mEmptyView.setVisibility(View.GONE);
		mListView.setVisibility(View.VISIBLE);
	}

	private void setLayout() {
		setContentView(R.layout.list_users);

		setActionBar();

		mEmptyView = (ViewGroup) findViewById(R.id.empty);

		mEditText = (EditText) findViewById(R.id.choose_input);
		mEditText.addTextChangedListener(new MyTextWatcher());

		mListView = (EndlessListView) findViewById(R.id.list);
		mListView.setOnRefreshListener(this);

		mCursorAdapter = new UserCursorAdapter(mContext, mCursor);
		mCursorAdapter.setFilterQueryProvider(this);

		mListView.setAdapter(mCursorAdapter);
		registerForContextMenu(mListView);

		mListView.post(new Runnable() {

			@Override
			public void run() {
				mListView.setSelection(1);
			}
		});
	}

	private class MyTextWatcher extends TextChangeListener {
		@Override
		public void onTextChanged(CharSequence s, int start, int before,
				int count) {
			mCursorAdapter.getFilter().filter(s.toString().trim());
		}
	}

	/**
	 * 初始化和设置ActionBar
	 */
	private void setActionBar() {
		mActionBar = (ActionBar) findViewById(R.id.actionbar);
		if (user != null) {
			if (type == Constants.TYPE_USERS_FRIENDS) {
				mActionBar.setTitle(user.screenName + "关注的人");
			} else if (type == Constants.TYPE_USERS_FOLLOWERS) {
				mActionBar.setTitle("关注" + user.screenName + "的人");
			}
		}
	}

	protected boolean parseIntent() {
		Intent intent = getIntent();
		type = intent.getIntExtra(Constants.EXTRA_TYPE,
				Constants.TYPE_USERS_FRIENDS);
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
		page = 1;
		doRetrieve(false);
	}

	protected void doGetMore() {
		page++;
		doRetrieve(true);
	}

	protected void doRetrieve(boolean isGetMore) {
		if (userId == null) {
			if (App.DEBUG)
				log("userId is null");
			return;
		}
		ResultReceiver receiver = new MyResultHandler(mHandler, isGetMore);
		if (type == Constants.TYPE_USERS_FRIENDS) {
			FanFouService.doFetchFriends(this, receiver, page, userId);
		} else {
			FanFouService.doFetchFollowers(this, receiver, page, userId);
		}
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
		if (App.getApnType() != ApnType.WIFI) {
			App.getImageLoader().clearQueue();
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

	protected class ResultHandler extends Handler {

		@Override
		public void handleMessage(Message msg) {
		}

	}

	protected class MyResultHandler extends ResultReceiver {
		private boolean doGetMore;

		public MyResultHandler(Handler handler, boolean doGetMore) {
			super(handler);
			this.doGetMore = doGetMore;
		}

		@Override
		protected void onReceiveResult(int resultCode, Bundle resultData) {
			switch (resultCode) {
			case Constants.RESULT_SUCCESS:
				if (!isInitialized) {
					showContent();
				}
				int count = resultData.getInt(Constants.EXTRA_COUNT);
				if (doGetMore) {
					mListView.onLoadMoreComplete();
				} else {
					mListView.onRefreshComplete();
				}
				updateUI();
				break;
			case Constants.RESULT_ERROR:
				String msg = resultData.getString(Constants.EXTRA_ERROR);
				int errorCode = resultData.getInt(Constants.EXTRA_CODE);
				if (!isInitialized) {
					showContent();
				}
				if (doGetMore) {
					mListView.onLoadMoreComplete();
				} else {
					mListView.onRefreshComplete();
				}

				Utils.notify(mContext, msg);
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
	public void onLoadMore(ListView view) {
		doGetMore();
	}

	@Override
	public void onItemClick(ListView view, View row, int position) {
		final Cursor c = (Cursor) view.getItemAtPosition(position);
		final User u = User.parse(c);
		if (u != null) {
			if (App.DEBUG)
				log("userId=" + u.id + " username=" + u.screenName);
			ActionManager.doProfile(mContext, u);
		}
	}

	// private static final int CONTEXT_MENU_ID_TIMELINE=1001;
	// private static final int CONTEXT_MENU_ID_FAVORITES=1002;
	// private static final int CONTEXT_MENU_ID_FRIENDS=1003;
	// private static final int CONTEXT_MENU_ID_FOLLOWERS=1004;
	// private static final int CONTEXT_MENU_ID_FOLLOW=1005;
	// private static final int CONTEXT_MENU_ID_UNFOLLOW=1006;
	// private static final int CONTEXT_MENU_ID_BLOCK=1007;

	// @Override
	// public void onCreateContextMenu(ContextMenu menu, View v,
	// ContextMenuInfo menuInfo) {
	// MenuItem timeline=menu.add(0, CONTEXT_MENU_ID_TIMELINE,
	// CONTEXT_MENU_ID_TIMELINE, "查看消息");
	// MenuItem favorites=menu.add(0, CONTEXT_MENU_ID_FAVORITES,
	// CONTEXT_MENU_ID_FAVORITES, "查看收藏");
	// MenuItem friends=menu.add(0, CONTEXT_MENU_ID_FRIENDS,
	// CONTEXT_MENU_ID_FRIENDS, "查看关注的人");
	// MenuItem followers=menu.add(0, CONTEXT_MENU_ID_FOLLOWERS,
	// CONTEXT_MENU_ID_FOLLOWERS, "查看关注者");
	// MenuItem follow=menu.add(0, CONTEXT_MENU_ID_FOLLOW,
	// CONTEXT_MENU_ID_FOLLOW, "添加关注");
	// MenuItem unfollow=menu.add(0,
	// CONTEXT_MENU_ID_UNFOLLOW,CONTEXT_MENU_ID_UNFOLLOW, "取消关注");
	// MenuItem delete=menu.add(0, CONTEXT_MENU_ID_BLOCK, CONTEXT_MENU_ID_BLOCK,
	// "删除关注");
	// }

	@Override
	public Cursor runQuery(CharSequence constraint) {
		String where = BasicColumns.TYPE + " = " + type + " AND "
				+ BasicColumns.OWNER_ID + " = '" + userId + "' AND ("
				+ UserInfo.SCREEN_NAME + " like '%" + constraint + "%' OR "
				+ BasicColumns.ID + " like '%" + constraint + "%' )";
		;
		return managedQuery(UserInfo.CONTENT_URI, UserInfo.COLUMNS, where,
				null, null);
	}

}
