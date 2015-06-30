package com.mcxiaoke.minicat.app;

import android.app.AlertDialog;
import android.app.DownloadManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.design.widget.TabLayout;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.Toolbar;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import butterknife.ButterKnife;
import butterknife.InjectView;
import com.mcxiaoke.minicat.AppContext;
import com.mcxiaoke.minicat.R;
import com.mcxiaoke.minicat.adapter.HomePagesAdapter;
import com.mcxiaoke.minicat.controller.UIController;
import com.mcxiaoke.minicat.fragment.AbstractFragment;
import com.mcxiaoke.minicat.fragment.ConversationListFragment;
import com.mcxiaoke.minicat.fragment.ProfileFragment;
import com.mcxiaoke.minicat.menu.MenuCallback;
import com.mcxiaoke.minicat.menu.MenuFragment;
import com.mcxiaoke.minicat.menu.MenuItemResource;
import com.mcxiaoke.minicat.preference.PreferenceHelper;
import com.mcxiaoke.minicat.push.PushService;
import com.mcxiaoke.minicat.service.AutoCompleteService;
import com.mcxiaoke.minicat.service.Constants;
import com.mcxiaoke.minicat.util.LogUtil;
import com.mcxiaoke.minicat.util.Utils;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.umeng.analytics.MobclickAgent;
import com.umeng.update.UmengUpdateAgent;
import com.umeng.update.UmengUpdateListener;
import com.umeng.update.UpdateResponse;
import org.oauthsimple.utils.MimeUtils;


/**
 * @author mcxiaoke
 */
public class UIHome extends UIBaseSupport implements MenuCallback{

    public static final String TAG = UIHome.class.getSimpleName();
    private static final int UCODE_HAS_UPDATE = 0;
    private static final int UCODE_NO_UPDATE = 1;
    private static final int UCODE_NO_WIFI = 2;
    private static final int UCODE_IO_ERROR = 3;
    private static final long TIME_THREE_DAYS = 1000 * 3600 * 24 * 5L;
    private HomePagesAdapter mPagesAdapter;
    private DownloadManager mDownloadManager;
    private int mCurrentIndex;
    private int mCurrentPage;
    private BroadcastReceiver mReceiver;
    private AbstractFragment mCurrentFragment;

    @InjectView(R.id.drawer_layout)
    DrawerLayout mDrawerLayout;

    @InjectView(R.id.nav_view)
    NavigationView mNavigationView;

    @InjectView(R.id.appbar)
    AppBarLayout mAppBarLayout;


    @InjectView(R.id.toolbar)
    Toolbar mToolbar;

    @InjectView(R.id.tabs)
    TabLayout mTabLayout;

    @InjectView(R.id.viewpager)
    ViewPager mViewPager;

    @InjectView(R.id.fab)
    FloatingActionButton mActionButton;

    private void log(String message) {
        LogUtil.v(TAG, message);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        MobclickAgent.updateOnlineConfig(this);
        if (AppContext.DEBUG) {
            log("onCreate()");
        }
        setLayout();
        setUmengUpdate();
        registerReceiver();
    }

    @Override
    protected void onResume() {
        super.onResume();
//        if (mDrawerLayout.isDrawerOpen(mDrawFrame)) {
//            mDrawerLayout.closeDrawer(Gravity.LEFT);
//        }
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver();
        PushService.check(this);
        AutoCompleteService.check(this);
        ImageLoader.getInstance().clearMemoryCache();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        return super.onCreateOptionsMenu(menu);
        // Get the SearchView and set the searchable configuration
//        SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
//        SearchView searchView = (SearchView) menu.findItem(R.id.menu_search).getActionView();
        // Assumes current activity is the searchable activity
//        searchView.setSearchableInfo(searchManager.getSearchableInfo(getComponentName()));
//        searchView.setIconifiedByDefault(true); // Do not iconify the widget; expand it by default
    }

