package com.mcxiaoke.minicat.dao.model;

import android.content.ContentValues;
import android.database.Cursor;
import android.net.Uri;
import android.os.Parcel;
import android.os.Parcelable;
import com.mcxiaoke.minicat.controller.DataController;

import java.util.List;


/**
 * @author mcxiaoke
 * @version 1.5 2012.03.13
 */
public class StatusModel extends BaseModel {
    public static final int TYPE_HOME = 101;
    public static final int TYPE_MENTIONS = 102;
    public static final int TYPE_PUBLIC = 103;
    public static final int TYPE_USER = 104;
    public static final int TYPE_SEARCH = 105;
    public static final int TYPE_CONTEXT = 106;
    public static final int TYPE_FAVORITES = 107;
    public static final int TYPE_RETWEET = 108;
    public static final int TYPE_PHOTO = 109;

    public static final String TAG = StatusModel.class.getSimpleName();
    public static final Parcelable.Creator<StatusModel> CREATOR = new Parcelable.Creator<StatusModel>() {

        @Override
        public StatusModel createFromParcel(Parcel source) {
            return new StatusModel(source);
        }

        @Override
        public StatusModel[] newArray(int size) {
            return new StatusModel[size];
        }
    };
    private String text; // html format text
    private String simpleText; // plain text
    private String source; // source
    private String geo; // geo location info
    private String media;// photo url or video url
    private long userRawid; // user id
    private String userId; //
    private String userScreenName;
    private String userProfileImageUrl;
    private String inReplyToStatusId;
    private String inReplyToUserId;
    private String inReplyToScreenName;
    private String rtStatusId;
    private String rtUserId;
    private String rtScreenName;
    private String photoImageUrl;
    private String photoThumbUrl;
    private String photoLargeUrl;
    private boolean truncated;
    private boolean favorited;
    private boolean retweeted;
    private boolean self;
    private boolean read;
    private boolean thread;
    private boolean photo;
    private boolean special;
    private List<String> urls;
    private List<String> hashtags;
    private List<String> mentions;
    private UserModel user;

    public StatusModel() {
    }

    public StatusModel(Parcel in) {
        readBase(in);

        text = in.readString();
        simpleText = in.readString();
        source = in.readString();
        geo = in.readString();
        media = in.readString();

        userRawid = in.readLong();
        userId = in.readString();
        userScreenName = in.readString();
        userProfileImageUrl = in.readString();

        inReplyToStatusId = in.readString();
        inReplyToUserId = in.readString();
        inReplyToScreenName = in.readString();

        rtStatusId = in.readString();
        rtUserId = in.readString();
        rtScreenName = in.readString();

        photoImageUrl = in.readString();
        photoLargeUrl = in.readString();
        photoThumbUrl = in.readString();

        truncated = in.readInt() == 0 ? false : true;
        favorited = in.readInt() == 0 ? false : true;
        retweeted = in.readInt() == 0 ? false : true;
        self = in.readInt() == 0 ? false : true;

        read = in.readInt() == 0 ? false : true;
        thread = in.readInt() == 0 ? false : true;
        photo = in.readInt() == 0 ? false : true;
        special = in.readInt() == 0 ? false : true;

    }

