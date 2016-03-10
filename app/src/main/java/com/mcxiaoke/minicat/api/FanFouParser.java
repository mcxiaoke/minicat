package com.mcxiaoke.minicat.api;

import android.text.Html;
import android.text.TextUtils;
import com.mcxiaoke.minicat.AppContext;
import com.mcxiaoke.minicat.dao.model.BaseModel;
import com.mcxiaoke.minicat.dao.model.DirectMessageModel;
import com.mcxiaoke.minicat.dao.model.Search;
import com.mcxiaoke.minicat.dao.model.StatusModel;
import com.mcxiaoke.minicat.dao.model.UserModel;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.StringReader;
import java.text.ParsePosition;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.BitSet;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


/**
 * @author mcxiaoke
 * @version 4.2 2012.03.13
 */
final class FanFouParser implements ApiParser {

    public static final String TAG = FanFouParser.class.getSimpleName();
    public static final boolean DEBUG = AppContext.DEBUG;
    private static final String FANFOU_DATE_FORMAT_STRING = "EEE MMM dd HH:mm:ss Z yyyy";
    public static final SimpleDateFormat FANFOU_DATE_FORMAT = new SimpleDateFormat(
            FANFOU_DATE_FORMAT_STRING, Locale.US);
    private static final Pattern PATTERN_SOURCE = Pattern
            .compile("<a href.+blank\">(.+)</a>");
    private static final ParsePosition mPosition = new ParsePosition(0);
    private String account;

    public FanFouParser() {

    }

    public static Photo photo(JSONObject o) {
        try {
            Photo photo = new Photo();
            photo.url = o.getString("url");
            photo.imageUrl = o.getString("imageurl");
            photo.thumbUrl = o.getString("thumburl");
            photo.largeUrl = o.getString("largeurl");
            return photo;
        } catch (JSONException e) {
            return null;
        }
    }

    public static BitSet parseFriendship(String response) throws ApiException {
        try {
            JSONObject o = new JSONObject(response);
            JSONObject relationship = o.getJSONObject("relationship");
            JSONObject source = relationship.getJSONObject("source");
            BitSet state = new BitSet(3);
            boolean following = source.getBoolean("following");
            boolean followedBy = source.getBoolean("followed_by");
            boolean blocking = source.getBoolean("blocking");
            state.set(0, following);
            state.set(1, followedBy);
            state.set(2, blocking);
            return state;
        } catch (JSONException e) {
            throw new ApiException(ApiException.DATA_ERROR, e.getMessage(), e);
        }
    }

    public static String error(String response) {
//        if (DEBUG) {
//            Log.e(TAG, "error() response:" + response);
//        }
        String result = response;
        try {
            JSONObject o = new JSONObject(response);
            if (o.has("error")) {
                result = o.getString("error");
            }
        } catch (Exception e) {
            result = parseXMLError(response);
        }
        return result;
    }

    private static String parseXMLError(String error) {
        String result = error;
        XmlPullParser pull;
        String tag = null;
        try {
            pull = XmlPullParserFactory.newInstance().newPullParser();
            pull.setInput(new StringReader(error));
            boolean found = false;
            while (!found) {
                int eventType = pull.getEventType();
                switch (eventType) {
                    case XmlPullParser.START_TAG:
                        tag = pull.getName();
                        if (tag.equalsIgnoreCase("error")) {
                            result = pull.nextText();
                        }
                        break;
                    case XmlPullParser.END_TAG:
                        if (tag.equalsIgnoreCase("error")) {
                            found = true;
                        }
                        break;
                    default:
                        break;
                }
                pull.next();
            }
        } catch (Exception e) {
        }

        return result;
    }

    public static String parseSource(String input) {
        String source = input;
        Matcher m = PATTERN_SOURCE.matcher(input);
        if (m.find()) {
            source = m.group(1);
        }
        // Log.e("SourceParse", "source="+source);
        return source;
    }

    /**
     * @param s 代表饭否日期和时间的字符串
     * @return 字符串解析为对应的Date对象
     */
    public static Date date(String s) {
        return fanfouStringToDate(s);
    }

    public static Date fanfouStringToDate(String s) {
        // Fanfou Date String example --> "Mon Dec 13 03:10:21 +0000 2010"
        // final ParsePosition position = new ParsePosition(0);//
        // 这个如果放在方法外面的话，必须每次重置Index为0
        mPosition.setIndex(0);
        return FANFOU_DATE_FORMAT.parse(s, mPosition);
    }

