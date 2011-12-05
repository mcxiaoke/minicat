package com.fanfou.app.http;

import java.io.IOException;

import org.apache.http.HttpResponse;

/**
 * @author mcxiaoke
 * @version 1.0 2011.12.05
 * 
 */
public class OneTimeNetClient extends AbstractNetClient {
	private boolean used=false;
	
	public static OneTimeNetClient newInstance(){
		return new OneTimeNetClient();
	}

	OneTimeNetClient() {
		super();
	}

	@Override
	protected void signRequest(NetRequest cr) {
	}

	@Override
	public HttpResponse exec(NetRequest cr) throws IOException {
		if (used) {
			throw new IllegalStateException(
					"OneTimeNetClient can only be used once.");
		}
		used = true;
		return super.exec(cr);
	}

}
