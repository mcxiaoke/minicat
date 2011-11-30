package com.fanfou.app.auth;

import java.io.IOException;
import java.io.Serializable;

public class OAuthToken implements Serializable {

	private static final long serialVersionUID = 3891133932519746686L;
	private String token;
	private String tokenSecret;
	
	public OAuthToken(){	
	}

	public OAuthToken(String token, String tokenSecret) {
		this.token = token;
		this.tokenSecret = tokenSecret;
	}

	public static OAuthToken from(String response)  throws IOException{
		return parse(response);
	}

	private static OAuthToken parse(String response) {
		OAuthToken token=null;
		try {
			String[] strs = response.split("&");
			token=new OAuthToken();
			for (String str : strs) {
				if (str.startsWith("oauth_token=")) {
					token.setToken(str.split("=")[1].trim());
				} else if (str.startsWith("oauth_token_secret=")) {
					token.setTokenSecret(str.split("=")[1].trim());
				}
			}
		} catch (Exception e) {
		}
		return token;
	}

	public String getToken() {
		return token;
	}

	public void setToken(String token) {
		this.token = token;
	}

	public String getTokenSecret() {
		return tokenSecret;
	}

	public void setTokenSecret(String tokenSecret) {
		this.tokenSecret = tokenSecret;
	}
	
	public boolean isNull(){
		return token==null||tokenSecret==null;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o)
			return true;
		if (!(o instanceof OAuthToken))
			return false;

		OAuthToken that = (OAuthToken) o;

		if (!token.equals(that.token))
			return false;
		if (!tokenSecret.equals(that.tokenSecret))
			return false;

		return true;
	}

	@Override
	public int hashCode() {
		int result = token.hashCode();
		result = 31 * result + tokenSecret.hashCode();
		return result;
	}

	@Override
	public String toString() {
		return "OAuthToken{" + "token='" + token + '\'' + ", tokenSecret='"
				+ tokenSecret + '}';
	}
}
