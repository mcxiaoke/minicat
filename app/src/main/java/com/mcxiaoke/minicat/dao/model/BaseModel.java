package com.mcxiaoke.minicat.dao.model;

import android.content.ContentValues;
import android.os.Parcel;


/**
 * @author mcxiaoke
 * @version 2.0 2012.02.16
 */
public abstract class BaseModel implements Model {
    public static final int TYPE_NONE = 0;

    protected String id;// id in string format
    protected String account; // related account id/userid
    protected String owner; // owner id of the item
    protected String note; // note of the item, reserved

    protected int type; // type of the item
    protected int flag; // flag of the item, reserved

    protected long rawid; // raw id in number format
    protected long time; // created at of the item

    public BaseModel() {
    }

    protected void readBase(Parcel in) {
        id = in.readString();
        owner = in.readString();
        account = in.readString();
        note = in.readString();

        type = in.readInt();
        flag = in.readInt();

        rawid = in.readLong();
        time = in.readLong();
    }

    protected void writeBase(Parcel dest, int flags) {
        dest.writeString(id);
        dest.writeString(owner);
        dest.writeString(account);
        dest.writeString(note);

        dest.writeInt(type);
        dest.writeInt(flag);

        dest.writeLong(rawid);
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

        cv.put(IBaseColumns.RAWID, rawid);
        cv.put(IBaseColumns.TIME, time);

        return cv;
    }


    public String getId() {
        return id;
    }

    public void setId(String id) {
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

    public long getRawid() {
        return rawid;
    }

    public void setRawid(long rawid) {
        this.rawid = rawid;
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
