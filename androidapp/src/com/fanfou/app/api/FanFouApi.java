package com.fanfou.app.api;

import java.io.File;
import java.io.IOException;
import java.util.List;

import org.apache.http.HttpResponse;

import android.util.Log;

import com.fanfou.app.App;
import com.fanfou.app.cache.CacheManager;
import com.fanfou.app.http.ConnectionManager;
import com.fanfou.app.http.ConnectionRequest;
import com.fanfou.app.http.Response;
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
 * 
 */
public class FanFouApi implements Api, FanFouApiConfig, ResponseCode {
	// oauth is on or off
	private static final boolean OAUTH_ON = true;
	private static final String TAG = FanFouApi.class.getSimpleName();

	/**
	 * @param message
	 */
	private void log(String message) {
		Log.d(TAG, message);
	}

	public FanFouApi() {
	}

	/**
	 * exec http request
	 * 
	 * @param request
	 * @return response object
	 * @throws ApiException
	 */
	private Response fetch(ConnectionRequest request) throws ApiException {
		try {
			// long startTime = System.currentTimeMillis();
			HttpResponse response = ConnectionManager.execWithOAuth(request);
			// if(App.DEBUG){
			// long reqTime = System.currentTimeMillis() - startTime;
			// Utils.logTime("fetch", reqTime);}
			int statusCode = response.getStatusLine().getStatusCode();
			if (statusCode == HTTP_OK) {
				return new Response(response);
			} else {
				throw new ApiException(statusCode, Parser.error(response));
			}
		} catch (IOException e) {
			if (App.DEBUG)
				e.printStackTrace();
			throw new ApiException(ERROR_NOT_CONNECTED, e.toString(),
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
	List<User> fetchUsers(String url, String userId) throws ApiException {
		Response response = fetch(new ConnectionRequest.Builder().url(url)
				.id(userId).build());
		int statusCode = response.statusCode;
		if (App.DEBUG) {
			log("fetchUsers()---url=" + url + " userid=" + userId);
			log("fetchStatuses()---statusCode=" + statusCode);
		}

		// handlerResponseError(response);
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
			String sinceId, String maxId, boolean isHtml, int type)
			throws ApiException {
		if (App.DEBUG) {
			log("fetchStatuses()---url=" + url + " count=" + count + " page="
					+ page + " userid=" + userId + " sinceId=" + sinceId
					+ " maxid=" + maxId);
		}
		ConnectionRequest.Builder builder = new ConnectionRequest.Builder();
		builder.url(url).count(count).page(page).id(userId).sinceId(sinceId)
				.maxId(maxId).param("mode", "noprofile");

		// count<=0,count>60时返回60条
		// count>=0&&count<=60时返回count条
		// int c=count;
		// if(c<1||c>ApiConfig.MAX_COUNT){
		// c=ApiConfig.MAX_COUNT;
		// }
		// builder.count(c);

		if (isHtml) {
			builder.param("format", "html");
		}
		Response response = fetch(builder.build());
		int statusCode = response.statusCode;
		if (App.DEBUG) {
			log("fetchStatuses()---statusCode=" + statusCode);
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
	private Response doGetIdAction(String url, String id) throws ApiException {
		if (App.DEBUG)
			log("doGetIdAction() ---url=" + url + " id=" + id);
		return doSingleIdAction(url, id, false);
	}

	/**
	 * action for only id param --post
	 * 
	 * @param url
	 * @param id
	 * @return
	 * @throws ApiException
	 */
	private Response doPostIdAction(String url, String id) throws ApiException {
		if (StringHelper.isEmpty(id)) {
			throw new IllegalArgumentException("POST请求ID参数不能为空");
		}
		if (App.DEBUG)
			log("doPostIdAction() ---url=" + url + " id=" + id);
		return doSingleIdAction(url, id, true);
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
	private Response doSingleIdAction(String url, String id, boolean isPost)
			throws ApiException {
		ConnectionRequest.Builder builder = new ConnectionRequest.Builder();
		builder.url(url).id(id).post(isPost).param("mode", "noprofile");
		return fetch(builder.build());
	}

	@Override
	public User verifyAccount() throws ApiException {
		Response response = fetch(new ConnectionRequest.Builder().url(
				URL_VERIFY_CREDENTIALS).param("mode", "noprofile").build());
		return User.parse(response);
	}

	@Override
	public List<Status> pubicTimeline(int count, boolean isHtml)
			throws ApiException {
		List<Status> ss = fetchStatuses(URL_TIMELINE_PUBLIC, count, 0, null,
				null, null, isHtml, Status.TYPE_PUBLIC);
		return ss;
	}

	@Override
	public List<Status> homeTimeline(int count, int page, String sinceId,
			String maxId, boolean isHtml) throws ApiException {
		List<Status> ss = fetchStatuses(URL_TIMELINE_FRIENDS, count, page,
				null, sinceId, maxId, isHtml, Status.TYPE_HOME);
		return ss;
	}

	@Override
	public List<Status> userTimeline(int count, int page, String userId,
			String sinceId, String maxId, boolean isHtml) throws ApiException {
		List<Status> ss = fetchStatuses(URL_TIMELINE_USER, count, page, userId,
				sinceId, maxId, isHtml, Status.TYPE_USER);
		return ss;
	}

	@Override
	public List<Status> mentions(int count, int page, String sinceId,
			String maxId, boolean isHtml) throws ApiException {
		List<Status> ss = fetchStatuses(URL_TIMELINE_MENTIONS, count, page,
				null, sinceId, maxId, isHtml, Status.TYPE_MENTION);
		return ss;
	}

	@Override
	public List<Status> contextTimeline(String id, boolean isHtml)
			throws ApiException {
		ConnectionRequest.Builder builder = new ConnectionRequest.Builder();
		builder.url(URL_TIMELINE_CONTEXT).id(id).format(isHtml);
		Response response = fetch(builder.build());
		List<Status> ss = Status.parseStatuses(response, Status.TYPE_CONTEXT);
		return ss;
	}

	@Override
	public List<Status> replies(int count, int page, String userId,
			String sinceId, String maxId, boolean isHtml) throws ApiException {
		List<Status> ss = fetchStatuses(URL_TIMELINE_REPLIES, count, page,
				userId, sinceId, maxId, isHtml, Status.TYPE_MENTION);
		return ss;
	}

	@Override
	public List<Status> favorites(int count, int page, String userId,
			boolean isHtml) throws ApiException {
		List<Status> ss = fetchStatuses(URL_FAVORITES, count, page, userId,
				null, null, isHtml, Status.TYPE_FAVORITES);

		if (userId != null && ss != null) {
			for (Status status : ss) {
				status.ownerId = userId;
			}
		}
		return ss;
	}

	@Override
	public Status statusFavorite(String statusId) throws ApiException {
		Response response = doPostIdAction(URL_FAVORITES_CREATE, statusId);
		int statusCode = response.statusCode;
		if (App.DEBUG) {
			log("statusFavorite()---statusCode=" + statusCode);
		}
		Status s = Status.parse(response);
		return s;
	}

	@Override
	public Status statusUnfavorite(String statusId) throws ApiException {
		Response response = doPostIdAction(URL_FAVORITES_DESTROY, statusId);
		int statusCode = response.statusCode;
		if (App.DEBUG) {
			log("statusUnfavorite()---statusCode=" + statusCode);
		}

		Status s = Status.parse(response);
		return s;
	}

	@Override
	public Status statusShow(String statusId) throws ApiException {
		if (StringHelper.isEmpty(statusId)) {
			throw new IllegalArgumentException("消息ID不能为空");
		}
		if (App.DEBUG)
			log("statusShow()---statusId=" + statusId);
		Response response = doGetIdAction(URL_STATUS_SHOW, statusId);
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
	public Status statusUpdate(String status, String inReplyToStatusId,
			String source, String location, String repostStatusId)
			throws ApiException {
		if (StringHelper.isEmpty(status)) {
			if (App.DEBUG)
				throw new IllegalArgumentException("消息内容不能为空");
			return null;
		}
		if (App.DEBUG)
			log("statusUpdate() ---[status=(" + status + ") replyToStatusId="
					+ inReplyToStatusId + " source=" + source + " location="
					+ location + " repostStatusId=" + repostStatusId + " ]");

		ConnectionRequest.Builder builder = new ConnectionRequest.Builder();
		builder.url(URL_STATUS_UPDATE).post();
		builder.status(status).location(location);
		builder.param("in_reply_to_status_id", inReplyToStatusId);
		builder.param("repost_status_id", repostStatusId);
		builder.param("source", source);
		Response response = fetch(builder.build());
		int statusCode = response.statusCode;
		if (App.DEBUG) {
			log("statusUpdate()---statusCode=" + statusCode);
		}
		if (StringHelper.isEmpty(response.getContent())) {
			throw new ApiException(ERROR_DUPLICATE, "重复消息，发送失败");
		}
		return Status.parse(response, Status.TYPE_HOME);
	}

	@Override
	public Status statusDelete(String statusId) throws ApiException {
		Response response = doPostIdAction(URL_STATUS_DESTROY, statusId);
		int statusCode = response.statusCode;
		if (App.DEBUG) {
			log("statusDelete()---statusCode=" + statusCode);
		}
		return Status.parse(response);
	}

	@Override
	public Status photoUpload(File photo, String status, String source,
			String location) throws ApiException {
		if (photo == null) {
			if (App.DEBUG)
				throw new IllegalArgumentException("文件不能为空");
			return null;
		}
		if (App.DEBUG)
			log("upload()---photo=" + photo.getAbsolutePath() + " status="
					+ status + " source=" + source + " location=" + location);
		;

		ConnectionRequest.Builder builder = new ConnectionRequest.Builder();
		builder.url(URL_PHOTO_UPLOAD).post();
		builder.status(status).location(location);
		builder.param("photo", photo);
		builder.param("source", source);

		Response response = fetch(builder.build());
		int statusCode = response.statusCode;
		if (App.DEBUG) {
			log("photoUpload()---statusCode=" + statusCode);
		}
		return Status.parse(response, Status.TYPE_HOME);
	}

	@Override
	public List<Status> search(String keyword, String maxId, boolean isHtml)
			throws ApiException {
		if (StringHelper.isEmpty(keyword)) {
			if (App.DEBUG)
				throw new IllegalArgumentException("搜索词不能为空");
			return null;
		}

		ConnectionRequest.Builder builder = new ConnectionRequest.Builder();
		builder.url(URL_SEARCH_PUBLIC);
		builder.param("q", keyword);
		builder.param("max_id", maxId);
		builder.param("format", "html");
		Response response = fetch(builder.build());

		int statusCode = response.statusCode;
		if (App.DEBUG) {
			log("search()---statusCode=" + statusCode);
		}
		return Status.parseStatuses(response, Status.TYPE_SEARCH);

	}

	@Override
	public List<Search> trends() throws ApiException {
		Response response = fetch(new ConnectionRequest.Builder().url(
				URL_SEARCH_TRENDS).build());

		int statusCode = response.statusCode;
		if (App.DEBUG) {
			log("trends()---statusCode=" + statusCode);
		}
		// handlerResponseError(response);
		return Parser.trends(response);

	}

	@Override
	public List<Search> savedSearches() throws ApiException {
		Response response = fetch(new ConnectionRequest.Builder().url(
				URL_SEARCH_SAVED).build());

		int statusCode = response.statusCode;
		if (App.DEBUG) {
			log("savedSearches()---statusCode=" + statusCode);
		}

		// handlerResponseError(response);
		return Parser.savedSearches(response);

	}

	@Override
	public Search savedSearchShow(int id) throws ApiException {
		ConnectionRequest.Builder builder = new ConnectionRequest.Builder();
		Response response = fetch(builder.url(URL_SEARCH_SAVED_ID)
				.param("id", id).build());

		int statusCode = response.statusCode;
		if (App.DEBUG) {
			log("savedSearchShow()---statusCode=" + statusCode);
		}

		// handlerResponseError(response);
		return Parser.savedSearch(response);

	}

	@Override
	public Search savedSearchCreate(String query) throws ApiException {
		if (StringHelper.isEmpty(query)) {
			if (App.DEBUG)
				throw new IllegalArgumentException("搜索词不能为空");
			return null;
		}
		ConnectionRequest.Builder builder = new ConnectionRequest.Builder();
		builder.url(URL_SEARCH_SAVED_CREATE).post();
		builder.param("query", query);

		Response response = fetch(builder.build());

		int statusCode = response.statusCode;
		if (App.DEBUG) {
			log("savedSearchCreate()---statusCode=" + statusCode);
		}
		// handlerResponseError(response);
		return Parser.savedSearch(response);
	}

	@Override
	public Search savedSearchDelete(int id) throws ApiException {
		ConnectionRequest.Builder builder = new ConnectionRequest.Builder();
		builder.url(URL_SEARCH_SAVED_DESTROY).post().id(String.valueOf(id));
		Response response = fetch(builder.build());
		int statusCode = response.statusCode;
		if (App.DEBUG) {
			log("savedSearchDelete()---statusCode=" + statusCode);
		}

		// handlerResponseError(response);
		return Parser.savedSearch(response);
	}

	private List<User> fetchUsers(String url, String userId, int count, int page)
			throws ApiException {
		ConnectionRequest.Builder builder = new ConnectionRequest.Builder();
		builder.url(url).id(userId).count(count).page(page).param("mode", "noprofile");
		Response response = fetch(builder.build());

		return User.parseUsers(response);
	}

	@Override
	public List<User> usersFriends(String userId, int count, int page) throws ApiException {
		List<User> users = fetchUsers(URL_USERS_FRIENDS, userId, count,page);
		if (users != null && users.size() > 0) {
			for (User user : users) {
				user.type = User.TYPE_FRIENDS;
				user.ownerId = (userId == null ? App.me.userId : userId);
			}
		}
		return users;
	}

	@Override
	public List<User> usersFollowers(String userId, int count, int page)
			throws ApiException {
		List<User> users = fetchUsers(URL_USERS_FOLLOWERS, userId, count,page);
		if (users != null && users.size() > 0) {
			for (User user : users) {
				user.type = User.TYPE_FOLLOWERS;
				user.ownerId = (userId == null ? App.me.userId : userId);
			}
		}
		return users;
	}

	@Override
	public User userShow(String userId) throws ApiException {
		Response response = doGetIdAction(URL_USER_SHOW, userId);
		int statusCode = response.statusCode;
		if (App.DEBUG) {
			log("userShow()---statusCode=" + statusCode);
		}

		// handlerResponseError(response);
		User u = User.parse(response);
		if (u != null) {
			u.ownerId = App.me.userId;
			CacheManager.put(u);
		}
		return u;
	}

	@Override
	public User userFollow(String userId) throws ApiException {
		Response response = doPostIdAction(URL_FRIENDSHIPS_CREATE, userId);

		int statusCode = response.statusCode;
		if (App.DEBUG) {
			log("userFollow()---statusCode=" + statusCode);
		}

		// handlerResponseError(response);
		User u = User.parse(response);
		if (u != null) {
			u.ownerId = App.me.userId;
			CacheManager.put(u);
		}
		return u;
	}

	@Override
	public User userUnfollow(String userId) throws ApiException {
		Response response = doPostIdAction(URL_FRIENDSHIPS_DESTROY, userId);
		int statusCode = response.statusCode;
		if (App.DEBUG) {
			log("userUnfollow()---statusCode=" + statusCode);
		}

		// handlerResponseError(response);
		User u = User.parse(response);
		if (u != null) {
			u.ownerId = App.me.userId;
			CacheManager.put(u);
		}
		return u;
	}

	@Override
	public User userBlock(String userId) throws ApiException {
		Response response = doPostIdAction(URL_BLOCKS_CREATE, userId);
		int statusCode = response.statusCode;
		if (App.DEBUG) {
			log("userBlock()---statusCode=" + statusCode);
		}

		// handlerResponseError(response);
		User u = User.parse(response);
		if (u != null) {
			u.ownerId = App.me.userId;
		}
		return u;
	}

	@Override
	public User userUnblock(String userId) throws ApiException {
		Response response = doPostIdAction(URL_BLOCKS_DESTROY, userId);
		int statusCode = response.statusCode;
		if (App.DEBUG) {
			log("userUnblock()---statusCode=" + statusCode);
		}

		// handlerResponseError(response);
		User u = User.parse(response);
		if (u != null) {
			u.ownerId = App.me.userId;
		}
		return u;
	}

	@Override
	public boolean isFriends(String userA, String userB) throws ApiException {
		if (StringHelper.isEmpty(userA) || StringHelper.isEmpty(userB)) {
			if (App.DEBUG)
				throw new IllegalArgumentException("两个用户的ID都不能为空");
			throw new ApiException(ERROR_NULL_TOKEN, "两个用户的ID都不能为空");
		}
		ConnectionRequest.Builder builder = new ConnectionRequest.Builder();
		builder.url(URL_FRIENDSHIS_EXISTS);
		builder.param("user_a", userA);
		builder.param("user_b", userB);
		Response response = fetch(builder.build());

		int statusCode = response.statusCode;
		if (App.DEBUG)
			log("isFriends()---statusCode=" + statusCode);
		String content = response.getContent();
		if (App.DEBUG)
			log("isFriends()---response=" + content);
		return content.contains("true");

	}

	// 最大2000
	private List<String> ids(String url, String userId, int count, int page)
			throws ApiException {
		ConnectionRequest.Builder builder = new ConnectionRequest.Builder();
		builder.url(url);
		builder.id(userId);
		builder.count(count);
		builder.page(page);
		Response response = fetch(builder.build());
		int statusCode = response.statusCode;
		if (App.DEBUG) {
			log("ids()---statusCode=" + statusCode);
		}

		// handlerResponseError(response);
		return Parser.ids(response);

	}

	@Override
	public List<String> usersFriendsIDs(String userId, int count, int page)
			throws ApiException {
		return ids(URL_USERS_FRIENDS_IDS, userId, count, page);
	}

	@Override
	public List<String> usersFollowersIDs(String userId, int count, int page)
			throws ApiException {
		return ids(URL_USERS_FOLLOWERS_IDS, userId, count, page);
	}

	private List<DirectMessage> messages(String url, int count, int page,
			String sinceId, String maxId) throws ApiException {
		ConnectionRequest.Builder builder = new ConnectionRequest.Builder();
		builder.url(url).count(count).page(page).maxId(maxId).sinceId(sinceId).param("mode", "noprofile");

		// count<=0,count>60时返回60条
		// count>=0&&count<=60时返回count条
		// int c=count;
		// if(c<1||c>ApiConfig.MAX_COUNT){
		// c=ApiConfig.MAX_COUNT;
		// }
		// builder.count(c);

		Response response = fetch(builder.build());
		int statusCode = response.statusCode;
		if (App.DEBUG) {
			log("messages()---statusCode=" + statusCode);
		}
		return DirectMessage.parseMessges(response);
	}

	@Override
	public List<DirectMessage> messagesInbox(int count, int page,
			String sinceId, String maxId) throws ApiException {
		List<DirectMessage> dms = messages(URL_DIRECT_MESSAGES_INBOX, count,
				page, sinceId, maxId);
		if (dms != null && dms.size() > 0) {
			for (DirectMessage dm : dms) {
				dm.type = DirectMessage.TYPE_IN;
				dm.threadUserId = dm.senderId;
				dm.threadUserName = dm.senderScreenName;
			}
		}
		return dms;
	}

	@Override
	public List<DirectMessage> messagesOutbox(int count, int page,
			String sinceId, String maxId) throws ApiException {
		List<DirectMessage> dms = messages(URL_DIRECT_MESSAGES_OUTBOX, count,
				page, sinceId, maxId);
		if (dms != null && dms.size() > 0) {
			for (DirectMessage dm : dms) {
				dm.type = DirectMessage.TYPE_OUT;
				dm.threadUserId = dm.recipientId;
				dm.threadUserName = dm.recipientScreenName;
			}
		}
		return dms;
	}

	@Override
	public DirectMessage messageCreate(String userId, String text,
			String inReplyToId) throws ApiException {
		if (StringHelper.isEmpty(userId) || StringHelper.isEmpty(text)) {
			if (App.DEBUG)
				throw new IllegalArgumentException("收信人ID和私信内容都不能为空");
			return null;
		}
		ConnectionRequest.Builder builder = new ConnectionRequest.Builder();
		builder.url(URL_DIRECT_MESSAGES_NEW);
		builder.post();
		builder.param("user", userId);
		builder.param("text", text);
		builder.param("in_reply_to_id", inReplyToId);
		Response response = fetch(builder.build());
		int statusCode = response.statusCode;
		if (App.DEBUG) {
			log("messageCreate()---statusCode=" + statusCode);
		}

		DirectMessage dm = DirectMessage.parse(response);
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
	public DirectMessage messageDelete(String directMessageId)
			throws ApiException {
		Response response = doPostIdAction(URL_DIRECT_MESSAGES_DESTROY,
				directMessageId);
		int statusCode = response.statusCode;
		if (App.DEBUG) {
			log("messageDelete()---statusCode=" + statusCode);
		}

		// handlerResponseError(response);
		return DirectMessage.parse(response);
	}

	@Override
	public User notificationsOn(String userId) throws ApiException {
		Response response = doPostIdAction(URL_NOTIFICATIONS_OPEN, userId);
		int statusCode = response.statusCode;
		if (App.DEBUG) {
			log("notificationsOn()---statusCode=" + statusCode);
		}

		// handlerResponseError(response);
		return User.parse(response);
	}

	@Override
	public User notificationsOff(String userId) throws ApiException {
		Response response = doPostIdAction(URL_NOTIFICATIONS_CLOSE, userId);
		int statusCode = response.statusCode;
		if (App.DEBUG) {
			log("notificationsOff()---statusCode=" + statusCode);
		}

		// handlerResponseError(response);
		return User.parse(response);
	}

	@Override
	public void test() throws ApiException {
	}

	@Override
	public List<Status> photosTimeline(int count, int page, String userId,
			String sinceId, String maxId, boolean isHtml) throws ApiException {
		List<Status> ss = fetchStatuses(URL_PHOTO_USER_TIMELINE, count, page,
				userId, sinceId, maxId, isHtml, Status.TYPE_USER);
		if (App.DEBUG) {
			log("photosTimeline()");
		}
		return ss;
	}

	@Override
	public User updateProfile(String description, String name, String location,
			String url) throws ApiException {
		ConnectionRequest.Builder builder = new ConnectionRequest.Builder();
		builder.url(URL_ACCOUNT_UPDATE_PROFILE).post();
		builder.param("description", description);
		builder.param("name", name);
		builder.param("location", location);
		builder.param("url", url);
		Response response = fetch(builder.build());
		int statusCode = response.statusCode;
		if (App.DEBUG) {
			log("updateProfile()---statusCode=" + statusCode);
		}
		return User.parse(response);
	}

	@Override
	public User updateProfileImage(File image) throws ApiException {
		ConnectionRequest.Builder builder = new ConnectionRequest.Builder();
		builder.url(URL_ACCOUNT_UPDATE_PROFILE_IMAGE).post();
		builder.param("image", image);
		Response response = fetch(builder.build());
		int statusCode = response.statusCode;
		if (App.DEBUG) {
			log("updateProfileImage()---statusCode=" + statusCode);
		}
		return User.parse(response);
	}

	@Override
	public User isBlocked(String userId) throws ApiException {
		Response response = doPostIdAction(URL_BLOCKS_EXISTS, userId);
		int statusCode = response.statusCode;
		if (App.DEBUG) {
			log("userIsBlocked()---statusCode=" + statusCode);
		}
		return User.parse(response);
	}

	@Override
	public List<User> userBlockedList(int count, int page) throws ApiException {
		ConnectionRequest.Builder builder = new ConnectionRequest.Builder();
		builder.url(URL_BLOCKS_USERS);
		builder.count(count).page(page);
		Response response = fetch(builder.build());
		int statusCode = response.statusCode;
		if (App.DEBUG) {
			log("userBlockedList()---statusCode=" + statusCode);
		}
		return User.parseUsers(response);
	}

	@Override
	public List<String> userBlockedIDs() throws ApiException {
		ConnectionRequest.Builder builder = new ConnectionRequest.Builder();
		builder.url(URL_BLOCKS_IDS);
		Response response = fetch(builder.build());
		int statusCode = response.statusCode;
		if (App.DEBUG) {
			log("userBlockedIDs()---statusCode=" + statusCode);
		}
		return Parser.ids(response);
	}

}
