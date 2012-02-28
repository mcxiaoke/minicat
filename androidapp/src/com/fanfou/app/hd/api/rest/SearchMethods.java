package com.fanfou.app.hd.api.rest;

import java.util.List;

import com.fanfou.app.hd.api.ApiException;
import com.fanfou.app.hd.api.Paging;
import com.fanfou.app.hd.dao.model.StatusModel;
import com.fanfou.app.hd.dao.model.UserModel;

/**
 * @author mcxiaoke
 * @version 1.0 2012.02.22
 *
 */
public interface SearchMethods {
	
	public List<StatusModel> search(String query, Paging paging) throws ApiException;
	public List<StatusModel> searchUserTimeline(String query, String id, Paging paging) throws ApiException;
	public List<UserModel> searchUsers(String query, Paging pading) throws ApiException;

}
