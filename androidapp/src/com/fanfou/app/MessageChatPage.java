package com.fanfou.app;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;

import com.fanfou.app.adapter.MessageCursorAdapter;
import com.fanfou.app.config.Actions;
import com.fanfou.app.config.Commons;
import com.fanfou.app.db.Contents.BasicColumns;
import com.fanfou.app.db.Contents.DirectMessageInfo;
import com.fanfou.app.service.PostMessageService;
import com.fanfou.app.ui.ActionBar;
import com.fanfou.app.ui.ActionBar.AbstractAction;
import com.fanfou.app.ui.TextChangeListener;
import com.fanfou.app.ui.widget.EndlessListView;
import com.fanfou.app.util.IntentHelper;
import com.fanfou.app.util.StringHelper;
import com.fanfou.app.util.Utils;

/**
 * @author mcxiaoke
 * @version 1.0 2011.10.08
 * 
 */
public class MessageChatPage extends BaseActivity {

	private static final String TAG = MessageChatPage.class.getSimpleName();
	private String mUserId;
	private String mUserName;

	private Cursor mCursor;
	private EndlessListView mListView;
	private MessageCursorAdapter mCursorAdapter;

	private ActionBar mActionBar;

	private EditText mEditText;

	private String mContent;

//	private BroadcastReceiver mSendSuccessReceiver;
//	private IntentFilter mSendSuccessFilter;

	// private Button mButton;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		if (parseIntent()) {
//			initSendSuccessReceiver();
			setLayout();
			setActionBar();
			setListView();
			updateUI();
		} else {
			finish();
			return;
		}
	}

//	private void initSendSuccessReceiver() {
//		mSendSuccessReceiver = new SendSuccessReceiver();
//		mSendSuccessFilter = new IntentFilter(Actions.ACTION_MESSAGE_SEND);
//		mSendSuccessFilter.setPriority(1000);
//	}

//	private class SendSuccessReceiver extends BroadcastReceiver {
//
//		@Override
//		public void onReceive(Context context, Intent intent) {
//			if (App.DEBUG) {
//				Log.d(TAG, "SendSuccessReceiver.received");
//				IntentHelper.logIntent(TAG, intent);
//			}
//			onSendSuccess();
//			abortBroadcast();
//		}
//
//	}

	private void onSendSuccess() {

	}

	private boolean parseIntent() {
		Intent intent = getIntent();
		mUserId = intent.getStringExtra(Commons.EXTRA_USER_ID);
		mUserName = intent.getStringExtra(Commons.EXTRA_USER_NAME);

		if (App.DEBUG) {
			IntentHelper.logIntent(TAG, intent);
		}
		return !StringHelper.isEmpty(mUserId);
	}

	private void setLayout() {
		setContentView(R.layout.chat);
		mEditText = (EditText) findViewById(R.id.msgchat_input);
		mEditText.addTextChangedListener(textMonitor);
	}

	private void setActionBar() {
		mActionBar = (ActionBar) findViewById(R.id.actionbar);
		mActionBar.setLeftAction(new ActionBar.BackAction(this));
		mActionBar.setTitle("写私信");
		mActionBar.setRightAction(new SendAction());
	}

	private void setListView() {
		initCursor();
		mCursorAdapter = new MessageCursorAdapter(this, mCursor,true);
		mListView = (EndlessListView) findViewById(R.id.list);
		mListView.setAdapter(mCursorAdapter);
		mListView.removeHeader();
		mListView.removeFooter();
		mListView.setSelection(mListView.getCount() - 1);
	}

	private void initCursor() {
		String where = DirectMessageInfo.THREAD_USER_ID + "=?";
		String[] whereArgs = new String[] { mUserId };
		String orderBy = BasicColumns.CREATED_AT;
		mCursor = managedQuery(DirectMessageInfo.CONTENT_URI,
				DirectMessageInfo.COLUMNS, where, whereArgs, orderBy);

		if (mCursor != null && mCursor.getCount() == 0) {
			mActionBar.setTitle("给" + mUserName + "写私信");
		}
	}

	private void updateUI() {
		if (mCursor != null) {
			mCursor.requery();
		}
	}

	@Override
	protected void onResume() {
		super.onResume();
//		registerReceiver(mSendSuccessReceiver, mSendSuccessFilter);
	}

	@Override
	protected void onPause() {
//		unregisterReceiver(mSendSuccessReceiver);
		super.onPause();
	}

	@Override
	public void onClick(View v) {
		super.onClick(v);
	}

	private class SendAction extends AbstractAction {

		public SendAction() {
			super(R.drawable.i_send);
		}

		@Override
		public void performAction(View view) {
			send();
		}

	}

	private void send() {
		if (StringHelper.isEmpty(mContent)) {
			Utils.notify(this, "私信内容不能为空");
			return;
		}
		finish();
		startSendService();
	}

	private void startSendService() {
		Intent i = new Intent(mContext, PostMessageService.class);
		i.putExtra(Commons.EXTRA_USER_ID, mUserId);
		i.putExtra(Commons.EXTRA_USER_NAME, mUserName);
		i.putExtra(Commons.EXTRA_TEXT, mContent);
		startService(i);
	}

	private TextChangeListener textMonitor = new TextChangeListener() {

		@Override
		public void onTextChanged(CharSequence s, int start, int before,
				int count) {
			mContent = s.toString();
		}
	};

	@Override
	protected void onRestoreInstanceState(Bundle savedInstanceState) {
		super.onRestoreInstanceState(savedInstanceState);
	}

	@Override
	protected void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
	}

}
