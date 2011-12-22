package com.fanfou.app.db;

import java.util.ArrayList;
import java.util.List;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.fanfou.app.api.DirectMessage;
import com.fanfou.app.api.Status;
import com.fanfou.app.api.User;
import com.fanfou.app.db.Contents.BasicColumns;
import com.fanfou.app.db.Contents.DirectMessageInfo;
import com.fanfou.app.db.Contents.StatusInfo;
import com.fanfou.app.db.Contents.UserInfo;
import com.fanfou.app.service.Constants;
import com.fanfou.app.util.Utils;

/**
 * @author mcxiaoke
 * @version 1.0 2011.05.01
 * @version 1.1 2011.05.01
 * @version 1.2 2011.05.02
 * @version 2.0 2011.05.25
 * @version 2.1 2011.12.19
 */
public class Database {
	private static final String tag = "Database";

	private Context mContext;
	private static Database instance;
	private SQLiteHelper mSQLiteHelper;

	private Database(Context context) {
		this.mContext = context;
		this.mSQLiteHelper = new SQLiteHelper(mContext);
		instance = this;
	}

	public static synchronized Database getInstance(Context context) {
		if (instance == null) {
			return new Database(context);
		}
		return instance;
	}

	/*************************************** 消息操作 ****************************************************/
	/**
	 * 消息操作 1. 写入单条消息 2. 批量写入消息 3. 删除单条消息 4. 批量删除消息 5. 删除指定类型的消息 6.
	 * 删除100条以前的指定类型消息 7. 删除全部消息 8. 更新单条消息 9. 批量更新消息属性 10. 替换单条消息 11. 批量替换消息 12.
	 * 查询单条消息，返回Status对象 13. 查询批量消息，返回Status列表，返回Cursor 14. 查询指定类型消息，同上 15.
	 * 查询指定用户消息，同上 16. 查询指定时间消息，同上 17. 查询消息数量 18. 查询指定类型消息数量 19. 查询指定用户消息数量
	 * 
	 * */

	/**
	 * 单条消息，写入数据库
	 * 
	 * @param status
	 *            消息对象
	 * @return
	 */
	public long statusWrite(Status status) {
		long result = -1;
		SQLiteDatabase db = mSQLiteHelper.getWritableDatabase();
		try {
			db.beginTransaction();
			result = db.insert(StatusInfo.TABLE_NAME, BasicColumns.ID,
					status.toContentValues());
			db.setTransactionSuccessful();
		} catch (Exception e) {
			e.printStackTrace();
			result = -1;
		} finally {
			db.endTransaction();
			db.close();
		}
		return result;
	}

	/**
	 * 批量消息，写入数据库
	 * 
	 * @param statuses
	 *            消息列表
	 * @return
	 */
	public int statusWrite(List<Status> statuses) {
		if (Utils.isEmpty(statuses)) {
			return 0;
		}
		int result = statuses.size();
		SQLiteDatabase db = mSQLiteHelper.getWritableDatabase();
		try {
			db.beginTransaction();

			for (int i = statuses.size() - 1; i >= 0; i--) {
				Status s = statuses.get(i);
				long id = db.insert(StatusInfo.TABLE_NAME, null,
						s.toContentValues());
				if (id == -1) {
					result--;
				}
			}
			db.setTransactionSuccessful();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			db.endTransaction();
			db.close();
		}
		return result;
	}

	/**
	 * 单条消息，从数据库删除
	 * 
	 * @param id
	 *            消息ID
	 * @return
	 */
	public long statusDeleteById(String id) {
		String where = BasicColumns.ID + "=?";
		String[] whereArgs = new String[] { id };
		return statusDeleteByCondition(where, whereArgs);
	}

	/**
	 * 批量删除某一用户的消息
	 * 
	 * @param userId
	 *            用户ID
	 * @return
	 */
	public long statusDeleteByUserId(String userId) {
		String where = StatusInfo.USER_ID + "=?";
		String[] whereArgs = new String[] { userId };
		return statusDeleteByCondition(where, whereArgs);
	}

