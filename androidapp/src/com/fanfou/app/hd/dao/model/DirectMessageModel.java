package com.fanfou.app.hd.dao.model;

import java.util.Date;

import android.content.ContentValues;
import android.os.Parcel;
import android.os.Parcelable;

import com.fanfou.app.hd.db.Contents.BasicColumns;
import com.fanfou.app.hd.db.Contents.DirectMessageInfo;

/**
 * @author mcxiaoke
 * @version 1.0 2011.12.21
 * 
 */
public class DirectMessageModel  extends AbstractModel<DirectMessageModel> {

	public static final String TAG = DirectMessageModel.class.getSimpleName();

	private String id;
	private String account;
	private String owner;
	private int type;
	private Date time;
	
	private String text;
	private String senderId;
	private String senderScreenName;
	private String recipientId;
	private String recipientScreenName;

	private String senderProfileImageUrl;
	private String recipientProfileImageUrl;


	private String threadUserId;
	private String threadUserName;
	private boolean read;

	private UserModel sender = null;
	private UserModel recipient = null;

	public DirectMessageModel() {

	}

	public DirectMessageModel(Parcel in) {
		id = in.readString();
		account = in.readString();
		time = new Date(in.readLong());
		type = in.readInt();
		
		senderId=in.readString();
		recipientId=in.readString();
		text=in.readString();
		
		senderScreenName=in.readString();
		recipientScreenName=in.readString();
		senderProfileImageUrl=in.readString();
		recipientProfileImageUrl=in.readString();
		
		senderId=in.readString();
		senderId=in.readString();
		
		read = in.readInt() == 0 ? false : true;
	}
	
	@Override
	public void put() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public DirectMessageModel get(String key) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public ContentValues values() {
		ContentValues cv = new ContentValues();

		cv.put(BasicColumns.ID, this.id);
		cv.put(BasicColumns.OWNER_ID, this.account);
		cv.put(BasicColumns.CREATED_AT, this.time.getTime());
		cv.put(BasicColumns.TYPE, this.type);

		cv.put(DirectMessageInfo.SENDER_ID, this.senderId);
		cv.put(DirectMessageInfo.RECIPIENT_ID, this.recipientId);
		cv.put(DirectMessageInfo.TEXT, this.text);

		cv.put(DirectMessageInfo.SENDER_SCREEN_NAME, this.senderScreenName);
		cv.put(DirectMessageInfo.RECIPIENT_SCREEN_NAME,
				this.recipientScreenName);

		cv.put(DirectMessageInfo.SENDER_PROFILE_IMAGE_URL,
				this.senderProfileImageUrl);
		cv.put(DirectMessageInfo.RECIPIENT_PROFILE_IMAGE_URL,
				this.recipientProfileImageUrl);

		cv.put(DirectMessageInfo.THREAD_USER_ID, this.threadUserId);
		cv.put(DirectMessageInfo.THREAD_USER_NAME, this.threadUserName);
		cv.put(DirectMessageInfo.IS_READ, this.read);

		return cv;
	}

	@Override
	public String toString() {
		return "[Message] " + BasicColumns.ID + "=" + this.id + " "
				+ DirectMessageInfo.TEXT + "=" + this.text + " "
				+ BasicColumns.CREATED_AT + "=" + this.time + " "
				+ DirectMessageInfo.SENDER_ID + "=" + this.senderId + " "
				+ DirectMessageInfo.RECIPIENT_ID + "=" + this.recipientId + " ";
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
		
		dest.writeString(senderId);
		dest.writeString(recipientId);
		dest.writeString(text);
		
		dest.writeString(senderScreenName);
		dest.writeString(recipientScreenName);
		dest.writeString(senderProfileImageUrl);
		dest.writeString(recipientProfileImageUrl);
		
		dest.writeString(threadUserId);
		dest.writeString(threadUserName);
		
		dest.writeInt(read?1:0);
		
	}

	public static final Parcelable.Creator<DirectMessageModel> CREATOR = new Parcelable.Creator<DirectMessageModel>() {

		@Override
		public DirectMessageModel createFromParcel(Parcel source) {
			return new DirectMessageModel(source);
		}

		@Override
		public DirectMessageModel[] newArray(int size) {
			return new DirectMessageModel[size];
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
	 * @return the senderId
	 */
	public final String getSenderId() {
		return senderId;
	}

	/**
	 * @param senderId the senderId to set
	 */
	public final void setSenderId(String senderId) {
		this.senderId = senderId;
	}

	/**
	 * @return the senderScreenName
	 */
	public final String getSenderScreenName() {
		return senderScreenName;
	}

	/**
	 * @param senderScreenName the senderScreenName to set
	 */
	public final void setSenderScreenName(String senderScreenName) {
		this.senderScreenName = senderScreenName;
	}

	/**
	 * @return the recipientId
	 */
	public final String getRecipientId() {
		return recipientId;
	}

	/**
	 * @param recipientId the recipientId to set
	 */
	public final void setRecipientId(String recipientId) {
		this.recipientId = recipientId;
	}

	/**
	 * @return the recipientScreenName
	 */
	public final String getRecipientScreenName() {
		return recipientScreenName;
	}

	/**
	 * @param recipientScreenName the recipientScreenName to set
	 */
	public final void setRecipientScreenName(String recipientScreenName) {
		this.recipientScreenName = recipientScreenName;
	}

	/**
	 * @return the senderProfileImageUrl
	 */
	public final String getSenderProfileImageUrl() {
		return senderProfileImageUrl;
	}

	/**
	 * @param senderProfileImageUrl the senderProfileImageUrl to set
	 */
	public final void setSenderProfileImageUrl(String senderProfileImageUrl) {
		this.senderProfileImageUrl = senderProfileImageUrl;
	}

	/**
	 * @return the recipientProfileImageUrl
	 */
	public final String getRecipientProfileImageUrl() {
		return recipientProfileImageUrl;
	}

	/**
	 * @param recipientProfileImageUrl the recipientProfileImageUrl to set
	 */
	public final void setRecipientProfileImageUrl(String recipientProfileImageUrl) {
		this.recipientProfileImageUrl = recipientProfileImageUrl;
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
	 * @return the threadUserId
	 */
	public final String getThreadUserId() {
		return threadUserId;
	}

	/**
	 * @param threadUserId the threadUserId to set
	 */
	public final void setThreadUserId(String threadUserId) {
		this.threadUserId = threadUserId;
	}

	/**
	 * @return the threadUserName
	 */
	public final String getThreadUserName() {
		return threadUserName;
	}

	/**
	 * @param threadUserName the threadUserName to set
	 */
	public final void setThreadUserName(String threadUserName) {
		this.threadUserName = threadUserName;
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
	 * @return the sender
	 */
	public final UserModel getSender() {
		return sender;
	}

	/**
	 * @param sender the sender to set
	 */
	public final void setSender(UserModel sender) {
		this.sender = sender;
	}

	/**
	 * @return the recipient
	 */
	public final UserModel getRecipient() {
		return recipient;
	}

	/**
	 * @param recipient the recipient to set
	 */
	public final void setRecipient(UserModel recipient) {
		this.recipient = recipient;
	}

}
