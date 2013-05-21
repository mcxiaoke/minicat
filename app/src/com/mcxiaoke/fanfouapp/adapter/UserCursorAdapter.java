package com.mcxiaoke.fanfouapp.adapter;

import android.content.Context;
import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.mcxiaoke.fanfouapp.R;
import com.mcxiaoke.fanfouapp.dao.model.UserModel;

/**
 * @author mcxiaoke
 * @version 2.1 2012.02.27
 */
public class UserCursorAdapter extends BaseCursorAdapter {

    public UserCursorAdapter(Context context, Cursor c) {
        super(context, c);
    }

    @Override
    protected int getLayoutId() {
        return R.layout.list_item_user;
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        View convertView = LayoutInflater.from(context).inflate(getLayoutId(), null);
        UserViewHolder holder = new UserViewHolder(convertView);
        convertView.setTag(holder);
        return convertView;
    }

    @Override
    public void bindView(View row, Context context, Cursor cursor) {
        UserViewHolder holder = (UserViewHolder) row.getTag();
        final UserModel u = UserModel.from(cursor);
        holder.setUserContent(mContext.getResources(), u);
        String headUrl = u.getProfileImageUrl();
        mImageLoader.displayImage(headUrl, holder.head);
    }

}