    public static StatusModel from(Cursor cursor) {
        if (cursor == null) {
            return null;
        }
        StatusModel st = new StatusModel();
        st.id = DataController.parseString(cursor, IBaseColumns.ID);
        st.account = DataController.parseString(cursor, IBaseColumns.ACCOUNT);
        st.owner = DataController.parseString(cursor, IBaseColumns.OWNER);
        st.note = DataController.parseString(cursor, IBaseColumns.NOTE);

        st.type = DataController.parseInt(cursor, IBaseColumns.TYPE);
        st.flag = DataController.parseInt(cursor, IBaseColumns.FLAG);

        st.rawid = DataController.parseLong(cursor, IBaseColumns.RAWID);
        st.time = DataController.parseLong(cursor, IBaseColumns.TIME);

        st.text = DataController.parseString(cursor, StatusColumns.TEXT);
        st.simpleText = DataController.parseString(cursor, StatusColumns.SIMPLE_TEXT);
        st.source = DataController.parseString(cursor, StatusColumns.SOURCE);
        st.geo = DataController.parseString(cursor, StatusColumns.GEO);
        st.media = DataController.parseString(cursor, StatusColumns.MEDIA);

        st.userRawid = DataController.parseLong(cursor, StatusColumns.USER_RAWID);
        st.userId = DataController.parseString(cursor, StatusColumns.USER_ID);
        st.userScreenName = DataController.parseString(cursor, StatusColumns.USER_SCREEN_NAME);
        st.userProfileImageUrl = DataController.parseString(cursor, StatusColumns.USER_PROFILE_IMAGE_URL);

        st.inReplyToStatusId = DataController.parseString(cursor, StatusColumns.IN_REPLY_TO_STATUS_ID);
        st.inReplyToUserId = DataController.parseString(cursor, StatusColumns.IN_REPLY_TO_USER_ID);
        st.inReplyToScreenName = DataController.parseString(cursor, StatusColumns.IN_REPLY_TO_SCREEN_NAME);

        st.rtStatusId = DataController.parseString(cursor, StatusColumns.RT_STATUS_ID);
        st.rtUserId = DataController.parseString(cursor, StatusColumns.RT_USER_ID);
        st.rtScreenName = DataController.parseString(cursor, StatusColumns.RT_USER_SCREEN_NAME);

        st.photoImageUrl = DataController.parseString(cursor, StatusColumns.PHOTO_IMAGE_URL);
        st.photoLargeUrl = DataController.parseString(cursor, StatusColumns.PHOTO_LARGE_URL);
        st.photoThumbUrl = DataController.parseString(cursor, StatusColumns.PHOTO_THUMB_URL);

        st.truncated = DataController.parseBoolean(cursor, StatusColumns.TRUNCATED);
        st.favorited = DataController.parseBoolean(cursor, StatusColumns.FAVORITED);
        st.retweeted = DataController.parseBoolean(cursor, StatusColumns.RETWEETED);
        st.self = DataController.parseBoolean(cursor, StatusColumns.SELF);

        st.read = DataController.parseBoolean(cursor, StatusColumns.READ);
        st.thread = DataController.parseBoolean(cursor, StatusColumns.THREAD);
        st.photo = DataController.parseBoolean(cursor, StatusColumns.PHOTO);
        st.special = DataController.parseBoolean(cursor, StatusColumns.SPECIAL);

        return st;
    }

    @Override
    public ContentValues values() {
        ContentValues cv = convert();

        cv.put(StatusColumns.TEXT, this.text);
        cv.put(StatusColumns.SIMPLE_TEXT, this.simpleText);
        cv.put(StatusColumns.SOURCE, this.source);
        cv.put(StatusColumns.GEO, this.geo);
        cv.put(StatusColumns.MEDIA, this.media);

        cv.put(StatusColumns.USER_RAWID, this.userRawid);
        cv.put(StatusColumns.USER_ID, this.userId);
        cv.put(StatusColumns.USER_SCREEN_NAME, this.userScreenName);
        cv.put(StatusColumns.USER_PROFILE_IMAGE_URL, this.userProfileImageUrl);

        cv.put(StatusColumns.IN_REPLY_TO_STATUS_ID, this.inReplyToStatusId);
        cv.put(StatusColumns.IN_REPLY_TO_USER_ID, this.inReplyToUserId);
        cv.put(StatusColumns.IN_REPLY_TO_SCREEN_NAME, this.inReplyToScreenName);

        cv.put(StatusColumns.RT_STATUS_ID, this.rtStatusId);
        cv.put(StatusColumns.RT_USER_ID, this.rtUserId);
        cv.put(StatusColumns.RT_USER_SCREEN_NAME, this.rtScreenName);

        cv.put(StatusColumns.PHOTO_IMAGE_URL, this.photoImageUrl);
        cv.put(StatusColumns.PHOTO_THUMB_URL, this.photoThumbUrl);
        cv.put(StatusColumns.PHOTO_LARGE_URL, this.photoLargeUrl);

        cv.put(StatusColumns.TRUNCATED, this.truncated);
        cv.put(StatusColumns.FAVORITED, this.favorited);
        cv.put(StatusColumns.RETWEETED, this.retweeted);
        cv.put(StatusColumns.SELF, this.self);

        cv.put(StatusColumns.READ, this.read);
        cv.put(StatusColumns.THREAD, this.thread);
        cv.put(StatusColumns.PHOTO, this.photo);
        cv.put(StatusColumns.SPECIAL, this.special);

        return cv;
    }

