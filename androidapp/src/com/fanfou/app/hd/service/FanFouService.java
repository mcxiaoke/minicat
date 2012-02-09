package com.fanfou.app.hd.service;

import static com.fanfou.app.hd.service.Constants.DEFAULT_TIMELINE_COUNT;
import static com.fanfou.app.hd.service.Constants.DEFAULT_USERS_COUNT;
import static com.fanfou.app.hd.service.Constants.EXTRA_BOOLEAN;
import static com.fanfou.app.hd.service.Constants.EXTRA_CODE;
import static com.fanfou.app.hd.service.Constants.EXTRA_COUNT;
import static com.fanfou.app.hd.service.Constants.EXTRA_DATA;
import static com.fanfou.app.hd.service.Constants.EXTRA_ERROR;
import static com.fanfou.app.hd.service.Constants.EXTRA_ID;
import static com.fanfou.app.hd.service.Constants.EXTRA_MAX_ID;
import static com.fanfou.app.hd.service.Constants.EXTRA_MESSENGER;
import static com.fanfou.app.hd.service.Constants.EXTRA_PAGE;
import static com.fanfou.app.hd.service.Constants.EXTRA_SINCE_ID;
import static com.fanfou.app.hd.service.Constants.EXTRA_TYPE;
import static com.fanfou.app.hd.service.Constants.FORMAT;
import static com.fanfou.app.hd.service.Constants.MAX_TIMELINE_COUNT;
import static com.fanfou.app.hd.service.Constants.MAX_USERS_COUNT;
import static com.fanfou.app.hd.service.Constants.MODE;
import static com.fanfou.app.hd.service.Constants.RESULT_ERROR;
import static com.fanfou.app.hd.service.Constants.RESULT_SUCCESS;
import static com.fanfou.app.hd.service.Constants.TYPE_ACCOUNT_NOTIFICATION;
import static com.fanfou.app.hd.service.Constants.TYPE_ACCOUNT_RATE_LIMIT_STATUS;
import static com.fanfou.app.hd.service.Constants.TYPE_ACCOUNT_REGISTER;
import static com.fanfou.app.hd.service.Constants.TYPE_ACCOUNT_UPDATE_PROFILE;
import static com.fanfou.app.hd.service.Constants.TYPE_ACCOUNT_UPDATE_PROFILE_IMAGE;
import static com.fanfou.app.hd.service.Constants.TYPE_ACCOUNT_VERIFY_CREDENTIALS;
import static com.fanfou.app.hd.service.Constants.TYPE_BLOCKS;
import static com.fanfou.app.hd.service.Constants.TYPE_BLOCKS_CREATE;
import static com.fanfou.app.hd.service.Constants.TYPE_BLOCKS_DESTROY;
import static com.fanfou.app.hd.service.Constants.TYPE_BLOCKS_EXISTS;
import static com.fanfou.app.hd.service.Constants.TYPE_BLOCKS_IDS;
import static com.fanfou.app.hd.service.Constants.TYPE_DIRECT_MESSAGES_CONVERSTATION;
import static com.fanfou.app.hd.service.Constants.TYPE_DIRECT_MESSAGES_CONVERSTATION_LIST;
import static com.fanfou.app.hd.service.Constants.TYPE_DIRECT_MESSAGES_CREATE;
import static com.fanfou.app.hd.service.Constants.TYPE_DIRECT_MESSAGES_DESTROY;
import static com.fanfou.app.hd.service.Constants.TYPE_DIRECT_MESSAGES_INBOX;
import static com.fanfou.app.hd.service.Constants.TYPE_DIRECT_MESSAGES_OUTBOX;
import static com.fanfou.app.hd.service.Constants.TYPE_FAVORITES_CREATE;
import static com.fanfou.app.hd.service.Constants.TYPE_FAVORITES_DESTROY;
import static com.fanfou.app.hd.service.Constants.TYPE_FAVORITES_LIST;
import static com.fanfou.app.hd.service.Constants.TYPE_FOLLOWERS_IDS;
import static com.fanfou.app.hd.service.Constants.TYPE_FRIENDSHIPS_ACCEPT;
import static com.fanfou.app.hd.service.Constants.TYPE_FRIENDSHIPS_CREATE;
import static com.fanfou.app.hd.service.Constants.TYPE_FRIENDSHIPS_DENY;
import static com.fanfou.app.hd.service.Constants.TYPE_FRIENDSHIPS_DESTROY;
import static com.fanfou.app.hd.service.Constants.TYPE_FRIENDSHIPS_EXISTS;
import static com.fanfou.app.hd.service.Constants.TYPE_FRIENDSHIPS_REQUESTS;
import static com.fanfou.app.hd.service.Constants.TYPE_FRIENDSHIPS_SHOW;
import static com.fanfou.app.hd.service.Constants.TYPE_FRIENDS_IDS;
import static com.fanfou.app.hd.service.Constants.TYPE_NONE;
import static com.fanfou.app.hd.service.Constants.TYPE_PHOTOS_UPLOAD;
import static com.fanfou.app.hd.service.Constants.TYPE_PHOTOS_USER_TIMELINE;
import static com.fanfou.app.hd.service.Constants.TYPE_SAVED_SEARCHES_CREATE;
import static com.fanfou.app.hd.service.Constants.TYPE_SAVED_SEARCHES_DESTROY;
import static com.fanfou.app.hd.service.Constants.TYPE_SAVED_SEARCHES_LIST;
import static com.fanfou.app.hd.service.Constants.TYPE_SAVED_SEARCHES_SHOW;
import static com.fanfou.app.hd.service.Constants.TYPE_SEARCH_PUBLIC_TIMELINE;
import static com.fanfou.app.hd.service.Constants.TYPE_SEARCH_USERS;
import static com.fanfou.app.hd.service.Constants.TYPE_SEARCH_USER_TIMELINE;
import static com.fanfou.app.hd.service.Constants.TYPE_STATUSES_CONTEXT_TIMELINE;
import static com.fanfou.app.hd.service.Constants.TYPE_STATUSES_DESTROY;
import static com.fanfou.app.hd.service.Constants.TYPE_STATUSES_HOME_TIMELINE;
import static com.fanfou.app.hd.service.Constants.TYPE_STATUSES_MENTIONS;
import static com.fanfou.app.hd.service.Constants.TYPE_STATUSES_PUBLIC_TIMELINE;
import static com.fanfou.app.hd.service.Constants.TYPE_STATUSES_SHOW;
import static com.fanfou.app.hd.service.Constants.TYPE_STATUSES_UPDATE;
import static com.fanfou.app.hd.service.Constants.TYPE_STATUSES_USER_TIMELINE;
import static com.fanfou.app.hd.service.Constants.TYPE_TRENDS_LIST;
import static com.fanfou.app.hd.service.Constants.TYPE_USERS_FOLLOWERS;
import static com.fanfou.app.hd.service.Constants.TYPE_USERS_FRIENDS;
import static com.fanfou.app.hd.service.Constants.TYPE_USERS_SHOW;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.app.IntentService;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.Messenger;
import android.os.Parcelable;
import android.os.RemoteException;
import android.util.Log;
import android.widget.BaseAdapter;

