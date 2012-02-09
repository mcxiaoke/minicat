package com.fanfou.app.hd.dao.model;

import java.util.Date;

import android.content.ContentValues;
import android.os.Parcel;
import android.os.Parcelable;

/**
 * @author mcxiaoke
 * @version 1.0 2011.12.21
 * 
 */
public class PhotoModel extends AbstractModel<PhotoModel> {
	public String id;
	public Date createdAt;
	public String thumbUrl;
	public String largeUrl;
	public String imageUrl;

	public PhotoModel() {
	}
	

	@Override
	public void put() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public PhotoModel get(String key) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public ContentValues values() {
		ContentValues cv = new ContentValues();
		cv.put("id", id);
		cv.put("createdAt", createdAt.getTime());
		cv.put("imageUrl", imageUrl);
		cv.put("thumbUrl", thumbUrl);
		cv.put("largeUrl", largeUrl);
		return cv;
	}

	@Override
	public String toString() {
		return largeUrl;
	}

	@Override
	public int describeContents() {
		return 0;
	}

	@Override
	public void writeToParcel(Parcel dest, int flags) {
		dest.writeString(id);
		dest.writeLong(createdAt.getTime());
		dest.writeString(imageUrl);
		dest.writeString(largeUrl);
		dest.writeString(thumbUrl);
	}

	public static final Parcelable.Creator<PhotoModel> CREATOR = new Parcelable.Creator<PhotoModel>() {

		@Override
		public PhotoModel createFromParcel(Parcel source) {
			return new PhotoModel(source);
		}

		@Override
		public PhotoModel[] newArray(int size) {
			return new PhotoModel[size];
		}
	};

	public PhotoModel(Parcel in) {
		id = in.readString();
		createdAt = new Date(in.readLong());
		imageUrl = in.readString();
		largeUrl = in.readString();
		thumbUrl = in.readString();
	}

}