    @Override
    public Uri getContentUri() {
        // TODO Auto-generated method stub
        return StatusColumns.CONTENT_URI;
    }

    @Override
    public String getTable() {
        return StatusColumns.TABLE_NAME;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        writeBase(dest, flags);

        dest.writeString(text);
        dest.writeString(simpleText);
        dest.writeString(source);
        dest.writeString(geo);
        dest.writeString(media);

        dest.writeLong(userRawid);
        dest.writeString(userId);
        dest.writeString(userScreenName);
        dest.writeString(userProfileImageUrl);

        dest.writeString(inReplyToStatusId);
        dest.writeString(inReplyToUserId);
        dest.writeString(inReplyToScreenName);

        dest.writeString(rtStatusId);
        dest.writeString(rtUserId);
        dest.writeString(rtScreenName);

        dest.writeString(photoImageUrl);
        dest.writeString(photoLargeUrl);
        dest.writeString(photoThumbUrl);

        dest.writeInt(truncated ? 1 : 0);
        dest.writeInt(favorited ? 1 : 0);
        dest.writeInt(retweeted ? 1 : 0);
        dest.writeInt(self ? 1 : 0);

        dest.writeInt(read ? 1 : 0);
        dest.writeInt(thread ? 1 : 0);
        dest.writeInt(photo ? 1 : 0);
        dest.writeInt(special ? 1 : 0);

    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public String getSimpleText() {
        return simpleText;
    }

    public void setSimpleText(String simpleText) {
        this.simpleText = simpleText;
    }

    public String getSource() {
        return source;
    }

    public void setSource(String source) {
        this.source = source;
    }

    public String getGeo() {
        return geo;
    }

    public void setGeo(String geo) {
        this.geo = geo;
    }

    public String getMedia() {
        return media;
    }

    public void setMedia(String media) {
        this.media = media;
    }

    public long getUserRawid() {
        return userRawid;
    }

    public void setUserRawid(long userRawid) {
        this.userRawid = userRawid;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getUserScreenName() {
        return userScreenName;
    }

    public void setUserScreenName(String userScreenName) {
        this.userScreenName = userScreenName;
    }

    public String getInReplyToStatusId() {
        return inReplyToStatusId;
    }

    public void setInReplyToStatusId(String inReplyToStatusId) {
        this.inReplyToStatusId = inReplyToStatusId;
    }

    public String getInReplyToUserId() {
        return inReplyToUserId;
    }

    public void setInReplyToUserId(String inReplyToUserId) {
        this.inReplyToUserId = inReplyToUserId;
    }

    public String getInReplyToScreenName() {
        return inReplyToScreenName;
    }

    public void setInReplyToScreenName(String inReplyToScreenName) {
        this.inReplyToScreenName = inReplyToScreenName;
    }

    public String getRtStatusId() {
        return rtStatusId;
    }

    public void setRtStatusId(String rtStatusId) {
        this.rtStatusId = rtStatusId;
    }

    public String getRtUserId() {
        return rtUserId;
    }

    public void setRtUserId(String rtUserId) {
        this.rtUserId = rtUserId;
    }

    public String getRtScreenName() {
        return rtScreenName;
    }

    public void setRtScreenName(String rtScreenName) {
        this.rtScreenName = rtScreenName;
    }

    public String getUserProfileImageUrl() {
        return userProfileImageUrl;
    }

    public void setUserProfileImageUrl(String userProfileImageUrl) {
        this.userProfileImageUrl = userProfileImageUrl;
    }

    public String getPhotoImageUrl() {
        return photoImageUrl;
    }

    public void setPhotoImageUrl(String photoImageUrl) {
        this.photoImageUrl = photoImageUrl;
    }

    public String getPhotoThumbUrl() {
        return photoThumbUrl;
    }

    public void setPhotoThumbUrl(String photoThumbUrl) {
        this.photoThumbUrl = photoThumbUrl;
    }

    public String getPhotoLargeUrl() {
        return photoLargeUrl;
    }

    public void setPhotoLargeUrl(String photoLargeUrl) {
        this.photoLargeUrl = photoLargeUrl;
    }

    public boolean isTruncated() {
        return truncated;
    }

    public void setTruncated(boolean truncated) {
        this.truncated = truncated;
    }

    public boolean isFavorited() {
        return favorited;
    }

    public void setFavorited(boolean favorited) {
        this.favorited = favorited;
    }

    public boolean isRetweeted() {
        return retweeted;
    }

    public void setRetweeted(boolean retweeted) {
        this.retweeted = retweeted;
    }

    public boolean isSelf() {
        return self;
    }

    public void setSelf(boolean self) {
        this.self = self;
    }

    public boolean isRead() {
        return read;
    }

    public void setRead(boolean read) {
        this.read = read;
    }

    public boolean isThread() {
        return thread;
    }

    public void setThread(boolean thread) {
        this.thread = thread;
    }

    public boolean isPhoto() {
        return photo;
    }

    public void setPhoto(boolean photo) {
        this.photo = photo;
    }

    public boolean isSpecial() {
        return special;
    }

    public void setSpecial(boolean special) {
        this.special = special;
    }

    public List<String> getUrls() {
        return urls;
    }

    public void setUrls(List<String> urls) {
        this.urls = urls;
    }

    public List<String> getHashtags() {
        return hashtags;
    }

    public void setHashtags(List<String> hashtags) {
        this.hashtags = hashtags;
    }

    public List<String> getMentions() {
        return mentions;
    }

    public void setMentions(List<String> mentions) {
        this.mentions = mentions;
    }

    public UserModel getUser() {
        return user;
    }

    public void setUser(UserModel user) {
        this.user = user;
        if (user != null) {
            this.userId = user.getId();
            this.userScreenName = user.getScreenName();
            this.userRawid = user.getRawid();
            this.userProfileImageUrl = user.getProfileImageUrlLarge();
        }
    }

    @Override
    public String toString() {
        return "StatusModel{" +
                "text='" + text + '\'' +
                ", simpleText='" + simpleText + '\'' +
                ", source='" + source + '\'' +
                ", geo='" + geo + '\'' +
                ", media='" + media + '\'' +
                ", userRawid=" + userRawid +
                ", userId='" + userId + '\'' +
                ", userScreenName='" + userScreenName + '\'' +
                ", userProfileImageUrl='" + userProfileImageUrl + '\'' +
                ", inReplyToStatusId='" + inReplyToStatusId + '\'' +
                ", inReplyToUserId='" + inReplyToUserId + '\'' +
                ", inReplyToScreenName='" + inReplyToScreenName + '\'' +
                ", rtStatusId='" + rtStatusId + '\'' +
                ", rtUserId='" + rtUserId + '\'' +
                ", rtScreenName='" + rtScreenName + '\'' +
                ", photoImageUrl='" + photoImageUrl + '\'' +
                ", photoThumbUrl='" + photoThumbUrl + '\'' +
                ", photoLargeUrl='" + photoLargeUrl + '\'' +
                ", truncated=" + truncated +
                ", favorited=" + favorited +
                ", retweeted=" + retweeted +
                ", self=" + self +
                ", read=" + read +
                ", thread=" + thread +
                ", photo=" + photo +
                ", special=" + special +
                ", urls=" + urls +
                ", hashtags=" + hashtags +
                ", mentions=" + mentions +
                ", user=" + user +
                '}';
    }

}