	/**
	 * 批量删除某一日期之前的消息
	 * 
	 * @param maxDate
	 *            临界日期
	 * @return
	 */
	public long statusDeleteByDate(int maxDate) {
		String where = StatusInfo.USER_ID + "<?";
		String[] whereArgs = new String[] { String.valueOf(maxDate) };
		return statusDeleteByCondition(where, whereArgs);
	}

	/**
	 * 删除指定类型的旧消息
	 * 
	 * @param type
	 * @return
	 */
	private static final int STATUS_STORE_MAX = 20;

	/**
	 * 压缩数据库，删除旧消息
	 * 
	 * @param context
	 * @param type
	 */
	public static void trimDB(Context context, int type) {
		Database db = Database.getInstance(context);
		int sum = db.statusCountByType(type);
		if (sum > Database.STATUS_STORE_MAX) {
			db.statusDeleteOld(type);
		}
	}

	public boolean statusDeleteOld(int type) {
		// select created_at,id,text from status order by created_at desc limit
		// 1 offset 10;
		//
		// select id from status order by created_at desc limit 5,3;
		// 偏移查询语法为 limit offset,count
		// 例如 limit 5,3 表示偏移量为5，条目数限制为3
		// SELECT MAX（column） 表示查询改列的最大值，相应的还有
		// MIN(column) 最小值 COUNT(*) 条目数量
		// select id,text,created_at from status where created_at <
		// (select max(created_at) from status order by created_at desc limit
		// 10);
		// String sql2 = "DELETE FROM " + StatusInfo.TABLE_NAME + " WHERE "
		// + StatusInfo.CREATED_AT + " < ";
		// String conditionSql = "(SELECT MAX(" + StatusInfo.CREATED_AT
		// + ") FROM " + StatusInfo.TABLE_NAME + " ORDER BY "
		// + StatusInfo.CREATED_AT + " DESC LIMIT " + STATUS_STORE_MAX
		// + ")";
		log("statusDeleteOld()");
		SQLiteDatabase db = mSQLiteHelper.getWritableDatabase();
		try {
			// String countSql = "SELECT id FROM "+ StatusInfo.TABLE_NAME;
			// if (type == Status.TYPE_NONE) {
			// countSql += " ;";
			// } else {
			// countSql += " WHERE " + StatusInfo.TYPE + "=" + type + ";";
			// }
			//
			// log("statusDeleteOld() countSql=" + countSql);
			//
			// Cursor aaaa = db.rawQuery(countSql, null);
			//
			// if (aaaa == null) {
			// log("statusDeleteOld() c0=null");
			// aaaa.close();
			// db.close();
			// return false;
			// }

			// aaaa.moveToFirst();
			// int bbb = aaaa.getInt(aaaa.getColumnIndex("id"));
			// int ccc=aaaa.getCount();

			// log("statusDeleteOld() countResult=" + ccc);
			// if (ccc <= STATUS_STORE_MAX) {
			// aaaa.close();
			// db.close();
			// return false;
			// }
			// aaaa.close();

			String where = " " + BasicColumns.CREATED_AT + " < " + " (SELECT "
					+ BasicColumns.CREATED_AT + " FROM "
					+ StatusInfo.TABLE_NAME;

			// String sql = "DELETE FROM " + StatusInfo.TABLE_NAME + " WHERE ";
			// + StatusInfo.CREATED_AT + " < " + " (SELECT "
			// + StatusInfo.CREATED_AT + " FROM " + StatusInfo.TABLE_NAME;

			// String sql = "SELECT id,created_at,text FROM " +
			// StatusInfo.TABLE_NAME + " WHERE "
			// + StatusInfo.CREATED_AT + " < "
			// + " (SELECT "+ StatusInfo.CREATED_AT
			// + " FROM " + StatusInfo.TABLE_NAME;

			if (type != Constants.TYPE_NONE) {
				where += " WHERE " + BasicColumns.TYPE + " = " + type + " ";
			}
			where += " ORDER BY " + BasicColumns.CREATED_AT
					+ " DESC LIMIT 1 OFFSET " + STATUS_STORE_MAX + ")";

			if (type != Constants.TYPE_NONE) {
				where += " AND " + BasicColumns.TYPE + " = " + type + " ";
			}
			// sql+=where;
			// log("statusDeleteOld() type=" + type);
			log("statusDeleteOld() where=[" + where + "]");
			// log("statusDeleteOld() sql=[" + sql+"]");
			// Cursor c = db.rawQuery(sql, null);
			int rs = db.delete(StatusInfo.TABLE_NAME, where, null);
			log("statusDeleteOld() deleted count=" + rs);
			// if (c != null) {
			// c.moveToFirst();
			// int num = c.getCount();
			// log("statusDeleteOld() status count=" + num);
			// while (!c.isAfterLast()) {
			// Status s = Status.parse(c);
			// log("statusDeleteOld() status=" + s);
			// c.moveToNext();
			// }
			// }
		} finally {
			db.close();
		}

		return true;
	}

