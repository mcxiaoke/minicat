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
import android.widget.CursorAdapter;
import android.widget.ListView;
import android.widget.Toast;

import com.fanfou.app.adapter.UserCursorAdapter;
import com.fanfou.app.api.Status;
import com.fanfou.app.api.User;
import com.fanfou.app.config.Commons;
import com.fanfou.app.db.Contents.BasicColumns;
import com.fanfou.app.db.Contents.UserInfo;
import com.fanfou.app.ui.ActionBar;
import com.fanfou.app.ui.ActionBar.Action;
import com.fanfou.app.ui.ActionManager;
import com.fanfou.app.ui.widget.EndlessListView;
import com.fanfou.app.ui.widget.EndlessListView.OnRefreshListener;
import com.fanfou.app.util.StringHelper;
import com.fanfou.app.util.Utils;

/**
 * @author mcxiaoke
 * @version 1.0 2011.06.10
 * @version 1.5 2011.10.29
 * 
 */
public class UserListPage extends BaseActivity implements OnRefreshListener{

	protected ActionBar mActionBar;
	protected EndlessListView mListView;
	protected ViewGroup mEmptyView;

	protected Cursor mCursor;
	protected UserCursorAdapter mCursorAdapter;

	protected Handler mHandler;
	protected ResultReceiver mResultReceiver;

	protected String userId;
	protected String userName;
	protected User user;
	protected int type;

	protected int page = 1;

	private boolean isInitialized = false;

	private static final String tag = UserListPage.class.getSimpleName();

	private void log(String message) {
		Log.i(tag, message);
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
		// clearDB();
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
		setContentView(R.layout.list);
		setActionBar();

		mEmptyView = (ViewGroup) findViewById(R.id.empty);

		mListView = (EndlessListView) findViewById(R.id.list);
		mListView.setOnRefreshListener(this);
		// mListView.setOnItemClickListener(this);

		mCursorAdapter = new UserCursorAdapter(mContext, mCursor);
		mListView.setAdapter(mCursorAdapter);
	}

	/**
	 * 初始化和设置ActionBar
	 */
	private void setActionBar() {
		mActionBar = (ActionBar) findViewById(R.id.actionbar);
		
		if(user!=null){
			if (type == User.TYPE_FRIENDS) {
				mActionBar.setTitle(user.screenName + "关注的人");
			} else if (type == User.TYPE_FOLLOWERS) {
				mActionBar.setTitle("关注" + user.screenName + "的人");
			}
		}

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
		page = 1;
		doRetrieve(false);
	}

	protected void doGetMore() {
		page++;
		doRetrieve(true);
	}

	protected void doRetrieve(boolean isGetMore) {
		if (!App.me.isLogin) {
			Utils.notify(this, "未通过验证，请登录");
			return;
		}
		if (userId == null) {
			if (App.DEBUG)
				log("userId is null");
			return;
		}
		Bundle b = new Bundle();
		b.putString(Commons.EXTRA_ID, userId);
		b.putInt(Commons.EXTRA_PAGE, page);
		Utils.startFetchService(this, type, mResultReceiver, b);
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
		App.me.clearImageTasks();
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
				if (count < 100) {
					mListView.onNoLoadMore();
				} else {
					mListView.onLoadMoreComplete();
				}
				updateUI();
				break;
			case Commons.RESULT_CODE_ERROR:
				int code = resultData.getInt(Commons.EXTRA_ERROR_CODE);
				String msg = resultData.getString(Commons.EXTRA_ERROR_MESSAGE);
				Toast.makeText(mContext, msg, Toast.LENGTH_SHORT).show();
				// if(code==ResponseCode.HTTP_FORBIDDEN||code==ResponseCode.HTTP_NOT_FOUND){
				// finish();
				// return;
				// }
				if (!isInitialized) {
					showContent();
					mListView.onNoRefresh();
					mListView.onNoLoadMore();
				} else {
					mListView.onRefreshComplete();
					mListView.onLoadMoreComplete();
				}
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
}
