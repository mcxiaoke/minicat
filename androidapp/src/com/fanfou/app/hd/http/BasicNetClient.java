package com.fanfou.app.hd.http;

/**
 * @author mcxiaoke
 * @version 1.0 2011.12.01
 * @version 1.1 2011.12.02
 * 
 * 
 */
class BasicNetClient extends NetClient {
	private String username;
	private String password;
	private String authorization;

	private BasicNetClient(String username, String password) {
		super();
		this.username = username;
		this.password = password;
		createBasicAuthHeader();
	}

	static final BasicNetClient newInstance(String username, String password) {
		return new BasicNetClient(username, password);
	}

	public void setUsernameAndPassword(String username, String password) {
		this.username = username;
		this.password = password;
		createBasicAuthHeader();
	}

	@Override
	protected void signRequest(final NetRequest cr) {
		cr.request.addHeader("Authorization", authorization);

	}

	private void createBasicAuthHeader() {
		authorization = "Basic "
				+ com.fanfou.app.hd.util.Base64
						.encodeBytes((username + ":" + password).getBytes());
	}
}