    public static long stringToLong(String text) {
        try {
            return Long.parseLong(text);
        } catch (Exception e) {
            return -1;
        }
    }

    public static int stringToInt(String text) {
        try {
            return Integer.parseInt(text);
        } catch (Exception e) {
            return -1;
        }
    }

    @Override
    public void setAccount(String account) {
        this.account = account;
    }

    @Override
    public List<UserModel> users(String response, int type, String owner)
            throws ApiException {
        List<UserModel> us = new ArrayList<UserModel>();
        try {
            JSONArray array = new JSONArray(response);
            for (int i = 0; i < array.length(); i++) {
                JSONObject o = array.getJSONObject(i);
                UserModel user = user(o, type, owner);
                us.add(user);
            }
            return us;
        } catch (JSONException e) {
            throw new ApiException(ApiException.DATA_ERROR, e.getMessage(), e);
        }
    }

    @Override
    public UserModel user(String response, int type, String owner)
            throws ApiException {
        try {
            return user(new JSONObject(response), type, owner);
        } catch (JSONException e) {
            throw new ApiException(ApiException.DATA_ERROR, e.getMessage(), e);
        }
    }

    @Override
    public List<StatusModel> timeline(String response, int type, String owner)
            throws ApiException {
        List<StatusModel> ss = new ArrayList<StatusModel>();
        try {
            JSONArray array = new JSONArray(response);
            for (int i = 0; i < array.length(); i++) {
                JSONObject o = array.getJSONObject(i);
                StatusModel status = status(o, type, owner);
                ss.add(status);
            }
            return ss;
        } catch (JSONException e) {
            throw new ApiException(ApiException.DATA_ERROR, e.getMessage(), e);
        }
    }

    @Override
    public StatusModel status(String response, int type, String owner)
            throws ApiException {
        try {
            return status(new JSONObject(response), type, owner);
        } catch (JSONException e) {
            throw new ApiException(ApiException.DATA_ERROR, e.getMessage(), e);
        }
    }

    @Override
    public List<DirectMessageModel> directMessageConversation(String response,
                                                              String userId) throws ApiException {
        List<DirectMessageModel> dms = new ArrayList<DirectMessageModel>();
        try {
            JSONArray array = new JSONArray(response);
            for (int i = 0; i < array.length(); i++) {
                JSONObject o = array.getJSONObject(i);
                DirectMessageModel dm = directMessage(o,
                        BaseModel.TYPE_NONE);
                if (dm != null) {
                    if (account.equals(dm.getRecipientId())) {
                        dm.setType(DirectMessageModel.TYPE_INBOX);
                    } else {
                        dm.setType(DirectMessageModel.TYPE_OUTBOX);
                    }
                    dm.setConversationId(userId);
                    dms.add(dm);
                }
            }
            return dms;
        } catch (JSONException e) {
            throw new ApiException(ApiException.DATA_ERROR, e.getMessage(), e);
        }
    }

    @Override
    public List<DirectMessageModel> directMessagesConversationList(
            String response) throws ApiException {
        try {
            JSONArray array = new JSONArray(response);
            if (array.length() == 0) {
                return null;
            }
            List<DirectMessageModel> dms = new ArrayList<DirectMessageModel>();
            for (int i = 0; i < array.length(); i++) {
                JSONObject o = array.getJSONObject(i);
                JSONObject dmo = o.getJSONObject("dm");
                DirectMessageModel dm = directMessage(dmo,
                        DirectMessageModel.TYPE_CONVERSATION_LIST);
                String conversationId = o.getString("otherid");
                dm.setConversationId(conversationId);
                dm.setIncoming(dm.getSenderId().equals(conversationId));
                dms.add(dm);
            }
            return dms;
        } catch (JSONException e) {
            throw new ApiException(ApiException.DATA_ERROR, e.getMessage(), e);
        }
    }

    @Override
    public List<DirectMessageModel> directMessagesInBox(String response)
            throws ApiException {
        try {
            JSONArray array = new JSONArray(response);
            if (array.length() == 0) {
                return null;
            }
            List<DirectMessageModel> dms = new ArrayList<DirectMessageModel>();
            for (int i = 0; i < array.length(); i++) {
                JSONObject o = array.getJSONObject(i);
                DirectMessageModel dm = directMessage(o,
                        DirectMessageModel.TYPE_INBOX);
                dm.setConversationId(dm.getSenderId());
                dm.setIncoming(true);
                dms.add(dm);
            }
            return dms;
        } catch (Exception e) {
            throw new ApiException(ApiException.DATA_ERROR, e.getMessage(), e);
        }
    }

