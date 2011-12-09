package com.fanfou.app.http;

import com.fanfou.app.auth.OAuthService;

/**
 * @author mcxiaoke
 * @version 1.0 2011.12.01
 * @version 1.1 2011.12.05
 * @version 1.2 2011.12.06
 * 
 */
public class OAuthNetClient extends NetClient {
	private OAuthService mOAuth=null;
	private static OAuthNetClient sClient=new OAuthNetClient();

	private OAuthNetClient() {
		super();
	}
	
	public static OAuthNetClient getInstance() {
		return sClient;
	}

	public void setOAuthService(OAuthService oauth) {
		this.mOAuth = oauth;
	}

	@Override
	protected void signRequest(final NetRequest cr) {
		if (mOAuth != null) {
			mOAuth.signRequest(cr.request, cr.getParams());
		}
	}

}
