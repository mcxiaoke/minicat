package com.mcxiaoke.minicat.app;

import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import com.mcxiaoke.minicat.AppContext;
import com.mcxiaoke.minicat.R;
import com.mcxiaoke.minicat.fragment.ConversationFragment;
import com.mcxiaoke.minicat.service.PostMessageService;
import com.mcxiaoke.minicat.ui.widget.TextChangeListener;
import com.mcxiaoke.minicat.util.Utils;

/**
 * @author mcxiaoke
 * @version 4.0 2012.03.26
 */
public class UIConversation extends UIBaseSupport {

    private static final String TAG = UIConversation.class.getSimpleName();
    private static final int REQUEST_CODE_SELECT_USER = 2001;
    private String userId;
    private String screenName;
    private String profileImageUrl;
    private boolean refresh;
    private EditText mEditText;
    private View btnSend;
    private String text;
    private ConversationFragment fragment;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        parseIntent();
        setLayout();
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

    private void parseIntent() {
        Intent intent = getIntent();
        userId = intent.getStringExtra("id");
        screenName = intent.getStringExtra("screen_name");
        profileImageUrl = intent.getStringExtra("profile_image_url");
        refresh = intent.getBooleanExtra("refresh", false);
    }

    protected void setLayout() {
        setContentView(R.layout.ui_conversation);
        setProgressBarIndeterminateVisibility(false);

        mEditText = (EditText) findViewById(R.id.input);
        mEditText.addTextChangedListener(new TextChangeListener() {

            @Override
            public void onTextChanged(CharSequence s, int start, int before,
                                      int count) {
                text = s.toString().trim();
            }
        });

        btnSend = findViewById(R.id.button_ok);
        btnSend.setOnClickListener(this);

        if (TextUtils.isEmpty(userId)) {
            finish();
        } else {
            setTitle(screenName);
            setFragment();
        }
    }

    private void setFragment() {
        fragment = ConversationFragment.newInstance(userId, screenName, refresh);

        FragmentManager fm = getFragmentManager();
        FragmentTransaction ft = fm.beginTransaction();
        ft.add(R.id.container, fragment);
        ft.commit();

        if (AppContext.DEBUG) {
            Log.d(TAG, "setFragment() userId=" + userId + " screenName="
                    + screenName);
        }
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

    private void doSend(boolean finish) {
        if (TextUtils.isEmpty(text)) {
            Utils.notify(this, "私信内容不能为空");
            return;
        }

        if (TextUtils.isEmpty(userId)) {
            Utils.notify(this, "请选择收件人");
            return;
        }

        PostMessageService.send(mContext, new ResultHandler(), userId, text);
        if (finish) {
            Utils.hideKeyboard(this, mEditText);
            finish();
        } else {
            btnSend.setEnabled(false);
            mEditText.setEnabled(false);

        }
    }

    private void onSendSuccess(Bundle data) {
        mEditText.getEditableText().clear();
        mEditText.setEnabled(true);
        btnSend.setEnabled(true);
    }

    private void onSendError(Bundle data) {
        String errorMessage = data.getString("error_message");
        Utils.notify(this, errorMessage);
        mEditText.setEnabled(true);
        btnSend.setEnabled(true);
    }

    private class ResultHandler extends Handler {

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if (msg.what == PostMessageService.RESULT_SUCCESS) {
                onSendSuccess(msg.getData());
            } else if (msg.what == PostMessageService.RESULT_ERROR) {
                onSendError(msg.getData());
            }
        }
    }

}
