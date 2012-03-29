package com.fanfou.app.hd.adapter;

import android.content.Context;
import android.database.Cursor;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.fanfou.app.hd.App;
import com.fanfou.app.hd.R;
import com.fanfou.app.hd.dao.model.RecordModel;
import com.fanfou.app.hd.util.StringHelper;

/**
 * @author mcxiaoke
 * @version 1.0 2011.10.27
 * @version 2.0 2012.02.22
 * 
 */
public class RecordCursorAdaper extends BaseCursorAdapter {
	private static final String TAG = RecordCursorAdaper.class.getSimpleName();

	@Override
	public View newView(Context context, Cursor cursor, ViewGroup parent) {
		View view = mInflater.inflate(getLayoutId(), null);
		ViewHolder holder = new ViewHolder(view);
		view.setTag(holder);
		bindView(view, context, cursor);
		return view;
	}

	@Override
	public void bindView(View view, Context context, Cursor cursor) {
		View row = view;
		final RecordModel record = RecordModel.from(cursor);
		final ViewHolder holder = (ViewHolder) row.getTag();
		holder.text.setText(record.getText());
		// holder.date.setText(DateTimeHelper.formatDate(d.createdAt));
		if (App.DEBUG) {
			Log.d(TAG, "bindView filePath=" + record.getFile());
		}
		holder.icon
				.setVisibility(StringHelper.isEmpty(record.getFile()) ? View.GONE
						: View.VISIBLE);
	}

	public RecordCursorAdaper(Context context, Cursor c) {
		super(context, c);
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