	/**
	 * 批量删除指定类型的消息
	 * 
	 * @param type
	 * @return
	 */
	public long statusDeleteByType(int type) {
		String where = BasicColumns.TYPE + "=?";
		String[] whereArgs = new String[] { String.valueOf(type) };
		return statusDeleteByCondition(where, whereArgs);
	}

	/**
	 * 删除所有的Home类型的消息
	 * 
	 * @return
	 */
	public long statusDeleteHome() {
		String where = BasicColumns.TYPE + "=?";
		String[] whereArgs = new String[] { String
				.valueOf(Constants.TYPE_STATUSES_HOME_TIMELINE) };
		return statusDeleteByCondition(where, whereArgs);
	}

	/**
	 * 删除所有的Mention类新的消息
	 * 
	 * @return
	 */
	public long statusDeleteMention() {
		String where = BasicColumns.TYPE + "=?";
		String[] whereArgs = new String[] { String
				.valueOf(Constants.TYPE_STATUSES_MENTIONS) };
		return statusDeleteByCondition(where, whereArgs);
	}

	/**
	 * 删除全部消息
	 * 
	 * @return
	 */
	public long statusDeleteAll() {
		return statusDeleteByCondition(null, null);
	}

	/**
	 * 删除全部消息
	 * 
	 * @return
	 */
	public long statusClear() {
		return statusDeleteAll();
	}

	/**
	 * 根据条件批量删除消息
	 * 
	 * @param where
	 * @param whereArgs
	 * @return
	 */
	private long statusDeleteByCondition(String where, String[] whereArgs) {
		long result = -1;
		SQLiteDatabase db = mSQLiteHelper.getWritableDatabase();
		try {
			result = db.delete(StatusInfo.TABLE_NAME, where, whereArgs);
		} catch (Exception e) {
			e.printStackTrace();
			result = -1;
		} finally {
			db.close();
		}
		return result;
	}

	/**
	 * 根据数值列更新ID对应的消息
	 * 
	 * @param id
	 *            消息ID
	 * @param cv
	 *            需要更新的列
	 * @return
	 */
	public long statusUpdateById(String id, ContentValues values) {
		String where = BasicColumns.ID + "=?";
		String[] whereArgs = new String[] { id };
		return statusUpdate(where, whereArgs, values);
	}

	/**
	 * 更新消息数据
	 * 
	 * @param status
	 * @return
	 */
	public long statusUpdate(Status status) {
		return statusUpdateById(status.id, status.toContentValues());
	}

	/**
	 * 批量更新指定消息
	 * 
	 * @param statuses
	 * @param cv
	 * @return
	 */
	public long statusUpdate(List<Status> statuses, ContentValues cv) {
		for (Status status : statuses) {
			statusUpdateById(status.id, cv);
		}
		return 1;

	}

	/**
	 * 根据条件批量更新消息
	 * 
	 * @param where
	 * @param whereArgs
	 * @param values
	 * @return
	 */
	public long statusUpdate(String where, String[] whereArgs,
			ContentValues values) {
		long result = -1;
		SQLiteDatabase db = mSQLiteHelper.getWritableDatabase();
		try {
			result = db.update(StatusInfo.TABLE_NAME, values, where, whereArgs);
		} catch (Exception e) {
			e.printStackTrace();
			result = -1;
		}
		return result;
	}

