package com.fanfou.app.service;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.ResultReceiver;
import android.util.Log;

import com.fanfou.app.App;
import com.fanfou.app.api.Api;
import com.fanfou.app.api.ApiException;
import com.fanfou.app.api.DirectMessage;
import com.fanfou.app.api.Status;
import com.fanfou.app.api.User;
import com.fanfou.app.config.Commons;
import com.fanfou.app.db.Contents.BasicColumns;
import com.fanfou.app.db.Contents.DirectMessageInfo;
import com.fanfou.app.db.Contents.StatusInfo;
import com.fanfou.app.db.Contents.UserInfo;
import com.fanfou.app.http.ResponseCode;
import com.fanfou.app.util.StringHelper;

/**
 * @author mcxiaoke
 * @version 1.0 2011.06.15
 * @version 3.0 2011.10.28
 *
 */
public class ActionService extends BaseIntentService {
	private static final String TAG = ActionService.class.getSimpleName();
	ResultReceiver receiver;

	public ActionService() {
		super("ActionService");
	}

	@Override
	protected void onHandleIntent(Intent intent) {
		int type = intent.getIntExtra(Commons.EXTRA_TYPE, -1);
		receiver = intent.getParcelableExtra(Commons.EXTRA_RECEIVER);
		if (receiver != null) {
			receiver.send(Commons.RESULT_CODE_START, null);
		}
		String userA = intent.getStringExtra("user_a");
		String userB = intent.getStringExtra("user_b");
		if (!StringHelper.isEmpty(userA) && !StringHelper.isEmpty(userB)) {
			doDetectFriendships(userA, userB);
		} else {
			String id = intent.getStringExtra(Commons.EXTRA_ID);
			if (!StringHelper.isEmpty(id)) {
				performAction(id, type);
			} else {
				Bundle error = new Bundle();
				error.putInt(Commons.EXTRA_TYPE, type);
				error.putString(Commons.EXTRA_ERROR_MESSAGE, "null action id.");
				receiver.send(Commons.RESULT_CODE_ERROR, error);
			}
		}
	}

	private void doDetectFriendships(String userA, String userB) {
		Api api = App.me.api;
		try {
			boolean result = api.isFriends(userA, userB);
			Bundle data = new Bundle();
			data.putInt(Commons.EXTRA_TYPE, Commons.ACTION_USER_RELATION);
			data.putBoolean(Commons.EXTRA_BOOLEAN, result);
			receiver.send(Commons.RESULT_CODE_FINISH, data);
		} catch (ApiException e) {
			if (App.DEBUG) {
				Log.e(TAG, "doDetectFriendships:" + e.getMessage());
			}
			Bundle error = new Bundle();
			error.putInt(Commons.EXTRA_TYPE, Commons.ACTION_USER_RELATION);
			error.putSerializable(Commons.EXTRA_ERROR, e);
			error.putInt(Commons.EXTRA_ERROR_CODE, e.statusCode);
			error.putString(Commons.EXTRA_ERROR_MESSAGE, e.getMessage());
			receiver.send(Commons.RESULT_CODE_ERROR, error);
		}

	}

