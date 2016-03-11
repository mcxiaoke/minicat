package com.mcxiaoke.minicat.app;

import android.app.ActionBar;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.support.v4.app.TaskStackBuilder;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import com.mcxiaoke.minicat.AppContext;
import com.mcxiaoke.minicat.R;
import com.mcxiaoke.minicat.controller.UIController;
import com.mcxiaoke.minicat.util.LogUtil;
import com.umeng.analytics.MobclickAgent;

/**
 * @author mcxiaoke
 * @version 4.0 2013.05.07
 */
public abstract class UIBaseSupport extends Activity implements OnClickListener {
    public static final int STATE_INIT = 0;
    public static final int STATE_NORMAL = 1;
    public static final int STATE_EMPTY = 2;
    private static final boolean DEBUG = AppContext.DEBUG;
    private static final String TAG = UIBaseSupport.class.getSimpleName();
    protected UIBaseSupport mContext;
    protected LayoutInflater mInflater;
    protected Resources mResources;
    protected ActionBar mActionBar;
    protected DisplayMetrics mDisplayMetrics;
    protected boolean mRefreshing;
    protected MenuItem mRefreshMenuItem;

    private static void debug(String message) {
        LogUtil.v(TAG, message);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        debug("onCreate() "+this);
        requestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS);
        // 这句话必须在setContentView调用之后才有效
        //setProgressBarIndeterminateVisibility(false);
        AppContext.setActiveContext(this);
        this.mContext = this;
        this.mInflater = LayoutInflater.from(this);
        this.mResources = getResources();
        this.mActionBar = getActionBar();
        this.mActionBar.setDisplayHomeAsUpEnabled(true);
        this.mActionBar.setDisplayUseLogoEnabled(true);
        this.mDisplayMetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(mDisplayMetrics);
    }

    @Override
    protected void onResume() {
        super.onResume();
        MobclickAgent.onResume(this);
        debug("onResume()");
        AppContext.active = true;
    }

    @Override
    protected void onPause() {
        super.onPause();
        MobclickAgent.onPause(this);
        AppContext.active = false;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        debug("onDestroy() " + this);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
//        overridePendingTransition(R.anim.fade_in, R.anim.slide_out_to_right);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        debug("onCreateOptionsMenu() mRefreshing=" + mRefreshing);
        int id = getMenuResourceId();
        if (id > 0) {
            getMenuInflater().inflate(id, menu);
            mRefreshMenuItem = menu.findItem(R.id.menu_refresh);
        }
        return true;
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        debug("onPrepareOptionsMenu() mRefreshing=" + mRefreshing);
        return super.onPrepareOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        debug("onOptionsItemSelected() item=" + item);
        switch (item.getItemId()) {
            case android.R.id.home:
                onMenuHomeClick();
                return true;
            case R.id.menu_refresh:
                onMenuRefreshClick();
                return true;
//            case R.id.menu_write:
//                onMenuWriteClick();
//                return true;
//            case R.id.menu_search:
//                onMenuSearchClick();
//                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void finish() {
        super.finish();
    }

    protected int getMenuResourceId() {
        return -1;
//        return R.menu.menu_home;
    }

    protected void onMenuWriteClick() {
        UIController.showWrite(mContext);
    }

    protected void onMenuHomeClick() {
        if (isFinishing()) {
            return;
        }
        Intent upIntent = NavUtils.getParentActivityIntent(this);
        if (upIntent != null) {
            if (NavUtils.shouldUpRecreateTask(this, upIntent)) {
                // This activity is NOT part of this app's task, so create a new task
                // when navigating up, with a synthesized back stack.
                TaskStackBuilder.create(this)
                        // Add all of this activity's parents to the back stack
                        .addNextIntentWithParentStack(upIntent)
                                // Navigate up to the closest parent
                        .startActivities();
            } else {
                // This activity is part of this app's task, so simply
                // navigate up to the logical parent activity.
                NavUtils.navigateUpTo(this, upIntent);
            }
        } else {
            finish();
        }
//        overridePendingTransition(R.anim.fade_in, R.anim.slide_out_to_right);
    }

    protected void onMenuRefreshClick() {
        debug("onMenuRefreshClick()");
        startRefresh();
    }

    protected void onMenuSearchClick() {
//        onSearchRequested();
        startSearchUI();
    }

    private void startSearchUI() {
        UIController.showSearchResults(this, null);
    }

    protected void startRefresh() {
        debug("startRefresh()");
    }

    public void showProgressIndicator() {
        debug("showProgressIndicator() mRefreshing=" + mRefreshing);
        if (!mRefreshing) {
            mRefreshing = true;
            setProgressBarIndeterminateVisibility(true);
            invalidateOptionsMenu();
        }
    }

    public void hideProgressIndicator() {
        debug("hideProgressIndicator() mRefreshing=" + mRefreshing);
        if (mRefreshing) {
            mRefreshing = false;
            setProgressBarIndeterminateVisibility(false);
            invalidateOptionsMenu();
        }
    }

    @Override
    public void onClick(View v) {
    }

}
