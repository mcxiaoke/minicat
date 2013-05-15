package com.mcxiaoke.fanfouapp.adapter;

import android.content.Context;
import android.database.Cursor;
import android.view.View;
import com.mcxiaoke.fanfouapp.dao.model.DirectMessageModel;
import com.mcxiaoke.fanfouapp.ui.widget.ItemView;
import com.mcxiaoke.fanfouapp.util.DateTimeHelper;

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

	@Override
	public void bindView(View row, Context context, Cursor cursor) {
		ItemView view = (ItemView) row;

		final DirectMessageModel dm = DirectMessageModel.from(cursor);

		view.setMeta(DateTimeHelper.getInterval(dm.getTime()));

		boolean incoming = dm.isIncoming();

		if (incoming) {
			view.setTitle(dm.getSenderScreenName());
			view.setContent(dm.getText());
		} else {
			view.setTitle(dm.getRecipientScreenName());
			StringBuilder builder = new StringBuilder();
			builder.append("我：").append(dm.getText());
			view.setContent(builder.toString());
		}

		String headUrl = incoming ? dm.getSenderProfileImageUrl() : dm
				.getRecipientProfileImageUrl();
		mImageLoader.displayImage(headUrl, view.getImageView());

	}

}
