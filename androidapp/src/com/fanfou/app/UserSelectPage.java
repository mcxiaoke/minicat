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
import android.widget.Toast;

import com.fanfou.app.adapter.UserCursorAdapter;
import com.fanfou.app.api.FanFouApiConfig;
import com.fanfou.app.api.User;
import com.fanfou.app.cache.ImageLoader;
import com.fanfou.app.config.Commons;
import com.fanfou.app.db.Contents.BasicColumns;
import com.fanfou.app.db.Contents.UserInfo;
import com.fanfou.app.service.FetchService;
import com.fanfou.app.ui.ActionBar;
import com.fanfou.app.ui.TextChangeListener;
import com.fanfou.app.ui.widget.EndlessListView;
import com.fanfou.app.ui.widget.EndlessListView.OnRefreshListener;
import com.fanfou.app.util.Utils;

/**
 * @author mcxiaoke
 * @version 1.0 2011.11.09
 * @version 1.1 2011.11.21
 * 
 */

// select direct message target
public class UserSelectPage extends BaseActivity implements OnRefreshListener,
		FilterQueryProvider {

	protected ActionBar mActionBar;
	protected EndlessListView mListView;
	protected ViewGroup mEmptyView;

	protected EditText mEditText;

	protected Cursor mCursor;
	protected UserCursorAdapter mCursorAdapter;

	protected Handler mHandler;
	protected ResultReceiver mResultReceiver;

	private boolean isInitialized = false;
	
	private int page=1;

	private static final String tag = UserSelectPage.class.getSimpleName();

	private void log(String message) {
		Log.i(tag, message);
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		if (App.DEBUG)
			log("onCreate");
			initialize();
			setLayout();
			initCheckState();
	}

	protected void initialize() {
		mHandler = new ResultHandler();
		mResultReceiver = new MyResultHandler(mHandler);
		// clearDB();
		initCursor();
	}

	protected void initCursor() {
		String where = UserInfo.TYPE + "=? AND " + UserInfo.OWNER_ID
				+ "=?";
		String[] whereArgs = new String[] { String.valueOf(User.TYPE_FRIENDS), App.me.userId };
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
			mCursorAdapter.getFilter().filter(s.toString());
		}
	}

	/**
	 * 初始化和设置ActionBar
	 */
	private void setActionBar() {
		mActionBar = (ActionBar) findViewById(R.id.actionbar);
		mActionBar.setTitle("我关注的人");
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
		Bundle b = new Bundle();
		b.putString(Commons.EXTRA_ID, App.me.userId);
		b.putInt(Commons.EXTRA_PAGE, page);
		b.putInt(Commons.EXTRA_COUNT, FanFouApiConfig.MAX_USERS_COUNT);
		FetchService.start(this, User.TYPE_FRIENDS, mResultReceiver, b);
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
	protected void onStop(){
		super.onStop();
		ImageLoader.getInstance(this).clearQueue();
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
			onSelected(u);
		}
	}
	
	private void onSelected(User user){
		Intent intent=new Intent();
		intent.putExtra(Commons.EXTRA_USER_ID, user.id);
		intent.putExtra(Commons.EXTRA_USER_NAME, user.screenName);
		setResult(RESULT_OK, intent);
		finish();
	}

	@Override
	public Cursor runQuery(CharSequence constraint) {
		String where = BasicColumns.TYPE + " = " + User.TYPE_FRIENDS + " AND "
				+ BasicColumns.OWNER_ID + " = '" + App.me.userId + "' AND ("
				+ UserInfo.SCREEN_NAME + " like '%" + constraint + "%' OR "
				+ BasicColumns.ID + " like '%" + constraint + "%' )";
		;
		return managedQuery(UserInfo.CONTENT_URI, UserInfo.COLUMNS, where,
				null, null);
	}

}
