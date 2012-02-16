package com.fanfou.app.hd.dao;

import java.util.List;

import android.database.Cursor;

import com.fanfou.app.hd.dao.model.UserModel;

/**
 * @author mcxiaoke
 * @version 1.0 2012.02.16
 *
 */
interface IUserController {
	// insert user
	boolean insertUser(String account, UserModel u);
	int insertUsers(String account, UserModel[] u);
	int insertUsers(String account, List<UserModel> us);
	
	// delete statuses
	boolean deleteUserById(String account, String id);
	boolean deleteUserFriends(String account, String id);
	boolean deleteUserFollowers(String account, String id);
	int deleteUsersAll(String account);
	
	int deleteUsers(String account, int type, String owner, String idstr);
	int deleteUsers(String account, String where, String[] whereArgs);
	
	// query and parse users, close cursor
	UserModel getUserById(String account, String id);
	List<UserModel> getUsers(String account, int type, String owner, String id, String orderBy);
	List<UserModel> getUsers(String account, String where, String[] whereArgs, String orderBy);
	
	// query users, keep cursor open
	Cursor queryUserFriends(String account, String id, String orderBy);
	Cursor queryUserFollowers(String account, String id, String orderBy);
	
	Cursor queryUsers(String account, int type, String owner, String orderBy);
	Cursor queryUsers(String account, String where, String[] whereArgs, String orderBy);
	
}
