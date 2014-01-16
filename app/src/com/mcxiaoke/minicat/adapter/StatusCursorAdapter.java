package com.mcxiaoke.minicat.adapter;

import android.content.Context;
import android.database.Cursor;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import com.mcxiaoke.minicat.R;
import com.mcxiaoke.minicat.AppContext;
import com.mcxiaoke.minicat.dao.model.StatusModel;
import com.mcxiaoke.minicat.ui.widget.ItemView;

/**
 * @author mcxiaoke
 * @version 2.1 2012.02.27
 */
public class StatusCursorAdapter extends BaseCursorAdapter {
    private static final int NONE = 0;
    private static final int MENTION = 1;
    private static final int SELF = 2;
    private static final int[] TYPES = new int[]{NONE, MENTION, SELF,};

    private int mMentionedBgColor;// = 0x332266aa;
    private int mSelfBgColor;// = 0x33999999;
    private boolean colored;

    public static final String TAG = StatusCursorAdapter.class.getSimpleName();

    private void log(String message) {
        Log.d(TAG, message);

    }

    public StatusCursorAdapter(Context context, boolean colored) {
        super(context, null);
        initialize(context, colored);
    }

    public void initialize(Context context, boolean colored) {
        this.colored = colored;
        mMentionedBgColor = mContext.getResources().getColor(R.color.list_item_status_mention_highlight);
        mSelfBgColor = mContext.getResources().getColor(R.color.list_item_status_self_highlight);
    }

    public void setColored(boolean colored) {
        this.colored = colored;
    }

    @Override
    public int getItemViewType(int position) {
        final Cursor cursor = (Cursor) getItem(position);
        if (cursor == null) {
            return NONE;
        }
        final StatusModel s = StatusModel.from(cursor);
        if (s == null) {
            return NONE;
        }
        if (s.getType() == StatusModel.TYPE_MENTIONS
                || s.getSimpleText().contains("@" + AppContext.getScreenName())) {
            return MENTION;
        }

        return s.isSelf() ? SELF : NONE;
    }

    @Override
    public int getViewTypeCount() {
        return TYPES.length;
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        ItemView view = new ItemView(mContext);
        view.setId(R.id.list_item);
        return view;
    }

    @Override
    public void bindView(View row, Context context, final Cursor cursor) {
        final StatusModel s = StatusModel.from(cursor);
        View root = row;
        final ItemView view = (ItemView) root.findViewById(R.id.list_item);

        setColor(cursor, view);
        UIHelper.setContent(view, s);
        UIHelper.setMetaInfo(view, s);
        UIHelper.setImageClick(view, s.getUserId());

        String headUrl = s.getUserProfileImageUrl();
        mImageLoader.displayImage(headUrl, view.getImageView());
    }

    private void setColor(final Cursor cursor, View row) {
        if (!colored) {
            return;
        }
        int itemType = getItemViewType(cursor.getPosition());
        switch (itemType) {
            case MENTION:
                row.setBackgroundColor(mMentionedBgColor);
                break;
            case SELF:
                row.setBackgroundColor(mSelfBgColor);
                break;
            case NONE:
                break;
            default:
                break;
        }
    }

}
