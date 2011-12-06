package com.fanfou.app.api;

import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.List;

import org.apache.http.HttpResponse;
import org.apache.http.protocol.HTTP;

import android.text.TextUtils;
import android.util.Log;

import com.fanfou.app.App;
import com.fanfou.app.auth.FanFouOAuthProvider;
import com.fanfou.app.auth.OAuthService;
import com.fanfou.app.auth.OAuthToken;
import com.fanfou.app.cache.CacheManager;
import com.fanfou.app.config.Commons;
import com.fanfou.app.http.NetRequest;
import com.fanfou.app.http.NetResponse;
import com.fanfou.app.http.OAuthNetClient;
import com.fanfou.app.http.ResponseCode;
import com.fanfou.app.util.StringHelper;

/**
 * @author mcxiaoke
 * @version 1.0 2011.05.15
 * @version 1.1 2011.05.17
 * @version 1.2 2011.10.28
 * @version 1.3 2011.11.04
 * @version 1.4 2011.11.07
 * @version 2.0 2011.11.07
 * @version 2.1 2011.11.09
 * @version 2.2 2011.11.11
 * @version 3.0 2011.11.18
 * @version 4.0 2011.11.21
 * @version 4.1 2011.11.22
 * @version 4.2 2011.11.23
 * @version 4.3 2011.11.28
 * @version 4.4 2011.11.29
 * @version 4.5 2011.11.30
 * @version 4.6 2011.12.01
 * @version 4.7 2011.12.02
 * @version 4.8 2011.12.05
 * @version 4.9 2011.12.06
 * 
 */
public class FanFouApi implements Api, FanFouApiConfig, ResponseCode {
	private static final String TAG = FanFouApi.class.getSimpleName();
	private OAuthNetClient conn;
	private OAuthService oauth;

	/**
	 * @param message
	 */
	private void log(String message) {
		Log.d(TAG, message);
	}

	private FanFouApi() {
		oauth = new OAuthService(new FanFouOAuthProvider());
		conn = OAuthNetClient.getInstance(oauth);
		setOAuthToken(App.getOAuthToken());
	}

	public static FanFouApi newInstance() {
		return new FanFouApi();
	}

	public void setOAuthToken(OAuthToken token) {
		oauth.setOAuthToken(token);
	}

	/**
	 * exec http request
	 * 
	 * @param request
	 * @return response object
	 * @throws ApiException
	 */
	private NetResponse fetch(final NetRequest request) throws ApiException {
		try {
			HttpResponse response = conn.exec(request);
			int statusCode = response.getStatusLine().getStatusCode();

			if (App.DEBUG) {
				log("fetch() url=" + request.url + " post=" + request.post
						+ " statusCode=" + statusCode);
			}
			if (statusCode == HTTP_OK) {
				return new NetResponse(response);
			}
			throw new ApiException(statusCode, Parser.error(response));
		} catch (IOException e) {
			if (App.DEBUG) {
				Log.e(TAG, e.getMessage() + " " + e.toString());
			}
			throw new ApiException(ERROR_IO_EXCEPTION, e.toString(),
					e.getCause());
		}
	}

	/**
	 * fetch useres --get
	 * 
	 * @param url
	 * @param userId
	 * @return
	 * @throws ApiException
	 */
	List<User> fetchUsers(String url, String userId, String mode)
			throws ApiException {
		NetRequest request = new NetRequest.Builder().url(url).id(userId)
				.mode(mode).build();
		NetResponse response = fetch(request);
		int statusCode = response.statusCode;
		if (App.DEBUG) {
			log("fetchStatuses()---statusCode=" + statusCode + " url="
					+ request.url);
		}
		return User.parseUsers(response);

	}

