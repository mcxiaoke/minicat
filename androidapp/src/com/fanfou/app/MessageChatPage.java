package com.fanfou.app;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.os.Bundle;
import android.util.Log;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView.AdapterContextMenuInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import com.fanfou.app.adapter.MessageCursorAdapter;
import com.fanfou.app.api.DirectMessage;
import com.fanfou.app.config.Actions;
import com.fanfou.app.config.Commons;
import com.fanfou.app.db.Contents.BasicColumns;
import com.fanfou.app.db.Contents.DirectMessageInfo;
import com.fanfou.app.service.PostMessageService;
import com.fanfou.app.ui.ActionBar;
import com.fanfou.app.ui.ActionManager;
import com.fanfou.app.ui.ActionBar.AbstractAction;
import com.fanfou.app.ui.TextChangeListener;
import com.fanfou.app.util.IOHelper;
import com.fanfou.app.util.IntentHelper;
import com.fanfou.app.util.StringHelper;
import com.fanfou.app.util.Utils;

/**
 * @author mcxiaoke
 * @version 1.0 2011.10.08
 * @version 1.1 2011.10.25
 * @version 1.2 2011.10.26
 * @version 1.3 2011.11.07
 * 
 */
public class MessageChatPage extends BaseActivity {

	private static final String TAG = MessageChatPage.class.getSimpleName();
	private String mUserId;
	private String mUserName;

	private Cursor mCursor;
	private ListView mListView;
	private MessageCursorAdapter mCursorAdapter;

	private ActionBar mActionBar;

	private EditText mEditText;

	private Button mSendButton;

	private String mContent;

	private BroadcastReceiver mSendSuccessReceiver;
	private IntentFilter mSendSuccessFilter;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		if (parseIntent()) {
			initSendSuccessReceiver();
			setLayout();
			setActionBar();
			setListView();
			updateUI();
		} else {
			finish();
			return;
		}
	}

	private void initSendSuccessReceiver() {
		mSendSuccessReceiver = new SendSuccessReceiver();
		mSendSuccessFilter = new IntentFilter(Actions.ACTION_MESSAGE_SENT);
		mSendSuccessFilter.setPriority(1000);
	}

	private class SendSuccessReceiver extends BroadcastReceiver {

		@Override
		public void onReceive(Context context, Intent intent) {
			if (App.DEBUG) {
				Log.d(TAG, "SendSuccessReceiver.received");
			}
			if (onSendSuccess()) {
				abortBroadcast();
			}
		}

	}

	private boolean onSendSuccess() {
		Utils.notify(this, "私信发送成功！");
		mListView.setSelection(mCursorAdapter.getCount());
		return true;
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

		mSendButton = (Button) findViewById(R.id.button_ok);
		mSendButton.setOnClickListener(this);

		// boolean sendOnEnter=OptionHelper.readBoolean(this,
		// R.string.option_send_on_enter, false);
		// if(sendOnEnter){
		// mEditText.setImeOptions(EditorInfo.IME_ACTION_SEND);
		// mEditText.setOnEditorActionListener(new OnEditorActionListener() {
		// @Override
		// public boolean onEditorAction(TextView v, int actionId, KeyEvent
		// event) {
		// switch (actionId) {
		// case EditorInfo.IME_ACTION_SEND:
		// doSend(false);
		// return true;
		// default:
		// break;
		// }
		// return false;
		// }
		// });
		// }else{
		// mEditText.setImeOptions(EditorInfo.IME_ACTION_DONE);
		// }
	}

	private void setActionBar() {
		mActionBar = (ActionBar) findViewById(R.id.actionbar);
		mActionBar.setLeftAction(new ActionBar.BackAction(this));
		mActionBar.setTitle("写私信");
		mActionBar.setRightAction(new SendAction());
	}

	private void setListView() {
		initCursor();
		mCursorAdapter = new MessageCursorAdapter(this, mCursor, true, true);

		mListView = (ListView) findViewById(R.id.list);
		registerForContextMenu(mListView);

		mListView.setCacheColorHint(0);
		mListView.setHorizontalScrollBarEnabled(false);
		mListView.setVerticalScrollBarEnabled(false);
		mListView.setSelector(getResources().getDrawable(
				R.drawable.list_selector));
		mListView.setDivider(getResources().getDrawable(R.drawable.separator));
		mListView.setAdapter(mCursorAdapter);
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
		registerReceiver(mSendSuccessReceiver, mSendSuccessFilter);
	}

	@Override
	protected void onPause() {
		unregisterReceiver(mSendSuccessReceiver);
		super.onPause();
	}

	@Override
	public void onClick(View v) {
		int id = v.getId();
		switch (id) {
		case R.id.button_ok:
			doSend(false);
			break;

		default:
			break;
		}
	}

	@Override
	public void onCreateContextMenu(ContextMenu menu, View v,
			ContextMenuInfo menuInfo) {
		getMenuInflater().inflate(R.menu.dm_list_menu, menu);
		super.onCreateContextMenu(menu, v, menuInfo);
	}

	@Override
	public boolean onContextItemSelected(MenuItem item) {
		final AdapterContextMenuInfo menuInfo = (AdapterContextMenuInfo) item
				.getMenuInfo();
		int id = item.getItemId();
		final Cursor c = (Cursor) mCursorAdapter.getItem(menuInfo.position);
		if (c != null) {
			final DirectMessage dm = DirectMessage.parse(c);
			if (dm != null && !dm.isNull()) {
				switch (id) {
				case R.id.dm_copy:
					doCopy(dm);
					break;
				case R.id.dm_delete:
					doDelete(dm);
					break;
				default:
					break;
				}
			}
		}

		return true;
		// return super.onContextItemSelected(item);
	}

	private void doCopy(DirectMessage dm) {
		IOHelper.copyToClipBoard(this, dm.senderScreenName + "：" + dm.text);
		Utils.notify(this, "私信内容已复制到剪贴板");
	}

	private void doDelete(DirectMessage dm) {
		ActionManager.doMessageDelete(this, dm.id, null, false);
		// if (dm.type == DirectMessage.TYPE_OUT) {
		// ActionManager.doMessageDelete(this, dm.id, null, false);
		// } else {
		// Utils.notify(this, "只能删除你自己发送的私信");
		// }
	}

	private class SendAction extends AbstractAction {

		public SendAction() {
			super(R.drawable.ic_send);
		}

		@Override
		public void performAction(View view) {
			doSend(true);
		}

	}

	private void doSend(boolean finish) {
		if (StringHelper.isEmpty(mContent)) {
			Utils.notify(this, "私信内容不能为空");
			return;
		}
		startSendService();
		if (finish) {
			Utils.hideKeyboard(this, mEditText);
			finish();
		} else {
			mEditText.setText("");
		}
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
