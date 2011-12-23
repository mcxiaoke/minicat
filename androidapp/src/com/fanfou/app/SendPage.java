package com.fanfou.app;

import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.os.Bundle;
import android.provider.BaseColumns;
import android.text.Selection;
import android.util.Log;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewStub;
import android.widget.AdapterView;
import android.widget.AdapterView.AdapterContextMenuInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.MultiAutoCompleteTextView;

import com.fanfou.app.adapter.AutoCompleteCursorAdapter;
import com.fanfou.app.adapter.MessageCursorAdapter;
import com.fanfou.app.adapter.SpaceTokenizer;
import com.fanfou.app.api.DirectMessage;
import com.fanfou.app.api.User;
import com.fanfou.app.db.Contents.BasicColumns;
import com.fanfou.app.db.Contents.DirectMessageInfo;
import com.fanfou.app.db.Contents.UserInfo;
import com.fanfou.app.service.Constants;
import com.fanfou.app.service.FanFouService;
import com.fanfou.app.service.PostMessageService;
import com.fanfou.app.ui.ActionBar;
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
 * @version 1.4 2011.11.18
 * 
 */
public class SendPage extends BaseActivity {

	private static final String TAG = SendPage.class.getSimpleName();
	private String mUserId;
	private String mUserName;
	private String mSelectInput;

	private Cursor mCursor;
	private ListView mListView;
	private MessageCursorAdapter mCursorAdapter;

	private ViewStub mViewStub;
	private ViewGroup mSelectView;
	private ImageView mSelectButton;
	private MultiAutoCompleteTextView mSelectAutoComplete;

	private ActionBar mActionBar;

	private EditText mEditText;

	private Button mSendButton;

	private String mContent;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		parseIntent();
		setLayout();
		checkUserId();
	}

	@Override
	protected IntentFilter getIntentFilter() {
		IntentFilter filter= new IntentFilter(Constants.ACTION_MESSAGE_SENT);
		filter.setPriority(1000);
		return filter;
	}

	@Override
	protected boolean onBroadcastReceived(Intent intent) {
		Utils.notify(this, "私信发送成功！");
		mListView.setSelection(mCursorAdapter.getCount());
		return true;
	}

	private void parseIntent() {
		Intent intent = getIntent();
		mUserId = intent.getStringExtra(Constants.EXTRA_ID);
		mUserName = intent.getStringExtra(Constants.EXTRA_USER_NAME);
		if (App.DEBUG) {
			IntentHelper.logIntent(TAG, intent);
		}
	}

	private void setLayout() {
		setContentView(R.layout.send);

		setActionBar();
		mEditText = (EditText) findViewById(R.id.msgchat_input);
		mEditText.addTextChangedListener(new TextChangeListener() {

			@Override
			public void onTextChanged(CharSequence s, int start, int before,
					int count) {
				mContent = s.toString().trim();
			}
		});

		mSendButton = (Button) findViewById(R.id.button_ok);
		mSendButton.setOnClickListener(this);

		mListView = (ListView) findViewById(R.id.list);
		mViewStub = (ViewStub) findViewById(R.id.stub);

	}

	private void checkUserId() {
		if (App.DEBUG) {
			Log.d(TAG, "checkUserId userId=" + mUserId);
		}
		if (StringHelper.isEmpty(mUserId)) {
			mSelectView = (ViewGroup) mViewStub.inflate();
			mSelectButton = (ImageView) findViewById(R.id.send_select_button);
			mSelectButton.setOnClickListener(this);
			setAutoComplete();
		} else {
			setListView();
			updateUI();
		}
	}

	private void setAutoComplete() {
		mSelectAutoComplete = (MultiAutoCompleteTextView) findViewById(R.id.send_select_edit);
		mSelectAutoComplete.setTokenizer(new SpaceTokenizer());
		mSelectAutoComplete.setBackgroundColor(R.color.background_color);
		final String[] projection = new String[] { BaseColumns._ID,
				BasicColumns.ID, UserInfo.SCREEN_NAME, BasicColumns.TYPE,
				BasicColumns.OWNER_ID };
		String where = BasicColumns.TYPE + " = '"
				+ Constants.TYPE_USERS_FRIENDS + "'";
		Cursor c = managedQuery(UserInfo.CONTENT_URI, projection, where,
				null, null);
		mSelectAutoComplete.setAdapter(new AutoCompleteCursorAdapter(this, c));
		mSelectAutoComplete.addTextChangedListener(new TextChangeListener() {

			@Override
			public void onTextChanged(CharSequence s, int start, int before,
					int count) {
				mUserId = null;
				mUserName = null;
			}
		});

		mSelectAutoComplete
				.setOnItemClickListener(new ListView.OnItemClickListener() {

					@Override
					public void onItemClick(AdapterView<?> parent, View view,
							int position, long id) {
						if (App.DEBUG) {
							Log.d(TAG, "onItemClick position=" + position);
						}
						final Cursor c = (Cursor) parent
								.getItemAtPosition(position);
						if (c != null) {
							final User user = User.parse(c);
							if (user != null && !user.isNull()) {
								mUserId = user.id;
								mUserName = user.screenName;
								if (App.DEBUG) {
									Log.d(TAG, "onItemClick user.id=" + user.id);
								}
							}
						}
					}
				});

	}

	private void setActionBar() {
		mActionBar = (ActionBar) findViewById(R.id.actionbar);
		mActionBar.setTitle("写私信");
		mActionBar.setRightAction(new SendAction());
	}

	private void setListView() {
		initCursor();
		registerForContextMenu(mListView);
		mCursorAdapter = new MessageCursorAdapter(this, mCursor, true, true);
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
	}

	private void updateUI() {
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
	public void onClick(View v) {
		int id = v.getId();
		switch (id) {
		case R.id.button_ok:
			doSend(false);
			break;
		case R.id.send_select_button:
			startSelectUser();
			break;
		default:
			break;
		}
	}

	private static final int REQUEST_CODE_SELECT_USER = 2001;

	private void startSelectUser() {
		Intent intent = new Intent(this, UserSelectPage.class);
		startActivityForResult(intent, REQUEST_CODE_SELECT_USER);
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if (resultCode == RESULT_OK) {
			if (requestCode == REQUEST_CODE_SELECT_USER) {
				mUserId = data.getStringExtra(Constants.EXTRA_ID);
				mUserName = data.getStringExtra(Constants.EXTRA_USER_NAME);
				mSelectAutoComplete.setText(mUserName);
				Selection.setSelection(mSelectAutoComplete.getEditableText(),
						mSelectAutoComplete.getEditableText().length());
			}
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
		FanFouService.doMessageDelete(this, dm.id, null, false);
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
		if (StringHelper.isEmpty(mUserId)) {
			Utils.notify(this, "请选择收件人");
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
		i.putExtra(Constants.EXTRA_ID, mUserId);
		i.putExtra(Constants.EXTRA_USER_NAME, mUserName);
		i.putExtra(Constants.EXTRA_TEXT, mContent);
		startService(i);
	}

	@Override
	protected void onRestoreInstanceState(Bundle savedInstanceState) {
		super.onRestoreInstanceState(savedInstanceState);
	}

	@Override
	protected void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
	}

}
