package org.mcxiaoke.fancooker.adapter;

import org.mcxiaoke.fancooker.R;
import org.mcxiaoke.fancooker.dao.model.StatusModel;
import org.mcxiaoke.fancooker.dao.model.UserModel;
import org.mcxiaoke.fancooker.ui.widget.ItemView;
import org.mcxiaoke.fancooker.util.DateTimeHelper;

import android.graphics.Bitmap;
import android.widget.ImageView;

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

	public static void setItemTextSize(final ItemView view, int fontSize) {
		view.setContentTextSize(fontSize);
		view.setTitleTextSize(fontSize + 2);
		view.setMetaTextSize(fontSize - 2);
	}

	public static void setMetaInfo(final ItemView view, final StatusModel s) {
		view.showIconThread(s.isThread());
		view.showIconFavorite(s.isFavorited());
		view.showIconPhoto(s.isPhoto());
		view.setTitle(s.getUserScreenName());

		StringBuilder meta = new StringBuilder();
		meta.append(getDateString(s.getTime()));
		meta.append(" 通过");
		meta.append(s.getSource());
		view.setMeta(meta.toString());
	}

	public static void setContent(final ItemView view, final UserModel u) {
		view.showIconLock(u.isProtect());
		view.setTitle(u.getScreenName());
		StringBuilder content = new StringBuilder();
		content.append(u.getLocation());
		content.append(" ");
		content.append(u.getGender());
		view.setContent(content.toString());
		view.showMeta(false);
	}

}
