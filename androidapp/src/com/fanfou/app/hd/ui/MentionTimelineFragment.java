package com.fanfou.app.hd.ui;

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
public class MentionTimelineFragment extends BaseTimlineFragment {
	private static final String TAG = MentionTimelineFragment.class
			.getSimpleName();

	public static MentionTimelineFragment newInstance(int type) {
		Bundle args = new Bundle();
		args.putInt(Constants.EXTRA_TYPE, type);
		MentionTimelineFragment fragment = new MentionTimelineFragment();
		fragment.setArguments(args);
		if (App.DEBUG) {
			Log.d(TAG, "newInstance() "+fragment);
		}
		return fragment;
	}

	@Override
	protected int getType() {
		return Constants.TYPE_STATUSES_MENTIONS;
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
		FanFouService.doFetchMentions(getActivity(), new Messenger(handler),
				sinceId, maxId);
	}

}