	/**
	 * fetch statuses --get
	 * 
	 * @param url
	 *            api url
	 * @param count
	 *            optional
	 * @param page
	 *            optional
	 * @param userId
	 *            optional
	 * @param sinceId
	 *            optional
	 * @param maxId
	 *            optional
	 * @param isHtml
	 *            optional
	 * @return statuses list
	 * @throws ApiException
	 */
	List<Status> fetchStatuses(String url, int count, int page, String userId,
			String sinceId, String maxId, String format, String mode, int type)
			throws ApiException {
		NetRequest.Builder builder = new NetRequest.Builder();
		builder.url(url).count(count).page(page).id(userId).sinceId(sinceId)
				.maxId(maxId).format(format).mode(mode);
		NetRequest request = builder.build();
		NetResponse response = fetch(request);
		int statusCode = response.statusCode;
		if (App.DEBUG) {
			log("fetchStatuses()---statusCode=" + statusCode + " url="
					+ request.url);
		}
		return Status.parseStatuses(response, type);

	}

	/**
	 * action for only id param --get
	 * 
	 * @param url
	 *            api url
	 * @param id
	 *            userid or status id or dm id
	 * @return string for id
	 * @throws ApiException
	 */
	private NetResponse doGetIdAction(String url, String id, String format,
			String mode) throws ApiException {
		if (App.DEBUG)
			log("doGetIdAction() ---url=" + url + " id=" + id);
		return doSingleIdAction(url, id, format, mode, false);
	}

	/**
	 * action for only id param --post
	 * 
	 * @param url
	 * @param id
	 * @return
	 * @throws ApiException
	 */
	private NetResponse doPostIdAction(String url, String id, String format,
			String mode) throws ApiException {
		if (App.DEBUG)
			log("doPostIdAction() ---url=" + url + " id=" + id);
		return doSingleIdAction(url, id, format, mode, true);
	}

	/**
	 * action for only id param --get/post
	 * 
	 * @param url
	 * @param id
	 * @param isPost
	 * @return
	 * @throws ApiException
	 */
	private NetResponse doSingleIdAction(String url, String id, String format,
			String mode, boolean isPost) throws ApiException {
		NetRequest.Builder builder = new NetRequest.Builder();
		builder.url(url).id(id).post(isPost).format(format).mode(mode);
		return fetch(builder.build());
	}

	@Override
	public User verifyAccount(String mode) throws ApiException {
		NetResponse response = fetch(new NetRequest.Builder()
				.url(URL_VERIFY_CREDENTIALS).mode(mode).build());
		return User.parse(response);
	}

	@Override
	public List<Status> pubicTimeline(int count, String format, String mode)
			throws ApiException {
		List<Status> ss = fetchStatuses(URL_TIMELINE_PUBLIC, count, 0, null,
				null, null, format, mode, Status.TYPE_PUBLIC);
		return ss;
	}

	@Override
	public List<Status> homeTimeline(int count, int page, String sinceId,
			String maxId, String format, String mode) throws ApiException {
		List<Status> ss = fetchStatuses(URL_TIMELINE_HOME, count, page, null,
				sinceId, maxId, format, mode, Status.TYPE_HOME);
		return ss;
	}

	@Override
	public List<Status> userTimeline(int count, int page, String userId,
			String sinceId, String maxId, String format, String mode)
			throws ApiException {
		if (TextUtils.isEmpty(userId)) {
			throw new NullPointerException(
					"userTimeline() userId must not be empty or null.");
		}
		List<Status> ss = fetchStatuses(URL_TIMELINE_USER, count, page, userId,
				sinceId, maxId, format, mode, Status.TYPE_USER);
		return ss;
	}

	@Override
	public List<Status> mentions(int count, int page, String sinceId,
			String maxId, String format, String mode) throws ApiException {
		List<Status> ss = fetchStatuses(URL_TIMELINE_MENTIONS, count, page,
				null, sinceId, maxId, format, mode, Status.TYPE_MENTION);
		return ss;
	}

	@Override
	public List<Status> contextTimeline(String id, String format, String mode)
			throws ApiException {
		NetRequest.Builder builder = new NetRequest.Builder();
		builder.url(URL_TIMELINE_CONTEXT).id(id).format("html").mode("lite");
		NetResponse response = fetch(builder.build());
		List<Status> ss = Status.parseStatuses(response, Status.TYPE_CONTEXT);
		return ss;
	}

