package com.fanfou.app.hd.service;

import java.util.List;

import android.app.IntentService;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.Messenger;
import android.os.Parcelable;
import android.os.RemoteException;
import android.util.Log;

import com.fanfou.app.hd.App;
import com.fanfou.app.hd.App.ApnType;
import com.fanfou.app.hd.R;
import com.fanfou.app.hd.api.Api;
import com.fanfou.app.hd.api.ApiException;
import com.fanfou.app.hd.api.Paging;
import com.fanfou.app.hd.controller.DataController;
import com.fanfou.app.hd.dao.model.BaseModel;
import com.fanfou.app.hd.dao.model.DirectMessageModel;
import com.fanfou.app.hd.dao.model.StatusColumns;
import com.fanfou.app.hd.dao.model.StatusModel;
import com.fanfou.app.hd.dao.model.UserColumns;
import com.fanfou.app.hd.dao.model.UserModel;
import com.fanfou.app.hd.util.Assert;

/**
 * @author mcxiaoke
 * @version 1.0 20110602
 * @version 2.0 20110714
 * @version 2.1 2011.10.10
 * @version 3.0 2011.10.20
 * @version 3.1 2011.10.21
 * @version 3.2 2011.10.24
 * @version 3.3 2011.10.28
 * @version 4.0 2011.11.04
 * @version 4.1 2011.11.07
 * @version 4.2 2011.11.10
 * @version 4.3 2011.11.11
 * @version 4.4 2011.11.17
 * @version 5.0 2011.11.18
 * @version 5.1 2011.11.21
 * @version 5.2 2011.11.22
 * @version 5.3 2011.12.13
 * @version 6.0 2011.12.16
 * @version 6.1 2011.12.19
 * @version 7.0 2011.12.23
 * @version 7.1 2011.12.26
 * @version 7.2 2012.01.31
 * @version 7.5 2012.02.20
 * @version 8.0 2012.02.24
 * @version 8.1 2012.03.07
 * 
 */
public final class FanFouService extends IntentService {
	private static final String TAG = FanFouService.class.getSimpleName();
	private static final boolean DEBUG = App.DEBUG;

	public static final int MAX_TIMELINE_COUNT = 60;
	public static final int DEFAULT_TIMELINE_COUNT = 20;
	public static final int MAX_USERS_COUNT = 60;
	public static final int DEFAULT_USERS_COUNT = 20;
	public static final int MAX_IDS_COUNT = 2000;

	public static final int RESULT_SUCCESS = 1;
	public static final int RESULT_ERROR = -1;

	public static final int STATUS_SHOW = -101;
	public static final int STATUS_DELETE = -102;
	public static final int STATUS_FAVORITE = -103;
	public static final int STATUS_UNFAVORITE = -104;

	public static final int USER_SHOW = -201;
	public static final int USER_FOLLOW = -202;
	public static final int USER_UNFOLLOW = -203;
	public static final int USER_BLOCK = -204;
	public static final int USER_UNBLOCK = -205;

	public static final int DM_DELETE = -302;

	public static final int FRIENDSHIPS_EXISTS = -401;
	public static final int FRIENDSHIPS_SHOW = -402;
	public static final int FRIENDSHIPS_REQUESTS = -403;
	public static final int FRIENDSHIPS_ACCEPT = -404;
	public static final int FRIENDSHIPS_DENY = -405;

	private int type;
	private Messenger messenger;
	private Api api;
	private String account;
	private String id;

	public FanFouService() {
		super("FetchService");
	}

	@Override
	public void onCreate() {
		super.onCreate();
		api = App.getApi();

	}

