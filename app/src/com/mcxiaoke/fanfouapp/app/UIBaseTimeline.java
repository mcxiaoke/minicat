package com.mcxiaoke.fanfouapp.app;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import com.mcxiaoke.fanfouapp.fragments.BaseTimlineFragment;
import com.mcxiaoke.fanfouapp.ui.widget.GestureManager.SwipeListener;
import com.mcxiaoke.fanfouapp.R;

/**
 * @author mcxiaoke
 * @version 5.2 2012.03.19
 */
abstract class UIBaseTimeline extends UIBaseSupport implements SwipeListener {

    private static final String TAG = UIBaseTimeline.class.getSimpleName();

    private BaseTimlineFragment mFragment;

    private String userId;

    protected abstract int getType();

    protected abstract BaseTimlineFragment getFragment(String userId);

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        parseIntent();
        setLayout();

    }

    protected void setLayout() {
        if (userId != null) {
            setTitle("@" + userId);
        } else {
            setTitle("时间线");
        }
        setContentView(R.layout.ui_container);
        setFragment();
    }

    private void setFragment() {
        if (AppContext.DEBUG) {
            Log.d(TAG, "setFragment()");
        }

        mFragment = getFragment(userId);
        android.app.FragmentTransaction transaction = getFragmentManager()
                .beginTransaction();
        transaction.add(R.id.container, mFragment);
        transaction.commit();
    }

    private void parseIntent() {
        Intent intent = getIntent();
        String action = intent.getAction();
        if (action.equals(Intent.ACTION_VIEW)) {
            Uri data = intent.getData();
            if (data != null) {
                userId = data.getLastPathSegment();
            }
        } else {
            userId = intent.getStringExtra("id");

        }
        if (TextUtils.isEmpty(userId)) {
            userId = AppContext.getAccount();
        }

    }

    @Override
    public void onClick(View v) {
    }

    @Override
    public boolean onSwipeLeft() {
        finish();
        return true;
    }

    @Override
    public boolean onSwipeRight() {
        return true;
    }

}
