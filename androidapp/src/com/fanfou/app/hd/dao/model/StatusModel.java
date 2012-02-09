package com.fanfou.app.hd.dao.model;

import java.util.Date;
import java.util.List;

import android.content.ContentValues;
import android.os.Parcel;
import android.os.Parcelable;

import com.fanfou.app.hd.db.Contents.BasicColumns;
import com.fanfou.app.hd.db.Contents.StatusInfo;

/**
 * @author mcxiaoke
 * @version 1.0 2011.12.21
 * 
 */
public class StatusModel extends AbstractModel<StatusModel> {

	public static final String TAG = StatusModel.class.getSimpleName();

	private String id;
	private String account;
	private String owner;
	private Date time;

	private String text;
	private String simpleText;
	private String source;

	private String inReplyToStatusId;
	private String inReplyToUserId;
	private String inReplyToScreenName;

	private String photoImageUrl;
	private String photoThumbUrl;
	private String photoLargeUrl;

	private String userId;
	private String userScreenName;
	private String userProfileImageUrl;
	
	private String location;

	private boolean truncated;
	private boolean favorited;
	private boolean self;

	private boolean read;
	private boolean thread;
	private boolean photo;
	private boolean special;

	private int type;
	
	private List<String> urls;
	private List<String> hashtags;
	private List<String> names;

	private UserModel user;

	public StatusModel() {
	}

	public StatusModel(Parcel in) {
		id=in.readString();
		account=in.readString();
		time=new Date(in.readLong());
		type=in.readInt();
		
		text=in.readString();
		simpleText=in.readString();
		source=in.readString();
		
		inReplyToStatusId=in.readString();
		inReplyToUserId=in.readString();
		inReplyToScreenName=in.readString();
		
		photoImageUrl=in.readString();
		photoLargeUrl=in.readString();
		photoThumbUrl=in.readString();
		
		userId=in.readString();
		userScreenName=in.readString();
		userProfileImageUrl=in.readString();
		
		truncated=in.readInt()==0?false:true;
		favorited=in.readInt()==0?false:true;
		self=in.readInt()==0?false:true;
		
		read=in.readInt()==0?false:true;
		thread=in.readInt()==0?false:true;
		photo=in.readInt()==0?false:true;
		
		special=in.readInt()==0?false:true;
	}
	

	@Override
	public void put() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public StatusModel get(String key) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public ContentValues values() {
		ContentValues cv = new ContentValues();

		cv.put(BasicColumns.ID, this.id);
		cv.put(BasicColumns.OWNER_ID, this.account);
		cv.put(BasicColumns.CREATED_AT, this.time.getTime());

		cv.put(StatusInfo.TEXT, this.text);
		cv.put(StatusInfo.SOURCE, this.source);
		cv.put(StatusInfo.SIMPLE_TEXT, this.simpleText);

		cv.put(StatusInfo.IN_REPLY_TO_STATUS_ID, this.inReplyToStatusId);
		cv.put(StatusInfo.IN_REPLY_TO_USER_ID, this.inReplyToUserId);
		cv.put(StatusInfo.IN_REPLY_TO_SCREEN_NAME, this.inReplyToScreenName);

		cv.put(StatusInfo.PHOTO_IMAGE_URL, this.photoImageUrl);
		cv.put(StatusInfo.PHOTO_THUMB_URL, this.photoThumbUrl);
		cv.put(StatusInfo.PHOTO_LARGE_URL, this.photoLargeUrl);

		cv.put(StatusInfo.USER_ID, this.userId);
		cv.put(StatusInfo.USER_SCREEN_NAME, this.userScreenName);
		cv.put(StatusInfo.USER_PROFILE_IMAGE_URL, this.userProfileImageUrl);

		cv.put(StatusInfo.TRUNCATED, this.truncated);
		cv.put(StatusInfo.FAVORITED, this.favorited);
		cv.put(StatusInfo.IS_SELF, this.self);

		cv.put(StatusInfo.IS_READ, this.read);
		cv.put(StatusInfo.IS_THREAD, this.thread);
		cv.put(StatusInfo.HAS_PHOTO, this.photo);
		cv.put(StatusInfo.SPECIAL, this.special);

		cv.put(BasicColumns.TYPE, this.type);

		return cv;
	}

