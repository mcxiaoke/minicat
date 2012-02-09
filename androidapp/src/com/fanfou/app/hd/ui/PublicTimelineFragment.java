package com.fanfou.app.hd.ui;

import android.os.Bundle;
import android.os.Messenger;
import android.util.Log;

import com.fanfou.app.hd.App;
import com.fanfou.app.hd.service.Constants;
import com.fanfou.app.hd.service.FanFouService;

/**
 * @author mcxiaoke
 * @version 1.0 2012.02.06
 * 
 */
public class PublicTimelineFragment extends BaseTimlineFragment {
	private static final String TAG = PublicTimelineFragment.class
			.getSimpleName();

	public static PublicTimelineFragment newInstance(int type) {
		Bundle args = new Bundle();
		args.putInt(Constants.EXTRA_TYPE, type);
		PublicTimelineFragment fragment = new PublicTimelineFragment();
		fragment.setArguments(args);
		if (App.DEBUG) {
			Log.d(TAG, "newInstance() "+fragment);
		}
		return fragment;
	}

	@Override
	protected int getType() {
		return Constants.TYPE_STATUSES_PUBLIC_TIMELINE;
	}

	@Override
	protected void doFetch(boolean doGetMore) {
		if (App.DEBUG) {
			Log.d(TAG, "doFetch() doGetMore=" + doGetMore);
		}
		final ResultHandler handler = new ResultHandler(this);
		FanFouService.doFetchPublicTimeline(getActivity(), new Messenger(
				handler));
	}

}
