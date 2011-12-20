package com.fanfou.app;

import android.database.Cursor;

import com.fanfou.app.db.Contents.BasicColumns;
import com.fanfou.app.db.Contents.StatusInfo;
import com.fanfou.app.db.FanFouProvider;
import com.fanfou.app.service.Constants;
import com.fanfou.app.service.FanFouService;
import com.fanfou.app.util.Utils;

/**
 * @author mcxiaoke
 * @version 1.0 2011.10.21
 * 
 */
public class UserTimelinePage extends BaseTimelineActivity {

	@Override
	protected Cursor getCursor() {
		String where = BasicColumns.TYPE + "=? AND " + StatusInfo.USER_ID
				+ "=? ";
		String[] whereArgs = new String[] { String.valueOf(getType()), userId };
		return managedQuery(StatusInfo.CONTENT_URI, StatusInfo.COLUMNS, where,
				whereArgs, FanFouProvider.ORDERBY_DATE_DESC);
	}

	@Override
	protected void doRetrieveImpl(final MyResultHandler receiver) {
		String sinceId = null;
		String maxId = null;
		if (receiver.doGetMore) {
			maxId = Utils.getMaxId(mCursor);
		} else {
			sinceId = Utils.getSinceId(mCursor);
		}
		FanFouService.doFetchUserTimeline(this, receiver, userId, sinceId,
				maxId);
	}

	@Override
	protected String getPageTitle() {
		return "消息";
	}

	protected int getType() {
		return Constants.TYPE_STATUSES_USER_TIMELINE;
	}

}
