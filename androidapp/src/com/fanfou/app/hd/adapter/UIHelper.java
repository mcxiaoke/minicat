package com.fanfou.app.hd.adapter;

import android.text.TextPaint;
import android.view.View;
import com.fanfou.app.hd.dao.model.StatusModel;
import com.fanfou.app.hd.dao.model.UserModel;
import com.fanfou.app.hd.ui.widget.StatusView;
import com.fanfou.app.hd.util.DateTimeHelper;

/**
 * @author mcxiaoke
 * @version 1.0 2012.02.22
 * @version 2.0 2012.03.28
 * 
 */
public class UIHelper {
	public static String getDateString(long date) {
		return DateTimeHelper.getInterval(date);
	}

	public static void setStatusTextSize(final StatusView view, int fontSize) {
		view.setContentTextSize(fontSize);
		view.setTitleTextSize(fontSize + 2);
		view.setMetaTextSize(fontSize - 2);
	}

	public static void setStatusMetaInfo(final StatusView view,
			final StatusModel s) {
		view.showIconThread(s.isThread());
		view.showIconFavorite(s.isFavorited());
		view.showIconPhoto(s.isPhoto());
		view.setTitle(s.getUserScreenName());
		view.setMeta(getDateString(s.getTime()) + " 通过" + s.getSource());
	}

	public static void setUserTextStyle(UserViewHolder holder, int fontSize) {
		holder.genderText.setTextSize(fontSize);
		holder.locationText.setTextSize(fontSize);
		holder.nameText.setTextSize(fontSize);
		holder.dateText.setTextSize(fontSize - 2);
		TextPaint tp = holder.nameText.getPaint();
		tp.setFakeBoldText(true);
	}

	public static void setUserContent(final UserViewHolder holder,
			final UserModel u) {

		if (u.isProtect()) {
			holder.lockIcon.setVisibility(View.VISIBLE);
		} else {
			holder.lockIcon.setVisibility(View.GONE);
		}
		holder.nameText.setText(u.getScreenName());
		holder.idText.setText("(" + u.getId() + ")");
		holder.dateText.setText(DateTimeHelper.formatDateOnly(u.getTime()));
		holder.genderText.setText(u.getGender());
		holder.locationText.setText(u.getLocation());
	}

	public static void showOrHide(View view, boolean show) {
		view.setVisibility(show ? View.VISIBLE : View.GONE);
	}
}
