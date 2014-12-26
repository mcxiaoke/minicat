package com.mcxiaoke.minicat.app;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import com.mcxiaoke.minicat.AppContext;
import com.mcxiaoke.minicat.R;
import com.mcxiaoke.minicat.controller.CacheController;
import com.mcxiaoke.minicat.controller.EmptyViewController;
import com.mcxiaoke.minicat.dao.model.UserModel;
import com.mcxiaoke.minicat.fragment.BaseTimlineFragment;

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

    @Override
    public void onClick(View v) {
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
        setProgressBarIndeterminateVisibility(false);

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

        if (TextUtils.isEmpty(userId)) {
            return true;
        }

        if (user == null) {
            user = CacheController.getUser(userId);
        }

        if (user != null) {
            getActionBar().setTitle(user.getScreenName() + "的" + getTitleSuffix());
        }

        return false;

    }

    protected abstract String getTitleSuffix();

}
