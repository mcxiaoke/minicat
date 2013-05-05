/**
 * 
 */
package org.mcxiaoke.fancooker.api.rest;

import java.util.List;

import org.mcxiaoke.fancooker.api.ApiException;
import org.mcxiaoke.fancooker.dao.model.Search;


/**
 * @author mcxiaoke
 * @version 1.0 2012-2-23 上午10:09:02
 *
 */
public interface SavedSearchMethods {
	
	public List<Search> getSavedSearches() throws ApiException;
	public Search showSavedSearch(String id) throws ApiException;
	public Search createSavedSearch(String query) throws ApiException;
	public Search deleteSavedSearch(String id) throws ApiException;
	

}