	/**
	 * 替换单条消息
	 * 
	 * @param status
	 * @return
	 */
	public long statusReplace(Status status) {
		long result = -1;
		SQLiteDatabase db = mSQLiteHelper.getWritableDatabase();
		try {
			result = db.replace(StatusInfo.TABLE_NAME, null,
					status.toContentValues());
		} catch (Exception e) {
			e.printStackTrace();
			result = -1;
		} finally {
			db.close();
		}
		return result;
	}

	/**
	 * 根据类型查询消息数量
	 * 
	 * @param type
	 * @return
	 */
	public int statusCountByType(int type) {
		int result = -1;
		String sql = "SELECT COUNT(" + BasicColumns.ID + ") FROM "
				+ StatusInfo.TABLE_NAME;
		if (type == Constants.TYPE_NONE) {
			sql += " ;";
		} else {
			sql += " WHERE " + BasicColumns.TYPE + "=" + type + ";";
		}
		SQLiteDatabase db = mSQLiteHelper.getReadableDatabase();
		try {
			// String[] columns=new String[]{StatusInfo.ID};
			// String where=null;
			// String[] whereArgs=null;
			// if(type!=Status.TYPE_NONE){
			// where=StatusInfo.TYPE + "=?";
			// whereArgs=new String[]{String.valueOf(type)};
			// }
			// Cursor c=db.query(StatusInfo.TABLE_NAME, columns, where,
			// whereArgs, null, null, null);
			log("statusCountByType() sql=" + sql);
			Cursor c = db.rawQuery(sql, null);
			if (c != null) {
				c.moveToFirst();
				result = c.getInt(0);
			}
			// 方法二
			// String where=StatusInfo.TYPE+"=?";
			// String[] whereArgs=new String[]{String.valueOf(type)};
			// Cursor c2= db.query(StatusInfo.TABLE_NAME, new
			// String[]{StatusInfo.ID}, where, whereArgs, null, null, null);
			// if(c2!=null){
			// result=c.getCount();
			// }
		} catch (Exception e) {
			e.printStackTrace();
			result = 0;
		} finally {
			db.close();
		}
		log("statusCountByType() type=" + type + " result=" + result);
		return result;
	}

	/**
	 * 查询所有消息数量
	 * 
	 * @return
	 */
	public int statusCount() {
		return statusCountByType(Constants.TYPE_NONE);
	}

	public Cursor query(String table, String[] columns, String selection,
			String[] selectionArgs, String groupBy, String having,
			String orderBy, String limit) {
		SQLiteDatabase db = mSQLiteHelper.getWritableDatabase();
		return db.query(table, columns, selection, selectionArgs, groupBy,
				having, null, limit);
	}

	public long insert(String table, String nullColumnHack, ContentValues values) {
		SQLiteDatabase db = mSQLiteHelper.getWritableDatabase();
		long rowId = db.insert(table, nullColumnHack, values);
		db.close();
		return rowId;
	}

	public int delete(String table, String where, String[] whereArgs) {
		SQLiteDatabase db = mSQLiteHelper.getWritableDatabase();
		int count = db.delete(table, where, whereArgs);
		db.close();
		return count;
	}

	public int update(String table, ContentValues values, String where,
			String[] whereArgs) {
		SQLiteDatabase db = mSQLiteHelper.getWritableDatabase();
		int count = db.update(table, values, where, whereArgs);
		db.close();
		return count;
	}

	private Cursor queryCommon(String table, String[] columns, String where,
			String[] whereArgs, String orderBy) {
		return query(table, columns, where, whereArgs, null, null, orderBy,
				null);
	}

	public Cursor getUserCusor(String where, String[] whereArgs, String orderBy) {
		return queryCommon(UserInfo.TABLE_NAME, UserInfo.COLUMNS, where,
				whereArgs, orderBy);
	}

	public Cursor getStatusCusor(String where, String[] whereArgs,
			String orderBy) {
		return queryCommon(StatusInfo.TABLE_NAME, StatusInfo.COLUMNS, where,
				whereArgs, orderBy);
	}

