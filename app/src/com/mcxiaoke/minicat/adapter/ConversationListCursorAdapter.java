package com.mcxiaoke.minicat.adapter;

import android.content.Context;
import android.database.Cursor;
import android.view.View;
import com.mcxiaoke.minicat.dao.model.DirectMessageModel;
import com.mcxiaoke.minicat.ui.widget.ItemView;
import com.mcxiaoke.minicat.util.DateTimeHelper;

/**
 * @author mcxiaoke
 * @version 2.0 2012.02.28
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
            view.setUserName(dm.getSenderScreenName());
            view.setUserId("@" + dm.getSenderId());
            view.setContent(dm.getText());
        } else {
            view.setUserName(dm.getRecipientScreenName());
            view.setUserId("@" + dm.getRecipientId());
            StringBuilder builder = new StringBuilder();
            builder.append("我：").append(dm.getText());
            view.setContent(builder.toString());
        }
        view.setPhoto(null);
        UIHelper.setImageClick(view, incoming ? dm.getSenderId() : dm.getRecipientId());

        String headUrl = incoming ? dm.getSenderProfileImageUrl() : dm
                .getRecipientProfileImageUrl();
        mImageLoader.displayImage(headUrl, view.getImageView());

    }

}
