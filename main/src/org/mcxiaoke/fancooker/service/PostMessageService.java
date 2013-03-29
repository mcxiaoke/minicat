package org.mcxiaoke.fancooker.service;

import org.mcxiaoke.fancooker.App;
import org.mcxiaoke.fancooker.R;
import org.mcxiaoke.fancooker.api.Api;
import org.mcxiaoke.fancooker.api.ApiException;
import org.mcxiaoke.fancooker.controller.DataController;
import org.mcxiaoke.fancooker.dao.model.DirectMessageModel;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.Messenger;
import android.os.RemoteException;
import android.util.Log;

/**
 * @author mcxiaoke
 * @version 1.0 2011.06.25
 * @version 1.1 2011.10.25
 * @version 2.0 2011.11.18
 * @version 2.1 2011.11.21
 * @version 2.2 2011.12.13
 * @version 2.3 2012.02.22
 * @version 2.4 2012.02.24
 * @version 2.5 2012.03.28
 * 
 */
public class PostMessageService extends BaseIntentService {

	public static final int RESULT_SUCCESS = 1;
	public static final int RESULT_ERROR = -1;

	private static final String TAG = PostMessageService.class.getSimpleName();
	private NotificationManager nm;

	public void log(String message) {
		Log.i(TAG, message);
	}

	private String text;
	private String userId;

	private Messenger messenger;

	public PostMessageService() {
		super("UpdateService");

	}

	@Override
	protected void onHandleIntent(Intent intent) {
		if (intent == null) {
			return;
		}
		parseIntent(intent);
		this.nm = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
		doSend();
	}

	private void parseIntent(Intent intent) {
		messenger = intent.getParcelableExtra("messenger");
		userId = intent.getStringExtra("id");
		text = intent.getStringExtra("text");
		if (App.DEBUG) {
			log("parseIntent userId=" + userId);
			log("parseIntent content=" + text);
		}
	}

	private boolean doSend() {
		showSendingNotification();
		boolean res = true;
		Api api = App.getApi();
		try {
			DirectMessageModel model = api.createDirectmessage(userId, text,
					null);

			nm.cancel(10);
			if (model == null) {
				res = false;
			} else {
				if (model.getRecipientId().equals(App.getAccount())) {
					model.setIncoming(true);
					model.setConversationId(model.getSenderId());
				} else {
					model.setIncoming(false);
					model.setConversationId(model.getRecipientId());
				}
				DataController.store(this, model);
				res = true;
				sendMessage(RESULT_SUCCESS, null);
			}
		} catch (ApiException e) {
			nm.cancel(10);
			if (App.DEBUG) {
				Log.e(TAG,
						"error: code=" + e.statusCode + " msg="
								+ e.getMessage());
			}
			sendErrorMessage(e);
		} finally {
			nm.cancel(12);
		}
		return res;
	}

	private int showSendingNotification() {
		int id = 10;
		Notification notification = new Notification(R.drawable.ic_stat_notify,
				"饭否私信正在发送...", System.currentTimeMillis());
		PendingIntent contentIntent = PendingIntent.getActivity(this, 0,
				new Intent(), 0);
		notification.setLatestEventInfo(this, "饭否私信", "正在发送...", contentIntent);
		notification.flags |= Notification.FLAG_ONGOING_EVENT;
		nm.notify(id, notification);
		return id;
	}

	private void sendMessage(int what, final Bundle bundle) {
		if (messenger == null) {
			return;
		}
		Message m = Message.obtain();
		m.what = what;
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

	public static void send(Context context, final Handler handler, String id,
			String text) {
		Intent intent = new Intent(context, PostMessageService.class);
		intent.putExtra("messenger", new Messenger(handler));
		intent.putExtra("id", id);
		intent.putExtra("text", text);
		context.startService(intent);
	}

}