	private void performAction(String id, int type) {
		Api api = App.me.api;
		String where = BasicColumns.ID + "=?";
		String[] whereArgs = new String[] { id };
		try {
			switch (type) {
			case Commons.ACTION_STATUS_SHOW: {
				// 404 消息不存在
				Status s = api.statusShow(id);
				if (s == null || s.isNull()) {
					receiver.send(Commons.RESULT_CODE_FINISH, null);
				} else {
					Bundle data = new Bundle();
					data.putInt(Commons.EXTRA_TYPE, type);
					data.putSerializable(Commons.EXTRA_STATUS, s);
					receiver.send(Commons.RESULT_CODE_FINISH, data);
				}
			}
				break;
			case Commons.ACTION_STATUS_DELETE: {
				// 删除消息
				// 404 说明消息不存在
				// 403 说明不是你的消息，无权限删除
				Status s = api.statusDelete(id);
				if (s == null || s.isNull()) {
					receiver.send(Commons.RESULT_CODE_FINISH, null);
				} else {
					ContentResolver cr = getContentResolver();
					Uri uri=Uri.parse(StatusInfo.CONTENT_URI+"/id/"+id);
					int result = cr.delete(uri, null,
							null);
					Bundle data = new Bundle();
					data.putInt(Commons.EXTRA_TYPE, type);
					data.putSerializable(Commons.EXTRA_STATUS, s);
					receiver.send(Commons.RESULT_CODE_FINISH, data);
				}
			}
				break;
			case Commons.ACTION_STATUS_FAVORITE: {
				// 404 消息不存在
				// 404 没有通过用户验证
				Status s = api.statusFavorite(id);
				if (s == null || s.isNull()) {
					receiver.send(Commons.RESULT_CODE_FINISH, null);
				} else {
					ContentResolver cr = getContentResolver();
					ContentValues values = new ContentValues();
					values.put(StatusInfo.FAVORITED, true);
					int result = cr.update(StatusInfo.CONTENT_URI, values,
							where, whereArgs);
					Bundle data = new Bundle();
					data.putInt(Commons.EXTRA_TYPE, type);
					data.putSerializable(Commons.EXTRA_STATUS, s);
					receiver.send(Commons.RESULT_CODE_FINISH, data);
				}
			}
				break;
			case Commons.ACTION_STATUS_UNFAVORITE: {
				// 404 没有这条消息
				// 404 收藏不存在
				Status s = api.statusUnfavorite(id);
				if (s == null || s.isNull()) {
					receiver.send(Commons.RESULT_CODE_FINISH, null);
				} else {
					ContentResolver cr = getContentResolver();
					ContentValues values = new ContentValues();
					values.put(StatusInfo.FAVORITED, false);
					int result = cr.update(StatusInfo.CONTENT_URI, values,
							where, whereArgs);
					Bundle data = new Bundle();
					data.putInt(Commons.EXTRA_TYPE, type);
					data.putSerializable(Commons.EXTRA_STATUS, s);
					receiver.send(Commons.RESULT_CODE_FINISH, data);
				}
			}
				break;
			case Commons.ACTION_USER_SHOW: {
				User u = api.userShow(id);
				if (u == null || u.isNull()) {
					receiver.send(Commons.RESULT_CODE_FINISH, null);
				} else {
					ContentResolver cr = getContentResolver();
					Uri uri = Uri.parse(UserInfo.CONTENT_URI + "/id/" + id);
					int result = cr
							.update(uri, u.toContentValues(), null, null);
					Bundle data = new Bundle();
					data.putInt(Commons.EXTRA_TYPE, type);
					data.putSerializable(Commons.EXTRA_USER, u);
					receiver.send(Commons.RESULT_CODE_FINISH, data);
				}
			}
				break;

			case Commons.ACTION_USER_FOLLOW: {
				User u = api.userFollow(id);
				if (u == null || u.isNull()) {
					receiver.send(Commons.RESULT_CODE_FINISH, null);
				} else {
					u.type = User.TYPE_FRIENDS;
					ContentResolver cr = getContentResolver();
					ContentValues values = new ContentValues();
					values.put(UserInfo.FOLLOWING, u.following);
					Uri uri = Uri.parse(UserInfo.CONTENT_URI + "/id/" + id);
					int result = cr.update(uri, values, null, null);
					Bundle data = new Bundle();
					data.putInt(Commons.EXTRA_TYPE, type);
					data.putSerializable(Commons.EXTRA_USER, u);
					receiver.send(Commons.RESULT_CODE_FINISH, data);
				}
				break;
			}
			case Commons.ACTION_USER_UNFOLLOW: {
				User u = api.userUnfollow(id);
				if (u == null || u.isNull()) {
					receiver.send(Commons.RESULT_CODE_FINISH, null);
				} else {
					u.type = Commons.TYPE_NONE;
					ContentResolver cr = getContentResolver();
					cr.delete(UserInfo.CONTENT_URI, BasicColumns.ID + "=?",
							new String[] { id });

					// ContentValues values = new ContentValues();
					// values.put(UserInfo.FOLLOWING, u.following);
					// Uri uri = Uri.withAppendedPath(UserInfo.CONTENT_URI,
					// "item/" + id);
					// int result = cr.update(uri, values, null, null);
					Bundle data = new Bundle();
					data.putInt(Commons.EXTRA_TYPE, type);
					data.putSerializable(Commons.EXTRA_USER, u);
					receiver.send(Commons.RESULT_CODE_FINISH, data);
					// 取消关注后要清空该用户名下的消息
					cr.delete(StatusInfo.CONTENT_URI,
							StatusInfo.USER_ID + "=?", new String[] { id });
				}
			}
				break;
			case Commons.ACTION_USER_BLOCK: {
				User u = api.userBlock(id);
				if (u == null || u.isNull()) {
					receiver.send(Commons.RESULT_CODE_FINISH, null);
				} else {
					ContentResolver cr = getContentResolver();
					cr.delete(UserInfo.CONTENT_URI, where, whereArgs);
					Bundle data = new Bundle();
					data.putInt(Commons.EXTRA_TYPE, type);
					data.putSerializable(Commons.EXTRA_USER, u);
					receiver.send(Commons.RESULT_CODE_FINISH, data);
				}
			}
				break;
			case Commons.ACTION_USER_UNBLOCK: {
				User u = api.userUnblock(id);
				if (u == null || u.isNull()) {
					receiver.send(Commons.RESULT_CODE_FINISH, null);
				} else {
					Bundle data = new Bundle();
					data.putInt(Commons.EXTRA_TYPE, type);
					data.putSerializable(Commons.EXTRA_USER, u);
					receiver.send(Commons.RESULT_CODE_FINISH, data);
				}
			}
				break;
			case Commons.ACTION_DIRECT_MESSAGE_DELETE:
			{
				// 删除消息
				// 404 说明消息不存在
				// 403 说明不是你的消息，无权限删除
				DirectMessage dm = api.messageDelete(id);
				if (dm == null || dm.isNull()) {
					receiver.send(Commons.RESULT_CODE_FINISH, null);
				} else {
					ContentResolver cr = getContentResolver();
					int result = cr.delete(DirectMessageInfo.CONTENT_URI, where,
							whereArgs);
					Bundle data = new Bundle();
					data.putInt(Commons.EXTRA_TYPE, type);
					data.putSerializable(Commons.EXTRA_MESSAGE, dm);
					receiver.send(Commons.RESULT_CODE_FINISH, data);
				}
			}
				break;
			case Commons.ACTION_DIRECT_MESSAGE_SHOW:
				break;
			case Commons.ACTION_DIRECT_MESSAGE_CREATE:
				break;
			default:
				break;
			}

		} catch (ApiException e) {
			if (App.DEBUG) {
				Log.e(TAG,
						"performAction: id=" + id + " type=" + type
								+ e.getMessage());
			}
			String message=e.getMessage();
			if(e.statusCode==ResponseCode.ERROR_NOT_CONNECTED||e.statusCode>=500){
				message="网络连接异常，请稍后重试";
			}
			Bundle b = new Bundle();
			b.putInt(Commons.EXTRA_TYPE, type);
			b.putInt(Commons.EXTRA_ERROR_CODE, e.statusCode);
			b.putString(Commons.EXTRA_ERROR_MESSAGE, message);
			receiver.send(Commons.RESULT_CODE_ERROR, b);
		}
	}
}
