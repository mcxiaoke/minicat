/**
 * 
 */
package com.fanfou.app.hd.api.rest;

import java.io.File;
import java.util.List;

import com.fanfou.app.hd.api.ApiException;
import com.fanfou.app.hd.api.Paging;
import com.fanfou.app.hd.dao.model.StatusModel;

/**
 * @author mcxiaoke
 * @version 1.0 2012-2-23 上午10:13:38
 *
 */
public interface PhotosMethods {
	
	public List<StatusModel> getPhotosTimeline(String id, Paging paging) throws ApiException;
	public StatusModel uploadPhoto(File photo, String status, String location) throws ApiException;

}
