package com.mcxiaoke.minicat.api;

import com.mcxiaoke.minicat.api.rest.AccountMethods;
import com.mcxiaoke.minicat.api.rest.BlockMethods;
import com.mcxiaoke.minicat.api.rest.DirectMessagesMethods;
import com.mcxiaoke.minicat.api.rest.FavoritesMethods;
import com.mcxiaoke.minicat.api.rest.FriendsFollowersMethods;
import com.mcxiaoke.minicat.api.rest.FriendshipsMethods;
import com.mcxiaoke.minicat.api.rest.OAuthMethods;
import com.mcxiaoke.minicat.api.rest.ParseMethods;
import com.mcxiaoke.minicat.api.rest.PhotosMethods;
import com.mcxiaoke.minicat.api.rest.SavedSearchMethods;
import com.mcxiaoke.minicat.api.rest.SearchMethods;
import com.mcxiaoke.minicat.api.rest.StatusMethods;
import com.mcxiaoke.minicat.api.rest.TimelineMethods;
import com.mcxiaoke.minicat.api.rest.TrendsMethods;
import com.mcxiaoke.minicat.api.rest.UsersMethods;

/**
 * @author mcxiaoke
 * @version 4.0 2012.02.23
 */
public interface Api extends OAuthMethods, ParseMethods, AccountMethods,
        BlockMethods, DirectMessagesMethods, FriendsFollowersMethods,
        FriendshipsMethods, PhotosMethods, SavedSearchMethods, TrendsMethods,
        SearchMethods, StatusMethods, TimelineMethods, UsersMethods, FavoritesMethods {

}
