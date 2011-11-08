package com.fanfou.app.auth;

import com.fanfou.app.api.User;

/**
 * @author mcxiaoke
 * @version 1.0 2011.10.18
 * 
 */
public class AuthenticationManager {

	public AuthenticationManager() {
	}

	public interface AuthListener {
		void onAuthSuccess(int statusCode, User user);

		void onAuthFailed(int statusCode, String errorMessage);
	}

}
