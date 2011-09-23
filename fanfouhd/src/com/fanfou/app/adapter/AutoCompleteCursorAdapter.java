package com.fanfou.app.adapter;

import android.content.Context;
import android.database.Cursor;
import android.view.View;
import android.widget.Filterable;
import android.widget.ResourceCursorAdapter;
import android.widget.TextView;

import com.fanfou.app.App;
import com.fanfou.app.R;
import com.fanfou.app.api.User;
import com.fanfou.app.db.Contents.UserInfo;

/**
 * @version 1.0 20110828
 * @author mcxiaoke
 * 
 * 
 */
public class AutoCompleteCursorAdapter extends ResourceCursorAdapter implements
		Filterable {
	private Context mContext;

	public AutoCompleteCursorAdapter(Context context, Cursor c) {
		super(context, R.layout.list_item_autocomplete, c);
		mContext = context;
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
		String where = UserInfo.SCREEN_NAME + " like '%" + constraint
				+ "%' OR " + UserInfo.ID + " like '%" + constraint + "%'";
		return mContext.getContentResolver().query(UserInfo.CONTENT_URI, null,
				where, null, null);
	}

	@Override
	public void bindView(View view, Context context, Cursor cursor) {
		String id = cursor.getString(cursor.getColumnIndex(UserInfo.ID));
		String screenName = cursor.getString(cursor
				.getColumnIndex(UserInfo.SCREEN_NAME));
		TextView tv = (TextView) view.findViewById(R.id.item_user_name);
		tv.setText(screenName + " (" + id.replace("@", "") + ")");
	}

}