	@Override
	public String toString() {
		// return toContentValues().toString();
		return "[Status] " + BasicColumns.ID + "=" + this.id + " "
				+ StatusInfo.TEXT + "=" + this.text + " "
				+ BasicColumns.CREATED_AT + "+" + this.time + " "
				// +StatusInfo.SOURCE+"="+this.source+" "
				// +StatusInfo.TRUNCATED+"="+this.truncated+" "
				// +StatusInfo.IN_REPLY_TO_STATUS_ID+"="+this.inReplyToStatusId+" "
				// +StatusInfo.IN_REPLY_TO_USER_ID+"="+this.inReplyToUserId+" "
				// +StatusInfo.FAVORITED+"="+this.favorited+" "
				// +StatusInfo.IN_REPLY_TO_SCREEN_NAME+"="+this.inReplyToScreenName+" "
				// +StatusInfo.PHOTO_IMAGE_URL+"="+this.photoImageUrl+" "
				// +StatusInfo.PHOTO_LARGE_URL+"="+this.photoLargeUrl+" "
				// +StatusInfo.PHOTO_THUMB_URL+"="+this.photoThumbUrl+" "
				+ StatusInfo.USER_ID + "=" + this.userId + " ";
		// +StatusInfo.USER_SCREEN_NAME+"="+this.userScreenName+" "
		// +StatusInfo.READ+"="+this.read+" "
		// +StatusInfo.TYPE+"="+this.type+" ";
	}

	@Override
	public int describeContents() {
		return 0;
	}
	
	public StatusModel readFromParcel(Parcel source){
		return new StatusModel(source);
	}

	@Override
	public void writeToParcel(Parcel dest, int flags) {
		dest.writeString(id);
		dest.writeString(account);
		dest.writeLong(time.getTime());
		dest.writeInt(type);		
		
		dest.writeString(text);
		dest.writeString(simpleText);
		dest.writeString(source);
		
		dest.writeString(inReplyToStatusId);
		dest.writeString(inReplyToUserId);
		dest.writeString(inReplyToScreenName);
		
		dest.writeString(photoImageUrl);
		dest.writeString(photoLargeUrl);
		dest.writeString(photoThumbUrl);
		
		dest.writeString(userId);
		dest.writeString(userScreenName);
		dest.writeString(userProfileImageUrl);
		
		dest.writeInt(truncated?1:0);
		dest.writeInt(favorited?1:0);
		dest.writeInt(self?1:0);
		
		dest.writeInt(read?1:0);
		dest.writeInt(thread?1:0);
		dest.writeInt(photo?1:0);
		
		dest.writeInt(special?1:0);
		
	}

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
	 * @return the account
	 */
	public final String getAccount() {
		return account;
	}

	/**
	 * @param account the account to set
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
	 * @return the text
	 */
	public final String getText() {
		return text;
	}

	/**
	 * @param text the text to set
	 */
	public final void setText(String text) {
		this.text = text;
	}

	/**
	 * @return the simpleText
	 */
	public final String getSimpleText() {
		return simpleText;
	}

	/**
	 * @param simpleText the simpleText to set
	 */
	public final void setSimpleText(String simpleText) {
		this.simpleText = simpleText;
	}

	/**
	 * @return the source
	 */
	public final String getSource() {
		return source;
	}

	/**
	 * @param source the source to set
	 */
	public final void setSource(String source) {
		this.source = source;
	}

	/**
	 * @return the inReplyToStatusId
	 */
	public final String getInReplyToStatusId() {
		return inReplyToStatusId;
	}

	/**
	 * @param inReplyToStatusId the inReplyToStatusId to set
	 */
	public final void setInReplyToStatusId(String inReplyToStatusId) {
		this.inReplyToStatusId = inReplyToStatusId;
	}

	/**
	 * @return the inReplyToUserId
	 */
	public final String getInReplyToUserId() {
		return inReplyToUserId;
	}

	/**
	 * @param inReplyToUserId the inReplyToUserId to set
	 */
	public final void setInReplyToUserId(String inReplyToUserId) {
		this.inReplyToUserId = inReplyToUserId;
	}

	/**
	 * @return the inReplyToScreenName
	 */
	public final String getInReplyToScreenName() {
		return inReplyToScreenName;
	}

