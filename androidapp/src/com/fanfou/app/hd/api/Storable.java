package com.fanfou.app.hd.api;

import android.content.ContentValues;
import android.os.Parcelable;

/**
 * can store into database
 * 
 * @author mcxiaoke
 * 
 * @param <T>
 */
public interface Storable<T> extends Parcelable, Comparable<T> {

	ContentValues toContentValues();

}
