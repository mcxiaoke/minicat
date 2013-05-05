/**
 * 
 */
package org.mcxiaoke.fancooker.api.rest;

import java.util.List;

import org.mcxiaoke.fancooker.api.ApiException;
import org.mcxiaoke.fancooker.dao.model.Search;


/**
 * @author mcxiaoke
 * @version 1.0 2012-2-23 上午10:13:02
 *
 */
public interface TrendsMethods {
	
	public List<Search> getTrends() throws ApiException;

}
