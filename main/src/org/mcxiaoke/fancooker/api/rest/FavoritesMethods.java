/**
 * 
 */
package org.mcxiaoke.fancooker.api.rest;

import java.util.List;

import org.mcxiaoke.fancooker.api.ApiException;
import org.mcxiaoke.fancooker.api.Paging;
import org.mcxiaoke.fancooker.dao.model.StatusModel;


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
