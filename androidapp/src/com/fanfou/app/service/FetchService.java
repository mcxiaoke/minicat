package com.fanfou.app.service;

import java.util.ArrayList;
import java.util.List;

import android.content.ContentResolver;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.ResultReceiver;
import android.util.Log;

import com.fanfou.app.App;
import com.fanfou.app.api.Api;
import com.fanfou.app.api.ApiException;
import com.fanfou.app.api.DirectMessage;
import com.fanfou.app.api.Parser;
import com.fanfou.app.api.Status;
import com.fanfou.app.api.User;
import com.fanfou.app.config.Commons;
import com.fanfou.app.db.Contents.BasicColumns;
import com.fanfou.app.db.Contents.DirectMessageInfo;
import com.fanfou.app.db.Contents.StatusInfo;
import com.fanfou.app.db.Contents.UserInfo;
import com.fanfou.app.http.ResponseCode;
import com.fanfou.app.util.Utils;

/**
 * @author mcxiaoke
 * @version 1.0 20110602
 * @version 2.0 20110714
 * @version 2.1 2011.10.10
 * @version 3.0 2011.10.20
 * @version 3.1 2011.10.21
 * @version 3.2 2011.10.24
 * @version 3.3 2011.10.28
 * 
 */
public class FetchService extends BaseIntentService {
	private static final String TAG = FetchService.class.getSimpleName();

	ResultReceiver receiver;

	public void log(String message) {
		Log.d(TAG, message);
	}

	private int mType;
	private Bundle mBundle;

	public FetchService() {
		super("FetchService");
	}

	@Override
	protected void onHandleIntent(Intent intent) {

		mType = intent.getIntExtra(Commons.EXTRA_TYPE, -1);
		mBundle = intent.getBundleExtra(Commons.EXTRA_BUNDLE);
		receiver = intent.getParcelableExtra(Commons.EXTRA_RECEIVER);
		if (receiver != null) {
			receiver.send(Commons.RESULT_CODE_START, null);
		}
		switch (mType) {
		case Status.TYPE_USER:
		case Status.TYPE_FAVORITES:
		case Status.TYPE_HOME:
		case Status.TYPE_MENTION:
		case Status.TYPE_PUBLIC:
			doFetchStatuses();
			break;
		// case DirectMessage.TYPE_IN:
		// doFetchMessages();
		// break;
		// case DirectMessage.TYPE_OUT:
		// doFetchMessages();
		// break;
		case DirectMessage.TYPE_ALL:
			doFetchMessages();
			break;
		case User.TYPE_FRIENDS:
		case User.TYPE_FOLLOWERS:
			doFetchUsers(mBundle);
			break;
		default:
			break;
		}
	}

	private void cleanUsers(String userId, int type) {
		ContentResolver cr = getContentResolver();
		String where = BasicColumns.OWNER_ID + "=? AND " + BasicColumns.TYPE
				+ "=? ";
		String[] whereArgs = new String[] { userId, String.valueOf(type) };
		int result = cr.delete(UserInfo.CONTENT_URI, where, whereArgs);
		if (App.DEBUG)
			log("cleanUsers ownerId=" + userId + " type=" + mType + " result="
					+ result);
	}

	private void doFetchUsers(Bundle bundle) {
		String ownerId = bundle.getString(Commons.EXTRA_ID);
		int page = bundle.getInt(Commons.EXTRA_PAGE);
		if (App.DEBUG)
			log("doFetchUsers ownerId=" + ownerId + " page=" + page);

		Api api = App.me.api;
		try {
			List<User> users = null;
			if (mType == User.TYPE_FRIENDS) {
				users = api.usersFriends(ownerId, page);
			} else if (mType == User.TYPE_FOLLOWERS) {
				users = api.usersFollowers(ownerId, page);
			}
			if (users != null && users.size() > 0) {
				int size = users.size();
				if (App.DEBUG)
					log("doFetchUsers size=" + size);
				ContentResolver cr = getContentResolver();
				int count=cr.bulkInsert(UserInfo.CONTENT_URI,
						Parser.toContentValuesArray(users));
				sendCountMessage(count);
			} else {
				sendCountMessage(0);
			}
		} catch (ApiException e) {
			if (App.DEBUG) {
				e.printStackTrace();
			}
			handleError(e);
		}
	}

	private void sendCountMessage(int size) {
		Bundle update = new Bundle();
		update.putInt(Commons.EXTRA_TYPE, mType);
		update.putInt(Commons.EXTRA_COUNT, size);
		receiver.send(Commons.RESULT_CODE_FINISH, update);
	}

