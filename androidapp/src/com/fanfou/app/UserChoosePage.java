package com.fanfou.app;

import java.util.ArrayList;
import java.util.List;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.ResultReceiver;
import android.util.Log;
import android.util.SparseBooleanArray;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewStub;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FilterQueryProvider;
import android.widget.ListView;
import android.widget.Toast;

import com.fanfou.app.adapter.UserChooseCursorAdapter;
import com.fanfou.app.api.User;
import com.fanfou.app.config.Commons;
import com.fanfou.app.db.Contents.UserInfo;
import com.fanfou.app.ui.ActionBar;
import com.fanfou.app.ui.TextChangeListener;
import com.fanfou.app.ui.widget.EndlessListView;
import com.fanfou.app.ui.widget.EndlessListView.OnRefreshListener;
import com.fanfou.app.util.StringHelper;
import com.fanfou.app.util.Utils;

/**
 * @author mcxiaoke
 * @version 1.0 2011.10.21
 * @version 2.0 2011.10.24
 */
public class UserChoosePage extends BaseActivity implements OnRefreshListener,
		FilterQueryProvider {

	protected ActionBar mActionBar;
	protected EndlessListView mListView;
	protected EditText mEditText;
	protected ViewGroup mEmptyView;

	private ViewStub mViewStub;
	private Button okButton;
	private Button cancelButton;

	// private boolean mViewStubInitialized;

	private TextChangeListener mTextChangeListener;

	protected Cursor mCursor;
	protected UserChooseCursorAdapter mCursorAdapter;

	protected Handler mHandler;
	protected ResultReceiver mResultReceiver;

	private List<String> mUserNames;

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
		mUserNames = new ArrayList<String>();
		mHandler = new ResultHandler();
		mResultReceiver = new MyResultHandler(mHandler);
		mTextChangeListener = new MyTextWatcher();
		initCursorAdapter();

	}

	protected void initCursorAdapter() {
		String where = UserInfo.TYPE + "=? AND " + UserInfo.OWNER_ID + "=?";
		String[] whereArgs = new String[] { String.valueOf(User.TYPE_FRIENDS),
				App.me.userId };
		mCursor = managedQuery(UserInfo.CONTENT_URI, UserInfo.COLUMNS, where,
				whereArgs, null);

		mCursorAdapter = new UserChooseCursorAdapter(mContext, mCursor);
		mCursorAdapter.setFilterQueryProvider(this);
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
		mEditText.setVisibility(View.GONE);
		mEmptyView.setVisibility(View.VISIBLE);
	}

	private void showContent() {
		if (App.DEBUG) {
			log("showContent()");
		}
		isInitialized = true;
		mEmptyView.setVisibility(View.GONE);
		mEditText.setVisibility(View.VISIBLE);
		mListView.setVisibility(View.VISIBLE);
	}

	private void setLayout() {
		setContentView(R.layout.user_choose);
		setActionBar();

		mViewStub = (ViewStub) findViewById(R.id.stub);

		mEmptyView = (ViewGroup) findViewById(R.id.empty);

		mEditText = (EditText) findViewById(R.id.choose_input);
		mEditText.addTextChangedListener(mTextChangeListener);

		mListView = (EndlessListView) findViewById(R.id.list);
		mListView.setOnRefreshListener(this);
		mListView.setItemsCanFocus(false);
		mListView.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);
		mListView.setAdapter(mCursorAdapter);
		mListView.removeHeader();
	}

	private void initViewStub() {
		// mViewStubInitialized = true;
		mViewStub.inflate();
		mViewStub = null;

		okButton = (Button) findViewById(R.id.button1);
		okButton.setText(android.R.string.ok);
		okButton.setOnClickListener(this);

		cancelButton = (Button) findViewById(R.id.button2);
		cancelButton.setText(android.R.string.cancel);
		cancelButton.setOnClickListener(this);
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
		Utils.startFetchService(this, User.TYPE_FRIENDS, mResultReceiver, b);
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
	}

	@Override
	protected void onPause() {
		super.onPause();
		App.me.clearImageTasks();
	}

	@Override
	public void onClick(View v) {
		int id = v.getId();
		switch (id) {
		case R.id.button1:
			doAddUserNames();
			break;
		case R.id.button2:
			finish();
			break;
		default:
			break;
		}
	}

	private void doAddUserNames() {
		StringBuilder sb = new StringBuilder();
		for (String screenName : mUserNames) {
			sb.append("@").append(screenName).append(" ");
		}

		log("User Names: " + sb.toString());
		Intent intent = new Intent();
		intent.putExtra(Commons.EXTRA_TEXT, sb.toString());
		setResult(RESULT_OK, intent);
		finish();
	}

	protected class ResultHandler extends Handler {

		@Override
		public void handleMessage(Message msg) {
		}

	}

	private class MyTextWatcher extends TextChangeListener {
		@Override
		public void onTextChanged(CharSequence s, int start, int before,
				int count) {
			mCursorAdapter.getFilter().filter(s.toString());
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
		SparseBooleanArray sba = view.getCheckedItemPositions();
		mUserNames.clear();
		for (int i = 0; i < sba.size(); i++) {
			mCursorAdapter.setItemChecked(sba.keyAt(i), sba.valueAt(i));
			if (sba.valueAt(i)) {
				final Cursor cc = (Cursor) view.getItemAtPosition(sba.keyAt(i));
				final User uu = User.parse(cc);
				if (App.DEBUG) {
					log("onItemClick Checked userId=" + uu.id + " username=" + uu.screenName);
				}
				mUserNames.add(uu.screenName);
			}
		}

		log(StringHelper.toString(mUserNames));

		if (mViewStub != null) {
			initViewStub();
		}

	}

	@Override
	public Cursor runQuery(CharSequence constraint) {
		String where = UserInfo.TYPE + " = " + User.TYPE_FRIENDS + " AND "
				+ UserInfo.OWNER_ID + " = '" + App.me.userId + "' AND ("
				+ UserInfo.SCREEN_NAME + " like '%" + constraint + "%' OR "
				+ UserInfo.ID + " like '%" + constraint + "%' )";
		;
		return managedQuery(UserInfo.CONTENT_URI, UserInfo.COLUMNS, where,
				null, null);
	}

}
