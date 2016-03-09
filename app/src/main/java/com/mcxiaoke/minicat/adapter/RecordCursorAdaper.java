package com.mcxiaoke.minicat.adapter;

import android.content.Context;
import android.database.Cursor;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import com.mcxiaoke.minicat.AppContext;
import com.mcxiaoke.minicat.R;
import com.mcxiaoke.minicat.dao.model.StatusUpdateInfo;
import com.mcxiaoke.minicat.util.StringHelper;

/**
 * @author mcxiaoke
 * @version 2.0 2012.02.22
 */
public class RecordCursorAdaper extends BaseCursorAdapter {
    private static final String TAG = RecordCursorAdaper.class.getSimpleName();

    public RecordCursorAdaper(Context context, Cursor c) {
        super(context, c);
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        View view = mInflater.inflate(getLayoutId(), parent, false);
        ViewHolder holder = new ViewHolder(view);
        view.setTag(holder);
        bindView(view, context, cursor);
        return view;
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        View row = view;
        final StatusUpdateInfo record = StatusUpdateInfo.from(cursor);
        final ViewHolder holder = (ViewHolder) row.getTag();
        holder.text.setText(record.text);
        // holder.date.setText(DateTimeHelper.formatDate(d.createdAt));
        if (AppContext.DEBUG) {
            Log.d(TAG, "bindView filePath=" + record.fileName);
        }
        holder.icon
                .setVisibility(StringHelper.isEmpty(record.fileName) ? View.GONE
                        : View.VISIBLE);
    }

    @Override
    protected int getLayoutId() {
        return R.layout.list_item_draft;
    }

    private static class ViewHolder {
        TextView text = null;
        // TextView date = null;
        ImageView icon = null;

        ViewHolder(View base) {
            this.text = (TextView) base.findViewById(R.id.text);
            // this.date = (TextView) base.findViewById(R.id.date);
            this.icon = (ImageView) base.findViewById(R.id.icon);

        }
    }

}
