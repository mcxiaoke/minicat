package com.mcxiaoke.minicat.fragment;

import android.content.Loader;
import android.database.Cursor;
import android.os.Bundle;
import android.util.Log;
import com.mcxiaoke.minicat.AppContext;
import com.mcxiaoke.minicat.api.Paging;
import com.mcxiaoke.minicat.controller.DataController;
import com.mcxiaoke.minicat.dao.model.StatusModel;
import com.mcxiaoke.minicat.service.SyncService;
import com.mcxiaoke.minicat.util.StringHelper;


/**
 * @author mcxiaoke
 * @version 1.3 2012.03.08
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
        if (AppContext.DEBUG) {
            Log.d(TAG, "newInstance() " + fragment);
        }
        return fragment;
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        return DataController.getUserFavoritesCursorLoader(getActivity(),
                userId);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (AppContext.DEBUG) {
            Log.d(TAG, "onCreate() userId=" + userId);
        }
    }

    @Override
    protected void doFetch(boolean doGetMore) {
        if (AppContext.DEBUG) {
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

        if (AppContext.DEBUG) {
            Log.d(TAG, "doFetch() doGetMore=" + doGetMore + " Paging=" + p);
        }
        SyncService.getTimeline(getActivity(), StatusModel.TYPE_FAVORITES,
                handler, userId, p);

    }

    @Override
    protected int getType() {
        return StatusModel.TYPE_FAVORITES;
    }

    @Override
    protected void parseArguments(Bundle args) {
        if (args != null) {
            userId = args.getString("id");
        }

        if (StringHelper.isEmpty(userId)) {
            userId = AppContext.getAccount();
        }
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
//		getActivity().setTitle("我的收藏");
    }

    @Override
    public String getTitle() {
        return "收藏";
    }

}
