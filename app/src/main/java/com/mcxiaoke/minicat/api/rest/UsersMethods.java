package com.mcxiaoke.minicat.api.rest;

import com.mcxiaoke.minicat.api.ApiException;
import com.mcxiaoke.minicat.api.Paging;
import com.mcxiaoke.minicat.dao.model.UserModel;

import java.util.List;


/**
 * @author mcxiaoke
 * @version 1.0 2012.02.23
 */
public interface UsersMethods {

    public List<UserModel> getFriends(String id, Paging paging) throws ApiException;

    public List<UserModel> getFollowers(String id, Paging paging) throws ApiException;

    public List<UserModel> getUserRecommendation(Paging paging) throws ApiException;

    public UserModel ignoreUserRecommendation(String id) throws ApiException;

    public List<UserModel> getUsersByTag(String tag, Paging paging) throws ApiException;

    public List<String> getUserTags(String id) throws ApiException;

    public UserModel showUser(String id) throws ApiException;

}
