package com.fanfou.app.hd.api.rest;

import java.util.List;

import com.fanfou.app.hd.api.ApiException;
import com.fanfou.app.hd.api.Paging;
import com.fanfou.app.hd.dao.model.UserModel;

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
