package com.fanfou.app.hd.dao.model;

import com.fanfou.app.hd.service.Constants;

import android.provider.BaseColumns;

interface IBaseColumns extends BaseColumns{
	public static final String AUTHORITY = Constants.PACKAGE_NAME+".provider";
	
	public static final String IDSTR = "idstr";// id in string format
	public static final String ACCOUNT="account"; // related account id/userid
	public static final String OWNER = "owner"; // owner id of the item
	public static final String NOTE="note"; // state of the item, reserved
	
	public static final String TYPE = "type"; // type of the item
	public static final String FLAG="flag"; // flag of the item, reserved
	
	public static final String ID = "id"; // raw id in number format
	public static final String TIME = "time"; // created at of the item
	
}
