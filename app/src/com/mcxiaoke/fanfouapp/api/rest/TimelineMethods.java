package com.mcxiaoke.fanfouapp.api.rest;

import com.mcxiaoke.fanfouapp.api.ApiException;
import com.mcxiaoke.fanfouapp.api.Paging;
import com.mcxiaoke.fanfouapp.dao.model.StatusModel;

import java.util.List;


/**
 * @author mcxiaoke
 * @version 1.0 2012.02.22
 *
 */
public interface TimelineMethods {
	
	public List<StatusModel> getHomeTimeline(Paging paging) throws ApiException;
	public List<StatusModel> getMentions(Paging paging) throws ApiException;
	public List<StatusModel> getPublicTimeline() throws ApiException;
	public List<StatusModel> getUserTimeline(String userId, Paging paging) throws ApiException;
	public List<StatusModel> getContextTimeline(String contextId) throws ApiException;

}
