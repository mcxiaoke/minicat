package com.fanfou.app.dao.model;

import java.util.Date;

import android.content.ContentValues;
import android.os.Parcel;
import android.os.Parcelable;

import com.fanfou.app.db.Contents.DraftInfo;

/**
 * @author mcxiaoke
 * @version 1.0 2011.12.21
 * 
 */
public class RecordModel extends AbstractModel<RecordModel> {

	public RecordModel() {

	}

	public RecordModel(Parcel in) {
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

	public static final String TAG = RecordModel.class.getSimpleName();

	public int id;
	public String ownerId;
	public String text;
	public Date createdAt;
	public int type;
	public String replyTo;
	public String filePath;
	

	@Override
	public void put() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public RecordModel get(String key) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public ContentValues values() {
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

	public static final Parcelable.Creator<RecordModel> CREATOR = new Parcelable.Creator<RecordModel>() {

		@Override
		public RecordModel createFromParcel(Parcel source) {
			return new RecordModel(source);
		}

		@Override
		public RecordModel[] newArray(int size) {
			return new RecordModel[size];
		}
	};

}
