package com.mcxiaoke.minicat.api.rest;

import com.mcxiaoke.minicat.api.ApiException;
import com.mcxiaoke.minicat.api.Paging;
import com.mcxiaoke.minicat.dao.model.UserModel;

import java.util.List;


/**
 * @author mcxiaoke
 * @version 1.0 2012.02.23
 */
public interface BlockMethods {

    public List<String> blockIDs() throws ApiException;

    public List<UserModel> blockUsers(Paging paging) throws ApiException;

    public UserModel isBlocked(String id) throws ApiException;

    public UserModel block(String id) throws ApiException;

    public UserModel unblock(String id) throws ApiException;


}
