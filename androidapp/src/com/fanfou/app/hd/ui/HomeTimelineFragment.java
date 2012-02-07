package com.fanfou.app.hd.ui;

import android.content.Context;
import android.database.Cursor;
import android.os.Bundle;
import android.os.Messenger;
import android.util.Log;

import com.fanfou.app.App;
import com.fanfou.app.service.Constants;
import com.fanfou.app.service.FanFouService;
import com.fanfou.app.util.Utils;

/**
 * @author mcxiaoke
 * @version 1.0 2012.02.06
 * 
 */
public class HomeTimelineFragment extends BaseTimlineFragment {
	private static final String TAG = HomeTimelineFragment.class
			.getSimpleName();

	public static HomeTimelineFragment newInstance(int type) {
		Bundle args = new Bundle();
		args.putInt(Constants.EXTRA_TYPE, type);
		HomeTimelineFragment fragment = new HomeTimelineFragment();
		fragment.setArguments(args);
		if (App.DEBUG) {
			Log.d(TAG, "newInstance() "+fragment);
		}
		return fragment;
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		if (getAdapter().isEmpty()) {
			startRefresh();
		}
	}

	@Override
	protected int getType() {
		return Constants.TYPE_STATUSES_HOME_TIMELINE;
	}

	@Override
	protected void doFetch(boolean doGetMore) {
		if (App.DEBUG) {
			Log.d(TAG, "doFetch() doGetMore=" + doGetMore+" id="+this+"activity ="+getActivity());
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
		FanFouService.doFetchHomeTimeline(context,
				new Messenger(handler), sinceId, maxId);
	}

}
