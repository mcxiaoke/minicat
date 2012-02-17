package com.fanfou.app.hd.dao.model;

import android.content.ContentValues;
import android.os.Parcel;
import android.os.Parcelable;

/**
 * @author mcxiaoke
 * @version 1.0 2011.12.21
 * @version 1.1 2012.02.15
 * 
 */
public class DirectMessageModel extends BaseModel<DirectMessageModel> {

	public static final String TAG = DirectMessageModel.class.getSimpleName();

	private String text;

	private String senderId;
	private String senderScreenName;
	private String senderProfileImageUrl;

	private String recipientId;
	private String recipientScreenName;
	private String recipientProfileImageUrl;

	private String conversationId;

	private boolean read;

	private UserModel sender = null;
	private UserModel recipient = null;

	public DirectMessageModel() {

	}

	public DirectMessageModel(Parcel in) {
		readBase(in);
		text = in.readString();

		senderId = in.readString();
		senderScreenName = in.readString();
		senderProfileImageUrl = in.readString();

		recipientId = in.readString();
		recipientScreenName = in.readString();
		recipientProfileImageUrl = in.readString();

		conversationId = in.readString();

		read = in.readInt() == 0 ? false : true;
	}

	@Override
	public ContentValues values() {
		ContentValues cv = convert();

		cv.put(DirectMessageColumns.TEXT, this.text);

		cv.put(DirectMessageColumns.SENDER_ID, this.senderId);
		cv.put(DirectMessageColumns.SENDER_SCREEN_NAME, this.senderScreenName);
		cv.put(DirectMessageColumns.SENDER_PROFILE_IMAGE_URL,
				this.senderProfileImageUrl);

		cv.put(DirectMessageColumns.RECIPIENT_ID, this.recipientId);
		cv.put(DirectMessageColumns.RECIPIENT_SCREEN_NAME,
				this.recipientScreenName);
		cv.put(DirectMessageColumns.RECIPIENT_PROFILE_IMAGE_URL,
				this.recipientProfileImageUrl);

		cv.put(DirectMessageColumns.CONVERSATION_ID, this.conversationId);

		cv.put(DirectMessageColumns.READ, this.read);

		return cv;
	}

	@Override
	public void writeToParcel(Parcel dest, int flags) {
		writeBase(dest, flags);
		dest.writeString(text);

		dest.writeString(senderId);
		dest.writeString(senderScreenName);
		dest.writeString(senderProfileImageUrl);

		dest.writeString(recipientId);
		dest.writeString(recipientScreenName);
		dest.writeString(recipientProfileImageUrl);

		dest.writeString(conversationId);

		dest.writeInt(read ? 1 : 0);

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

	@Override
	public String toString() {
		return "[Message] " + DirectMessageColumns.ID;
	}

	@Override
	public int describeContents() {
		return 0;
	}

}
