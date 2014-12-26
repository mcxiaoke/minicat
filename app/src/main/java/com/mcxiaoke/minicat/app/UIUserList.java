package com.mcxiaoke.minicat.app;

import android.app.FragmentTransaction;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import com.mcxiaoke.minicat.AppContext;
import com.mcxiaoke.minicat.R;
import com.mcxiaoke.minicat.dao.model.UserModel;
import com.mcxiaoke.minicat.fragment.FollowersListFragment;
import com.mcxiaoke.minicat.fragment.FriendsListFragment;
import com.mcxiaoke.minicat.fragment.OnInitCompleteListener;
import com.mcxiaoke.minicat.fragment.UserListFragment;
import com.mcxiaoke.minicat.ui.widget.TextChangeListener;

/**
 * @author mcxiaoke
 * @version 4.1 2012.03.26
 */
public class UIUserList extends UIBaseSupport implements OnInitCompleteListener {
    private static final String TAG = UIUserList.class.getSimpleName();
    private static final String tag = UIUserList.class.getSimpleName();

//    private EditText mEditText;
    private UserListFragment mFragment;
    private String userId;
    private String screenName;
    private int type;

    private void log(String message) {
        Log.d(tag, message);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (!checkIntent()) {
            finish();
        }
        setLayout();
    }

    protected void setLayout() {
        setContentView(R.layout.ui_users);
        setProgressBarIndeterminateVisibility(false);

        setActionBarTitle();
//        mEditText = (EditText) findViewById(R.id.input);
//        mEditText.addTextChangedListener(new MyTextWatcher(this));
        setFragment();

    }

    private void setActionBarTitle() {
        if (screenName != null) {
            StringBuilder builder = new StringBuilder();
            if (type == UserModel.TYPE_FOLLOWERS) {
                builder.append("关注");
                builder.append(screenName);
                builder.append("的人");
            } else {
                builder.append(screenName);
                builder.append("关注的人");
            }
            getActionBar().setTitle(builder.toString());
        }
    }

    private void filter(String text) {
        if (AppContext.DEBUG) {
            Log.d(TAG, "filter() text=" + text);
        }
        mFragment.filter(text);
    }

    private void showSearchBox() {
//        mEditText.setVisibility(View.VISIBLE);
    }

    private void setFragment() {
        if (AppContext.DEBUG) {
            Log.d(TAG, "setFragment()");
        }
        if (type == UserModel.TYPE_FRIENDS) {
            mFragment = FriendsListFragment.newInstance(userId, false);
        } else {
            mFragment = FollowersListFragment.newInstance(userId, false);
        }

//        mFragment.setOnInitCompleteListener(this);

        FragmentTransaction transaction = getFragmentManager()
                .beginTransaction();
        transaction.replace(R.id.container, mFragment);
        transaction.commit();
    }

    private boolean checkIntent() {
        Intent intent = getIntent();
        type = intent.getIntExtra("type", UserModel.TYPE_FRIENDS);
        userId = intent.getStringExtra("id");
        screenName = intent.getStringExtra("name");
        return !TextUtils.isEmpty(userId);
    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    @Override
    public void onInitComplete(Bundle data) {
//        showSearchBox();
    }

    private static class MyTextWatcher extends TextChangeListener {
        private UIUserList mUiUserList;

        public MyTextWatcher(UIUserList ui) {
            this.mUiUserList = ui;
        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before,
                                  int count) {
            if (AppContext.DEBUG) {
                Log.d(TAG, "onTextChanged() text=" + s);
            }
            mUiUserList.filter(s.toString().trim());
        }
    }

}
