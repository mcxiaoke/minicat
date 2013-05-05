package org.mcxiaoke.fancooker.api.rest;

import java.io.File;

import org.mcxiaoke.fancooker.api.ApiException;
import org.mcxiaoke.fancooker.dao.model.Notifications;
import org.mcxiaoke.fancooker.dao.model.RateLimitStatus;
import org.mcxiaoke.fancooker.dao.model.UserModel;


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
