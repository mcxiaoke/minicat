package com.fanfou.app.service;

import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.List;

import com.fanfou.app.App;
import com.fanfou.app.R;
import com.fanfou.app.api.ApiException;
import com.fanfou.app.api.DirectMessage;
import com.fanfou.app.api.Parser;
import com.fanfou.app.api.Status;
import com.fanfou.app.config.Commons;
import com.fanfou.app.db.Contents.BasicColumns;
import com.fanfou.app.db.Contents.DirectMessageInfo;
import com.fanfou.app.db.Contents.StatusInfo;
import com.fanfou.app.util.OptionHelper;
import com.fanfou.app.util.Utils;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.PowerManager;
import android.util.Log;

/**
 * @author mcxiaoke
 * @version 1.0 20110920
 * 
 */
public class NotificationService extends BaseIntentService {
	private static final String TAG = NotificationService.class.getSimpleName();

	public static final String ACTION_NOTIFICATION = "com.fanfou.app.action.NOTIFICATION";
	public static final int NOTIFICATION_TYPE_HOME = 3;
	public static final int NOTIFICATION_TYPE_ALL = 2;// 私信和@消息
	public static final int NOTIFICATION_TYPE_MENTION = 1; // @消息
	public static final int NOTIFICATION_TYPE_DM = 0; // 私信

	private PowerManager.WakeLock mWakeLock;

	public NotificationService() {
		super("NotificationService");
	}

	@Override
	public void onCreate() {
		super.onCreate();
		PowerManager pm = (PowerManager) getSystemService(Context.POWER_SERVICE);
		mWakeLock = pm.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, TAG);
		mWakeLock.acquire();
	}

	@Override
	public void onDestroy() {
		mWakeLock.release();
		super.onDestroy();
	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		return super.onStartCommand(intent, flags, startId);
	}

	@Override
	protected void onHandleIntent(Intent intent) {
		if (App.DEBUG) {
			Log.i(TAG, "onHandleIntent");
		}
		try {
			handleStatus();
		} catch (ApiException e) {
			if (App.DEBUG) {
				Log.e(TAG, "error code=" + e.statusCode + " error message="
						+ e.errorMessage);
				e.printStackTrace();
			}
		}
	}

	private void handleStatus() throws ApiException {
		Cursor mc = initCursor(Status.TYPE_MENTION);
		List<Status> ss = App.me.api.mentions(0, 0, Utils.getSinceId(mc), null,
				true);
		if (ss != null) {
			int size = ss.size();
			if (size > 0) {
				getContentResolver().bulkInsert(StatusInfo.CONTENT_URI,
						Parser.toContentValuesArray(ss));
				if(size==1){
					notifyStatusOne(NOTIFICATION_TYPE_MENTION,ss.get(0));
				}else{
					notifyStatusList(NOTIFICATION_TYPE_MENTION,size);
				}
			}
		}
		mc.close();
	}
	
	private void notifyStatusOne(int type, Status status){
		sendStatusNotification(type, 1, status);
	}
	
	private void notifyStatusList(int type, int count){
		sendStatusNotification(type, count, null);
	}
	
	private void notifyDmOne(){
		
	}
	
	private void notifyDmList(){
		
	}

	private Cursor initCursor(int type) {
		String where = BasicColumns.TYPE + "=?";
		String[] whereArgs = new String[] { String.valueOf(type) };
		Uri uri = StatusInfo.CONTENT_URI;
		String[] columns = StatusInfo.COLUMNS;
		if (type == DirectMessage.TYPE_NONE) {
			uri = DirectMessageInfo.CONTENT_URI;
			columns = DirectMessageInfo.COLUMNS;
		}
		return getContentResolver().query(uri, columns, where, whereArgs, null);
	}

	private void sendStatusNotification(int type, int count ,Status status) {
		if (App.DEBUG) {
			Log.i(TAG, "sendStatusNotification type="+type+" count="+count+" status="+(status==null?"null":status));
		}
		Intent intent = new Intent();
		intent.putExtra(Commons.EXTRA_TYPE, type);
		intent.putExtra(Commons.EXTRA_COUNT, count);
		intent.putExtra(Commons.EXTRA_STATUS, status);
		intent.setAction(ACTION_NOTIFICATION);
		sendOrderedBroadcast(intent, null);
	}
	
	private void sendMessageNotification(int type, int count ,DirectMessage dm) {
		if (App.DEBUG) {
			Log.i(TAG, "sendNotificationBroadcast");
		}
		Intent intent = new Intent();
		intent.setAction(ACTION_NOTIFICATION);
		sendOrderedBroadcast(intent, null);
	}

}
