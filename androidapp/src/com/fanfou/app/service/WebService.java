package com.fanfou.app.service;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;

import com.fanfou.app.App;
import com.fanfou.app.api.Api;
import com.fanfou.app.api.ApiException;
import com.fanfou.app.api.FanFouApiConfig;
import com.fanfou.app.api.User;
import com.fanfou.app.config.Commons;

/**
 * @author mcxiaoke
 * @version 1.0 2011.11.21
 * 
 */
public class WebService extends Service implements IWebService {

	public static final int STATUS_CREATE = 0;
	public static final int STATUS_SHOW = 1;
	public static final int STATUS_DELETE = 2;
	public static final int STATUS_FAVORITE = 3;
	public static final int STATUS_UNFAVORITE = 4;

	public static final int USER_FOLLOW = 21;
	public static final int USER_UNFOLLOW = 22;
	public static final int USER_BLOCK = 23;
	public static final int USER_UNBLOCK = 24;

	public static final int MESSAGE_CREATE = 31;
	public static final int MESSAGE_DELETE = 32;

	public static final int TIMELINE_HOME = 51;
	public static final int TIMELINE_MENTION = 52;
	public static final int TIMELINE_PUBLIC = 53;
	public static final int TIMELINE_USER = 54;
	public static final int TIMELINE_FAVORITES = 55;
	public static final int TIMELINE_CONTEXT = 56;

	public static final int MESSAGES_INBOX = 71;
	public static final int MESSAGES_OUTBOX = 72;
	public static final int MESSAGES_ALL = 73;

	private static final int WHAT_SUCCESS = 0;
	private static final int WHAT_FAILED = 1;

	private final ExecutorService mExecutorService = Executors
			.newCachedThreadPool();
	private final BlockingQueue<String> mBlockingQueue = new LinkedBlockingQueue<String>();
	private final Binder mBinder = new ServiceBinder();
	private final Handler mHandler = new ServiceHandler();
	private Api mApi;
	private WebService instance;

	@Override
	public void onCreate() {
		super.onCreate();
		instance = this;
		initialize();
	}

	private void initialize() {
		mApi = App.getApi();
	}

	@Override
	public void onDestroy() {
		mExecutorService.shutdown();
		mBlockingQueue.clear();
		super.onDestroy();
	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		super.onStartCommand(intent, flags, startId);
		return START_NOT_STICKY;
	}

	@Override
	public IBinder onBind(Intent intent) {
		return mBinder;
	}

	private class ServiceHandler extends Handler {

		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
		}

	}

	public class ServiceBinder extends Binder {
		WebService getService() {
			return WebService.this;
		}
	}

	public interface IWebServiceCallback {

		public void onSuccess(Bundle bundle);

		public void onFailed(int code, String message);
	}

	@Override
	public void friendshipsCreate(final String id,
			final IWebServiceCallback callback) {
		final Handler handler = new Handler() {
			@Override
			public void handleMessage(Message msg) {
				int id = msg.what;
				switch (id) {
				case WHAT_SUCCESS:

					break;
				case WHAT_FAILED:
				default:
					break;
				}
			}

		};

		final Runnable task = new Runnable() {

			@Override
			public void run() {
				User user;
				try {
					user = mApi
							.friendshipsCreate(id, FanFouApiConfig.MODE_LITE);
					if (user == null || user.isNull()) {
						callback.onFailed(0, "操作不成功");
					} else {
						Bundle bundle = new Bundle();
						bundle.putParcelable(Commons.EXTRA_USER, user);
						callback.onSuccess(bundle);
					}
				} catch (ApiException e) {
					callback.onFailed(e.statusCode, e.getMessage());
					if (App.DEBUG) {
						e.printStackTrace();
					}
				}

			}
		};
		mExecutorService.submit(task);
	}

	@Override
	public void friendshipsDelete(String id, final IWebServiceCallback callback) {
	}

	@Override
	public void favoritesCreate(String id, final IWebServiceCallback callback) {
	}

	@Override
	public void favoritesDelete(String id, final IWebServiceCallback callback) {
	}

}
