package org.mcxiaoke.fancooker.adapter;

import org.mcxiaoke.fancooker.App;
import org.mcxiaoke.fancooker.R;
import org.mcxiaoke.fancooker.dao.model.IBaseColumns;
import org.mcxiaoke.fancooker.dao.model.UserColumns;
import org.mcxiaoke.fancooker.dao.model.UserModel;

import android.app.Activity;
import android.content.Context;
import android.database.Cursor;
import android.provider.BaseColumns;
import android.support.v4.widget.CursorAdapter;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

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
	private LayoutInflater mInflater;

	public AutoCompleteCursorAdapter(Activity context, Cursor cursor) {
		super(context, cursor,FLAG_REGISTER_CONTENT_OBSERVER);
		mContext = context;
		mInflater = LayoutInflater.from(mContext);
	}

	@Override
	public CharSequence convertToString(Cursor cursor) {
		return cursor.getString(cursor.getColumnIndex(UserColumns.SCREEN_NAME));
	}

	// private static final Pattern PATTERN_SQL=Pattern.compile("[\\W]+");
	@Override
	public Cursor runQueryOnBackgroundThread(CharSequence constraint) {
		if (TextUtils.isEmpty(constraint)) {
			return null;
		}

		if (App.DEBUG) {
			Log.d(TAG, "constraint = " + constraint);
		}

		final String[] projection = new String[] { BaseColumns._ID,
				IBaseColumns.ID, UserColumns.SCREEN_NAME, IBaseColumns.TYPE,
				IBaseColumns.OWNER };
		String where = IBaseColumns.OWNER + " = '" + App.getAccount() + "' AND "
				+ IBaseColumns.TYPE + " = '" + UserModel.TYPE_FRIENDS
				+ "' AND " + UserColumns.SCREEN_NAME + " like '%" + constraint
				+ "%' OR " + IBaseColumns.ID + " like '%" + constraint + "%'";
		if (App.DEBUG) {
			Log.d(TAG, "runQueryOnBackgroundThread where=" + where);
		}
		return mContext.getContentResolver().query(UserColumns.CONTENT_URI,
				projection, where, null, null);
	}

	@Override
	public void bindView(View view, Context context, Cursor cursor) {
		String id = cursor.getString(cursor.getColumnIndex(IBaseColumns.ID));
		String screenName = cursor.getString(cursor
				.getColumnIndex(UserColumns.SCREEN_NAME));
		TextView tv = (TextView) view.findViewById(R.id.name);
		tv.setText("@" + screenName + " (" + id + ")");
	}

	@Override
	public View newView(Context context, Cursor cursor, ViewGroup parent) {
		View view = mInflater.inflate(R.layout.list_item_autocomplete, parent,
				false);
		return view;
	}

}
