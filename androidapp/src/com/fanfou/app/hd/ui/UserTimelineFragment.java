package com.fanfou.app.hd.ui;

import android.content.Context;
import android.database.Cursor;
import android.os.Bundle;
import android.os.Messenger;
import android.util.Log;

import com.fanfou.app.App;
import com.fanfou.app.db.Contents.StatusInfo;
import com.fanfou.app.db.FanFouProvider;
import com.fanfou.app.service.Constants;
import com.fanfou.app.service.FanFouService;
import com.fanfou.app.util.StringHelper;
import com.fanfou.app.util.Utils;

/**
 * @author mcxiaoke
 * @version 1.0 2012.02.07
 * 
 */
public class UserTimelineFragment extends BaseTimlineFragment {
	private static final String TAG = UserTimelineFragment.class
			.getSimpleName();
	private String userId;

	public static UserTimelineFragment newInstance(String userId) {
		Bundle args = new Bundle();
		args.putString(Constants.EXTRA_ID, userId);
		UserTimelineFragment fragment = new UserTimelineFragment();
		fragment.setArguments(args);
		if (App.DEBUG) {
			Log.d(TAG, "newInstance() "+fragment);
		}
		return fragment;
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
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
	}

	@Override
	protected int getType() {
		return Constants.TYPE_STATUSES_USER_TIMELINE;
	}

	@Override
	protected Cursor createCursor() {
		if (App.DEBUG) {
			Log.d(TAG, "createCursor() userId="+userId);
		}
		String where = StatusInfo.TYPE + " =? AND " + StatusInfo.USER_ID
				+ " =? ";
		String[] whereArgs = new String[] { String.valueOf(getType()), userId};
		return getActivity().managedQuery(StatusInfo.CONTENT_URI, StatusInfo.COLUMNS, where,
				whereArgs, FanFouProvider.ORDERBY_DATE_DESC);
	}

	@Override
	protected void doFetch(boolean doGetMore) {
		if (App.DEBUG) {
			Log.d(TAG, "doFetch() doGetMore=" + doGetMore);
		}
		final ResultHandler handler = new ResultHandler(this);
		final Cursor cursor = getCursor();
		String sinceId = null;
		String maxId = null;
		if (doGetMore) {
			maxId = Utils.getMaxId(cursor);
		} else {
			sinceId = Utils.getSinceId(cursor);
		}
		Context context=getActivity();
		Log.d(TAG, "context="+context);
		FanFouService.doFetchUserTimeline(getActivity(), new Messenger(handler), userId, sinceId, maxId);
	}

}