    @Override
    public List<DirectMessageModel> directMessagesOutBox(String response)
            throws ApiException {
        try {
            JSONArray array = new JSONArray(response);
            if (array.length() == 0) {
                return null;
            }
            List<DirectMessageModel> dms = new ArrayList<DirectMessageModel>();
            for (int i = 0; i < array.length(); i++) {
                JSONObject o = array.getJSONObject(i);
                DirectMessageModel dm = directMessage(o,
                        DirectMessageModel.TYPE_OUTBOX);
                dm.setConversationId(dm.getRecipientId());
                dm.setIncoming(false);
                dms.add(dm);
            }
            return dms;
        } catch (Exception e) {
            throw new ApiException(ApiException.DATA_ERROR, e.getMessage(), e);
        }
    }

    @Override
    public DirectMessageModel directMessage(String response, int type)
            throws ApiException {
        try {
            JSONObject o = new JSONObject(response);
            return directMessage(o, type);
        } catch (JSONException e) {
            throw new ApiException(ApiException.DATA_ERROR, e.getMessage(), e);
        }
    }

    @Override
    public List<Search> trends(String response) throws ApiException {

        try {
            List<Search> ss = new ArrayList<Search>();
            JSONObject o = new JSONObject(response);
            JSONArray a = o.getJSONArray("trends");
            final Date date = date(o.getString("as_of"));
            for (int i = 0; i < a.length(); i++) {
                JSONObject so = a.getJSONObject(i);
                Search sh = trend(so, date);
                if (sh != null) {
                    ss.add(sh);
                }
            }
            return ss;
        } catch (JSONException e) {
            throw new ApiException(ApiException.DATA_ERROR, e.getMessage(), e);
        }
    }

    @Override
    public List<Search> savedSearches(String response) throws ApiException {

        try {
            List<Search> ss = new ArrayList<Search>();

            JSONArray a = new JSONArray(response);
            for (int i = 0; i < a.length(); i++) {
                JSONObject o = a.getJSONObject(i);
                Search sh = savedSearch(o);
                if (sh != null) {
                    ss.add(sh);
                }
            }
            return ss;
        } catch (JSONException e) {
            throw new ApiException(ApiException.DATA_ERROR, e.getMessage(), e);
        }
    }

    @Override
    public Search savedSearch(String response) throws ApiException {
        try {
            return savedSearch(new JSONObject(response));
        } catch (JSONException e) {
            throw new ApiException(ApiException.DATA_ERROR, e.getMessage(), e);
        }
    }

    @Override
    public List<String> strings(String response) throws ApiException {
        try {
            JSONArray a = new JSONArray(response);
            ArrayList<String> ids = new ArrayList<String>();
            for (int i = 0; i < a.length(); i++) {
                ids.add(a.getString(i));
            }
            return ids;
        } catch (JSONException e) {
            throw new ApiException(ApiException.DATA_ERROR, e.getMessage(), e);
        }
    }

    private UserModel user(JSONObject o, int type, String owner)
            throws JSONException {
        UserModel model = new UserModel();
        model.setId(o.getString("id"));
        model.setAccount(account);
        model.setOwner(owner);
        // model.setNote();

        model.setType(type);
        model.setFlag(0);

        model.setRawid(0);
        model.setTime(FanFouParser.date(o.getString("created_at")).getTime());

        model.setName(o.getString("name"));
        model.setScreenName(o.getString("screen_name"));
        model.setLocation(o.getString("location"));
        model.setGender(o.getString("gender"));
        model.setBirthday(o.getString("birthday"));
        model.setDescription(o.getString("description"));

        model.setProfileImageUrl(o.getString("profile_image_url"));
        model.setProfileImageUrlLarge(o.getString("profile_image_url_large"));
        model.setUrl(o.getString("url"));

        if (o.has("status")) {
            JSONObject so = o.getJSONObject("status");
            model.setStatus(so.getString("text"));
        }

        model.setFollowersCount(o.getInt("followers_count"));
        model.setFriendsCount(o.getInt("friends_count"));
        model.setFavouritesCount(o.getInt("favourites_count"));
        model.setStatusesCount(o.getInt("statuses_count"));

        model.setFollowing(o.getBoolean("following"));
        model.setProtect(o.getBoolean("protected"));
        model.setNotifications(o.getBoolean("notifications"));
        model.setVerified(false);
        model.setFollowMe(false);

        if (DEBUG) {
//            Log.d(TAG,
//                    " user() id=" + model.getId() + "type=" + model.getType()
//                            + " owner=" + model.getOwner() + " account="
//                            + model.getAccount()
//            );
        }
        return model;
    }

