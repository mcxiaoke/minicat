package com.fanfou.app.adapter;

import java.util.Date;
import java.util.List;

import android.content.Context;

import com.fanfou.app.api.Status;
import com.fanfou.app.util.DateTimeHelper;

/**
 * @author mcxiaoke
 * @version 1.0 2011.10.25
 * @version 1.1 2011.10.26
 *
 */
public class ConversationAdapter extends StatusArrayAdapter {

	public ConversationAdapter(Context context, List<Status> ss) {
		super(context, ss);
	}
	
	@Override
	protected String getDateString(Date date) {
		return DateTimeHelper.formatDate(date);
	}

}
