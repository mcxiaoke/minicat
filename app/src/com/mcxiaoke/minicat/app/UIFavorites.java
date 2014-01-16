package com.mcxiaoke.minicat.app;

import com.mcxiaoke.minicat.dao.model.StatusModel;
import com.mcxiaoke.minicat.fragment.BaseTimlineFragment;
import com.mcxiaoke.minicat.fragment.UserFavoritesFragment;

/**
 * @author mcxiaoke
 * @version 1.0 2012.02.08
 */
public class UIFavorites extends UIBaseTimeline {

    @Override
    protected int getType() {
        return StatusModel.TYPE_FAVORITES;
    }

    @Override
    protected BaseTimlineFragment getFragment(String userId) {
        return UserFavoritesFragment.newInstance(userId, true);
    }

    @Override
    protected String getTitleSuffix() {
        return "收藏";
    }

}