	public Cursor getDirectMessageCusor(String where, String[] whereArgs,
			String orderBy) {
		return queryCommon(DirectMessageInfo.TABLE_NAME,
				DirectMessageInfo.COLUMNS, where, whereArgs, orderBy);
	}

	public int insertUsers(List<User> users) {
		log("insertUsers() size=" + users.size());
		if (Utils.isEmpty(users)) {
			return 0;
		}

		int result = users.size();
		SQLiteDatabase db = mSQLiteHelper.getWritableDatabase();
		try {
			db.beginTransaction();

			for (int i = users.size() - 1; i >= 0; i--) {
				User u = users.get(i);
				long id = db.insert(UserInfo.TABLE_NAME, null,
						u.toContentValues());
				if (id == -1) {
					log("insertUsers() user.id=" + u.id);
					result--;
				}
			}
			db.setTransactionSuccessful();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			db.endTransaction();
			db.close();
		}
		return result;
	}

	public long insertUser(User user) {
		return insert(UserInfo.TABLE_NAME, null, user.toContentValues());
	}

	public List<User> queryAllUsers() {
		return queryUsers(-1);
	}

	public List<User> queryUsers(int count) {
		return queryUsers(count, 0);
	}

	// select id from status order by created_at desc limit 5,3;
	// 偏移查询语法为 limit offset,count
	// 例如 limit 5,3 表示偏移量为5，条目数限制为3
	public List<User> queryUsers(int count, int offset) {
		log("queryUsers() count=" + count + " offset=" + offset);
		String limit = null;
		if (count > 0) {
			if (offset > 0) {
				limit = String.valueOf(offset) + " ," + String.valueOf(count);
			} else {
				limit = String.valueOf(count);
			}
		}
		List<User> users = new ArrayList<User>();
		SQLiteDatabase db = mSQLiteHelper.getWritableDatabase();
		Cursor c = db.query(UserInfo.TABLE_NAME, UserInfo.COLUMNS, null, null,
				null, null, null, limit);
		c.moveToFirst();
		while (!c.isAfterLast()) {
			User u = User.parse(c);
			log("queryUsers() get user: id=" + u.id);
			users.add(u);
			c.moveToNext();
		}
		db.close();
		log("queryUsers() result count=" + users.size());
		return users;
	}

	public Cursor queryUser(String id) {
		SQLiteDatabase db = mSQLiteHelper.getWritableDatabase();
		String selection = BasicColumns.ID + "=?";
		String[] selectionArgs = new String[] { id };
		return db.query(UserInfo.TABLE_NAME, UserInfo.COLUMNS, selection,
				selectionArgs, null, null, null);
	}

	public User queryUserById(String id) {
		log("queryUserById() id=" + id);
		Cursor c = queryUser(id);
		if (c != null) {
			log("queryUserById() cursor.length=" + c.getCount());
			c.moveToFirst();
			if (c.getCount() > 0) {
				return User.parse(c);
			}
			c.close();
		}
		return null;
	}

	public long updateUser(User user) {
		return updateUser(user.id, user.toContentValues());
	}

	public long updateUser(String userId, ContentValues cv) {
		long result = -1;
		SQLiteDatabase db = mSQLiteHelper.getWritableDatabase();
		try {
			db.beginTransaction();
			String whereClause = BasicColumns.ID + "=?";
			String[] whereArgs = new String[] { userId };
			result = db.update(UserInfo.TABLE_NAME, cv, whereClause, whereArgs);
			db.setTransactionSuccessful();
		} catch (Exception e) {
			e.printStackTrace();
			result = -1;
		} finally {
			db.endTransaction();
			db.close();
		}
		return result;
	}

	public long deleteUser(String id) {

		long result = -1;
		SQLiteDatabase db = mSQLiteHelper.getWritableDatabase();
		try {
			String whereClause = BasicColumns.ID + "=?";
			String[] whereArgs = new String[] { id };
			result = db.delete(UserInfo.TABLE_NAME, whereClause, whereArgs);
		} catch (Exception e) {
			e.printStackTrace();
			result = -1;
		} finally {
			db.close();
		}

		return result;
	}

