package com.fanfou.app.hd;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewStub;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

import com.fanfou.app.hd.cache.IImageLoader;
import com.fanfou.app.hd.controller.DataController;
import com.fanfou.app.hd.dao.model.DirectMessageModel;
import com.fanfou.app.hd.fragments.ConversationFragment;
import com.fanfou.app.hd.service.PostMessageService;
import com.fanfou.app.hd.ui.widget.TextChangeListener;
import com.fanfou.app.hd.util.IOHelper;
import com.fanfou.app.hd.util.IntentHelper;
import com.fanfou.app.hd.util.Utils;

/**
 * @author mcxiaoke
 * @version 1.0 2011.10.08
 * @version 1.1 2011.10.25
 * @version 1.2 2011.10.26
 * @version 1.3 2011.11.07
 * @version 1.4 2011.11.18
 * @version 1.5 2012.02.09
 * @version 1.6 2012.02.10
 * @version 2.0 2012.02.21
 * @version 3.0 2012.02.28
 * 
 */
public class UIConversation extends UIBaseSupport {

	private static final String TAG = UIConversation.class.getSimpleName();
	private String userId;
	private String screenName;
	private String profileImageUrl;

	private ViewGroup container;
	private ViewStub mViewStub;
	private ViewGroup recipientView;
	private TextView recipientText;
	private ImageButton recipientIcon;
	private TextView recipientName;

	private EditText mEditText;

	private Button btnSend;

	private String text;

	private ConversationFragment fragment;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
	}

	private void parseIntent() {
		Intent intent = getIntent();
		userId = intent.getStringExtra("id");
		screenName = intent.getStringExtra("screen_name");
		profileImageUrl = intent.getStringExtra("profile_image_url");

		if (App.DEBUG) {
			IntentHelper.logIntent(TAG, intent);
		}
	}

	@Override
	protected void initialize() {
		parseIntent();
	}

	@Override
	protected void setLayout() {
		setContentView(R.layout.ui_conversation);

		container = (ViewGroup) findViewById(R.id.container);

		mEditText = (EditText) findViewById(R.id.input);
		mEditText.addTextChangedListener(new TextChangeListener() {

			@Override
			public void onTextChanged(CharSequence s, int start, int before,
					int count) {
				text = s.toString().trim();
			}
		});

		btnSend = (Button) findViewById(R.id.button_ok);
		btnSend.setOnClickListener(this);

		mViewStub = (ViewStub) findViewById(R.id.stub);

		if (TextUtils.isEmpty(userId)) {
			initRecipientView();
		} else {
			setTitle(screenName);
			setFragment();
			// checkConversation();
		}

	}

	private void setFragment() {
		fragment = ConversationFragment.newInstance(userId);

		FragmentManager fm = getSupportFragmentManager();
		FragmentTransaction ft = fm.beginTransaction();
		ft.add(R.id.container, fragment);
		ft.commit();
		
		if (App.DEBUG) {
			Log.d(TAG, "setFragment() userId=" + userId + " screenName="
					+ screenName);
		}
	}

	private void initRecipientView() {
		recipientView = (ViewGroup) mViewStub.inflate();
		recipientIcon = (ImageButton) findViewById(R.id.recipient_choose);
		recipientText = (TextView) findViewById(R.id.recipient_text);
		recipientName = (TextView) findViewById(R.id.recipient_name);
		recipientIcon.setOnClickListener(this);
	}

	private void updateRecipientView() {
		IImageLoader loader = App.getImageLoader();
		recipientName.setText(screenName);
		recipientIcon.setTag(profileImageUrl);
		loader.displayImage(profileImageUrl, recipientIcon, 0);

	}

	@Override
	public void onClick(View v) {
		int id = v.getId();
		switch (id) {
		case R.id.button_ok:
			doSend(false);
			break;
		case R.id.recipient_choose:
			startSelectUser();
			break;
		default:
			break;
		}
	}

	private static final int REQUEST_CODE_SELECT_USER = 2001;

	private void startSelectUser() {
		Intent intent = new Intent(this, UIUserSelect.class);
		startActivityForResult(intent, REQUEST_CODE_SELECT_USER);
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if (resultCode == RESULT_OK) {
			if (requestCode == REQUEST_CODE_SELECT_USER) {
				userId = data.getStringExtra("id");
				screenName = data.getStringExtra("screen_name");
				profileImageUrl = data.getStringExtra("profile_image_url");
			}
		}
	}

	private void doCopy(DirectMessageModel dm) {
		IOHelper.copyToClipBoard(this,
				dm.getSenderScreenName() + "：" + dm.getText());
		Utils.notify(this, "私信内容已复制到剪贴板");
	}

	private void doDelete(DirectMessageModel dm) {
		// FanFouService.doDirectMessagesDelete(this, id, handler);
		// if (dm.type == DirectMessage.TYPE_OUT) {
		// ActionManager.doMessageDelete(this, dm.id, null, false);
		// } else {
		// Utils.notify(this, "只能删除你自己发送的私信");
		// }
	}

	private void doSend(boolean finish) {
		if (TextUtils.isEmpty(text)) {
			Utils.notify(this, "私信内容不能为空");
			return;
		}

		if (TextUtils.isEmpty(userId)) {
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
		i.putExtra("id", userId);
		i.putExtra("screen_name", screenName);
		i.putExtra("text", text);
		startService(i);
	}

}
