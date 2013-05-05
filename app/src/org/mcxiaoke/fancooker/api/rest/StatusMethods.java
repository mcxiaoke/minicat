package org.mcxiaoke.fancooker.api.rest;

import org.mcxiaoke.fancooker.api.ApiException;
import org.mcxiaoke.fancooker.dao.model.StatusModel;

/**
 * @author mcxiaoke
 * @version 1.0 2012.02.22
 * 
 */
public interface StatusMethods {

	StatusModel showStatus(String id) throws ApiException;
	StatusModel deleteStatus(String id) throws ApiException;
	StatusModel retweetStatus(String id) throws ApiException;
	StatusModel updateStatus(String status, String replyId,
			String repostId, String location) throws ApiException;
	
	

}
