package com.mcxiaoke.minicat.service;

import android.app.AlarmManager;
import android.app.AlertDialog;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationCompat.Builder;
import android.util.Log;
import android.webkit.MimeTypeMap;
import android.widget.RemoteViews;
import com.mcxiaoke.minicat.AppContext;
import com.mcxiaoke.minicat.R;
import com.mcxiaoke.minicat.app.UIVersionUpdate;
import com.mcxiaoke.minicat.util.DateTimeHelper;
import com.mcxiaoke.minicat.util.IOHelper;
import com.mcxiaoke.minicat.util.StringHelper;
import com.mcxiaoke.minicat.util.Utils;
import org.oauthsimple.http.Request;
import org.oauthsimple.http.Response;
import org.oauthsimple.http.Verb;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Calendar;
import java.util.Date;

/**
 * @author mcxiaoke
 * @version 3.1 2012.02.24
 */
public class DownloadService extends BaseIntentService {
    public static final String UPDATE_VERSION_FILE = "http://apps.fanfou.com/android/update.json";
    public static final int TYPE_CHECK = 0;
    public static final int TYPE_DOWNLOAD = 1;
    private static final String TAG = DownloadService.class.getSimpleName();
    private static final int NOTIFICATION_PROGRESS_ID = -12345;
    private static final int MSG_PROGRESS = 0;
    private static final int MSG_SUCCESS = 1;
    private NotificationManager nm;
    private Notification notification;
    private Handler mHandler;

    public DownloadService() {
        super("DownloadService");

    }

    public static void set(Context context, boolean set) {
        if (set) {
            set(context);
        } else {
            unset(context);
        }
    }

    public static void set(Context context) {
        Calendar c = Calendar.getInstance();
        c.set(c.get(Calendar.YEAR), c.get(Calendar.MONTH),
                c.get(Calendar.DAY_OF_MONTH), 11, 0);
        c.add(Calendar.DATE, 1);
        long interval = 5 * 24 * 3600 * 1000;
        AlarmManager am = (AlarmManager) context
                .getSystemService(Context.ALARM_SERVICE);
        am.setInexactRepeating(AlarmManager.RTC, c.getTimeInMillis(), interval,
                getPendingIntent(context));
        if (AppContext.DEBUG) {
            Log.d(TAG,
                    "set interval=2day first time="
                            + DateTimeHelper.formatDate(c.getTime()));
        }
    }

    public static void unset(Context context) {
        AlarmManager am = (AlarmManager) context
                .getSystemService(Context.ALARM_SERVICE);
        am.cancel(getPendingIntent(context));
        if (AppContext.DEBUG) {
            Log.d(TAG, "unset");
        }
    }

    private final static PendingIntent getPendingIntent(Context context) {
        Intent intent = new Intent(context, DownloadService.class);
        intent.putExtra("type", TYPE_CHECK);
        PendingIntent pi = PendingIntent.getService(context, 0, intent,
                PendingIntent.FLAG_UPDATE_CURRENT);
        return pi;
    }

    public static void startDownload(Context context, String url) {
        Intent intent = new Intent(context, DownloadService.class);
        intent.putExtra("type", TYPE_DOWNLOAD);
        intent.putExtra("url", url);
        context.startService(intent);
    }

    public static VersionInfo fetchVersionInfo() {
        try {
            Request request = new Request(Verb.GET, UPDATE_VERSION_FILE);
            Response response = request.send();
            int statusCode = response.getCode();
            if (AppContext.DEBUG) {
                Log.d(TAG, "statusCode=" + statusCode);
            }
            if (statusCode == 200) {
                String content = response.getBody();
                if (AppContext.DEBUG) {
                    Log.d(TAG, "response=" + content);
                }
                return VersionInfo.parse(content);
            }
        } catch (Exception e) {
            if (AppContext.DEBUG) {
                e.printStackTrace();
            }
        }
        return null;
    }

    public static void notifyUpdate(VersionInfo info, Context context) {
        String versionInfo = info.versionName;
        NotificationManager nm = (NotificationManager) context
                .getSystemService(Context.NOTIFICATION_SERVICE);
        final NotificationCompat.Builder builder = new Builder(context);
        builder.setSmallIcon(R.drawable.ic_stat_app);
        builder.setWhen(System.currentTimeMillis());
        builder.setContentIntent(PendingIntent.getActivity(context, 0,
                getNewVersionIntent(context, info), 0));
        builder.setTicker("饭否客户端有新版本：" + versionInfo);
        builder.setContentText("饭否客户端有新版本：" + versionInfo);
        builder.setSubText("点击查看更新内容");
        builder.setAutoCancel(true);
        nm.notify(2, builder.build());

    }

