package org.mcxiaoke.fancooker.api;

import org.mcxiaoke.fancooker.api.rest.AccountMethods;
import org.mcxiaoke.fancooker.api.rest.BlockMethods;
import org.mcxiaoke.fancooker.api.rest.DirectMessagesMethods;
import org.mcxiaoke.fancooker.api.rest.FavoritesMethods;
import org.mcxiaoke.fancooker.api.rest.FriendsFollowersMethods;
import org.mcxiaoke.fancooker.api.rest.FriendshipsMethods;
import org.mcxiaoke.fancooker.api.rest.OAuthMethods;
import org.mcxiaoke.fancooker.api.rest.ParseMethods;
import org.mcxiaoke.fancooker.api.rest.PhotosMethods;
import org.mcxiaoke.fancooker.api.rest.SavedSearchMethods;
import org.mcxiaoke.fancooker.api.rest.SearchMethods;
import org.mcxiaoke.fancooker.api.rest.StatusMethods;
import org.mcxiaoke.fancooker.api.rest.TimelineMethods;
import org.mcxiaoke.fancooker.api.rest.TrendsMethods;
import org.mcxiaoke.fancooker.api.rest.UsersMethods;

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
