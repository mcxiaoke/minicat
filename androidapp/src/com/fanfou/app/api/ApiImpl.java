package com.fanfou.app.api;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.HttpResponse;

import android.content.Context;
import android.util.Log;

import com.fanfou.app.App;
import com.fanfou.app.cache.CacheManager;
import com.fanfou.app.http.BaseClient;
import com.fanfou.app.http.BasicClient;
import com.fanfou.app.http.OAuthClient;
import com.fanfou.app.http.Parameter;
import com.fanfou.app.http.Request;
import com.fanfou.app.http.Response;
import com.fanfou.app.http.ResponseCode;
import com.fanfou.app.util.StringHelper;

/**
 * @author mcxiaoke
 * @version 1.0 2011.05.15
 * @version 1.1 2011.05.17
 * 
 */
public class ApiImpl implements Api, ResponseCode {
	// oauth is on or off
	private static final boolean OAUTH_ON = true;
	private static final String TAG = ApiImpl.class.getSimpleName();

	private BaseClient mClient;

	/**
	 * log for debug
	 * 
	 * @param message
	 */
	private void log(String message) {
		Log.d(TAG, message);
	}

	public ApiImpl(Context context) {
		if (App.DEBUG)
			log("new api instance");
		if (OAUTH_ON) {
			mClient = new OAuthClient();
		} else {
			mClient = new BasicClient("test", "test");
		}
	}

	/**
	 * handler http response with non-200 statusCode
	 * 
	 * @param response
	 * @throws ApiException
	 */
	// private void checkResponse(Response response) throws ApiException {
	// if (response == null || response.statusCode == HTTP_OK) {
	// return;
	// } else {
	// throw new ApiException(response.statusCode, Parser.error(response));
	// }
	// switch (response.statusCode) {
	// case HTTP_UNAUTHORIZED:
	// case HTTP_BAD_REQUEST:
	// case HTTP_FORBIDDEN:
	// case HTTP_NOT_FOUND:
	// case HTTP_INTERNAL_SERVER_ERROR:
	// case HTTP_BAD_GATEWAY:
	// case HTTP_SERVICE_UNAVAILABLE:
	// case ERROR_NOT_CONNECTED:
	// default:
	// throw new ApiException(response.statusCode, Parser.error(response));
	// }
	// }

