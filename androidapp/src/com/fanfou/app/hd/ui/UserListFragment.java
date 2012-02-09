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
import android.widget.FilterQueryProvider;

import com.fanfou.app.hd.App;
import com.fanfou.app.hd.adapter.UserCursorAdapter;
import com.fanfou.app.hd.api.User;
import com.fanfou.app.hd.db.Contents.BasicColumns;
import com.fanfou.app.hd.db.Contents.UserInfo;
import com.fanfou.app.hd.service.Constants;
import com.fanfou.app.hd.service.FanFouService;
import com.fanfou.app.hd.ui.widget.ActionManager;
import com.fanfou.app.hd.util.StringHelper;

/**
 * @author mcxiaoke
 * @version 1.0 2012.02.07
 * @version 1.1 2012.02.08
 * @version 1.2 2012.02.09
 * 
 */
public abstract class UserListFragment extends PullToRefreshListFragment implements FilterQueryProvider{

	private static final String TAG = UserListFragment.class.getSimpleName();

	private int page = 1;
	private String userId;
	
	private OnInitCompleteListener mListener;
	
	public void setOnInitCompleteListener(OnInitCompleteListener listener){
		this.mListener=listener;
	}
	
	private void onInitComplete(){
		if(mListener!=null){
			mListener.onInitComplete(null);
		}
	}

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position,
			long id) {
		final Cursor c = (Cursor) parent.getItemAtPosition(position);
		final User u = User.parse(c);
		if (u != null) {
			if (App.DEBUG){		
				Log.d(TAG, "userId=" + u.id + " username=" + u.screenName);
			}
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
	
	@Override
	public void onLoadFinished(Loader<Cursor> loader, Cursor newCursor) {
		super.onLoadFinished(loader, newCursor);
		getAdapter().setFilterQueryProvider(this);
		onInitComplete();
	}

	@Override
	public Cursor runQuery(CharSequence constraint) {
		if(App.DEBUG){
			Log.d(TAG, "runQuery() constraint="+constraint);
		}
		String where = UserInfo.TYPE + " = " + getType() + " AND "
				+ UserInfo.OWNER_ID + " = '" + userId + "' AND ("
				+ UserInfo.SCREEN_NAME + " like '%" + constraint + "%' OR "
				+ UserInfo.ID + " like '%" + constraint + "%' )";
		;
		return getActivity().managedQuery(UserInfo.CONTENT_URI, UserInfo.COLUMNS, where,
				null, null);
	}

}
