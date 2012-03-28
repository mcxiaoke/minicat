package com.fanfou.app.hd.adapter;

import android.content.Context;
import android.database.Cursor;
import android.text.TextPaint;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.fanfou.app.hd.R;

/**
 * @author mcxiaoke
 * @version 1.0 2011.06.09
 * @version 1.5 2011.10.24
 * @version 1.6 2012.02.22
 * @version 1.7 2012.02.27
 * @version 2.0 2012.02.28
 * 
 */
public abstract class BaseMessageCursorAdapter extends BaseCursorAdapter {

	public BaseMessageCursorAdapter(Context context) {
		super(context, null);
	}

	public BaseMessageCursorAdapter(Context context, Cursor c) {
		super(context, c);
	}

	private static final String TAG = BaseMessageCursorAdapter.class
			.getSimpleName();

	private void setTextStyle(ViewHolder holder) {
		int fontSize = getFontSize();
		holder.contentText.setTextSize(fontSize);
		holder.nameText.setTextSize(fontSize);
		holder.dateText.setTextSize(fontSize - 4);
		TextPaint tp = holder.nameText.getPaint();
		tp.setFakeBoldText(true);
	}

	@Override
	public View newView(Context context, Cursor cursor, ViewGroup parent) {
		View view = mInflater.inflate(getLayoutId(), null);
		ViewHolder holder = new ViewHolder(view);
		setTextStyle(holder);
		view.setTag(holder);
		return view;
	}

	@Override
	public abstract void bindView(View view, Context context, Cursor cursor);

	@Override
	protected int getLayoutId() {
		return R.layout.list_item_message;
	}

	protected static class ViewHolder {
		ImageView headIcon = null;
		TextView nameText = null;
		TextView dateText = null;
		TextView contentText = null;

		ViewHolder(View base) {
			this.headIcon = (ImageView) base.findViewById(R.id.head);
			this.contentText = (TextView) base.findViewById(R.id.text);
			this.dateText = (TextView) base.findViewById(R.id.date);
			this.nameText = (TextView) base.findViewById(R.id.name);

		}
	}

}
