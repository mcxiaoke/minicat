package com.fanfou.app.dao.model;

import java.util.Date;

import android.content.ContentValues;
import android.os.Parcel;
import android.os.Parcelable;
import com.fanfou.app.db.Contents.BasicColumns;
import com.fanfou.app.db.Contents.UserInfo;

/**
 * @author mcxiaoke
 * @version 1.0 2011.12.21
 * 
 */
public class UserModel extends AbstractModel<UserModel> {

	public static final String TAG = UserModel.class.getSimpleName();

	private String id;
	private String account;
	private String owner;
	private int type;
	private Date time;

	private String screenName;
	private String location;
	private String gender;
	private String birthday;

	private String description;
	private String profileImageUrl;
	private String profileImageLargeUrl;
	private String url;

	private int followersCount;
	private int friendsCount;
	private int favouritesCount;
	private int statusesCount;

	private boolean following;
	private boolean protect;


	public UserModel() {
	}

	public UserModel(Parcel in) {
		id = in.readString();
		account = in.readString();
		time = new Date(in.readLong());
		type = in.readInt();

		screenName = in.readString();
		location = in.readString();
		gender = in.readString();
		birthday = in.readString();

		description = in.readString();
		profileImageUrl = in.readString();
		profileImageLargeUrl=in.readString();
		url = in.readString();
		protect = in.readInt() == 0 ? false : true;

		followersCount = in.readInt();
		friendsCount = in.readInt();
		favouritesCount = in.readInt();
		statusesCount = in.readInt();

		following = in.readInt() == 0 ? false : true;
	}

	@Override
	public ContentValues values() {
		UserModel u = this;
		ContentValues cv = new ContentValues();

		cv.put(BasicColumns.ID, u.id);
		cv.put(BasicColumns.OWNER_ID, u.account);

		cv.put(UserInfo.SCREEN_NAME, u.screenName);
		cv.put(UserInfo.LOCATION, u.location);
		cv.put(UserInfo.GENDER, u.gender);
		cv.put(UserInfo.BIRTHDAY, u.birthday);

		cv.put(UserInfo.DESCRIPTION, u.description);
		cv.put(UserInfo.PROFILE_IMAGE_URL, u.profileImageUrl);
		cv.put(UserInfo.URL, u.url);
		cv.put(UserInfo.PROTECTED, u.protect);

		cv.put(UserInfo.FOLLOWERS_COUNT, u.followersCount);
		cv.put(UserInfo.FRIENDS_COUNT, u.friendsCount);
		cv.put(UserInfo.FAVORITES_COUNT, u.favouritesCount);
		cv.put(UserInfo.STATUSES_COUNT, u.statusesCount);

		cv.put(UserInfo.FOLLOWING, u.following);
		cv.put(BasicColumns.CREATED_AT, u.time.getTime());

		cv.put(BasicColumns.TYPE, u.type);

		return cv;
	}

	@Override
	public void put() {
	}

	@Override
	public UserModel get(String key) {
		return null;
	}

	@Override
	public String toString() {
		return "[User] " + BasicColumns.ID + "=" + id + " "
				+ UserInfo.SCREEN_NAME + "=" + screenName + " "
				+ UserInfo.LOCATION + "=" + location + " " + UserInfo.GENDER
				+ "=" + gender + " " + UserInfo.BIRTHDAY + "=" + birthday + " "
				+ UserInfo.DESCRIPTION + "=" + description + " "
				+ UserInfo.PROFILE_IMAGE_URL + "=" + profileImageUrl + " "
				+ UserInfo.URL + "=" + url + " " + UserInfo.PROTECTED + "="
				+ protect + " " + UserInfo.FOLLOWERS_COUNT + "="
				+ followersCount + " " + UserInfo.FRIENDS_COUNT + "="
				+ friendsCount + " " + UserInfo.FAVORITES_COUNT + "="
				+ favouritesCount + " " + UserInfo.STATUSES_COUNT + "="
				+ statusesCount + " " + UserInfo.FOLLOWING + "=" + following
				+ " " + BasicColumns.CREATED_AT + "=" + time + " "
				+ BasicColumns.TYPE + "=" + type + " ";
	}

	@Override
	public int describeContents() {
		return 0;
	}

	@Override
	public void writeToParcel(Parcel dest, int flags) {
		dest.writeString(id);
		dest.writeString(account);
		dest.writeLong(time.getTime());
		dest.writeInt(type);

		dest.writeString(screenName);
		dest.writeString(location);
		dest.writeString(gender);
		dest.writeString(birthday);

		dest.writeString(description);
		dest.writeString(profileImageUrl);
		dest.writeString(profileImageLargeUrl);
		dest.writeString(url);
		dest.writeInt(protect ? 1 : 0);

		dest.writeInt(followersCount);
		dest.writeInt(friendsCount);
		dest.writeInt(favouritesCount);
		dest.writeInt(statusesCount);

		dest.writeInt(following ? 1 : 0);

	}

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


