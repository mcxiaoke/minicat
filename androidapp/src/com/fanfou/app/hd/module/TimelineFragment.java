package com.fanfou.app.hd.module;

import com.fanfou.app.App;
import com.fanfou.app.adapter.StatusCursorAdapter;
import com.fanfou.app.api.Status;
import com.fanfou.app.db.Contents.BasicColumns;
import com.fanfou.app.db.Contents.StatusInfo;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.support.v4.app.LoaderManager.LoaderCallbacks;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.util.Log;
import android.view.View;
import android.widget.ListView;

/**
 * @author mcxiaoke
 * @version 1.0 2011.11.02
 * 
 */
public class TimelineFragment extends ListFragment implements
		LoaderCallbacks<Cursor> {

	private static final String TAG = TimelineFragment.class.getSimpleName();

	public static final int TYPE_HOME = 0;
	public static final int TYPE_MENTION = 1;
	public static final int TYPE_PUBLIC = 2;
	public static final int TYPE_ME = 3;

	private int type;

	private void log(String message) {
		Log.d(TAG, message);
	}

	private StatusCursorAdapter mCursorAdapter;

	public static TimelineFragment newInstance(int type) {
		return new TimelineFragment(type);
	}

	private TimelineFragment(int type) {
		this.type = type;
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
	}

	@Override
	public void onListItemClick(ListView l, View v, int position, long id) {
		super.onListItemClick(l, v, position, id);
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		mCursorAdapter = new StatusCursorAdapter(getActivity());
		setListAdapter(mCursorAdapter);
		getListView().setHorizontalScrollBarEnabled(false);
		getListView().setVerticalScrollBarEnabled(false);
		getLoaderManager().initLoader(0, null, this);
		if (App.DEBUG) {
			log("onActivityCreated");
		}
	}

	@Override
	public Loader<Cursor> onCreateLoader(int flag, Bundle bundle) {
		switch (type) {
		case TYPE_HOME:
			return createHomeCursor();
		case TYPE_MENTION:
			return createMentionCursor();
		case TYPE_PUBLIC:
			return createPublicCursor();
		case TYPE_ME:
			return createUserCursor();
		default:
			return null;
		}
	}

	private CursorLoader createHomeCursor() {
		String where = BasicColumns.TYPE + "=?";
		String[] whereArgs = new String[] { String.valueOf(Status.TYPE_HOME) };
		Uri uri = StatusInfo.CONTENT_URI;
		if (App.DEBUG) {
			log("onCreateLoader");
		}
		return new CursorLoader(getActivity(), uri, StatusInfo.COLUMNS, where,
				whereArgs, null);
	}

	private CursorLoader createMentionCursor() {
		String where = BasicColumns.TYPE + "=?";
		String[] whereArgs = new String[] { String.valueOf(Status.TYPE_MENTION) };
		Uri uri = StatusInfo.CONTENT_URI;
		if (App.DEBUG) {
			log("onCreateLoader");
		}
		return new CursorLoader(getActivity(), uri, StatusInfo.COLUMNS, where,
				whereArgs, null);
	}

	private CursorLoader createUserCursor() {
		String where = BasicColumns.TYPE + "=? AND " + StatusInfo.USER_ID
				+ " =? ";
		String[] whereArgs = new String[] { String.valueOf(Status.TYPE_USER),
				App.me.userId };
		Uri uri = StatusInfo.CONTENT_URI;
		if (App.DEBUG) {
			log("onCreateLoader");
		}
		return new CursorLoader(getActivity(), uri, StatusInfo.COLUMNS, where,
				whereArgs, null);
	}

	private CursorLoader createPublicCursor() {
		String where = BasicColumns.TYPE + "=?";
		String[] whereArgs = new String[] { String.valueOf(Status.TYPE_PUBLIC) };
		Uri uri = StatusInfo.CONTENT_URI;
		if (App.DEBUG) {
			log("onCreateLoader");
		}
		return new CursorLoader(getActivity(), uri, StatusInfo.COLUMNS, where,
				whereArgs, null);
	}

	@Override
	public void onLoadFinished(Loader<Cursor> loader, Cursor newCursor) {
		mCursorAdapter.switchCursor(newCursor);
		if (App.DEBUG) {
			log("onLoadFinished " + mCursorAdapter.getCount());
		}
	}

	@Override
	public void onLoaderReset(Loader<Cursor> loader) {
		mCursorAdapter.switchCursor(null);
		if (App.DEBUG) {
			log("onLoaderReset");
		}
	}

}
