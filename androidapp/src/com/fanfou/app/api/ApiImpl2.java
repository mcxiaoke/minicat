package com.fanfou.app.api;

import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.HttpResponse;
import org.apache.http.HttpVersion;
import org.apache.http.ParseException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.conn.ClientConnectionManager;
import org.apache.http.conn.params.ConnManagerParams;
import org.apache.http.conn.params.ConnPerRoute;
import org.apache.http.conn.routing.HttpRoute;
import org.apache.http.conn.scheme.PlainSocketFactory;
import org.apache.http.conn.scheme.Scheme;
import org.apache.http.conn.scheme.SchemeRegistry;
import org.apache.http.entity.mime.MultipartEntity;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.entity.mime.content.StringBody;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.conn.tsccm.ThreadSafeClientConnManager;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.apache.http.params.HttpProtocolParams;
import org.apache.http.protocol.HTTP;

import android.content.Context;
import android.net.Uri;
import android.util.Log;

import com.fanfou.app.App;
import com.fanfou.app.auth.OAuth;
import com.fanfou.app.http.Response;
import com.fanfou.app.http.ResponseCode;
import com.fanfou.app.util.NetworkHelper;
import com.fanfou.app.util.StringHelper;
import com.fanfou.app.util.Utils;

/**
 * @author mcxiaoke
 * @version 1.0 2011.05.15
 * @version 1.1 2011.05.17
 * 
 */
public class ApiImpl2 implements Api, ResponseCode {
	// oauth is on or off
	private static final boolean OAUTH_ON = true;
	private static final String TAG = ApiImpl2.class.getSimpleName();

//	private OAuthConsumer mConsumer = null;
	private HttpClient mClient;

	/**
	 * log for debug
	 * 
	 * @param message
	 */
	private void log(String message) {
		Log.e(TAG, message);
	}

	public ApiImpl2(Context context) {
		if (App.DEBUG)
			log("new ApiImpl2 instance");
		init();
	}

	private void init() {
		mClient = NetworkHelper.setHttpClient();

//		mConsumer = new CommonsHttpOAuthConsumer(ApiConfig.CONSUMER_KEY,
//				ApiConfig.CONSUMER_SECRET);
//		mConsumer.setTokenWithSecret(App.me.oauthAccessToken,
//				App.me.oauthAccessTokenSecret);
	}

