package com.mcxiaoke.minicat.api.rest;

import com.mcxiaoke.minicat.api.ApiException;
import com.mcxiaoke.minicat.api.Paging;
import com.mcxiaoke.minicat.dao.model.StatusModel;

import java.util.List;


/**
 * @author mcxiaoke
 * @version 1.0 2012.02.22
 */
public interface TimelineMethods {

    public List<StatusModel> getHomeTimeline(Paging paging) throws ApiException;

    public List<StatusModel> getMentions(Paging paging) throws ApiException;

    public List<StatusModel> getPublicTimeline() throws ApiException;

    public List<StatusModel> getUserTimeline(String userId, Paging paging) throws ApiException;

    public List<StatusModel> getContextTimeline(String contextId) throws ApiException;

}
