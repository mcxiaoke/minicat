package com.fanfou.app.hd.dao.model;

import java.util.Date;

import android.content.ContentValues;
import android.os.Parcel;
import android.os.Parcelable;

import com.fanfou.app.hd.db.Contents.DraftInfo;

/**
 * @author mcxiaoke
 * @version 1.0 2011.12.21
 * 
 */
public class RecordModel extends BaseModel<RecordModel> {
	public static final int TYPE_NONE = 0;
	public static final int ID_NONE = 0;

	public String text;
	public String reply;
	public String file;

	public RecordModel() {

	}

	public RecordModel(Parcel in) {
		readBase(in);
		text = in.readString();
		reply = in.readString();
		file = in.readString();
	}


	public ContentValues values() {
		ContentValues cv = convert();
		cv.put(RecordColumns.TEXT, text);
		cv.put(RecordColumns.REPLY, reply);
		cv.put(RecordColumns.FILE, file);
		return cv;
	}

	@Override
	public int describeContents() {
		return 0;
	}

	@Override
	public void writeToParcel(Parcel dest, int flags) {
		writeBase(dest, flags);
		dest.writeString(text);
		dest.writeString(reply);
		dest.writeString(file);
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
	
	@Override
	public String toString() {
		return "id=" + id + " text= " + text + " filepath=" + file;
	}

}
