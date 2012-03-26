package com.fanfou.app.hd.fragments;

import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.content.Loader;
import android.support.v4.widget.CursorAdapter;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;

import com.fanfou.app.hd.App;
import com.fanfou.app.hd.adapter.ConversationListCursorAdapter;
import com.fanfou.app.hd.api.Paging;
import com.fanfou.app.hd.controller.DataController;
import com.fanfou.app.hd.controller.UIController;
import com.fanfou.app.hd.dao.model.DirectMessageModel;
import com.fanfou.app.hd.service.FanFouService;
import com.fanfou.app.hd.util.Utils;

/**
 * @author mcxiaoke
 * @version 1.0 2012.02.06
 * @version 1.1 2012.02.08
 * @version 1.2 2012.02.09
 * @version 1.3 2012.02.24
 * @version 1.4 2012.02.28
 * @version 1.5 2012.03.07
 * 
 */
public class ConversationListFragment extends PullToRefreshListFragment {
	private static final String TAG = ConversationListFragment.class
			.getSimpleName();

	public static ConversationListFragment newInstance(boolean refresh) {
		Bundle args = new Bundle();
		args.putBoolean("refresh", refresh);
		ConversationListFragment fragment = new ConversationListFragment();
		fragment.setArguments(args);
		if (App.DEBUG) {
			Log.d(TAG, "newInstance() " + fragment);
		}
		return fragment;
	}

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position,
			long id) {
		final Cursor cursor = (Cursor) parent.getItemAtPosition(position);
		if (cursor != null) {
			final DirectMessageModel dm = DirectMessageModel.from(cursor);
			if (App.DEBUG) {
				Log.d(TAG, "cursor=" + cursor + " dm=" + dm);
			}
			if (dm != null) {
				UIController.showConversation(getActivity(), dm);
			}
		}

	}

	@Override
	public boolean onItemLongClick(AdapterView<?> parent, View view,
			int position, long id) {
		final Cursor c = (Cursor) parent.getItemAtPosition(position);
		showPopup(getActivity(), view, c);
		return true;
	}

	@Override
	protected CursorAdapter onCreateAdapter() {
		if (App.DEBUG) {
			Log.d(TAG, "createAdapter()");
		}
		return new ConversationListCursorAdapter(getActivity(), null);
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
		FanFouService.getConversationList(getActivity(), handler, p);
	}

	@Override
	protected int getType() {
		return DirectMessageModel.TYPE_CONVERSATION_LIST;
	}

	@Override
	public Loader<Cursor> onCreateLoader(int id, Bundle args) {
		return DataController.getConversationListLoader(getActivity());
	}

	@Override
	protected void parseArguments(Bundle args) {
	}

	@Override
	public String getTitle() {
		return "私信";
	}

}