    private BroadcastReceiver mOnDownloadCompleteReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            LogUtil.v(TAG, "onReceive() intent " + intent.getExtras());
            if (DownloadManager.ACTION_DOWNLOAD_COMPLETE.equals(intent.getAction())) {
                unregisterReceiver(mOnDownloadCompleteReceiver);
                onDownloadComplete(intent);
            }
        }
    };

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
//        boolean drawerOpen = mDrawerLayout.isDrawerOpen(mDrawFrame);
//        menu.findItem(R.id.menu_refresh).setVisible(!drawerOpen);
//        menu.findItem(R.id.menu_write).setVisible(!drawerOpen);
//        if (drawerOpen) {
//            return true;
//        }
//        return super.onPrepareOptionsMenu(menu);
        if (mRefreshMenuItem != null) {
            mRefreshMenuItem.setVisible(false);
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.menu_write) {
            onMenuWriteClick();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    protected int getMenuResourceId() {
        return R.menu.menu_home;
//        return R.menu.menu;
    }

    @Override
    protected void onMenuHomeClick() {
        mDrawerLayout.openDrawer(GravityCompat.START);
//        super.onMenuHomeClick();
    }

    @Override
    protected void onMenuRefreshClick() {
        super.onMenuRefreshClick();
    }

    @Override
    protected void startRefresh() {
        log("check refresh, current fragment=" + mCurrentFragment);
        if (mCurrentFragment != null) {
            mCurrentFragment.startRefresh();
        } else {
            hideProgressIndicator();
        }
    }

    @Override
    public void onClick(View v) {
    }

    private void setUmengUpdate() {
        LogUtil.v(TAG, "setUmengUpdate()");
        mDownloadManager = (DownloadManager) getSystemService(DOWNLOAD_SERVICE);
        boolean autoUpdate = PreferenceHelper.getInstance(this).isAutoUpdate();
        long lastUpdateTime = PreferenceHelper.getInstance(this).getLastUpdateTime();
        long now = System.currentTimeMillis();
        boolean needUpdate = now - lastUpdateTime > TIME_THREE_DAYS;
        if (autoUpdate || needUpdate) {
            PreferenceHelper.getInstance(this).setKeyLastUpdateTime(now);
            UmengUpdateAgent.setUpdateCheckConfig(false);
            UmengUpdateAgent.update(this);
            UmengUpdateAgent.setUpdateOnlyWifi(false);
            UmengUpdateAgent.setUpdateAutoPopup(false);
            UmengUpdateAgent.setUpdateListener(new UmengUpdateListener() {
                @Override
                public void onUpdateReturned(int i, UpdateResponse updateResponse) {
                    LogUtil.v(TAG, "onUpdateReturned() response is " + updateResponse);
                    if (updateResponse != null && UCODE_HAS_UPDATE == i) {
                        showUpdateDialog(updateResponse);
                    }
                }
            });
        }
    }

    private void showUpdateDialog(final UpdateResponse response) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("发现新版本");
        StringBuilder sb = new StringBuilder();
        sb.append("当前版本：").append(AppContext.versionName).append("\n");
        sb.append("最新版本：").append(response.version).append("\n");
        sb.append("\n");
        sb.append("更新日志：\n");
        sb.append(response.updateLog).append("\n");
        builder.setMessage(sb.toString());
        builder.setPositiveButton(R.string.button_update_now, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                startDownloadUpdateApk(response.version, response.path);
                dialog.dismiss();
            }
        });
        builder.setNegativeButton(R.string.button_update_later, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        builder.create().show();

    }

    private void startDownloadUpdateApk(String version, String path) {
        LogUtil.v(TAG, "startDownloadUpdateApk() path " + path);
        String fileName = AppContext.packageName + "_" + version + ".apk";
        IntentFilter filter = new IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE);
        registerReceiver(mOnDownloadCompleteReceiver, filter);
        DownloadManager.Request request = new DownloadManager.Request(Uri.parse(path));
        request.setAllowedNetworkTypes(DownloadManager.Request.NETWORK_MOBILE | DownloadManager.Request.NETWORK_WIFI);
        request.setMimeType(MimeUtils.getMimeTypeFromExtension(".apk"));
        request.setVisibleInDownloadsUi(true);
        request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);
        request.setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS, fileName);
        request.setTitle(getString(R.string.app_name));
        request.setDescription("下载中");
        mDownloadManager.enqueue(request);
    }

    private void onDownloadComplete(Intent intent) {
        long downloadId = intent.getLongExtra(DownloadManager.EXTRA_DOWNLOAD_ID, -1);
        LogUtil.v(TAG, "onDownloadComplete() id is " + downloadId);
        DownloadManager.Query query = new DownloadManager.Query();
        query.setFilterById(downloadId);
        try {
            Cursor cursor = mDownloadManager.query(query);
            if (cursor != null && cursor.moveToFirst()) {
                int downloadStatus = cursor.getInt(cursor.getColumnIndex(DownloadManager.COLUMN_STATUS));
                if (downloadStatus == DownloadManager.STATUS_SUCCESSFUL) {
                    String path = cursor.getString(cursor.getColumnIndex(DownloadManager.COLUMN_LOCAL_FILENAME));
                    String uri = cursor.getString(cursor.getColumnIndex(DownloadManager.COLUMN_LOCAL_URI));
                    LogUtil.v(TAG, "onDownloadComplete() path is " + path);
                    LogUtil.v(TAG, "onDownloadComplete() uri is " + uri);
                    Utils.open(mContext, path);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    protected void setLayout() {
        setContentView(R.layout.act_home);
        setProgressBarIndeterminateVisibility(false);
        ButterKnife.inject(this);
        setSupportActionBar(mToolbar);
        final ActionBar actionBar = getSupportActionBar();
        actionBar.setHomeAsUpIndicator(R.drawable.ic_drawer);
        actionBar.setDisplayHomeAsUpEnabled(true);
        mNavigationView.setNavigationItemSelectedListener(
                new NavigationView.OnNavigationItemSelectedListener() {
                    @Override
                    public boolean onNavigationItemSelected(MenuItem menuItem) {
                        menuItem.setChecked(true);
                        mDrawerLayout.closeDrawers();
                        return true;
                    }
                });

        setTitle(R.string.page_title_home);
        mViewPager = (ViewPager) findViewById(R.id.viewpager);
        mPagesAdapter = new HomePagesAdapter(getSupportFragmentManager(), this);
        mViewPager.setAdapter(mPagesAdapter);
        mTabLayout.setupWithViewPager(mViewPager);

        setHomeTitle(mCurrentPage);
        mCurrentFragment = mPagesAdapter.getItem(mCurrentPage);
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        setIntent(intent);
    }

    private void switchContent(AbstractFragment fragment) {
        log("switchContent fragment=" + fragment);
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        ft.setCustomAnimations(R.anim.fade_in, R.anim.fade_out, R.anim.fade_in, R.anim.fade_out);
//        ft.setTransitionStyle(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
        ft.replace(R.id.content_frame, fragment);
        ft.commit();
//        getSlidingMenu().showContent();
        mCurrentFragment = fragment;
        // hide profile menus
        mCurrentFragment.setMenuVisibility(false);
        mDrawerLayout.closeDrawer(GravityCompat.START);
    }

    private void showProfileFragment() {
        switchContent(ProfileFragment.newInstance(AppContext.getAccount(), false));
        setTitle("我的资料");
    }

    private void showMessageFragment() {
        switchContent(ConversationListFragment.newInstance(false));
        setTitle("收件箱");
    }

    @Override
    public void onMenuItemSelected(int position, MenuItemResource menuItem) {
        log("onMenuItemSelected: " + menuItem + " position=" + position
                + " mCurrentIndex=" + mCurrentIndex);
        if (position == mCurrentIndex) {
            mDrawerLayout.closeDrawer(Gravity.LEFT);
//            getSlidingMenu().toggle();
            return;
        }
        int id = menuItem.id;
        switch (id) {
            case MenuFragment.MENU_ID_HOME:
                getSupportFragmentManager().beginTransaction().remove(mCurrentFragment)
                        .commit();
                mDrawerLayout.closeDrawer(Gravity.LEFT);
//                getSlidingMenu().showContent();
                setHomeTitle(mCurrentPage);
                mCurrentFragment = mPagesAdapter.getItem(mCurrentPage);
                mCurrentIndex = position;
                break;
            case MenuFragment.MENU_ID_PROFILE:
                mCurrentIndex = position;
                showProfileFragment();
                break;
            case MenuFragment.MENU_ID_MESSAGE:
                mCurrentIndex = position;
                showMessageFragment();
                break;
            case MenuFragment.MENU_ID_TOPIC:
                UIController.showTopic(this);
                break;
            case MenuFragment.MENU_ID_RECORD:
                UIController.showRecords(this);
                break;
            case MenuFragment.MENU_ID_DIGEST:
                UIController.showFanfouBlog(this);
                break;
            case MenuFragment.MENU_ID_THEME:
                break;
            case MenuFragment.MENU_ID_OPTION:
                UIController.showOption(this);
                break;
            case MenuFragment.MENU_ID_LOGOUT:
                onMenuLogoutClick();
                break;
            case MenuFragment.MENU_ID_ABOUT:
                UIController.showAbout(this);
                break;
            case MenuFragment.MENU_ID_DEBUG:
                UIController.showDebug(this);
                break;
            default:
                break;
        }
    }

    public void onPageSelected(int position) {
        mCurrentPage = position;
        setHomeTitle(position);
        mCurrentFragment = mPagesAdapter.getItem(mCurrentPage);
    }

    private void setHomeTitle(int page) {
        switch (page) {
            case 0:
                setTitle("主页");
                break;
            case 1:
                setTitle("提到我的");
                break;
            case 2:
                setTitle("随便看看");
                break;
            default:
                break;
        }
    }

    private void registerReceiver() {
        if (mReceiver == null) {
            final IntentFilter filter = new IntentFilter();
            filter.addAction(Constants.ACTION_STATUS_SENT);
            mReceiver = new BroadcastReceiver() {
                @Override
                public void onReceive(final Context context, final Intent intent) {
                    if (Constants.ACTION_STATUS_SENT.equals(intent.getAction())) {
                        if (mCurrentFragment != null) {
                            mCurrentFragment.startRefresh();
                        }
                    }
                }
            };
            registerReceiver(mReceiver, filter);
        }
    }

    private void unregisterReceiver() {
        if (mReceiver != null) {
            unregisterReceiver(mReceiver);
        }
    }


}
