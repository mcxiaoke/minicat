/**
 * 
 */
package com.mcxiaoke.fanfouapp.api.rest;

import com.mcxiaoke.fanfouapp.api.ApiException;
import com.mcxiaoke.fanfouapp.api.Paging;
import com.mcxiaoke.fanfouapp.dao.model.StatusModel;

import java.io.File;
import java.util.List;


/**
 * @author mcxiaoke
 * @version 1.0 2012-2-23 上午10:13:38
 *
 */
public interface PhotosMethods {
	
	public List<StatusModel> getPhotosTimeline(String id, Paging paging) throws ApiException;
	public StatusModel uploadPhoto(File photo, String status, String location) throws ApiException;

}
