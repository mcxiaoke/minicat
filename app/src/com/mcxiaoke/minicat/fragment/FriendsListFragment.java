package com.mcxiaoke.minicat.fragment;

import android.os.Bundle;
import android.util.Log;
import com.mcxiaoke.minicat.AppContext;
import com.mcxiaoke.minicat.dao.model.UserModel;


/**
 * @author mcxiaoke
 * @version 1.3 2012.03.08
 */
public class FriendsListFragment extends UserListFragment {
    private static final String TAG = FriendsListFragment.class.getSimpleName();

    public static FriendsListFragment newInstance(String userId) {
        return newInstance(userId, false);
    }

    public static FriendsListFragment newInstance(String userId, boolean refresh) {
        Bundle args = new Bundle();
        args.putString("id", userId);
        args.putBoolean("refresh", refresh);
        FriendsListFragment fragment = new FriendsListFragment();
        fragment.setArguments(args);
        if (AppContext.DEBUG) {
            Log.d(TAG, "newInstance() " + fragment);
        }
        return fragment;
    }


    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
//		getActivity().setTitle("关注的人");
    }

    @Override
    protected int getType() {
        return UserModel.TYPE_FRIENDS;
    }

    @Override
    public String getTitle() {
        return "关注的人";
    }

}