	@Override
	public List<Status> replies(int count, int page, String userId,
			String sinceId, String maxId, String format, String mode)
			throws ApiException {
		List<Status> ss = fetchStatuses(URL_TIMELINE_REPLIES, count, page,
				userId, sinceId, maxId, format, mode, Status.TYPE_MENTION);
		return ss;
	}

	@Override
	public List<Status> favorites(int count, int page, String userId,
			String format, String mode) throws ApiException {
		List<Status> ss = fetchStatuses(URL_FAVORITES_LIST, count, page,
				userId, null, null, format, mode, Status.TYPE_FAVORITES);

		if (userId != null && ss != null) {
			for (Status status : ss) {
				status.ownerId = userId;
			}
		}
		return ss;
	}

	@Override
	public Status favoritesCreate(String statusId, String format, String mode)
			throws ApiException {
		if (TextUtils.isEmpty(statusId)) {
			throw new NullPointerException(
					"favoritesCreate() statusId must not be empty or null.");
		}
		String url = String.format(URL_FAVORITES_CREATE, statusId);
		NetResponse response = doPostIdAction(url, null, format, mode);
		int statusCode = response.statusCode;
		if (App.DEBUG) {
			log("favoritesCreate()---statusCode=" + statusCode + " url=" + url);
		}
		Status s = Status.parse(response);
		return s;
	}

	@Override
	public Status favoritesDelete(String statusId, String format, String mode)
			throws ApiException {
		if (TextUtils.isEmpty(statusId)) {
			throw new NullPointerException(
					"favoritesDelete() statusId must not be empty or null.");
		}
		String url = String.format(URL_FAVORITES_DESTROY, statusId);
		NetResponse response = doPostIdAction(url, null, format, mode);
		int statusCode = response.statusCode;
		if (App.DEBUG) {
			log("favoritesDelete()---statusCode=" + statusCode + " url=" + url);
		}

		Status s = Status.parse(response);
		return s;
	}

	@Override
	public Status statusesShow(String statusId, String format, String mode)
			throws ApiException {
		if (StringHelper.isEmpty(statusId)) {
			throw new IllegalArgumentException("消息ID不能为空");
		}
		if (App.DEBUG)
			log("statusShow()---statusId=" + statusId);

		String url = String.format(URL_STATUS_SHOW, statusId);

		NetResponse response = doGetIdAction(url, statusId, format, mode);
		int statusCode = response.statusCode;
		if (App.DEBUG) {
			log("statusShow()---statusCode=" + statusCode);
		}
		Status s = Status.parse(response);
		if (s != null) {
			CacheManager.put(s);
		}
		return s;
	}

	@Override
	public Status statusesCreate(String status, String inReplyToStatusId,
			String source, String location, String repostStatusId,
			String format, String mode) throws ApiException {
		if (StringHelper.isEmpty(status)) {
			throw new IllegalArgumentException("消息内容不能为空");
		}
		if (App.DEBUG)
			log("statusUpdate() ---[status=(" + status + ") replyToStatusId="
					+ inReplyToStatusId + " source=" + source + " location="
					+ location + " repostStatusId=" + repostStatusId + " ]");

		NetRequest.Builder builder = new NetRequest.Builder();
		builder.url(URL_STATUS_UPDATE).post();
		builder.status(status).location(location);
		builder.format(format).mode(mode);
		builder.param("in_reply_to_status_id", inReplyToStatusId);
		builder.param("repost_status_id", repostStatusId);
		builder.param("source", source);
		NetResponse response = fetch(builder.build());
		int statusCode = response.statusCode;
		if (App.DEBUG) {
			log("statusUpdate()---statusCode=" + statusCode);
		}
		Status s= Status.parse(response, Status.TYPE_HOME);
		if(App.DEBUG){
			log("statusesCreate "+s);
		}
		return s;
	}

