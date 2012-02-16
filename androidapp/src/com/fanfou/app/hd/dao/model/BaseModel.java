package com.fanfou.app.hd.dao.model;

import android.content.ContentValues;
import android.os.Parcel;
import android.os.Parcelable;


/**
 * @author mcxiaoke
 * @version 1.0 2011.12.21
 * @version 2.0 2012.02.16
 * 
 */
abstract class BaseModel<T> implements Model, Parcelable {

	protected String idstr;// id in string format
	protected String account; // related account id/userid
	protected String owner; // owner id of the item
	protected String note; // note of the item, reserved

	protected int type; // type of the item
	protected int flag; // flag of the item, reserved

	protected long id; // raw id in number format
	protected long time; // created at of the item
	
	public BaseModel(){}

	protected void readBase(Parcel in) {
		idstr = in.readString();
		owner = in.readString();
		account = in.readString();
		note = in.readString();

		type = in.readInt();
		flag = in.readInt();

		id = in.readLong();
		time = in.readLong();
	}

	protected void writeBase(Parcel dest, int flags) {
		dest.writeString(idstr);
		dest.writeString(owner);
		dest.writeString(account);
		dest.writeString(note);

		dest.writeInt(type);
		dest.writeInt(flag);

		dest.writeLong(id);
		dest.writeLong(time);
	}

	protected ContentValues convert() {
		ContentValues cv = new ContentValues();

		cv.put(IBaseColumns.ID, id);
		cv.put(IBaseColumns.OWNER, owner);
		cv.put(IBaseColumns.ACCOUNT, account);
		cv.put(IBaseColumns.NOTE, note);

		cv.put(IBaseColumns.TYPE, type);
		cv.put(IBaseColumns.FLAG, flag);

		cv.put(IBaseColumns.ID, id);
		cv.put(IBaseColumns.TIME, time);

		return cv;
	}

	public abstract ContentValues values();

	public String getIdstr() {
		return idstr;
	}

	public void setIdstr(String idstr) {
		this.idstr = idstr;
	}

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public String getAccount() {
		return account;
	}

	public void setAccount(String account) {
		this.account = account;
	}

	public String getOwner() {
		return owner;
	}

	public void setOwner(String owner) {
		this.owner = owner;
	}

	public long getTime() {
		return time;
	}

	public void setTime(long time) {
		this.time = time;
	}

	public int getType() {
		return type;
	}

	public void setType(int type) {
		this.type = type;
	}

	public int getFlag() {
		return flag;
	}

	public void setFlag(int flag) {
		this.flag = flag;
	}

	public String getNote() {
		return note;
	}

	public void setNote(String note) {
		this.note = note;
	}

}
