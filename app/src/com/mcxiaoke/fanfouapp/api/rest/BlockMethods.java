package com.mcxiaoke.fanfouapp.api.rest;

import com.mcxiaoke.fanfouapp.api.ApiException;
import com.mcxiaoke.fanfouapp.api.Paging;
import com.mcxiaoke.fanfouapp.dao.model.UserModel;

import java.util.List;


/**
 * @author mcxiaoke
 * @version 1.0 2012.02.23
 *
 */
public interface BlockMethods {
	
	public List<String> blockIDs() throws ApiException;
	public List<UserModel> blockUsers(Paging paging) throws ApiException;
	public UserModel isBlocked(String id) throws ApiException;
	public UserModel block(String id) throws ApiException;
	public UserModel unblock(String id) throws ApiException;
	

}
