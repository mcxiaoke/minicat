package org.mcxiaoke.fancooker.api.rest;

import java.util.List;

import org.mcxiaoke.fancooker.api.ApiException;
import org.mcxiaoke.fancooker.api.Paging;
import org.mcxiaoke.fancooker.dao.model.UserModel;


/**
 * @author mcxiaoke
 * @version 1.0 2012.02.22
 *
 */
public interface FriendsFollowersMethods {
	
	List<String> getFriendsIDs(String id, Paging paging) throws ApiException;
	List<String> getFollowersIDs(String id, Paging paging) throws ApiException;
	
	List<UserModel> getFriends(String id, Paging paging) throws ApiException;
	List<UserModel> getFollowers(String id, Paging paging) throws ApiException;

}
