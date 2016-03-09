package com.mcxiaoke.minicat.adapter;

import android.content.Context;
import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.mcxiaoke.minicat.R;
import com.mcxiaoke.minicat.dao.model.UserModel;

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
        View convertView = LayoutInflater.from(context).inflate(getLayoutId(), parent, false);
        UserViewHolder holder = new UserViewHolder(convertView);
        convertView.setTag(holder);
        return convertView;
    }

    @Override
    public void bindView(View row, Context context, Cursor cursor) {
        UserViewHolder holder = (UserViewHolder) row.getTag();
        final UserModel u = UserModel.from(cursor);
        holder.setUserContent(mContext.getResources(), u);
        String headUrl = u.getProfileImageUrlLarge();
        mImageLoader.displayImage(headUrl, holder.head);
    }

}