	@Override
	protected void onHandleIntent(Intent intent) {
		if (intent == null) {
			return;
		}

		account = App.getAccount();
		messenger = intent.getParcelableExtra("messenger");
		id = intent.getStringExtra("id");
		type = intent.getIntExtra("type", -1);

		switch (type) {
		case BaseModel.TYPE_NONE:
			break;
		case StatusModel.TYPE_HOME:
		case StatusModel.TYPE_MENTIONS:
		case StatusModel.TYPE_USER:
		case StatusModel.TYPE_CONTEXT:
		case StatusModel.TYPE_PUBLIC:
		case StatusModel.TYPE_FAVORITES:
			getTimeline(intent);
			break;
		case STATUS_SHOW:
			showStatus(id);
			break;
		case STATUS_DELETE:
			deleteStatus(id);
			break;
		case DirectMessageModel.TYPE_INBOX:
			getInBox(intent);
			break;
		case DirectMessageModel.TYPE_OUTBOX:
			getOutBox(intent);
			break;
		case DirectMessageModel.TYPE_CONVERSATION_LIST:
			getConversationList(intent);
			break;
		case DirectMessageModel.TYPE_CONVERSATION:
			getConversation(id, intent);
			break;
		case DM_DELETE:
			deleteDirectMessage(id);
			break;
		case USER_SHOW:
			showUser(id);
			break;
		case UserModel.TYPE_FRIENDS:
		case UserModel.TYPE_FOLLOWERS:
			fetchUsers(intent);
			break;
		case USER_FOLLOW:
			follow(id);
			break;
		case USER_UNFOLLOW:
			unfollow(id);
			break;
		case FRIENDSHIPS_EXISTS:
			isFriends(intent);
			break;
		case FRIENDSHIPS_SHOW:
			// TODO
			break;
		case FRIENDSHIPS_REQUESTS:
			// TODO
			break;
		case FRIENDSHIPS_DENY:
			// TODO
			break;
		case FRIENDSHIPS_ACCEPT:
			// TODO
			break;
		case USER_BLOCK:
			block(id);
			break;
		case USER_UNBLOCK:
			unblock(id);
			break;
		case STATUS_FAVORITE:
			favorite(id);
			break;
		case STATUS_UNFAVORITE:
			unfavorite(id);
			break;
		case StatusModel.TYPE_PHOTO:
			break;
		default:
			break;
		}

	}

	public static void deleteDirectMessage(Context context, String id,
			final Handler handler) {
		Intent intent = new Intent(context, FanFouService.class);
		intent.putExtra("type", DM_DELETE);
		intent.putExtra("id", id);
		intent.putExtra("messenger", new Messenger(handler));
		context.startService(intent);
	}

	private void deleteDirectMessage(String id) {
		DirectMessageModel dm = null;
		try {
			// 删除消息
			// 404 说明消息不存在
			// 403 说明不是你的消息，无权限删除
			dm = api.deleteDirectMessage(id);
			if (dm == null) {
				sendSuccessMessage();
			} else {
				DataController.delete(this, dm);
				sendParcelableMessage(dm);
			}
		} catch (ApiException e) {
			if (DEBUG) {
				e.printStackTrace();
			}
			if (e.statusCode == 404) {
				DataController.delete(this, dm);
			}
			sendErrorMessage(e);
		}
	}

	private void block(String id) {
		Assert.notEmpty(id);
		UserModel u = null;
		try {
			u = api.block(id);
			if (u == null) {
				sendSuccessMessage();
			} else {
				DataController.delete(this, u);

				sendParcelableMessage(u);
			}
		} catch (ApiException e) {
			if (App.DEBUG) {
				e.printStackTrace();
			}
			sendErrorMessage(e);
		}
	}

	private void unblock(String id) {
		Assert.notEmpty(id);
		try {
			UserModel u = api.unblock(id);
			if (u == null) {
				sendSuccessMessage();
			} else {
				sendParcelableMessage(u);
			}
		} catch (ApiException e) {
			if (App.DEBUG) {
				e.printStackTrace();
			}
			sendErrorMessage(e);
		}
	}

	public static void follow(Context context, String userId,
			final Handler handler) {
		Intent intent = new Intent(context, FanFouService.class);
		intent.putExtra("type", USER_FOLLOW);
		intent.putExtra("id", userId);
		intent.putExtra("messenger", new Messenger(handler));
		context.startService(intent);

	}

	public static void unFollow(Context context, String userId,
			final Handler handler) {
		Intent intent = new Intent(context, FanFouService.class);
		intent.putExtra("type", USER_UNFOLLOW);
		intent.putExtra("id", userId);
		intent.putExtra("messenger", new Messenger(handler));
		context.startService(intent);

	}

	private void follow(String id) {
		Assert.notEmpty(id);
		try {
			UserModel u = api.follow(id);
			if (u != null) {
				u.setType(UserModel.TYPE_FRIENDS);
				DataController.updateUserModel(this, u);
			}
			sendSuccessMessage();
		} catch (ApiException e) {
			if (App.DEBUG) {
				e.printStackTrace();
			}
			sendErrorMessage(e);
		}
	}

