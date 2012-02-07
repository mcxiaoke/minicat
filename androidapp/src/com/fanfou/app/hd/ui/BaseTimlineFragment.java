package com.fanfou.app.hd.ui;

import com.fanfou.app.App;
import com.fanfou.app.adapter.StatusCursorAdapter;
import com.fanfou.app.api.Status;
import com.fanfou.app.db.FanFouProvider;
import com.fanfou.app.db.Contents.BasicColumns;
import com.fanfou.app.db.Contents.StatusInfo;
import com.fanfou.app.service.Constants;
import com.fanfou.app.service.FanFouService;
import com.fanfou.app.util.Utils;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.Messenger;
import android.widget.CursorAdapter;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;

/**
 * @author mcxiaoke
 * @version 1.0 2012.02.06
 * @version 1.1 2012.02.07
 * 
 */
public abstract class BaseTimlineFragment extends PullToRefreshListFragment {
	private static final String TAG = BaseTimlineFragment.class.getSimpleName();

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position,
			long id) {
		final Cursor c = (Cursor) parent.getItemAtPosition(position);
		if (c != null) {
			final Status s = Status.parse(c);
			if (s != null && !s.isNull()) {
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
	protected CursorAdapter createAdapter() {
		if (App.DEBUG) {
			Log.d(TAG, "createAdapter() id="+this+"activity ="+getActivity());
		}
		return new StatusCursorAdapter(true, getActivity(), getCursor());
	}

	@Override
	protected Cursor createCursor() {
		if (App.DEBUG) {
			Log.d(TAG, "createCursor() id="+this+"activity ="+getActivity());
		}
		String where = BasicColumns.TYPE + "=?";
		String[] whereArgs = new String[] { String.valueOf(getType()) };
		Uri uri = StatusInfo.CONTENT_URI;
		return getActivity().managedQuery(uri, StatusInfo.COLUMNS, where,
				whereArgs, FanFouProvider.ORDERBY_DATE_DESC);
	}

	@Override
	protected void showToast(int count) {
		Context context=getActivity();
		if(context!=null){
			Utils.notify(context, count + "条新消息");
		}
	}

}