	private void doFetchMessages() {
		boolean doGetMore = mBundle.getBoolean(Commons.EXTRA_BOOLEAN);
		if (doGetMore) {
			sendCountMessage(doFetchMessagesMore());
		} else {
			sendCountMessage(doFetchMessagesRefresh());
		}
	}

	private int doFetchMessagesRefresh() {
		Api api = App.me.api;
		Cursor ic = initMessagesCursor(false);
		Cursor oc = initMessagesCursor(true);
		try {
			String inboxSinceId = Utils.getDmSinceId(ic);
			String outboxSinceId = Utils.getDmSinceId(oc);
			List<DirectMessage> messages = new ArrayList<DirectMessage>();
			List<DirectMessage> in = api
					.messagesInbox(0, 0, inboxSinceId, null);
			if (in != null && in.size() > 0) {
				messages.addAll(in);
			}
			List<DirectMessage> out = api.messagesOutbox(0, 0, outboxSinceId,
					null);
			if (out != null && out.size() > 0) {
				messages.addAll(out);
			}
			if (messages != null && messages.size() > 0) {
				ContentResolver cr = getContentResolver();
				int size = messages.size();
				log("doFetchMessagesRefresh size()=" + size);
				int count=cr.bulkInsert(DirectMessageInfo.CONTENT_URI,
						Parser.toContentValuesArray(messages));
				sendCountMessage(count);
				return size;
			} else {
				log("doFetchMessagesRefresh size()=0");
				sendCountMessage(0);
			}
		} catch (ApiException e) {
			if (App.DEBUG) {
				e.printStackTrace();
			}
			handleError(e);
		} finally {
			oc.close();
			ic.close();
		}
		return 0;
	}

	private int doFetchMessagesMore() {
		Api api = App.me.api;
		Cursor ic = initMessagesCursor(false);
		Cursor oc = initMessagesCursor(true);
		try {
			String inboxMaxId = Utils.getDmMaxId(ic);
			String outboxMaxid = Utils.getDmMaxId(oc);
			List<DirectMessage> messages = new ArrayList<DirectMessage>();
			List<DirectMessage> in = api.messagesInbox(0, 0, null, inboxMaxId);
			if (in != null && in.size() > 0) {
				messages.addAll(in);
			}
			List<DirectMessage> out = api.messagesOutbox(0, 0, null,
					outboxMaxid);
			if (out != null && out.size() > 0) {
				messages.addAll(out);
			}
			if (messages != null && messages.size() > 0) {
				ContentResolver cr = getContentResolver();
				int size = messages.size();
				log("doFetchMessagesMore size()=" + size);
				cr.bulkInsert(DirectMessageInfo.CONTENT_URI,
						Parser.toContentValuesArray(messages));
				sendCountMessage(size);
				return size;
			} else {
				log("doFetchMessagesMore size()=0");
				sendCountMessage(0);
			}
		} catch (ApiException e) {
			if (App.DEBUG) {
				e.printStackTrace();
			}
			handleError(e);
		} finally {
			oc.close();
			ic.close();
		}
		return 0;
	}

	private Cursor initMessagesCursor(final boolean outbox) {
		String where = BasicColumns.TYPE + " = ? ";
		String[] whereArgs = new String[] { String
				.valueOf(outbox ? DirectMessage.TYPE_OUT
						: DirectMessage.TYPE_IN) };
		return getContentResolver().query(DirectMessageInfo.CONTENT_URI,
				DirectMessageInfo.COLUMNS, where, whereArgs, null);
	}

	@SuppressWarnings("unused")
	private void cleanStatuses(String userId) {
		Uri cleanUri = Uri.withAppendedPath(StatusInfo.CONTENT_URI,
				"action/clean/" + mType);
		String where = null;
		String[] whereArgs = null;
		if (mType == Status.TYPE_USER) {
			where = StatusInfo.USER_ID + "=?";
			whereArgs = new String[] { userId };
		} else if (mType == Status.TYPE_FAVORITES) {
			where = BasicColumns.OWNER_ID + "=?";
			whereArgs = new String[] { userId };
		}
		int result = getContentResolver().delete(cleanUri, where, whereArgs);
		if (App.DEBUG)
			log("cleanStatuses() deleted statuses count=" + result + " userId="
					+ userId);
	}

	private void cleanStatuses() {
		Uri cleanUri = Uri.withAppendedPath(StatusInfo.CONTENT_URI,
				"action/clean/" + mType);
		int result = getContentResolver().delete(cleanUri, null, null);
		if (App.DEBUG)
			log("cleanStatuses() deleted statuses count=" + result);
	}