	@Override
	public Status statusesDelete(String statusId, String format, String mode)
			throws ApiException {
		String url = String.format(URL_STATUS_DESTROY, statusId);
		NetResponse response = doPostIdAction(url, null, format, mode);
		int statusCode = response.statusCode;
		if (App.DEBUG) {
			log("statusDelete()---statusCode=" + statusCode + " url=" + url);
		}
		return Status.parse(response);
	}

	@Override
	public Status photosUpload(File photo, String status, String source,
			String location, String format, String mode) throws ApiException {
		if (photo == null) {
			throw new IllegalArgumentException("文件不能为空");
		}
		if (App.DEBUG)
			log("upload()---photo=" + photo.getAbsolutePath() + " status="
					+ status + " source=" + source + " location=" + location);
		;

		NetRequest.Builder builder = new NetRequest.Builder();
		builder.url(URL_PHOTO_UPLOAD).post();
		builder.status(status).location(location);
		builder.param("photo", photo);
		builder.param("source", source);
		builder.format(format).mode(mode);
		NetResponse response = fetch(builder.build());
		int statusCode = response.statusCode;
		if (App.DEBUG) {
			log("photoUpload()---statusCode=" + statusCode);
		}
		return Status.parse(response, Status.TYPE_HOME);
	}

	@Override
	public List<Status> search(String keyword, String sinceId, String maxId,
			int count, String format, String mode) throws ApiException {
		if (StringHelper.isEmpty(keyword)) {
				throw new IllegalArgumentException("搜索词不能为空");
		}

		NetRequest.Builder builder = new NetRequest.Builder();
		builder.url(URL_SEARCH);
		builder.param("q", keyword);
		builder.maxId(maxId).sinceId(sinceId);
		builder.format("html").mode("lite");
		builder.count(count);
		NetResponse response = fetch(builder.build());

		int statusCode = response.statusCode;
		if (App.DEBUG) {
			log("search()---statusCode=" + statusCode);
		}
		return Status.parseStatuses(response, Status.TYPE_SEARCH);

	}

	@Override
	public List<User> searchUsers(String keyword, int count, int page,
			String mode) throws ApiException {
		if (StringHelper.isEmpty(keyword)) {
			if (App.DEBUG)
				throw new IllegalArgumentException("搜索词不能为空");
			return null;
		}

		NetRequest.Builder builder = new NetRequest.Builder();
		builder.url(URL_SEARCH_USERS);
		builder.param("q", keyword);
		builder.count(count).page(page);
		builder.format("html").mode("lite");
		builder.count(count);
		NetResponse response = fetch(builder.build());

		int statusCode = response.statusCode;
		if (App.DEBUG) {
			log("search()---statusCode=" + statusCode);
		}
		return User.parseUsers(response);

	}

	@Override
	public List<Search> trends() throws ApiException {
		NetResponse response = fetch(new NetRequest.Builder().url(
				URL_TRENDS_LIST).build());

		int statusCode = response.statusCode;
		if (App.DEBUG) {
			log("trends()---statusCode=" + statusCode);
		}
		// handlerResponseError(response);
		return Parser.trends(response);

	}

	@Override
	public List<Search> savedSearchesList() throws ApiException {
		NetResponse response = fetch(new NetRequest.Builder().url(
				URL_SAVED_SEARCHES_LIST).build());

		int statusCode = response.statusCode;
		if (App.DEBUG) {
			log("savedSearchesList()---statusCode=" + statusCode);
		}

		// handlerResponseError(response);
		return Parser.savedSearches(response);

	}

	@Override
	public Search savedSearchesShow(int id) throws ApiException {
		NetRequest.Builder builder = new NetRequest.Builder();
		NetResponse response = fetch(builder.url(URL_SAVED_SEARCHES_SHOW)
				.param("id", id).build());

		int statusCode = response.statusCode;
		if (App.DEBUG) {
			log("savedSearchShow()---statusCode=" + statusCode);
		}

		// handlerResponseError(response);
		return Parser.savedSearch(response);

	}

