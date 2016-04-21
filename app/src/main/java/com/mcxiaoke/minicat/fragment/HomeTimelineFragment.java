package com.mcxiaoke.minicat.fragment;

import android.content.Loader;
import android.database.Cursor;
import android.os.Bundle;
import android.util.Log;
import com.mcxiaoke.bus.Bus;
import com.mcxiaoke.bus.annotation.BusReceiver;
import com.mcxiaoke.minicat.AppContext;
import com.mcxiaoke.minicat.api.Paging;
import com.mcxiaoke.minicat.controller.DataController;
import com.mcxiaoke.minicat.dao.model.StatusModel;
import com.mcxiaoke.minicat.service.StatusUpdateEvent;
import com.mcxiaoke.minicat.service.SyncService;
import com.mcxiaoke.minicat.util.Utils;


/**
 * @author mcxiaoke
 * @version 1.3 2012.03.19
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
    public void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bus.getDefault().register(this);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Bus.getDefault().unregister(this);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
//		getActivity().setTitle("我的主页");
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
        SyncService.getTimeline(getActivity(), StatusModel.TYPE_HOME,
                handler, p);
    }

    @Override
    protected int getType() {
        return StatusModel.TYPE_HOME;
    }

    @Override
    public String getTitle() {
        return "主页";
    }

    @BusReceiver
    public void onEvent(StatusUpdateEvent event) {
        if (AppContext.DEBUG) {
            Log.d(TAG, "onEvent StatusUpdateEvent ");
        }
        Utils.notify(getActivity(), "消息发送成功");
        doRefresh();

    }


}
