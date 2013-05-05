package org.mcxiaoke.fancooker.auth;

import java.io.Serializable;

import javax.crypto.spec.SecretKeySpec;

abstract class OAuthToken implements Serializable {

	/**
	 *
	 */
	private static final long serialVersionUID = 2385887178385032767L;

	private String token;
	private String tokenSecret;

	private transient SecretKeySpec secretKeySpec;
	String[] responseStr = null;

	public OAuthToken(String token, String tokenSecret) {
		this.token = token;
		this.tokenSecret = tokenSecret;
	}

	public OAuthToken(String content) {
		responseStr = content.split("&");
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
		for (String str : responseStr) {
			if (str.startsWith(parameter + '=')) {
				value = str.split("=")[1].trim();
				break;
			}
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

		if (secretKeySpec != null ? !secretKeySpec.equals(that.secretKeySpec)
				: that.secretKeySpec != null)
			return false;
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
		result = 31 * result
				+ (secretKeySpec != null ? secretKeySpec.hashCode() : 0);
		return result;
	}

	@Override
	public String toString() {
		return "OAuthToken{" + "token='" + token + '\'' + ", tokenSecret='"
				+ tokenSecret + '\'' + ", secretKeySpec=" + secretKeySpec + '}';
	}
}
