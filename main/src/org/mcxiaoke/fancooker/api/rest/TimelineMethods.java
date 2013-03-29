package org.mcxiaoke.fancooker.api.rest;

import java.util.List;

import org.mcxiaoke.fancooker.api.ApiException;
import org.mcxiaoke.fancooker.api.Paging;
import org.mcxiaoke.fancooker.dao.model.StatusModel;


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
