package com.fanfou.app;

import android.database.Cursor;

import com.fanfou.app.db.Contents.BasicColumns;
import com.fanfou.app.db.Contents.StatusInfo;
import com.fanfou.app.service.Constants;
import com.fanfou.app.service.FanFouService;

/**
 * @author mcxiaoke
 * @version 1.0 2011.10.21
 * @version 1.1 2011.10.24
 * 
 */
public class UserFavoritesPage extends BaseTimelineActivity {
	private int page = 1;

	@Override
	protected Cursor getCursor() {
		String where = BasicColumns.TYPE + "=? AND " + BasicColumns.OWNER_ID
				+ "=? ";
		String[] whereArgs = new String[] { String.valueOf(getType()), userId };
		return managedQuery(StatusInfo.CONTENT_URI, StatusInfo.COLUMNS, where,
				whereArgs, null);
	}

	@Override
	protected void doRetrieveImpl(final MyResultHandler receiver) {
		if (receiver.doGetMore) {
			page++;
		} else {
			page = 1;
		}
		FanFouService.doFetchFavorites(this, receiver, page, userId);
	}

	@Override
	protected String getPageTitle() {
		return "收藏";
	}

	protected int getType() {
		return Constants.TYPE_FAVORITES_LIST;
	}

}
