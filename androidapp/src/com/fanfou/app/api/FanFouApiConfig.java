package com.fanfou.app.api;

/**
 * @author mcxiaoke
 * @version 1.0 2011.05.02
 * @version 1.1 2011.05.04
 * @version 1.2 2011.05.15
 * @version 1.3 2011.10.18
 * @version 1.4 2011.11.04
 * @version 1.5 2011.11.07
 * @version 1.6 2011.11.07
 * @version 1.7 2011.11.11
 * @version 2.0 2011.11.18
 * @version 3.0 2011.11.21
 * 
 */
public interface FanFouApiConfig {

	public static final String CONSUMER_KEY = "c403d5a51bde9cce58fe31f4cec06b0a";
	public static final String CONSUMER_SECRET = "5da0bcda353f7d2fe8e3de01e3c97741";

	public static final String HOST = "http://fanfou.com/";
	public static final String API_BASE_DOMAIN = "http://api.fanfou.com/";
	public static final String API_BASE_IP = "http://58.83.129.19/";
	public static final String API_BASE = API_BASE_DOMAIN;
	public static final String EXTENSION = ".json";
	public static final int MAX_TIMELINE_COUNT = 60;
	public static final int DEFAULT_TIMELINE_COUNT = 20;
	public static final int MAX_USERS_COUNT = 100;
	public static final int DEFAULT_USERS_COUNT = 20;
	public static final int MAX_IDS_COUNT = 2000;
	
	public static final String FORMAT_HTML="html";
	public static final String MODE_LITE="lite";

	// verify account
	public static final String URL_VERIFY_CREDENTIALS = API_BASE
			+ "account/verify_credentials" + EXTENSION;

	// register
	public static final String URL_REGISTER = API_BASE + "account/register"
			+ EXTENSION;

	// public timeline
	// param count -- 1-20
	// param format -- format=html
	public static final String URL_TIMELINE_PUBLIC = API_BASE
			+ "statuses/public_timeline" + EXTENSION;

	// param id -- userid
	// param count -- 1-20
	// param since_id
	// param max_id
	// param page 1~
	// param format -- format=html
	// home timeline
	public static final String URL_TIMELINE_HOME = API_BASE
			+ "statuses/home_timeline" + EXTENSION;

	// show home timeline/replies/mentions
	public static final String URL_TIMELINE_USER = API_BASE
			+ "statuses/user_timeline" + EXTENSION;
	public static final String URL_TIMELINE_REPLIES = API_BASE
			+ "statuses/replies" + EXTENSION;
	public static final String URL_TIMELINE_MENTIONS = API_BASE
			+ "statuses/mentions" + EXTENSION;
	
	public static final String URL_TIMELINE_CONTEXT = API_BASE
	+ "statuses/context_timeline" + EXTENSION;

	// timeline contains photos
	public static final String URL_TIMELINE_PHOTOS = API_BASE
			+ "photos/user_timeline" + EXTENSION;

	// show a status, param id -- status id
	public static final String URL_STATUS_SHOW = API_BASE + "statuses/show/id"
			+ EXTENSION;

	// post a status
	// param status -- status content
	// param in_reply_to_status_id -- reply a status
	// param source -- api source
	// param location -- location string or latitude
	// param repost_status_id -- only for repost
	// POST METHOD
	public static final String URL_STATUS_UPDATE = API_BASE + "statuses/update"
			+ EXTENSION;

	// delete a status
	// param id -- status id
	// POST METHOD
	public static final String URL_STATUS_DESTROY = API_BASE
			+ "statuses/destroy/%s" + EXTENSION;

	// photo upload
	// param photo -- photo file
	// param status -- photo description
	// param source -- api source
	// param location -- optional
	// POST METHOD
	public static final String URL_PHOTO_UPLOAD = API_BASE + "photos/upload"
			+ EXTENSION;

	// user timeline only contains photos
	public static final String URL_PHOTO_USER_TIMELINE = API_BASE
			+ "photos/user_timeline" + EXTENSION;

	// search for public timeline
	// param q -- search keywords
	// param max_id -- max status id
	public static final String URL_SEARCH = API_BASE
			+ "search/public_timeline" + EXTENSION;
	
	public static final String URL_SEARCH_USERS = API_BASE
	+ "search/users" + EXTENSION;
	
	
	public static final String URL_TRENDS_LIST = API_BASE + "trends/list"
			+ EXTENSION;

	// show saved searches list
	public static final String URL_SAVED_SEARCHES_LIST = API_BASE + "saved_searches/list"
			+ EXTENSION;