	/**
	 * exec http request
	 * 
	 * @param request
	 * @return response object
	 * @throws ApiException
	 */
	private Response fetch(Request request) throws ApiException {
		try {
			// long startTime = System.currentTimeMillis();
			HttpResponse response = mClient.exec(request);
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
			throw new ApiException(ERROR_NOT_CONNECTED, "Connection error: "
					+ e.getMessage(), e.getCause());
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
		Request request = null;
		if (StringHelper.isEmpty(userId)) {
			request = new Request(url);
		} else {
			List<Parameter> params = new ArrayList<Parameter>();
			params.add(new Parameter("id", userId));
			request = new Request(url, params);
		}
		if (App.DEBUG)
			log("fetchUsers()---request=" + request.toString());

		Response response = fetch(request);
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
		List<Parameter> params = new ArrayList<Parameter>();
		if (count > 0 && count < 20) {
			params.add(new Parameter("count", count));
		}
		if (page > 0) {
			params.add(new Parameter("page", page));
		}
		if (!StringHelper.isEmpty(userId)) {
			params.add(new Parameter("id", userId));
		}
		if (!StringHelper.isEmpty(sinceId)) {
			params.add(new Parameter("since_id", sinceId));
		}
		if (!StringHelper.isEmpty(maxId)) {
			params.add(new Parameter("max_id", maxId));
		}
		if (isHtml) {
			params.add(new Parameter("format", "html"));
		}
		Request request = new Request(url, params);
		if (App.DEBUG) {
			log("fetchStatuses()---url=" + url + " count=" + count + " page="
					+ page + " userid=" + userId + " sinceId=" + sinceId
					+ " maxid=" + maxId);
			log("fetchStatuses()---request=" + request.toString());
		}
		Response response = fetch(request);
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
		// if (Utils.isEmpty(id)) {
		// throw new IllegalArgumentException("ID参数不能为空");
		// }
		Request request = null;
		if (StringHelper.isEmpty(id)) {
			request = new Request(url, isPost);
		} else {
			List<Parameter> params = new ArrayList<Parameter>();
			params.add(new Parameter("id", id));
			request = new Request(url, params, isPost);
		}
		if (App.DEBUG)
			log("doSingleIdAction()---request=" + request.toString());
		return fetch(request);
	}

	@Override
	public User verifyAccount() throws ApiException {
		Request request = new Request(URL_VERIFY_CREDENTIALS);
		Response response = fetch(request);
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
		for (Status status : ss) {
			CacheManager.put(status.user);
		}
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
		if(s!=null){
			CacheManager.put(s);
		}
		return s;
	}

	@Override
	public Status statusUnfavorite(String statusId) throws ApiException {
		Response response = doPostIdAction(URL_FAVORITES_DESTROY, statusId);
		int statusCode = response.statusCode;
		if (App.DEBUG) {
			log("statusUnfavorite()---statusCode=" + statusCode);
		}

		// handlerResponseError(response);
		Status s = Status.parse(response);
		if(s!=null){
			CacheManager.put(s);
		}
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
		Status s= Status.parse(response);
		if(s!=null){
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
		List<Parameter> params = new ArrayList<Parameter>();
		params.add(new Parameter("status", status));
		if (!StringHelper.isEmpty(inReplyToStatusId)) {
			params.add(new Parameter("in_reply_to_status_id", inReplyToStatusId));
		}
		if (!StringHelper.isEmpty(source)) {
			params.add(new Parameter("source", source));
		}
		if (!StringHelper.isEmpty(location)) {
			params.add(new Parameter("location", location));
		}
		if (!StringHelper.isEmpty(repostStatusId)) {
			params.add(new Parameter("repost_status_id", repostStatusId));
		}

		Request request = new Request(URL_STATUS_UPDATE, params, true);
		if (App.DEBUG)
			log("statusUpdate() request=" + request.toString());
		Response response = fetch(request);
		int statusCode = response.statusCode;
		if (App.DEBUG) {
			log("statusUpdate()---statusCode=" + statusCode);
		}
		if (StringHelper.isEmpty(response.getContent())) {
			throw new ApiException(ERROR_DUPLICATE, "重复消息，发送失败");
		}
		Status s= Status.parse(response,Status.TYPE_HOME);
		if(s!=null){
			CacheManager.put(s);
		}
		return s;
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
		List<Parameter> params = new ArrayList<Parameter>();
		params.add(new Parameter("photo", photo));
		params.add(new Parameter("status", status));
		if (!StringHelper.isEmpty(source)) {
			params.add(new Parameter("source", source));
		}
		if (!StringHelper.isEmpty(location)) {
			params.add(new Parameter("location", location));
		}
		Request request = new Request(URL_PHOTO_UPLOAD, params, true);
		if (App.DEBUG)
			log("photoUpload() request=" + request.toString());
		Response response = fetch(request);
		int statusCode = response.statusCode;
		if (App.DEBUG) {
			log("photoUpload()---statusCode=" + statusCode);
		}
		Status s= Status.parse(response,Status.TYPE_HOME);
		if(s!=null){
			CacheManager.put(s);
		}
		return s;
	}

	@Override
	public List<Status> search(String keyword, String maxId, boolean isHtml)
			throws ApiException {
		if (StringHelper.isEmpty(keyword)) {
			if (App.DEBUG)
				throw new IllegalArgumentException("搜索词不能为空");
			return null;
		}
		List<Parameter> params = new ArrayList<Parameter>();
		params.add(new Parameter("q", keyword));
		if (!StringHelper.isEmpty(maxId)) {
			params.add(new Parameter("max_id", maxId));
		}
		if (isHtml) {
			params.add(new Parameter("format", "html"));
		}
		Request request = new Request(URL_SEARCH_PUBLIC, params);
		if (App.DEBUG)
			log("search() request=" + request.toString());
		Response response = fetch(request);

		int statusCode = response.statusCode;
		if (App.DEBUG) {
			log("search()---statusCode=" + statusCode);
		}
		return Status.parseStatuses(response, Status.TYPE_SEARCH);

	}

	@Override
	public List<Search> trends() throws ApiException {
		Request request = new Request(URL_SEARCH_TRENDS);
		log("trends() request=" + request.toString());
		Response response = fetch(request);

		int statusCode = response.statusCode;
		if (App.DEBUG) {
			log("trends()---statusCode=" + statusCode);
		}

		// handlerResponseError(response);
		return Parser.trends(response);

	}

	@Override
	public List<Search> savedSearches() throws ApiException {
		Request request = new Request(URL_SEARCH_SAVED);
		if (App.DEBUG)
			log("savedSearches() request=" + request.toString());
		Response response = fetch(request);

		int statusCode = response.statusCode;
		if (App.DEBUG) {
			log("savedSearches()---statusCode=" + statusCode);
		}

		// handlerResponseError(response);
		return Parser.savedSearches(response);

	}

	@Override
	public Search savedSearchShow(int id) throws ApiException {
		List<Parameter> params = new ArrayList<Parameter>();
		params.add(new Parameter("id", id));
		Request request = new Request(URL_SEARCH_SAVED_ID, params);
		if (App.DEBUG)
			log("savedSearchShow() request=" + request.toString());
		Response response = fetch(request);

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
		List<Parameter> params = new ArrayList<Parameter>();
		params.add(new Parameter("query", query));
		Request request = new Request(URL_SEARCH_SAVED_CREATE, params, true);
		if (App.DEBUG)
			log("savedSearchCreate() request=" + request.toString());
		Response response = fetch(request);

		int statusCode = response.statusCode;
		if (App.DEBUG) {
			log("savedSearchCreate()---statusCode=" + statusCode);
		}
		// handlerResponseError(response);
		return Parser.savedSearch(response);
	}

	@Override
	public Search savedSearchDelete(int id) throws ApiException {
		List<Parameter> params = new ArrayList<Parameter>();
		params.add(new Parameter("id", id));
		Request request = new Request(URL_SEARCH_SAVED_DESTROY, params, true);
		if (App.DEBUG)
			log("savedSearchesDelete request=" + request.toString());
		Response response = fetch(request);
		int statusCode = response.statusCode;
		if (App.DEBUG) {
			log("savedSearchDelete()---statusCode=" + statusCode);
		}

		// handlerResponseError(response);
		return Parser.savedSearch(response);
	}

	private List<User> fetchUsers(String url, String userId, int page)
			throws ApiException {
		List<Parameter> params = new ArrayList<Parameter>();
		if (!StringHelper.isEmpty(userId)) {
			params.add(new Parameter("id", userId));
		}
		if (page > 0) {
			params.add(new Parameter("page", page));
		}
		Request request = new Request(url, params);
		Response response = fetch(request);

		return User.parseUsers(response);
	}

	@Override
	public List<User> usersFriends(String userId, int page) throws ApiException {
		List<User> users = fetchUsers(URL_USERS_FRIENDS, userId, page);
		if (users != null && users.size() > 0) {
			for (User user : users) {
				user.type = User.TYPE_FRIENDS;
				user.ownerId = (userId == null ? App.me.userId : userId);
			}
		}
		return users;
	}

	@Override
	public List<User> usersFollowers(String userId, int page)
			throws ApiException {
		List<User> users = fetchUsers(URL_USERS_FOLLOWERS, userId, page);
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
	// 此方法API未实现
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
	// 此方法API未实现
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
		List<Parameter> params = new ArrayList<Parameter>();
		params.add(new Parameter("user_a", userA));
		params.add(new Parameter("user_b", userB));
		Request request = new Request(URL_FRIENDSHIS_EXISTS, params);
		if (App.DEBUG)
			log("isFriends() request=" + request.toString());
		Response response = fetch(request);

		int statusCode = response.statusCode;
		if (App.DEBUG)
			log("isFriends()---statusCode=" + statusCode);
		String content = response.getContent();
		if (App.DEBUG)
			log("isFriends()---response=" + content);
		return content.contains("true");

	}

	private List<String> ids(String url, String userId, int count, int page)
			throws ApiException {
		List<Parameter> params = new ArrayList<Parameter>();
		if (!StringHelper.isEmpty(userId)) {
			params.add(new Parameter("id", userId));
		}
		if (count > 0) {
			params.add(new Parameter("count", count));
		}
		if (page > 0) {
			params.add(new Parameter("page", page));
		}
		Request request = new Request(url, params);

		Response response = fetch(request);
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
		List<Parameter> params = new ArrayList<Parameter>();
		if (count > 0 && count < 20) {
			params.add(new Parameter("count", count));
		}
		if (page > 0) {
			params.add(new Parameter("page", page));
		}
		if (!StringHelper.isEmpty(sinceId)) {
			params.add(new Parameter("since_id", sinceId));
		}
		if (!StringHelper.isEmpty(maxId)) {
			params.add(new Parameter("max_id", maxId));
		}
		Request request = new Request(url, params);
		if (App.DEBUG)
			log("messages() request=" + request.toString());
		Response response = fetch(request);
		int statusCode = response.statusCode;
		if (App.DEBUG) {
			log("messages()---statusCode=" + statusCode);
		}

		// handlerResponseError(response);
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
		List<Parameter> params = new ArrayList<Parameter>();
		params.add(new Parameter("user", userId));
		params.add(new Parameter("text", text));
		if (!StringHelper.isEmpty(inReplyToId)) {
			params.add(new Parameter("in_reply_to_id", inReplyToId));
		}
		Request request = new Request(URL_DIRECT_MESSAGES_NEW, params, true);
		if (App.DEBUG)
			log("messageCreate() send request=" + request.toString());
		Response response = fetch(request);
		int statusCode = response.statusCode;
		if (App.DEBUG) {
			log("messageCreate()---statusCode=" + statusCode);
		}

		DirectMessage dm= DirectMessage.parse(response);
		if(dm!=null&&!dm.isNull()){
			dm.type = DirectMessage.TYPE_OUT;
			dm.threadUserId = dm.recipientId;
			dm.threadUserName = dm.recipientScreenName;
			return dm;
		}else{
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
		// TODO Auto-generated method stub
		return null;
	}

}
