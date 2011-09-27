/* 
 * Copyright (C) 2008 Torgny Bjers
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.fanfou.app.adapter;

import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.widget.FilterQueryProvider;
import android.widget.SimpleCursorAdapter;

/**
 * SearchableCursorAdapter provides an easy way of extending SimpleCursorAdapter
 * in order to allow for searches from auto-complete text fields and the like.
 * 
 * @author torgny.bjers
 */
public class SearchableCursorAdapter extends SimpleCursorAdapter implements FilterQueryProvider {

	private ContentResolver mContentResolver;
	private String[] mProjection;
	private String[] mFrom;
	private Uri mUri;
	private String mSortOrder;
	private Cursor mCursor;

	/**
	 * Creates a new searchable cursor adapter for auto-complete and similar.
	 * 
	 * @see android.widget.SimpleCursorAdapter#SimpleCursorAdapter(Context, int, Cursor, String[], int[])
	 * @param context
	 * @param layout
	 * @param c
	 * @param from
	 * @param to
	 */
	public SearchableCursorAdapter(Context context, int layout, Cursor c,
			String[] from, int[] to, Uri uri, String[] projection, String sortOrder) {
		super(context, layout, c, from, to);
		mContentResolver = context.getContentResolver();
		mFrom = from;
		mProjection = projection;
		mUri = uri;
		mSortOrder = sortOrder;
		setFilterQueryProvider(this);
	}

	/**
	 * Create a new query with the constraint search parameters.
	 * 
	 * @see android.widget.FilterQueryProvider#runQuery(java.lang.CharSequence)
	 */
	@Override
	public Cursor runQuery(CharSequence constraint) {
		String selection = null;
		String selectionArgs[] = null;
		if (constraint != null) {
			selection = mFrom[0] + " LIKE ?";
			String filter = constraint.toString() + "%";
			selectionArgs = new String[] { filter };
		}
		if (mCursor != null && !mCursor.isClosed()) mCursor.close();
		return mContentResolver.query(mUri, mProjection, selection, selectionArgs, mSortOrder);
	}
}
