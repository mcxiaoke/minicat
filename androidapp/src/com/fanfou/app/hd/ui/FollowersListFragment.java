package com.fanfou.app.hd.ui;

import android.os.Bundle;
import android.util.Log;

import com.fanfou.app.App;
import com.fanfou.app.service.Constants;

/**
 * @author mcxiaoke
 * @version 1.0 2012.02.07
 * @version 1.1 2012.02.08
 *
 */
public class FollowersListFragment extends UserListFragment {
	private static final String TAG=FollowersListFragment.class.getSimpleName();
	
	public static FollowersListFragment newInstance(String userId) {
		Bundle args = new Bundle();
		args.putString(Constants.EXTRA_ID, userId);
		FollowersListFragment fragment = new FollowersListFragment();
		fragment.setArguments(args);
		if (App.DEBUG) {
			Log.d(TAG, "newInstance() "+fragment);
		}
		return fragment;
	}

	@Override
	protected int getType() {
		return Constants.TYPE_USERS_FOLLOWERS;
	}

}
