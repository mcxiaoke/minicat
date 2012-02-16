package com.fanfou.app.hd.dao;

import java.util.List;

import com.fanfou.app.hd.dao.model.StatusModel;

import android.content.ContentValues;
import android.database.Cursor;

/**
 * @author mcxiaoke
 * @version 1.0 2012.02.16
 *
 */
interface IStatusController {
	// insert statuses
	boolean insertStatus(String account, StatusModel s);
	int insertStatuses(String account, StatusModel[] s);
	int insertStatuses(String account, List<StatusModel> ss);
	
	// delete statuses
	boolean deleteStatusById(String account, String id);
	int deleteStatusesByUser(String account, String userId);
	int deleteStatusesByType(String account, int type);
	int deleteStatusesAll(String account);
	
	int deleteStatuses(String account, int type, String owner, String userIdstr);
	int deleteStatuses(String account, String where, String[] whereArgs);
	
	
	// query and parse status/statuses, close cursor
	StatusModel getStatusById(String account, String id);
	List<StatusModel> getStatuses(String account, int type, String owner, String userIdstr, String orderBy);
	List<StatusModel> getStatuses(String account, String where, String[] whereArgs, String orderBy);
	
	// query statuses, keep cursor open
	Cursor queryHomeTimeline(String account, String orderBy);
	Cursor queryMentionTimeline(String account, String orderBy);
	Cursor queryPublicTimeline(String account, String orderBy);
	Cursor queryUserTimeline(String account, String userIdstr, String orderBy);
	Cursor queryUserFavorites(String account, String userIdstr, String orderBy);
	Cursor queryByType(String account, int type, String orderBy);
	
	Cursor queryStatuses(String account, int type, String owner, String userIdstr, String orderBy);
	Cursor queryStatuses(String account, String where, String[] whereArgs, String orderBy);
	
	// update status/statuses
	boolean updateStatusById(String account, String id, StatusModel s);
	boolean updateStatuses(String account, String where, String[] whereArgs,ContentValues values);

}
