package com.mcxiaoke.fanfouapp.adapter;

import android.content.res.Resources;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import com.mcxiaoke.fanfouapp.R;
import com.mcxiaoke.fanfouapp.dao.model.UserModel;

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
        button.setText(u.isFollowing() ? "正在关注" : "添加关注");
        button.setTextColor(u.isFollowing() ? res.getColorStateList(R.color.text_color_selectable_primary_light_inverse) : res.getColorStateList(R.color.text_color_selectable_primary_light));
        button.setBackgroundResource(u.isFollowing() ? R.drawable.button_follow_on : R.drawable.button_follow_off);
        button.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
            }
        });
    }
}
