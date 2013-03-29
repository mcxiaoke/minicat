package org.mcxiaoke.fancooker.api.rest;

import java.util.List;

import org.mcxiaoke.fancooker.api.ApiException;
import org.mcxiaoke.fancooker.api.Paging;
import org.mcxiaoke.fancooker.dao.model.StatusModel;
import org.mcxiaoke.fancooker.dao.model.UserModel;


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
