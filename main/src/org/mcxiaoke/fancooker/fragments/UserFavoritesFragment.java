package org.mcxiaoke.fancooker.fragments;

import org.mcxiaoke.fancooker.App;
import org.mcxiaoke.fancooker.api.Paging;
import org.mcxiaoke.fancooker.controller.DataController;
import org.mcxiaoke.fancooker.dao.model.StatusModel;
import org.mcxiaoke.fancooker.service.FanFouService;
import org.mcxiaoke.fancooker.util.StringHelper;

import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.content.Loader;
import android.util.Log;


/**
 * @author mcxiaoke
 * @version 1.0 2012.02.07
 * @version 1.1 2012.02.09
 * @version 1.2 2012.02.24
 * @version 1.3 2012.03.08
 * 
 */
public class UserFavoritesFragment extends BaseTimlineFragment {
	private static final String TAG = UserFavoritesFragment.class
			.getSimpleName();
	private String userId;
	private int page;

	public static UserFavoritesFragment newInstance(String userId) {
		return newInstance(userId, false);
	}

	public static UserFavoritesFragment newInstance(String userId,
			boolean refresh) {
		Bundle args = new Bundle();
		args.putString("id", userId);
		args.putBoolean("refresh", refresh);
		UserFavoritesFragment fragment = new UserFavoritesFragment();
		fragment.setArguments(args);
		if (App.DEBUG) {
			Log.d(TAG, "newInstance() " + fragment);
		}
		return fragment;
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		if (App.DEBUG) {
			Log.d(TAG, "onCreate() userId=" + userId);
		}
	}

	@Override
	protected int getType() {
		return StatusModel.TYPE_FAVORITES;
	}

	@Override
	protected void doFetch(boolean doGetMore) {
		if (App.DEBUG) {
			Log.d(TAG, "doFetch() doGetMore=" + doGetMore);
		}
		final ResultHandler handler = new ResultHandler(this);
		Paging p = new Paging();

		if (doGetMore) {
			page++;
		} else {
			page = 1;
		}

		p.page = page;

		if (App.DEBUG) {
			Log.d(TAG, "doFetch() doGetMore=" + doGetMore + " Paging=" + p);
		}
		FanFouService.getTimeline(getActivity(), StatusModel.TYPE_FAVORITES,
				handler, userId, p);

	}

	@Override
	public Loader<Cursor> onCreateLoader(int id, Bundle args) {
		return DataController.getUserFavoritesCursorLoader(getActivity(),
				userId);
	}

	@Override
	protected void parseArguments(Bundle args) {
		if (args != null) {
			userId = args.getString("id");
		}

		if (StringHelper.isEmpty(userId)) {
			userId = App.getAccount();
		}
	}

	@Override
	public String getTitle() {
		return "收藏";
	}

}
