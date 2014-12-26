package com.mcxiaoke.minicat.adapter;

import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.widget.FilterQueryProvider;

/**
 * @author mcxiaoke
 * @version 1.0 2011.10.24
 */
public class SearchQueryProvider implements FilterQueryProvider {
    public final Uri mUri;
    public final String[] mProjection;
    public final String mContent;
    public final String mSortOrder;
    public final String mWhere;
    public final String[] mWhereArgs;
    private final Context mContext;

    public SearchQueryProvider(Context context, Uri uri, String[] projection,
                               String where, String[] whereArgs, String content, String sortOrder) {
        this.mContext = context;
        this.mUri = uri;
        this.mProjection = projection;
        this.mWhere = where;
        this.mWhereArgs = whereArgs;
        this.mContent = content;
        this.mSortOrder = sortOrder;
    }

    @Override
    public Cursor runQuery(CharSequence constraint) {
        String selection = mWhere;
        String selectionArgs[] = mWhereArgs;
        if (constraint != null) {
            if (mWhere != null) {
                selection += " AND " + mContent + " LIKE ?";
                String filter = constraint.toString() + "%";
                selectionArgs = new String[]{filter};
            } else {
                selection = mContent + " LIKE ?";
                String filter = constraint.toString() + "%";
                selectionArgs = new String[]{filter};
            }

        }
        return mContext.getContentResolver().query(mUri, mProjection,
                selection, selectionArgs, mSortOrder);
    }

}
