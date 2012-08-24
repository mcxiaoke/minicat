/**
 * 
 */
package com.fanfou.app.hd.api.rest;

import java.util.List;

import com.fanfou.app.hd.api.ApiException;
import com.fanfou.app.hd.dao.model.Search;

/**
 * @author mcxiaoke
 * @version 1.0 2012-2-23 上午10:13:02
 *
 */
public interface TrendsMethods {
	
	public List<Search> getTrends() throws ApiException;

}
