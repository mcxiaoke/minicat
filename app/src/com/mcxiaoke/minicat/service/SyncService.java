package com.mcxiaoke.minicat.service;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.IBinder;
import android.os.Looper;
import android.os.Message;
import android.os.Messenger;
import android.os.Parcelable;
import android.os.RemoteException;
import android.support.v4.app.NotificationCompat;
import android.util.Log;
import com.mcxiaoke.minicat.R;
import com.mcxiaoke.minicat.api.Api;
import com.mcxiaoke.minicat.api.ApiException;
import com.mcxiaoke.minicat.api.Paging;
import com.mcxiaoke.minicat.AppContext;
import com.mcxiaoke.minicat.app.UIRecords;
import com.mcxiaoke.minicat.controller.CacheController;
import com.mcxiaoke.minicat.controller.DataController;
import com.mcxiaoke.minicat.dao.model.BaseModel;
import com.mcxiaoke.minicat.dao.model.DirectMessageModel;
import com.mcxiaoke.minicat.dao.model.IBaseColumns;
import com.mcxiaoke.minicat.dao.model.StatusColumns;
import com.mcxiaoke.minicat.dao.model.StatusModel;
import com.mcxiaoke.minicat.dao.model.StatusUpdateInfo;
import com.mcxiaoke.minicat.dao.model.StatusUpdateInfoColumns;
import com.mcxiaoke.minicat.dao.model.UserColumns;
import com.mcxiaoke.minicat.dao.model.UserModel;
import com.mcxiaoke.minicat.util.Assert;
import com.mcxiaoke.minicat.util.ImageHelper;
import com.mcxiaoke.minicat.util.LogUtil;
import com.mcxiaoke.minicat.util.NetworkHelper;
import com.mcxiaoke.minicat.util.StringHelper;
import com.mcxiaoke.minicat.util.UmengHelper;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author mcxiaoke
 * @version 8.1 2012.03.07
 */
public final class SyncService extends Service implements Handler.Callback {
    private static final String TAG = SyncService.class.getSimpleName();
    private static final boolean DEBUG = AppContext.DEBUG;

    private static void debug(String message) {
        LogUtil.v(TAG, message);
    }

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

    public static final int STATUS_UPDATE = -150;

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

    public static final int DRAFTS_SEND = -501;

    private static final int MSG_SYNC_DATA = 0;
    private static final int MSG_EXEC_IDOP = 1;
    private static final int MSG_POST_DATA = 2;
    private static final int MSG_CMD_OTHERS = 3;
    private static final int MSG_STOP_CHECK = 4;

    public static final int NOTIFICATION_ID = 1001;

    private static final long STOP_SELF_CHECK_INTERVAL = 1000 * 30;

    static class Commmand {
        public Messenger messenger;
        public String id;
        public int type;
        public Paging paging;

        @Override
        public String toString() {
            final StringBuilder sb = new StringBuilder("Commmand{");
            sb.append("id='").append(id).append('\'');
            sb.append(", messenger=").append(messenger);
            sb.append(", type=").append(type);
            sb.append(", paging=").append(paging);
            sb.append('}');
            return sb.toString();
        }
    }

    private NotificationManager mNotificationManager;
    private Api mApi;

    private Handler mUiHandler;
    private Handler mCommandHandler;
    private HandlerThread mHandlerThread;
    private ExecutorService mExecutor;

    private SyncService mService;

    private AtomicInteger mCounter = new AtomicInteger();

    private volatile boolean mIdle = true;

    @Override
    public void onCreate() {
        super.onCreate();
        debug("onCreate()");
        mService = this;
        mNotificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        mApi = AppContext.getApi();
        mUiHandler = new Handler(Looper.getMainLooper());
        mHandlerThread = new HandlerThread(TAG);
        mHandlerThread.start();
        mCommandHandler = new Handler(mHandlerThread.getLooper(), this);
        ensureExecutor();
        sendStopCheckMessage();
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (intent == null) {
            debug("onStartCommand() intent is null.");
            return START_NOT_STICKY;
        }
        int type = intent.getIntExtra("type", BaseModel.TYPE_NONE);
        debug("onStartCommand type=" + type);
        switch (type) {
            case StatusModel.TYPE_HOME:
            case StatusModel.TYPE_MENTIONS:
            case StatusModel.TYPE_USER:
            case StatusModel.TYPE_CONTEXT:
            case StatusModel.TYPE_PUBLIC:
            case StatusModel.TYPE_FAVORITES:
            case StatusModel.TYPE_PHOTO:
            case DirectMessageModel.TYPE_INBOX:
            case DirectMessageModel.TYPE_OUTBOX:
            case DirectMessageModel.TYPE_CONVERSATION_LIST:
            case DirectMessageModel.TYPE_CONVERSATION:
            case UserModel.TYPE_FRIENDS:
            case UserModel.TYPE_FOLLOWERS: {
                Message message = Message.obtain();
                message.what = MSG_SYNC_DATA;
                message.arg1 = type;
                message.obj = intent;
                mCommandHandler.sendMessage(message);
            }
            break;
            case STATUS_SHOW:
            case STATUS_DELETE:
            case STATUS_FAVORITE:
            case STATUS_UNFAVORITE:
            case USER_SHOW:
            case USER_FOLLOW:
            case USER_UNFOLLOW:
            case USER_BLOCK:
            case USER_UNBLOCK:
            case DM_DELETE: {
                Message message = Message.obtain();
                message.what = MSG_EXEC_IDOP;
                message.arg1 = type;
                message.obj = intent;
                mCommandHandler.sendMessage(message);
            }
            break;
            case FRIENDSHIPS_EXISTS:
            case FRIENDSHIPS_SHOW:
            case FRIENDSHIPS_REQUESTS:
            case FRIENDSHIPS_DENY:
            case FRIENDSHIPS_ACCEPT: {
                Message message = Message.obtain();
                message.what = MSG_CMD_OTHERS;
                message.arg1 = type;
                message.obj = intent;
                mCommandHandler.sendMessage(message);
            }
            break;
            case STATUS_UPDATE:
            case DRAFTS_SEND: {
                Message message = Message.obtain();
                message.what = MSG_POST_DATA;
                message.arg1 = type;
                message.obj = intent;
                mCommandHandler.sendMessage(message);
            }
            break;
            case BaseModel.TYPE_NONE:
                break;
            default:
                break;
        }

        return START_NOT_STICKY;
    }

