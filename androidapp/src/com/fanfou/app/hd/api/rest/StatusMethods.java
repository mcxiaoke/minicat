package com.fanfou.app.hd.api.rest;

import com.fanfou.app.hd.api.ApiException;
import com.fanfou.app.hd.dao.model.StatusModel;

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
