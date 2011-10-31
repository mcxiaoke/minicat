package com.fanfou.app.adapter;

import com.fanfou.app.App;
import com.fanfou.app.R;
import com.fanfou.app.api.Draft;
import com.fanfou.app.util.StringHelper;

import android.content.Context;
import android.database.Cursor;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

/**
 * @author mcxiaoke
 * @version 1.0 2011.10.27
 *
 */
public class DraftsCursorAdaper extends BaseCursorAdapter {
	private static final String TAG=DraftsCursorAdaper.class.getSimpleName();

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
		final Draft d = Draft.parse(cursor);
		final ViewHolder holder = (ViewHolder) row.getTag();
		holder.text.setText(d.text);
//		holder.date.setText(DateTimeHelper.formatDate(d.createdAt));
		if(App.DEBUG){
			Log.d(TAG, "bindView filePath="+d.filePath);
		}
		holder.icon.setVisibility(StringHelper.isEmpty(d.filePath)?View.GONE:View.VISIBLE);
	}

	@Override
	protected void init(Context context, Cursor c) {
		super.init(context, c);
	}

	public DraftsCursorAdaper(Context context, Cursor c) {
		super(context, c);
	}

	@Override
	int getLayoutId() {
		return R.layout.list_item_draft;
	}

	private static class ViewHolder {
		TextView text = null;
//		TextView date = null;
		ImageView icon=null;

		ViewHolder(View base) {
			this.text = (TextView) base.findViewById(R.id.text);
//			this.date = (TextView) base.findViewById(R.id.date);
			this.icon=(ImageView) base.findViewById(R.id.mini_icon);

		}
	}

}