	private Response doPost(String url, ArrayList<BasicNameValuePair> params)
			throws ApiException {
		if (App.DEBUG) {
			log("doPost url=" + url);
		}
		HttpPost post = new HttpPost(url);
		try {
			post.setEntity(new UrlEncodedFormEntity(params));
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		try {
			long st = System.currentTimeMillis();
			HttpResponse res = request(post);
			Response response = new Response(res);
			Utils.logTime("doPost url=" + url, System.currentTimeMillis() - st);
			return response;
		} catch (IOException e) {
			throw new ApiException(ERROR_NOT_CONNECTED, e.getMessage(),
					e.getCause());
		}
	}

	private Response doPost(String url, ArrayList<BasicNameValuePair> params,
			String filename, File file) throws ApiException {
		HttpPost post = new HttpPost(url);
		try {
			MultipartEntity entity = new MultipartEntity();
			for (BasicNameValuePair param : params) {
				entity.addPart(param.getName(),
						new StringBody(param.getValue()));
			}
			entity.addPart(filename, new FileBody(file));
			post.setEntity(entity);
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		try {
			HttpResponse response = request(post);
			int statusCode = response.getStatusLine().getStatusCode();
			if (statusCode == 200) {
				return new Response(response);
			}
			throw new ApiException(statusCode, Parser.error(response));
		} catch (IOException e) {
			throw new ApiException(ERROR_NOT_CONNECTED, e.getMessage(),
					e.getCause());
		}
	}

	private Response doGet(String url) throws ApiException {
		HttpGet get = new HttpGet(url);
		try {
			HttpResponse response = request(get);
			int statusCode = response.getStatusLine().getStatusCode();
			if (statusCode == 200) {
				return new Response(response);
			}
			throw new ApiException(statusCode, Parser.error(response));
		} catch (IOException e) {
			e.printStackTrace();
			throw new ApiException(ERROR_NOT_CONNECTED, e.getMessage(),
					e.getCause());
		}
	}

	private Response doGet(String url, ArrayList<BasicNameValuePair> params)
			throws ApiException {
		Uri sUri = Uri.parse(url);
		Uri.Builder builder = sUri.buildUpon();
		for (BasicNameValuePair param : params) {
			builder.appendQueryParameter(param.getName(), param.getValue());
		}
		String requestUrl = builder.build().toString();
		if (App.DEBUG) {
			log("doGet url=" + requestUrl);
		}
		HttpGet get = new HttpGet(requestUrl);
		try {
			long st = System.currentTimeMillis();
			HttpResponse response = request(get);
			Utils.logTime("doGet url=" + requestUrl, System.currentTimeMillis()
					- st);
			int statusCode = response.getStatusLine().getStatusCode();
			if (statusCode == 200) {
				return new Response(response);
			}
			throw new ApiException(statusCode, Parser.error(response));
		} catch (IOException e) {
			e.printStackTrace();
			throw new ApiException(ERROR_NOT_CONNECTED, e.getMessage(),
					e.getCause());
		}
	}

	private HttpResponse request(HttpRequestBase request) throws IOException {
//		try {
//			mConsumer.setTokenWithSecret(App.me.oauthAccessToken,
//					App.me.oauthAccessTokenSecret);
//			mConsumer.sign(request);
//		} catch (OAuthMessageSignerException e) {
//			e.printStackTrace();
//		} catch (OAuthExpectationFailedException e) {
//			e.printStackTrace();
//		} catch (OAuthCommunicationException e) {
//			e.printStackTrace();
//		}
		// return mClient.execute(request);
		return new DefaultHttpClient().execute(request);
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
		Response response = null;
		if (StringHelper.isEmpty(userId)) {
			response = doGet(url);
		} else {
			ArrayList<BasicNameValuePair> params = new ArrayList<BasicNameValuePair>();
			params.add(new BasicNameValuePair("id", userId));
			response = doGet(url, params);
		}
		int statusCode = response.statusCode;
		if (App.DEBUG) {
			log("fetchUsers()---url=" + url + " userid=" + userId);
			log("fetchStatuses()---statusCode=" + statusCode);
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
			String sinceId, String maxId, boolean isHtml, int type)
			throws ApiException {
		ArrayList<BasicNameValuePair> params = new ArrayList<BasicNameValuePair>();
		if (count > 0 && count < 20) {
			params.add(new BasicNameValuePair("count", String.valueOf(count)));
		}
		if (page > 0) {
			params.add(new BasicNameValuePair("page", String.valueOf(page)));
		}
		if (!StringHelper.isEmpty(userId)) {
			params.add(new BasicNameValuePair("id", userId));
		}
		if (!StringHelper.isEmpty(sinceId)) {
			params.add(new BasicNameValuePair("since_id", sinceId));
		}
		if (!StringHelper.isEmpty(maxId)) {
			params.add(new BasicNameValuePair("max_id", maxId));
		}
		if (isHtml) {
			params.add(new BasicNameValuePair("format", "html"));
		}
		if (App.DEBUG) {
			log("fetchStatuses()---url=" + url + " count=" + count + " page="
					+ page + " userid=" + userId + " sinceId=" + sinceId
					+ " maxid=" + maxId);
		}
		Response response = doGet(url, params);
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
		if (Utils.isEmpty(id)) {
			throw new IllegalArgumentException("ID参数不能为空");
		}
		ArrayList<BasicNameValuePair> params = new ArrayList<BasicNameValuePair>();
		params.add(new BasicNameValuePair("id", id));
		if (isPost) {
			return doPost(url, params);
		} else {
			return doGet(url, params);
		}
	}

	@Override
	public User verifyAccount() throws ApiException {
		Response response = doGet(URL_VERIFY_CREDENTIALS);
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

		// handlerResponseError(response);
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

		// handlerResponseError(response);
		Status s = Status.parse(response);
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
		ArrayList<BasicNameValuePair> params = new ArrayList<BasicNameValuePair>();
		params.add(new BasicNameValuePair("status", status));
		if (!StringHelper.isEmpty(inReplyToStatusId)) {
			params.add(new BasicNameValuePair("in_reply_to_status_id",
					inReplyToStatusId));
		}
		if (!StringHelper.isEmpty(source)) {
			params.add(new BasicNameValuePair("source", source));
		}
		if (!StringHelper.isEmpty(location)) {
			params.add(new BasicNameValuePair("location", location));
		}
		if (!StringHelper.isEmpty(repostStatusId)) {
			params.add(new BasicNameValuePair("repost_status_id",
					repostStatusId));
		}
		Response response = doPost(URL_STATUS_UPDATE, params);
		int statusCode = response.statusCode;
		if (App.DEBUG) {
			log("statusUpdate()---statusCode=" + statusCode);
		}
		if (StringHelper.isEmpty(response.getContent())) {
			throw new ApiException(ERROR_DUPLICATE, "重复消息，发送失败");
		}
		return Status.parse(response);
	}

	@Override
	public Status statusDelete(String statusId) throws ApiException {
		Response response = doPostIdAction(URL_STATUS_DESTROY, statusId);
		int statusCode = response.statusCode;
		if (App.DEBUG) {
			log("statusDelete()---statusCode=" + statusCode);
		}
		Status s = Status.parse(response);
		if (s != null) {
			s.ownerId = App.me.userId;
		}
		return s;
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
		ArrayList<BasicNameValuePair> params = new ArrayList<BasicNameValuePair>();
		params.add(new BasicNameValuePair("status", status));
		if (!StringHelper.isEmpty(source)) {
			params.add(new BasicNameValuePair("source", source));
		}
		if (!StringHelper.isEmpty(location)) {
			params.add(new BasicNameValuePair("location", location));
		}
		Response response = doPost(URL_PHOTO_UPLOAD, params, "photo", photo);
		int statusCode = response.statusCode;
		if (App.DEBUG) {
			log("photoUpload()---statusCode=" + statusCode);
		}
		Status s = Status.parse(response);
		if (s != null) {
			s.ownerId = App.me.userId;
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
		ArrayList<BasicNameValuePair> params = new ArrayList<BasicNameValuePair>();
		params.add(new BasicNameValuePair("q", keyword));
		if (!StringHelper.isEmpty(maxId)) {
			params.add(new BasicNameValuePair("max_id", maxId));
		}
		if (isHtml) {
			params.add(new BasicNameValuePair("format", "html"));
		}
		Response response = doGet(URL_SEARCH_PUBLIC, params);

		int statusCode = response.statusCode;
		if (App.DEBUG) {
			log("search()---statusCode=" + statusCode);
		}
		return Status.parseStatuses(response, Status.TYPE_SEARCH);

	}

	@Override
	public List<Search> trends() throws ApiException {
		Response response = doGet(URL_SEARCH_TRENDS);

		int statusCode = response.statusCode;
		if (App.DEBUG) {
			log("trends()---statusCode=" + statusCode);
		}
		return Parser.trends(response);

	}

	@Override
	public List<Search> savedSearches() throws ApiException {
		Response response = doGet(URL_SEARCH_SAVED);

		int statusCode = response.statusCode;
		if (App.DEBUG) {
			log("savedSearches()---statusCode=" + statusCode);
		}
		return Parser.savedSearches(response);

	}

	@Override
	public Search savedSearchShow(int id) throws ApiException {
		// ArrayList<BasicNameValuePair> params = new
		// ArrayList<BasicNameValuePair>();
		// params.add(new BasicNameValuePair("id", String.valueOf(id)));
		// Response response = doGet(URL_SEARCH_SAVED_ID, params);
		Response response = doSingleIdAction(URL_SEARCH_SAVED_ID,
				String.valueOf(id), false);
		int statusCode = response.statusCode;
		if (App.DEBUG) {
			log("savedSearchShow()---statusCode=" + statusCode);
		}
		return Parser.savedSearch(response);

	}

	@Override
	public Search savedSearchCreate(String query) throws ApiException {
		if (StringHelper.isEmpty(query)) {
			if (App.DEBUG)
				throw new IllegalArgumentException("搜索词不能为空");
			return null;
		}
		ArrayList<BasicNameValuePair> params = new ArrayList<BasicNameValuePair>();
		params.add(new BasicNameValuePair("query", query));
		Response response = doPost(URL_SEARCH_SAVED_CREATE, params);
		int statusCode = response.statusCode;
		if (App.DEBUG) {
			log("savedSearchCreate()---statusCode=" + statusCode);
		}
		// handlerResponseError(response);
		return Parser.savedSearch(response);
	}

	@Override
	public Search savedSearchDelete(int id) throws ApiException {
		Response response = doSingleIdAction(URL_SEARCH_SAVED_DESTROY,
				String.valueOf(id), true);
		int statusCode = response.statusCode;
		if (App.DEBUG) {
			log("savedSearchDelete()---statusCode=" + statusCode);
		}
		return Parser.savedSearch(response);
	}

	private List<User> fetchUsers(String url, String userId, int page)
			throws ApiException {
		ArrayList<BasicNameValuePair> params = new ArrayList<BasicNameValuePair>();
		if (!StringHelper.isEmpty(userId)) {
			params.add(new BasicNameValuePair("id", userId));
		}
		if (page > 0) {
			params.add(new BasicNameValuePair("page", String.valueOf(page)));
		}
		Response response = doGet(url, params);
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
		ArrayList<BasicNameValuePair> params = new ArrayList<BasicNameValuePair>();
		params.add(new BasicNameValuePair("user_a", userA));
		params.add(new BasicNameValuePair("user_b", userB));
		Response response = doGet(URL_FRIENDSHIS_EXISTS, params);
		if (App.DEBUG)
			log("isFriends()---statusCode=" + response.statusCode);
		String content = response.getContent();
		if (App.DEBUG)
			log("isFriends()---response=" + content);
		return content.contains("true");

	}

	private List<String> ids(String url, String userId, int count, int page)
			throws ApiException {
		ArrayList<BasicNameValuePair> params = new ArrayList<BasicNameValuePair>();
		if (!StringHelper.isEmpty(userId)) {
			params.add(new BasicNameValuePair("id", userId));
		}
		if (count > 0) {
			params.add(new BasicNameValuePair("count", String.valueOf(count)));
		}
		if (page > 0) {
			params.add(new BasicNameValuePair("page", String.valueOf(count)));
		}
		Response response = doGet(url, params);
		int statusCode = response.statusCode;
		if (App.DEBUG) {
			log("ids()---statusCode=" + statusCode);
		}
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

	@Override
	public List<DirectMessage> messagesInbox(int count, int page,
			String sinceId, String maxId) throws ApiException {
		List<DirectMessage> dms = messages(URL_DIRECT_MESSAGES_INBOX, count,
				page, sinceId, maxId);
		// if (dms != null && dms.size() > 0) {
		// for (DirectMessage dm : dms) {
		// dm.type = DirectMessage.TYPE_IN;
		// }
		//
		// }

		return dms;
	}

	private List<DirectMessage> messages(String url, int count, int page,
			String sinceId, String maxId) throws ApiException {
		ArrayList<BasicNameValuePair> params = new ArrayList<BasicNameValuePair>();
		if (count > 0 && count < 20) {
			params.add(new BasicNameValuePair("count", String.valueOf(count)));
		}
		if (page > 0) {
			params.add(new BasicNameValuePair("page", String.valueOf(page)));
		}
		if (!StringHelper.isEmpty(sinceId)) {
			params.add(new BasicNameValuePair("since_id", sinceId));
		}
		if (!StringHelper.isEmpty(maxId)) {
			params.add(new BasicNameValuePair("max_id", maxId));
		}
		Response response = doGet(url, params);
		int statusCode = response.statusCode;
		if (App.DEBUG) {
			log("messages()---statusCode=" + statusCode);
		}
		return DirectMessage.parseMessges(response);
	}

	@Override
	public List<DirectMessage> messagesOutbox(int count, int page,
			String sinceId, String maxId) throws ApiException {
		List<DirectMessage> dms = messages(URL_DIRECT_MESSAGES_OUTBOX, count,
				page, sinceId, maxId);

		// if (dms != null && dms.size() > 0) {
		// for (DirectMessage dm : dms) {
		// dm.type = DirectMessage.TYPE_OUT;
		// }
		//
		// }

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
		ArrayList<BasicNameValuePair> params = new ArrayList<BasicNameValuePair>();
		params.add(new BasicNameValuePair("user", userId));
		params.add(new BasicNameValuePair("text", text));
		if (!StringHelper.isEmpty(inReplyToId)) {
			params.add(new BasicNameValuePair("in_reply_to_id", inReplyToId));
		}
		Response response = doPost(URL_DIRECT_MESSAGES_NEW, params);
		int statusCode = response.statusCode;
		if (App.DEBUG) {
			log("messageCreate()---statusCode=" + statusCode);
		}
		return DirectMessage.parse(response);
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

}
