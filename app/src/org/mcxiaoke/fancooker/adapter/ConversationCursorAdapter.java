package org.mcxiaoke.fancooker.adapter;

import org.mcxiaoke.fancooker.dao.model.DirectMessageModel;
import org.mcxiaoke.fancooker.ui.widget.ItemView;
import org.mcxiaoke.fancooker.util.DateTimeHelper;

import android.content.Context;
import android.database.Cursor;
import android.view.View;


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
		view.setTitle(dm.getSenderScreenName());
		view.setMeta(DateTimeHelper.getInterval(dm.getTime()));
		view.setContent(dm.getText());
		
		String headUrl = dm.getSenderProfileImageUrl();
		mImageLoader.displayImage(headUrl, view.getImageView());
	}



}