	private void doFetchStatuses() {
		if (App.DEBUG)
			Log.d(TAG, "doFetchStatuses");
		Api api = App.me.api;
		List<Status> statuses = null;
		int count = mBundle.getInt(Commons.EXTRA_COUNT);
		int page = mBundle.getInt(Commons.EXTRA_PAGE);
		String userId = mBundle.getString(Commons.EXTRA_ID);
		String sinceId = mBundle.getString(Commons.EXTRA_SINCE_ID);
		String maxId = mBundle.getString(Commons.EXTRA_MAX_ID);
		boolean format = mBundle.getBoolean(Commons.EXTRA_FORMAT, true);
		format = true;
		try {
			switch (mType) {
			case Status.TYPE_HOME:
				if (App.DEBUG)
					Log.d(TAG, "doFetchStatuses TYPE_HOME");
				statuses = api
						.homeTimeline(count, page, sinceId, maxId, format);

				break;
			case Status.TYPE_MENTION:
				if (App.DEBUG)
					Log.d(TAG, "doFetchStatuses TYPE_MENTION");
				statuses = api.mentions(count, page, sinceId, maxId, format);
				break;
			case Status.TYPE_PUBLIC:
				if (App.DEBUG)
					Log.d(TAG, "doFetchStatuses TYPE_PUBLIC");
				statuses = api.pubicTimeline(count, format);
				break;
			case Status.TYPE_FAVORITES:
				if (App.DEBUG)
					Log.d(TAG, "doFetchStatuses TYPE_FAVORITES");
				statuses = api.favorites(count, page, userId, format);
				break;
			case Status.TYPE_USER:
				if (App.DEBUG)
					Log.d(TAG, "doFetchStatuses TYPE_USER");
				statuses = api.userTimeline(count, page, userId, sinceId,
						maxId, format);
				break;
			default:
				break;
			}
			if (statuses == null || statuses.size() == 0) {
				sendCountMessage(0);
				if (App.DEBUG)
					Log.d(TAG, "doFetchStatuses received no items.");
				return;
			} else {
				int size = statuses.size();
				if (App.DEBUG) {
					Log.d(TAG, "doFetchStatuses received items count=" + size);
				}
				ContentResolver cr = getContentResolver();
				
				// add at 2011.10.21
				// if count=20, clear old statuses.
				if (size == 20 && maxId == null) {
					String where = BasicColumns.TYPE + " = ?";
					String[] whereArgs = new String[] { String.valueOf(mType) };
					cr.delete(StatusInfo.CONTENT_URI, where, whereArgs);
					if (App.DEBUG) {
						Log.d(TAG,
								"doFetchStatuses items count = 20 ,remove old statuses.");
					}
				}	
				
				int insertedCount=cr.bulkInsert(StatusInfo.CONTENT_URI,
						Parser.toContentValuesArray(statuses));
				sendCountMessage(insertedCount);
				
				// extract users and insert to db, replace original object.
				extractUsers(statuses, mType);
			}
		} catch (ApiException e) {
			if (App.DEBUG) {
				log("doFetchStatuses [error]" + e.statusCode + ":"
						+ e.errorMessage);
				e.printStackTrace();
			}
			handleError(e);
		}
	}
	
	// add at 2011.10.28
	private int extractUsers(List<Status> ss, int type){
		int result=0;
		boolean isFriends=type==Status.TYPE_HOME;
		ArrayList<User> us=new ArrayList<User>();
		for (Status s : ss) {
			User u=s.user;
			if(u!=null){
				if(isFriends){
					u.type=User.TYPE_FRIENDS;
				}
				us.add(s.user);
			}
		}
		result=getContentResolver().bulkInsert(UserInfo.CONTENT_URI, Parser.toContentValuesArray(us));
		if(App.DEBUG){
			log("extractUsers from status list , result="+result+" isFriends="+isFriends);
		}
		return result;
	}

	private void handleError(ApiException e) {
		String message=e.getMessage();
		if(e.statusCode==ResponseCode.ERROR_NOT_CONNECTED||e.statusCode>=500){
			message="网络连接异常，请稍后重试";
		}
		Bundle b = new Bundle();
		b.putInt(Commons.EXTRA_ERROR_CODE, e.statusCode);
		b.putString(Commons.EXTRA_ERROR_MESSAGE, message);
		receiver.send(Commons.RESULT_CODE_ERROR, b);
	}

}
