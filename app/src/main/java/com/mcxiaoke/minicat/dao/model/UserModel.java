package com.mcxiaoke.minicat.dao.model;

import android.content.ContentValues;
import android.database.Cursor;
import android.net.Uri;
import android.os.Parcel;
import android.os.Parcelable;
import com.mcxiaoke.minicat.controller.DataController;


/**
 * @author mcxiaoke
 * @version 1.2 2012.02.20
 */
public class UserModel extends BaseModel {
    public static final int TYPE_FRIENDS = 201;
    public static final int TYPE_FOLLOWERS = 202;
    public static final int TYPE_SEARCH = 203;
    public static final int TYPE_BLOCK = 204;
    public static final int TYPE_SPECIAL = 205;

    public static final String TAG = UserModel.class.getSimpleName();
    public static final Parcelable.Creator<UserModel> CREATOR = new Parcelable.Creator<UserModel>() {

        @Override
        public UserModel createFromParcel(Parcel source) {
            return new UserModel(source);
        }

        @Override
        public UserModel[] newArray(int size) {
            return new UserModel[size];
        }
    };
    private String name;
    private String screenName;
    private String location;
    private String gender;
    private String birthday;
    private String description;
    private String profileImageUrl;
    private String profileImageUrlLarge;
    private String url;
    private String status;
    private int followersCount;
    private int friendsCount;
    private int favouritesCount;
    private int statusesCount;
    private boolean following;
    private boolean protect;
    private boolean notifications;
    private boolean verified;
    private boolean followMe;

    public UserModel() {
    }

    public UserModel(Parcel in) {
        readBase(in);
        name = in.readString();
        screenName = in.readString();
        location = in.readString();
        gender = in.readString();
        birthday = in.readString();
        description = in.readString();

        profileImageUrl = in.readString();
        profileImageUrlLarge = in.readString();
        url = in.readString();
        status = in.readString();

        followersCount = in.readInt();
        friendsCount = in.readInt();
        favouritesCount = in.readInt();
        statusesCount = in.readInt();

        following = in.readInt() != 0;
        protect = in.readInt() != 0;
        notifications = in.readInt() != 0;
        verified = in.readInt() != 0;
        followMe = in.readInt() != 0;

    }

    public static UserModel from(Cursor cursor) {
        if (cursor == null) {
            return null;
        }
        UserModel user = new UserModel();
        user.id = DataController.parseString(cursor, IBaseColumns.ID);
        user.account = DataController.parseString(cursor, IBaseColumns.ACCOUNT);
        user.owner = DataController.parseString(cursor, IBaseColumns.OWNER);
        user.note = DataController.parseString(cursor, IBaseColumns.NOTE);

        user.type = DataController.parseInt(cursor, IBaseColumns.TYPE);
        user.flag = DataController.parseInt(cursor, IBaseColumns.FLAG);

        user.rawid = DataController.parseLong(cursor, IBaseColumns.RAWID);
        user.time = DataController.parseLong(cursor, IBaseColumns.TIME);

        user.name = DataController.parseString(cursor, UserColumns.NAME);
        user.screenName = DataController.parseString(cursor, UserColumns.SCREEN_NAME);
        user.location = DataController.parseString(cursor, UserColumns.LOCATION);
        user.gender = DataController.parseString(cursor, UserColumns.GENDER);
        user.birthday = DataController.parseString(cursor, UserColumns.BIRTHDAY);
        user.description = DataController.parseString(cursor, UserColumns.DESCRIPTION);

        user.profileImageUrl = DataController.parseString(cursor, UserColumns.PROFILE_IMAGE_URL);
        user.profileImageUrlLarge = DataController.parseString(cursor, UserColumns.PROFILE_IMAGE_URL_LARGE);
        user.url = DataController.parseString(cursor, UserColumns.URL);
        user.status = DataController.parseString(cursor, UserColumns.STATUS);

        user.followersCount = DataController.parseInt(cursor, UserColumns.FOLLOWERS_COUNT);
        user.friendsCount = DataController.parseInt(cursor, UserColumns.FRIENDS_COUNT);
        user.favouritesCount = DataController.parseInt(cursor, UserColumns.FAVORITES_COUNT);
        user.statusesCount = DataController.parseInt(cursor, UserColumns.STATUSES_COUNT);

        user.following = DataController.parseBoolean(cursor, UserColumns.FOLLOWING);
        user.protect = DataController.parseBoolean(cursor, UserColumns.PROTECTED);
        user.notifications = DataController.parseBoolean(cursor, UserColumns.NOTIFICATIONS);
        user.verified = DataController.parseBoolean(cursor, UserColumns.VERIFIED);
        user.followMe = DataController.parseBoolean(cursor, UserColumns.FOLLOW_ME);

        return user;
    }

