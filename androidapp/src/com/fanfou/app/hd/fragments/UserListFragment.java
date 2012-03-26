package com.fanfou.app.hd.fragments;

import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.content.Loader;
import android.support.v4.widget.CursorAdapter;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.FilterQueryProvider;

import com.fanfou.app.hd.App;
import com.fanfou.app.hd.adapter.UserCursorAdapter;
import com.fanfou.app.hd.api.Paging;
import com.fanfou.app.hd.controller.DataController;
import com.fanfou.app.hd.controller.UIController;
import com.fanfou.app.hd.dao.model.UserModel;
import com.fanfou.app.hd.service.FanFouService;

/**
 * @author mcxiaoke
 * @version 1.0 2012.02.07
 * @version 1.1 2012.02.08
 * @version 1.2 2012.02.09
 * @version 1.3 2012.02.22
 * @version 1.4 2012.02.24
 * @version 1.5 2012.03.08
 * @version 2.0 2012.03.26
 * 
 */
public abstract class UserListFragment extends PullToRefreshListFragment
		implements FilterQueryProvider {

	private static final String TAG = UserListFragment.class.getSimpleName();

	private int page;
	private String userId;

	private OnInitCompleteListener mListener;

	public void setOnInitCompleteListener(OnInitCompleteListener listener) {
		this.mListener = listener;
	}

	private void onInitComplete() {
		if (mListener != null) {
			mListener.onInitComplete(null);
		}
	}

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position,
			long id) {
		final Cursor c = (Cursor) parent.getItemAtPosition(position);
		final UserModel u = UserModel.from(c);
		if (u != null) {
			if (App.DEBUG) {
				Log.d(TAG,
						"userId=" + u.getId() + " username="
								+ u.getScreenName());
			}
			UIController.showProfile(getActivity(), u);
		}
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		if (App.DEBUG) {
			Log.d(TAG, "onCreate() userId=" + userId);
		}
	}

	@Override
	protected void parseArguments(Bundle args) {
		if (args != null) {
			userId = args.getString("id");
		}
		if (TextUtils.isEmpty(userId)) {
			userId = App.getAccount();
		}
	}

	@Override
	protected CursorAdapter onCreateAdapter() {
		return new UserCursorAdapter(getActivity(), null);
	}

	@Override
	protected void doFetch(boolean doGetMore) {
		Paging p = new Paging();

		if (doGetMore) {
			page++;
		} else {
			page = 1;
		}
		p.page = page;

		final ResultHandler handler = new ResultHandler(this);
		FanFouService.getUsers(getActivity(), userId, getType(), p, handler);
	}

	@Override
	public Loader<Cursor> onCreateLoader(int id, Bundle args) {
		return DataController.getUserListCursorLoader(getActivity(), getType(),
				userId);
	}

	@Override
	public void onLoadFinished(Loader<Cursor> loader, Cursor newCursor) {
		super.onLoadFinished(loader, newCursor);
		getAdapter().setFilterQueryProvider(this);
		onInitComplete();
	}

	@Override
	public Cursor runQuery(CharSequence constraint) {
		return DataController.getUserListSearchCursor(getActivity(), getType(), userId, constraint);

	}
	
	public void filter(String text){
		getAdapter().getFilter().filter(text);
	}

}
