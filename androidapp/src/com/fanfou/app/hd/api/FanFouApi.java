/**
 * 
 */
package com.fanfou.app.hd.api;

import java.io.File;
import java.io.IOException;
import java.util.BitSet;
import java.util.List;

import org.apache.http.Header;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpUriRequest;

import android.util.Log;

import com.fanfou.app.hd.App;
import com.fanfou.app.hd.auth.AccessToken;
import com.fanfou.app.hd.auth.OAuthProvider;
import com.fanfou.app.hd.auth.OAuthService;
import com.fanfou.app.hd.auth.RequestToken;
import com.fanfou.app.hd.auth.exception.AuthException;
import com.fanfou.app.hd.dao.model.DirectMessageModel;
import com.fanfou.app.hd.dao.model.Notifications;
import com.fanfou.app.hd.dao.model.RateLimitStatus;
import com.fanfou.app.hd.dao.model.Search;
import com.fanfou.app.hd.dao.model.StatusModel;
import com.fanfou.app.hd.dao.model.UserModel;
import com.fanfou.app.hd.http.NetHelper;
import com.fanfou.app.hd.http.RestRequest;
import com.fanfou.app.hd.http.RestResponse;

/**
 * @author mcxiaoke
 * @version 1.0 2012-2-23 上午10:22:32
 * @version 1.1 2012.02.24
 * @version 1.5 2012.02.27
 * @version 1.6 2012.03.02
 * 
 */
class FanFouApi implements Api {
	private static final String TAG = "API";
	private static final String API_HOST = "http://api.fanfou.com";

	private static final boolean DEBUG = App.DEBUG;
	private OAuthService mOAuthService;
	private OAuthProvider mOAuthProvider;
	private AccessToken mAccessToken;
	private ApiParser mParser;
	private String account;

	public FanFouApi() {
		initialize(null);
	}

	public FanFouApi(AccessToken token) {
		initialize(token);
	}

	private void initialize(AccessToken token) {
		this.mOAuthProvider = new FanFouOAuthProvider();
		this.mOAuthService = new OAuthService(mOAuthProvider, mAccessToken);
		this.mParser = new FanFouParser();
	}

	private void log(String message) {
		Log.d(TAG, message);
	}

	private String makeUrl(String url) {
		return new StringBuilder().append(API_HOST).append(url).append(".json")
				.toString();
	}

	private UserModel fetchUser(String url, String id, int type, boolean post)
			throws ApiException {
		RestRequest.Builder builder = RestRequest.newBuilder();
		builder.url(makeUrl(url)).id(id).post(post).mode("lite");
		return mParser.user(fetch(builder.build()), type, account);
	}

	private UserModel fetchUser(String url, int type, boolean post)
			throws ApiException {
		RestRequest.Builder builder = RestRequest.newBuilder();
		builder.url(makeUrl(url)).post(post).mode("lite");
		return mParser.user(fetch(builder.build()), type, account);
	}

	private List<UserModel> fetchUsers(String url, Paging paging, int type)
			throws ApiException {
		return fetchUsers(url, null, paging, type);
	}

	private List<UserModel> fetchUsers(String url, String userId,
			Paging paging, int type) throws ApiException {
		RestRequest.Builder builder = RestRequest.newBuilder();
		builder.url(makeUrl(url)).id(userId).mode("lite").format("html");
		if (paging != null) {
			builder.paging(paging);
		}
		return mParser.users(fetch(builder.build()), type, userId);
	}

	private List<StatusModel> fetchTimeline(String url, Paging paging,
			int type, String owner) throws ApiException {
		RestRequest.Builder builder = RestRequest.newBuilder();
		builder.url(makeUrl(url)).mode("lite").format("html");
		if (paging != null) {
			builder.paging(paging);
		}
		String response = fetch(builder.build());
		return mParser.timeline(response, type, owner);
	}

	private List<StatusModel> fetchTimeline(String url, Paging paging,
			String id, int type, String owner) throws ApiException {
		RestRequest.Builder builder = RestRequest.newBuilder();
		builder.url(makeUrl(url)).id(id).mode("lite").format("html");
		if (paging != null) {
			builder.paging(paging);
		}
		String response = fetch(builder.build());
		return mParser.timeline(response, type, owner);
	}

	private StatusModel fetchStatus(String url, int type, boolean post)
			throws ApiException {
		if (DEBUG) {
			Log.d(TAG, "fetchStatus url=" + url + " type=" + type + " post="
					+ post);
		}

		RestRequest.Builder builder = RestRequest.newBuilder();
		builder.url(makeUrl(url)).post(post).mode("lite").format("html");
		return mParser.status(fetch(builder.build()), type, account);
	}

