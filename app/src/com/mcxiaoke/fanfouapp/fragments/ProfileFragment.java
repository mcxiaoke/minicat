package com.mcxiaoke.fanfouapp.fragments;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.mcxiaoke.fanfouapp.R;
import com.mcxiaoke.fanfouapp.app.AppContext;
import com.mcxiaoke.fanfouapp.controller.CacheController;
import com.mcxiaoke.fanfouapp.controller.SimpleDialogListener;
import com.mcxiaoke.fanfouapp.controller.UIController;
import com.mcxiaoke.fanfouapp.dao.model.UserModel;
import com.mcxiaoke.fanfouapp.dialog.ConfirmDialog;
import com.mcxiaoke.fanfouapp.service.FanFouService;
import com.mcxiaoke.fanfouapp.ui.widget.ProfileView;
import com.mcxiaoke.fanfouapp.util.Utils;

/**
 * Project: fanfouapp
 * Package: com.mcxiaoke.fanfouapp.fragments
 * User: mcxiaoke
 * Date: 13-5-20
 * Time: 下午10:59
 */
public class ProfileFragment extends AbstractFragment {
    private static final String TAG = ProfileFragment.class.getSimpleName();

    public static ProfileFragment newInstance(String userId) {
        ProfileFragment fragment = new ProfileFragment();
        Bundle args = new Bundle();
        args.putString("id", userId);
        fragment.setArguments(args);
        return fragment;
    }

    private String userId;
    private UserModel user;

    private boolean noPermission = false;
    private ProfileView vProfile;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle data = getArguments();
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
    }


    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        refreshProfile();
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
        return null;
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
        if (user != null) {
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

    private void doFollow() {
        if (user == null) {
            return;
        }

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
                        user.setFollowing(true);
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
                        user.setFollowing(false);
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

        final ConfirmDialog dialog = new ConfirmDialog(getActivity());
        dialog.setTitle("提示");
        dialog.setMessage("要取消关注" + user.getScreenName() + "吗？");
        dialog.setClickListener(new SimpleDialogListener() {

            @Override
            public void onPositiveClick() {
                super.onPositiveClick();
                FanFouService.unFollow(getActivity(), user.getId(), handler);
            }
        });
        dialog.show();
    }

    private boolean hasPermission() {
        if (noPermission) {
            Utils.notify(getActivity(), "你没有通过这个用户的验证");
            return false;
        }
        return true;
    }


    private void doSendDirectMessage() {
        UIController.showConversation(getActivity(), user, false);
    }

    private void doRefreshProfile() {
        fetchUser();
    }


}
