package com.mcxiaoke.minicat.fragment;

import android.content.Loader;
import android.database.Cursor;
import android.os.Bundle;
import android.util.Log;
import com.mcxiaoke.minicat.AppContext;
import com.mcxiaoke.minicat.api.Paging;
import com.mcxiaoke.minicat.controller.DataController;
import com.mcxiaoke.minicat.dao.model.StatusModel;
import com.mcxiaoke.minicat.service.SyncService;
import com.mcxiaoke.minicat.util.Utils;


/**
 * @author mcxiaoke
 * @version 1.2 2012.03.19
 */
public class MentionTimelineFragment extends BaseTimlineFragment {
    private static final String TAG = MentionTimelineFragment.class
            .getSimpleName();

    public static MentionTimelineFragment newInstance() {
        return newInstance(false);
    }

    public static MentionTimelineFragment newInstance(boolean refresh) {
        Bundle args = new Bundle();
        args.putBoolean("refresh", refresh);
        MentionTimelineFragment fragment = new MentionTimelineFragment();
        fragment.setArguments(args);
        if (AppContext.DEBUG) {
            Log.d(TAG, "newInstance() " + fragment);
        }
        return fragment;
    }


    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
//		getActivity().setTitle("@我的消息");
    }

    @Override
    protected boolean isColored() {
        return false;
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        return DataController.getTimelineCursorLoader(getActivity(),
                StatusModel.TYPE_MENTIONS);
    }

    @Override
    protected void doFetch(boolean doGetMore) {
        if (AppContext.DEBUG) {
            Log.d(TAG, "doFetch() doGetMore=" + doGetMore);
        }
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
        SyncService.getTimeline(getActivity(), StatusModel.TYPE_MENTIONS,
                handler, p);
    }

    @Override
    protected int getType() {
        return StatusModel.TYPE_MENTIONS;
    }

    @Override
    public String getTitle() {
        return "提到我的";
    }
}
