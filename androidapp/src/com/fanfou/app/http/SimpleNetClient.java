package com.fanfou.app.http;

/**
 * @author mcxiaoke
 * @version 1.0 2011.12.02
 * 
 */
public class SimpleNetClient extends AbstractNetClient {
	
	
	public static SimpleNetClient newInstance(){
		return new SimpleNetClient();
	}

	SimpleNetClient() {
		super();
	}

	@Override
	protected void signRequest(NetRequest cr) {
	}

}