	/**
	 * @param inReplyToScreenName the inReplyToScreenName to set
	 */
	public final void setInReplyToScreenName(String inReplyToScreenName) {
		this.inReplyToScreenName = inReplyToScreenName;
	}

	/**
	 * @return the photoImageUrl
	 */
	public final String getPhotoImageUrl() {
		return photoImageUrl;
	}

	/**
	 * @param photoImageUrl the photoImageUrl to set
	 */
	public final void setPhotoImageUrl(String photoImageUrl) {
		this.photoImageUrl = photoImageUrl;
	}

	/**
	 * @return the photoThumbUrl
	 */
	public final String getPhotoThumbUrl() {
		return photoThumbUrl;
	}

	/**
	 * @param photoThumbUrl the photoThumbUrl to set
	 */
	public final void setPhotoThumbUrl(String photoThumbUrl) {
		this.photoThumbUrl = photoThumbUrl;
	}

	/**
	 * @return the photoLargeUrl
	 */
	public final String getPhotoLargeUrl() {
		return photoLargeUrl;
	}

	/**
	 * @param photoLargeUrl the photoLargeUrl to set
	 */
	public final void setPhotoLargeUrl(String photoLargeUrl) {
		this.photoLargeUrl = photoLargeUrl;
	}

	/**
	 * @return the userId
	 */
	public final String getUserId() {
		return userId;
	}

	/**
	 * @param userId the userId to set
	 */
	public final void setUserId(String userId) {
		this.userId = userId;
	}

	/**
	 * @return the userScreenName
	 */
	public final String getUserScreenName() {
		return userScreenName;
	}

	/**
	 * @param userScreenName the userScreenName to set
	 */
	public final void setUserScreenName(String userScreenName) {
		this.userScreenName = userScreenName;
	}

	/**
	 * @return the userProfileImageUrl
	 */
	public final String getUserProfileImageUrl() {
		return userProfileImageUrl;
	}

	/**
	 * @param userProfileImageUrl the userProfileImageUrl to set
	 */
	public final void setUserProfileImageUrl(String userProfileImageUrl) {
		this.userProfileImageUrl = userProfileImageUrl;
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
	 * @return the truncated
	 */
	public final boolean isTruncated() {
		return truncated;
	}

	/**
	 * @param truncated the truncated to set
	 */
	public final void setTruncated(boolean truncated) {
		this.truncated = truncated;
	}

	/**
	 * @return the favorited
	 */
	public final boolean isFavorited() {
		return favorited;
	}

	/**
	 * @param favorited the favorited to set
	 */
	public final void setFavorited(boolean favorited) {
		this.favorited = favorited;
	}

	/**
	 * @return the self
	 */
	public final boolean isSelf() {
		return self;
	}

	/**
	 * @param self the self to set
	 */
	public final void setSelf(boolean self) {
		this.self = self;
	}

	/**
	 * @return the read
	 */
	public final boolean isRead() {
		return read;
	}

	/**
	 * @param read the read to set
	 */
	public final void setRead(boolean read) {
		this.read = read;
	}

	/**
	 * @return the thread
	 */
	public final boolean isThread() {
		return thread;
	}

	/**
	 * @param thread the thread to set
	 */
	public final void setThread(boolean thread) {
		this.thread = thread;
	}

	/**
	 * @return the photo
	 */
	public final boolean isPhoto() {
		return photo;
	}

	/**
	 * @param photo the photo to set
	 */
	public final void setPhoto(boolean photo) {
		this.photo = photo;
	}

	/**
	 * @return the special
	 */
	public final boolean isSpecial() {
		return special;
	}

	/**
	 * @param special the special to set
	 */
	public final void setSpecial(boolean special) {
		this.special = special;
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
	 * @return the urls
	 */
	public final List<String> getUrls() {
		return urls;
	}

	/**
	 * @param urls the urls to set
	 */
	public final void setUrls(List<String> urls) {
		this.urls = urls;
	}

	/**
	 * @return the hashtags
	 */
	public final List<String> getHashtags() {
		return hashtags;
	}

	/**
	 * @param hashtags the hashtags to set
	 */
	public final void setHashtags(List<String> hashtags) {
		this.hashtags = hashtags;
	}

	/**
	 * @return the names
	 */
	public final List<String> getNames() {
		return names;
	}

	/**
	 * @param names the names to set
	 */
	public final void setNames(List<String> names) {
		this.names = names;
	}

}
