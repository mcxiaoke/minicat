package com.fanfou.app.hd.dao.model;

import android.content.ContentValues;
import android.os.Parcelable;

/**
 * @author mcxiaoke
 * @version 1.0 2011.12.21
 * 
 */
public abstract class AbstractModel<T> implements Model,Parcelable {
	
	public abstract void put();

	public abstract T get(String key);

	public abstract ContentValues values();

}
