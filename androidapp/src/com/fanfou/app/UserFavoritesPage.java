package com.fanfou.app;

import com.fanfou.app.api.Status;
import com.fanfou.app.config.Commons;
import com.fanfou.app.db.Contents.StatusInfo;
import com.fanfou.app.util.Utils;

import android.database.Cursor;
import android.os.Bundle;
import android.os.ResultReceiver;

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
		String where = StatusInfo.TYPE + "=? AND " + StatusInfo.OWNER_ID
				+ "=? ";
		String[] whereArgs = new String[] { String.valueOf(getType()),
				userId };
		return managedQuery(StatusInfo.CONTENT_URI, StatusInfo.COLUMNS, where,
				whereArgs, null);
	}

	@Override
	protected void doRetrieveImpl(Bundle b,MyResultHandler receiver) {
		if(receiver.doGetMore){
			b.putInt(Commons.EXTRA_PAGE, page++);
		}
		Utils.startFetchService(this, getType(), receiver, b);
	}

	@Override
	protected String getPageTitle() {
		return "收藏";
	}
	
	protected int getType(){
		return Status.TYPE_FAVORITES;
	}

}
