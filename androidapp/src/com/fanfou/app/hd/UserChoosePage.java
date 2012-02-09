package com.fanfou.app.hd;

import java.util.ArrayList;
import java.util.List;

import android.content.Intent;
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

import com.fanfou.app.hd.R;
import com.fanfou.app.hd.adapter.UserChooseCursorAdapter;
import com.fanfou.app.hd.api.User;
import com.fanfou.app.hd.db.Contents.BasicColumns;
import com.fanfou.app.hd.db.Contents.UserInfo;
import com.fanfou.app.hd.service.AutoCompleteService;
import com.fanfou.app.hd.service.Constants;
import com.fanfou.app.hd.service.FanFouService;
import com.fanfou.app.hd.service.WakefulIntentService;
import com.fanfou.app.hd.ui.widget.TextChangeListener;
import com.fanfou.app.hd.util.StringHelper;
import com.fanfou.app.hd.util.Utils;

/**
 * @author mcxiaoke
 * @version 1.0 2011.10.21
 * @version 2.0 2011.10.24
 * @version 2.1 2011.10.26
 * @version 2.2 2011.11.01
 * @version 2.3 2011.11.07
 * @version 2.4 2011.11.18
 * @version 2.5 2011.11.21
 * @version 2.6 2011.11.25
 * @version 2.7 2011.12.02
 * @version 2.8 2011.12.23
 */
public class UserChoosePage extends BaseActivity implements
		FilterQueryProvider, OnItemClickListener {
	protected ListView mListView;
	protected EditText mEditText;
	protected ViewGroup mEmptyView;

	private ViewStub mViewStub;
	private View mButtonGroup;
	private Button okButton;
	private Button cancelButton;

	protected Cursor mCursor;
	protected UserChooseCursorAdapter mCursorAdapter;

	private List<String> mUserNames;

	protected int page = 1;

	private boolean isInitialized = false;

	private static final String tag = UserChoosePage.class.getSimpleName();

	private void log(String message) {
		Log.d(tag, message);
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
		initCursorAdapter();

	}

	protected void initCursorAdapter() {
		String where = BasicColumns.TYPE + "=? AND " + BasicColumns.OWNER_ID
				+ "=?";
		String[] whereArgs = new String[] {
				String.valueOf(Constants.TYPE_USERS_FRIENDS),
				App.getUserId() };
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
			Handler handler=new Handler();
			handler.postDelayed(new Runnable() {
				
				@Override
				public void run() {
					WakefulIntentService.sendWakefulWork(mContext, AutoCompleteService.class);
				}
			}, 30000);
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

		mViewStub = (ViewStub) findViewById(R.id.stub);

		mEmptyView = (ViewGroup) findViewById(R.id.empty);

		mEditText = (EditText) findViewById(R.id.choose_input);
		mEditText.addTextChangedListener(new MyTextWatcher());

		setListView();
	}

	private void setListView() {
		mListView = (ListView) findViewById(R.id.list);
		mListView.setCacheColorHint(0);
		mListView.setHorizontalScrollBarEnabled(false);
		mListView.setVerticalScrollBarEnabled(false);
		mListView.setSelector(getResources().getDrawable(
				R.drawable.list_selector));
		mListView.setDivider(getResources().getDrawable(R.drawable.separator));

		mListView.setOnItemClickListener(this);
		mListView.setItemsCanFocus(false);
		mListView.setChoiceMode(AbsListView.CHOICE_MODE_MULTIPLE);
		mListView.setAdapter(mCursorAdapter);
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

	protected void doRefresh() {
		page = 1;
		doRetrieve(false);
	}

	protected void doGetMore() {
		page++;
		doRetrieve(true);
	}

	protected void doRetrieve(boolean isGetMore) {
		FanFouService.doFetchFriends(this, new ResultHandler(), page,
				App.getUserId());
	}

	protected void updateUI() {
		if (App.DEBUG) {
			log("updateUI()");
		}
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
	}

	@Override
	protected void onStop() {
		super.onStop();
		App.getImageLoader().clearQueue();
	}

	@Override
	public void onClick(View v) {
		int id = v.getId();
		switch (id) {
		case R.id.button_ok:
			doAddUserNames();
			break;
		case R.id.button_cancel:
			finish();
			break;
		default:
			break;
		}
	}

	private void doAddUserNames() {
		if (!mUserNames.isEmpty()) {
			StringBuilder sb = new StringBuilder();
			for (String screenName : mUserNames) {
				sb.append("@").append(screenName).append(" ");
			}
			if (App.DEBUG) {
				log("User Names: " + sb.toString());
			}
			Intent intent = new Intent();
			intent.putExtra(Constants.EXTRA_TEXT, sb.toString());
			setResult(RESULT_OK, intent);
		}
		finish();
	}

	private class MyTextWatcher extends TextChangeListener {
		@Override
		public void onTextChanged(CharSequence s, int start, int before,
				int count) {
			resetChoices();
			mCursorAdapter.getFilter().filter(s.toString().trim());
		}
	}

	private void resetChoices() {
		SparseBooleanArray sba = mListView.getCheckedItemPositions();
		for (int i = 0; i < sba.size(); i++) {
			mCursorAdapter.setItemChecked(sba.keyAt(i), false);
		}
		mListView.clearChoices();
	}

	protected class ResultHandler extends Handler {
		
		@Override
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case Constants.RESULT_SUCCESS:
				if (!isInitialized) {
					showContent();
				}
				int count = msg.getData().getInt(Constants.EXTRA_COUNT);
				if (count > 0) {
					updateUI();
				}
				break;
			case Constants.RESULT_ERROR:
				int code = msg.getData().getInt(Constants.EXTRA_CODE);
				String errorMessage = msg.getData().getString(Constants.EXTRA_ERROR);
				Utils.notify(mContext, errorMessage);
				if (!isInitialized) {
					showContent();
				}
				break;
			default:
				break;
			}
		}

	}

	@Override
	public Cursor runQuery(CharSequence constraint) {
		String where = BasicColumns.TYPE + " = "
				+ Constants.TYPE_USERS_FRIENDS + " AND "
				+ BasicColumns.OWNER_ID + " = '" + App.getUserId() + "' AND ("
				+ UserInfo.SCREEN_NAME + " like '%" + constraint + "%' OR "
				+ BasicColumns.ID + " like '%" + constraint + "%' )";
		;
		return managedQuery(UserInfo.CONTENT_URI, UserInfo.COLUMNS, where,
				null, null);
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
			if (App.DEBUG) {
				log("sba.values i=" + i + " key=" + key + " value=" + value
						+ " cursor.size=" + mCursor.getCount()
						+ " adapter.size=" + mCursorAdapter.getCount());
			}
			if (value) {
				final Cursor cc = (Cursor) mCursorAdapter.getItem(key);
				final User uu = User.parse(cc);
				mUserNames.add(uu.screenName);
			}
		}

		if (App.DEBUG) {
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

}
