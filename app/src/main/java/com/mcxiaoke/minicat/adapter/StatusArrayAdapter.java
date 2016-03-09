package com.mcxiaoke.minicat.adapter;

import android.content.Context;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import com.mcxiaoke.minicat.AppContext;
import com.mcxiaoke.minicat.R;
import com.mcxiaoke.minicat.dao.model.StatusModel;

import java.util.List;

/**
 * @author mcxiaoke
 * @version 3.0 2012.02.22
 */
public class StatusArrayAdapter extends BaseStatusArrayAdapter {

    private static final String TAG = StatusArrayAdapter.class.getSimpleName();

    private static final int NONE = 0;
    private static final int MENTION = 1;
    private static final int SELF = 2;
    private static final int[] TYPES = new int[]{NONE, MENTION, SELF,};

    private boolean colored;

    private int mMentionedBgColor;// = 0x332266aa;

    public StatusArrayAdapter(Context context, List<StatusModel> ss) {
        super(context, ss);
        init(context, false);
    }

    public StatusArrayAdapter(Context context, List<StatusModel> ss, boolean colored) {
        super(context, ss);
        init(context, colored);
    }

    void log(String message) {
        Log.e(TAG, message);
    }

    private void init(Context context, boolean colored) {
        this.colored = colored;
        mMentionedBgColor = mContext.getResources().getColor(R.color.list_item_status_mention_highlight);
    }

    @Override
    public int getItemViewType(int position) {
        final StatusModel s = getItem(position);
        if (s == null) {
            return NONE;
        }
        if (s.getSimpleText().contains("@" + AppContext.getScreenName())) {
            return MENTION;
        } else {
            return s.isSelf() ? SELF : NONE;
        }
    }

    @Override
    public int getViewTypeCount() {
        return TYPES.length;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View view = super.getView(position, convertView, parent);
        setColor(position, convertView);
        return view;
    }

    private void setColor(int position, View convertView) {
        if (!colored) {
            return;
        }

        int itemType = getItemViewType(position);
        switch (itemType) {
            case MENTION:
                convertView.setBackgroundColor(mMentionedBgColor);
                break;
            default:
                break;
        }

    }

    public void changeData(List<StatusModel> data) {
        setData(data);
        notifyDataSetChanged();
    }

}
