package com.mcxiaoke.minicat.api.rest;

import com.mcxiaoke.minicat.api.ApiException;
import com.mcxiaoke.minicat.api.Paging;
import com.mcxiaoke.minicat.dao.model.UserModel;

import java.util.List;


/**
 * @author mcxiaoke
 * @version 1.0 2012.02.22
 */
public interface FriendsFollowersMethods {

    List<String> getFriendsIDs(String id, Paging paging) throws ApiException;

    List<String> getFollowersIDs(String id, Paging paging) throws ApiException;

    List<UserModel> getFriends(String id, Paging paging) throws ApiException;

    List<UserModel> getFollowers(String id, Paging paging) throws ApiException;

}