	@Override
	public Search savedSearchesCreate(String query) throws ApiException {
		if (StringHelper.isEmpty(query)) {
			if (App.DEBUG)
				throw new IllegalArgumentException("搜索词不能为空");
			return null;
		}
		NetRequest.Builder builder = new NetRequest.Builder();
		builder.url(URL_SAVED_SEARCHES_CREATE).post();
		builder.param("query", query);

		NetResponse response = fetch(builder.build());

		int statusCode = response.statusCode;
		if (App.DEBUG) {
			log("savedSearchCreate()---statusCode=" + statusCode);
		}
		// handlerResponseError(response);
		return Parser.savedSearch(response);
	}

	@Override
	public Search savedSearchesDelete(int id) throws ApiException {
		NetRequest.Builder builder = new NetRequest.Builder();
		builder.url(URL_SAVED_SEARCHES_DESTROY).post().id(String.valueOf(id));
		NetResponse response = fetch(builder.build());
		int statusCode = response.statusCode;
		if (App.DEBUG) {
			log("savedSearchDelete()---statusCode=" + statusCode);
		}

		// handlerResponseError(response);
		return Parser.savedSearch(response);
	}

	private List<User> fetchUsers(String url, String userId, int count, int page)
			throws ApiException {
		NetRequest.Builder builder = new NetRequest.Builder();
		builder.url(url).id(userId).count(count).page(page)
				.param("mode", "noprofile");
		NetResponse response = fetch(builder.build());

		return User.parseUsers(response);
	}

	@Override
	public List<User> usersFriends(String userId, int count, int page,
			String mode) throws ApiException {
		List<User> users = fetchUsers(URL_USERS_FRIENDS, userId, count, page);
		if (users != null && users.size() > 0) {
			for (User user : users) {
				user.type = User.TYPE_FRIENDS;
				user.ownerId = (userId == null ? App.getUserId() : userId);
			}
		}
		return users;
	}

	@Override
	public List<User> usersFollowers(String userId, int count, int page,
			String mode) throws ApiException {
		List<User> users = fetchUsers(URL_USERS_FOLLOWERS, userId, count, page);
		if (users != null && users.size() > 0) {
			for (User user : users) {
				user.type = User.TYPE_FOLLOWERS;
				user.ownerId = (userId == null ? App.getUserId() : userId);
			}
		}
		return users;
	}

	@Override
	public User userShow(String userId, String mode) throws ApiException {
		NetResponse response = doGetIdAction(URL_USER_SHOW, userId, null, mode);
		int statusCode = response.statusCode;
		if (App.DEBUG) {
			log("userShow()---statusCode=" + statusCode);
		}

		// handlerResponseError(response);
		User u = User.parse(response);
		if (u != null) {
			u.ownerId = App.getUserId();
			CacheManager.put(u);
		}
		return u;
	}

	@Override
	public User friendshipsCreate(String userId, String mode)
			throws ApiException {
		if (TextUtils.isEmpty(userId)) {
			throw new NullPointerException(
					"friendshipsCreate() userId must not be empty or null.");
		}
		String url;
		try {
			// hack for oauth chinese charactar encode
			url = String.format(URL_FRIENDSHIPS_CREATE,
					URLEncoder.encode(userId, HTTP.UTF_8));
		} catch (UnsupportedEncodingException e) {
			url = String.format(URL_FRIENDSHIPS_CREATE, userId);
		}

		NetResponse response = doPostIdAction(url, null, null, mode);
		int statusCode = response.statusCode;
		if (App.DEBUG) {
			log("friendshipsCreate()---statusCode=" + statusCode + " url="
					+ url);
		}
		User u = User.parse(response);
		if (u != null) {
			u.ownerId = App.getUserId();
			CacheManager.put(u);
		}
		return u;
	}

