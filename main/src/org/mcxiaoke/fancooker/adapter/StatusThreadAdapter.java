package org.mcxiaoke.fancooker.adapter;

import java.util.List;

import org.mcxiaoke.fancooker.dao.model.StatusModel;

import android.content.Context;


/**
 * @author mcxiaoke
 * @version 1.0 2011.06.25
 * @version 1.1 2011.10.26
 * @version 2.0 2012.02.22
 * 
 */
public class StatusThreadAdapter extends BaseStatusArrayAdapter {

	public StatusThreadAdapter(Context context, List<StatusModel> data) {
		super(context, data);
	}

}
