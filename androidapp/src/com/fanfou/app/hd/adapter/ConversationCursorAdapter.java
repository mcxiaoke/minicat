package com.fanfou.app.hd.adapter;

import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.view.View;
import android.widget.ImageView;

import com.fanfou.app.hd.R;
import com.fanfou.app.hd.dao.model.DirectMessageModel;
import com.fanfou.app.hd.ui.widget.ItemView;
import com.fanfou.app.hd.util.DateTimeHelper;

/**
 * @author mcxiaoke
 * @version 1.0 2012.02.28
 * 
 */
public class ConversationCursorAdapter extends BaseMessageCursorAdapter {

	public static final String TAG = "ConversationCursorAdapter";

	public ConversationCursorAdapter(Context context) {
		super(context, null);
	}

	public ConversationCursorAdapter(Context context, Cursor c) {
		super(context, c);
	}

	@Override
	public void bindView(View row, Context context, Cursor cursor) {
		ItemView view = (ItemView) row;

		final DirectMessageModel dm = DirectMessageModel.from(cursor);

		if (dm.isIncoming()) {
			row.setBackgroundColor(0x33999999);
		}

		view.setTitle(dm.getSenderScreenName());
		view.setMeta(DateTimeHelper.getInterval(dm.getTime()));
		view.setContent(dm.getText());
		
		String headUrl = dm.getSenderProfileImageUrl();
		UIHelper.setImage(view, mLoader, headUrl, busy);
	}



}
