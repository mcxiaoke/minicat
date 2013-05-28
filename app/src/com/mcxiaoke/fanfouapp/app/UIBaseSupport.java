package com.mcxiaoke.fanfouapp.app;

import android.app.ActionBar;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.res.Resources;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import com.mcxiaoke.fanfouapp.R;
import com.mcxiaoke.fanfouapp.controller.UIController;
import com.mcxiaoke.fanfouapp.util.LogUtil;
import com.umeng.analytics.MobclickAgent;

/**
 * @author mcxiaoke
 * @version 4.0 2013.05.07
 */
public abstract class UIBaseSupport extends Activity implements OnClickListener {
    private static final boolean DEBUG = AppContext.DEBUG;
    private static final String TAG = UIBaseSupport.class.getSimpleName();

    private static void debug(String message) {
        LogUtil.d(TAG, message);
    }

    public static final int STATE_INIT = 0;
    public static final int STATE_NORMAL = 1;
    public static final int STATE_EMPTY = 2;

    protected UIBaseSupport mContext;
    protected LayoutInflater mInflater;
    protected Resources mResources;

    protected ActionBar mActionBar;
    protected DisplayMetrics mDisplayMetrics;
    protected boolean mRefreshing;
    protected MenuItem mRefreshMenuItem;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        debug("onCreate()");
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

    protected int getMenuResourceId() {
        return -1;
//        return R.menu.menu_home;
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

    protected void onMenuWriteClick() {
        UIController.showWrite(mContext);
    }

    protected void onMenuHomeClick() {
        finish();
        overridePendingTransition(R.anim.fade_in, R.anim.slide_out_to_right);
    }


    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(R.anim.fade_in, R.anim.slide_out_to_right);
    }

    protected void onMenuRefreshClick() {
        debug("onMenuRefreshClick()");
        startRefresh();
    }

    protected void onMenuSearchClick() {
        onSearchRequested();
    }

    protected void onMenuLogoutClick() {
        final AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("@" + AppContext.getScreenName());
        builder.setMessage("确定注销当前登录帐号吗？");
        builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                AppContext.doLogin(mContext);
                finish();
            }
        });
        builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        builder.create().show();
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
    }

    @Override
    public void onClick(View v) {
    }

    @Override
    public void finish() {
        super.finish();
    }

}
