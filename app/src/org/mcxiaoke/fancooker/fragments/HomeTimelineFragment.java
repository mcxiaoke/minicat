package org.mcxiaoke.fancooker.fragments;

import org.mcxiaoke.fancooker.AppContext;
import org.mcxiaoke.fancooker.api.Paging;
import org.mcxiaoke.fancooker.controller.DataController;
import org.mcxiaoke.fancooker.dao.model.StatusModel;
import org.mcxiaoke.fancooker.service.FanFouService;
import org.mcxiaoke.fancooker.util.Utils;

import android.content.Loader;
import android.database.Cursor;
import android.os.Bundle;
import android.util.Log;


/**
 * @author mcxiaoke
 * @version 1.0 2012.02.06
 * @version 1.1 2012.02.24
 * @version 1.2 2012.03.08
 * @version 1.3 2012.03.19
 * 
 */
public class HomeTimelineFragment extends BaseTimlineFragment {
	private static final String TAG = HomeTimelineFragment.class
			.getSimpleName();

	public static HomeTimelineFragment newInstance() {
		return newInstance(false);
	}

	public static HomeTimelineFragment newInstance(boolean refresh) {
		Bundle args = new Bundle();
		args.putBoolean("refresh", refresh);
		HomeTimelineFragment fragment = new HomeTimelineFragment();
		fragment.setArguments(args);
		if (AppContext.DEBUG) {
			Log.d(TAG, "newInstance() " + fragment);
		}
		return fragment;
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
//		getActivity().setTitle("我的主页");
	}

	@Override
	protected int getType() {
		return StatusModel.TYPE_HOME;
	}

	@Override
	public Loader<Cursor> onCreateLoader(int id, Bundle args) {
		return DataController.getTimelineCursorLoader(getActivity(),
				StatusModel.TYPE_HOME);
	}

	@Override
	protected void doFetch(boolean doGetMore) {

		final ResultHandler handler = new ResultHandler(this);
		final Cursor cursor = getCursor();
		Paging p = new Paging();
		if (doGetMore) {
			p.maxId = Utils.getMaxId(cursor);
		} else {
			p.sinceId = Utils.getSinceId(cursor);
		}
		if (AppContext.DEBUG) {
			Log.d(TAG, "doFetch() doGetMore=" + doGetMore + " Paging=" + p);
		}
		FanFouService.getTimeline(getActivity(), StatusModel.TYPE_HOME,
				handler, p);
	}

	@Override
	public String getTitle() {
		return "主页";
	}

}