	private void unfollow(String id) {
		Assert.notEmpty(id);
		try {
			UserModel u = api.unfollow(id);
			if (u != null) {
				DataController.delete(this, u);
			}
			sendSuccessMessage();
		} catch (ApiException e) {
			if (App.DEBUG) {
				e.printStackTrace();
			}
			sendErrorMessage(e);
		}
	}

	public static void showUser(Context context, String id,
			final Handler handler) {
		startService(context, USER_SHOW, id, handler);
	}

	private void showUser(String id) {
		try {
			UserModel u = api.showUser(id);
			if (u == null) {
				sendSuccessMessage();
			} else {
				sendParcelableMessage(u);

			}
		} catch (ApiException e) {
			if (App.DEBUG) {
				e.printStackTrace();
			}
			sendErrorMessage(e);
		}
	}

	private static void startService(Context context, int type, String id,
			final Handler handler) {
		Intent intent = new Intent(context, FanFouService.class);
		intent.putExtra("type", type);
		intent.putExtra("id", id);
		intent.putExtra("messenger", new Messenger(handler));
		context.startService(intent);
	}

	public static void favorite(Context context, String id,
			final Handler handler) {
		favoriteAction(context, id, handler, true);
	}

	public static void unfavorite(Context context, String id,
			final Handler handler) {
		favoriteAction(context, id, handler, false);
	}

	private static void favoriteAction(Context context, String id,
			final Handler handler, boolean favorite) {
		startService(context, favorite ? STATUS_FAVORITE : STATUS_UNFAVORITE,
				id, handler);
	}

	private void favorite(String id) {
		Assert.notEmpty(id);
		StatusModel s = null;
		try {
			s = api.favorite(id);
			if (s == null) {
				sendSuccessMessage();
			} else {
				ContentValues values = new ContentValues();
				values.put(StatusColumns.FAVORITED, true);
				DataController.update(this, s, values);
				Bundle bundle = new Bundle();
				bundle.putInt("type", type);
				bundle.putBoolean("boolean", true);
				sendSuccessMessage(bundle);
			}
		} catch (ApiException e) {
			if (App.DEBUG) {
				e.printStackTrace();
			}
			if (e.statusCode == 404) {
				DataController.delete(this, s);
			}
			sendErrorMessage(e);
		}
	}

	private void unfavorite(String id) {
		Assert.notEmpty(id);
		// 404 消息不存在
		// 404 没有通过用户验证
		StatusModel s = null;
		try {
			s = api.unfavorite(id);
			if (s == null) {
				sendSuccessMessage();
			} else {
				ContentValues values = new ContentValues();
				values.put("favorited", false);
				DataController.update(this, s, values);

				Bundle bundle = new Bundle();
				bundle.putInt("type", type);
				bundle.putBoolean("boolean", false);
				sendSuccessMessage(bundle);
			}
		} catch (ApiException e) {
			if (App.DEBUG) {
				e.printStackTrace();
			}
			if (e.statusCode == 404) {
				DataController.delete(this, s);
			}
			sendErrorMessage(e);
		}
	}

	public static void deleteStatus(Context context, String id,
			final Handler handler) {
		Intent intent = new Intent(context, FanFouService.class);
		intent.putExtra("type", STATUS_DELETE);
		intent.putExtra("id", id);
		intent.putExtra("messenger", new Messenger(handler));
		context.startService(intent);
	}

	private void deleteStatus(String id) {
		StatusModel s = null;
		try {
			s = api.deleteStatus(id);
			if (s == null) {
				sendSuccessMessage();
			} else {
				DataController.delete(this, s);
				sendParcelableMessage(s);
			}
		} catch (ApiException e) {
			if (App.DEBUG) {
				e.printStackTrace();
			}
			if (e.statusCode == 404) {
				DataController.delete(this, s);
			}
			sendErrorMessage(e);
		}
	}

	public static void doProfile(Context context, String userId,
			final Handler handler) {
		startService(context, USER_SHOW, userId, handler);
	}

	public static void showStatus(Context context, String id,
			final Handler handler) {
		startService(context, STATUS_SHOW, id, handler);
	}

