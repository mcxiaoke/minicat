/**
 * 
 */
package org.mcxiaoke.fancooker.api.rest;

import java.io.File;
import java.util.List;

import org.mcxiaoke.fancooker.api.ApiException;
import org.mcxiaoke.fancooker.api.Paging;
import org.mcxiaoke.fancooker.dao.model.StatusModel;


/**
 * @author mcxiaoke
 * @version 1.0 2012-2-23 上午10:13:38
 *
 */
public interface PhotosMethods {
	
	public List<StatusModel> getPhotosTimeline(String id, Paging paging) throws ApiException;
	public StatusModel uploadPhoto(File photo, String status, String location) throws ApiException;

}
