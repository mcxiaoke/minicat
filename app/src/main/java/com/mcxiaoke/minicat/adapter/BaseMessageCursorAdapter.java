package com.mcxiaoke.minicat.adapter;

import android.content.Context;
import android.database.Cursor;
import android.view.View;
import android.view.ViewGroup;
import com.mcxiaoke.minicat.R;
import com.mcxiaoke.minicat.ui.widget.ItemView;

/**
 * @author mcxiaoke
 * @version 2.0 2012.02.28
 */
public abstract class BaseMessageCursorAdapter extends BaseCursorAdapter {

    private static final String TAG = BaseMessageCursorAdapter.class
            .getSimpleName();

    public BaseMessageCursorAdapter(Context context) {
        super(context, null);
    }

    public BaseMessageCursorAdapter(Context context, Cursor c) {
        super(context, c);
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        ItemView view = new ItemView(mContext);
        view.setId(R.id.list_item);
        return view;
    }

    @Override
    public abstract void bindView(View view, Context context, Cursor cursor);

    @Override
    protected int getLayoutId() {
        return -1;
    }

}
