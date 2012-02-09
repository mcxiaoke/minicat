package com.fanfou.app.hd.api;

import android.util.Log;

import com.fanfou.app.hd.App;

/**
 * @author mcxiaoke
 * @version 1.0 2011.12.15
 * 
 */
public enum ApiConfig{

	NULL(""),

	TEST("/test"),

	ACCOUNT_REGISTER("/account/register"),
	// POST or GET
	ACCOUNT_VERIFY_CREDENTIALS("/account/verify_credentials"), ACCOUNT_RATE_LIMIT_STATUS(
			"/account/rate_limit_status"),
	// POST
	ACCOUNT_UPDATE_PROFILE("/account/update_profile"),
	// post
	ACCOUNT_UPDATE_PROFILE_IMAGE("/account/update_profile_image"), ACCOUNT_NOTIFICATION(
			"/account/notification"),

	STATUSES_HOME_TIMELINE("/statuses/home_timeline"), STATUSES_MENTIONS(
			"/statuses/mentions"), STATUSES_USER_TIMELINE(
			"/statuses/user_timeline"), STATUSES_CONTEXT_TIMELINE(
			"/statuses/context_timeline"), STATUSES_PUBLIC_TIMELINE(
			"/statuses/public_timeline"),

	STATUSES_SHOW("/statuses/show/%s"),
	// POST
	STATUSES_UPDATE("/statuses/update"),
	// POST
	STATUSES_DESTROY("/statuses/destroy/%s"),

	DIRECT_MESSAGES_INBOX("/direct_messages/inbox"), DIRECT_MESSAGES_OUTBOX(
			"/direct_messages/sent"), DIRECT_MESSAGES_CONVERSTATION_LIST(
			"/direct_messages/conversation_list"), DIRECT_MESSAGES_CONVERSTATION(
			"/direct_messages/conversation"),
	// POST
	DIRECT_MESSAGES_CREATE("/direct_messages/new"),
	// POST
	DIRECT_MESSAGES_DESTROY("/direct_messages/destroy/%s"),

	USERS_SHOW("/users/show"), USERS_FRIENDS("/users/friends"), USERS_FOLLOWERS(
			"/users/followers"),

	// POST
	FRIENDSHIPS_CREATE("/friendships/create/%s"),
	// POST
	FRIENDSHIPS_DESTROY("/friendships/destroy/%s"), FRIENDSHIPS_EXISTS(
			"/friendships/exists"), FRIENDSHIPS_SHOW("/friendships/show"), FRIENDSHIPS_REQUESTS(
			"/friendships/requests"), FRIENDSHIPS_DENY("/friendships/deny"), FRIENDSHIPS_ACCEPT(
			"/friendships/accept"),

	BLOCKS("/blocks/blocking"), BLOCKS_IDS("/blocks/ids"),
	// POST
	BLOCKS_CREATE("/blocks/create/%s"),
	// POST
	BLOCKS_DESTROY("/blocks/destroy/%s"), BLOCKS_EXISTS("/blocks/exists"),

	FRIENDS_IDS("/friends/ids"), FOLLOWERS_IDS("/followers/ids"),

	FAVORITES_LIST("/favorites/list"),
	// POST
	FAVORITES_CREATE("/favorites/create/%s"),
	// POST
	FAVORITES_DESTROY("/favorites/destroy/%s"),

	PHOTOS_USER_TIMELINE("/photos/user_timeline"),
	// POST
	PHOTOS_UPLOAD("/photos/upload"),

	SEARCH_PUBLIC_TIMELINE("search/public_timeline"), SEARCH_USER_TIMELINE(
			"/search/user_timeline"), SEARCH_USERS("/search/users"),

	SAVED_SEARCHES_LIST("/saved_searches/list"), SAVED_SEARCHES_SHOW(
			"/saved_searches/show"),
	// POST
	SAVED_SEARCHES_CREATE("/saved_searches/create"),
	// POST
	SAVED_SEARCHES_DESTROY("/saved_searches/destroy"),

	TRENDS_LIST("/trends/list"),

	;

	private static final String TAG = ApiConfig.class.getSimpleName();
	private static final String API_HOST = "http://api.fanfou.com";
	private static final String JSON = ".json";

	private final String path;
	private final String url;

	private ApiConfig(String path) {
		this.path = path;
		this.url = API_HOST + path + JSON;
	}

	public String url() {
		if (App.DEBUG) {
			Log.d(TAG, "url=" + url);
		}
		return url;
	}
	
	public String path(){
		return path;
	}

	@Override
	public String toString() {
		return ordinal()+":"+name();
	}

}
