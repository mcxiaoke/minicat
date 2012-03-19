package com.fanfou.app.hd;

import com.fanfou.app.hd.dao.model.StatusModel;
import com.fanfou.app.hd.fragments.BaseTimlineFragment;
import com.fanfou.app.hd.fragments.UserFavoritesFragment;

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