    @Override
    public boolean handleMessage(Message msg) {
        mIdle = false;
        int what = msg.what;
        Intent intent = (Intent) msg.obj;
        debug("handleMessage what=" + what);
        switch (what) {
            case MSG_SYNC_DATA:
                handleSyncDataCommands(intent);
                break;
            case MSG_POST_DATA:
                handlePostDataCommands(intent);
                break;
            case MSG_EXEC_IDOP:
                handleExecOpCommands(intent);
                break;
            case MSG_CMD_OTHERS:
                handleOthersCommands(intent);
                break;
            case MSG_STOP_CHECK:
                handleStopCheck();
                break;
            default:
                break;
        }
        if (intent == null) {
            return true;
        }


        return true;
    }

    private void sendStopCheckMessage() {
        Message message = Message.obtain();
        message.what = MSG_STOP_CHECK;
        mCommandHandler.sendMessageDelayed(message, STOP_SELF_CHECK_INTERVAL);
    }

    private void handleSyncDataCommands(Intent intent) {
        Commmand cmd = new Commmand();
        cmd.messenger = intent.getParcelableExtra("messenger");
        cmd.id = intent.getStringExtra("id");
        cmd.type = intent.getIntExtra("type", BaseModel.TYPE_NONE);
        cmd.paging = intent.getParcelableExtra("paging");
        debug("handleMessage cmd=" + cmd);
        switch (cmd.type) {
            case BaseModel.TYPE_NONE:
                break;
            case StatusModel.TYPE_HOME:
            case StatusModel.TYPE_MENTIONS:
            case StatusModel.TYPE_USER:
            case StatusModel.TYPE_CONTEXT:
            case StatusModel.TYPE_PUBLIC:
            case StatusModel.TYPE_FAVORITES:
            case StatusModel.TYPE_PHOTO:
                getTimeline(cmd);
                break;
            case DirectMessageModel.TYPE_INBOX:
                getInBox(cmd);
                break;
            case DirectMessageModel.TYPE_OUTBOX:
                getOutBox(cmd);
                break;
            case DirectMessageModel.TYPE_CONVERSATION_LIST:
                getConversationList(cmd);
                break;
            case DirectMessageModel.TYPE_CONVERSATION:
                getConversation(cmd);
                break;
            case UserModel.TYPE_FRIENDS:
            case UserModel.TYPE_FOLLOWERS:
                getUsers(cmd);
                break;
            default:
                break;
        }

    }

    private void handlePostDataCommands(Intent intent) {
        int type = intent.getIntExtra("type", BaseModel.TYPE_NONE);
        switch (type) {
            case STATUS_UPDATE: {
                StatusUpdateInfo info = intent.getParcelableExtra(StatusUpdateInfo.TAG);
                if (info != null) {
                    debug("handlePostDataCommands info=" + info);
                    statusUpdate(info);
                }
            }
            break;
            case DRAFTS_SEND: {
                doSendAllDrafts();
            }
            break;
        }

    }

    private void handleExecOpCommands(Intent intent) {
        Commmand cmd = new Commmand();
        cmd.messenger = intent.getParcelableExtra("messenger");
        cmd.id = intent.getStringExtra("id");
        cmd.type = intent.getIntExtra("type", BaseModel.TYPE_NONE);
        debug("handleExecOpCommands cmd=" + cmd);
        switch (cmd.type) {
            case STATUS_SHOW:
                showStatus(cmd);
                break;
            case STATUS_DELETE:
                deleteStatus(cmd);
                break;
            case STATUS_FAVORITE:
                favorite(cmd, true);
                break;
            case STATUS_UNFAVORITE:
                favorite(cmd, false);
                break;
            case USER_SHOW:
                showUser(cmd);
                break;
            case USER_FOLLOW:
                follow(cmd, true);
                break;
            case USER_UNFOLLOW:
                follow(cmd, false);
                break;
            case USER_BLOCK:
                block(cmd, true);
                break;
            case USER_UNBLOCK:
                block(cmd, false);
                break;
            case DM_DELETE:
                deleteDirectMessage(cmd);
                break;
            default:
                break;
        }
    }

