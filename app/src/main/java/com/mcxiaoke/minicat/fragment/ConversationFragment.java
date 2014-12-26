package com.mcxiaoke.minicat.fragment;

import android.content.Loader;
import android.database.Cursor;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.CursorAdapter;
import android.widget.ListView;
import com.mcxiaoke.minicat.AppContext;
import com.mcxiaoke.minicat.adapter.ConversationCursorAdapter;
import com.mcxiaoke.minicat.api.Paging;
import com.mcxiaoke.minicat.controller.DataController;
import com.mcxiaoke.minicat.dao.model.DirectMessageModel;
import com.mcxiaoke.minicat.service.SyncService;
import com.mcxiaoke.minicat.util.Utils;


/**
 * @author mcxiaoke
 * @version 1.3 2012.03.23
 */
public class ConversationFragment extends SwipeRefreshListFragment {
    private static final String TAG = ConversationFragment.class
            .getSimpleName();

    private String userId;
    private String screenName;

    public static ConversationFragment newInstance(String id, String screenName) {
        return newInstance(id, screenName, false);
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
    public void onItemClick(AdapterView<?> parent, View view, int position,
                            long id) {
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        if (AppContext.DEBUG) {
            Log.d(TAG, "onCreateLoader() userId=" + userId);
        }
        return DataController.getConversationLoader(getActivity(), userId);
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
//        getListView().setStackFromBottom(true);
        getListView().setTranscriptMode(ListView.TRANSCRIPT_MODE_ALWAYS_SCROLL);
    }

    @Override
    protected void parseArguments(Bundle args) {
        userId = args.getString("id");
        screenName = args.getString("screen_name");
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

        SyncService.getConversation(getActivity(), handler, p, userId);
    }

    @Override
    protected int getType() {
        return DirectMessageModel.TYPE_CONVERSATION_LIST;
    }

    @Override
    public boolean onItemLongClick(AdapterView<?> parent, View view,
                                   int position, long id) {
        return true;
    }

    @Override
    public String getTitle() {
        return screenName;
    }

}
