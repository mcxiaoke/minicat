package com.fanfou.app.hd.api;

import com.fanfou.app.hd.api.rest.AccountMethods;
import com.fanfou.app.hd.api.rest.BlockMethods;
import com.fanfou.app.hd.api.rest.DirectMessagesMethods;
import com.fanfou.app.hd.api.rest.FavoritesMethods;
import com.fanfou.app.hd.api.rest.FriendsFollowersMethods;
import com.fanfou.app.hd.api.rest.FriendshipsMethods;
import com.fanfou.app.hd.api.rest.OAuthMethods;
import com.fanfou.app.hd.api.rest.ParseMethods;
import com.fanfou.app.hd.api.rest.PhotosMethods;
import com.fanfou.app.hd.api.rest.SavedSearchMethods;
import com.fanfou.app.hd.api.rest.SearchMethods;
import com.fanfou.app.hd.api.rest.StatusMethods;
import com.fanfou.app.hd.api.rest.TimelineMethods;
import com.fanfou.app.hd.api.rest.TrendsMethods;
import com.fanfou.app.hd.api.rest.UsersMethods;

/**
 * @author mcxiaoke
 * @version 1.0 2011.05.12
 * @version 1.1 2011.05.15
 * @version 1.2 2011.10.18
 * @version 1.3 2011.10.28
 * @version 1.4 2011.11.07
 * @version 1.5 2011.11.09
 * @version 1.6 2011.11.11
 * @version 2.0 2011.11.18
 * @version 3.0 2011.11.21
 * @version 4.0 2012.02.23
 * 
 */
public interface Api extends OAuthMethods, ParseMethods, AccountMethods,
		BlockMethods, DirectMessagesMethods, FriendsFollowersMethods,
		FriendshipsMethods, PhotosMethods, SavedSearchMethods, TrendsMethods,
		SearchMethods, StatusMethods, TimelineMethods, UsersMethods,FavoritesMethods {

}
