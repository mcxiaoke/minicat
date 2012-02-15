package com.fanfou.app.hd.fragments;

import android.os.Bundle;
import android.util.Log;

import com.fanfou.app.hd.App;
import com.fanfou.app.hd.service.Constants;

/**
 * @author mcxiaoke
 * @version 1.0 2012.02.07
 * @version 1.1 2012.02.08
 *
 */
public class FriendsListFragment extends UserListFragment {
	private static final String TAG=FriendsListFragment.class.getSimpleName();
	
	public static FriendsListFragment newInstance(String userId) {
		Bundle args = new Bundle();
		args.putString(Constants.EXTRA_ID, userId);
		FriendsListFragment fragment = new FriendsListFragment();
		fragment.setArguments(args);
		if (App.DEBUG) {
			Log.d(TAG, "newInstance() "+fragment);
		}
		return fragment;
	}

	@Override
	protected int getType() {
		return Constants.TYPE_USERS_FRIENDS;
	}

}
