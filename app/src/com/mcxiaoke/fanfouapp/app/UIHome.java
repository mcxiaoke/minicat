package com.mcxiaoke.fanfouapp.app;

import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.view.PagerTabStrip;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import com.jeremyfeinstein.slidingmenu.lib.SlidingMenu;
import com.mcxiaoke.fanfouapp.R;
import com.mcxiaoke.fanfouapp.adapter.HomePagesAdapter;
import com.mcxiaoke.fanfouapp.controller.UIController;
import com.mcxiaoke.fanfouapp.fragments.AbstractFragment;
import com.mcxiaoke.fanfouapp.fragments.ConversationListFragment;
import com.mcxiaoke.fanfouapp.fragments.ProfileFragment;
import com.mcxiaoke.fanfouapp.menu.MenuCallback;
import com.mcxiaoke.fanfouapp.menu.MenuFragment;
import com.mcxiaoke.fanfouapp.menu.MenuItemResource;


/**
 * @author mcxiaoke
 */
public class UIHome extends UIBaseSlidingSupport implements MenuCallback,
        OnPageChangeListener {

    public static final String TAG = UIHome.class.getSimpleName();

    private ViewGroup mContainer;
    private Fragment mMenuFragment;

    private ViewPager mViewPager;
    private PagerTabStrip mPagerTabStrip;
    private HomePagesAdapter mPagesAdapter;

    private int mCurrentIndex;
    private int mCurrentPage;

    private void log(String message) {
        Log.d(TAG, message);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (AppContext.DEBUG) {
            log("onCreate()");
        }
        setLayout();
    }

    protected void setLayout() {
        setContentView(R.layout.content_frame);
        setProgressBarIndeterminateVisibility(false);

        mContainer = (ViewGroup) findViewById(R.id.content_frame);

        mViewPager = (ViewPager) findViewById(R.id.viewpager);
        mViewPager.setOnPageChangeListener(this);
        mPagerTabStrip = (PagerTabStrip) findViewById(R.id.viewpager_strip);
        mPagerTabStrip.setDrawFullUnderline(false);
        mPagerTabStrip.setTabIndicatorColor(getResources().getColor(
                R.color.theme_blue_light));
        mPagerTabStrip.setTextColor(Color.WHITE);
//        mPagerTabStrip.setBackgroundColor(Color.DKGRAY);
        mPagesAdapter = new HomePagesAdapter(getFragmentManager());
        mViewPager.setAdapter(mPagesAdapter);
        setHomeTitle(mCurrentPage);
        mCurrentFragment = mPagesAdapter.getItem(mCurrentPage);
        setSlidingMenu(R.layout.menu_frame);
        FragmentManager fm = getFragmentManager();
        mMenuFragment = MenuFragment.newInstance();
        fm.beginTransaction().replace(R.id.menu_frame, mMenuFragment).commit();
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        setIntent(intent);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.menu_write) {
            onMenuWriteClick();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onClick(View v) {
    }

    private AbstractFragment mCurrentFragment;

    private void replaceFragment(AbstractFragment fragment) {
        log("fragment=" + fragment);
        mCurrentFragment = fragment;
        FragmentTransaction ft = getFragmentManager().beginTransaction();
        ft.setTransitionStyle(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
        ft.replace(R.id.content_frame, fragment);
        ft.commit();
        getSlidingMenu().showContent();
    }

    private void showProfileFragment() {
        replaceFragment(ProfileFragment.newInstance(AppContext.getAccount()));
        setTitle("我的空间");
    }

    private void showMessageFragment() {
        replaceFragment(ConversationListFragment.newInstance(false));
        setTitle("收件箱");
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

    @Override
    protected void startRefresh() {
        log("start refresh, current fragment=" + mCurrentFragment);
        if (mCurrentFragment != null) {
            mCurrentFragment.startRefresh();
        } else {
            hideProgressIndicator();
        }
    }

    @Override
    public void onMenuItemSelected(int position, MenuItemResource menuItem) {
        log("onMenuItemSelected: " + menuItem + " position=" + position
                + " mCurrentIndex=" + mCurrentIndex);
        if (position == mCurrentIndex) {
            getSlidingMenu().toggle();
            return;
        }
        int id = menuItem.id;
        switch (id) {
            case MenuFragment.MENU_ID_HOME:
                getFragmentManager().beginTransaction().remove(mCurrentFragment)
                        .commit();
                getSlidingMenu().showContent();
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

    protected int getMenuResourceId() {
        return R.menu.menu_home;
//        return R.menu.menu;
    }

    @Override
    protected void onMenuHomeClick() {
        super.onMenuHomeClick();
    }

    @Override
    protected void onMenuRefreshClick() {
        super.onMenuRefreshClick();
    }

    @Override
    public void onPageScrollStateChanged(int page) {
    }

    @Override
    public void onPageScrolled(int arg0, float arg1, int arg2) {
    }

    @Override
    public void onPageSelected(int page) {
        mCurrentPage = page;
        setHomeTitle(page);
        mCurrentFragment = mPagesAdapter.getItem(mCurrentPage);
        if (page == 0) {
            setTouchModeAbove(SlidingMenu.TOUCHMODE_MARGIN);
        } else {
            setTouchModeAbove(SlidingMenu.TOUCHMODE_NONE);
        }
    }

    private void setHomeTitle(int page) {
        switch (page) {
            case 0:
                setTitle("首页");
                break;
            case 1:
                setTitle("提及");
                break;
            case 2:
                setTitle("随便看看");
                break;
            default:
                break;
        }
    }

}
