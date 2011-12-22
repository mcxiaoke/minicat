package com.fanfou.app.api;

import java.util.Date;

import android.content.ContentValues;
import android.database.Cursor;
import android.os.Parcel;
import android.os.Parcelable;
import android.provider.BaseColumns;

import com.fanfou.app.db.Contents.DraftInfo;

/**
 * @author mcxiaoke
 * @version 1.0 2011.10.26
 * @version 1.1 2011.11.04
 * @version 2.0 2011.11.10
 * @version 3.0 2011.12.21
 * 
 */
public class Draft implements Storable<Draft> {

	public Draft() {

	}

	public Draft(Parcel in) {
		id = in.readInt();
		type = in.readInt();
		createdAt = new Date(in.readLong());
		ownerId = in.readString();
		text = in.readString();
		replyTo = in.readString();
		filePath = in.readString();
	}

	@Override
	public String toString() {
		return "id=" + id + " text= " + text + " filepath=" + filePath;
	}

	public static final int TYPE_NONE = 0;
	public static final int ID_NONE = 0;

	public static final String TAG = Draft.class.getSimpleName();

	public int id;
	public String ownerId;
	public String text;
	public Date createdAt;
	public int type;
	public String replyTo;
	public String filePath;

	public static Draft parse(Cursor c) {
		if (c == null) {
			return null;
		}
		Draft d = new Draft();
		d.id = Parser.parseInt(c, BaseColumns._ID);
		d.ownerId = Parser.parseString(c, DraftInfo.OWNER_ID);
		d.text = Parser.parseString(c, DraftInfo.TEXT);
		d.createdAt = Parser.parseDate(c, DraftInfo.CREATED_AT);
		d.type = Parser.parseInt(c, DraftInfo.TYPE);
		d.replyTo = Parser.parseString(c, DraftInfo.REPLY_TO);
		d.filePath = Parser.parseString(c, DraftInfo.FILE_PATH);
		return d;
	}

	@Override
	public int compareTo(Draft another) {
		return createdAt.compareTo(another.createdAt);
	}

	@Override
	public ContentValues toContentValues() {
		ContentValues cv = new ContentValues();
		cv.put(DraftInfo.OWNER_ID, ownerId);
		cv.put(DraftInfo.TEXT, text);
		cv.put(DraftInfo.CREATED_AT, new Date().getTime());
		cv.put(DraftInfo.TYPE, type);
		cv.put(DraftInfo.REPLY_TO, replyTo);
		cv.put(DraftInfo.FILE_PATH, filePath);
		return cv;
	}

	@Override
	public boolean equals(Object o) {
		if (o == this) {
			return true;
		}
		if (o instanceof Draft) {
			Draft d = (Draft) o;
			if (id == d.id && text.equals(d.text)) {
				return true;
			}
		}
		return false;
	}

	@Override
	public int describeContents() {
		return 0;
	}

	@Override
	public void writeToParcel(Parcel dest, int flags) {
		dest.writeInt(id);
		dest.writeInt(type);
		dest.writeLong(createdAt.getTime());
		dest.writeString(ownerId);
		dest.writeString(text);
		dest.writeString(replyTo);
		dest.writeString(filePath);
	}

	public static final Parcelable.Creator<Draft> CREATOR = new Parcelable.Creator<Draft>() {

		@Override
		public Draft createFromParcel(Parcel source) {
			return new Draft(source);
		}

		@Override
		public Draft[] newArray(int size) {
			return new Draft[size];
		}
	};

}