	@Override
	public User friendshipsDelete(String userId, String mode)
			throws ApiException {
		if (TextUtils.isEmpty(userId)) {
			throw new NullPointerException(
					"friendshipsDelete() userId must not be empty or null.");
		}
		// String url=String.format(URL_FRIENDSHIPS_DESTROY, userId);
		String url;
		try {
			url = String.format(URL_FRIENDSHIPS_DESTROY,
					URLEncoder.encode(userId, HTTP.UTF_8));
		} catch (UnsupportedEncodingException e) {
			url = String.format(URL_FRIENDSHIPS_DESTROY, userId);
		}

		NetResponse response = doPostIdAction(url, null, null, mode);
		int statusCode = response.statusCode;
		if (App.DEBUG) {
			log("friendshipsDelete()---statusCode=" + statusCode + " url="
					+ url);
		}
		User u = User.parse(response);
		if (u != null) {
			u.ownerId = App.getUserId();
			CacheManager.put(u);
		}
		return u;
	}

	@Override
	public User blocksCreate(String userId, String mode) throws ApiException {
		// String url=String.format(URL_BLOCKS_CREATE, userId);
		if (TextUtils.isEmpty(userId)) {
			throw new NullPointerException(
					"blocksCreate() userId must not be empty or null.");
		}
		String url;
		try {
			url = String.format(URL_BLOCKS_CREATE,
					URLEncoder.encode(userId, HTTP.UTF_8));
		} catch (UnsupportedEncodingException e) {
			url = String.format(URL_BLOCKS_CREATE, userId);
		}

		NetResponse response = doPostIdAction(url, null, null, mode);
		int statusCode = response.statusCode;
		if (App.DEBUG) {
			log("userBlock()---statusCode=" + statusCode + " url=" + url);
		}

		// handlerResponseError(response);
		User u = User.parse(response);
		if (u != null) {
			u.ownerId = App.getUserId();
		}
		return u;
	}

	@Override
	public User blocksDelete(String userId, String mode) throws ApiException {
		// String url=String.format(URL_BLOCKS_DESTROY, userId);
		if (TextUtils.isEmpty(userId)) {
			throw new NullPointerException(
					"blocksDelete() userId must not be empty or null.");
		}
		String url;
		try {
			url = String.format(URL_BLOCKS_DESTROY,
					URLEncoder.encode(userId, HTTP.UTF_8));
		} catch (UnsupportedEncodingException e) {
			url = String.format(URL_BLOCKS_DESTROY, userId);
		}

		NetResponse response = doPostIdAction(url, null, null, mode);
		int statusCode = response.statusCode;
		if (App.DEBUG) {
			log("userUnblock()---statusCode=" + statusCode + " url=" + url);
		}

		// handlerResponseError(response);
		User u = User.parse(response);
		if (u != null) {
			u.ownerId = App.getUserId();
		}
		return u;
	}

	@Override
	public boolean friendshipsExists(String userA, String userB)
			throws ApiException {
		if (StringHelper.isEmpty(userA) || StringHelper.isEmpty(userB)) {
			throw new IllegalArgumentException(
					"friendshipsExists() usera and userb must not be empty or null.");
		}
		NetRequest.Builder builder = new NetRequest.Builder();
		builder.url(URL_FRIENDSHIS_EXISTS);
		builder.param("user_a", userA);
		builder.param("user_b", userB);
		NetResponse response = fetch(builder.build());

		int statusCode = response.statusCode;
		if (App.DEBUG)
			log("isFriends()---statusCode=" + statusCode);
		try {
			String content = response.getContent();
			if (App.DEBUG)
				log("isFriends()---response=" + content);
			return content.contains("true");
		} catch (IOException e) {
			if (App.DEBUG) {
				e.printStackTrace();
			}
			return false;
		}
	}

	// 最大2000
	private List<String> ids(String url, String userId, int count, int page)
			throws ApiException {
		NetRequest.Builder builder = new NetRequest.Builder();
		builder.url(url);
		builder.id(userId);
		builder.count(count);
		builder.page(page);
		NetResponse response = fetch(builder.build());
		int statusCode = response.statusCode;
		if (App.DEBUG) {
			log("ids()---statusCode=" + statusCode);
		}

		// handlerResponseError(response);
		return Parser.ids(response);

	}