    @Override
    public ContentValues values() {
        ContentValues cv = convert();

        cv.put(UserColumns.NAME, name);
        cv.put(UserColumns.SCREEN_NAME, screenName);
        cv.put(UserColumns.LOCATION, location);
        cv.put(UserColumns.GENDER, gender);
        cv.put(UserColumns.BIRTHDAY, birthday);
        cv.put(UserColumns.DESCRIPTION, description);

        cv.put(UserColumns.PROFILE_IMAGE_URL, profileImageUrl);
        cv.put(UserColumns.PROFILE_IMAGE_URL_LARGE, profileImageUrlLarge);
        cv.put(UserColumns.URL, url);
        cv.put(UserColumns.STATUS, status);

        cv.put(UserColumns.FOLLOWERS_COUNT, followersCount);
        cv.put(UserColumns.FRIENDS_COUNT, friendsCount);
        cv.put(UserColumns.FAVORITES_COUNT, favouritesCount);
        cv.put(UserColumns.STATUSES_COUNT, statusesCount);

        cv.put(UserColumns.FOLLOWING, following);
        cv.put(UserColumns.PROTECTED, protect);
        cv.put(UserColumns.NOTIFICATIONS, notifications);
        cv.put(UserColumns.VERIFIED, verified);
        cv.put(UserColumns.FOLLOW_ME, followMe);

        return cv;
    }

    @Override
    public Uri getContentUri() {
        return UserColumns.CONTENT_URI;
    }

    @Override
    public String getTable() {
        return UserColumns.TABLE_NAME;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        writeBase(dest, flags);
        dest.writeString(name);
        dest.writeString(screenName);
        dest.writeString(location);
        dest.writeString(gender);
        dest.writeString(birthday);
        dest.writeString(description);

        dest.writeString(profileImageUrl);
        dest.writeString(profileImageUrlLarge);
        dest.writeString(url);
        dest.writeString(status);

        dest.writeInt(followersCount);
        dest.writeInt(friendsCount);
        dest.writeInt(favouritesCount);
        dest.writeInt(statusesCount);

        dest.writeInt(following ? 1 : 0);
        dest.writeInt(protect ? 1 : 0);
        dest.writeInt(notifications ? 1 : 0);
        dest.writeInt(verified ? 1 : 0);
        dest.writeInt(followMe ? 1 : 0);

    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getScreenName() {
        return screenName;
    }

    public void setScreenName(String screenName) {
        this.screenName = screenName;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public String getBirthday() {
        return birthday;
    }

    public void setBirthday(String birthday) {
        this.birthday = birthday;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getProfileImageUrl() {
        return profileImageUrl;
    }

    public void setProfileImageUrl(String profileImageUrl) {
        this.profileImageUrl = profileImageUrl;
    }

    public String getProfileImageUrlLarge() {
        return profileImageUrlLarge;
    }

    public void setProfileImageUrlLarge(String profileImageUrlLarge) {
        this.profileImageUrlLarge = profileImageUrlLarge;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public int getFollowersCount() {
        return followersCount;
    }

    public void setFollowersCount(int followersCount) {
        this.followersCount = followersCount;
    }

    public int getFriendsCount() {
        return friendsCount;
    }

    public void setFriendsCount(int friendsCount) {
        this.friendsCount = friendsCount;
    }

    public int getFavouritesCount() {
        return favouritesCount;
    }

    public void setFavouritesCount(int favouritesCount) {
        this.favouritesCount = favouritesCount;
    }

    public int getStatusesCount() {
        return statusesCount;
    }

    public void setStatusesCount(int statusesCount) {
        this.statusesCount = statusesCount;
    }

    public boolean isFollowing() {
        return following;
    }

    public void setFollowing(boolean following) {
        this.following = following;
    }

    public boolean isProtect() {
        return protect;
    }

    public void setProtect(boolean protect) {
        this.protect = protect;
    }

    public boolean isNotifications() {
        return notifications;
    }

    public void setNotifications(boolean notifications) {
        this.notifications = notifications;
    }

    public boolean isVerified() {
        return verified;
    }

    public void setVerified(boolean verified) {
        this.verified = verified;
    }

    public boolean isFollowMe() {
        return followMe;
    }

    public void setFollowMe(boolean followMe) {
        this.followMe = followMe;
    }

    @Override
    public String toString() {
        return "UserModel [name=" + name + ", screenName=" + screenName
                + ", location=" + location + ", gender=" + gender
                + ", birthday=" + birthday + ", description=" + description
                + ", profileImageUrl=" + profileImageUrl
                + ", profileImageUrlLarge=" + profileImageUrlLarge + ", url="
                + url + ", status=" + status + ", followersCount="
                + followersCount + ", friendsCount=" + friendsCount
                + ", favouritesCount=" + favouritesCount + ", statusesCount="
                + statusesCount + ", following=" + following + ", protect="
                + protect + ", notifications=" + notifications + ", verified="
                + verified + ", followMe=" + followMe + "]";
    }

}