	public long clearUsers() {
		long result = -1;
		SQLiteDatabase db = mSQLiteHelper.getWritableDatabase();
		result = db.delete(UserInfo.TABLE_NAME, null, null);
		db.close();
		return result;
	}

	public Cursor queryStatus(String id) {
		SQLiteDatabase db = mSQLiteHelper.getWritableDatabase();
		String selection = BasicColumns.ID + "=?";
		String[] selectionArgs = new String[] { id };
		return db.query(StatusInfo.TABLE_NAME, StatusInfo.COLUMNS, selection,
				selectionArgs, null, null, null);
	}

	public Status queryStatusById(String id) {
		log("queryStatusById() id=" + id);
		Cursor c = queryStatus(id);
		if (c != null) {
			log("queryStatusById() cursor.length=" + c.getCount());
			c.moveToFirst();
			if (c.getCount() > 0) {
				return Status.parse(c);
			}
			c.close();
		}
		return null;
	}

	public List<Status> queryAllStatuses() {
		return queryStatuses(-1);
	}

	public List<Status> queryStatuses(int count) {
		return queryStatuses(count, 0);
	}

	public List<Status> queryStatuses(int count, int offset) {
		log("queryStatuses() count=" + count + " offset=" + offset);
		String limit = null;
		// if(count<0||offset<0){
		// throw new IllegalArgumentException("查询数量和偏移量都不能为负");
		// }
		if (count > 0) {
			if (offset > 0) {
				limit = String.valueOf(offset) + " ," + String.valueOf(count);
			} else {
				limit = String.valueOf(count);
			}
		}
		List<Status> statuses = new ArrayList<Status>();
		SQLiteDatabase db = mSQLiteHelper.getWritableDatabase();
		Cursor c = db.query(StatusInfo.TABLE_NAME, StatusInfo.COLUMNS, null,
				null, null, null, null, limit);
		log("queryStatuses() cursor.size=" + c.getCount());
		if (c != null) {
			c.moveToFirst();
			while (!c.isAfterLast()) {
				Status s = Status.parse(c);
				log("queryStatuses() status: id=" + s.id);
				statuses.add(s);
				c.moveToNext();
			}
			c.close();
		}
		db.close();
		return statuses;
	}

	public long clearStatuses() {
		long result = -1;
		SQLiteDatabase db = mSQLiteHelper.getWritableDatabase();
		try {
			result = db.delete(StatusInfo.TABLE_NAME, null, null);
		} catch (Exception e) {
			e.printStackTrace();
			result = -1;
		}
		db.close();

		return result;
	}

	public int insertDirectMessages(List<DirectMessage> messages) {
		if (Utils.isEmpty(messages)) {
			return 0;
		}

		int result = messages.size();
		SQLiteDatabase db = mSQLiteHelper.getWritableDatabase();
		try {
			db.beginTransaction();

			for (int i = messages.size() - 1; i >= 0; i--) {
				DirectMessage dm = messages.get(i);
				long id = db.insert(DirectMessageInfo.TABLE_NAME, null,
						dm.toContentValues());
				if (id == -1) {
					result--;
				}
			}
			db.setTransactionSuccessful();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			db.endTransaction();
			db.close();
		}
		return result;
	}

	public long insertDirectMessage(DirectMessage message) {
		long result = -1;
		SQLiteDatabase db = mSQLiteHelper.getWritableDatabase();

		try {
			db.beginTransaction();
			result = db.insert(DirectMessageInfo.TABLE_NAME, BasicColumns.ID,
					message.toContentValues());
			db.setTransactionSuccessful();
		} catch (Exception e) {
			e.printStackTrace();
			result = -1;
		} finally {
			db.endTransaction();
			db.close();
		}

		return result;
	}

	public List<DirectMessage> queryAllDirectMessages() {
		return queryDirectMessages(-1);
	}

	public List<DirectMessage> queryDirectMessages(int count) {
		return queryDirectMessages(count, 0);
	}