	@Override
	public List<String> friendsIDs(String userId, int count, int page)
			throws ApiException {
		return ids(URL_USERS_FRIENDS_IDS, userId, count, page);
	}

	@Override
	public List<String> followersIDs(String userId, int count, int page)
			throws ApiException {
		return ids(URL_USERS_FOLLOWERS_IDS, userId, count, page);
	}

	private List<DirectMessage> messages(String url, int count, int page,
			String sinceId, String maxId, String mode, int type)
			throws ApiException {
		NetRequest.Builder builder = new NetRequest.Builder();
		builder.url(url).count(count).page(page).maxId(maxId).sinceId(sinceId)
				.mode(mode);

		// count<=0,count>60时返回60条
		// count>=0&&count<=60时返回count条
		// int c=count;
		// if(c<1||c>ApiConfig.MAX_COUNT){
		// c=ApiConfig.MAX_COUNT;
		// }
		// builder.count(c);

		NetResponse response = fetch(builder.build());
		int statusCode = response.statusCode;
		if (App.DEBUG) {
			log("messages()---statusCode=" + statusCode);
		}
		return DirectMessage.parseMessges(response, type);
	}

	@Override
	public List<DirectMessage> directMessagesInbox(int count, int page,
			String sinceId, String maxId, String mode) throws ApiException {
		List<DirectMessage> dms = messages(URL_DIRECT_MESSAGES_INBOX, count,
				page, sinceId, maxId, mode, DirectMessage.TYPE_IN);
		// TODO new dm api, need type set to type_user
		if (dms != null && dms.size() > 0) {
			for (DirectMessage dm : dms) {
				dm.threadUserId = dm.senderId;
				dm.threadUserName = dm.senderScreenName;
			}
		}
		return dms;
	}

	@Override
	public List<DirectMessage> directMessagesOutbox(int count, int page,
			String sinceId, String maxId, String mode) throws ApiException {
		List<DirectMessage> dms = messages(URL_DIRECT_MESSAGES_OUTBOX, count,
				page, sinceId, maxId, mode, DirectMessage.TYPE_OUT);
		// TODO new dm api, need type set to type_user
		if (dms != null && dms.size() > 0) {
			for (DirectMessage dm : dms) {
				dm.threadUserId = dm.recipientId;
				dm.threadUserName = dm.recipientScreenName;
			}
		}
		return dms;
	}

	@Override
	public List<DirectMessage> directMessagesConversationList(int count,
			int page, String mode) throws ApiException {
		NetRequest.Builder builder = NetRequest.newBuilder();
		builder.url(URL_DIRECT_MESSAGES_CONVERSATION).count(count).page(page)
				.mode(mode);
		NetResponse response = fetch(builder.build());
		return DirectMessage.parseConversationList(response);
	}

	@Override
	public List<DirectMessage> directMessagesConversation(String userId,
			String maxId, int count, String mode) throws ApiException {
		if (TextUtils.isEmpty(userId)) {
			throw new NullPointerException(
					"directMessagesConversation() userId must not be empty or null.");
		}
		NetRequest.Builder builder = NetRequest.newBuilder();
		builder.url(URL_DIRECT_MESSAGES_CONVERSATION).id(userId).count(count)
				.maxId(maxId).mode(mode);
		NetResponse response = fetch(builder.build());
		List<DirectMessage> dms = DirectMessage.parseConversationUser(response);
		if (dms != null && dms.size() > 0) {
			for (DirectMessage dm : dms) {
				dm.threadUserId = userId;
			}
		}
		return null;
	}

