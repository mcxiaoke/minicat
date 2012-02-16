package com.fanfou.app.hd.dao;

import java.util.List;

import android.database.Cursor;

import com.fanfou.app.hd.dao.model.RecordModel;

/**
 * @author mcxiaoke
 * @version 2012.02.16
 *
 */
interface IRecordController {
	
	// insert record
	boolean insertRecord(String account, RecordModel rm);
	int insertRecords(String account, RecordModel[] rms);
	int insertRecords(String account, List<RecordModel> rms);
	
	boolean deleteRecordById(String account, String id);
	int deleteRecordsAll(String account);
	int deleteRecords(String account, String where, String[] whereArgs);
	
	RecordModel getRecordById(String account, String id);
	List<RecordModel> getRecordsByType(String account, int type);
	List<RecordModel> getRecords(String account);
	
	Cursor queryRecordById(String account, String id);
	Cursor queryRecordsByType(String account, int type);
	Cursor queryRecords(String account);

}
