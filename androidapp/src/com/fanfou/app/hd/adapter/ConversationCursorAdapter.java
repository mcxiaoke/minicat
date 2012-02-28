package com.fanfou.app.hd.adapter;

import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.view.View;

import com.fanfou.app.hd.R;
import com.fanfou.app.hd.dao.model.DirectMessageModel;
import com.fanfou.app.hd.util.DateTimeHelper;

/**
 * @author mcxiaoke
 * @version 1.0 2012.02.28
 * 
 */
public class ConversationCursorAdapter extends BaseMessageCursorAdapter {

	public static final String TAG = "ConversationCursorAdapter";

	public ConversationCursorAdapter(Context context, Cursor c) {
		super(context, c, false);
	}

	public ConversationCursorAdapter(Context context, Cursor c,
			boolean autoRequery) {
		super(context, c, autoRequery);
	}

	@Override
	public void bindView(View view, Context context, Cursor cursor) {
		View row = view;
		final ViewHolder holder = (ViewHolder) row.getTag();

		final DirectMessageModel dm = DirectMessageModel.from(cursor);

		if (dm.isIncoming()) {
			row.setBackgroundColor(0x33999999);
		}

		String headUrl = dm.getSenderProfileImageUrl();
		if (busy) {
			Bitmap bitmap = mLoader.getImage(headUrl, null);
			if (bitmap != null) {
				holder.headIcon.setImageBitmap(bitmap);
			}
		} else {
			holder.headIcon.setTag(headUrl);
			mLoader.displayImage(headUrl, holder.headIcon,
					R.drawable.default_head);
		}

		holder.nameText.setText(dm.getSenderScreenName());
		holder.dateText.setText(DateTimeHelper.getInterval(dm.getTime()));
		holder.contentText.setText(dm.getText());
	}

}
