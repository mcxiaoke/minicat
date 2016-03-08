package com.mcxiaoke.minicat.service;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.Messenger;
import android.os.RemoteException;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationCompat.Builder;
import android.util.Log;
import com.mcxiaoke.minicat.AppContext;
import com.mcxiaoke.minicat.R;
import com.mcxiaoke.minicat.api.Api;
import com.mcxiaoke.minicat.api.ApiException;
import com.mcxiaoke.minicat.controller.DataController;
import com.mcxiaoke.minicat.dao.model.DirectMessageModel;
import com.mcxiaoke.minicat.util.UmengHelper;

/**
 * @author mcxiaoke
 * @version 2.5 2012.03.28
 */
public class PostMessageService extends BaseIntentService {

    public static final int RESULT_SUCCESS = 1;
    public static final int RESULT_ERROR = -1;

    private static final String TAG = PostMessageService.class.getSimpleName();
    private NotificationManager nm;
    private String text;
    private String userId;
    private Messenger messenger;

    public PostMessageService() {
        super("UpdateService");

    }

    public static void send(Context context, final Handler handler, String id,
                            String text) {
        Intent intent = new Intent(context, PostMessageService.class);
        intent.putExtra("messenger", new Messenger(handler));
        intent.putExtra("id", id);
        intent.putExtra("text", text);
        context.startService(intent);
    }

    public void log(String message) {
        Log.i(TAG, message);
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
        if (AppContext.DEBUG) {
            log("parseIntent userId=" + userId);
            log("parseIntent content=" + text);
        }
    }

    private boolean doSend() {
        showSendingNotification();
        boolean res = true;
        Api api = AppContext.getApi();
        try {
            DirectMessageModel model = api.createDirectmessage(userId, text,
                    null);

            nm.cancel(10);
            if (model == null) {
                res = false;
            } else {
                if (model.getRecipientId().equals(AppContext.getAccount())) {
                    model.setIncoming(true);
                    model.setConversationId(model.getSenderId());
                } else {
                    model.setIncoming(false);
                    model.setConversationId(model.getRecipientId());
                }
                DataController.store(this, model);
                res = true;
                sendMessage(RESULT_SUCCESS, null);
                UmengHelper.onSendDMEvent(this);
            }
        } catch (ApiException e) {
            nm.cancel(10);
            if (AppContext.DEBUG) {
                Log.e(TAG,
                        "error: code=" + e.statusCode + " msg="
                                + e.getMessage()
                );
            }
            sendErrorMessage(e);
        } catch (Exception e) {
            sendErrorMessage(new ApiException(ApiException.IO_ERROR, e));
        } finally {
            nm.cancel(12);
        }
        return res;
    }

    private int showSendingNotification() {
        int id = 10;
        final NotificationCompat.Builder builder = new Builder(this);
        builder.setSmallIcon(R.drawable.ic_stat_app);
        builder.setTicker("饭否私信正在发送...");
        builder.setContentText("饭否私信");
        builder.setSubText("正在发送...");
        builder.setWhen(System.currentTimeMillis());
        builder.setContentIntent(PendingIntent.getActivity(this, 0,
                new Intent(), 0));
        builder.setOngoing(true);
        nm.notify(id, builder.build());
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
            if (AppContext.DEBUG) {
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

}
