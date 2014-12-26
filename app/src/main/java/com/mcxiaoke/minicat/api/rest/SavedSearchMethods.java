/**
 *
 */
package com.mcxiaoke.minicat.api.rest;

import com.mcxiaoke.minicat.api.ApiException;
import com.mcxiaoke.minicat.dao.model.Search;

import java.util.List;


/**
 * @author mcxiaoke
 * @version 1.0 2012-2-23 上午10:09:02
 */
public interface SavedSearchMethods {

    public List<Search> getSavedSearches() throws ApiException;

    public Search showSavedSearch(String id) throws ApiException;

    public Search createSavedSearch(String query) throws ApiException;

    public Search deleteSavedSearch(String id) throws ApiException;


}
