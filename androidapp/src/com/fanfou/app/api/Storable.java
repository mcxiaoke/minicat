package com.fanfou.app.api;

import java.io.Serializable;

import android.content.ContentValues;
import android.database.Cursor;

/**
 * can store into database
 * 
 * @author mcxiaoke
 * 
 * @param <T>
 */
public interface Storable<T> extends Serializable, Comparable<T> {

	ContentValues toContentValues();

}