	private void showStatus(String id) {
		StatusModel s = null;
		try {
			s = api.showStatus(id);
			if (s == null) {
				sendSuccessMessage();
			} else {
				sendParcelableMessage(s);
			}
		} catch (ApiException e) {
			if (App.DEBUG) {
				e.printStackTrace();
			}
			if (e.statusCode == 404) {
				DataController.delete(this, s);
			}
			sendErrorMessage(e);
		}

	}

	public static void showRelation(Context context, String userA,
			String userB, final Handler handler) {
		if (context == null || handler == null) {
			return;
		}
		Intent intent = new Intent(context, FanFouService.class);
		intent.putExtra("type", FRIENDSHIPS_EXISTS);
		intent.putExtra("user_a", userA);
		intent.putExtra("user_b", userB);
		intent.putExtra("messenger", new Messenger(handler));
		context.startService(intent);
	}

	private void isFriends(Intent intent) {
		String userA = intent.getStringExtra("user_a");
		String userB = intent.getStringExtra("user_b");
		boolean result = false;
		try {
			result = api.isFriends(userA, userB);
		} catch (ApiException e) {
			if (App.DEBUG) {
				Log.e(TAG, "doDetectFriendships:" + e.getMessage());
			}
			sendErrorMessage(e);
		}
		Bundle data = new Bundle();
		data.putBoolean("boolean", result);
		sendSuccessMessage(data);
	}

	private void fetchUsers(Intent intent) {
		String id = intent.getStringExtra("id");
		Paging p = intent.getParcelableExtra("data");

		if (App.getApnType() == ApnType.WIFI) {
			p.count = MAX_USERS_COUNT;
		} else {
			p.count = DEFAULT_USERS_COUNT;
		}
		try {
			List<UserModel> users = null;
			if (type == UserModel.TYPE_FRIENDS) {
				users = api.getFriends(id, p);
			} else if (type == UserModel.TYPE_FOLLOWERS) {
				users = api.getFollowers(id, p);
			}
			if (users != null && users.size() > 0) {

				int size = users.size();
				ContentResolver cr = getContentResolver();
				if (p.page < 2 && id != account) {
					String where = UserColumns.TYPE + "=? AND "
							+ UserColumns.OWNER + "=?";
					String[] whereArgs = new String[] { String.valueOf(type),
							id };
					int deletedNums = cr.delete(UserColumns.CONTENT_URI, where,
							whereArgs);
					if (App.DEBUG) {
						Log.d(TAG, "fetchUsers delete old rows " + deletedNums
								+ " ownerId=" + id);
					}
				}
				int nums = DataController.store(this, users);
				if (App.DEBUG) {
					Log.d(TAG, "fetchUsers refresh ,insert rows, num=" + nums
							+ " ownerId=" + id);
				}
				sendIntMessage(nums);
			} else {
				sendIntMessage(0);
			}
		} catch (ApiException e) {
			if (App.DEBUG) {
				e.printStackTrace();
			}
			sendErrorMessage(e);
		}
	}

	private void getConversation(String id, Intent intent) {

		Paging p = intent.getParcelableExtra("data");
		Assert.notNull(p);
		if (App.getApnType() == ApnType.WIFI) {
			p.count = MAX_TIMELINE_COUNT;
		} else {
			p.count = DEFAULT_TIMELINE_COUNT;
		}

		try {
			List<DirectMessageModel> messages = api.getConversation(id, p);
			if (messages != null && messages.size() > 0) {

				if (App.DEBUG) {
					Log.d(TAG, "getConversation() id=" + id + " result="
							+ messages);
				}

				int nums = DataController.store(this, messages);
				sendIntMessage(nums);
			}
			sendIntMessage(0);
		} catch (ApiException e) {
			if (DEBUG) {
				e.printStackTrace();
			}
			sendErrorMessage(e);
		}
	}

	private void getConversationList(Intent intent) {
		Paging p = intent.getParcelableExtra("data");
		Assert.notNull(p);
		if (App.getApnType() == ApnType.WIFI) {
			p.count = MAX_TIMELINE_COUNT;
		} else {
			p.count = DEFAULT_TIMELINE_COUNT;
		}
		try {
			List<DirectMessageModel> messages = api.getConversationList(p);
			if (messages != null && messages.size() > 0) {
				int nums = DataController.store(this, messages);
				sendIntMessage(nums);
			}
			sendIntMessage(0);
		} catch (ApiException e) {
			if (DEBUG) {
				e.printStackTrace();
			}
			sendErrorMessage(e);
		}
	}

