package com.mcxiaoke.minicat.fragment;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.view.ViewCompat;
import android.support.v4.widget.SwipeRefreshLayout.OnRefreshListener;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ScrollView;
import butterknife.ButterKnife;
import butterknife.InjectView;
import com.mcxiaoke.minicat.AppContext;
import com.mcxiaoke.minicat.R;
import com.mcxiaoke.minicat.controller.CacheController;
import com.mcxiaoke.minicat.controller.UIController;
import com.mcxiaoke.minicat.dao.model.UserModel;
import com.mcxiaoke.minicat.service.SyncService;
import com.mcxiaoke.minicat.ui.widget.ProfileView;
import com.mcxiaoke.minicat.ui.widget.SwipeRefreshLayoutEx;
import com.mcxiaoke.minicat.ui.widget.SwipeRefreshLayoutEx.CanChildScrollUpCallback;
import com.mcxiaoke.minicat.util.IntentHelper;
import com.mcxiaoke.minicat.util.LogUtil;
import com.mcxiaoke.minicat.util.Utils;

/**
 * Project: fanfouapp
 * Package: com.mcxiaoke.fanfouapp.fragment
 * User: mcxiaoke
 * Date: 13-5-20
 * Time: 下午10:59
 */
public class ProfileFragment extends AbstractFragment
        implements ProfileView.ProfileClickListener {
    private static final String TAG = ProfileFragment.class.getSimpleName();
    @InjectView(R.id.root)
    SwipeRefreshLayoutEx mSwipeRefreshLayout;
    @InjectView(R.id.container)
    ScrollView mScrollView;
    @InjectView(R.id.profile)
    ProfileView mProfileView;
    private boolean useMenu;
    private String userId;
    private UserModel user;
    private boolean noPermission;
    private MenuItem followMemu;
    private MenuItem unfollowMenu;

    public static ProfileFragment newInstance(String userId, boolean useMenu) {
        ProfileFragment fragment = new ProfileFragment();
        Bundle args = new Bundle();
        args.putString("id", userId);
        args.putBoolean("menu", useMenu);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle data = getArguments();
        useMenu = data.getBoolean("menu");
        user = data.getParcelable("data");
        if (user == null) {
            userId = data.getString("id");
        } else {
            userId = user.getId();
        }
        if (useMenu) {
            setHasOptionsMenu(true);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.fm_profile, container, false);
        ButterKnife.inject(this, view);
        return view;
    }


    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mSwipeRefreshLayout.setCanChildScrollUpCallback(new CanChildScrollUpCallback() {
            @Override
            public boolean canSwipeRefreshChildScrollUp() {
                return ViewCompat.canScrollVertically(mScrollView, -1);
            }
        });
        mSwipeRefreshLayout.setColorSchemeResources(
                R.color.color1,
                R.color.color2,
                R.color.color3,
                R.color.color4);
        mProfileView.setProfileClickListener(this);
    }


    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mSwipeRefreshLayout.setOnRefreshListener(new OnRefreshListener() {
            @Override
            public void onRefresh() {
                forceRefreshProfile();
            }
        });
        refreshProfile();
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.menu_profile, menu);
        followMemu = menu.findItem(R.id.menu_follow);
        unfollowMenu = menu.findItem(R.id.menu_unfollow);
    }

    @Override
    public void onPrepareOptionsMenu(Menu menu) {
        super.onPrepareOptionsMenu(menu);
        checkMenuAction(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_write:
                showWrite();
                break;
            case R.id.menu_follow:
                follow();
                break;
            case R.id.menu_unfollow:
                unfollow();
                break;
            case R.id.menu_dm:
                showDM();
                break;
            case R.id.menu_web:
                showWebPage();
                break;
        }
        return true;
    }

    private void checkMenuAction(Menu menu) {
        if (user != null) {
            boolean following = user.isFollowing();
            LogUtil.v(TAG, "checkMenuAction() following=" + following);
            if (followMemu != null) {
                followMemu.setVisible(!following);
            }

            if (unfollowMenu != null) {
                unfollowMenu.setVisible(following);
            }
        } else {
            if (followMemu != null) {
                followMemu.setVisible(false);
            }

            if (unfollowMenu != null) {
                unfollowMenu.setVisible(false);
            }
        }
    }

    private void showWrite() {
        if (user != null && getActivity() != null) {
            UIController.showWrite(getActivity(), "@" + user.getScreenName() + " ");
        }
    }

    private void showDM() {
        if (user != null && getActivity() != null) {
            UIController.showConversation(getActivity(), user, false);
        }
    }

    private void showWebPage() {
        if (user != null && getActivity() != null) {
            String url = "http://fanfou.com/" + user.getId();
            IntentHelper.startWebIntent(getActivity(), url);
        }
    }

    @Override
    public String getTitle() {
        return "我的资料啊";
    }

    @Override
    public void startRefresh() {
    }

    @Override
    public void onProfileItemClick(int type) {
        if (type == ProfileView.TYPE_TOP_FOLLOWING) {
            onTopFollowingClick();
        } else if (type == ProfileView.TYPE_TOP_FOLLOWERS) {
            onTopFollowersClick();
        } else if (type == ProfileView.TYPE_TOP_STATUSES) {
            onTopStatusesClick();
        } else if (type == ProfileView.TYPE_FOLLOW_STATE) {
            onFollowStateClick();
        } else if (type == ProfileView.TYPE_ALBUM) {
            onItemPhotosClick();
        } else if (type == ProfileView.TYPE_FOLLOWING) {
            onItemFollowingClick();
        } else if (type == ProfileView.TYPE_FOLLOWERS) {
            onItemFollowersClick();
        } else if (type == ProfileView.TYPE_STATUSES) {
            onItemStatusesClick();
        } else if (type == ProfileView.TYPE_TOP_FAVORATIES) {
            onItemFavoratiesClick();
        }
    }


    private void onTopFollowingClick() {
        if (hasPermission()) {
            UIController.showFollowing(getActivity(), user.getId(), user.getScreenName());
        }
    }


    private void onTopFollowersClick() {
        if (hasPermission()) {
            UIController.showFollowers(getActivity(), user.getId(), user.getScreenName());
        }
    }


    private void onTopStatusesClick() {
        if (hasPermission()) {
            UIController.showTimeline(getActivity(), user);
        }
    }

    private void onFollowStateClick() {
        doFollow();
    }


    private void onItemPhotosClick() {
        if (hasPermission()) {
            UIController.showAlbum(getActivity(), user);
        }
    }

    private void onItemFollowingClick() {
        onTopFollowingClick();
    }

    private void onItemFollowersClick() {
        onTopFollowersClick();
    }

    private void onItemFavoratiesClick() {
        if (hasPermission()) {
            UIController.showFavorites(getActivity(), user);
        }
    }

    private void onItemStatusesClick() {
        onTopStatusesClick();
    }

    private void refreshProfile() {
        if (user == null) {
            user = CacheController.getUserAndCache(userId, getActivity());
        }
        if (user == null) {
            fetchUser();
            hideProfileHeader();
        } else {
            showProfileHeader(user);
        }
    }

    private void forceRefreshProfile() {
        fetchUser();
    }

    private void showEmptyView(String text) {
        mProfileView.setVisibility(View.GONE);
    }

    private void hideProfileHeader() {
        mProfileView.setVisibility(View.GONE);
    }

    private void showProfileHeader(UserModel user) {
        this.user = user;
        if (user == null) {
            return;
        }

        this.userId = user.getId();

        if (AppContext.DEBUG) {
            Log.d(TAG, "updateUI() userid=" + userId);
            Log.d(TAG, "updateUI() user.following=" + user.isFollowing());
        }
        mProfileView.setContent(user);
        mProfileView.setVisibility(View.VISIBLE);
        updateTitle(user);

        getBaseSupport().invalidateOptionsMenu();

        if (userId.equals(AppContext.getAccount())) {
            mProfileView.hideFollowState();
        } else {
            updatePermission();
            refreshFollowState();
        }

        if (AppContext.DEBUG) {
            Log.d(TAG, "showProfileHeader userId=" + userId);
        }
    }

    private void updateTitle(UserModel user) {
        if (user != null && useMenu) {
            Activity activity = getActivity();
            if (activity != null) {
                activity.getActionBar().setTitle("@" + user.getScreenName());
            }
        }
    }

    private void updatePermission() {
        if (user.getId().equals(AppContext.getAccount())) {
            noPermission = false;
            return;
        }
        noPermission = user.isProtect() && !user.isFollowing();
    }

    private void updateState(boolean follow) {
        mProfileView.setFollowState(follow);
    }

    protected void showRefreshIndicator(final boolean show) {
        if (mSwipeRefreshLayout != null) {
            mSwipeRefreshLayout.setRefreshing(show);
        }
    }

    private void fetchUser() {
        final Handler handler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                switch (msg.what) {
                    case SyncService.RESULT_SUCCESS:
                        showRefreshIndicator(false);
                        UserModel result = msg.getData().getParcelable("data");
                        if (AppContext.DEBUG) {
                            Log.d(TAG, "fetchUser result=" + result);
                        }
                        if (result != null) {
                            showProfileHeader(result);
                        }
                        break;
                    case SyncService.RESULT_ERROR:
                        showRefreshIndicator(false);
                        String errorMessage = msg.getData().getString(
                                "error_message");
                        showEmptyView(errorMessage);
                        break;
                    default:
                        break;
                }
            }
        };

        SyncService.showUser(getActivity(), userId, handler);
    }

    private void refreshFollowState() {

        final Handler handler = new Handler() {

            @Override
            public void handleMessage(Message msg) {
                switch (msg.what) {
                    case SyncService.RESULT_SUCCESS:
                        boolean follow = msg.getData().getBoolean("boolean");
                        updateState(follow);
                        break;
                    case SyncService.RESULT_ERROR:
                        break;
                    default:
                        break;
                }
            }
        };
        SyncService.showRelation(getActivity(), user.getId(),
                AppContext.getAccount(), handler);
    }

    private void updateFollowButton(boolean following) {
        if (getBaseSupport() != null) {
            user.setFollowing(following);
            following = user.isFollowing();

            LogUtil.v(TAG, "updateFollowButton following=" + following);
            LogUtil.v(TAG, "updateFollowButton user.isFollowing()=" + user.isFollowing());
            updatePermission();
            mProfileView.updateFollowState(following);
            getBaseSupport().invalidateOptionsMenu();
        }
    }

    private void doFollow() {
        if (user == null) {
            return;
        }

        LogUtil.v(TAG, "doFollow following=" + user.isFollowing());

        if (user.isFollowing()) {
            unfollow();
        } else {
            follow();
        }

    }

    private void follow() {
        final Handler handler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                switch (msg.what) {
                    case SyncService.RESULT_SUCCESS:
                        if (AppContext.DEBUG) {
                            Log.d(TAG, "follow success");
                        }
                        updateFollowButton(true);
                        Utils.notify(AppContext.getApp(), "关注成功");
                        break;
                    case SyncService.RESULT_ERROR:
                        if (AppContext.DEBUG) {
                            Log.d(TAG, "follow error");
                        }
                        String errorMessage = msg.getData().getString(
                                "error_message");
                        Utils.notify(AppContext.getApp(), errorMessage);
                        break;
                    default:
                        break;
                }
            }
        };
        SyncService.follow(getActivity(), user.getId(), handler);
    }

    private void unfollow() {
        final Handler handler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                switch (msg.what) {
                    case SyncService.RESULT_SUCCESS:
                        if (AppContext.DEBUG) {
                            Log.d(TAG, "unfollow success");
                        }
                        updateFollowButton(false);
                        Utils.notify(AppContext.getApp(), "已取消关注");
                        break;
                    case SyncService.RESULT_ERROR:
                        if (AppContext.DEBUG) {
                            Log.d(TAG, "unfollow error");
                        }
                        String errorMessage = msg.getData().getString(
                                "error_message");
                        Utils.notify(AppContext.getApp(), errorMessage);
                        break;
                    default:
                        break;
                }
            }
        };

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setCancelable(true);
        builder.setTitle("取消关注");
        builder.setMessage("要取消关注" + user.getScreenName() + "吗？");
        builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                SyncService.unFollow(getActivity(), user.getId(), handler);
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

    private boolean hasPermission() {
        if (noPermission) {
            Utils.notify(getActivity(), "你没有通过这个用户的验证");
            return false;
        }
        return true;
    }

}
