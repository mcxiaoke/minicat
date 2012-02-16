package com.fanfou.app.hd.dao;

import java.util.List;

import android.database.Cursor;

import com.fanfou.app.hd.dao.model.DirectMessageColumns;

/**
 * @author mcxiaoke
 * @version 2012.02.16
 *
 */
interface IDirectMessagesController {
	// insert dm
	boolean insertDirectMessage(String account, DirectMessageColumns dm);
	int insertDirectMessages(String account, DirectMessageColumns[] dms);
	int insertDirectMessages(String account, List<DirectMessageColumns> dms);
	
	// delete dm
	boolean deleteDirectMessageById(String account, String id);
	int deleteDirectMessagesInbox(String account);
	int deleteDirectMessagesOutbox(String account);
	int deleteDirectMessagesConversation(String account);
	int deleteDirectMessageAll(String account);
	
	int deleteDirectMessages(String account, int type, String orderBy);
	int deleteDirectMessages(String account, String where, String[] whereArgs);
	
	// query and parse dm, close cursor
	DirectMessageColumns getDirectMessageById(String account, String id);
	List<DirectMessageColumns> getDirectMessagesInbox(String account,String orderBy);
	List<DirectMessageColumns> getDirectMessagesOutbox(String account, String orderBy);
	
	// query dm, keep cursor open
	Cursor queryDirectMessagesInbox(String account, String orderBy);
	Cursor queryDirectMessagesOutbox(String account, String orderBy);
	Cursor queryDirectMessagesConversation(String account, String orderBy);
	
	Cursor queryDirectMessage(String account, int type, String userIdstr, String orderBy);
	Cursor queryDirectMessage(String account, String where, String[] whereArgs, String orderBy);

}
