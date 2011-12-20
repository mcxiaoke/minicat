package com.fanfou.app.adapter;

import android.app.Activity;
import android.content.Context;
import android.database.Cursor;
import android.provider.BaseColumns;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.TextView;

import com.fanfou.app.App;
import com.fanfou.app.R;
import com.fanfou.app.db.Contents.BasicColumns;
import com.fanfou.app.db.Contents.UserInfo;
import com.fanfou.app.service.Constants;
import com.fanfou.app.service.FanFouService;

/**
 * @author mcxiaoke
 * @version 1.0 2011.08.28
 * @version 1.1 2011.11.02
 * @version 1.2 2011.11.21
 * @version 1.3 2011.12.05
 * 
 */
public class AutoCompleteCursorAdapter extends CursorAdapter {
	private static final String TAG = AutoCompleteCursorAdapter.class
			.getSimpleName();

	private Activity mContext;
	// private Cursor mCursor;
	private LayoutInflater mInflater;

	public AutoCompleteCursorAdapter(Activity context, Cursor cursor) {
		super(context, cursor);
		mContext = context;
		mInflater = LayoutInflater.from(mContext);
		// mCursor = cursor;
	}

	@Override
	public CharSequence convertToString(Cursor cursor) {
		return cursor.getString(cursor.getColumnIndex(UserInfo.SCREEN_NAME));
	}

	// private static final Pattern PATTERN_SQL=Pattern.compile("[\\W]+");
	@Override
	public Cursor runQueryOnBackgroundThread(CharSequence constraint) {
		if (TextUtils.isEmpty(constraint)) {
			return null;
		}

		// String condition=PATTERN_SQL.matcher(constraint).replaceAll("");
		if (App.DEBUG) {
			Log.d(TAG, "constraint = " + constraint);
			// Log.d(TAG, "condition = "+condition);
		}

		final String[] projection = new String[] { BaseColumns._ID,
				BasicColumns.ID, UserInfo.SCREEN_NAME, BasicColumns.TYPE,
				BasicColumns.OWNER_ID };
		String where = BasicColumns.OWNER_ID + " = '" + App.getUserId() + "' AND "
				+ BasicColumns.TYPE + " = '" + Constants.TYPE_USERS_FRIENDS
				+ "' AND " + UserInfo.SCREEN_NAME + " like '%" + constraint
				+ "%' OR " + BasicColumns.ID + " like '%" + constraint + "%'";
		if (App.DEBUG) {
			Log.d(TAG, "runQueryOnBackgroundThread where=" + where);
		}

		// Cursor oldCursor = getCursor();

		// return mContext.getContentResolver().query(UserInfo.CONTENT_URI,
		// projection, where, null, null);

		return mContext.getContentResolver().query(UserInfo.CONTENT_URI, projection, where,
				null, null);
		// if(oldCursor!=null){
		// oldCursor.close();
		// oldCursor = null;
		// }
		// return newCursor;
	}

	@Override
	public void bindView(View view, Context context, Cursor cursor) {
		String id = cursor.getString(cursor.getColumnIndex(BasicColumns.ID));
		String screenName = cursor.getString(cursor
				.getColumnIndex(UserInfo.SCREEN_NAME));
		TextView tv = (TextView) view.findViewById(R.id.item_user_name);
		tv.setText("@" + screenName + " (" + id + ")");
	}

	@Override
	public View newView(Context context, Cursor cursor, ViewGroup parent) {
		View view = mInflater.inflate(R.layout.list_item_autocomplete, parent,
				false);
		return view;
	}

}
