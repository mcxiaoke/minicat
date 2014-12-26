package com.mcxiaoke.minicat.dao.model;

import android.content.ContentValues;
import android.net.Uri;
import android.os.Parcelable;

/**
 * @author mcxiaoke
 * @version 1.0 2011.12.21
 */
public interface Model extends Parcelable {

    public abstract ContentValues values();

    public abstract Uri getContentUri();

    public abstract String getTable();
}