	// show item in saved searches
	// param id -- keyword id
	public static final String URL_SAVED_SEARCHES_SHOW = API_BASE
			+ "saved_searches/show" + EXTENSION;

	// create a saved search
	// param query -- keyword to save
	public static final String URL_SAVED_SEARCHES_CREATE = API_BASE
			+ "saved_searches/create" + EXTENSION;

	// remove a saved search
	// param id -- saved search item id
	// POST METHOD
	public static final String URL_SAVED_SEARCHES_DESTROY = API_BASE
			+ "saved_searches/destroy" + EXTENSION;

	// show friends or followers or user profile
	// param id -- user id,optional
	public static final String URL_USERS_FRIENDS = API_BASE + "users/friends"
			+ EXTENSION;
	public static final String URL_USERS_FOLLOWERS = API_BASE
			+ "users/followers" + EXTENSION;
	public static final String URL_USER_SHOW = API_BASE + "users/show"
			+ EXTENSION;

	// add or delete a friend
	// param id -- userid
	// POST METHOD
	public static final String URL_FRIENDSHIPS_CREATE = API_BASE
			+ "friendships/create/%s" + EXTENSION;
	public static final String URL_FRIENDSHIPS_DESTROY = API_BASE
			+ "friendships/destroy/%s" + EXTENSION;

	// friendships exists?
	// param user_a -- user id
	// param user_b -- user_id
	public static final String URL_FRIENDSHIS_EXISTS = API_BASE
			+ "friendships/exists" + EXTENSION;

	// friends ids or followers ids
	// param id -- userid
	public static final String URL_USERS_FRIENDS_IDS = API_BASE + "friends/ids"
			+ EXTENSION;
	public static final String URL_USERS_FOLLOWERS_IDS = API_BASE
			+ "followers/ids" + EXTENSION;

	// show direct messages in outbox and inbox
	// param count -- 1-20
	// param since_id
	// param max_id
	// param page
	public static final String URL_DIRECT_MESSAGES_INBOX = API_BASE
			+ "direct_messages/inbox" + EXTENSION;
	public static final String URL_DIRECT_MESSAGES_OUTBOX = API_BASE
			+ "direct_messages/sent" + EXTENSION;

	// send direct message
	// param user -- recipient user id
	// param text -- message content
	// param in_reply_to_id -- in reply to a message
	// POST METHOD
	public static final String URL_DIRECT_MESSAGES_NEW = API_BASE
			+ "direct_messages/new" + EXTENSION;

	// delete a message
	// param id -- message id
	// POST METHOD
	public static final String URL_DIRECT_MESSAGES_DESTROY = API_BASE
			+ "direct_messages/destroy/%s" + EXTENSION;

	// show favorites
	// param id -- userid
	// param count
	// param page
	public static final String URL_FAVORITES_LIST = API_BASE + "favorites/list"
			+ EXTENSION;

	// favorite or unfavorite a status
	// param id -- status id
	// POST METHOD
	public static final String URL_FAVORITES_CREATE = API_BASE
			+ "favorites/create/%s" + EXTENSION;
	public static final String URL_FAVORITES_DESTROY = API_BASE
			+ "favorites/destroy/%s" + EXTENSION;

	// add or remove in blocks
	// param id --userid
	// POST METHOD
	public static final String URL_BLOCKS_CREATE = API_BASE + "blocks/create/%s"
			+ EXTENSION;
	public static final String URL_BLOCKS_DESTROY = API_BASE + "blocks/destroy/%s"
			+ EXTENSION;

	// show blocking list
	// param count count 0-60, default is 20
	// param page page >=0
	public static final String URL_BLOCKS_USERS = API_BASE + "blocks/blocking"
			+ EXTENSION;

	public static final String URL_BLOCKS_IDS = API_BASE + "blocks/ids"
			+ EXTENSION;

	// show user is or not in my blocks
	// param id userId
	// POST METHOD
	public static final String URL_BLOCKS_EXISTS = API_BASE + "blocks/exists"
			+ EXTENSION;

	// update my profile
	// param url
	// param location
	// param description
	// param name realname
	// param email email
	// POST METHOD
	public static final String URL_ACCOUNT_UPDATE_PROFILE = API_BASE
			+ "account/update_profile" + EXTENSION;

	// update my profile image
	// param image photo file
	// POST METHOD
	public static final String URL_ACCOUNT_UPDATE_PROFILE_IMAGE = API_BASE
			+ "account/update_profile_image" + EXTENSION;

}
