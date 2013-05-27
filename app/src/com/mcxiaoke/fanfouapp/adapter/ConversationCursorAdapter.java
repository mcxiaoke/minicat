package com.mcxiaoke.fanfouapp.adapter;

import android.content.Context;
import android.database.Cursor;
import android.view.View;
import com.mcxiaoke.fanfouapp.dao.model.DirectMessageModel;
import com.mcxiaoke.fanfouapp.ui.widget.ItemView;
import com.mcxiaoke.fanfouapp.util.DateTimeHelper;


/**
 * @author mcxiaoke
 * @version 1.0 2012.02.28
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
        view.setUserName(dm.getSenderScreenName());
        view.setUserId("@" + dm.getSenderId());
        view.setTime(DateTimeHelper.getInterval(dm.getTime()));
        view.setContent(dm.getText());
        UIHelper.setImageClick(view, dm.getSenderId());
        String headUrl = dm.getSenderProfileImageUrl();
        mImageLoader.displayImage(headUrl, view.getImageView());
    }


}
