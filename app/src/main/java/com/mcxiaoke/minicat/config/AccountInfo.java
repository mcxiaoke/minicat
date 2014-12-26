/**
 *
 */
package com.mcxiaoke.minicat.config;

import org.oauthsimple.model.OAuthToken;

/**
 * @author mcxiaoke
 * @version 1.0 2012-2-27 上午9:55:59
 */
public class AccountInfo {

    private String account;
    private String screenName;
    private String profileImage;
    private String loginName;
    private String loginPassword;
    private String token;
    private String tokenSecret;
    private OAuthToken accessToken;

    private static boolean isNotEmpty(String text) {
        return text != null && text.length() > 0;
    }

    public void setAccountInfo(String account, String screenName) {
        this.account = account;
        this.screenName = screenName;
    }

    public void setAccountInfo(String account, String screenName,
                               String profileImage) {
        this.account = account;
        this.screenName = screenName;
        this.profileImage = profileImage;
    }

    public void setTokenAndSecret(String token, String tokenSecret) {
        this.token = token;
        this.tokenSecret = tokenSecret;
        if (isNotEmpty(token) && isNotEmpty(tokenSecret)) {
            this.accessToken = new OAuthToken(token, tokenSecret);
        } else {
            this.accessToken = null;
        }
    }

    public void setLoginInfo(String loginName, String loginPassword) {
        this.loginName = loginName;
        this.loginPassword = loginPassword;
    }

    public String getAccount() {
        return account;
    }

    public void setAccount(String account) {
        this.account = account;
    }

    public String getScreenName() {
        return screenName;
    }

    public void setScreenName(String screenName) {
        this.screenName = screenName;
    }

    public String getProfileImage() {
        return profileImage;
    }

    public void setProfileImage(String profileImage) {
        this.profileImage = profileImage;
    }

    public String getLoginName() {
        return loginName;
    }

    public void setLoginName(String loginName) {
        this.loginName = loginName;
    }

    public String getLoginPassword() {
        return loginPassword;
    }

    public void setLoginPassword(String loginPassword) {
        this.loginPassword = loginPassword;
    }

    public String getToken() {
        return token;
    }

    public String getTokenSecret() {
        return tokenSecret;
    }

    public OAuthToken getAccessToken() {
        return new OAuthToken(token, tokenSecret);
    }

    public void setAccessToken(OAuthToken accessToken) {
        this.token = accessToken.getToken();
        this.tokenSecret = accessToken.getSecret();
    }

    public boolean isVerified() {
        return !isEmpty(token) || !isEmpty(tokenSecret) || !isEmpty(account);
    }

    private boolean isEmpty(String text) {
        return text == null || text.equals("");
    }

    public void clear() {
        this.account = null;
        this.screenName = null;
        this.profileImage = null;
        this.loginName = null;
        this.loginPassword = null;
        this.token = null;
        this.tokenSecret = null;
    }

    @Override
    public String toString() {
        return "AccountInfo [account=" + account + ", screenName=" + screenName
                + ", profileImage=" + profileImage + ", loginName=" + loginName
                + ", loginPassword=" + loginPassword + ", token=" + token
                + ", tokenSecret=" + tokenSecret + ", accessToken="
                + accessToken + "]";
    }

}
