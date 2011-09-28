package com.fanfou.app;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextPaint;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.fanfou.app.api.DirectMessage;
import com.fanfou.app.config.Commons;
import com.fanfou.app.service.MessageService;
import com.fanfou.app.ui.ActionBar;
import com.fanfou.app.ui.ActionBar.AbstractAction;
import com.fanfou.app.ui.TextChangeListener;
import com.fanfou.app.util.DateTimeHelper;
import com.fanfou.app.util.Utils;

/**
 * @author mcxiaoke
 * 
 */
public class SendPage extends BaseActivity {

	private static final String TAG = SendPage.class.getSimpleName();

	public static final int TYPE_NORMAL = 0;
	public static final int TYPE_REPLY = 1;

	private ActionBar mActionBar;
	private EditText eContent;

	private View vOrigin;
	private ImageView iOriginHead;
	private TextView tOriginName;
	private TextView tOriginText;
	private TextView tOriginDate;

	private String userId;
	private String userName;
	private String inReplyToMessageId;
	private DirectMessage origin;

	private String text;
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
		// if (count >= 140) {
		// tWordsCount.setTextColor(getResources().getColorStateList(
		// R.color.write_count_alert_text));
		// } else {
		// tWordsCount.setTextColor(getResources().getColorStateList(
		// R.color.write_count_text));
		// }
		// tWordsCount.setText("剩余字数：" + (140 - count));
	}

	private void parseIntent(Intent intent) {
		origin = (DirectMessage) intent
				.getSerializableExtra(Commons.EXTRA_MESSAGE);
		if (origin != null) {
			userId = origin.senderId;
			userName = origin.senderScreenName;
			inReplyToMessageId = origin.id;
		} else {
			userId = intent.getStringExtra(Commons.EXTRA_ID);
			userName = intent.getStringExtra(Commons.EXTRA_USER_NAME);
		}
	}

	/**
	 * 初始化和设置ActionBar
	 */
	private void setActionBar() {
		mActionBar = (ActionBar) findViewById(R.id.actionbar);
		mActionBar.setRightAction(new SendAction());
		mActionBar.setLeftAction(new ActionBar.BackAction(mContext));
		mActionBar.setTitle(userName);

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
		
		vOrigin=findViewById(R.id.send_origin);
		iOriginHead = (ImageView) findViewById(R.id.send_origin_head);
		tOriginName = (TextView) findViewById(R.id.send_origin_name);
		tOriginText = (TextView) findViewById(R.id.send_origin_text);
		tOriginDate = (TextView) findViewById(R.id.send_origin_date);
		
		TextPaint tp = tOriginName.getPaint();
		tp.setFakeBoldText(true);

		eContent = (EditText) findViewById(R.id.send_text);
		eContent.addTextChangedListener(new TextChangeListener() {

			@Override
			public void onTextChanged(CharSequence s, int start, int before,
					int count) {
				text = s.toString();
				wordsCount = text.length();
				showCount(wordsCount);
			}
		});
		// tWordsCount = (TextView) findViewById(R.id.write_extra_words);

	}

	private void setValues() {
		if (origin != null) {
			tOriginName.setText(origin.senderScreenName);
			tOriginText.setText(origin.text);
			tOriginDate.setText(DateTimeHelper.formatDate(origin.createdAt));
			App.me.getImageLoader().set(origin.senderProfileImageUrl,
					iOriginHead, R.drawable.default_head);
		}else{
			vOrigin.setVisibility(View.INVISIBLE);
		}
		// showCount(wordsCount);
	}

	@Override
	protected void onResume() {
		super.onResume();
	}

	@Override
	protected void onPause() {
		super.onPause();
	}

	private void doSend() {
		if (wordsCount < 1) {
			Toast.makeText(this, "私信内容不能为空", Toast.LENGTH_SHORT).show();
			return;
		}
		if (!App.me.isLogin) {
			Utils.notify(this, "未通过验证，请先登录");
			LoginPage.doLogin(this);
			return;
		}
		finish();
		startSendService();
	}

	private void startSendService() {
		Intent intent = new Intent(this, MessageService.class);
		intent.putExtra(Commons.EXTRA_TEXT, text);
		intent.putExtra(Commons.EXTRA_IN_REPLY_TO_ID, inReplyToMessageId);
		intent.putExtra(Commons.EXTRA_USER_ID, userId);
		if (App.DEBUG) {
			Log.d(TAG, "doSend() userId=" + userId);
		}
		startService(intent);
	}

}
