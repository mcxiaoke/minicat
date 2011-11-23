package com.fanfou.app.adapter;

import android.content.Context;
import android.database.Cursor;
import android.text.TextPaint;
import android.text.util.Linkify;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.fanfou.app.R;
import com.fanfou.app.api.DirectMessage;
import com.fanfou.app.ui.ActionManager;
import com.fanfou.app.util.DateTimeHelper;

/**
 * @author mcxiaoke
 * @version 1.0 2011.06.09
 * @version 1.5 2011.10.24
 * 
 */
public class MessageCursorAdapter extends BaseCursorAdapter {

	public static final String TAG = "MessageCursorAdapter";

	private static final int ITEM_TYPE_ME = 0;
	private static final int ITEM_TYPE_NONE = 1;
	private static final int[] TYPES = new int[] { ITEM_TYPE_ME, ITEM_TYPE_NONE };
	private boolean autoLink;

	private void log(String message) {
		Log.e(TAG, message);
	}

	public MessageCursorAdapter(Context context, Cursor c) {
		super(context, c, false);
		this.autoLink = false;
	}

	public MessageCursorAdapter(Context context, Cursor c, boolean autoRequery,
			boolean autoLink) {
		super(context, c, autoRequery);
		this.autoLink = autoLink;
	}

	@Override
	public int getItemViewType(int position) {
		final Cursor c = (Cursor) getItem(position);
		if (c == null) {
			return ITEM_TYPE_NONE;
		}
		final DirectMessage dm = DirectMessage.parse(c);
		if (dm == null || dm.isNull()) {
			return ITEM_TYPE_NONE;
		}

		if (dm.senderId.equals(dm.threadUserId)) {
			return ITEM_TYPE_NONE;
		} else {
			return ITEM_TYPE_ME;
		}
	}

	@Override
	public int getViewTypeCount() {
		return TYPES.length;
	}

	private void setTextStyle(ViewHolder holder) {
		int fontSize = getFontSize();
		holder.contentText.setTextSize(fontSize);
		holder.nameText.setTextSize(fontSize);
		holder.dateText.setTextSize(fontSize - 4);
		TextPaint tp = holder.nameText.getPaint();
		tp.setFakeBoldText(true);

		if (autoLink) {
			holder.contentText.setAutoLinkMask(Linkify.ALL);
		}
	}

	@Override
	public View newView(Context context, Cursor cursor, ViewGroup parent) {
		View view = mInflater.inflate(getLayoutId(), null);
		ViewHolder holder = new ViewHolder(view);
		setHeadImage(mContext, holder.headIcon);
		setTextStyle(holder);
		view.setTag(holder);
		return view;
	}

	@Override
	public void bindView(View view, Context context, Cursor cursor) {
		View row = view;
		final ViewHolder holder = (ViewHolder) row.getTag();

		final DirectMessage dm = DirectMessage.parse(cursor);

		if (getItemViewType(cursor.getPosition()) == ITEM_TYPE_ME) {
			row.setBackgroundColor(0x33999999);
		}

		if (!isTextMode()) {
//			holder.headIcon.setTag(dm.senderProfileImageUrl);
			mLoader.set(dm.senderProfileImageUrl, holder.headIcon,
					R.drawable.default_head);
		}

		holder.headIcon.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				if (dm != null) {
					ActionManager.doProfile(mContext, dm);
				}
			}
		});

		holder.nameText.setText(dm.senderScreenName);
		holder.dateText.setText(DateTimeHelper.getInterval(dm.createdAt));
		holder.contentText.setText(dm.text);
	}

	@Override
	int getLayoutId() {
		return R.layout.list_item_message;
	}

	static class ViewHolder {
		ImageView headIcon = null;
		TextView nameText = null;
		TextView dateText = null;
		TextView contentText = null;

		ViewHolder(View base) {
			this.headIcon = (ImageView) base
					.findViewById(R.id.item_message_head);
			this.contentText = (TextView) base
					.findViewById(R.id.item_message_text);
			this.dateText = (TextView) base
					.findViewById(R.id.item_message_date);
			this.nameText = (TextView) base
					.findViewById(R.id.item_message_user);

		}
	}

}
