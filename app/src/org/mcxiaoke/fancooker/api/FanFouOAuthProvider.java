package org.mcxiaoke.fancooker.api;

import org.mcxiaoke.fancooker.auth.OAuthProvider;

/**
 * @author mcxiaoke
 * @version 1.0 2011.11.30
 * 
 */
class FanFouOAuthProvider implements OAuthProvider {
	// private static String key;
	// private static String secret;

	// CONSUMER_KEY c403d5a51bde9cce58fe31f4cec06b0a
	// ECF318CA13B0E89CF847BC48BCBAECD64082AC3B7FE78C8D21B4FB5D98861344FE0687E249EBF374
	// CONSUMER_SECRET 5da0bcda353f7d2fe8e3de01e3c97741
	// 1EABD7AA1DD250E7981D6D9CE7E07DC907645E76ABEBF14B4E524F6D36597D3AFE0687E249EBF374
	// SECURE_KEY = "g$#Tdg%$^mc[54jxiaoke";
	// 这两个是反的，加密后，而且缺少最后一节
	// public static final String CONSUMER_KEY =
	// "1EABD7AA1DD250E7981D6D9CE7E07DC907645E76ABEBF14B4E524F6D36597D3A";
	// public static final String CONSUMER_SECRET =
	// "ECF318CA13B0E89CF847BC48BCBAECD64082AC3B7FE78C8D21B4FB5D98861344";
	private static final String CONSUMER_KEY = "84d8fa6c95047475c9a14aae1f53f7b2";
	private static final String CONSUMER_SECRET = "4623b62a6d6a911533093987406549d0";
	private static final String REQUEST_TOKEN_URL="http://fanfou.com/oauth/request_token";
	private static final String AUTHORIZE_URL="http://fanfou.com/oauth/authorize";
	private static final String ACCESS_TOKEN_URL="http://fanfou.com/oauth/access_token";

	@Override
	public String getRequestTokenURL() {
		return REQUEST_TOKEN_URL;
	}

	@Override
	public String getAuthorizeURL() {
		return AUTHORIZE_URL;
	}

	@Override
	public String getAccessTokenURL() {
		return ACCESS_TOKEN_URL;
	}

	@Override
	public String getConsumerKey() {
		// if (key == null) {
		// String encoded = CONSUMER_SECRET + OAuthHelper.KEY_SUFFIX;
		// key = CryptoHelper.getInstance().decode(encoded);
		// }
		// return key;
		return CONSUMER_KEY;
	}

	@Override
	public String getConsumerSercret() {
		// if (secret == null) {
		// String encoded = CONSUMER_KEY + OAuthHelper.KEY_SUFFIX;
		// secret = CryptoHelper.getInstance().decode(encoded);
		// }
		// return secret;
		return CONSUMER_SECRET;
	}

	/* (non-Javadoc)
	 * @see com.fanfou.app.hd.auth.OAuthProvider#getApiHostURL()
	 */
	@Override
	public String getApiHostURL() {
		// TODO Auto-generated method stub
		return "http://api.fanfou.com";
	}

}
