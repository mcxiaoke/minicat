package com.mcxiaoke.minicat.api.rest;

import com.mcxiaoke.minicat.api.ApiException;
import org.oauthsimple.model.OAuthToken;

import java.io.IOException;

/**
 * @author mcxiaoke
 * @version 1.1 2012.02.27
 */
public interface OAuthMethods {

    public String getAccount();

    public void setAccount(String account);

    public OAuthToken getOAuthRequestToken() throws ApiException;

    public OAuthToken getOAuthAccessToken(String username, String password)
            throws IOException, ApiException;

    public void setAccessToken(OAuthToken token);

}
