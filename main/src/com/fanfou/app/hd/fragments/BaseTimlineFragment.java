package com.fanfou.app.hd.fragments;

import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.widget.CursorAdapter;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;

import com.fanfou.app.hd.App;
import com.fanfou.app.hd.adapter.StatusCursorAdapter;
import com.fanfou.app.hd.dao.model.StatusModel;
import com.fanfou.app.hd.util.Utils;

/**
 * @author mcxiaoke
 * @version 1.0 2012.02.06
 * @version 1.1 2012.02.07
 * @version 1.2 2012.02.09
 * @version 1.3 2012.02.22
 * 
 */
public abstract class BaseTimlineFragment extends PullToRefreshListFragment {
	private static final String TAG = BaseTimlineFragment.class.getSimpleName();

	@Override
	protected void parseArguments(Bundle args) {
	}

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position,
			long id) {
		final Cursor cursor = (Cursor) parent.getItemAtPosition(position);
		if (cursor != null) {
			final StatusModel s = StatusModel.from(cursor);
			if (s != null) {
				Utils.goStatusPage(getActivity(), s);
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
			Log.d(TAG, "createAdapter() id=" + this + "activity ="
					+ getActivity());
		}
		return new StatusCursorAdapter(getActivity());
	}

}
