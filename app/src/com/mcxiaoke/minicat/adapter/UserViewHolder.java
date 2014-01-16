package com.mcxiaoke.minicat.adapter;

import android.content.res.Resources;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import com.mcxiaoke.minicat.R;
import com.mcxiaoke.minicat.dao.model.UserModel;

/**
 * Project: fanfouapp
 * Package: com.mcxiaoke.fanfouapp.adapter
 * User: mcxiaoke
 * Date: 13-5-21
 * Time: 下午11:48
 */
public class UserViewHolder {
    public ImageView head;
    public TextView title;
    public ImageView lock;
    public TextView text;
    public TextView button;

    public UserViewHolder(View base) {
        head = (ImageView) base.findViewById(R.id.image);
        title = (TextView) base.findViewById(R.id.title);
        lock = (ImageView) base.findViewById(R.id.lock);
        text = (TextView) base.findViewById(R.id.text);
        button = (TextView) base.findViewById(R.id.button);
    }

    public void setUserContent(Resources res, final UserModel u) {
        lock.setVisibility(u.isProtect() ? View.VISIBLE : View.GONE);
        title.setText(u.getScreenName());
        text.setText(u.getDescription());
        button.setClickable(false);
        button.setText(u.isFollowing() ? "正在关注" : "没有关注");
        button.setTextColor(u.isFollowing() ? res.getColor(R.color.solid_white) : res.getColor(R.color.text_primary));
        button.setBackgroundResource(u.isFollowing() ? R.drawable.state_on : R.drawable.state_off);
    }
}
