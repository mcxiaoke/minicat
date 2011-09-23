package com.fanfou.app;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.fanfou.app.api.Api;
import com.fanfou.app.api.ApiException;
import com.fanfou.app.api.DirectMessage;
import com.fanfou.app.config.Commons;
import com.fanfou.app.service.MessageService;
import com.fanfou.app.ui.ActionBar;
import com.fanfou.app.ui.ActionBar.AbstractAction;
import com.fanfou.app.ui.TextChangeListener;
import com.fanfou.app.util.Utils;

public class SendPage extends BaseActivity implements OnClickListener {

	private static final String TAG = SendPage.class.getSimpleName();

	public static final int TYPE_NORMAL = 0;
	public static final int TYPE_REPLY = 1;

	private ActionBar mActionBar;
	private EditText eContent;

	private TextView tTargetName;
	private TextView tTargetContent;
	private TextView tWordsCount;

	private ViewGroup vButtons;
	private ImageView bCancel;
	private ImageView bOK;

	private String userId;
	private String userName;
	private String userProfileImage;
	private String inReplyToMessageId;
	private DirectMessage origin;

	private String content;
	private int wordsCount;
	private int type;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		parseIntent(getIntent());
		initialize();
		setContentView(R.layout.send);
		setActionBar();
		setLayout();
		setValues();
	}

	private void initialize() {
	}

	private void showCount(int count) {
		if (count >= 140) {
			tWordsCount.setTextColor(getResources().getColorStateList(
					R.color.write_count_alert_text));
		} else {
			tWordsCount.setTextColor(getResources().getColorStateList(
					R.color.write_count_text));
		}
		tWordsCount.setText("剩余字数：" + (140 - count));
	}

	private void parseIntent(Intent intent) {
		origin = (DirectMessage) intent
				.getSerializableExtra(Commons.EXTRA_MESSAGE);
		if (origin != null) {
			userId = origin.senderId;
			userName = origin.senderScreenName;
			userProfileImage = origin.senderProfileImageUrl;
			inReplyToMessageId = origin.id;
		} else {
			userId = intent.getStringExtra(Commons.EXTRA_ID);
			userName = intent.getStringExtra(Commons.EXTRA_USER_NAME);
			userProfileImage = intent.getStringExtra(Commons.EXTRA_USER_HEAD);
		}
	}

	/**
	 * 初始化和设置ActionBar
	 */
	private void setActionBar() {
		mActionBar = (ActionBar) findViewById(R.id.actionbar);
		mActionBar.setTitle("发私信");
		mActionBar.setRightAction(new SendAction());
		mActionBar.setLeftAction(new ActionBar.BackAction(mContext));

	}

	private class SendAction extends AbstractAction {

		public SendAction() {
			super(R.drawable.i_send);
		}

		@Override
		public void performAction(View view) {
			doSend();
		}

	}

	private void setLayout() {
		eContent = (EditText) findViewById(R.id.write_text);
		eContent.addTextChangedListener(textMonitor);

		tTargetName = (TextView) findViewById(R.id.write_target_name);
		tTargetContent = (TextView) findViewById(R.id.write_target_content);

		tWordsCount = (TextView) findViewById(R.id.write_extra_words);

		vButtons = (ViewGroup) findViewById(R.id.write_buttons);

		bOK = (ImageView) findViewById(R.id.write_button_ok);
		bOK.setOnClickListener(this);

		bCancel = (ImageView) findViewById(R.id.write_button_cancel);
		bCancel.setOnClickListener(this);

	}

	private void setValues() {
		if (origin != null) {
			tTargetName.setText("收件人：" + userName);
			tTargetContent.setText(origin.text);
		} else {
			tTargetName.setText("收件人：" + userName);
			tTargetContent.setVisibility(View.GONE);
		}

		showCount(wordsCount);
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
		case R.id.write_button_cancel:
			finish();
			break;
		case R.id.write_button_ok:
			doSend();
			break;
		default:
			break;
		}

	}

	private void doSend() {
		if (wordsCount < 1) {
			Toast.makeText(this, "私信内容不能为空", Toast.LENGTH_SHORT).show();
			return;
		}
		if (!App.me.isLogin) {
			Utils.notify(this, "未通过验证，请先登录");
			return;
		}
		finish();
		startSendService();
	}

	private void startSendService() {
		Intent intent = new Intent(this, MessageService.class);
		intent.putExtra(Commons.EXTRA_TEXT, content);
		intent.putExtra(Commons.EXTRA_IN_REPLY_TO_ID, inReplyToMessageId);
		intent.putExtra(Commons.EXTRA_USER_ID, userId);
		if (App.DEBUG) {
			Log.d(TAG, "doSend() userId=" + userId);
		}
		startService(intent);
	}

	private final class SendMessageTask extends
			AsyncTask<Void, Void, DirectMessage> {

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
		}

		@Override
		protected void onPostExecute(DirectMessage result) {
			if (result != null) {
				Toast.makeText(getApplicationContext(), "发送成功",
						Toast.LENGTH_SHORT).show();
				finish();
			}
		}

		@Override
		protected DirectMessage doInBackground(Void... params) {
			Api api = App.me.api;
			DirectMessage result = null;
			try {
				result = api.messageCreate(userId, content,
						origin == null ? null : origin.id);
			} catch (ApiException e) {
				e.printStackTrace();
				Message msg = errorHandler.obtainMessage(0);
				msg.getData().putString("message", e.errorMessage);
				errorHandler.sendMessage(msg);
			}
			return result;
		}

	}

	private final Handler errorHandler = new Handler() {

		@Override
		public void handleMessage(Message msg) {
			Toast.makeText(getApplicationContext(),
					"发送失败：" + msg.getData().getString("message"),
					Toast.LENGTH_SHORT).show();
		}

	};

	private TextChangeListener textMonitor = new TextChangeListener() {

		@Override
		public void onTextChanged(CharSequence s, int start, int before,
				int count) {
			content = s.toString();
			wordsCount = content.length();
			showCount(wordsCount);
		}
	};

}
