package com.fanfou.app.fragment;

import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.LoaderManager.LoaderCallbacks;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v4.widget.CursorAdapter;
import android.view.ViewGroup;

public abstract class BaseCursorListFragment extends BaseListFragment implements
		LoaderCallbacks<Cursor> {
	protected static final int LOADER_ID = 0;
	protected CursorAdapter mAdapter;
	protected ViewGroup mHeaderView;
	protected ViewGroup mFooterView;

	// Observable a;
	// Observer b;

	@Override
	public Loader<Cursor> onCreateLoader(int id, Bundle args) {
		return getCursorLoader();
	}

	@Override
	public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
		if (!isVisible()) {
			return;
		}
		mAdapter.swapCursor(data);
		if (isResumed()) {
			setListShown(true);
		} else {
			setListShownNoAnimation(true);
		}

	}

	@Override
	public void onLoaderReset(Loader<Cursor> loader) {
		mAdapter.swapCursor(null);

	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		mAdapter = getCursorAdapter();
		setListAdapter(mAdapter);
		setListShown(false);
		getLoaderManager().initLoader(LOADER_ID, null, this);
	}

	protected abstract CursorAdapter getCursorAdapter();

	protected abstract CursorLoader getCursorLoader();

}
