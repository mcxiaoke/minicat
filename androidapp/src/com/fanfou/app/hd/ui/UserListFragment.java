package com.fanfou.app.hd.ui;

import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v4.widget.CursorAdapter;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;

import com.fanfou.app.App;
import com.fanfou.app.adapter.UserCursorAdapter;
import com.fanfou.app.api.User;
import com.fanfou.app.db.Contents.UserInfo;
import com.fanfou.app.service.Constants;
import com.fanfou.app.service.FanFouService;
import com.fanfou.app.ui.ActionManager;
import com.fanfou.app.util.StringHelper;

/**
 * @author mcxiaoke
 * @version 1.0 2012.02.07
 * @version 1.1 2012.02.08
 * 
 */
public abstract class UserListFragment extends PullToRefreshListFragment {
	private static final String TAG = UserListFragment.class.getSimpleName();

	private int page = 1;
	private String userId;

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position,
			long id) {
		final Cursor c = (Cursor) parent.getItemAtPosition(position);
		final User u = User.parse(c);
		if (u != null) {
			if (App.DEBUG)
				Log.d(TAG, "userId=" + u.id + " username=" + u.screenName);
			ActionManager.doProfile(getActivity(), u);
		}
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Bundle data=getArguments();
		if(data!=null){
			userId=data.getString(Constants.EXTRA_ID);
		}
		if(StringHelper.isEmpty(userId)){
			userId=App.getUserId();
		}
		
		if (App.DEBUG) {
			Log.d(TAG, "onCreate() userId="+userId);
		}
	}

	@Override
	protected CursorAdapter onCreateAdapter() {
		return new UserCursorAdapter(getActivity(), getCursor());
	}

	@Override
	protected Cursor onCreateCursor() {
		String where = UserInfo.TYPE + "=? AND " + UserInfo.OWNER_ID + "=?";
		String[] whereArgs = new String[] { String.valueOf(getType()), userId };
		return getActivity().managedQuery(UserInfo.CONTENT_URI,
				UserInfo.COLUMNS, where, whereArgs, null);
	}

	@Override
	protected void doFetch(boolean doGetMore) {
		if (doGetMore) {
			page++;
		} else {
			page = 1;
		}
		final ResultHandler handler = new ResultHandler(this);
		if (getType() == Constants.TYPE_USERS_FRIENDS) {
			FanFouService.doFetchFriends(getActivity(), handler, page, userId);
		} else {
			FanFouService
					.doFetchFollowers(getActivity(), handler, page, userId);
		}
	}

	@Override
	protected void showToast(int count) {
	}
	
	@Override
	public Loader<Cursor> onCreateLoader(int id, Bundle args) {
		Uri uri = UserInfo.CONTENT_URI;
		String where = UserInfo.TYPE + "=? AND " + UserInfo.OWNER_ID + "=?";
		String[] whereArgs = new String[] { String.valueOf(getType()), userId };
		CursorLoader loader=new CursorLoader(getActivity(), uri, null, where, whereArgs, null);
		if(App.DEBUG){
			Log.d(TAG, "onCreateLoader() uri=["+uri+"] where=["+where+"] whereArgs=["+whereArgs+"]");
		}
		return loader;
	}

}
