package com.mcxiaoke.minicat.dao.model;

import android.content.ContentValues;
import android.database.Cursor;
import android.net.Uri;
import android.os.Parcel;
import android.os.Parcelable;
import com.mcxiaoke.minicat.controller.DataController;


/**
 * @author mcxiaoke
 * @version 1.3 2012.02.27
 */
public class DirectMessageModel extends BaseModel {
    public static final int TYPE_CONVERSATION_LIST = 301;
    public static final int TYPE_CONVERSATION = 302;
    public static final int TYPE_INBOX = 303;
    public static final int TYPE_OUTBOX = 304;

    public static final String TAG = DirectMessageModel.class.getSimpleName();
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
    private String text;
    private String senderId;
    private String senderScreenName;
    private String senderProfileImageUrl;
    private String recipientId;
    private String recipientScreenName;
    private String recipientProfileImageUrl;
    private String conversationId;
    private boolean read;
    private boolean incoming;
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
        incoming = in.readInt() == 0 ? false : true;
    }

    public static DirectMessageModel from(Cursor cursor) {
        if (cursor == null) {
            return null;
        }

        DirectMessageModel dm = new DirectMessageModel();
        dm.id = DataController.parseString(cursor, IBaseColumns.ID);
        dm.account = DataController.parseString(cursor,
                IBaseColumns.ACCOUNT);
        dm.owner = DataController.parseString(cursor,
                IBaseColumns.OWNER);
        dm.note = DataController.parseString(cursor, IBaseColumns.NOTE);

        dm.type = DataController.parseInt(cursor, IBaseColumns.TYPE);
        dm.flag = DataController.parseInt(cursor, IBaseColumns.FLAG);

        dm.rawid = DataController.parseLong(cursor, IBaseColumns.RAWID);
        dm.time = DataController.parseLong(cursor, IBaseColumns.TIME);

        dm.text = DataController.parseString(cursor, DirectMessageColumns.TEXT);

        dm.senderId = DataController.parseString(cursor,
                DirectMessageColumns.SENDER_ID);
        dm.senderScreenName = DataController.parseString(cursor,
                DirectMessageColumns.SENDER_SCREEN_NAME);
        dm.senderProfileImageUrl = DataController.parseString(cursor,
                DirectMessageColumns.SENDER_PROFILE_IMAGE_URL);

        dm.recipientId = DataController.parseString(cursor,
                DirectMessageColumns.RECIPIENT_ID);
        dm.recipientScreenName = DataController.parseString(cursor,
                DirectMessageColumns.RECIPIENT_SCREEN_NAME);
        dm.recipientProfileImageUrl = DataController.parseString(cursor,
                DirectMessageColumns.RECIPIENT_PROFILE_IMAGE_URL);

        dm.conversationId = DataController.parseString(cursor,
                DirectMessageColumns.CONVERSATION_ID);

        dm.read = DataController
                .parseBoolean(cursor, DirectMessageColumns.READ);
        dm.incoming = DataController.parseBoolean(cursor,
                DirectMessageColumns.INCOMING);

        return dm;
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
        cv.put(DirectMessageColumns.INCOMING, this.incoming);

        return cv;
    }

    @Override
    public Uri getContentUri() {
        return DirectMessageColumns.CONTENT_URI;
    }

    @Override
    public String getTable() {
        return DirectMessageColumns.TABLE_NAME;
    }

    @Override
    public int describeContents() {
        return 0;
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
        dest.writeInt(incoming ? 1 : 0);

    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public String getSenderId() {
        return senderId;
    }

    public void setSenderId(String senderId) {
        this.senderId = senderId;
    }

    public String getSenderScreenName() {
        return senderScreenName;
    }

    public void setSenderScreenName(String senderScreenName) {
        this.senderScreenName = senderScreenName;
    }

    public String getSenderProfileImageUrl() {
        return senderProfileImageUrl;
    }

    public void setSenderProfileImageUrl(String senderProfileImageUrl) {
        this.senderProfileImageUrl = senderProfileImageUrl;
    }

    public String getRecipientId() {
        return recipientId;
    }

    public void setRecipientId(String recipientId) {
        this.recipientId = recipientId;
    }

    public String getRecipientScreenName() {
        return recipientScreenName;
    }

    public void setRecipientScreenName(String recipientScreenName) {
        this.recipientScreenName = recipientScreenName;
    }

    public String getRecipientProfileImageUrl() {
        return recipientProfileImageUrl;
    }

    public void setRecipientProfileImageUrl(String recipientProfileImageUrl) {
        this.recipientProfileImageUrl = recipientProfileImageUrl;
    }

    public String getConversationId() {
        return conversationId;
    }

    public void setConversationId(String conversationId) {
        this.conversationId = conversationId;
    }

    public boolean isRead() {
        return read;
    }

    public void setRead(boolean read) {
        this.read = read;
    }

    public boolean isIncoming() {
        return incoming;
    }

    public void setIncoming(boolean incoming) {
        this.incoming = incoming;
    }

    public UserModel getSender() {
        return sender;
    }

    public void setSender(UserModel sender) {
        this.sender = sender;
        if (sender != null) {
            this.senderProfileImageUrl = sender.getProfileImageUrlLarge();
        }
    }

    public UserModel getRecipient() {
        return recipient;
    }

    public void setRecipient(UserModel recipient) {
        this.recipient = recipient;
        if (recipient != null) {
            this.recipientProfileImageUrl = recipient.getProfileImageUrlLarge();
        }
    }

    @Override
    public String toString() {
        return "DirectMessageModel [ id=" + id + " text=" + text + ", senderId=" + senderId
                + ", senderScreenName=" + senderScreenName + ", recipientId="
                + recipientId + ", recipientScreenName=" + recipientScreenName
                + ", conversationId=" + conversationId + ", incoming="
                + incoming + "]";
    }

}
