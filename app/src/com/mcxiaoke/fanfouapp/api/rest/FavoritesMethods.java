/**
 * 
 */
package com.mcxiaoke.fanfouapp.api.rest;

import com.mcxiaoke.fanfouapp.api.ApiException;
import com.mcxiaoke.fanfouapp.api.Paging;
import com.mcxiaoke.fanfouapp.dao.model.StatusModel;

import java.util.List;


/**
 * @author mcxiaoke
 * @version 1.0 2012-2-24 上午10:31:08
 *
 */
public interface FavoritesMethods {
	public List<StatusModel> getFavorites(String id, Paging paging) throws ApiException;
	public StatusModel favorite(String id) throws ApiException;
	public StatusModel unfavorite(String id) throws ApiException;
}
