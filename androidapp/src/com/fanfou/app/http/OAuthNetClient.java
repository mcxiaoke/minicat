package com.fanfou.app.http;

import com.fanfou.app.auth.OAuthService;

/**
 * @author mcxiaoke
 * @version 1.0 2011.12.01
 *
 */
public class OAuthNetClient extends NetClient {
	private OAuthService mOAuth;
	
	private OAuthNetClient(OAuthService oauth) {
		super();
		this.mOAuth=oauth;
	}
	
	public static final OAuthNetClient newInstance(OAuthService oauth) {
		return new OAuthNetClient(oauth);
	}
	
	
	public void setOAuthService(OAuthService oauth){
		this.mOAuth=oauth;
	}

	@Override
	protected void signRequest(final NetRequest cr) {
		if(mOAuth!=null){
			mOAuth.signRequest(cr.request, cr.params);
		}
	}

}
