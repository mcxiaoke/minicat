package com.mcxiaoke.minicat.adapter;

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
import com.mcxiaoke.minicat.AppContext;
import com.mcxiaoke.minicat.R;
import com.mcxiaoke.minicat.dao.model.IBaseColumns;
import com.mcxiaoke.minicat.dao.model.UserColumns;
import com.mcxiaoke.minicat.dao.model.UserModel;

/**
 * @author mcxiaoke
 * @version 1.3 2011.12.05
 */
public class AutoCompleteCursorAdapter extends CursorAdapter {
    private static final String TAG = AutoCompleteCursorAdapter.class
            .getSimpleName();

    private Activity mContext;
    private LayoutInflater mInflater;

    public AutoCompleteCursorAdapter(Activity context, Cursor cursor) {
        super(context, cursor, FLAG_REGISTER_CONTENT_OBSERVER);
        mContext = context;
        mInflater = LayoutInflater.from(mContext);
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        View view = mInflater.inflate(R.layout.list_item_autocomplete, parent,
                false);
        return view;
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
    public CharSequence convertToString(Cursor cursor) {
        return cursor.getString(cursor.getColumnIndex(UserColumns.SCREEN_NAME));
    }

    // private static final Pattern PATTERN_SQL=Pattern.compile("[\\W]+");
    @Override
    public Cursor runQueryOnBackgroundThread(CharSequence constraint) {
        if (TextUtils.isEmpty(constraint)) {
            return null;
        }

        if (AppContext.DEBUG) {
            Log.d(TAG, "constraint = " + constraint);
        }

        final String[] projection = new String[]{BaseColumns._ID,
                IBaseColumns.ID, UserColumns.SCREEN_NAME, IBaseColumns.TYPE,
                IBaseColumns.OWNER};
        String where = IBaseColumns.OWNER + " = '" + AppContext.getAccount() + "' AND "
                + IBaseColumns.TYPE + " = '" + UserModel.TYPE_FRIENDS
                + "' AND " + UserColumns.SCREEN_NAME + " like '%" + constraint
                + "%' OR " + IBaseColumns.ID + " like '%" + constraint + "%'";
        if (AppContext.DEBUG) {
            Log.d(TAG, "runQueryOnBackgroundThread where=" + where);
        }
        return mContext.getContentResolver().query(UserColumns.CONTENT_URI,
                projection, where, null, null);
    }

}
