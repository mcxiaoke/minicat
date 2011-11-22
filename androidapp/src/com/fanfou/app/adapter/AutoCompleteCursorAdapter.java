package com.fanfou.app.adapter;

import android.content.Context;
import android.database.Cursor;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.ResourceCursorAdapter;
import android.widget.TextView;

import com.fanfou.app.App;
import com.fanfou.app.R;
import com.fanfou.app.api.User;
import com.fanfou.app.db.Contents.BasicColumns;
import com.fanfou.app.db.Contents.UserInfo;

/**
 * @author mcxiaoke
 * @version 1.0 2011.08.28
 * @version 1.1 2011.11.02
 * @version 1.2 2011.11.21
 * 
 */
public class AutoCompleteCursorAdapter extends ResourceCursorAdapter {
	private static final String TAG = AutoCompleteCursorAdapter.class
			.getSimpleName();

	private Context mContext;

	public AutoCompleteCursorAdapter(Context context, Cursor c) {
		super(context, R.layout.list_item_autocomplete, c);
		mContext = context;
	}

	@Override
	public CharSequence convertToString(Cursor cursor) {
		return cursor.getString(cursor.getColumnIndex(UserInfo.SCREEN_NAME));
	}

	@Override
	public Cursor runQueryOnBackgroundThread(CharSequence constraint) {
		if (TextUtils.isEmpty(constraint)) {
			return null;
		}
		final String[] projection = new String[] { UserInfo._ID, UserInfo.ID,
				UserInfo.SCREEN_NAME, UserInfo.TYPE, UserInfo.OWNER_ID };
		String where = UserInfo.OWNER_ID + " = '" + App.me.userId + "' AND "
				+ UserInfo.TYPE + " = '" + User.TYPE_FRIENDS + "' AND "
				+ UserInfo.SCREEN_NAME + " like '%" + constraint + "%' OR "
				+ UserInfo.ID + " like '%" + constraint + "%'";
		if (App.DEBUG) {
			Log.d(TAG, "runQueryOnBackgroundThread where=" + where);
		}
		return mContext.getContentResolver().query(UserInfo.CONTENT_URI,
				projection, where, null, null);
	}

	@Override
	public void bindView(View view, Context context, Cursor cursor) {
		String id = cursor.getString(cursor.getColumnIndex(UserInfo.ID));
		String screenName = cursor.getString(cursor
				.getColumnIndex(UserInfo.SCREEN_NAME));
		TextView tv = (TextView) view.findViewById(R.id.item_user_name);
		tv.setText("@" + screenName + " (" + id + ")");
	}

}
