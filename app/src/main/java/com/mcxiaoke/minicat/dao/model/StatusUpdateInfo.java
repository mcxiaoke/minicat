package com.mcxiaoke.minicat.dao.model;

import android.content.ContentValues;
import android.database.Cursor;
import android.net.Uri;
import android.os.Parcel;
import android.os.Parcelable;
import android.provider.BaseColumns;
import com.mcxiaoke.minicat.controller.DataController;

/**
 * Project: fanfouapp
 * User: mcxiaoke
 * Date: 13-5-28
 * Time: 上午10:16
 */
public class StatusUpdateInfo implements Model {
    public static final String TAG = StatusUpdateInfo.class.getSimpleName();
    public static final int TYPE_NONE = 0;
    public static final int TYPE_REPLY = 1;
    public static final int TYPE_REPOST = 2;
    public static final Parcelable.Creator<StatusUpdateInfo> CREATOR = new Creator<StatusUpdateInfo>() {
        @Override
        public StatusUpdateInfo createFromParcel(Parcel source) {
            return new StatusUpdateInfo(source);
        }

        @Override
        public StatusUpdateInfo[] newArray(int size) {
            return new StatusUpdateInfo[size];
        }
    };
    public int id;
    public int type;
    public String userId;
    public String text;
    public String location;
    public String reply;
    public String repost;
    public String fileName;

    public StatusUpdateInfo() {

    }

    public StatusUpdateInfo(Parcel in) {
        this.id = in.readInt();
        this.type = in.readInt();
        this.userId = in.readString();
        this.text = in.readString();
        this.location = in.readString();
        this.reply = in.readString();
        this.repost = in.readString();
        this.fileName = in.readString();

    }

    public static StatusUpdateInfo from(Cursor cursor) {
        if (cursor == null) {
            return null;
        }

        StatusUpdateInfo info = new StatusUpdateInfo();

        info.id = DataController.parseInt(cursor, BaseColumns._ID);
        info.type = DataController.parseInt(cursor, StatusUpdateInfoColumns.TYPE);
        info.userId = DataController.parseString(cursor, StatusUpdateInfoColumns.USER_ID);
        info.text = DataController.parseString(cursor, StatusUpdateInfoColumns.TEXT);
        info.location = DataController.parseString(cursor, StatusUpdateInfoColumns.LOCATION);
        info.reply = DataController.parseString(cursor, StatusUpdateInfoColumns.REPLY);
        info.repost = DataController.parseString(cursor, StatusUpdateInfoColumns.REPOST);
        info.fileName = DataController.parseString(cursor, StatusUpdateInfoColumns.FILE);

        return info;


    }

    @Override
    public ContentValues values() {
        ContentValues cv = new ContentValues();
        cv.put(StatusUpdateInfoColumns.TYPE, type);
        cv.put(StatusUpdateInfoColumns.USER_ID, userId);
        cv.put(StatusUpdateInfoColumns.TEXT, text);
        cv.put(StatusUpdateInfoColumns.LOCATION, location);
        cv.put(StatusUpdateInfoColumns.REPLY, reply);
        cv.put(StatusUpdateInfoColumns.REPOST, repost);
        cv.put(StatusUpdateInfoColumns.FILE, fileName);
        return cv;
    }

    @Override
    public Uri getContentUri() {
        return StatusUpdateInfoColumns.CONTENT_URI;
    }

    @Override
    public String getTable() {
        return StatusUpdateInfoColumns.TABLE_NAME;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(this.id);
        dest.writeInt(this.type);
        dest.writeString(this.userId);
        dest.writeString(this.text);
        dest.writeString(this.location);
        dest.writeString(this.reply);
        dest.writeString(this.repost);
        dest.writeString(this.fileName);

    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("StatusUpdateInfo{");
        sb.append("fileName='").append(fileName).append('\'');
        sb.append(", type=").append(type);
        sb.append(", userId='").append(userId).append('\'');
        sb.append(", text='").append(text).append('\'');
        sb.append(", location='").append(location).append('\'');
        sb.append(", reply='").append(reply).append('\'');
        sb.append(", repost='").append(repost).append('\'');
        sb.append('}');
        return sb.toString();
    }
}
