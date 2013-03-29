package org.mcxiaoke.fancooker.fragments;

import org.mcxiaoke.fancooker.App;
import org.mcxiaoke.fancooker.dao.model.UserModel;

import android.os.Bundle;
import android.util.Log;


/**
 * @author mcxiaoke
 * @version 1.0 2012.02.07
 * @version 1.1 2012.02.08
 * @version 1.2 2012.02.24
 * @version 1.3 2012.03.08
 * 
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
		if (App.DEBUG) {
			Log.d(TAG, "newInstance() " + fragment);
		}
		return fragment;
	}

	@Override
	protected int getType() {
		return UserModel.TYPE_FRIENDS;
	}

	@Override
	public String getTitle() {
		return "好友 ";
	}

}
