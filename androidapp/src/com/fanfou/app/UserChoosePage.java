package com.fanfou.app;

import android.R.bool;
import android.database.Cursor;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.ResultReceiver;
import android.util.Log;
import android.util.SparseArray;
import android.util.SparseBooleanArray;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CursorAdapter;
import android.widget.ListView;
import android.widget.Toast;

import com.fanfou.app.adapter.UserChooseCursorAdapter;
import com.fanfou.app.adapter.UserCursorAdapter;
import com.fanfou.app.api.User;
import com.fanfou.app.config.Commons;
import com.fanfou.app.db.Contents.UserInfo;
import com.fanfou.app.ui.ActionBar;
import com.fanfou.app.ui.ActionManager;
import com.fanfou.app.ui.widget.EndlessListView;
import com.fanfou.app.ui.widget.EndlessListView.OnRefreshListener;
import com.fanfou.app.util.Utils;

/**
 * @author mcxiaoke
 * @version 1.0 2011.10.21
 */
public class UserChoosePage extends BaseActivity implements OnRefreshListener{

	protected ActionBar mActionBar;
	protected EndlessListView mListView;
	protected ViewGroup mEmptyView;

	protected Cursor mCursor;
	protected CursorAdapter mCursorAdapter;

	protected Handler mHandler;
	protected ResultReceiver mResultReceiver;

	protected int page = 1;

	private boolean isInitialized = false;

	private static final String tag = UserChoosePage.class.getSimpleName();

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
		initCursor();
	}

	protected void initCursor() {
		String where = UserInfo.TYPE + "=? AND " + UserInfo.OWNER_ID
				+ "=?";
		String[] whereArgs = new String[] { String.valueOf(User.AUTO_COMPLETE), App.me.userId };
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
		mListView.setItemsCanFocus(false);
		mListView.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);
		mListView.setTextFilterEnabled(true);
		mCursorAdapter = new UserChooseCursorAdapter(mContext, mCursor);
		mListView.setAdapter(mCursorAdapter);
		mListView.removeHeader();
	}

	/**
	 * 初始化和设置ActionBar
	 */
	private void setActionBar() {
		mActionBar = (ActionBar) findViewById(R.id.actionbar);
		mActionBar.setTitle("我关注的人");
		mActionBar.setLeftAction(new ActionBar.BackAction(mContext));
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
		Bundle b = new Bundle();
		b.putString(Commons.EXTRA_ID, App.me.userId);
		b.putInt(Commons.EXTRA_PAGE, page);
		Utils.startFetchService(this, User.AUTO_COMPLETE, mResultReceiver, b);
	}

	protected void updateUI() {
		if (App.DEBUG)
			log("updateUI()");
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
			if (App.DEBUG){
				log("userId=" + u.id + " username=" + u.screenName);
				Utils.notify(this, "userId=" + u.id + " username=" + u.screenName);
				}
			SparseBooleanArray selectedItems=view.getCheckedItemPositions();
//			CheckBox cb=(CheckBox) row.findViewById(R.id.item_user_checkbox);
//			cb.setChecked(true);
//			ActionManager.doProfile(mContext, u);
		}
	}

}
