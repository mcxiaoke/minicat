package com.fanfou.app.hd.api.rest;

import java.io.File;

import com.fanfou.app.hd.api.ApiException;
import com.fanfou.app.hd.dao.model.Notifications;
import com.fanfou.app.hd.dao.model.RateLimitStatus;
import com.fanfou.app.hd.dao.model.UserModel;

/**
 * @author mcxiaoke
 * @version 1.0 2012.02.23
 * 
 */
public interface AccountMethods {

	public UserModel verifyCredentials() throws ApiException;

	public UserModel updateProfile(String url, String location,
			String description, String name) throws ApiException;

	public UserModel updateProfileImage(File image) throws ApiException;

	public RateLimitStatus getRateLimitStatus() throws ApiException;

	public Notifications getNotifications() throws ApiException;

}
