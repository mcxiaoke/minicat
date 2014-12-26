/**
 *
 */
package com.mcxiaoke.minicat.api.rest;

import com.mcxiaoke.minicat.api.ApiException;
import com.mcxiaoke.minicat.dao.model.Search;

import java.util.List;


/**
 * @author mcxiaoke
 * @version 1.0 2012-2-23 上午10:13:02
 */
public interface TrendsMethods {

    public List<Search> getTrends() throws ApiException;

}