import com.fanfou.app.hd.R;
import com.fanfou.app.hd.App;
import com.fanfou.app.hd.App.ApnType;
import com.fanfou.app.hd.api.Api;
import com.fanfou.app.hd.api.ApiException;
import com.fanfou.app.hd.api.DirectMessage;
import com.fanfou.app.hd.api.FanFouApi;
import com.fanfou.app.hd.api.Parser;
import com.fanfou.app.hd.api.Status;
import com.fanfou.app.hd.api.User;
import com.fanfou.app.hd.db.FanFouProvider;
import com.fanfou.app.hd.db.Contents.BasicColumns;
import com.fanfou.app.hd.db.Contents.DirectMessageInfo;
import com.fanfou.app.hd.db.Contents.StatusInfo;
import com.fanfou.app.hd.db.Contents.UserInfo;
import com.fanfou.app.hd.http.ResponseCode;
import com.fanfou.app.hd.ui.widget.ActionManager.ResultListener;
import com.fanfou.app.hd.ui.widget.UIManager.ActionResultHandler;
import com.fanfou.app.hd.util.StringHelper;
import com.fanfou.app.hd.util.Utils;

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
 * 
 */
public class FanFouService extends IntentService {
	private static final String TAG = FanFouService.class.getSimpleName();

	private int type;
	private Messenger messenger;
	private Api api;

	public FanFouService() {
		super("FetchService");
	}

	public void log(String message) {
		Log.d(TAG, message);
	}

	@Override
	protected void onHandleIntent(Intent intent) {
		if (intent == null) {
			return;
		}
		messenger = intent.getParcelableExtra(EXTRA_MESSENGER);
		type = intent.getIntExtra(EXTRA_TYPE, -1);
		api = FanFouApi.newInstance();

		if (App.DEBUG) {
			log("onHandleIntent() type=" + type);
		}

		switch (type) {
		case TYPE_NONE:
			break;
		case TYPE_ACCOUNT_REGISTER:
			break;
		case TYPE_ACCOUNT_VERIFY_CREDENTIALS:
			break;
		case TYPE_ACCOUNT_RATE_LIMIT_STATUS:
			break;
		case TYPE_ACCOUNT_UPDATE_PROFILE:
			break;
		case TYPE_ACCOUNT_UPDATE_PROFILE_IMAGE:
			break;
		case TYPE_ACCOUNT_NOTIFICATION:
			break;
		case TYPE_STATUSES_HOME_TIMELINE:
		case TYPE_STATUSES_MENTIONS:
		case TYPE_STATUSES_USER_TIMELINE:
		case TYPE_STATUSES_CONTEXT_TIMELINE:
		case TYPE_STATUSES_PUBLIC_TIMELINE:
		case TYPE_FAVORITES_LIST:
			fetchTimeline(intent);
			break;
		case TYPE_STATUSES_SHOW:
			statusesShow(intent);
			break;
		case TYPE_STATUSES_UPDATE:
			break;
		case TYPE_STATUSES_DESTROY:
			statusesDestroy(intent);
			break;
		case TYPE_DIRECT_MESSAGES_INBOX:
			fetchDirectMessagesInbox(intent);
			break;
		case TYPE_DIRECT_MESSAGES_OUTBOX:
			fetchDirectMessagesOutbox(intent);
			break;
		case TYPE_DIRECT_MESSAGES_CONVERSTATION_LIST:
			fetchConversationList(intent);
			break;
		case TYPE_DIRECT_MESSAGES_CONVERSTATION:
			break;
		case TYPE_DIRECT_MESSAGES_CREATE:
			break;
		case TYPE_DIRECT_MESSAGES_DESTROY:
			directMessagesDelete(intent);
			break;
		case TYPE_USERS_SHOW:
			userShow(intent);
			break;
		case TYPE_USERS_FRIENDS:
		case TYPE_USERS_FOLLOWERS:
			fetchUsers(intent);
			break;
		case TYPE_FRIENDSHIPS_CREATE:
			friendshipsCreate(intent);
			break;
		case TYPE_FRIENDSHIPS_DESTROY:
			friendshipsDelete(intent);
			break;
		case TYPE_FRIENDSHIPS_EXISTS:
			friendshipsExists(intent);
			break;
		case TYPE_FRIENDSHIPS_SHOW:
			break;
		case TYPE_FRIENDSHIPS_REQUESTS:
			break;
		case TYPE_FRIENDSHIPS_DENY:
			break;
		case TYPE_FRIENDSHIPS_ACCEPT:
			break;
		case TYPE_BLOCKS:
			break;
		case TYPE_BLOCKS_IDS:
			break;
		case TYPE_BLOCKS_CREATE:
			blocksCreate(intent);
			break;
		case TYPE_BLOCKS_DESTROY:
			blocksDelete(intent);
			break;
		case TYPE_BLOCKS_EXISTS:
			break;
		case TYPE_FRIENDS_IDS:
			break;
		case TYPE_FOLLOWERS_IDS:
			break;
		case TYPE_FAVORITES_CREATE:
			favoritesCreate(intent);
			break;
		case TYPE_FAVORITES_DESTROY:
			favoritesDelete(intent);
			break;
		case TYPE_PHOTOS_USER_TIMELINE:
			break;
		case TYPE_PHOTOS_UPLOAD:
			break;
		case TYPE_SEARCH_PUBLIC_TIMELINE:
			break;
		case TYPE_SEARCH_USER_TIMELINE:
			break;
		case TYPE_SEARCH_USERS:
			break;
		case TYPE_SAVED_SEARCHES_LIST:
			break;
		case TYPE_SAVED_SEARCHES_SHOW:
			break;
		case TYPE_SAVED_SEARCHES_CREATE:
			break;
		case TYPE_SAVED_SEARCHES_DESTROY:
			break;
		case TYPE_TRENDS_LIST:
		default:
			break;
		}

	}

