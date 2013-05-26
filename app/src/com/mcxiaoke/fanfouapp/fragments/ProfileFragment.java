package com.mcxiaoke.fanfouapp.fragments;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;
import com.mcxiaoke.fanfouapp.R;
import com.mcxiaoke.fanfouapp.app.AppContext;
import com.mcxiaoke.fanfouapp.controller.CacheController;
import com.mcxiaoke.fanfouapp.controller.UIController;
import com.mcxiaoke.fanfouapp.dao.model.UserModel;
import com.mcxiaoke.fanfouapp.service.FanFouService;
import com.mcxiaoke.fanfouapp.ui.widget.ProfileView;
import com.mcxiaoke.fanfouapp.util.IntentHelper;
import com.mcxiaoke.fanfouapp.util.LogUtil;
import com.mcxiaoke.fanfouapp.util.Utils;

/**
 * Project: fanfouapp
 * Package: com.mcxiaoke.fanfouapp.fragments
 * User: mcxiaoke
 * Date: 13-5-20
 * Time: 下午10:59
 */
public class ProfileFragment extends AbstractFragment implements ProfileView.ProfileClickListener {
    private static final String TAG = ProfileFragment.class.getSimpleName();

    public static ProfileFragment newInstance(String userId, boolean useMenu) {
        ProfileFragment fragment = new ProfileFragment();
        Bundle args = new Bundle();
        args.putString("id", userId);
        args.putBoolean("menu", useMenu);
        fragment.setArguments(args);
        return fragment;
    }

    private boolean useMenu;
    private String userId;
    private UserModel user;

    private boolean noPermission;
    private ProfileView vProfile;

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
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fm_profile, null, false);
    }


    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        vProfile = (ProfileView) getView().findViewById(R.id.profile);
        vProfile.setProfileClickListener(this);
    }


    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        if (useMenu) {
            setHasOptionsMenu(true);
        }
        refreshProfile();
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.menu_profile, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_write:
                showWrite();
                break;
            case R.id.menu_dm:
                showDM();
                break;
            case R.id.menu_web:
                showWebPage();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onPrepareOptionsMenu(Menu menu) {
        super.onPrepareOptionsMenu(menu);
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
    public void onDestroy() {
        super.onDestroy();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
    }

    @Override
    public void onPause() {
        super.onPause();
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void startRefresh() {
    }

    @Override
    public String getTitle() {
        return "我的资料啊";
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
        } else if (type == ProfileView.TYPE_PHOTOS) {
            onItemPhotosClick();
        } else if (type == ProfileView.TYPE_FOLLOWING) {
            onItemFollowingClick();
        } else if (type == ProfileView.TYPE_FOLLOWERS) {
            onItemFollowersClick();
        } else if (type == ProfileView.TYPE_STATUSES) {
            onItemStatusesClick();
        } else if (type == ProfileView.TYPE_FAVORATIES) {
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
            Toast.makeText(getActivity(), "暂未实现", Toast.LENGTH_SHORT).show();
            //TODO show photos ui
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

    private void showEmptyView(String text) {
        vProfile.setVisibility(View.GONE);
    }

    private void hideProfileHeader() {
        vProfile.setVisibility(View.GONE);
    }

    private void showProfileHeader(UserModel user) {
        this.user = user;
        if (user == null) {
            return;
        }

        if (AppContext.DEBUG) {
            Log.d(TAG, "updateUI() userid=" + userId);
            Log.d(TAG, "updateUI() user.following=" + user.isFollowing());
        }
        vProfile.setContent(user);
        vProfile.setVisibility(View.VISIBLE);
        updateTitle(user);
        updatePermission();

        refreshFollowState();

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
        vProfile.setFollowState(follow);
    }

    private void fetchUser() {
        final Handler handler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                switch (msg.what) {
                    case FanFouService.RESULT_SUCCESS:
                        UserModel result = msg.getData().getParcelable("data");
                        if (AppContext.DEBUG) {
                            Log.d(TAG, "fetchUser result=" + result);
                        }
                        if (result != null) {
                            showProfileHeader(result);
                        }
                        break;
                    case FanFouService.RESULT_ERROR:
                        String errorMessage = msg.getData().getString(
                                "error_message");
                        showEmptyView(errorMessage);
                        break;
                    default:
                        break;
                }
            }
        };

        FanFouService.showUser(getActivity(), userId, handler);
    }

    private void refreshFollowState() {

        final Handler handler = new Handler() {

            @Override
            public void handleMessage(Message msg) {
                switch (msg.what) {
                    case FanFouService.RESULT_SUCCESS:
                        boolean follow = msg.getData().getBoolean("boolean");
                        updateState(follow);
                        break;
                    case FanFouService.RESULT_ERROR:
                        break;
                    default:
                        break;
                }
            }
        };
        FanFouService.showRelation(getActivity(), user.getId(),
                AppContext.getAccount(), handler);
    }

    private void updateFollowButton(boolean following) {
        user.setFollowing(following);
        updatePermission();
        vProfile.updateFollowState(following);
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
                    case FanFouService.RESULT_SUCCESS:
                        if (AppContext.DEBUG) {
                            Log.d(TAG, "follow success");
                        }
                        updateFollowButton(true);
                        Utils.notify(getActivity(), "关注成功");
                        break;
                    case FanFouService.RESULT_ERROR:
                        if (AppContext.DEBUG) {
                            Log.d(TAG, "follow error");
                        }
                        String errorMessage = msg.getData().getString(
                                "error_message");
                        Utils.notify(getActivity(), errorMessage);
                        break;
                    default:
                        break;
                }
            }
        };
        FanFouService.follow(getActivity(), user.getId(), handler);
    }

    private void unfollow() {
        final Handler handler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                switch (msg.what) {
                    case FanFouService.RESULT_SUCCESS:
                        if (AppContext.DEBUG) {
                            Log.d(TAG, "unfollow success");
                        }
                        updateFollowButton(false);
                        Utils.notify(getActivity(), "已取消关注");
                        break;
                    case FanFouService.RESULT_ERROR:
                        if (AppContext.DEBUG) {
                            Log.d(TAG, "unfollow error");
                        }
                        String errorMessage = msg.getData().getString(
                                "error_message");
                        Utils.notify(getActivity(), errorMessage);
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
                FanFouService.unFollow(getActivity(), user.getId(), handler);
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
