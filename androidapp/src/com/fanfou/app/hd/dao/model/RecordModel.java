package com.fanfou.app.hd.dao.model;
import com.fanfou.app.hd.controller.DataController;

import android.content.ContentValues;
import android.database.Cursor;
import android.net.Uri;
import android.os.Parcel;
import android.os.Parcelable;

/**
 * @author mcxiaoke
 * @version 1.0 2011.12.21
 * @version 2.0 2012.02.22
 * 
 */
public class RecordModel extends BaseModel {
	public static final int TYPE_STATUS=401;
	public static final int TYPE_MESSAGE=402;

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
	
	public static RecordModel from(Cursor cursor){
		if(cursor==null){
			return null;
		}
		
		RecordModel rm=new RecordModel();
		
		rm.id=DataController.parseString(cursor, RecordColumns.ID);
		rm.account=DataController.parseString(cursor, RecordColumns.ACCOUNT);
		rm.owner=DataController.parseString(cursor, RecordColumns.OWNER);
		rm.note=DataController.parseString(cursor, RecordColumns.NOTE);
		
		rm.type=DataController.parseInt(cursor, RecordColumns.TYPE);
		rm.flag=DataController.parseInt(cursor, RecordColumns.FLAG);
		
		rm.rawid=DataController.parseLong(cursor, RecordColumns.RAWID);
		rm.time=DataController.parseLong(cursor, RecordColumns.TIME);
		
		rm.text=DataController.parseString(cursor, RecordColumns.TEXT);
		rm.reply=DataController.parseString(cursor, RecordColumns.REPLY);
		rm.file=DataController.parseString(cursor, RecordColumns.FILE);
		
		
		return rm;
		
		
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

	public String getText() {
		return text;
	}

	public void setText(String text) {
		this.text = text;
	}

	public String getReply() {
		return reply;
	}

	public void setReply(String reply) {
		this.reply = reply;
	}

	public String getFile() {
		return file;
	}

	public void setFile(String file) {
		this.file = file;
	}

	@Override
	public Uri getContentUri() {
		// TODO Auto-generated method stub
		return RecordColumns.CONTENT_URI;
	}

	@Override
	public String getTable() {
		return RecordColumns.TABLE_NAME;
	}

}
