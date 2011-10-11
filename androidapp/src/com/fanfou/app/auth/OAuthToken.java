package com.fanfou.app.auth;

import java.io.IOException;

import javax.crypto.spec.SecretKeySpec;

import org.apache.http.ParseException;

import com.fanfou.app.api.ApiException;
import com.fanfou.app.http.Response;

public class OAuthToken implements java.io.Serializable {

	private static final long serialVersionUID = 3891133932519746686L;
	private String token;
	private String tokenSecret;
	private transient SecretKeySpec secretKeySpec;
	String[] responseStr = null;

	public OAuthToken(String token, String tokenSecret) {
		this.token = token;
		this.tokenSecret = tokenSecret;
	}

	public OAuthToken(Response response) throws ApiException, ParseException,
			IOException {
		this(response.getContent());
	}

	public OAuthToken(String string) {
		// Log.e("====================================", string);
		responseStr = string.split("&");
		tokenSecret = getParameter("oauth_token_secret");
		token = getParameter("oauth_token");
	}

	public String getToken() {
		return token;
	}

	public String getTokenSecret() {
		return tokenSecret;
	}

	void setSecretKeySpec(SecretKeySpec secretKeySpec) {
		this.secretKeySpec = secretKeySpec;
	}

	SecretKeySpec getSecretKeySpec() {
		return secretKeySpec;
	}

	public String getParameter(String parameter) {
		String value = null;
		try {
			for (String str : responseStr) {
				if (str.startsWith(parameter + '=')) {
					value = str.split("=")[1].trim();
					break;
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return value;
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
				+ tokenSecret + '\'' + ", secretKeySpec=" + secretKeySpec + '}';
	}
}