    private StatusModel status(JSONObject o, int type, String owner)
            throws JSONException {
        StatusModel model = new StatusModel();

        model.setId(o.getString("id"));
        model.setAccount(account);
        model.setOwner(owner);
        // model.setNote();

        model.setType(type);
        model.setFlag(0);

        model.setRawid(o.getLong("rawid"));
        model.setTime(FanFouParser.date(o.getString("created_at")).getTime());

        model.setText(o.getString("text"));
        model.setSimpleText(Html.fromHtml(model.getText()).toString());
        model.setSource(parseSource(o.getString("source")));
        model.setGeo(o.getString("location"));

        if (o.has("user")) {
            JSONObject uo = o.getJSONObject("user");
            UserModel user = user(uo, BaseModel.TYPE_NONE, owner);
            model.setUser(user);
        }

        if (o.has("in_reply_to_status_id")) {
            String replyId = o.getString("in_reply_to_status_id");
            if (!TextUtils.isEmpty(replyId)) {
                model.setInReplyToStatusId(o.getString("in_reply_to_status_id"));
                model.setInReplyToUserId(o.getString("in_reply_to_user_id"));
                model.setInReplyToScreenName(o
                        .getString("in_reply_to_screen_name"));
                model.setThread(true);
            }
        }

        if (o.has("repost_status_id")) {
            model.setRtStatusId(o.getString("repost_status_id"));
            model.setRtUserId(o.getString("repost_user_id"));
            model.setRtScreenName(o.getString("repost_screen_name"));
        }

        if (o.has("photo")) {
            JSONObject po = o.getJSONObject("photo");
            Photo photo = photo(po);
            model.setPhoto(true);
            model.setMedia(photo.url);
            model.setPhotoImageUrl(photo.imageUrl);
            model.setPhotoLargeUrl(photo.largeUrl);
            model.setPhotoThumbUrl(photo.thumbUrl);
        }

        model.setFavorited(o.getBoolean("favorited"));
        model.setTruncated(o.getBoolean("truncated"));
        model.setSelf(o.getBoolean("is_self"));

        if (o.has("repost_status")) {
            model.setRetweeted(true);
        }

        model.setRead(false);

        if (DEBUG) {
//            Log.d(TAG,
//                    " status() id=" + model.getId() + " userId="
//                            + model.getUserId() + " type=" + model.getType()
//                            + " owner=" + model.getOwner() + " account="
//                            + model.getAccount()
//            );
        }

        return model;
    }

    private DirectMessageModel directMessage(JSONObject o, int type)
            throws JSONException {

        DirectMessageModel model = new DirectMessageModel();

        model.setId(o.getString("id"));
        model.setAccount(account);
        model.setOwner(account);
        // model.setNote();

        model.setType(type);
        model.setFlag(0);

        model.setRawid(stringToLong(model.getId()));
        model.setTime(FanFouParser.date(o.getString("created_at")).getTime());

        model.setText(o.getString("text"));
        model.setSenderId(o.getString("sender_id"));
        model.setSenderScreenName(o.getString("sender_screen_name"));

        if (o.has("sender")) {
            JSONObject ro = o.getJSONObject("sender");
            UserModel sender = user(ro, BaseModel.TYPE_NONE, account);
            model.setSender(sender);
        }

        model.setRecipientId(o.getString("recipient_id"));
        model.setRecipientScreenName(o.getString("recipient_screen_name"));

        if (o.has("recipient")) {
            JSONObject ro = o.getJSONObject("recipient");
            UserModel recipient = user(ro, BaseModel.TYPE_NONE, account);
            model.setRecipient(recipient);
        }

        // if (json.has("otherid")) {
        // model.setConversationId(o.getString("otherid"));
        // }

        model.setRead(false);

        return model;
    }

    private Search trend(JSONObject o, Date date) throws JSONException {
        Search sh = new Search();
        sh.url = o.getString("url");
        sh.query = o.getString("query");
        sh.name = o.getString("name");
        sh.createdAt = date;
        return sh;
    }

    private Search savedSearch(JSONObject o) throws JSONException {
        Search sh = new Search();
        sh.id = o.getString("id");
        sh.query = o.getString("query");
        sh.name = o.getString("name");
        sh.createdAt = date(o.getString("created_at"));
        return sh;
    }

}