	/**
	 * @return the id
	 */
	public final String getId() {
		return id;
	}

	/**
	 * @param id the id to set
	 */
	public final void setId(String id) {
		this.id = id;
	}

	/**
	 * @return the owner
	 */
	public final String getAccount() {
		return account;
	}

	/**
	 * @param owner the owner to set
	 */
	public final void setAccount(String account) {
		this.account = account;
	}

	/**
	 * @return the owner
	 */
	public final String getOwner() {
		return owner;
	}

	/**
	 * @param owner the owner to set
	 */
	public final void setOwner(String owner) {
		this.owner = owner;
	}

	/**
	 * @return the type
	 */
	public final int getType() {
		return type;
	}

	/**
	 * @param type the type to set
	 */
	public final void setType(int type) {
		this.type = type;
	}

	/**
	 * @return the time
	 */
	public final Date getTime() {
		return time;
	}

	/**
	 * @param time the time to set
	 */
	public final void setTime(Date time) {
		this.time = time;
	}

	/**
	 * @return the screenName
	 */
	public final String getScreenName() {
		return screenName;
	}

	/**
	 * @param screenName the screenName to set
	 */
	public final void setScreenName(String screenName) {
		this.screenName = screenName;
	}

	/**
	 * @return the location
	 */
	public final String getLocation() {
		return location;
	}

	/**
	 * @param location the location to set
	 */
	public final void setLocation(String location) {
		this.location = location;
	}

	/**
	 * @return the gender
	 */
	public final String getGender() {
		return gender;
	}

	/**
	 * @param gender the gender to set
	 */
	public final void setGender(String gender) {
		this.gender = gender;
	}

	/**
	 * @return the birthday
	 */
	public final String getBirthday() {
		return birthday;
	}

	/**
	 * @param birthday the birthday to set
	 */
	public final void setBirthday(String birthday) {
		this.birthday = birthday;
	}

	/**
	 * @return the description
	 */
	public final String getDescription() {
		return description;
	}

	/**
	 * @param description the description to set
	 */
	public final void setDescription(String description) {
		this.description = description;
	}

	/**
	 * @return the profileImageUrl
	 */
	public final String getProfileImageUrl() {
		return profileImageUrl;
	}

	/**
	 * @param profileImageUrl the profileImageUrl to set
	 */
	public final void setProfileImageUrl(String profileImageUrl) {
		this.profileImageUrl = profileImageUrl;
	}

	/**
	 * @return the profileImageLargeUrl
	 */
	public final String getProfileImageLargeUrl() {
		return profileImageLargeUrl;
	}

	/**
	 * @param profileImageLargeUrl the profileImageLargeUrl to set
	 */
	public final void setProfileImageLargeUrl(String profileImageLargeUrl) {
		this.profileImageLargeUrl = profileImageLargeUrl;
	}

	/**
	 * @return the url
	 */
	public final String getUrl() {
		return url;
	}

	/**
	 * @param url the url to set
	 */
	public final void setUrl(String url) {
		this.url = url;
	}

	/**
	 * @return the followersCount
	 */
	public final int getFollowersCount() {
		return followersCount;
	}

	/**
	 * @param followersCount the followersCount to set
	 */
	public final void setFollowersCount(int followersCount) {
		this.followersCount = followersCount;
	}

	/**
	 * @return the friendsCount
	 */
	public final int getFriendsCount() {
		return friendsCount;
	}

	/**
	 * @param friendsCount the friendsCount to set
	 */
	public final void setFriendsCount(int friendsCount) {
		this.friendsCount = friendsCount;
	}

	/**
	 * @return the favouritesCount
	 */
	public final int getFavouritesCount() {
		return favouritesCount;
	}

	/**
	 * @param favouritesCount the favouritesCount to set
	 */
	public final void setFavouritesCount(int favouritesCount) {
		this.favouritesCount = favouritesCount;
	}

	/**
	 * @return the statusesCount
	 */
	public final int getStatusesCount() {
		return statusesCount;
	}

	/**
	 * @param statusesCount the statusesCount to set
	 */
	public final void setStatusesCount(int statusesCount) {
		this.statusesCount = statusesCount;
	}

	/**
	 * @return the following
	 */
	public final boolean isFollowing() {
		return following;
	}

	/**
	 * @param following the following to set
	 */
	public final void setFollowing(boolean following) {
		this.following = following;
	}

	/**
	 * @return the protect
	 */
	public final boolean isProtect() {
		return protect;
	}

	/**
	 * @param protect the protect to set
	 */
	public final void setProtect(boolean protect) {
		this.protect = protect;
	}

}