    public static void showUpdateConfirmDialog(final Context context,
                                               final VersionInfo info) {
        DialogInterface.OnClickListener li = new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                startDownload(context, info.downloadUrl);
            }
        };
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle("发现新版本，是否升级？").setCancelable(true)
                .setNegativeButton("以后再说", null);
        builder.setPositiveButton("立即升级", li);
        StringBuffer sb = new StringBuffer();
        sb.append("安装版本：").append(AppContext.versionName).append("(Build")
                .append(AppContext.versionCode).append(")");
        sb.append("\n最新版本：").append(info.versionName).append("(Build")
                .append(info.versionCode).append(")");
        sb.append("\n更新日期：").append(info.releaseDate);
        sb.append("\n更新级别：").append(info.forceUpdate ? "重要升级" : "一般升级");
        sb.append("\n更新内容：\n").append(info.changelog);
        builder.setMessage(sb.toString());
        AlertDialog dialog = builder.create();
        dialog.show();

    }

    public static Intent getNewVersionIntent(Context context,
                                             final VersionInfo info) {
        Intent intent = new Intent(context, UIVersionUpdate.class);
        intent.putExtra("data", info);
        return intent;
    }

    private void log(String message) {
        Log.d("DownloadService", message);
    }

    @Override
    public void onCreate() {
        super.onCreate();
        mHandler = new DownloadHandler();
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        nm = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        int type = intent.getIntExtra("type", TYPE_CHECK);
        if (type == TYPE_CHECK) {
            check();
        } else if (type == TYPE_DOWNLOAD) {
            String url = intent.getStringExtra("url");
            log("onHandleIntent TYPE_DOWNLOAD url=" + url);
            if (!StringHelper.isEmpty(url)) {
                download(url);
            }
        }
    }

    private void check() {
        VersionInfo info = fetchVersionInfo();
        if (AppContext.DEBUG) {
            if (info != null) {
                notifyUpdate(info, this);
                return;
            }
        }

        // for debug
        if (info != null && info.versionCode > AppContext.versionCode) {
            notifyUpdate(info, this);
        }
    }

    private void download(String url) {
        showProgress();
        InputStream is = null;
        BufferedOutputStream bos = null;

        final long UPDATE_TIME = 2000;
        long lastTime = 0;
        try {
            Request request = new Request(Verb.GET, url);
            Response response = request.send();
            int statusCode = response.getCode();
            if (statusCode == 200) {
                long total = response.getContentLength();
                long download = 0;
                is = response.getInputStream();
                File file = new File(IOHelper.getDownloadDir(this), "fanfou_"
                        + DateTimeHelper.formatDateFileName(new Date())
                        + ".apk");
                bos = new BufferedOutputStream(new FileOutputStream(file));
                byte[] buffer = new byte[8196];
                int read = -1;
                while ((read = is.read(buffer)) != -1) {
                    bos.write(buffer, 0, read);
                    download += read;
                    int progress = (int) (100.0 * download / total);
                    if (AppContext.DEBUG) {
                        log("progress=" + progress);
                    }
                    if (System.currentTimeMillis() - lastTime >= UPDATE_TIME) {
                        Message message = mHandler.obtainMessage(MSG_PROGRESS);
                        message.arg1 = progress;
                        mHandler.sendMessage(message);
                        lastTime = System.currentTimeMillis();
                    }
                    ;
                }
                bos.flush();
                if (download >= total) {
                    Message message = new Message();
                    message.what = MSG_SUCCESS;
                    message.getData().putString("filename",
                            file.getAbsolutePath());
                    mHandler.sendMessage(message);
                }
            }
        } catch (IOException e) {
            if (AppContext.DEBUG) {
                Log.e(TAG, "download error: " + e.getMessage());
                e.printStackTrace();
            }

        } finally {
            nm.cancel(NOTIFICATION_PROGRESS_ID);
            IOHelper.forceClose(is);
            IOHelper.forceClose(bos);
        }
    }

    private void showProgress() {
        notification = new Notification(R.drawable.ic_notify_download,
                "正在下载饭否客户端", System.currentTimeMillis());
        notification.flags |= Notification.FLAG_ONGOING_EVENT;
        notification.flags |= Notification.FLAG_AUTO_CANCEL;
        notification.contentIntent = PendingIntent.getActivity(this, 0,
                new Intent(), 0);
        RemoteViews view = new RemoteViews(getPackageName(),
                R.layout.download_notification);
        view.setTextViewText(R.id.download_notification_text, "正在下载饭否客户端 0%");
        view.setProgressBar(R.id.download_notification_progress, 100, 0, false);
        notification.contentView = view;
        nm.notify(NOTIFICATION_PROGRESS_ID, notification);
    }

    private void updateProgress(final int progress) {
        RemoteViews view = new RemoteViews(getPackageName(),
                R.layout.download_notification);
        view.setTextViewText(R.id.download_notification_text, "正在下载饭否客户端 "
                + progress + "%");
        view.setInt(R.id.download_notification_progress, "setProgress",
                progress);
        notification.contentView = view;
        nm.notify(NOTIFICATION_PROGRESS_ID, notification);
    }

    @SuppressWarnings("unused")
    private PendingIntent getInstallPendingIntent(String fileName) {
        String mimeType = MimeTypeMap.getSingleton().getMimeTypeFromExtension(
                Utils.getExtension(fileName));
        if (mimeType != null) {
            Intent intent = new Intent(Intent.ACTION_VIEW);
            intent.setDataAndType(Uri.fromFile(new File(fileName)), mimeType);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            PendingIntent pi = PendingIntent.getActivity(this, 0, intent, 0);
            return pi;
        }
        return null;
    }

    private class DownloadHandler extends Handler {

        @Override
        public void handleMessage(Message msg) {
            if (AppContext.DEBUG) {
                log("DownloadHandler what=" + msg.what + " progress="
                        + msg.arg1);
            }
            if (MSG_PROGRESS == msg.what) {
                int progress = msg.arg1;
                updateProgress(progress);
            } else if (MSG_SUCCESS == msg.what) {
                nm.cancel(NOTIFICATION_PROGRESS_ID);
                String filePath = msg.getData().getString("filename");
                Utils.open(DownloadService.this, filePath);
            }
        }
    }

}
