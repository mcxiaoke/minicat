package com.fanfou.app.adapter;

import android.content.Context;
import android.database.Cursor;
import android.view.View;
import android.widget.ResourceCursorAdapter;
import android.widget.TextView;

import com.fanfou.app.App;
import com.fanfou.app.R;
import com.fanfou.app.db.Contents.BasicColumns;
import com.fanfou.app.db.Contents.UserInfo;

/**
 * @author mcxiaoke
 * @version 1.0 2011.08.28
 * @version 1.1 2011.11.02
 * 
 */
public class AutoCompleteCursorAdapter extends ResourceCursorAdapter {
	private Context mContext;
	private Cursor mCursor;

	public AutoCompleteCursorAdapter(Context context, Cursor c) {
		super(context, R.layout.list_item_autocomplete, c);
		mContext = context;
		mCursor=c;
	}

	@Override
	public CharSequence convertToString(Cursor cursor) {
		// final User u=User.parse(cursor);
		return cursor.getString(cursor.getColumnIndex(UserInfo.SCREEN_NAME));
	}

	@Override
	public Cursor runQueryOnBackgroundThread(CharSequence constraint) {
		// String[] projection = new String[] {UserInfo.ID,
		// UserInfo.SCREEN_NAME};
		// String where = UserInfo.OWNER_ID + " = '" + App.me.userId + "' AND "
		// + UserInfo.SCREEN_NAME + " like '%" + constraint + "%'";
		String where = BasicColumns.OWNER_ID + " = '" + App.me.userId + "' AND "
				+ UserInfo.SCREEN_NAME + " like '%" + constraint + "%' OR "
				+ BasicColumns.ID + " like '%" + constraint + "%'";
		Cursor oldCursor=mCursor;
		mCursor= mContext.getContentResolver().query(UserInfo.CONTENT_URI, null,
				where, null, null);
		if(oldCursor!=null){
			oldCursor.close();
		}
		return mCursor;
	}

	@Override
	public void bindView(View view, Context context, Cursor cursor) {
		String id = cursor.getString(cursor.getColumnIndex(BasicColumns.ID));
		String screenName = cursor.getString(cursor
				.getColumnIndex(UserInfo.SCREEN_NAME));
		TextView tv = (TextView) view.findViewById(R.id.item_user_name);
		tv.setText("@" + screenName + " (" + id + ")");
	}

}