	@Override
	public DirectMessage directMessagesCreate(String userId, String text,
			String inReplyToId, String mode) throws ApiException {
		if (StringHelper.isEmpty(userId) || StringHelper.isEmpty(text)) {
			if (App.DEBUG)
				throw new IllegalArgumentException("收信人ID和私信内容都不能为空");
			return null;
		}
		NetRequest.Builder builder = new NetRequest.Builder();
		builder.url(URL_DIRECT_MESSAGES_NEW);
		builder.post();
		builder.param("user", userId);
		builder.param("text", text);
		builder.param("in_reply_to_id", inReplyToId);
		NetResponse response = fetch(builder.build());
		int statusCode = response.statusCode;
		if (App.DEBUG) {
			log("DirectMessagesCreate()---statusCode=" + statusCode);
		}

		DirectMessage dm = DirectMessage
				.parse(response, DirectMessage.TYPE_OUT);
		if (dm != null && !dm.isNull()) {
			dm.type = DirectMessage.TYPE_OUT;
			dm.threadUserId = dm.recipientId;
			dm.threadUserName = dm.recipientScreenName;
			return dm;
		} else {
			return null;
		}
	}

	@Override
	public DirectMessage directMessagesDelete(String directMessageId,
			String mode) throws ApiException {
		if (TextUtils.isEmpty(directMessageId)) {
			throw new NullPointerException(
					"directMessagesDelete() directMessageId must not be empty or null.");
		}
		String url = String
				.format(URL_DIRECT_MESSAGES_DESTROY, directMessageId);
		NetResponse response = doPostIdAction(url, null, null, mode);
		int statusCode = response.statusCode;
		if (App.DEBUG) {
			log("DirectMessagesDelete()---statusCode=" + statusCode + " url="
					+ url);
		}
		return DirectMessage.parse(response, Commons.TYPE_NONE);
	}

	@Override
	public List<Status> photosTimeline(int count, int page, String userId,
			String sinceId, String maxId, String format, String mode)
			throws ApiException {
		List<Status> ss = fetchStatuses(URL_PHOTO_USER_TIMELINE, count, page,
				userId, sinceId, maxId, format, mode, Status.TYPE_USER);
		if (App.DEBUG) {
			log("photosTimeline()");
		}
		return ss;
	}

	@Override
	public User updateProfile(String description, String name, String location,
			String url, String mode) throws ApiException {
		NetRequest.Builder builder = new NetRequest.Builder();
		builder.url(URL_ACCOUNT_UPDATE_PROFILE).post();
		builder.param("description", description);
		builder.param("name", name);
		builder.param("location", location);
		builder.param("url", url);
		NetResponse response = fetch(builder.build());
		int statusCode = response.statusCode;
		if (App.DEBUG) {
			log("updateProfile()---statusCode=" + statusCode);
		}
		return User.parse(response);
	}

	@Override
	public User updateProfileImage(File image, String mode) throws ApiException {
		NetRequest.Builder builder = new NetRequest.Builder();
		builder.url(URL_ACCOUNT_UPDATE_PROFILE_IMAGE).post();
		builder.param("image", image);
		NetResponse response = fetch(builder.build());
		int statusCode = response.statusCode;
		if (App.DEBUG) {
			log("updateProfileImage()---statusCode=" + statusCode);
		}
		return User.parse(response);
	}

	@Override
	public User blocksExists(String userId, String mode) throws ApiException {
		NetResponse response = doPostIdAction(URL_BLOCKS_EXISTS, userId, null,
				mode);
		int statusCode = response.statusCode;
		if (App.DEBUG) {
			log("userIsBlocked()---statusCode=" + statusCode);
		}
		return User.parse(response);
	}

	@Override
	public List<User> blocksBlocking(int count, int page, String mode)
			throws ApiException {
		NetRequest.Builder builder = new NetRequest.Builder();
		builder.url(URL_BLOCKS_USERS);
		builder.count(count).page(page);
		NetResponse response = fetch(builder.build());
		int statusCode = response.statusCode;
		if (App.DEBUG) {
			log("userBlockedList()---statusCode=" + statusCode);
		}
		return User.parseUsers(response);
	}

	@Override
	public List<String> blocksIDs() throws ApiException {
		NetRequest.Builder builder = new NetRequest.Builder();
		builder.url(URL_BLOCKS_IDS);
		NetResponse response = fetch(builder.build());
		int statusCode = response.statusCode;
		if (App.DEBUG) {
			log("userBlockedIDs()---statusCode=" + statusCode);
		}
		return Parser.ids(response);
	}

}