    private void handleOthersCommands(Intent intent) {
        Commmand cmd = new Commmand();
        cmd.messenger = intent.getParcelableExtra("messenger");
        cmd.id = intent.getStringExtra("id");
        cmd.type = intent.getIntExtra("type", BaseModel.TYPE_NONE);
        debug("handleOthersCommands cmd=" + cmd);
        switch (cmd.type) {
            case FRIENDSHIPS_EXISTS:
                isFriends(cmd, intent);
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
            default:
                break;
        }

    }

    private void handleStopCheck() {
        int taskCount = mCounter.get();
        if (DEBUG) {
            debug("handleStopCheck() taskCount=" + taskCount);
        }
        if (taskCount == 0) {
            stopSelf();
        } else {
            sendStopCheckMessage();
        }
    }

    public static void deleteDirectMessage(Context context, String id,
                                           final Handler handler) {
        Intent intent = new Intent(context, SyncService.class);
        intent.putExtra("type", DM_DELETE);
        intent.putExtra("id", id);
        intent.putExtra("messenger", new Messenger(handler));
        context.startService(intent);
    }

    private void deleteDirectMessage(final Commmand cmd) {
        final String id = cmd.id;
        final Runnable runnable = new Runnable() {
            @Override
            public void run() {
                DirectMessageModel dm = null;
                try {
                    // 删除消息
                    // 404 说明消息不存在
                    // 403 说明不是你的消息，无权限删除
                    dm = mApi.deleteDirectMessage(id);
                    if (dm == null) {
                        sendSuccessMessage(cmd);
                    } else {
                        DataController.delete(mService, dm);
                        sendParcelableMessage(cmd, dm);
                    }
                } catch (ApiException e) {
                    if (DEBUG) {
                        e.printStackTrace();
                    }
                    if (e.statusCode == 404) {
                        DataController.delete(mService, dm);
                    }
                    sendErrorMessage(cmd, e);
                }
            }
        };
        execute(runnable);

    }

    private void block(final Commmand cmd, final boolean block) {
        final String id = cmd.id;
        Assert.notEmpty(id);
        final Runnable runnable = new Runnable() {
            @Override
            public void run() {
                UserModel u = null;
                try {
                    u = block ? mApi.block(id) : mApi.unblock(id);
                    if (u == null) {
                        sendSuccessMessage(cmd);
                    } else {
                        DataController.delete(mService, u);

                        sendParcelableMessage(cmd, u);
                    }
                } catch (ApiException e) {
                    if (AppContext.DEBUG) {
                        e.printStackTrace();
                    }
                    sendErrorMessage(cmd, e);
                }
            }
        };
        execute(runnable);

    }

    public static void follow(Context context, String userId,
                              final Handler handler) {
        Intent intent = new Intent(context, SyncService.class);
        intent.putExtra("type", USER_FOLLOW);
        intent.putExtra("id", userId);
        intent.putExtra("messenger", new Messenger(handler));
        context.startService(intent);

    }

    public static void unFollow(Context context, String userId,
                                final Handler handler) {
        Intent intent = new Intent(context, SyncService.class);
        intent.putExtra("type", USER_UNFOLLOW);
        intent.putExtra("id", userId);
        intent.putExtra("messenger", new Messenger(handler));
        context.startService(intent);

    }

    private void follow(final Commmand cmd, final boolean follow) {
        final String id = cmd.id;
        Assert.notEmpty(id);
        final Runnable runnable = new Runnable() {
            @Override
            public void run() {
                try {
                    UserModel u = follow ? mApi.follow(id) : mApi.unfollow(id);
                    if (u != null) {
                        u.setType(UserModel.TYPE_FRIENDS);
                        DataController.updateUserModel(mService, u);
                    }
                    sendSuccessMessage(cmd);
                } catch (ApiException e) {
                    if (AppContext.DEBUG) {
                        e.printStackTrace();
                    }
                    sendErrorMessage(cmd, e);
                }
                mIdle = true;
            }
        };
        execute(runnable);

    }

    public static void showUser(Context context, String id,
                                final Handler handler) {
        startService(context, USER_SHOW, id, handler);
    }

    private void showUser(final Commmand cmd) {
        final String id = cmd.id;
        final Runnable runnable = new Runnable() {
            @Override
            public void run() {
                try {
                    UserModel u = mApi.showUser(id);
                    if (u == null) {
                        sendSuccessMessage(cmd);
                    } else {
                        CacheController.cacheAndStore(mService, u);
                        sendParcelableMessage(cmd, u);
                    }
                } catch (ApiException e) {
                    if (AppContext.DEBUG) {
                        e.printStackTrace();
                    }
                    sendErrorMessage(cmd, e);
                }
                mIdle = true;
            }
        };
        execute(runnable);

    }

