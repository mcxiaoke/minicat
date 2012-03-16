package com.fanfou.app.hd.adapter;

import android.text.TextPaint;
import android.util.Log;
import android.view.View;

import com.fanfou.app.hd.App;
import com.fanfou.app.hd.dao.model.StatusModel;
import com.fanfou.app.hd.dao.model.UserModel;
import com.fanfou.app.hd.util.DateTimeHelper;

/**
 * @author mcxiaoke
 * @version 1.0 2012.02.22
 * 
 */
public class UIHelper {
	public static String getDateString(long date) {
		return DateTimeHelper.getInterval(date);
	}

	public static void setStatusTextStyle(StatusViewHolder holder, int fontSize) {
		holder.contentText.setTextSize(fontSize);
		holder.nameText.setTextSize(fontSize);
		holder.metaText.setTextSize(fontSize - 4);
		TextPaint tp = holder.nameText.getPaint();
		tp.setFakeBoldText(true);
	}

	public static void setUserTextStyle(UserViewHolder holder, int fontSize) {
		holder.genderText.setTextSize(fontSize);
		holder.locationText.setTextSize(fontSize);
		holder.nameText.setTextSize(fontSize);
		holder.dateText.setTextSize(fontSize - 2);
		TextPaint tp = holder.nameText.getPaint();
		tp.setFakeBoldText(true);
	}

	public static void setStatusMetaInfo(StatusViewHolder holder,
			final StatusModel s) {
		holder.replyIcon.setVisibility(s.isThread() ? View.VISIBLE : View.GONE);
		holder.retweetIcon.setVisibility(s.isRetweeted() ? View.VISIBLE
				: View.GONE);
		holder.favoriteIcon.setVisibility(s.isFavorited() ? View.VISIBLE
				: View.GONE);
		holder.photoIcon.setVisibility(s.isPhoto() ? View.VISIBLE : View.GONE);

		holder.nameText.setText(s.getUserScreenName());
		holder.metaText.setText(getDateString(s.getTime()) + " 通过"
				+ s.getSource());
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
		if (show) {
			view.setVisibility(View.GONE);
		} else {
			view.setVisibility(View.VISIBLE);
		}
	}
}