	public static void getConversationList(Context context,
			final Handler handler, Paging paging) {
		getDirectMessages(context, handler, paging,
				DirectMessageModel.TYPE_CONVERSATION_LIST);
	}

	public static void getConversation(Context context, final Handler handler,
			Paging paging, String userId) {
		Intent intent = new Intent(context, FanFouService.class);
		intent.putExtra("type", DirectMessageModel.TYPE_CONVERSATION);
		intent.putExtra("messenger", new Messenger(handler));
		intent.putExtra("id", userId);
		intent.putExtra("data", paging);
		context.startService(intent);
	}

	public static void getInbox(Context context, final Handler handler,
			Paging paging) {
		getDirectMessages(context, handler, paging,
				DirectMessageModel.TYPE_INBOX);
	}

	public static void getOutbox(Context context, final Handler handler,
			Paging paging) {
		getDirectMessages(context, handler, paging,
				DirectMessageModel.TYPE_OUTBOX);
	}

	private static void getDirectMessages(Context context,
			final Handler handler, Paging paging, int type) {
		Intent intent = new Intent(context, FanFouService.class);
		intent.putExtra("type", type);
		intent.putExtra("messenger", new Messenger(handler));
		intent.putExtra("data", paging);
		context.startService(intent);
	}

	public static void getDirectMessages(Context context,
			final Messenger messenger, Paging paging, String userId) {
		Intent intent = new Intent(context, FanFouService.class);
		intent.putExtra("type", DirectMessageModel.TYPE_CONVERSATION);
		intent.putExtra("id", userId);
		intent.putExtra("messenger", messenger);
		intent.putExtra("data", paging);
		context.startService(intent);
	}

	private void getDirectMessages(Intent intent, boolean in) {
		Paging p = intent.getParcelableExtra("data");
		Assert.notNull(p);

		if (App.getApnType() == ApnType.WIFI) {
			p.count = MAX_TIMELINE_COUNT;
		} else {
			p.count = DEFAULT_TIMELINE_COUNT;
		}

		try {
			List<DirectMessageModel> messages = in ? api
					.getDirectMessagesInbox(p) : api.getDirectMessagesOutbox(p);
			if (messages != null && messages.size() > 0) {
				int nums = DataController.store(this, messages);
				sendIntMessage(nums);
			}
			sendIntMessage(0);
		} catch (ApiException e) {
			if (DEBUG) {
				e.printStackTrace();
			}
			sendErrorMessage(e);
		}
	}

	private void getInBox(Intent intent) {
		getDirectMessages(intent, true);
	}

	private void getOutBox(Intent intent) {
		getDirectMessages(intent, false);
	}

	private void getTimeline(Intent intent) {
		List<StatusModel> statuses = null;

		String id = intent.getStringExtra("id");
		Paging p = intent.getParcelableExtra("data");
		if (p == null) {
			p = new Paging();
		}
		if (App.getApnType() == ApnType.WIFI) {
			p.count = MAX_TIMELINE_COUNT;
		} else {
			p.count = DEFAULT_TIMELINE_COUNT;
		}

		if (App.DEBUG) {
			Log.d(TAG, "getTimeline userId=" + id + " paging=" + p + " type="
					+ type);
		}

		try {
			switch (type) {
			case StatusModel.TYPE_HOME:
				statuses = api.getHomeTimeline(p);
				break;
			case StatusModel.TYPE_MENTIONS:
				statuses = api.getMentions(p);
				break;
			case StatusModel.TYPE_PUBLIC:
				p.count = DEFAULT_TIMELINE_COUNT;
				statuses = api.getPublicTimeline();
				break;
			case StatusModel.TYPE_FAVORITES:
				statuses = api.getFavorites(id, p);
				break;
			case StatusModel.TYPE_USER:
				statuses = api.getUserTimeline(id, p);
				break;
			case StatusModel.TYPE_CONTEXT:
				statuses = api.getContextTimeline(id);
				break;
			default:
				break;
			}
			if (statuses == null || statuses.size() == 0) {
				sendIntMessage(0);
				if (App.DEBUG)
					Log.d(TAG, "getTimeline() count=0. userId=" + id + " type="
							+ type);
				return;
			} else {
				int size = statuses.size();
				if (size == p.count && p.maxId == null && p.page <= 1) {
					deleteOldStatuses();
				}
				int insertedCount = DataController.store(this, statuses);
				if (App.DEBUG) {
					Log.d(TAG, "getTimeline() size=" + size + " userId=" + id
							+ " count=" + p.count + " page=" + p.page
							+ " type=" + type + " insertedCount="
							+ insertedCount);
				}
				sendIntMessage(insertedCount);
			}
		} catch (ApiException e) {
			if (App.DEBUG) {
				Log.e(TAG, "getTimeline() [error]" + e.statusCode + ":"
						+ e.errorMessage + " userId=" + id + " type=" + type);
				e.printStackTrace();
			}
			sendErrorMessage(e);
		}
	}

