package com.mcxiaoke.fanfouapp.adapter;

import android.app.Activity;
import android.widget.ImageView;
import android.widget.TextView;
import com.mcxiaoke.fanfouapp.controller.UIController;
import com.mcxiaoke.fanfouapp.dao.model.StatusModel;
import com.mcxiaoke.fanfouapp.ui.widget.ItemView;
import com.mcxiaoke.fanfouapp.util.DateTimeHelper;

/**
 * @author mcxiaoke
 * @version 2.0 2012.03.28
 */
public class UIHelper {
    public static String getDateString(long date) {
        return DateTimeHelper.getInterval(date);
    }

    public static void setItemTextSize(final ItemView view, int fontSize) {
        view.setContentTextSize(fontSize);
        view.setTitleTextSize(fontSize + 2);
        view.setMetaTextSize(fontSize - 2, fontSize - 2);
    }

    public static void setImageClick(final ItemView view, final String userId) {
        view.setOnImageClickListener(new ItemView.OnImageClickListener() {
            @Override
            public void onImageClick(ImageView view) {
                UIController.showProfile((Activity) view.getContext(), userId);
            }
        });
    }

    public static void setContent(final ItemView view, final StatusModel s) {
        TextView textView = view.getContentTextView();
        String text = s.getSimpleText();
        textView.setText(text, TextView.BufferType.SPANNABLE);
    }

    public static void setMetaInfo(final ItemView view, final StatusModel s) {
        view.showIconThread(s.isThread());
        view.showIconFavorite(s.isFavorited());
        view.showIconPhoto(s.isPhoto());
        view.showIconRetweet(s.isRetweeted());
        boolean lock = s.getUser() != null && s.getUser().isProtect();
        view.showIconLock(lock);
        view.setUserName(s.getUserScreenName());
        view.setUserId("@" + s.getUserId());
        view.setTime(getDateString(s.getTime()));

        StringBuilder meta = new StringBuilder();
        meta.append(" 通过");
        String source = s.getSource();
        if (source.length() > 16) {
            source = source.substring(0, 15);
        }
        meta.append(source);
        view.setMeta(meta.toString());
    }

}
