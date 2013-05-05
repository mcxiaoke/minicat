package org.mcxiaoke.fancooker.fragments;

import org.mcxiaoke.fancooker.AppContext;
import org.mcxiaoke.fancooker.adapter.ConversationListCursorAdapter;
import org.mcxiaoke.fancooker.api.Paging;
import org.mcxiaoke.fancooker.controller.DataController;
import org.mcxiaoke.fancooker.controller.UIController;
import org.mcxiaoke.fancooker.dao.model.DirectMessageModel;
import org.mcxiaoke.fancooker.service.FanFouService;
import org.mcxiaoke.fancooker.util.Utils;

import android.content.Loader;
import android.database.Cursor;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.CursorAdapter;


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
		if (AppContext.DEBUG) {
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
			if (AppContext.DEBUG) {
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
		if (AppContext.DEBUG) {
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
