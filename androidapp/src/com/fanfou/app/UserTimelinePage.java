package com.fanfou.app;

import com.fanfou.app.api.Status;
import com.fanfou.app.config.Commons;
import com.fanfou.app.db.Contents.BasicColumns;
import com.fanfou.app.db.Contents.StatusInfo;
import com.fanfou.app.util.Utils;

import android.database.Cursor;
import android.os.Bundle;

/**
 * @author mcxiaoke
 * @version 1.0 2011.10.21
 *
 */
public class UserTimelinePage extends BaseTimelineActivity {

	@Override
	protected Cursor getCursor() {
		String where = BasicColumns.TYPE + "=? AND " + StatusInfo.USER_ID + "=? ";
		String[] whereArgs = new String[] { String.valueOf(getType()),
				userId };
		return managedQuery(StatusInfo.CONTENT_URI, StatusInfo.COLUMNS, where,
				whereArgs, null);
	}

	@Override
	protected void doRetrieveImpl(Bundle b, MyResultHandler receiver) {
		if (receiver.doGetMore) {
			String maxId = Utils.getMaxId(mCursor);
			b.putString(Commons.EXTRA_MAX_ID, maxId);
		} else {
			String sinceId = Utils.getSinceId(mCursor);
			b.putString(Commons.EXTRA_SINCE_ID, sinceId);
		}
		Utils.startFetchService(this, getType(), receiver, b);
	}

	@Override
	protected String getPageTitle() {
		return "消息";
	}
	
	protected int getType(){
		return Status.TYPE_USER;
	}

}
