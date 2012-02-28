package com.fanfou.app.hd.fragments;

import android.database.Cursor;
import android.os.Bundle;
import android.os.Messenger;
import android.util.Log;

import com.fanfou.app.hd.App;
import com.fanfou.app.hd.api.Paging;
import com.fanfou.app.hd.dao.model.StatusModel;
import com.fanfou.app.hd.fragments.PullToRefreshListFragment.ResultHandler;
import com.fanfou.app.hd.service.Constants;
import com.fanfou.app.hd.service.FanFouService;
import com.fanfou.app.hd.util.Utils;

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
		args.putInt("type", type);
		MentionTimelineFragment fragment = new MentionTimelineFragment();
		fragment.setArguments(args);
		if (App.DEBUG) {
			Log.d(TAG, "newInstance() "+fragment);
		}
		return fragment;
	}

	@Override
	protected int getType() {
		return StatusModel.TYPE_MENTIONS;
	}

	@Override
	protected void doFetch(boolean doGetMore) {
		if (App.DEBUG) {
			Log.d(TAG, "doFetch() doGetMore=" + doGetMore);
		}
		final ResultHandler handler = new ResultHandler(this);
		final Cursor cursor = getCursor();
		Paging p=new Paging();
		if (doGetMore) {
			p.maxId = Utils.getMaxId(cursor);
		} else {
			p.sinceId = Utils.getSinceId(cursor);
		}
		if (App.DEBUG) {
			Log.d(TAG, "doFetch() doGetMore=" + doGetMore+" Paging="+p);
		}
		FanFouService.getTimeline(getActivity(), StatusModel.TYPE_MENTIONS, handler, p);
	}

}