	private int deleteOldStatuses() {
		int numDeleted = 0;
		if (type == StatusModel.TYPE_USER) {
			numDeleted = DataController.deleteUserTimeline(this, id);
		} else if (type == StatusModel.TYPE_FAVORITES) {
			numDeleted = DataController.deleteUserFavorites(this, id);
		} else {
			numDeleted = DataController.deleteStatusByType(this, type);
		}
		if (App.DEBUG) {
			Log.d(TAG, "deleteOldStatuses numDeleted=" + numDeleted + " type="
					+ type + " id=" + id);
		}
		return numDeleted;
	}

	public static void getTimeline(Context context, int type,
			final Handler handler, String userId, Paging paging) {
		Intent intent = new Intent(context, FanFouService.class);
		intent.putExtra("type", type);
		intent.putExtra("id", userId);
		intent.putExtra("messenger", new Messenger(handler));
		intent.putExtra("data", paging);
		if (App.DEBUG) {
			Log.d(TAG, "getTimeline() type=" + type + " paging=" + paging
					+ " userId=" + userId);
		}
		context.startService(intent);
	}

	public static void getPublicTimeline(Context context, final Handler handler) {
		getTimeline(context, StatusModel.TYPE_PUBLIC, handler, null, null);
	}

	public static void getTimeline(Context context, int type,
			final Handler handler, Paging paging) {
		getTimeline(context, type, handler, null, paging);
	}

	public static void getUsers(Context context, String userId, int type,
			Paging paging, final Handler handler) {
		Intent intent = new Intent(context, FanFouService.class);
		intent.putExtra("type", type);
		intent.putExtra("messenger", new Messenger(handler));
		intent.putExtra("data", paging);
		intent.putExtra("id", userId);
		context.startService(intent);
	}

	private void sendErrorMessage(ApiException e) {
		String message = e.getMessage();
		if (e.statusCode == ApiException.IO_ERROR) {
			message = getString(R.string.msg_connection_error);
		} else if (e.statusCode >= 500) {
			message = getString(R.string.msg_server_error);
		}
		Bundle bundle = new Bundle();
		bundle.putInt("error_code", e.statusCode);
		bundle.putString("error_message", message);
		sendMessage(RESULT_ERROR, bundle);
	}

	private void sendIntMessage(int size) {
		Bundle bundle = new Bundle();
		bundle.putInt("count", size);
		sendMessage(RESULT_SUCCESS, bundle);
	}

	private void sendParcelableMessage(Parcelable parcel) {
		Bundle bundle = new Bundle();
		bundle.putParcelable("data", parcel);
		sendMessage(RESULT_SUCCESS, bundle);
	}

	private void sendSuccessMessage(Bundle bundle) {
		sendMessage(RESULT_SUCCESS, bundle);
	}

	private void sendSuccessMessage() {
		sendMessage(RESULT_SUCCESS, null);
	}

	private void sendMessage(int what, final Bundle bundle) {
		if (messenger == null) {
			return;
		}
		Message m = Message.obtain();
		m.what = what;
		m.arg1 = type;
		if (bundle != null) {
			m.getData().putAll(bundle);
		}
		try {
			messenger.send(m);
		} catch (RemoteException e) {
			if (App.DEBUG) {
				e.printStackTrace();
			}
		}
	}

}
