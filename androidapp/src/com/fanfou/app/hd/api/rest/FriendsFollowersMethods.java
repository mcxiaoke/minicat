package com.fanfou.app.hd.api.rest;

import java.util.List;

import com.fanfou.app.hd.api.ApiException;
import com.fanfou.app.hd.api.Paging;
import com.fanfou.app.hd.dao.model.UserModel;

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
