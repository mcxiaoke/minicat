package com.fanfou.app.hd.adapter;

import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.util.Log;
import android.view.View;

import com.fanfou.app.hd.App;
import com.fanfou.app.hd.R;
import com.fanfou.app.hd.dao.model.DirectMessageModel;
import com.fanfou.app.hd.util.DateTimeHelper;

/**
 * @author mcxiaoke
 * @version 1.0 2011.06.09
 * @version 1.5 2011.10.24
 * @version 1.6 2012.02.22
 * @version 1.7 2012.02.27
 * @version 2.0 2012.02.28
 * 
 */
public class ConversationListCursorAdapter extends BaseMessageCursorAdapter {
	private static final String TAG = ConversationListCursorAdapter.class
			.getSimpleName();

	public ConversationListCursorAdapter(Context context, Cursor c) {
		super(context, c);
	}

	public ConversationListCursorAdapter(Context context, Cursor c,
			boolean autoRequery, boolean autoLink) {
		super(context, c, autoRequery);
	}

	@Override
	public void bindView(View view, Context context, Cursor cursor) {
		View row = view;
		final ViewHolder holder = (ViewHolder) row.getTag();

		final DirectMessageModel dm = DirectMessageModel.from(cursor);

		if (App.DEBUG) {
//			Log.d(TAG, "bindView " + dm);
		}

		holder.dateText.setText(DateTimeHelper.getInterval(dm.getTime()));

		boolean incoming = dm.isIncoming();

		if (incoming) {
			holder.nameText.setText(dm.getSenderScreenName());
			holder.contentText.setText(dm.getText());
		} else {
			holder.nameText.setText(dm.getRecipientScreenName());
			StringBuilder builder = new StringBuilder();
			builder.append("我：").append(dm.getText());
			holder.contentText.setText(builder.toString());
		}

		String headUrl = incoming ? dm.getSenderProfileImageUrl() : dm
				.getRecipientProfileImageUrl();

		if (busy) {
			Bitmap bitmap = mLoader.getImage(headUrl, null);
			if (bitmap != null) {
				holder.headIcon.setImageBitmap(bitmap);
			} else {
				holder.headIcon.setImageResource(R.drawable.default_head);
			}
		} else {
			holder.headIcon.setTag(headUrl);
			mLoader.displayImage(headUrl, holder.headIcon,
					R.drawable.default_head);
		}

	}

}
