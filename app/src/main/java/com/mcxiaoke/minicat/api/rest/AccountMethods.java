package com.mcxiaoke.minicat.api.rest;

import com.mcxiaoke.minicat.api.ApiException;
import com.mcxiaoke.minicat.dao.model.Notifications;
import com.mcxiaoke.minicat.dao.model.RateLimitStatus;
import com.mcxiaoke.minicat.dao.model.UserModel;

import java.io.File;


/**
 * @author mcxiaoke
 * @version 1.0 2012.02.23
 */
public interface AccountMethods {

    public UserModel verifyCredentials() throws ApiException;

    public UserModel updateProfile(String url, String location,
                                   String description, String name) throws ApiException;

    public UserModel updateProfileImage(File image) throws ApiException;

    public RateLimitStatus getRateLimitStatus() throws ApiException;

    public Notifications getNotifications() throws ApiException;

}
