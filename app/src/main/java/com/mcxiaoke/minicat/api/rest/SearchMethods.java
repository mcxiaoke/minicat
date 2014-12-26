package com.mcxiaoke.minicat.api.rest;

import com.mcxiaoke.minicat.api.ApiException;
import com.mcxiaoke.minicat.api.Paging;
import com.mcxiaoke.minicat.dao.model.StatusModel;
import com.mcxiaoke.minicat.dao.model.UserModel;

import java.util.List;


/**
 * @author mcxiaoke
 * @version 1.0 2012.02.22
 */
public interface SearchMethods {

    public List<StatusModel> search(String query, Paging paging) throws ApiException;

    public List<StatusModel> searchUserTimeline(String query, String id, Paging paging) throws ApiException;

    public List<UserModel> searchUsers(String query, Paging pading) throws ApiException;

}
