/**
 *
 */
package com.mcxiaoke.minicat.api;

import android.text.TextUtils;
import android.util.Log;
import com.mcxiaoke.minicat.AppContext;
import com.mcxiaoke.minicat.dao.model.BaseModel;
import com.mcxiaoke.minicat.dao.model.DirectMessageModel;
import com.mcxiaoke.minicat.dao.model.Notifications;
import com.mcxiaoke.minicat.dao.model.RateLimitStatus;
import com.mcxiaoke.minicat.dao.model.Search;
import com.mcxiaoke.minicat.dao.model.StatusModel;
import com.mcxiaoke.minicat.dao.model.UserModel;
import org.apache.http.protocol.HTTP;
import org.oauthsimple.builder.ServiceBuilder;
import org.oauthsimple.builder.api.FanfouApi;
import org.oauthsimple.http.OAuthRequest;
import org.oauthsimple.http.Response;
import org.oauthsimple.http.Verb;
import org.oauthsimple.model.OAuthToken;
import org.oauthsimple.model.SignatureType;
import org.oauthsimple.oauth.OAuthService;

import java.io.File;
import java.io.IOException;
import java.io.PrintStream;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.BitSet;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * @author mcxiaoke
 * @version 1.6 2012.03.02
 */
final class FanFouApi implements Api {
    private static final String TAG = "API";
    private static final String API_HOST = "http://api.fanfou.com";
    private static final String API_KEY = "e5dd03165aebdba16611e1f4849ce2c3";
    private static final String API_SECRET = "2a14fcbdebfb936a769840b4d5a9263b";
    private static final String CALLBACK_URL = "http://m.fanfou.com";

    private static final boolean DEBUG = AppContext.DEBUG;
    private OAuthService mOAuthService;
    private OAuthToken mAccessToken;
    private ApiParser mParser;
    private String account;

    public FanFouApi() {
        initialize(null);
    }

