package com.fanfou.app.hd.fragments;

import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v4.widget.CursorAdapter;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;

import com.fanfou.app.hd.App;
import com.fanfou.app.hd.adapter.StatusCursorAdapter;
import com.fanfou.app.hd.api.Status;
import com.fanfou.app.hd.db.FanFouProvider;
import com.fanfou.app.hd.db.Contents.BasicColumns;
import com.fanfou.app.hd.db.Contents.StatusInfo;
import com.fanfou.app.hd.util.Utils;

/**
 * @author mcxiaoke
 * @version 1.0 2012.02.06
 * @version 1.1 2012.02.07
 * @version 1.2 2012.02.09
 * 
 */
public abstract class BaseTimlineFragment extends PullToRefreshListFragment{
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
	protected CursorAdapter onCreateAdapter() {
		if (App.DEBUG) {
			Log.d(TAG, "createAdapter() id="+this+"activity ="+getActivity());
		}
		return new StatusCursorAdapter(true, getActivity(), getCursor());
	}

	@Override
	protected void showToast(int count) {
		Context context=getActivity();
		if(context!=null){
			Utils.notify(context, count + "条新消息");
		}
	}

	@Override
	public Loader<Cursor> onCreateLoader(int id, Bundle args) {
		Uri uri = StatusInfo.CONTENT_URI;
		String selection = BasicColumns.TYPE + "=?";
		String[] selectionArgs = new String[] { String.valueOf(getType()) };
		String sortOrder=FanFouProvider.ORDERBY_DATE_DESC;
		CursorLoader loader=new CursorLoader(getActivity(), uri, null, selection, selectionArgs, sortOrder);
		return loader;
	}

}
