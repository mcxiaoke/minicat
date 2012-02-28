package com.fanfou.app.hd.api.rest;

import java.util.List;

import com.fanfou.app.hd.api.ApiException;
import com.fanfou.app.hd.api.Paging;
import com.fanfou.app.hd.dao.model.StatusModel;

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