    public FanFouApi(OAuthToken token) {
        initialize(token);
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

    private void initialize(OAuthToken token) {
        this.mParser = ApiFactory.getDefaultParser();
        this.mOAuthService = buildOAuthService(null);
    }

    private OAuthService buildOAuthService(OAuthToken token) {
        ServiceBuilder builder = new ServiceBuilder().apiKey(API_KEY)
                .apiSecret(API_SECRET).callback(CALLBACK_URL)
                .provider(FanfouApi.class)
                .signatureType(SignatureType.HEADER_OAUTH);
        if (DEBUG) {
            builder.debug().debugStream(new PrintStream(System.out));
        }
        return builder.build();
    }

    private void debug(String message) {
        Log.d(TAG, message);
    }

    private String makeUrl(String url) {
        return new StringBuilder().append(API_HOST).append(url).append(".json")
                .toString();
    }

    private UserModel fetchUser(String url, String id, int type, Verb verb)
            throws ApiException {
        RequestBuilder builder = RequestBuilder.newBuilder();
        builder.url(makeUrl(url)).id(id).verb(verb).mode("lite");
        return mParser.user(fetch(builder), type, account);
    }

    private UserModel fetchUser(String url, int type, Verb verb)
            throws ApiException {
        RequestBuilder builder = RequestBuilder.newBuilder();
        builder.url(makeUrl(url)).verb(verb).mode("lite");
        return mParser.user(fetch(builder), type, account);
    }

    private List<UserModel> fetchUsers(String url, Paging paging, int type)
            throws ApiException {
        return fetchUsers(url, null, paging, type);
    }

    private List<UserModel> fetchUsers(String url, String userId,
                                       Paging paging, int type) throws ApiException {
        RequestBuilder builder = RequestBuilder.newBuilder();
        builder.url(makeUrl(url)).id(userId).mode("lite").format("html");
        if (paging != null) {
            builder.paging(paging);
        }
        return mParser.users(fetch(builder), type, userId);
    }

    private List<StatusModel> fetchTimeline(String url, Paging paging,
                                            int type, String owner) throws ApiException {
        RequestBuilder builder = RequestBuilder.newBuilder();
        builder.url(makeUrl(url)).mode("lite").format("html").verb(Verb.GET);
        if (paging != null) {
            builder.paging(paging);
        }
        String response = fetch(builder);
        return mParser.timeline(response, type, owner);
    }

    private List<StatusModel> fetchTimeline(String url, Paging paging,
                                            String id, int type, String owner) throws ApiException {
        RequestBuilder builder = RequestBuilder.newBuilder();
        builder.url(makeUrl(url)).id(id).mode("lite").format("html")
                .verb(Verb.GET);
        if (paging != null) {
            builder.paging(paging);
        }
        String response = fetch(builder);
        return mParser.timeline(response, type, owner);
    }

    private StatusModel fetchStatus(String url, int type, Verb verb)
            throws ApiException {
        if (DEBUG) {
            Log.d(TAG, "fetchStatus url=" + url + " type=" + type + " verb="
                    + verb.name());
        }

        RequestBuilder builder = RequestBuilder.newBuilder();
        builder.url(makeUrl(url)).verb(verb).mode("lite").format("html");
        return mParser.status(fetch(builder), type, account);
    }

    private String fetchDirectMessages(String url, Paging paging, int type)
            throws ApiException {
        RequestBuilder builder = new RequestBuilder();
        builder.url(makeUrl(url)).paging(paging).mode("lite");
        return fetch(builder);
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

    /*
     * (non-Javadoc)
     *
     * @see http://fanfou.com/oauth/request_token
     *
     * @see https://github.com/FanfouAPI/FanFouAPIDoc/wiki/Oauth
     */
    @Override
    public OAuthToken getOAuthRequestToken() throws ApiException {
        try {
            return mOAuthService.getRequestToken();
        } catch (Exception e) {
            throw new ApiException(ApiException.IO_ERROR, e.getMessage(), e);
        }
    }

    @Override
    public OAuthToken getOAuthAccessToken(String username, String password)
            throws IOException, ApiException {
        try {
            return mOAuthService.getAccessToken(username, password);
        } catch (Exception e) {
            throw new ApiException(ApiException.IO_ERROR, e.getMessage(), e);
        }
    }

    @Override
    public void setAccessToken(OAuthToken token) {
        this.mAccessToken = token;
    }

    /*
     * GET/POST /account/verify_credentials
     *
     * @see
     * https://github.com/FanfouAPI/FanFouAPIDoc/wiki/account.verify-credentials
     */
    @Override
    public UserModel verifyCredentials() throws ApiException {
        return fetchUser("/account/verify_credentials", BaseModel.TYPE_NONE,
                Verb.POST);
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
        RequestBuilder builder = new RequestBuilder();
        builder.url(makeUrl("/account/update_profile")).verb(Verb.POST)
                .mode("lite");
        builder.param("description", description);
        builder.param("name", name);
        builder.param("location", location);
        builder.param("url", url);
        return mParser.user(fetch(builder), BaseModel.TYPE_NONE, account);
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
        RequestBuilder builder = new RequestBuilder();
        builder.url(makeUrl("/account/update_profile_image")).verb(Verb.POST);
        builder.file("image", image);
        return mParser.user(fetch(builder), BaseModel.TYPE_NONE, account);
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
        RequestBuilder builder = new RequestBuilder();
        builder.url(makeUrl("/blocks/ids"));
        String response = fetch(builder);
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
        return fetchUser("/blocks/exists", id, BaseModel.TYPE_NONE, Verb.GET);
    }

    /*
     * POST /blocks/create
     *
     * @see https://github.com/FanfouAPI/FanFouAPIDoc/wiki/blocks.create
     */
    @Override
    public UserModel block(String id) throws ApiException {
        checkNotEmpty(id);
        String url = String.format("/blocks/create/%s", utf8Encode(id));
        return fetchUser(url, UserModel.TYPE_BLOCK, Verb.POST);
    }

    /*
     * POST /blocks/destroy
     *
     * @see https://github.com/FanfouAPI/FanFouAPIDoc/wiki/blocks.destroy
     */
    @Override
    public UserModel unblock(String id) throws ApiException {
        checkNotEmpty(id);
        String url = String.format("/blocks/destroy/%s", utf8Encode(id));
        return fetchUser(url, UserModel.TYPE_BLOCK, Verb.POST);
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
        RequestBuilder builder = new RequestBuilder();
        builder.url(makeUrl("/direct_messages/conversation")).id(id)
                .paging(paging).mode("lite");
        String response = fetch(builder);
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
        RequestBuilder builder = new RequestBuilder();
        builder.url(makeUrl("/direct_messages/destroy")).verb(Verb.POST)
                .mode("lite");
        String response = fetch(builder);
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
        RequestBuilder builder = new RequestBuilder();
        builder.url(makeUrl("/direct_messages/new")).verb(Verb.POST);
        builder.param("user", id);
        builder.param("text", text);
        builder.param("in_reply_to_id", replyId);
        String response = fetch(builder);
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
        RequestBuilder builder = new RequestBuilder();
        builder.url(makeUrl("/friends/ids")).id(id).paging(paging);
        return mParser.strings(fetch(builder));
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
        RequestBuilder builder = new RequestBuilder();
        builder.url(makeUrl("/followers/ids")).id(id).paging(paging);
        return mParser.strings(fetch(builder));
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
        String url = String.format("/friendships/create/%s", utf8Encode(id));
        return fetchUser(url, UserModel.TYPE_BLOCK, Verb.POST);
    }

    /*
     * POST /friendships/destroy
     *
     * @see https://github.com/FanfouAPI/FanFouAPIDoc/wiki/friendships.destroy
     */
    @Override
    public UserModel unfollow(String id) throws ApiException {
        checkNotEmpty(id);
        String url = String.format("/friendships/destroy/%s", utf8Encode(id));
        return fetchUser(url, UserModel.TYPE_BLOCK, Verb.POST);
    }

    /*
     * GET /friendships/requests
     *
     * @see https://github.com/FanfouAPI/FanFouAPIDoc/wiki/friendships.requests
     */
    @Override
    public List<String> friendshipsRequests(Paging paging) throws ApiException {
        RequestBuilder builder = new RequestBuilder();
        builder.url(makeUrl("/blocks/ids")).paging(paging);
        String response = fetch(builder);
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
        return fetchUser("/friendships/accept", id, BaseModel.TYPE_NONE,
                Verb.POST);
    }

    /*
     * POST /friendships/deny
     *
     * @see https://github.com/FanfouAPI/FanFouAPIDoc/wiki/friendships.deny
     */
    @Override
    public UserModel denyFriendshipsRequest(String id) throws ApiException {
        checkNotEmpty(id);
        return fetchUser("/friendships/deny", id, BaseModel.TYPE_NONE,
                Verb.POST);
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
        RequestBuilder builder = new RequestBuilder();
        builder.url(makeUrl("/friendships/exists"));
        builder.param("user_a", userA);
        builder.param("user_b", userB);
        String response = fetch(builder);
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
        RequestBuilder builder = new RequestBuilder();
        builder.url(makeUrl("/friendships/exists"));
        builder.param("source_id", source);
        builder.param("target_id", target);
        String response = fetch(builder);
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
        RequestBuilder builder = new RequestBuilder();
        builder.url(makeUrl("/photos/upload")).verb(Verb.POST);
        builder.status(status);
        if (!TextUtils.isEmpty(location)) {
            builder.location(location);
        }
        builder.file("photo", photo);
        String response = fetch(builder);
        return mParser.status(response, StatusModel.TYPE_HOME, account);
    }

    /*
     * GET /saved_searches/list
     *
     * @see https://github.com/FanfouAPI/FanFouAPIDoc/wiki/saved-searches.list
     */
    @Override
    public List<Search> getSavedSearches() throws ApiException {
        RequestBuilder builder = new RequestBuilder();
        builder.url(makeUrl("/saved_searches/list"));
        String response = fetch(builder);
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
        RequestBuilder builder = new RequestBuilder();
        builder.url(makeUrl("/saved_searches/show")).id(id);
        String response = fetch(builder);
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
        RequestBuilder builder = new RequestBuilder();
        builder.url(makeUrl("/saved_searches/create")).param("query", query)
                .verb(Verb.POST);
        String response = fetch(builder);
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
        RequestBuilder builder = new RequestBuilder();
        builder.url(makeUrl("/saved_searches/destroy")).id(id).verb(Verb.POST);
        String response = fetch(builder);
        return mParser.savedSearch(response);
    }

    /*
     * GET /trends/list
     *
     * @see https://github.com/FanfouAPI/FanFouAPIDoc/wiki/trends.list
     */
    @Override
    public List<Search> getTrends() throws ApiException {
        RequestBuilder builder = new RequestBuilder();
        builder.url(makeUrl("/trends/list"));
        String response = fetch(builder);
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
        RequestBuilder builder = new RequestBuilder();
        builder.url(makeUrl("/search/public_timeline")).param("q", query);
        if (paging != null) {
            builder.paging(paging);
        }
        builder.mode("lite").format("html");
        String response = fetch(builder);
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
        RequestBuilder builder = new RequestBuilder();
        builder.url(makeUrl("/search/user_timeline")).id(id).param("q", query);
        if (paging != null) {
            builder.paging(paging);
        }
        builder.mode("lite").format("html");
        String response = fetch(builder);
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
        RequestBuilder builder = new RequestBuilder();
        builder.url(makeUrl("/search/users")).param("q", query);
        if (paging != null) {
            builder.paging(paging);
        }
        builder.mode("lite").format("html");
        String response = fetch(builder);
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
        return fetchStatus(url, BaseModel.TYPE_NONE, Verb.GET);
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
        return fetchStatus(url, BaseModel.TYPE_NONE, Verb.POST);
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
        RequestBuilder builder = new RequestBuilder();
        builder.url(makeUrl("/statuses/update")).verb(Verb.POST);
        builder.status(status);
        if (!TextUtils.isEmpty(location)) {
            builder.location(location);
        }
        builder.format("html").mode("lite");
        builder.param("in_reply_to_status_id", replyId);
        builder.param("repost_status_id", repostId);
        builder.param("location", location);
        String response = fetch(builder);
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
        return fetchUsers("/users/recommendation", paging, BaseModel.TYPE_NONE);
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
                BaseModel.TYPE_NONE, Verb.GET);
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
        RequestBuilder builder = RequestBuilder.newBuilder();
        builder.url(makeUrl("/users/tagged")).param("tag", tag).mode("lite")
                .format("html");
        if (paging != null) {
            builder.paging(paging);
        }
        return mParser.users(fetch(builder), BaseModel.TYPE_NONE, account);
    }

    /*
     * GET /users/tag_list
     *
     * @see https://github.com/FanfouAPI/FanFouAPIDoc/wiki/users.tag-list
     */
    @Override
    public List<String> getUserTags(String id) throws ApiException {
        checkNotEmpty(id);
        RequestBuilder builder = RequestBuilder.newBuilder();
        builder.url(makeUrl("/users/tag_list")).id(id);
        String response = fetch(builder);
        return mParser.strings(response);
    }

    /*
     * GET /users/show
     *
     * @see https://github.com/FanfouAPI/FanFouAPIDoc/wiki/users.show
     */
    @Override
    public UserModel showUser(String id) throws ApiException {
        checkNotEmpty(id);
        String url = String.format("/users/show/%s", utf8Encode(id));
        return fetchUser(url, UserModel.TYPE_BLOCK, Verb.GET);

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
        return fetchTimeline("/favorites", paging, id,
                StatusModel.TYPE_FAVORITES, id);
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
        return fetchStatus(url, BaseModel.TYPE_NONE, Verb.POST);
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
        return fetchStatus(url, BaseModel.TYPE_NONE, Verb.POST);
    }

    private String utf8Encode(String text) {
        try {
            return URLEncoder.encode(text, HTTP.UTF_8);
        } catch (UnsupportedEncodingException e) {
            return text;
        }
    }

    /**
     * @param builder
     * @return
     * @throws ApiException
     */
    private String fetch(final RequestBuilder builder) throws ApiException {
        OAuthRequest request = builder.build();
        request.setConnectTimeout(5, TimeUnit.SECONDS);
        request.setReadTimeout(10, TimeUnit.SECONDS);
        try {

            if (mOAuthService != null && mAccessToken != null) {
                mOAuthService.signRequest(mAccessToken, request);
            }

            Response response = request.send();
            int statusCode = response.getCode();
            String body = response.getBody();
            if (DEBUG) {
                debug("fetch() statusCode=" + statusCode + " builder=" + builder);
            }
            if (statusCode >= 200 && statusCode < 300) {
                return body;
            }
            throw new ApiException(statusCode, FanFouParser.error(body));
        } catch (IOException e) {
            if (DEBUG) {
                Log.e(TAG, e.toString());
            }
            throw new ApiException(ApiException.IO_ERROR, e.toString(),
                    e);
        }
    }

}