	private String fetchDirectMessages(String url, Paging paging, int type)
			throws ApiException {
		RestRequest.Builder builder = new RestRequest.Builder();
		builder.url(makeUrl(url)).paging(paging).mode("lite");
		return fetch(builder.build());
	}

	@Override
	public void setParser(ApiParser parser) {
		assert (parser != null);
		this.mParser = parser;
		this.mParser.setAccount(account);
	}

	@Override
	public String getAccount() {
		return account;
	}

	@Override
	public void setAccount(String account) {
		this.account = account;
		if (mParser != null) {
			this.mParser.setAccount(account);
		}
	}

	@Override
	public synchronized void setAccessToken(AccessToken token) {
		this.mAccessToken = token;
		this.mOAuthService.setAccessToken(token);
	}

	@Override
	public AccessToken getAccessToken() {
		return mAccessToken;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see http://fanfou.com/oauth/request_token
	 * 
	 * @see https://github.com/FanfouAPI/FanFouAPIDoc/wiki/Oauth
	 */
	@Override
	public RequestToken getOAuthRequestToken() throws ApiException {
		try {
			return mOAuthService.getOAuthRequestToken();
		} catch (IOException e) {
			throw new ApiException(ApiException.IO_ERROR, e.getMessage(), e);
		} catch (AuthException e) {
			throw new ApiException(ApiException.AUTH_ERROR, e.getMessage(), e);
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see m.fanfou.com/oauth/authorize
	 * 
	 * @see https://github.com/FanfouAPI/FanFouAPIDoc/wiki/Oauth
	 */
	@Override
	public RequestToken getOAuthRequestToken(String callback)
			throws ApiException {
		try {
			return mOAuthService.getOAuthRequestToken(callback);
		} catch (IOException e) {
			throw new ApiException(ApiException.IO_ERROR, e.getMessage(), e);
		} catch (AuthException e) {
			throw new ApiException(ApiException.AUTH_ERROR, e.getMessage(), e);
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see http://fanfou.com/oauth/access_token
	 * 
	 * @see https://github.com/FanfouAPI/FanFouAPIDoc/wiki/Oauth
	 */
	@Override
	public AccessToken getOAuthAccessToken(RequestToken requestToken)
			throws ApiException {
		try {
			return mOAuthService.getOAuthAccessToken(requestToken);
		} catch (IOException e) {
			throw new ApiException(ApiException.IO_ERROR, e.getMessage(), e);
		} catch (AuthException e) {
			throw new ApiException(ApiException.AUTH_ERROR, e.getMessage(), e);
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see http://fanfou.com/oauth/access_token
	 * 
	 * @see https://github.com/FanfouAPI/FanFouAPIDoc/wiki/Oauth
	 */
	@Override
	public AccessToken getOAuthAccessToken(RequestToken requestToken,
			String verifier) throws ApiException {
		try {
			return mOAuthService.getOAuthAccessToken(requestToken, verifier);
		} catch (IOException e) {
			throw new ApiException(ApiException.IO_ERROR, e.getMessage(), e);
		} catch (AuthException e) {
			throw new ApiException(ApiException.AUTH_ERROR, e.getMessage(), e);
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see http://fanfou.com/oauth/access_token
	 * 
	 * @see https://github.com/FanfouAPI/FanFouAPIDoc/wiki/Xauth
	 */
	@Override
	public AccessToken getOAuthAccessToken(String username, String password)
			throws ApiException {
		try {
			return mOAuthService.getOAuthAccessToken(username, password);
		} catch (IOException e) {
			throw new ApiException(ApiException.IO_ERROR, e.getMessage(), e);
		} catch (AuthException e) {
			throw new ApiException(ApiException.AUTH_ERROR, e.getMessage(), e);
		}
	}

	/*
	 * GET/POST /account/verify_credentials
	 * 
	 * @see
	 * https://github.com/FanfouAPI/FanFouAPIDoc/wiki/account.verify-credentials
	 */
	@Override
	public UserModel verifyCredentials() throws ApiException {
		return fetchUser("/account/verify_credentials",
				UserModel.TYPE_NONE, false);
	}

	/*
	 * POST /account/update_profile
	 * 
	 * @see
	 * https://github.com/FanfouAPI/FanFouAPIDoc/wiki/account.update-profile
	 */
	@Override
	public UserModel updateProfile(String url, String location,
			String description, String name) throws ApiException {
		RestRequest.Builder builder = new RestRequest.Builder();
		builder.url(makeUrl("/account/update_profile")).post().mode("lite");
		builder.param("description", description);
		builder.param("name", name);
		builder.param("location", location);
		builder.param("url", url);
		return mParser.user(fetch(builder.build()), UserModel.TYPE_NONE,
				account);
	}

	/*
	 * POST /account/update_profile_image
	 * 
	 * @see
	 * https://github.com/FanfouAPI/FanFouAPIDoc/wiki/account.update-profile
	 * -image
	 */
	@Override
	public UserModel updateProfileImage(File image) throws ApiException {
		checkNotNull(image);
		RestRequest.Builder builder = new RestRequest.Builder();
		builder.url(makeUrl("/account/update_profile_image")).post();
		builder.param("image", image);
		return mParser.user(fetch(builder.build()), UserModel.TYPE_NONE,
				account);
	}

	/*
	 * GET /account/rate_limit_status
	 * 
	 * @see
	 * https://github.com/FanfouAPI/FanFouAPIDoc/wiki/account.rate-limit-status
	 */
	@Override
	public RateLimitStatus getRateLimitStatus() throws ApiException {
		return null;
	}

	/*
	 * GET /account/notification
	 * 
	 * @see https://github.com/FanfouAPI/FanFouAPIDoc/wiki/account.notification
	 */
	@Override
	public Notifications getNotifications() throws ApiException {
		return null;
	}

	/*
	 * GET /blocks/ids
	 * 
	 * @see https://github.com/FanfouAPI/FanFouAPIDoc/wiki/blocks.ids
	 */
	@Override
	public List<String> blockIDs() throws ApiException {
		RestRequest.Builder builder = new RestRequest.Builder();
		builder.url(makeUrl("/blocks/ids"));
		String response = fetch(builder.build());
		return mParser.strings(response);
	}

	/*
	 * GET /blocks/blocking
	 * 
	 * @see https://github.com/FanfouAPI/FanFouAPIDoc/wiki/blocks.blocking
	 */
	@Override
	public List<UserModel> blockUsers(Paging paging) throws ApiException {
		return fetchUsers("/blocks/blocking", paging, UserModel.TYPE_BLOCK);
	}

	/*
	 * GET /blocks/exists
	 * 
	 * @see https://github.com/FanfouAPI/FanFouAPIDoc/wiki/blocks.exists
	 */
	@Override
	public UserModel isBlocked(String id) throws ApiException {
		checkNotEmpty(id);
		return fetchUser("/blocks/exists", id, UserModel.TYPE_NONE, false);
	}

	/*
	 * POST /blocks/create
	 * 
	 * @see https://github.com/FanfouAPI/FanFouAPIDoc/wiki/blocks.create
	 */
	@Override
	public UserModel block(String id) throws ApiException {
		checkNotEmpty(id);
		String url = String.format("/blocks/create/%s", id);
		return fetchUser(url, UserModel.TYPE_BLOCK, true);
	}

	/*
	 * POST /blocks/destroy
	 * 
	 * @see https://github.com/FanfouAPI/FanFouAPIDoc/wiki/blocks.destroy
	 */
	@Override
	public UserModel unblock(String id) throws ApiException {
		checkNotEmpty(id);
		String url = String.format("/blocks/destroy/%s", id);
		return fetchUser(url, UserModel.TYPE_BLOCK, true);
	}

	/*
	 * GET /direct_messages/inbox
	 * 
	 * @see https://github.com/FanfouAPI/FanFouAPIDoc/wiki/direct-messages.inbox
	 */
	@Override
	public List<DirectMessageModel> getDirectMessagesInbox(Paging paging)
			throws ApiException {
		String response = fetchDirectMessages("/direct_messages/inbox", paging,
				DirectMessageModel.TYPE_INBOX);
		return mParser.directMessagesInBox(response);
	}

	/*
	 * GET /direct_messages/sent
	 * 
	 * @see https://github.com/FanfouAPI/FanFouAPIDoc/wiki/direct-messages.sent
	 */
	@Override
	public List<DirectMessageModel> getDirectMessagesOutbox(Paging paging)
			throws ApiException {
		String response = fetchDirectMessages("/direct_messages/sent", paging,
				DirectMessageModel.TYPE_OUTBOX);
		return mParser.directMessagesOutBox(response);
	}

	/*
	 * GET /direct_messages/conversation_list
	 * 
	 * @see
	 * https://github.com/FanfouAPI/FanFouAPIDoc/wiki/direct-messages.conversation
	 * -list
	 */
	@Override
	public List<DirectMessageModel> getConversationList(Paging paging)
			throws ApiException {
		String response = fetchDirectMessages(
				"/direct_messages/conversation_list", paging,
				DirectMessageModel.TYPE_CONVERSATION_LIST);
		return mParser.directMessagesConversationList(response);

	}

	/*
	 * GET /direct_messages/conversation
	 * 
	 * @see
	 * https://github.com/FanfouAPI/FanFouAPIDoc/wiki/direct-messages.conversation
	 */
	@Override
	public List<DirectMessageModel> getConversation(String id, Paging paging)
			throws ApiException {
		checkNotEmpty(id);
		RestRequest.Builder builder = new RestRequest.Builder();
		builder.url(makeUrl("/direct_messages/conversation")).id(id)
				.paging(paging).mode("lite");
		String response = fetch(builder.build());
		return mParser.directMessageConversation(response, id);
	}

	/*
	 * POST /direct_messages/destroy
	 * 
	 * @see
	 * https://github.com/FanfouAPI/FanFouAPIDoc/wiki/direct-messages.destroy
	 */
	@Override
	public DirectMessageModel deleteDirectMessage(String id)
			throws ApiException {
		checkNotEmpty(id);
		// String url = String
		// .format("/direct_messages/destroy/%s", id);
		RestRequest.Builder builder = new RestRequest.Builder();
		builder.url(makeUrl("/direct_messages/destroy")).post().mode("lite");
		String response = fetch(builder.build());
		return mParser.directMessage(response, DirectMessageModel.TYPE_OUTBOX);
	}

	/*
	 * POST /direct_messages/new
	 * 
	 * @see https://github.com/FanfouAPI/FanFouAPIDoc/wiki/direct-messages.new
	 */
	@Override
	public DirectMessageModel createDirectmessage(String id, String text,
			String replyId) throws ApiException {
		checkNotEmpty(id);
		checkNotEmpty(text);
		RestRequest.Builder builder = new RestRequest.Builder();
		builder.url(makeUrl("/direct_messages/new")).post();
		builder.param("user", id);
		builder.param("text", text);
		builder.param("in_reply_to_id", replyId);
		String response = fetch(builder.build());
		return mParser.directMessage(response, DirectMessageModel.TYPE_OUTBOX);
	}

	/*
	 * GET /friends/ids
	 * 
	 * @see https://github.com/FanfouAPI/FanFouAPIDoc/wiki/friends.ids
	 */
	@Override
	public List<String> getFriendsIDs(String id, Paging paging)
			throws ApiException {
		checkNotEmpty(id);
		RestRequest.Builder builder = new RestRequest.Builder();
		builder.url(makeUrl("/friends/ids")).id(id).paging(paging);
		return mParser.strings(fetch(builder.build()));
	}

	/*
	 * GET /followers/ids
	 * 
	 * @see https://github.com/FanfouAPI/FanFouAPIDoc/wiki/followers.ids
	 */
	@Override
	public List<String> getFollowersIDs(String id, Paging paging)
			throws ApiException {
		checkNotEmpty(id);
		RestRequest.Builder builder = new RestRequest.Builder();
		builder.url(makeUrl("/followers/ids")).id(id).paging(paging);
		return mParser.strings(fetch(builder.build()));
	}

	/*
	 * GET /users/friends
	 * 
	 * @see https://github.com/FanfouAPI/FanFouAPIDoc/wiki/users.friends
	 */
	@Override
	public List<UserModel> getFriends(String id, Paging paging)
			throws ApiException {
		return fetchUsers("/users/friends", id, paging, UserModel.TYPE_FRIENDS);
	}

	/*
	 * GET /users/followers
	 * 
	 * @see https://github.com/FanfouAPI/FanFouAPIDoc/wiki/users.followers
	 */
	@Override
	public List<UserModel> getFollowers(String id, Paging paging)
			throws ApiException {
		return fetchUsers("/users/followers", id, paging,
				UserModel.TYPE_FOLLOWERS);
	}

	/*
	 * POST /friendships/create
	 * 
	 * @see https://github.com/FanfouAPI/FanFouAPIDoc/wiki/friendships.create
	 */
	@Override
	public UserModel follow(String id) throws ApiException {
		checkNotEmpty(id);
		String url = String.format("/friendships/create/%s", id);
		return fetchUser(url, UserModel.TYPE_BLOCK, true);
	}

	/*
	 * POST /friendships/destroy
	 * 
	 * @see https://github.com/FanfouAPI/FanFouAPIDoc/wiki/friendships.destroy
	 */
	@Override
	public UserModel unfollow(String id) throws ApiException {
		checkNotEmpty(id);
		String url = String.format("//friendships/destroy/%s", id);
		return fetchUser(url, UserModel.TYPE_BLOCK, true);
	}

	/*
	 * GET /friendships/requests
	 * 
	 * @see https://github.com/FanfouAPI/FanFouAPIDoc/wiki/friendships.requests
	 */
	@Override
	public List<String> friendshipsRequests(Paging paging) throws ApiException {
		RestRequest.Builder builder = new RestRequest.Builder();
		builder.url(makeUrl("/blocks/ids")).paging(paging);
		String response = fetch(builder.build());
		return mParser.strings(response);
	}

	/*
	 * POST /friendships/accept
	 * 
	 * @see https://github.com/FanfouAPI/FanFouAPIDoc/wiki/friendships.accept
	 */
	@Override
	public UserModel acceptFriendshipsRequest(String id) throws ApiException {
		checkNotEmpty(id);
		return fetchUser("/friendships/accept", id, UserModel.TYPE_NONE, true);
	}

	/*
	 * POST /friendships/deny
	 * 
	 * @see https://github.com/FanfouAPI/FanFouAPIDoc/wiki/friendships.deny
	 */
	@Override
	public UserModel denyFriendshipsRequest(String id) throws ApiException {
		checkNotEmpty(id);
		return fetchUser("/friendships/deny", id, UserModel.TYPE_NONE, true);
	}

	/*
	 * GET /friendships/exists
	 * 
	 * @see https://github.com/FanfouAPI/FanFouAPIDoc/wiki/friendships.exists
	 */
	@Override
	public boolean isFriends(String userA, String userB) throws ApiException {
		checkNotEmpty(userA);
		checkNotEmpty(userB);
		RestRequest.Builder builder = new RestRequest.Builder();
		builder.url(makeUrl("/friendships/exists"));
		builder.param("user_a", userA);
		builder.param("user_b", userB);
		String response = fetch(builder.build());
		return response.contains("true");
	}

	/*
	 * GET /friendships/show
	 * 
	 * @see https://github.com/FanfouAPI/FanFouAPIDoc/wiki/friendships.show
	 */
	@Override
	public BitSet friendshipsShow(String source, String target)
			throws ApiException {
		checkNotEmpty(source);
		checkNotEmpty(target);
		RestRequest.Builder builder = new RestRequest.Builder();
		builder.url(makeUrl("/friendships/exists"));
		builder.param("source_id", source);
		builder.param("target_id", target);
		String response = fetch(builder.build());
		return FanFouParser.parseFriendship(response);
	}

	/*
	 * GET /photos/user_timeline
	 * 
	 * @see https://github.com/FanfouAPI/FanFouAPIDoc/wiki/photos.user-timeline
	 */
	@Override
	public List<StatusModel> getPhotosTimeline(String id, Paging paging)
			throws ApiException {
		checkNotEmpty(id);
		return fetchTimeline("/photos/user_timeline", paging, id,
				StatusModel.TYPE_PHOTO, account);
	}

	/*
	 * POST /photos/upload
	 * 
	 * @see https://github.com/FanfouAPI/FanFouAPIDoc/wiki/photos.upload
	 */
	@Override
	public StatusModel uploadPhoto(File photo, String status, String location)
			throws ApiException {
		checkNotNull(photo);
		RestRequest.Builder builder = new RestRequest.Builder();
		builder.url(makeUrl("/photos/upload")).post();
		builder.status(status).location(location);
		builder.param("photo", photo);
		builder.format("html").mode("lite");
		String response = fetch(builder.build());
		return mParser.status(response, StatusModel.TYPE_HOME, account);
	}

	/*
	 * GET /saved_searches/list
	 * 
	 * @see https://github.com/FanfouAPI/FanFouAPIDoc/wiki/saved-searches.list
	 */
	@Override
	public List<Search> getSavedSearches() throws ApiException {
		RestRequest.Builder builder = new RestRequest.Builder();
		builder.url(makeUrl("/saved_searches/list"));
		String response = fetch(builder.build());
		return mParser.savedSearches(response);
	}

	/*
	 * GET /saved_searches/show
	 * 
	 * @see https://github.com/FanfouAPI/FanFouAPIDoc/wiki/saved-searches.show
	 */
	@Override
	public Search showSavedSearch(String id) throws ApiException {
		checkNotEmpty(id);
		RestRequest.Builder builder = new RestRequest.Builder();
		builder.url(makeUrl("/saved_searches/show")).id(id);
		String response = fetch(builder.build());
		return mParser.savedSearch(response);
	}

	/*
	 * POST /saved_searches/create
	 * 
	 * @see https://github.com/FanfouAPI/FanFouAPIDoc/wiki/saved-searches.create
	 */
	@Override
	public Search createSavedSearch(String query) throws ApiException {
		checkNotEmpty(query);
		RestRequest.Builder builder = new RestRequest.Builder();
		builder.url(makeUrl("/saved_searches/create")).param("query", query)
				.post();
		String response = fetch(builder.build());
		return mParser.savedSearch(response);
	}

	/*
	 * POST /saved_searches/destroy
	 * 
	 * @see
	 * https://github.com/FanfouAPI/FanFouAPIDoc/wiki/saved-searches.destroy
	 */
	@Override
	public Search deleteSavedSearch(String id) throws ApiException {
		checkNotEmpty(id);
		RestRequest.Builder builder = new RestRequest.Builder();
		builder.url(makeUrl("/saved_searches/destroy")).id(id).post();
		String response = fetch(builder.build());
		return mParser.savedSearch(response);
	}

	/*
	 * GET /trends/list
	 * 
	 * @see https://github.com/FanfouAPI/FanFouAPIDoc/wiki/trends.list
	 */
	@Override
	public List<Search> getTrends() throws ApiException {
		RestRequest.Builder builder = new RestRequest.Builder();
		builder.url(makeUrl("/trends/list"));
		String response = fetch(builder.build());
		return mParser.trends(response);
	}

	/*
	 * GET /search/public_timeline
	 * 
	 * @see
	 * https://github.com/FanfouAPI/FanFouAPIDoc/wiki/search.public-timeline
	 */
	@Override
	public List<StatusModel> search(String query, Paging paging)
			throws ApiException {
		checkNotEmpty(query);
		RestRequest.Builder builder = new RestRequest.Builder();
		builder.url(makeUrl("/search/public_timeline")).param("q", query);
		if (paging != null) {
			builder.paging(paging);
		}
		builder.mode("lite").format("html");
		String response = fetch(builder.build());
		return mParser.timeline(response, StatusModel.TYPE_SEARCH, account);
	}

	/*
	 * GET /search/user_timeline
	 * 
	 * @see https://github.com/FanfouAPI/FanFouAPIDoc/wiki/search.user-timeline
	 */
	@Override
	public List<StatusModel> searchUserTimeline(String query, String id,
			Paging paging) throws ApiException {
		checkNotEmpty(query);
		RestRequest.Builder builder = new RestRequest.Builder();
		builder.url(makeUrl("/search/user_timeline")).id(id).param("q", query);
		if (paging != null) {
			builder.paging(paging);
		}
		builder.mode("lite").format("html");
		String response = fetch(builder.build());
		return mParser.timeline(response, StatusModel.TYPE_SEARCH, account);
	}

	/*
	 * GET /search/users
	 * 
	 * @see https://github.com/FanfouAPI/FanFouAPIDoc/wiki/search.users
	 */
	@Override
	public List<UserModel> searchUsers(String query, Paging paging)
			throws ApiException {
		checkNotEmpty(query);
		RestRequest.Builder builder = new RestRequest.Builder();
		builder.url(makeUrl("/search/users")).param("q", query);
		if (paging != null) {
			builder.paging(paging);
		}
		builder.mode("lite").format("html");
		String response = fetch(builder.build());
		return mParser.users(response, UserModel.TYPE_SEARCH, account);
	}

	/*
	 * GET /statuses/show
	 * 
	 * @see https://github.com/FanfouAPI/FanFouAPIDoc/wiki/statuses.show
	 */
	@Override
	public StatusModel showStatus(String id) throws ApiException {
		checkNotEmpty(id);
		String url = String.format("/statuses/show/%s", id);
		return fetchStatus(url, StatusModel.TYPE_NONE, false);
	}

	/*
	 * POST /statuses/destroy
	 * 
	 * @see https://github.com/FanfouAPI/FanFouAPIDoc/wiki/statuses.destroy
	 */
	@Override
	public StatusModel deleteStatus(String id) throws ApiException {
		checkNotEmpty(id);
		String url = String.format("/statuses/destroy/%s", id);
		return fetchStatus(url, StatusModel.TYPE_NONE, true);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.fanfou.app.hd.api.rest.StatusMethods#retweetStatus(java.lang.String,
	 * boolean)
	 */
	@Override
	public StatusModel retweetStatus(String id) throws ApiException {
		return null;
	}

	/*
	 * POST /statuses/update
	 * 
	 * @see https://github.com/FanfouAPI/FanFouAPIDoc/wiki/statuses.update
	 */
	@Override
	public StatusModel updateStatus(String status, String replyId,
			String repostId, String location) throws ApiException {
		checkNotEmpty(status);
		RestRequest.Builder builder = new RestRequest.Builder();
		builder.url(makeUrl("/statuses/update")).post();
		builder.status(status).location(location);
		builder.format("html").mode("lite");
		builder.param("in_reply_to_status_id", replyId);
		builder.param("repost_status_id", repostId);
		builder.param("location", location);
		String response = fetch(builder.build());
		return mParser.status(response, StatusModel.TYPE_HOME, account);
	}

	/*
	 * GET /statuses/home_timeline
	 * 
	 * @see
	 * https://github.com/FanfouAPI/FanFouAPIDoc/wiki/statuses.home-timeline
	 */
	@Override
	public List<StatusModel> getHomeTimeline(Paging paging) throws ApiException {
		return fetchTimeline("/statuses/home_timeline", paging,
				StatusModel.TYPE_HOME, account);
	}

	/*
	 * GET /statuses/mentions
	 * 
	 * @see https://github.com/FanfouAPI/FanFouAPIDoc/wiki/statuses.mentions
	 */
	@Override
	public List<StatusModel> getMentions(Paging paging) throws ApiException {
		return fetchTimeline("/statuses/mentions", paging,
				StatusModel.TYPE_MENTIONS, account);
	}

	/*
	 * GET /statuses/public_timeline
	 * 
	 * @see
	 * https://github.com/FanfouAPI/FanFouAPIDoc/wiki/statuses.public-timeline
	 */
	@Override
	public List<StatusModel> getPublicTimeline() throws ApiException {
		return fetchTimeline("/statuses/public_timeline", null,
				StatusModel.TYPE_PUBLIC, account);
	}

	/*
	 * GET /statuses/user_timeline
	 * 
	 * @see
	 * https://github.com/FanfouAPI/FanFouAPIDoc/wiki/statuses.user-timeline
	 */
	@Override
	public List<StatusModel> getUserTimeline(String userId, Paging paging)
			throws ApiException {
		checkNotEmpty(userId);
		return fetchTimeline("/statuses/user_timeline", paging, userId,
				StatusModel.TYPE_USER, userId);
	}

	/*
	 * GET /statuses/context_timeline
	 * 
	 * @see
	 * https://github.com/FanfouAPI/FanFouAPIDoc/wiki/statuses.context-timeline
	 */
	@Override
	public List<StatusModel> getContextTimeline(String contextId)
			throws ApiException {
		checkNotEmpty(contextId);
		return fetchTimeline("/statuses/context_timeline", null, contextId,
				StatusModel.TYPE_USER, account);
	}

	/*
	 * GET /users/recommendation
	 * 
	 * @see https://github.com/FanfouAPI/FanFouAPIDoc/wiki/users.recommendation
	 */
	@Override
	public List<UserModel> getUserRecommendation(Paging paging)
			throws ApiException {
		return fetchUsers("/users/recommendation", paging, UserModel.TYPE_NONE);
	}

	/*
	 * POST /users/cancel_recommendation
	 * 
	 * @see
	 * https://github.com/FanfouAPI/FanFouAPIDoc/wiki/users.cancel-recommendation
	 */
	@Override
	public UserModel ignoreUserRecommendation(String id) throws ApiException {
		checkNotEmpty(id);
		return fetchUser("/users/cancel_recommendation", id,
				UserModel.TYPE_NONE, false);
	}

	/*
	 * GET /users/tagged
	 * 
	 * @see https://github.com/FanfouAPI/FanFouAPIDoc/wiki/users.tagged
	 */
	@Override
	public List<UserModel> getUsersByTag(String tag, Paging paging)
			throws ApiException {
		checkNotEmpty(tag);
		RestRequest.Builder builder = RestRequest.newBuilder();
		builder.url(makeUrl("/users/tagged")).param("tag", tag).mode("lite")
				.format("html");
		if (paging != null) {
			builder.paging(paging);
		}
		return mParser.users(fetch(builder.build()), UserModel.TYPE_NONE,
				account);
	}

	/*
	 * GET /users/tag_list
	 * 
	 * @see https://github.com/FanfouAPI/FanFouAPIDoc/wiki/users.tag-list
	 */
	@Override
	public List<String> getUserTags(String id) throws ApiException {
		checkNotEmpty(id);
		RestRequest.Builder builder = RestRequest.newBuilder();
		builder.url(makeUrl("/users/tag_list")).id(id);
		String response = fetch(builder.build());
		return mParser.strings(response);
	}

	public static void checkNotEmpty(String text) {
		if (text == null || text.length() == 0) {
			throw new IllegalArgumentException(
					"Parameter must not be null or empty string.");
		}
	}

	public static void checkNotNull(Object obj) {
		if (obj == null) {
			throw new IllegalArgumentException("Parameter must not be null.");
		}
	}

	/*
	 * GET /users/show
	 * 
	 * @see https://github.com/FanfouAPI/FanFouAPIDoc/wiki/users.show
	 */
	@Override
	public UserModel showUser(String id) throws ApiException {
		checkNotEmpty(id);
		String url = String.format("/users/show/%s", id);
		return fetchUser(url, UserModel.TYPE_BLOCK, false);
		
	}

	/*
	 * GET /favorites
	 * 
	 * @see https://github.com/FanfouAPI/FanFouAPIDoc/wiki/favorites
	 */
	@Override
	public List<StatusModel> getFavorites(String id, Paging paging)
			throws ApiException {
		checkNotEmpty(id);
		return fetchTimeline("/favorites", paging, StatusModel.TYPE_FAVORITES,
				id);
	}

	/*
	 * POST /favorites/create
	 * 
	 * @see https://github.com/FanfouAPI/FanFouAPIDoc/wiki/favorites.create
	 */
	@Override
	public StatusModel favorite(String id) throws ApiException {
		checkNotEmpty(id);
		String url = String.format("/favorites/create/%s", id);
		return fetchStatus(url, StatusModel.TYPE_NONE, true);
	}

	/*
	 * POST /favorites/destroy
	 * 
	 * @see https://github.com/FanfouAPI/FanFouAPIDoc/wiki/favorites.destroy
	 */
	@Override
	public StatusModel unfavorite(String id) throws ApiException {
		checkNotEmpty(id);
		String url = String.format("/favorites/destroy/%s", id);
		return fetchStatus(url, StatusModel.TYPE_NONE, true);
	}

	/*******************************************************************************
	 ******************************************************************************* 
	 ******************************************************************************* 
	 ******************************************************************************* 
	 * 
	 * Http Client for internal use.
	 * 
	 */

	/**
	 * @param request
	 * @return
	 * @throws ApiException
	 */
	private String fetch(final RestRequest nr) throws ApiException {
		try {

			if (mOAuthService != null) {
				mOAuthService.authorize(nr.request, nr.getParams());
			}
			HttpResponse response = execute(nr.request);
			RestResponse res = new RestResponse(response);

			int statusCode = res.statusCode;
			if (DEBUG) {
				log("fetch() url=" + nr.url + " post=" + nr.post
						+ " statusCode=" + statusCode);
			}
			if (statusCode == 200) {
				return res.getContent();
			}
			throw new ApiException(statusCode, FanFouParser.error(res
					.getContent()));
		} catch (IOException e) {
			if (DEBUG) {
				Log.e(TAG, e.toString());
			}
			throw new ApiException(ApiException.IO_ERROR, e.getMessage(),
					e.getCause());
		}
	}

	private HttpResponse execute(HttpUriRequest request) throws IOException {
		final HttpClient client = NetHelper.getHttpClient();
		if (App.DEBUG) {
			Log.d(TAG, "[Request] " + request.getRequestLine().toString()
					+ " --" + System.currentTimeMillis());
			Header[] headers = request.getAllHeaders();
			for (Header header : headers) {
				Log.d(TAG, "[Request Header] " + header.getName() + ":"
						+ header.getValue());
			}
		}
		HttpResponse response = client.execute(request);
		if (App.DEBUG) {
			Log.d(TAG, "[Response] " + response.getStatusLine().toString()
					+ " --" + System.currentTimeMillis());
			Header[] headers = response.getAllHeaders();
			for (Header header : headers) {
				Log.d(TAG, "[Response Header] " + header.getName() + ":"
						+ header.getValue());
			}
		}
		return response;
	}

}