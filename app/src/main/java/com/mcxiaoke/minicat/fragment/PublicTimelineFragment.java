package com.mcxiaoke.minicat.fragment;

import android.content.Loader;
import android.database.Cursor;
import android.os.Bundle;
import android.util.Log;
import com.mcxiaoke.minicat.AppContext;
import com.mcxiaoke.minicat.controller.DataController;
import com.mcxiaoke.minicat.dao.model.StatusModel;
import com.mcxiaoke.minicat.service.SyncService;


/**
 * @author mcxiaoke
 * @version 1.3 2012.03.19
 */
public class PublicTimelineFragment extends BaseTimlineFragment {
    private static final String TAG = PublicTimelineFragment.class
            .getSimpleName();

    public static PublicTimelineFragment newInstance() {
        return newInstance(false);
    }

    public static PublicTimelineFragment newInstance(boolean refresh) {
        Bundle args = new Bundle();
        args.putBoolean("refresh", refresh);
        PublicTimelineFragment fragment = new PublicTimelineFragment();
        fragment.setArguments(args);
        if (AppContext.DEBUG) {
            Log.d(TAG, "newInstance() " + fragment);
        }
        return fragment;
    }


    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        return DataController.getTimelineCursorLoader(getActivity(),
                StatusModel.TYPE_PUBLIC);
    }

    @Override
    protected boolean shouldDelayRefresh() {
        return true;
    }

    @Override
    protected void doFetch(boolean doGetMore) {
        if (AppContext.DEBUG) {
            Log.d(TAG, "doFetch() doGetMore=" + doGetMore);
        }
        final ResultHandler handler = new ResultHandler(this);
        SyncService.getPublicTimeline(getActivity(), handler);
    }

    @Override
    protected int getType() {
        return StatusModel.TYPE_PUBLIC;
    }

    @Override
    public String getTitle() {
        return "随便看看";
    }

}