	public static void doMessageDelete(final Activity activity,
			final String id, final ResultListener li, final boolean finish) {
		if (StringHelper.isEmpty(id)) {
			if (App.DEBUG) {
				Log.d(TAG, "doMessageDelete: status id is null.");
			}
			throw new NullPointerException("directmessageid cannot be null.");
		}

		final Handler handler = new Handler() {

			@Override
			public void handleMessage(Message msg) {
				switch (msg.what) {
				case Constants.RESULT_SUCCESS:
					Utils.notify(activity.getApplicationContext(), "删除成功");
					onSuccess(li, Constants.TYPE_DIRECT_MESSAGES_DESTROY,
							"删除成功");
					if (finish && activity != null) {
						activity.finish();
					}
					break;
				case Constants.RESULT_ERROR:
					String errorMessage = msg.getData().getString(
							Constants.EXTRA_ERROR);
					Utils.notify(activity.getApplicationContext(), errorMessage);
					onFailed(li, Constants.TYPE_DIRECT_MESSAGES_DESTROY, "删除失败");
					break;
				default:
					break;
				}
			}
		};
		FanFouService.doDirectMessagesDelete(activity, id, handler);
	}

	public static void doDirectMessagesDelete(Context context, String id,
			final Handler handler) {
		Intent intent = new Intent(context, FanFouService.class);
		intent.putExtra(EXTRA_TYPE, TYPE_DIRECT_MESSAGES_DESTROY);
		intent.putExtra(EXTRA_ID, id);
		intent.putExtra(EXTRA_MESSENGER, new Messenger(handler));
		context.startService(intent);
	}

	private void directMessagesDelete(Intent intent) {
		String id = intent.getStringExtra(EXTRA_ID);
		String where = BasicColumns.ID + "=?";
		String[] whereArgs = new String[] { id };
		try {
			// 删除消息
			// 404 说明消息不存在
			// 403 说明不是你的消息，无权限删除
			DirectMessage dm = api.directMessagesDelete(id, MODE);
			if (dm == null || dm.isNull()) {
				sendSuccessMessage();
			} else {
				ContentResolver cr = getContentResolver();
				int result = cr.delete(DirectMessageInfo.CONTENT_URI, where,
						whereArgs);
				sendParcelableMessage(dm);
			}
		} catch (ApiException e) {
			if (App.DEBUG) {
				e.printStackTrace();
			}
			sendErrorMessage(e);
		}
	}

	private void blocksCreate(Intent intent) {
		String id = intent.getStringExtra(EXTRA_ID);
		String where = BasicColumns.ID + "=?";
		String[] whereArgs = new String[] { id };
		try {
			User u = api.blocksCreate(id, MODE);
			if (u == null || u.isNull()) {
				sendSuccessMessage();
			} else {
				getContentResolver().delete(UserInfo.CONTENT_URI, where,
						whereArgs);
				sendParcelableMessage(u);
			}
		} catch (ApiException e) {
			if (App.DEBUG) {
				e.printStackTrace();
			}
			sendErrorMessage(e);
		}
	}

