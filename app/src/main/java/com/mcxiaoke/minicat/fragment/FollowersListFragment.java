package com.mcxiaoke.minicat.fragment;

import android.os.Bundle;
import android.util.Log;
import com.mcxiaoke.minicat.AppContext;
import com.mcxiaoke.minicat.dao.model.UserModel;


/**
 * @author mcxiaoke
 * @version 1.3 2012.03.08
 */
public class FollowersListFragment extends UserListFragment {
    private static final String TAG = FollowersListFragment.class
            .getSimpleName();

    public static FollowersListFragment newInstance(String userId) {
        return newInstance(userId, false);
    }

    public static FollowersListFragment newInstance(String userId,
                                                    boolean refresh) {
        Bundle args = new Bundle();
        args.putString("id", userId);
        args.putBoolean("refresh", refresh);
        FollowersListFragment fragment = new FollowersListFragment();
        fragment.setArguments(args);
        if (AppContext.DEBUG) {
            Log.d(TAG, "newInstance() " + fragment);
        }
        return fragment;
    }


    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
//		getActivity().setTitle("关注者");
    }

    @Override
    protected int getType() {
        return UserModel.TYPE_FOLLOWERS;
    }

    @Override
    public String getTitle() {
        return "关注者";
    }

}
