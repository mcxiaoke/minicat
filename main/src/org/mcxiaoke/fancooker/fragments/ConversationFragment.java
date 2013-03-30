package org.mcxiaoke.fancooker.fragments;

import org.mcxiaoke.fancooker.AppContext;
import org.mcxiaoke.fancooker.adapter.ConversationCursorAdapter;
import org.mcxiaoke.fancooker.api.Paging;
import org.mcxiaoke.fancooker.controller.DataController;
import org.mcxiaoke.fancooker.dao.model.DirectMessageModel;
import org.mcxiaoke.fancooker.service.FanFouService;
import org.mcxiaoke.fancooker.util.Utils;

import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.content.Loader;
import android.support.v4.widget.CursorAdapter;
import android.util.Log;
import android.view.View;
import android.widget.AbsListView;
import android.widget.AdapterView;


/**
 * @author mcxiaoke
 * @version 1.0 2012.02.28
 * @version 1.1 2012.03.07
 * @version 1.2 2012.03.08
 * @version 1.3 2012.03.23
 * 
 */
public class ConversationFragment extends PullToRefreshListFragment {
	private static final String TAG = ConversationFragment.class
			.getSimpleName();

	private String userId;
	private String screenName;

	public static ConversationFragment newInstance(String id, String screenName) {
		return newInstance(id, screenName,false);
	}

	public static ConversationFragment newInstance(String id, String screenName, boolean refresh) {
		Bundle args = new Bundle();
		args.putString("id", id);
		args.putString("screen_name", screenName);
		args.putBoolean("refresh", refresh);
		ConversationFragment fragment = new ConversationFragment();
		fragment.setArguments(args);
		if (AppContext.DEBUG) {
			Log.d(TAG, "newInstance() " + fragment + " id=" + id);
		}
		return fragment;
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		if (AppContext.DEBUG) {
			Log.d(TAG, "onCreate() userId=" + userId);
		}
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		getListView().setTranscriptMode(AbsListView.TRANSCRIPT_MODE_ALWAYS_SCROLL);
	}

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position,
			long id) {
	}

	@Override
	public boolean onItemLongClick(AdapterView<?> parent, View view,
			int position, long id) {
		return true;
	}

	@Override
	protected CursorAdapter onCreateAdapter() {
		if (AppContext.DEBUG) {
			Log.d(TAG, "createAdapter()");
		}
		return new ConversationCursorAdapter(getActivity());
	}

	@Override
	protected void doFetch(boolean doGetMore) {
		final ResultHandler handler = new ResultHandler(this);
		final Cursor cursor = getCursor();
		Paging p = new Paging();
		// 对于私信对话界面来说，最上面的为最旧的，最下面的为最新的
		if (doGetMore) {
			// 底部上拉获取最新的，需要sinceId
			p.sinceId = Utils.getMaxId(cursor);
		} else {
			// 顶部下拉是获取更旧的，需要maxId;
			p.maxId = Utils.getSinceId(cursor);
		}

		FanFouService.getConversation(getActivity(), handler, p, userId);
	}

	@Override
	protected int getType() {
		return DirectMessageModel.TYPE_CONVERSATION_LIST;
	}

	@Override
	public Loader<Cursor> onCreateLoader(int id, Bundle args) {
		if (AppContext.DEBUG) {
			Log.d(TAG, "onCreateLoader() userId=" + userId);
		}
		return DataController.getConversationLoader(getActivity(), userId);
	}

	@Override
	protected void parseArguments(Bundle args) {
		userId = args.getString("id");
		screenName=args.getString("screen_name");
	}

	@Override
	public String getTitle() {
		return screenName;
	}

}
