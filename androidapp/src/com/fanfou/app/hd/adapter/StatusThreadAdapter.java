package com.fanfou.app.hd.adapter;

import java.util.List;

import android.content.Context;

import com.fanfou.app.hd.R;
import com.fanfou.app.hd.dao.model.StatusModel;

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

	@Override
	int getLayoutId() {
		return R.layout.list_item_status;
	}

	public void changeData(List<StatusModel> data) {
		setData(data);
		notifyDataSetChanged();
	}

}