	private void blocksDelete(Intent intent) {
		String id = intent.getStringExtra(EXTRA_ID);
		try {
			User u = api.blocksDelete(id, MODE);
			if (u == null || u.isNull()) {
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

	public static void doFollow(Context context, final User user,
			final Handler handler) {
		if (user.following) {
			doUnFollow(context, user.id, handler);
		} else {
			doFollow(context, user.id, handler);
		}
	}

	public static void doFollow(Context context, String userId,
			final Handler handler) {
		Intent intent = new Intent(context, FanFouService.class);
		intent.putExtra(EXTRA_TYPE, TYPE_FRIENDSHIPS_CREATE);
		intent.putExtra(EXTRA_ID, userId);
		intent.putExtra(EXTRA_MESSENGER, new Messenger(handler));
		context.startService(intent);

	}

	public static void doUnFollow(Context context, String userId,
			final Handler handler) {
		Intent intent = new Intent(context, FanFouService.class);
		intent.putExtra(EXTRA_TYPE, TYPE_FRIENDSHIPS_DESTROY);
		intent.putExtra(EXTRA_ID, userId);
		intent.putExtra(EXTRA_MESSENGER, new Messenger(handler));
		context.startService(intent);

	}

	private void friendshipsCreate(Intent intent) {
		String id = intent.getStringExtra(EXTRA_ID);
		try {
			User u = api.friendshipsCreate(id, MODE);
			if (u == null || u.isNull()) {
				sendSuccessMessage();
			} else {
				u.type = Constants.TYPE_USERS_FRIENDS;
				getContentResolver().insert(UserInfo.CONTENT_URI,
						u.toContentValues());
				sendParcelableMessage(u);
			}
		} catch (ApiException e) {
			if (App.DEBUG) {
				e.printStackTrace();
			}
			sendErrorMessage(e);
		}
	}

	private void friendshipsDelete(Intent intent) {
		String id = intent.getStringExtra(EXTRA_ID);
		try {
			User u = api.friendshipsDelete(id, MODE);
			if (u == null || u.isNull()) {
				sendSuccessMessage();
			} else {
				u.type = TYPE_NONE;
				ContentResolver cr = getContentResolver();
				cr.delete(UserInfo.CONTENT_URI, BasicColumns.ID + "=?",
						new String[] { id });
				sendParcelableMessage(u);
				// 取消关注后要清空该用户名下的消息
				cr.delete(StatusInfo.CONTENT_URI, StatusInfo.USER_ID + "=?",
						new String[] { id });
			}
		} catch (ApiException e) {
			if (App.DEBUG) {
				e.printStackTrace();
			}
			sendErrorMessage(e);
		}
	}

	private void userShow(Intent intent) {
		String id = intent.getStringExtra(EXTRA_ID);
		try {
			User u = api.userShow(id, MODE);
			if (u == null || u.isNull()) {
				sendSuccessMessage();
			} else {
				if (!FanFouProvider.updateUserInfo(this, u)) {
					FanFouProvider.insertUserInfo(this, u);
				}
				sendParcelableMessage(u);

			}
		} catch (ApiException e) {
			if (App.DEBUG) {
				e.printStackTrace();
			}
			sendErrorMessage(e);
		}
	}

	public static void doFavorite(final Activity activity, final Status status) {
		doFavorite(activity, status, null, false);
	}

	public static void doFavorite(final Activity activity, final Status status,
			boolean finish) {
		doFavorite(activity, status, null, finish);
	}

	public static void doFavorite(final Activity activity, final Status status,
			final ResultListener li) {
		doFavorite(activity, status, li, false);
	}

	public static void doFavorite(final Activity activity, final Status status,
			final ResultListener li, final boolean finish) {
		if (status == null || status.isNull()) {
			if (App.DEBUG) {
				Log.d(TAG, "doFavorite: status is null.");
			}
			throw new NullPointerException("status cannot be null.");
		}
		final int type = status.favorited ? Constants.TYPE_FAVORITES_DESTROY
				: Constants.TYPE_FAVORITES_CREATE;

		final Handler handler = new Handler() {

			@Override
			public void handleMessage(Message msg) {
				switch (msg.what) {
				case Constants.RESULT_SUCCESS:
					Status result = (Status) msg.getData().getParcelable(
							Constants.EXTRA_DATA);
					String text = result.favorited ? "收藏成功" : "取消收藏成功";
					Utils.notify(activity.getApplicationContext(), text);
					onSuccess(li, type, text);
					if (finish) {
						activity.finish();
					}
					break;
				case Constants.RESULT_ERROR:
					String errorMessage = msg.getData().getString(
							Constants.EXTRA_ERROR);
					Utils.notify(activity.getApplicationContext(), errorMessage);
					onFailed(li, type, "收藏失败");
					break;
				default:
					break;
				}
			}
		};
		if (status.favorited) {
			FanFouService.doUnfavorite(activity, status.id, handler);
		} else {
			FanFouService.doFavorite(activity, status.id, handler);
		}
	}

	public static void doFavorite(final Activity activity, final Status s,
			final BaseAdapter adapter) {
		ActionResultHandler li = new ActionResultHandler() {
			@Override
			public void onActionSuccess(int type, String message) {
				if (type == Constants.TYPE_FAVORITES_CREATE) {
					s.favorited = true;
				} else {
					s.favorited = false;
				}
				adapter.notifyDataSetChanged();
			}
		};
		doFavorite(activity, s, li);
	}

	public static void doFavorite(final Activity activity, final Status s,
			final Cursor c) {
		ActionResultHandler li = new ActionResultHandler() {
			@Override
			public void onActionSuccess(int type, String message) {
				c.requery();
			}
		};
		doFavorite(activity, s, li);
	}

	public static void doFavorite(Context context, String id,
			final Handler handler) {
		Intent intent = new Intent(context, FanFouService.class);
		intent.putExtra(EXTRA_TYPE, TYPE_FAVORITES_CREATE);
		intent.putExtra(EXTRA_ID, id);
		intent.putExtra(EXTRA_MESSENGER, new Messenger(handler));
		context.startService(intent);
	}

	private void favoritesCreate(Intent intent) {
		String id = intent.getStringExtra(EXTRA_ID);
		String where = BasicColumns.ID + "=?";
		String[] whereArgs = new String[] { id };
		try {
			Status s = api.favoritesCreate(id, FORMAT, MODE);
			if (s == null || s.isNull()) {
				sendSuccessMessage();
			} else {
				ContentResolver cr = getContentResolver();
				ContentValues values = new ContentValues();
				values.put(StatusInfo.FAVORITED, true);
				int result = cr.update(StatusInfo.CONTENT_URI, values, where,
						whereArgs);
				FanFouProvider.updateUserInfo(this, s.user);
				sendParcelableMessage(s);
			}
		} catch (ApiException e) {
			if (App.DEBUG) {
				e.printStackTrace();
			}
			if (e.statusCode == 404) {
				Uri uri = FanFouProvider.buildUriWithStatusId(id);
				getContentResolver().delete(uri, null, null);
			}
			sendErrorMessage(e);
		}
	}

	public static void doUnfavorite(Context context, String id,
			final Handler handler) {
		Intent intent = new Intent(context, FanFouService.class);
		intent.putExtra(EXTRA_TYPE, TYPE_FAVORITES_DESTROY);
		intent.putExtra(EXTRA_ID, id);
		intent.putExtra(EXTRA_MESSENGER, new Messenger(handler));
		context.startService(intent);
	}

	private void favoritesDelete(Intent intent) {
		// 404 消息不存在
		// 404 没有通过用户验证
		String id = intent.getStringExtra(EXTRA_ID);
		String where = BasicColumns.ID + "=?";
		String[] whereArgs = new String[] { id };
		try {
			Status s = api.favoritesDelete(id, FORMAT, MODE);
			if (s == null || s.isNull()) {
				sendSuccessMessage();
			} else {
				ContentResolver cr = getContentResolver();
				ContentValues values = new ContentValues();
				values.put(StatusInfo.FAVORITED, false);
				int result = cr.update(StatusInfo.CONTENT_URI, values, where,
						whereArgs);
				FanFouProvider.updateUserInfo(this, s.user);
				sendParcelableMessage(s);
			}
		} catch (ApiException e) {
			if (App.DEBUG) {
				e.printStackTrace();
			}
			if (e.statusCode == 404) {
				Uri uri = FanFouProvider.buildUriWithStatusId(id);
				getContentResolver().delete(uri, null, null);
			}
			sendErrorMessage(e);
		}
	}

	public static void doStatusDelete(final Activity activity, final String id) {
		doStatusDelete(activity, id, null);
	}

	public static void doStatusDelete(final Activity activity, final String id,
			final ResultListener li) {
		doStatusDelete(activity, id, li, false);
	}

	public static void doStatusDelete(final Activity activity, final String id,
			final boolean finish) {
		doStatusDelete(activity, id, null, finish);
	}

	public static void doStatusDelete(final Activity activity, final String id,
			final ResultListener li, final boolean finish) {
		if (StringHelper.isEmpty(id)) {
			if (App.DEBUG) {
				Log.d(TAG, "doStatusDelete: status id is null.");
			}
			throw new NullPointerException("statusid cannot be null.");
		}
		final Handler handler = new Handler() {

			@Override
			public void handleMessage(Message msg) {
				switch (msg.what) {
				case Constants.RESULT_SUCCESS:
					Utils.notify(activity.getApplicationContext(), "删除成功");
					onSuccess(li, Constants.TYPE_STATUSES_DESTROY, "删除成功");
					if (finish && activity != null) {
						activity.finish();
					}
					break;
				case Constants.RESULT_ERROR:
					String errorMessage = msg.getData().getString(
							Constants.EXTRA_ERROR);
					Utils.notify(activity.getApplicationContext(), errorMessage);
					onFailed(li, Constants.TYPE_STATUSES_DESTROY, "删除失败");
					break;
				default:
					break;
				}
			}
		};
		FanFouService.doStatusesDelete(activity, id, handler);
	}

	public static void doStatusesDelete(Context context, String id,
			final Handler handler) {
		Intent intent = new Intent(context, FanFouService.class);
		intent.putExtra(EXTRA_TYPE, TYPE_STATUSES_DESTROY);
		intent.putExtra(EXTRA_ID, id);
		intent.putExtra(EXTRA_MESSENGER, new Messenger(handler));
		context.startService(intent);
	}

	private void statusesDestroy(Intent intent) {
		String id = intent.getStringExtra(EXTRA_ID);
		try {
			Status s = api.statusesDelete(id, FORMAT, MODE);
			if (s == null || s.isNull()) {
				sendSuccessMessage();
			} else {
				ContentResolver cr = getContentResolver();
				Uri uri = Uri.parse(StatusInfo.CONTENT_URI + "/id/" + id);
				int result = cr.delete(uri, null, null);
				sendParcelableMessage(s);
			}
		} catch (ApiException e) {
			if (App.DEBUG) {
				e.printStackTrace();
			}
			if (e.statusCode == 404) {
				Uri uri = FanFouProvider.buildUriWithStatusId(id);
				getContentResolver().delete(uri, null, null);
			}
			sendErrorMessage(e);
		}
	}

	public static void doProfile(Context context, String userId,
			final Handler handler) {
		Intent intent = new Intent(context, FanFouService.class);
		intent.putExtra(EXTRA_TYPE, TYPE_USERS_SHOW);
		intent.putExtra(EXTRA_ID, userId);
		intent.putExtra(EXTRA_MESSENGER, new Messenger(handler));
		context.startService(intent);
	}

	private void statusesShow(Intent intent) {
		String id = intent.getStringExtra(EXTRA_ID);
		try {
			Status s = api.statusesShow(id, FORMAT, MODE);
			if (s == null || s.isNull()) {
				sendSuccessMessage();
			} else {
				if (!FanFouProvider.updateUserInfo(this, s.user)) {
					FanFouProvider.insertUserInfo(this, s.user);
				}
				FanFouProvider.updateUserInfo(this, s.user);
				sendParcelableMessage(s);
			}
		} catch (ApiException e) {
			if (App.DEBUG) {
				e.printStackTrace();
			}
			if (e.statusCode == 404) {
				Uri uri = FanFouProvider.buildUriWithStatusId(id);
				getContentResolver().delete(uri, null, null);
			}
			sendErrorMessage(e);
		}

	}

	public static void doFriendshipsExists(Context context, String userA,
			String userB, final Handler handler) {
		Intent intent = new Intent(context, FanFouService.class);
		intent.putExtra(EXTRA_TYPE, TYPE_FRIENDSHIPS_EXISTS);
		intent.putExtra("user_a", userA);
		intent.putExtra("user_b", userB);
		intent.putExtra(EXTRA_MESSENGER, new Messenger(handler));
		context.startService(intent);
	}

	private void friendshipsExists(Intent intent) {
		String userA = intent.getStringExtra("user_a");
		String userB = intent.getStringExtra("user_b");
		boolean result = false;
		try {
			result = api.friendshipsExists(userA, userB);
		} catch (ApiException e) {
			if (App.DEBUG) {
				Log.e(TAG, "doDetectFriendships:" + e.getMessage());
			}
			sendErrorMessage(e);
		}
		Bundle data = new Bundle();
		data.putBoolean(EXTRA_BOOLEAN, result);
		sendSuccessMessage(data);
	}

	private void fetchUsers(Intent intent) {
		String ownerId = intent.getStringExtra(EXTRA_ID);
		int page = intent.getIntExtra(EXTRA_PAGE, 0);
		int count = intent.getIntExtra(EXTRA_COUNT, DEFAULT_USERS_COUNT);
		if (App.DEBUG)
			log("fetchFriendsOrFollowers ownerId=" + ownerId + " page=" + page);

		if (App.getApnType() == ApnType.WIFI) {
			count = MAX_USERS_COUNT;
		} else {
			count = DEFAULT_USERS_COUNT;
		}
		try {
			List<User> users = null;
			if (type == TYPE_USERS_FRIENDS) {
				users = api.usersFriends(ownerId, count, page, MODE);
			} else if (type == TYPE_USERS_FOLLOWERS) {
				users = api.usersFollowers(ownerId, count, page, MODE);
			}
			if (users != null && users.size() > 0) {

				int size = users.size();
				if (App.DEBUG) {
					log("fetchFriendsOrFollowers size=" + size);
				}
				ContentResolver cr = getContentResolver();
				if (page < 2 && ownerId != null) {
					String where = UserInfo.TYPE + "=? AND " + UserInfo.OWNER_ID + "=?";
					String[] whereArgs = new String[] { String.valueOf(type), ownerId };
					int deletedNums = cr.delete(UserInfo.CONTENT_URI, where,
							whereArgs);
					if (App.DEBUG) {
						log("fetchFriendsOrFollowers delete old rows "
								+ deletedNums+" ownerId="+ownerId);
					}
				}
				int nums = cr.bulkInsert(UserInfo.CONTENT_URI,
						Parser.toContentValuesArray(users));
				if (App.DEBUG) {
					log("fetchFriendsOrFollowers refresh ,insert rows, num="
							+ nums+" ownerId="+ownerId);
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

	private void fetchConversationList(Intent intent) {
		int count = intent.getIntExtra(EXTRA_COUNT,
				count = DEFAULT_TIMELINE_COUNT);
		if (App.getApnType() == ApnType.WIFI) {
			count = MAX_TIMELINE_COUNT;
		} else {
			count = DEFAULT_TIMELINE_COUNT;
		}
		boolean doGetMore = intent.getBooleanExtra(EXTRA_BOOLEAN, false);
		try {
			if (doGetMore) {
				sendIntMessage(fetchOldDirectMessages(count));
			} else {
				sendIntMessage(fetchNewDirectMessages(count));
			}
		} catch (ApiException e) {
			if (App.DEBUG) {
				e.printStackTrace();
			}
			sendErrorMessage(e);
		}
	}

	public static void doFetchDirectMessagesConversationList(Context context,
			final Messenger messenger, boolean doGetMore) {
		Intent intent = new Intent(context, FanFouService.class);
		intent.putExtra(EXTRA_TYPE, TYPE_DIRECT_MESSAGES_CONVERSTATION_LIST);
		intent.putExtra(EXTRA_MESSENGER, messenger);
		intent.putExtra(EXTRA_BOOLEAN, doGetMore);
		context.startService(intent);
	}

	public static void doFetchDirectMessagesInbox(Context context,
			final Messenger messenger, boolean doGetMore) {
		Intent intent = new Intent(context, FanFouService.class);
		intent.putExtra(EXTRA_TYPE, TYPE_DIRECT_MESSAGES_INBOX);
		intent.putExtra(EXTRA_MESSENGER, messenger);
		intent.putExtra(EXTRA_BOOLEAN, doGetMore);
		context.startService(intent);
	}

	private void fetchDirectMessagesInbox(Intent intent) {
		int count = intent.getIntExtra(EXTRA_COUNT,
				count = DEFAULT_TIMELINE_COUNT);
		if (App.getApnType() == ApnType.WIFI) {
			count = MAX_TIMELINE_COUNT;
		} else {
			count = DEFAULT_TIMELINE_COUNT;
		}
		boolean doGetMore = intent.getBooleanExtra(EXTRA_BOOLEAN, false);
		try {
			sendIntMessage(fetchDirectMessagesInbox(count, doGetMore));
		} catch (ApiException e) {
			if (App.DEBUG) {
				e.printStackTrace();
			}
			sendErrorMessage(e);
		}
	}

	private void fetchDirectMessagesOutbox(Intent intent) {
		int count = intent.getIntExtra(EXTRA_COUNT,
				count = DEFAULT_TIMELINE_COUNT);
		if (App.getApnType() == ApnType.WIFI) {
			count = MAX_TIMELINE_COUNT;
		} else {
			count = DEFAULT_TIMELINE_COUNT;
		}
		boolean doGetMore = intent.getBooleanExtra(EXTRA_BOOLEAN, false);
		try {
			sendIntMessage(fetchDirectMessagesOutbox(count, doGetMore));
		} catch (ApiException e) {
			if (App.DEBUG) {
				e.printStackTrace();
			}
			sendErrorMessage(e);
		}
	}

	private int fetchDirectMessagesInbox(int count, boolean doGetMore)
			throws ApiException {
		Cursor ic = initInboxMessagesCursor();
		List<DirectMessage> messages = null;
		if (doGetMore) {
			messages = api.directMessagesInbox(count, 0, null,
					Utils.getDmMaxId(ic), MODE);
		} else {
			messages = api.directMessagesInbox(count, 0,
					Utils.getDmSinceId(ic), null, MODE);
		}
		ic.close();
		if (messages != null && messages.size() > 0) {
			ContentResolver cr = getContentResolver();
			int size = messages.size();
			if (App.DEBUG) {
				log("fetchDirectMessagesInbox size()=" + size);
			}
			int nums = cr.bulkInsert(DirectMessageInfo.CONTENT_URI,
					Parser.toContentValuesArray(messages));
			return nums;
		} else {
			if (App.DEBUG) {
				log("fetchDirectMessagesInbox size()=0");
			}
		}
		return 0;
	}

	private int fetchDirectMessagesOutbox(int count, boolean doGetMore)
			throws ApiException {
		Cursor ic = initOutboxMessagesCursor();
		List<DirectMessage> messages = null;
		if (doGetMore) {
			messages = api.directMessagesOutbox(count, 0, null,
					Utils.getDmMaxId(ic), MODE);
		} else {
			messages = api.directMessagesOutbox(count, 0,
					Utils.getDmSinceId(ic), null, MODE);
		}
		ic.close();
		if (messages != null && messages.size() > 0) {
			ContentResolver cr = getContentResolver();
			int size = messages.size();
			if (App.DEBUG) {
				log("fetchDirectMessagesOutbox size()=" + size);
			}
			int nums = cr.bulkInsert(DirectMessageInfo.CONTENT_URI,
					Parser.toContentValuesArray(messages));
			return nums;
		} else {
			if (App.DEBUG) {
				log("fetchDirectMessagesOutbox size()=0");
			}
		}
		return 0;
	}

	private int fetchNewDirectMessages(int count) throws ApiException {
		Cursor ic = initInboxMessagesCursor();
		Cursor oc = initOutboxMessagesCursor();
		try {
			String inboxSinceId = Utils.getDmSinceId(ic);
			String outboxSinceId = Utils.getDmSinceId(oc);
			List<DirectMessage> messages = new ArrayList<DirectMessage>();
			List<DirectMessage> in = api.directMessagesInbox(count, 0,
					inboxSinceId, null, MODE);
			if (in != null && in.size() > 0) {
				messages.addAll(in);
			}
			List<DirectMessage> out = api.directMessagesOutbox(count, 0,
					outboxSinceId, null, MODE);
			if (out != null && out.size() > 0) {
				messages.addAll(out);
			}
			if (messages != null && messages.size() > 0) {
				ContentResolver cr = getContentResolver();
				int size = messages.size();
				if (App.DEBUG) {
					log("fetchNewDirectMessages size()=" + size);
				}
				int nums = cr.bulkInsert(DirectMessageInfo.CONTENT_URI,
						Parser.toContentValuesArray(messages));
				return nums;
			} else {
				if (App.DEBUG) {
					log("fetchNewDirectMessages size()=0");
				}
			}
		} finally {
			oc.close();
			ic.close();
		}
		return 0;
	}

	private int fetchOldDirectMessages(int count) throws ApiException {
		Cursor ic = initInboxMessagesCursor();
		Cursor oc = initOutboxMessagesCursor();
		try {
			String inboxMaxId = Utils.getDmMaxId(ic);
			String outboxMaxid = Utils.getDmMaxId(oc);
			List<DirectMessage> messages = new ArrayList<DirectMessage>();
			List<DirectMessage> in = api.directMessagesInbox(count, 0, null,
					inboxMaxId, MODE);
			if (in != null && in.size() > 0) {
				messages.addAll(in);
			}
			List<DirectMessage> out = api.directMessagesOutbox(count, 0, null,
					outboxMaxid, MODE);
			if (out != null && out.size() > 0) {
				messages.addAll(out);
			}
			if (messages != null && messages.size() > 0) {
				ContentResolver cr = getContentResolver();
				int size = messages.size();
				if (App.DEBUG) {
					log("doFetchMessagesMore size()=" + size);
				}
				int nums = cr.bulkInsert(DirectMessageInfo.CONTENT_URI,
						Parser.toContentValuesArray(messages));
				return nums;
			} else {
				if (App.DEBUG) {
					log("doFetchMessagesMore size()=0");
				}
			}
		} finally {
			oc.close();
			ic.close();
		}
		return 0;
	}

	private Cursor initInboxMessagesCursor() {
		String where = BasicColumns.TYPE + " = ? ";
		String[] whereArgs = new String[] { String
				.valueOf(TYPE_DIRECT_MESSAGES_INBOX) };
		return getContentResolver().query(DirectMessageInfo.CONTENT_URI,
				DirectMessageInfo.COLUMNS, where, whereArgs, null);
	}

	private Cursor initOutboxMessagesCursor() {
		String where = BasicColumns.TYPE + " = ? ";
		String[] whereArgs = new String[] { String
				.valueOf(TYPE_DIRECT_MESSAGES_OUTBOX) };
		return getContentResolver().query(DirectMessageInfo.CONTENT_URI,
				DirectMessageInfo.COLUMNS, where, whereArgs, null);
	}

	private void fetchTimeline(Intent intent) {
		List<Status> statuses = null;

		int page = intent.getIntExtra(EXTRA_PAGE, 0);
		String id = intent.getStringExtra(EXTRA_ID);
		String sinceId = intent.getStringExtra(EXTRA_SINCE_ID);
		String maxId = intent.getStringExtra(EXTRA_MAX_ID);

		int count = intent.getIntExtra(EXTRA_COUNT, DEFAULT_TIMELINE_COUNT);
		if (App.getApnType() == ApnType.WIFI) {
			count = MAX_TIMELINE_COUNT;
		} else {
			count = DEFAULT_TIMELINE_COUNT;
		}

		if (App.DEBUG) {
			Log.d(TAG, "fetchTimeline userId=" + id + " sinceId=" + sinceId
					+ " maxId=" + maxId + " page=" + page + " count=" + count);
		}

		try {
			switch (type) {
			case TYPE_STATUSES_HOME_TIMELINE:
				if (App.DEBUG)
					Log.d(TAG, "fetchTimeline TYPE_HOME userId=" + id);
				statuses = api.homeTimeline(count, page, sinceId, maxId,
						FORMAT, MODE);

				break;
			case TYPE_STATUSES_MENTIONS:
				if (App.DEBUG)
					Log.d(TAG, "fetchTimeline TYPE_MENTION userId=" + id);
				statuses = api.mentions(count, page, sinceId, maxId, FORMAT,
						MODE);
				break;
			case TYPE_STATUSES_PUBLIC_TIMELINE:
				count = DEFAULT_TIMELINE_COUNT;
				if (App.DEBUG)
					Log.d(TAG, "fetchTimeline TYPE_PUBLIC userId=" + id);
				statuses = api.pubicTimeline(count, FORMAT, MODE);
				break;
			case TYPE_FAVORITES_LIST:
				if (App.DEBUG)
					Log.d(TAG, "fetchTimeline TYPE_FAVORITES userId=" + id);
				statuses = api.favorites(count, page, id, FORMAT, MODE);
				break;
			case TYPE_STATUSES_USER_TIMELINE:
				if (App.DEBUG)
					Log.d(TAG, "fetchTimeline TYPE_USER userId=" + id);
				statuses = api.userTimeline(count, page, id, sinceId, maxId,
						FORMAT, MODE);
				break;
			case TYPE_STATUSES_CONTEXT_TIMELINE:
				if (App.DEBUG)
					Log.d(TAG, "fetchTimeline TYPE_CONTEXT userId=" + id);
				statuses = api.contextTimeline(id, FORMAT, MODE);
				break;
			default:
				break;
			}
			if (statuses == null || statuses.size() == 0) {
				sendIntMessage(0);
				if (App.DEBUG)
					Log.d(TAG, "fetchTimeline received no items. userId=" + id);
				return;
			} else {
				int size = statuses.size();
				if (App.DEBUG) {
					Log.d(TAG, "fetchTimeline received items count=" + size
							+ " userId=" + id);
				}
				ContentResolver cr = getContentResolver();
				if (size >= count && page <= 1 && maxId == null) {
					String where = BasicColumns.TYPE + " = ? ";
					String[] whereArgs = new String[] { String.valueOf(type) };
					if (type == TYPE_STATUSES_USER_TIMELINE) {
						where = BasicColumns.TYPE + " = ? AND "
								+ StatusInfo.USER_ID + " =? ";
						whereArgs = new String[] { String.valueOf(type), id };
					} else if (type == TYPE_FAVORITES_LIST) {
						where = BasicColumns.TYPE + " = ? AND "
								+ StatusInfo.OWNER_ID + " =? ";
						whereArgs = new String[] { String.valueOf(type), id };
					}
					int delete = cr.delete(StatusInfo.CONTENT_URI, where,
							whereArgs);
					if (App.DEBUG) {
						Log.d(TAG, "fetchTimeline items count = " + count
								+ " ,remove " + delete
								+ " old statuses. userId=" + id+" type="+type);
					}
				}
				int insertedCount = cr.bulkInsert(StatusInfo.CONTENT_URI,
						Parser.toContentValuesArray(statuses));
				sendIntMessage(insertedCount);
				updateUsersFromStatus(statuses, type);
			}
		} catch (ApiException e) {
			if (App.DEBUG) {
				log("fetchTimeline [error]" + e.statusCode + ":"
						+ e.errorMessage + " userId=" + id);
				e.printStackTrace();
			}
			sendErrorMessage(e);
		}
	}

	private int updateUsersFromStatus(List<Status> statuses, int type) {
		if (type == TYPE_STATUSES_USER_TIMELINE || type == TYPE_FAVORITES_LIST) {
			return 0;
		}
		ArrayList<User> us = new ArrayList<User>();
		for (Status s : statuses) {
			User u = s.user;
			if (u != null) {
				if (!FanFouProvider.updateUserInfo(this, u)) {
					if (App.DEBUG) {
						log("extractUsers from status list , udpate failed, insert it");
					}
					us.add(s.user);
				}
			}
		}

		int result = 0;
		if (us.size() > 0) {
			result = getContentResolver().bulkInsert(UserInfo.CONTENT_URI,
					Parser.toContentValuesArray(us));
			if (App.DEBUG) {
				log("extractUsers from status list , insert result=" + result);
			}
		}
		return result;
	}

	public static void doFetchHomeTimeline(Context context,
			final Messenger messenger, String sinceId, String maxId) {
		doFetchTimeline(context, TYPE_STATUSES_HOME_TIMELINE, messenger, 0,
				null, sinceId, maxId);
	}

	public static void doFetchMentions(Context context,
			final Messenger messenger, String sinceId, String maxId) {
		doFetchTimeline(context, TYPE_STATUSES_MENTIONS, messenger, 0, null,
				sinceId, maxId);
	}

	public static void doFetchUserTimeline(Context context,
			final Messenger messenger, String userId, String sinceId,
			String maxId) {
		doFetchTimeline(context, TYPE_STATUSES_USER_TIMELINE, messenger, 0,
				userId, sinceId, maxId);
	}

	public static void doFetchPublicTimeline(Context context,
			final Messenger messenger) {
		doFetchTimeline(context, TYPE_STATUSES_PUBLIC_TIMELINE, messenger, 0,
				null, null, null);
	}

	public static void doFetchFavorites(Context context,
			final Messenger messenger, int page, String userId) {
		doFetchTimeline(context, TYPE_FAVORITES_LIST, messenger, page, userId,
				null, null);
	}

	private static void doFetchTimeline(Context context, int type,
			final Messenger messenger, int page, String userId, String sinceId,
			String maxId) {
		Intent intent = new Intent(context, FanFouService.class);
		intent.putExtra(EXTRA_TYPE, type);
		intent.putExtra(EXTRA_MESSENGER, messenger);
		intent.putExtra(EXTRA_COUNT, MAX_TIMELINE_COUNT);
		intent.putExtra(EXTRA_PAGE, page);
		intent.putExtra(EXTRA_ID, userId);
		intent.putExtra(EXTRA_SINCE_ID, sinceId);
		intent.putExtra(EXTRA_MAX_ID, maxId);
		if (App.DEBUG) {
			Log.d(TAG, "doFetchTimeline() type=" + type + " page=" + page
					+ " userId=" + userId);
		}
		context.startService(intent);
	}

	public static void doFetchFriends(Context context, final Handler handler,
			int page, String userId) {
		doFetchUsers(context, TYPE_USERS_FRIENDS, handler, page, userId);
	}

	public static void doFetchFollowers(Context context, final Handler handler,
			int page, String userId) {
		doFetchUsers(context, TYPE_USERS_FOLLOWERS, handler, page, userId);
	}

	private static void doFetchUsers(Context context, int type,
			final Handler handler, int page, String userId) {
		Intent intent = new Intent(context, FanFouService.class);
		intent.putExtra(EXTRA_TYPE, type);
		intent.putExtra(EXTRA_MESSENGER, new Messenger(handler));
		intent.putExtra(EXTRA_COUNT, MAX_USERS_COUNT);
		intent.putExtra(EXTRA_PAGE, page);
		intent.putExtra(EXTRA_ID, userId);
		context.startService(intent);
	}

	private void sendErrorMessage(ApiException e) {
		String message = e.getMessage();
		if (e.statusCode == ResponseCode.ERROR_IO_EXCEPTION) {
			message = getString(R.string.msg_connection_error);
		} else if (e.statusCode >= 500) {
			message = getString(R.string.msg_server_error);
		}
		Bundle bundle = new Bundle();
		bundle.putInt(EXTRA_CODE, e.statusCode);
		bundle.putString(EXTRA_ERROR, message);
		sendMessage(RESULT_ERROR, bundle);
	}

	private void sendIntMessage(int size) {
		Bundle bundle = new Bundle();
		bundle.putInt(EXTRA_COUNT, size);
		sendMessage(RESULT_SUCCESS, bundle);
	}

	private void sendParcelableMessage(Parcelable parcel) {
		Bundle bundle = new Bundle();
		bundle.putParcelable(EXTRA_DATA, parcel);
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

	private static void onSuccess(ResultListener li, int type, String message) {
		if (li != null) {
			li.onActionSuccess(type, message);
		}
	}

	private static void onFailed(ResultListener li, int type, String message) {
		if (li != null) {
			li.onActionFailed(type, message);
		}
	}

}
