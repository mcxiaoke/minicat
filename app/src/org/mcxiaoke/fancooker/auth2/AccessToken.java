package org.mcxiaoke.fancooker.auth2;

/**
 * @author mcxiaoke
 * @version 1.0 2012-2-23 上午10:38:16
 *
 */
public class AccessToken extends OAuthToken {
	private static final long serialVersionUID = 1L;
	public AccessToken(String token, String tokenSecret) {
		super(token, tokenSecret);
	}

	public AccessToken(String content) {
		super(content);
	}

}
