/**
 * 
 */
package com.fanfou.app.hd.api.rest;

import java.util.List;

import com.fanfou.app.hd.api.ApiException;
import com.fanfou.app.hd.api.Paging;
import com.fanfou.app.hd.dao.model.StatusModel;

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