	public List<DirectMessage> queryDirectMessages(int count, int offset) {
		log("queryDirectMessages() count=" + count + " offset=" + offset);
		String limit = null;
		// if(count<0||offset<0){
		// throw new IllegalArgumentException("查询数量和偏移量都不能为负");
		// }
		if (count > 0) {
			if (offset > 0) {
				limit = String.valueOf(offset) + " ," + String.valueOf(count);
			} else {
				limit = String.valueOf(count);
			}
		}
		List<DirectMessage> dms = new ArrayList<DirectMessage>();
		SQLiteDatabase db = mSQLiteHelper.getWritableDatabase();
		Cursor c = db.query(DirectMessageInfo.TABLE_NAME,
				DirectMessageInfo.COLUMNS, null, null, null, null, null, limit);
		if (c != null) {
			log("queryDirectMessages() cursor.size=" + c.getCount());
			c.moveToFirst();
			while (!c.isAfterLast()) {
				DirectMessage dm = DirectMessage.parse(c);
				log("queryDirectMessages() status: id=" + dm.id);
				dms.add(dm);
				c.moveToNext();
			}
		}
		return dms;
	}

	public Cursor queryDirectMessage(String id) {
		SQLiteDatabase db = mSQLiteHelper.getWritableDatabase();
		String selection = BasicColumns.ID + "=?";
		String[] selectionArgs = new String[] { id };
		Cursor c = db.query(DirectMessageInfo.TABLE_NAME,
				DirectMessageInfo.COLUMNS, selection, selectionArgs, null,
				null, null);
		return c;
	}

	public DirectMessage queryDirectMessageById(String id) {
		log("queryDirectMessageById() id=" + id);
		Cursor c = queryDirectMessage(id);
		if (c != null) {
			log("queryDirectMessageById() cursor.length=" + c.getCount());
			c.moveToFirst();
			if (c.getCount() > 0) {
				return DirectMessage.parse(c);
			}
			c.close();
		}
		return null;
	}

	public long updateDirectMessage(DirectMessage message) {
		return updateDirectMessage(message.id, message.toContentValues());
	}

	public long updateDirectMessage(String id, ContentValues cv) {
		long result = -1;
		SQLiteDatabase db = mSQLiteHelper.getWritableDatabase();
		try {
			db.beginTransaction();
			String whereClause = BasicColumns.ID + "=?";
			String[] whereArgs = new String[] { id };
			result = db.update(DirectMessageInfo.TABLE_NAME, cv, whereClause,
					whereArgs);
			db.setTransactionSuccessful();
		} catch (Exception e) {
			e.printStackTrace();
			result = -1;
		} finally {
			db.endTransaction();
			db.close();
		}
		return result;
	}

	public long deleteDirectMessage(String id) {

		long result = -1;
		SQLiteDatabase db = mSQLiteHelper.getWritableDatabase();
		try {
			String whereClause = BasicColumns.ID + "=?";
			String[] whereArgs = new String[] { id };
			result = db.delete(DirectMessageInfo.TABLE_NAME, whereClause,
					whereArgs);
		} catch (Exception e) {
			e.printStackTrace();
			result = -1;
		} finally {
			db.close();
		}

		return result;
	}

	public long clearDirectMessages() {
		long result = -1;
		SQLiteDatabase db = mSQLiteHelper.getWritableDatabase();
		try {
			db.beginTransaction();
			result = db.delete(DirectMessageInfo.TABLE_NAME, null, null);
		} catch (Exception e) {
			e.printStackTrace();
			result = -1;
		} finally {
			db.close();
		}

		return result;
	}

	public String getStatusMaxIdInDB() {
		return null;
	}

	public String getStatusMinIdInDB() {
		return null;
	}

	public String getMentionsMaxIdInDB() {
		return null;
	}

	public String getMentionsMinIdInDB() {
		return null;
	}

	public String getDirectMessagesMaxIdInDB() {
		return null;
	}

	public String getDirectMessagesMinIdInDB() {
		return null;
	}

	private void log(String message) {
		// Log.e(tag, message);
	}

}
