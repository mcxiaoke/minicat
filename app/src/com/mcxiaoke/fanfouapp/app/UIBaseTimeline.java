package com.mcxiaoke.fanfouapp.app;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import com.mcxiaoke.fanfouapp.R;
import com.mcxiaoke.fanfouapp.controller.EmptyViewController;
import com.mcxiaoke.fanfouapp.dao.model.UserModel;
import com.mcxiaoke.fanfouapp.fragments.BaseTimlineFragment;

/**
 * @author mcxiaoke
 * @version 5.2 2012.03.19
 */
abstract class UIBaseTimeline extends UIBaseSupport {

    private static final String TAG = UIBaseTimeline.class.getSimpleName();

    private BaseTimlineFragment mFragment;

    private String userId;
    private UserModel user;

    private ViewGroup vContent;
    private ViewGroup vEmpty;
    private EmptyViewController emptyController;

    protected abstract int getType();

    protected abstract BaseTimlineFragment getFragment(String userId);

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (checkIntent()) {
            finish();
            return;
        }
        setLayout();

    }


    private void showEmptyView(String text) {
        vContent.setVisibility(View.GONE);
        emptyController.showEmpty(text);
    }

    private void showProgress() {
        vContent.setVisibility(View.GONE);
        emptyController.showProgress();
        if (AppContext.DEBUG) {
            Log.d(TAG, "showProgress userId=" + userId);
        }
    }

    private void showContent() {
        emptyController.hideProgress();
        vContent.setVisibility(View.VISIBLE);
        if (AppContext.DEBUG) {
            Log.d(TAG, "showContent userId=" + userId);
        }
    }

    protected void setLayout() {
        setContentView(R.layout.ui_container);
        vContent = (ViewGroup) findViewById(R.id.container);
        vEmpty = (ViewGroup) findViewById(android.R.id.empty);
        emptyController = new EmptyViewController(vEmpty);
        setFragment();
    }

    private void setFragment() {
        if (AppContext.DEBUG) {
            Log.d(TAG, "setFragment()");
        }

        mFragment = getFragment(userId);
        android.app.FragmentTransaction transaction = getFragmentManager()
                .beginTransaction();
        transaction.replace(R.id.container, mFragment);
        transaction.commit();
    }

    private boolean checkIntent() {
        Intent intent = getIntent();
        String action = intent.getAction();
        if (action == null) {
            user = intent.getParcelableExtra("data");
            if (user != null) {
                userId = user.getId();
            } else {
                userId = intent.getStringExtra("id");
            }
        } else if (action.equals(Intent.ACTION_VIEW)) {
            Uri data = intent.getData();
            if (data != null) {
                userId = data.getLastPathSegment();
            }
        }

        return TextUtils.isEmpty(userId);

    }

    @Override
    public void onClick(View v) {
    }

}
