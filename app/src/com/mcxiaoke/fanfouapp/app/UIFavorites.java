package com.mcxiaoke.fanfouapp.app;

import com.mcxiaoke.fanfouapp.dao.model.StatusModel;
import com.mcxiaoke.fanfouapp.fragments.BaseTimlineFragment;
import com.mcxiaoke.fanfouapp.fragments.UserFavoritesFragment;

/**
 * @author mcxiaoke
 * @version 1.0 2012.02.08
 *
 */
public class UIFavorites extends UIBaseTimeline {

	@Override
	protected int getType() {
		return StatusModel.TYPE_FAVORITES;
	}

	@Override
	protected BaseTimlineFragment getFragment(String userId) {
		return UserFavoritesFragment.newInstance(userId,true);
	}

}
