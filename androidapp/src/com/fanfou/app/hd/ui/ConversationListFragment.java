package com.fanfou.app.hd.ui;

import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.Messenger;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v4.widget.CursorAdapter;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;

import com.fanfou.app.hd.App;
import com.fanfou.app.hd.adapter.MessageCursorAdapter;
import com.fanfou.app.hd.db.Contents.DirectMessageInfo;
import com.fanfou.app.hd.service.Constants;
import com.fanfou.app.hd.service.FanFouService;
import com.fanfou.app.hd.util.Utils;

/**
 * @author mcxiaoke
 * @version 1.0 2012.02.06
 * @version 1.1 2012.02.08
 * @version 1.2 2012.02.09
 * 
 */
public class ConversationListFragment extends PullToRefreshListFragment {
	private static final String TAG = ConversationListFragment.class
			.getSimpleName();

	public static ConversationListFragment newInstance(int type) {
		Bundle args = new Bundle();
		args.putInt(Constants.EXTRA_TYPE, type);
		ConversationListFragment fragment = new ConversationListFragment();
		fragment.setArguments(args);
		if (App.DEBUG) {
			Log.d(TAG, "newInstance() "+fragment);
		}
		return fragment;
	}

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position,
			long id) {
		final Cursor c = (Cursor) parent.getItemAtPosition(position);
		if (c != null) {
			Utils.goMessageChatPage(getActivity(), c);
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
		return new MessageCursorAdapter(getActivity(), getCursor());
	}

	@Override
	protected void showToast(int count) {
		Utils.notify(getActivity(), count + "条新私信");
	}

	@Override
	protected void doFetch(boolean doGetMore) {
		final ResultHandler handler = new ResultHandler(this);
		FanFouService.doFetchDirectMessagesConversationList(getActivity(),
				new Messenger(handler), doGetMore);
	}

	@Override
	protected int getType() {
		return Constants.TYPE_DIRECT_MESSAGES_CONVERSTATION_LIST;
	}

	@Override
	public Loader<Cursor> onCreateLoader(int id, Bundle args) {
		Uri uri = Uri.withAppendedPath(DirectMessageInfo.CONTENT_URI, "list");
		CursorLoader loader=new CursorLoader(getActivity(), uri, null, null, null, null);
		return loader;
	}

}
