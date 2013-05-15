package com.mcxiaoke.fanfouapp.dao.model;

import android.content.ContentValues;
import android.database.Cursor;
import android.net.Uri;
import android.os.Parcel;
import android.os.Parcelable;
import android.provider.BaseColumns;
import com.mcxiaoke.fanfouapp.controller.DataController;


/**
 * @author mcxiaoke
 * @version 1.0 2011.12.21
 * @version 2.0 2012.02.22
 * @version 3.0 2012.03.02
 * 
 */
public class RecordModel implements Model{
	public static final int TYPE_STATUS=401;
	public static final int TYPE_MESSAGE=402;

	private int id;
	private int type;
	public String text;
	public String reply;
	public String file;

	public RecordModel() {

	}

	public RecordModel(Parcel in) {
		id=in.readInt();
		type=in.readInt();
		text = in.readString();
		reply = in.readString();
		file = in.readString();
	}
	
	public static RecordModel from(Cursor cursor){
		if(cursor==null){
			return null;
		}
		
		RecordModel rm=new RecordModel();
		
		rm.id=DataController.parseInt(cursor, BaseColumns._ID);
		rm.type=DataController.parseInt(cursor, RecordColumns.TYPE);
		rm.text=DataController.parseString(cursor, RecordColumns.TEXT);
		rm.reply=DataController.parseString(cursor, RecordColumns.REPLY);
		rm.file=DataController.parseString(cursor, RecordColumns.FILE);
		
		return rm;
		
		
	}


	@Override
	public ContentValues values() {
		ContentValues cv=new ContentValues();
		cv.put(RecordColumns.TYPE, type);
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
		dest.writeInt(id);
		dest.writeInt(type);
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

	public int getId() {
		return id;
	}

	public int getType() {
		return type;
	}

	public void setType(int type) {
		this.type = type;
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
