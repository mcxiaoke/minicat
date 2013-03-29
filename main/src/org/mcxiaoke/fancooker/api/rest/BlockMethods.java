package org.mcxiaoke.fancooker.api.rest;

import java.util.List;

import org.mcxiaoke.fancooker.api.ApiException;
import org.mcxiaoke.fancooker.api.Paging;
import org.mcxiaoke.fancooker.dao.model.UserModel;


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
