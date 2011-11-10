package com.fanfou.app.api;
import android.content.ContentValues;
import android.os.Bundle;
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
	
	void fromContentValues(ContentValues values);
	
//	Bundle toBundle();
//	
//	void fromBundle(Bundle bundle);

}
