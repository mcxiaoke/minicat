package com.fanfou.app.hd.fragments;

import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.content.Loader;
import android.support.v4.widget.CursorAdapter;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import com.fanfou.app.hd.App;
import com.fanfou.app.hd.adapter.ConversationCursorAdapter;
import com.fanfou.app.hd.api.Paging;
import com.fanfou.app.hd.controller.DataController;
import com.fanfou.app.hd.dao.model.DirectMessageModel;
import com.fanfou.app.hd.service.FanFouService;
import com.fanfou.app.hd.util.Utils;

/**
 * @author mcxiaoke
 * @version 1.0 2012.02.28
 * 
 */
public class ConversationFragment extends PullToRefreshListFragment {
	private static final String TAG = ConversationFragment.class
			.getSimpleName();
	
	private String userId;

	public static ConversationFragment newInstance(String id) {
		Bundle args = new Bundle();
		args.putString("id", id);
		ConversationFragment fragment = new ConversationFragment();
		fragment.setArguments(args);
		if (App.DEBUG) {
			Log.d(TAG, "newInstance() "+fragment+" id="+id);
		}
		return fragment;
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Bundle args=getArguments();
		userId=args.getString("id");
		if (App.DEBUG) {
			Log.d(TAG, "onCreate() userId="+userId);
		}
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		getListView().setTranscriptMode(ListView.TRANSCRIPT_MODE_ALWAYS_SCROLL);
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
		if (App.DEBUG) {
			Log.d(TAG, "createAdapter()");
		}
		return new ConversationCursorAdapter(getActivity(), getCursor(),true);
	}

	@Override
	protected void showToast(int count) {
		Utils.notify(getActivity(), count + "条新私信");
	}

	@Override
	protected void doFetch(boolean doGetMore) {
		final ResultHandler handler = new ResultHandler(this);
		final Cursor cursor=getCursor();
		Paging p=new Paging();
		// 对于私信对话界面来说，最上面的为最旧的，最下面的为最新的
		if(doGetMore){
			// 底部上拉获取最新的，需要sinceId
			p.sinceId=Utils.getMaxId(cursor);
		}else{
			// 顶部下拉是获取更旧的，需要maxId;
			p.maxId=Utils.getSinceId(cursor);
		}	
		
		FanFouService.getConversation(getActivity(), handler, p,userId);
	}

	@Override
	protected int getType() {
		return DirectMessageModel.TYPE_CONVERSATION_LIST;
	}

	@Override
	public Loader<Cursor> onCreateLoader(int id, Bundle args) {
		if (App.DEBUG) {
			Log.d(TAG, "onCreateLoader() userId="+userId);
		}
		return DataController.getConversationLoader(getActivity(),userId);
	}

}
