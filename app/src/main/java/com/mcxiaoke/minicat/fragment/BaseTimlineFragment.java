package com.mcxiaoke.minicat.fragment;

import android.database.Cursor;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.CursorAdapter;
import com.mcxiaoke.commons.view.endless.EndlessListView;
import com.mcxiaoke.minicat.AppContext;
import com.mcxiaoke.minicat.adapter.StatusCursorAdapter;
import com.mcxiaoke.minicat.controller.UIController;
import com.mcxiaoke.minicat.dao.model.StatusModel;

/**
 * @author mcxiaoke
 * @version 1.3 2012.02.22
 */
public abstract class BaseTimlineFragment extends SwipeRefreshListFragment {
    private static final String TAG = BaseTimlineFragment.class.getSimpleName();

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position,
                            long id) {
        final Cursor cursor = (Cursor) parent.getItemAtPosition(position);
        if (cursor != null) {
            final StatusModel s = StatusModel.from(cursor);
            if (s != null) {
                UIController.goStatusPage(getActivity(), s);
            }
        }

    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        if (getType() == StatusModel.TYPE_PUBLIC) {
            getListView().setRefreshMode(EndlessListView.RefreshMode.NONE);
        }
    }

    @Override
    protected void parseArguments(Bundle args) {
    }

    @Override
    protected CursorAdapter onCreateAdapter() {
        if (AppContext.DEBUG) {
            Log.d(TAG, "createAdapter() id=" + this + "activity ="
                    + getActivity());
        }
        return new StatusCursorAdapter(getActivity(), isColored());
    }

    @Override
    public boolean onItemLongClick(AdapterView<?> parent, View view,
                                   int position, long id) {
        final Cursor c = (Cursor) parent.getItemAtPosition(position);
        showPopup(getActivity(), view, c);
        return true;
    }

    protected boolean isColored() {
        return true;
    }

}
