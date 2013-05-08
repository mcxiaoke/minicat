package org.mcxiaoke.fancooker.fragments;

import org.mcxiaoke.fancooker.AppContext;
import org.mcxiaoke.fancooker.adapter.StatusCursorAdapter;
import org.mcxiaoke.fancooker.controller.UIController;
import org.mcxiaoke.fancooker.dao.model.StatusModel;
import org.mcxiaoke.fancooker.util.Utils;

import android.database.Cursor;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.CursorAdapter;


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
				UIController.goStatusPage(getActivity(), s);
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
			Log.d(TAG, "createAdapter() id=" + this + "activity ="
					+ getActivity());
		}
		return new StatusCursorAdapter(getActivity());
	}

}
