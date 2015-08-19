package com.mcxiaoke.minicat.dao.model;

import android.provider.BaseColumns;
import com.mcxiaoke.minicat.BuildConfig;
import com.mcxiaoke.minicat.service.Constants;


/**
 * @author mcxiaoke
 * @version 1.0 2012.02.16
 */
public interface IBaseColumns extends BaseColumns {
    public static final int TYPE_NONE = 0;
    public static final String AUTHORITY = BuildConfig.APPLICATION_ID + ".provider";

    public static final String RAWID = "rawid";// rawid in number format
    public static final String ACCOUNT = "account"; // related account id/userid
    public static final String OWNER = "owner"; // owner id of the item
    public static final String NOTE = "note"; // state of the item, reserved

    public static final String TYPE = "type"; // type of the item
    public static final String FLAG = "flag"; // flag of the item, reserved

    public static final String ID = "id"; // id in string format
    public static final String TIME = "time"; // created at of the item

}