    private static void startService(Context context, int type, String id,
                                     final Handler handler) {
        Intent intent = new Intent(context, SyncService.class);
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

    private void favorite(final Commmand cmd, final boolean favorite) {
        final String id = cmd.id;
        Assert.notEmpty(id);
        final Runnable runnable = new Runnable() {
            @Override
            public void run() {
                StatusModel s = null;
                try {
                    s = favorite ? mApi.favorite(id) : mApi.unfavorite(id);
                    if (s == null) {
                        sendSuccessMessage(cmd);
                    } else {
                        ContentValues values = new ContentValues();
                        values.put(StatusColumns.FAVORITED, true);
                        DataController.update(mService, s, values);
                        Bundle bundle = new Bundle();
                        bundle.putInt("type", cmd.type);
                        bundle.putBoolean("boolean", true);
                        sendSuccessMessage(cmd, bundle);
                    }
                } catch (ApiException e) {
                    if (AppContext.DEBUG) {
                        e.printStackTrace();
                    }
                    if (e.statusCode == 404) {
                        DataController.delete(mService, s);
                    }
                    sendErrorMessage(cmd, e);
                }
                mIdle = true;
            }
        };
        execute(runnable);

    }

    public static void deleteStatus(Context context, String id,
                                    final Handler handler) {
        Intent intent = new Intent(context, SyncService.class);
        intent.putExtra("type", STATUS_DELETE);
        intent.putExtra("id", id);
        intent.putExtra("messenger", new Messenger(handler));
        context.startService(intent);
    }

    private void deleteStatus(final Commmand cmd) {
        final String id = cmd.id;
        final Runnable runnable = new Runnable() {
            @Override
            public void run() {
                StatusModel s = null;
                try {
                    s = mApi.deleteStatus(id);
                    if (s == null) {
                        sendSuccessMessage(cmd);
                    } else {
                        DataController.delete(mService, s);
                        sendParcelableMessage(cmd, s);
                    }
                } catch (ApiException e) {
                    if (AppContext.DEBUG) {
                        Log.e(TAG, "deleteStatus() error:" + e);
                    }
                    if (e.statusCode == 404) {
                        DataController.delete(mService, s);
                        sendSuccessMessage(cmd);
                    } else {
                        sendErrorMessage(cmd, e);
                    }
                }
                mIdle = true;
            }
        };
        execute(runnable);

    }

    public static void doProfile(Context context, String userId,
                                 final Handler handler) {
        startService(context, USER_SHOW, userId, handler);
    }

    public static void showStatus(Context context, String id,
                                  final Handler handler) {
        startService(context, STATUS_SHOW, id, handler);
    }

    private void showStatus(final Commmand cmd) {
        final String id = cmd.id;
        final Runnable runnable = new Runnable() {
            @Override
            public void run() {
                StatusModel s = null;
                try {
                    s = mApi.showStatus(id);
                    if (s == null) {
                        sendSuccessMessage(cmd);
                    } else {
                        CacheController.cacheAndStore(mService, s);
                        sendParcelableMessage(cmd, s);
                    }
                } catch (ApiException e) {
                    if (AppContext.DEBUG) {
                        e.printStackTrace();
                    }
                    if (e.statusCode == 404) {
                        DataController.delete(mService, s);
                    }
                    sendErrorMessage(cmd, e);
                }
                mIdle = true;
            }
        };
        execute(runnable);


    }

    public static void showRelation(Context context, String userA,
                                    String userB, final Handler handler) {
        if (context == null || handler == null) {
            return;
        }
        Intent intent = new Intent(context, SyncService.class);
        intent.putExtra("type", FRIENDSHIPS_EXISTS);
        intent.putExtra("user_a", userA);
        intent.putExtra("user_b", userB);
        intent.putExtra("messenger", new Messenger(handler));
        context.startService(intent);
    }

    private void statusUpdate(final StatusUpdateInfo info) {
        debug("statusUpdate() info=" + info);
        final Runnable runnable = new Runnable() {
            @Override
            public void run() {
                boolean success = doStatusUpdate(info, false);
                if (success) {
                    mUiHandler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            showSuccessNotification();
                        }
                    }, 500);
                }
                mIdle = true;
            }
        };
        execute(runnable);
    }


    private boolean doStatusUpdate(final StatusUpdateInfo info, boolean needDeleteDraft) {
        showSendingNotification();
        isSending = true;
        boolean res = false;

        debug("doStatusUpdate() info=" + info);
        boolean photoUpload = false;
        try {
            StatusModel result = null;
            if (StringHelper.isEmpty(info.fileName) || new File(info.fileName).length() == 0) {
                if (info.type == StatusUpdateInfo.TYPE_REPLY) {
                    result = mApi.updateStatus(info.text, info.reply, null, info.location);
                } else {
                    result = mApi.updateStatus(info.text, null, info.repost, info.location);
                }
            } else {
                File file = new File(info.fileName);
                int quality = NetworkHelper.isWifi(this) ? ImageHelper.IMAGE_QUALITY_HIGH : ImageHelper.IMAGE_QUALITY_MEDIUM;

                File photo = ImageHelper.prepareUploadFile(this, file,
                        quality);
                if (photo != null && photo.length() > 0) {
                    if (DEBUG) {
                        debug("doStatusUpdate() photo file=" + file.getName() + " size="
                                + photo.length() / 1024 + " quality=" + quality);
                    }
                    photoUpload = true;
                    result = mApi.uploadPhoto(photo, info.text, info.location);
                    photo.delete();
                }
            }
            if (needDeleteDraft) {
                DataController.deleteRecord(this, info.id);
            }
//            mNotificationManager.cancel(NOTIFICATION_STATUS_UPDATE_ONGOING);
            if (result != null) {
                sendSuccessBroadcast(result);
                if (photoUpload) {
                    UmengHelper.onPhotoUploadEvent(this, AppContext.getAccount(), result.getId());
                } else {

                    UmengHelper.onStatusUpdateEvent(this, AppContext.getAccount(), result.getId());
                }
                res = true;
            }
        } catch (ApiException e) {
            if (DEBUG) {
                debug(e.toString());
                e.printStackTrace();
            }
            if (e.statusCode >= 500) {
                showFailedNotification(info, "消息未发送，已保存到草稿箱",
                        getString(R.string.msg_server_error));
            } else {
                showFailedNotification(info, "消息未发送，已保存到草稿箱", e.getMessage());
            }

            UmengHelper.onStatusUpdateError(this, AppContext.getAccount(), e.statusCode, e.errorMessage, e.getCause() + "");

        } catch (Exception e) {
            debug(e.toString());
            showFailedNotification(info, "消息未发送，已保存到草稿箱",
                    getString(R.string.msg_unkonow_error));
            UmengHelper.onStatusUpdateError(this, AppContext.getAccount(), 0, e.getMessage(), e.getCause() + "");
        } finally {
//            mNotificationManager.cancel(NOTIFICATION_STATUS_UPDATE_ONGOING);
        }
        isSending = false;
        return res;
    }

    private volatile boolean isSending = false;

    private void doSendAllDrafts() {
        if (isSending) {
            return;
        }
        isSending = true;
        final Runnable runnable = new Runnable() {
            @Override
            public void run() {
                List<StatusUpdateInfo> infos = new ArrayList<StatusUpdateInfo>();
                Cursor cursor = getContentResolver().query(StatusUpdateInfoColumns.CONTENT_URI,
                        null, null, null, null);
                if (cursor != null) {
                    while (cursor.moveToNext()) {
                        infos.add(StatusUpdateInfo.from(cursor));
                    }
                }
                if (infos != null && infos.size() > 0) {
                    for (StatusUpdateInfo info : infos) {
                        doStatusUpdate(info, true);

                    }
                }
                mIdle = true;
            }
        };
        execute(runnable);

    }

    private void isFriends(final Commmand cmd, Intent intent) {
        final String userA = intent.getStringExtra("user_a");
        final String userB = intent.getStringExtra("user_b");
        final Runnable runnable = new Runnable() {
            @Override
            public void run() {
                boolean result = false;
                try {
                    result = mApi.isFriends(userA, userB);
                } catch (ApiException e) {
                    if (AppContext.DEBUG) {
                        Log.e(TAG, "doDetectFriendships:" + e.getMessage());
                    }
                    sendErrorMessage(cmd, e);
                }
                Bundle data = new Bundle();
                data.putBoolean("boolean", result);
                sendSuccessMessage(cmd, data);
            }
        };
        execute(runnable);

    }

    private void getUsers(final Commmand cmd) {
        final String id = cmd.id;
        final Paging p = cmd.paging == null ? new Paging() : cmd.paging;

        if (NetworkHelper.isWifi(this)) {
            p.count = MAX_USERS_COUNT;
        } else {
            p.count = DEFAULT_USERS_COUNT;
        }

        final Runnable runnable = new Runnable() {
            @Override
            public void run() {
                try {
                    List<UserModel> users = null;
                    if (cmd.type == UserModel.TYPE_FRIENDS) {
                        users = mApi.getFriends(id, p);
                    } else if (cmd.type == UserModel.TYPE_FOLLOWERS) {
                        users = mApi.getFollowers(id, p);
                    }
                    if (users != null && users.size() > 0) {

                        int size = users.size();
                        ContentResolver cr = getContentResolver();
                        if (p.page < 2 && id != AppContext.getAccount()) {
                            String where = IBaseColumns.TYPE + "=? AND "
                                    + IBaseColumns.OWNER + "=?";
                            String[] whereArgs = new String[]{String.valueOf(cmd.type),
                                    id};
                            int deletedNums = cr.delete(UserColumns.CONTENT_URI, where,
                                    whereArgs);
                            if (AppContext.DEBUG) {
                                Log.d(TAG, "getUsers delete old rows " + deletedNums
                                        + " ownerId=" + id);
                            }
                        }
                        int nums = DataController.store(mService, users);
                        if (AppContext.DEBUG) {
                            Log.d(TAG, "getUsers refresh ,insert rows, num=" + nums
                                    + " ownerId=" + id);
                        }
                        sendIntMessage(cmd, nums);
                    } else {
                        sendIntMessage(cmd, 0);
                    }
                } catch (ApiException e) {
                    if (AppContext.DEBUG) {
                        e.printStackTrace();
                    }
                    sendErrorMessage(cmd, e);
                }
            }
        };
        execute(runnable);

    }

    private void getConversation(final Commmand cmd) {
        final String id = cmd.id;
        final Paging p = cmd.paging == null ? new Paging() : cmd.paging;
        if (NetworkHelper.isWifi(this)) {
            p.count = MAX_TIMELINE_COUNT;
        } else {
            p.count = DEFAULT_TIMELINE_COUNT;
        }

        final Runnable runnable = new Runnable() {
            @Override
            public void run() {
                try {
                    List<DirectMessageModel> messages = mApi.getConversation(id, p);
                    if (messages != null && messages.size() > 0) {

                        if (AppContext.DEBUG) {
                            Log.d(TAG, "getConversation() id=" + id + " result="
                                    + messages);
                        }

                        int nums = DataController.store(mService, messages);
                        sendIntMessage(cmd, nums);
                    }
                    sendIntMessage(cmd, 0);
                } catch (ApiException e) {
                    if (DEBUG) {
                        e.printStackTrace();
                    }
                    sendErrorMessage(cmd, e);
                }
            }
        };
        execute(runnable);

    }

    private void getConversationList(final Commmand cmd) {

        final Paging p = cmd.paging == null ? new Paging() : cmd.paging;
        if (NetworkHelper.isWifi(this)) {
            p.count = MAX_TIMELINE_COUNT;
        } else {
            p.count = DEFAULT_TIMELINE_COUNT;
        }

        final Runnable runnable = new Runnable() {
            @Override
            public void run() {
                try {
                    List<DirectMessageModel> messages = mApi.getConversationList(p);
                    if (messages != null && messages.size() > 0) {
                        int nums = DataController.store(mService, messages);
                        sendIntMessage(cmd, nums);
                    }
                    sendIntMessage(cmd, 0);
                } catch (ApiException e) {
                    if (DEBUG) {
                        e.printStackTrace();
                    }
                    sendErrorMessage(cmd, e);
                }
            }
        };
        execute(runnable);

    }

    public static void getConversationList(Context context,
                                           final Handler handler, Paging paging) {
        getDirectMessages(context, handler, paging,
                DirectMessageModel.TYPE_CONVERSATION_LIST);
    }

    public static void getConversation(Context context, final Handler handler,
                                       Paging paging, String userId) {
        Intent intent = new Intent(context, SyncService.class);
        intent.putExtra("type", DirectMessageModel.TYPE_CONVERSATION);
        intent.putExtra("messenger", new Messenger(handler));
        intent.putExtra("id", userId);
        intent.putExtra("paging", paging);
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
        Intent intent = new Intent(context, SyncService.class);
        intent.putExtra("type", type);
        intent.putExtra("messenger", new Messenger(handler));
        intent.putExtra("paging", paging);
        context.startService(intent);
    }

    public static void getDirectMessages(Context context,
                                         final Messenger messenger, Paging paging, String userId) {
        Intent intent = new Intent(context, SyncService.class);
        intent.putExtra("type", DirectMessageModel.TYPE_CONVERSATION);
        intent.putExtra("id", userId);
        intent.putExtra("messenger", messenger);
        intent.putExtra("paging", paging);
        context.startService(intent);
    }

    private void getDirectMessages(final Commmand cmd, final boolean in) {
        final Runnable runnable = new Runnable() {
            @Override
            public void run() {
                Paging p = cmd.paging;

                if (p == null) {
                    p = new Paging();
                }

                if (NetworkHelper.isWifi(mService)) {
                    p.count = MAX_TIMELINE_COUNT;
                } else {
                    p.count = DEFAULT_TIMELINE_COUNT;
                }

                try {
                    List<DirectMessageModel> messages = in ? mApi
                            .getDirectMessagesInbox(p) : mApi.getDirectMessagesOutbox(p);
                    if (messages != null && messages.size() > 0) {
                        int nums = DataController.store(mService, messages);
                        sendIntMessage(cmd, nums);
                    }
                    sendIntMessage(cmd, 0);
                } catch (ApiException e) {
                    if (DEBUG) {
                        e.printStackTrace();
                    }
                    sendErrorMessage(cmd, e);
                }
            }
        };
        execute(runnable);

    }

    private void getInBox(Commmand cmd) {
        getDirectMessages(cmd, true);
    }

    private void getOutBox(Commmand cmd) {
        getDirectMessages(cmd, false);
    }

    private void getTimeline(final Commmand cmd) {
        final int type = cmd.type;
        final Paging p = cmd.paging == null ? new Paging() : cmd.paging;
        final Runnable runnable = new Runnable() {
            @Override
            public void run() {
                List<StatusModel> statuses = null;

                String id = cmd.id;
                if (NetworkHelper.isWifi(mService)) {
                    p.count = MAX_TIMELINE_COUNT;
                } else {
                    p.count = DEFAULT_TIMELINE_COUNT;
                }

                if (AppContext.DEBUG) {
                    Log.d(TAG, "getTimeline userId=" + id + " paging=" + p + " type="
                            + type);
                }

                try {
                    switch (type) {
                        case StatusModel.TYPE_HOME:
                            statuses = mApi.getHomeTimeline(p);
                            break;
                        case StatusModel.TYPE_MENTIONS:
                            statuses = mApi.getMentions(p);
                            break;
                        case StatusModel.TYPE_PUBLIC:
                            p.count = DEFAULT_TIMELINE_COUNT;
                            statuses = mApi.getPublicTimeline();
                            break;
                        case StatusModel.TYPE_FAVORITES:
                            statuses = mApi.getFavorites(id, p);
                            break;
                        case StatusModel.TYPE_USER:
                            statuses = mApi.getUserTimeline(id, p);
                            break;
                        case StatusModel.TYPE_CONTEXT:
                            statuses = mApi.getContextTimeline(id);
                            break;
                        case StatusModel.TYPE_PHOTO:
                            statuses = mApi.getPhotosTimeline(id, p);
                            break;
                        default:
                            break;
                    }
                    if (statuses == null || statuses.size() == 0) {
                        sendIntMessage(cmd, 0);
                        if (AppContext.DEBUG)
                            Log.d(TAG, "getTimeline() count=0. userId=" + id + " type="
                                    + type);
                        return;
                    } else {
                        if (type == StatusModel.TYPE_PUBLIC) {

                        }
                        int size = statuses.size();
                        if (type == StatusModel.TYPE_PUBLIC
                                || (size == p.count && p.maxId == null && p.page <= 1)) {
                            deleteOldStatuses(cmd.id, cmd.type);
                        }
                        int insertedCount = DataController.storeStatusesWithUsers(mService,
                                statuses);
                        if (AppContext.DEBUG) {
                            Log.d(TAG, "getTimeline() size=" + size + " userId=" + id
                                    + " count=" + p.count + " page=" + p.page
                                    + " type=" + type + " insertedCount="
                                    + insertedCount);
                        }
                        sendIntMessage(cmd, insertedCount);
                    }
                } catch (ApiException e) {
                    if (AppContext.DEBUG) {
                        Log.e(TAG, "getTimeline() [error]" + e.statusCode + ":"
                                + e.errorMessage + " userId=" + id + " type=" + type);
                        e.printStackTrace();
                    }
                    sendErrorMessage(cmd, e);
                }
                mIdle = true;
            }
        };
        execute(runnable);

    }

    private int deleteOldStatuses(final String id, final int type) {
        int numDeleted = 0;
        if (type == StatusModel.TYPE_USER) {
            numDeleted = DataController.deleteUserTimeline(this, id);
        } else if (type == StatusModel.TYPE_FAVORITES) {
            numDeleted = DataController.deleteUserFavorites(this, id);
        } else {
            numDeleted = DataController.deleteStatusByType(this, type);
        }
        if (AppContext.DEBUG) {
            Log.d(TAG, "deleteOldStatuses numDeleted=" + numDeleted + " type="
                    + type + " id=" + id);
        }
        return numDeleted;
    }

    public static void getTimeline(Context context, int type,
                                   final Handler handler, String userId, Paging paging) {
        Intent intent = new Intent(context, SyncService.class);
        intent.putExtra("type", type);
        intent.putExtra("id", userId);
        intent.putExtra("messenger", new Messenger(handler));
        intent.putExtra("paging", paging);
        if (AppContext.DEBUG) {
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
        Intent intent = new Intent(context, SyncService.class);
        intent.putExtra("type", type);
        intent.putExtra("messenger", new Messenger(handler));
        intent.putExtra("paging", paging);
        intent.putExtra("id", userId);
        context.startService(intent);
    }

    private void execute(Runnable runnable) {
        if (runnable != null) {
            ensureExecutor();
            mExecutor.submit(new MyRunnable(runnable, mCounter));
        }
    }

    private void ensureExecutor() {
        if (mExecutor == null) {
            mExecutor = Executors.newCachedThreadPool();
        }
    }


    private void sendErrorMessage(Commmand cmd, ApiException e) {
        String message = e.getMessage();
        if (e.statusCode == ApiException.IO_ERROR) {
            message = getString(R.string.msg_connection_error);
        } else if (e.statusCode >= 500) {
            message = getString(R.string.msg_server_error);
        }
        Bundle bundle = new Bundle();
        bundle.putInt("error_code", e.statusCode);
        bundle.putString("error_message", message);
        sendMessage(cmd, RESULT_ERROR, bundle);
    }

    private void sendIntMessage(Commmand cmd, int size) {
        Bundle bundle = new Bundle();
        bundle.putInt("count", size);
        sendMessage(cmd, RESULT_SUCCESS, bundle);
    }

    private void sendParcelableMessage(Commmand cmd, Parcelable parcel) {
        Bundle bundle = new Bundle();
        bundle.putParcelable("data", parcel);
        sendMessage(cmd, RESULT_SUCCESS, bundle);
    }

    private void sendSuccessMessage(Commmand cmd, Bundle bundle) {
        sendMessage(cmd, RESULT_SUCCESS, bundle);
    }

    private void sendSuccessMessage(Commmand cmd) {
        sendMessage(cmd, RESULT_SUCCESS, null);
    }

    private void sendMessage(Commmand cmd, int what, final Bundle bundle) {
        if (cmd.messenger == null) {
            return;
        }
        Message m = Message.obtain();
        m.what = what;
        m.arg1 = cmd.type;
        if (bundle != null) {
            m.getData().putAll(bundle);
        }
        try {
            cmd.messenger.send(m);
        } catch (RemoteException e) {
            if (AppContext.DEBUG) {
                e.printStackTrace();
            }
        }
    }

    private int showSendingNotification() {
        int id = NOTIFICATION_ID;
        PendingIntent contentIntent = PendingIntent.getActivity(this, 0,
                new Intent(), 0);
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this);
        builder.setSmallIcon(R.drawable.ic_stat_app);
        builder.setTicker("饭否消息正在发送...");
        builder.setWhen(System.currentTimeMillis());
        builder.setContentTitle("饭否消息");
        builder.setContentText("正在发送...");
        builder.setOngoing(true);
        builder.setContentIntent(contentIntent);
        Notification notification = builder.build();
        mNotificationManager.notify(id, notification);
        return id;
    }

    private int showSuccessNotification() {
        int id = NOTIFICATION_ID;
        PendingIntent contentIntent = PendingIntent.getActivity(this, 0,
                new Intent(), 0);
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this);
        builder.setSmallIcon(R.drawable.ic_stat_app);
        builder.setTicker("消息已成功发送");
        builder.setWhen(System.currentTimeMillis());
        builder.setContentTitle("饭否消息");
        builder.setContentText("消息已成功发送");
        builder.setContentIntent(contentIntent);
        builder.setAutoCancel(true);
        builder.setOnlyAlertOnce(true);
        Notification notification = builder.build();
        mNotificationManager.notify(id, notification);
        cancelNotificationDeply(id);
        return id;
    }

    private int showFailedNotification(StatusUpdateInfo info, String title, String message) {
        doSaveRecords(info);
        int id = NOTIFICATION_ID;
        Intent intent = new Intent(this, UIRecords.class);
        PendingIntent contentIntent = PendingIntent.getActivity(this, 0,
                intent, PendingIntent.FLAG_UPDATE_CURRENT);
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this);
        builder.setSmallIcon(R.drawable.ic_stat_app);
        builder.setTicker(title);
        builder.setWhen(System.currentTimeMillis());
        builder.setContentTitle(title);
        builder.setContentText(message);
        builder.setContentIntent(contentIntent);
        builder.setAutoCancel(true);
        builder.setOnlyAlertOnce(true);
        Notification notification = builder.build();
        mNotificationManager.notify(id, notification);
        return id;
    }

    private static final long CANCEL_DEPLAY_TIME = 2000L;

    private void cancelNotificationDeply(final int notificationId) {
        mNotificationManager.cancel(notificationId);
//        final Runnable cancelRunnable = new Runnable() {
//            @Override
//            public void run() {
//                mNotificationManager.cancel(notificationId);
//            }
//        };
//        mUiHandler.postDelayed(cancelRunnable, CANCEL_DEPLAY_TIME);
    }

    private void doSaveRecords(StatusUpdateInfo info) {
        Uri resultUri = DataController.store(this, info);
    }

    private void sendSuccessBroadcast(StatusModel status) {
        Intent intent = new Intent(Constants.ACTION_STATUS_SENT);
        intent.putExtra("data", status);
        intent.setPackage(getPackageName());
        sendOrderedBroadcast(intent, null);
    }

    private void doClose() {
        mExecutor.shutdownNow();
        mExecutor = null;
        mCommandHandler.removeCallbacksAndMessages(null);
        mCommandHandler.getLooper().quit();
        mCommandHandler = null;
        mNotificationManager.cancelAll();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        debug("onDestroy()");
        doClose();
    }

    static class MyRunnable implements Runnable {
        private Runnable runnable;
        private AtomicInteger counter;

        public MyRunnable(Runnable runnable, AtomicInteger counter) {
            this.runnable = runnable;
            this.counter = counter;

        }

        @Override
        public void run() {
            counter.incrementAndGet();
            if (runnable != null) {
                runnable.run();
            }
            counter.decrementAndGet();
        }
    }

}
