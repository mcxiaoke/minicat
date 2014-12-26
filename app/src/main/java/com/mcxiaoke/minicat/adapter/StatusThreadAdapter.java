package com.mcxiaoke.minicat.adapter;

import android.content.Context;
import com.mcxiaoke.minicat.dao.model.StatusModel;

import java.util.List;


/**
 * @author mcxiaoke
 * @version 2.0 2012.02.22
 */
public class StatusThreadAdapter extends BaseStatusArrayAdapter {

    public StatusThreadAdapter(Context context, List<StatusModel> data) {
        super(context, data);
    }

}
