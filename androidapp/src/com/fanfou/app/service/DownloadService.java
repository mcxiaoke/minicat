package com.fanfou.app.service;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.EntityUtils;

import android.app.AlertDialog;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Handler;
import android.util.Log;
import android.webkit.MimeTypeMap;
import android.widget.RemoteViews;

import com.fanfou.app.App;
import com.fanfou.app.NewVersionPage;
import com.fanfou.app.R;
import com.fanfou.app.config.Commons;
import com.fanfou.app.http.ConnectionManager;
import com.fanfou.app.update.VersionInfo;
import com.fanfou.app.util.IOHelper;
import com.fanfou.app.util.StringHelper;
import com.fanfou.app.util.Utils;

/**
 * @author mcxiaoke
 * @version 1.0 2011.09.04
 * @version 2.0 2011.10.31
 * @version 2.1 2011.11.24
 * @version 2.2 2011.11.25
 * 
 */
public class DownloadService extends BaseIntentService {
	private static final String TAG=DownloadService.class.getSimpleName();
	
	public static final String APP_SITE_BASE_URL = "http://apps.fanfou.com/android/";
	public static final String APP_SITE_BETA=APP_SITE_BASE_URL+"beta/";
	public static final String APP_UPDATE_SITE = APP_SITE_BASE_URL+"update.json";
	public static final String APP_UPDATE_SITE_BETA = APP_SITE_BETA+"update.json";

	private static final int NOTIFICATION_PROGRESS_ID = 1;
	private NotificationManager nm;
	private Notification notification;
	private RemoteViews remoteViews;
	private Handler mHandler;

	public static final int TYPE_CHECK = 0;
	public static final int TYPE_DOWNLOAD = 1;

	private void log(String message) {
		Log.d("DownloadService", message);
	}

	public DownloadService() {
		super("DownloadService");

	}

	public static void startDownload(Context context, String url) {
		Intent intent = new Intent(context, DownloadService.class);
		intent.putExtra(Commons.EXTRA_TYPE, TYPE_DOWNLOAD);
		intent.putExtra(Commons.EXTRA_DOWNLOAD_URL, url);
		context.startService(intent);
	}

	public static void startCheck(Context context) {
		Intent intent = new Intent(context, DownloadService.class);
		intent.putExtra(Commons.EXTRA_TYPE, TYPE_CHECK);
		context.startService(intent);
	}

	@Override
	protected void onHandleIntent(Intent intent) {
		nm = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
		mHandler = new Handler();
		int type = intent.getIntExtra(Commons.EXTRA_TYPE, TYPE_CHECK);
		if (type == TYPE_CHECK) {
			check();
		} else if (type == TYPE_DOWNLOAD) {
			String url = intent.getStringExtra(Commons.EXTRA_DOWNLOAD_URL);
			if(App.DEBUG){
				url=(APP_SITE_BETA+"/fanfou.apk");
			}
			log("onHandleIntent TYPE_DOWNLOAD url="+url);
			if (!StringHelper.isEmpty(url)) {
				download(url);
			}
		}
	}

	private void check() {
		VersionInfo info = fetchVersionInfo();
		if (App.DEBUG && info != null) {
			notifyUpdate(info, this);
			return;
		}
		if (info != null && info.versionCode > App.me.appVersionCode) {
			notifyUpdate(info, this);
		}
	}

	private void download(String url) {
		showProgress();
		InputStream is = null;
		FileOutputStream fos = null;
		try {
			HttpResponse response = ConnectionManager.get(url);
			int statusCode = response.getStatusLine().getStatusCode();
			if (statusCode == 200) {
				HttpEntity entity = response.getEntity();
				long total = entity.getContentLength();
				long download = 0;
				is = entity.getContent();
				File file = new File(IOHelper.getDownloadDir(this),
						"fanfou.apk");
				fos = new FileOutputStream(file);
				// fos = openFileOutput("fanfou.apk", MODE_PRIVATE);
				byte[] buffer = new byte[20480];
				int read = -1;

				while ((read = is.read(buffer)) != -1) {
					fos.write(buffer, 0, read);
					download += read;

					int progress = (int) (100 * download / (total * 1.0));
					updateProgress(progress, file.getAbsolutePath());
					if (App.DEBUG) {
						log("progress=" + progress);
					}
				}
				fos.flush();
				if (download >= total) {
					nm.cancel(NOTIFICATION_PROGRESS_ID);
					Utils.open(this, file.getAbsolutePath());
				}
			}
		} catch (IOException e) {
			if (App.DEBUG)
				Log.e(TAG, "download error: "+e.getMessage());
				e.printStackTrace();
			nm.cancel(NOTIFICATION_PROGRESS_ID);
		} finally {
			IOHelper.forceClose(is);
			IOHelper.forceClose(fos);
		}
	}

	private void showProgress() {
		notification = new Notification(R.drawable.ic_notify_home, "正在下载饭否客户端",
				System.currentTimeMillis());
		notification.flags |= Notification.FLAG_ONGOING_EVENT;
		notification.flags |= Notification.FLAG_AUTO_CANCEL;
		remoteViews = new RemoteViews(getPackageName(),
				R.layout.download_notification);
		remoteViews.setTextViewText(R.id.download_notification_text,
				"正在下载饭否客户端 0%");
		remoteViews.setProgressBar(R.id.download_notification_progress, 100, 0,
				false);
		notification.contentView = remoteViews;
		PendingIntent contentIntent = PendingIntent.getActivity(this, 0,
				new Intent(), 0);
		notification.contentIntent = contentIntent;

		nm.notify(NOTIFICATION_PROGRESS_ID, notification);

	}

	private void updateProgress(final int progress, String fileName) {
		if (progress <= 100) {
			notification.contentView.setTextViewText(
					R.id.download_notification_text, "正在下载饭否客户端 " + progress
							+ "%");
			notification.contentView.setInt(
					R.id.download_notification_progress, "setProgress",
					progress);
			nm.notify(NOTIFICATION_PROGRESS_ID, notification);
		}
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

	public static VersionInfo fetchVersionInfo() {
		// HttpClient client = NetworkHelper.newHttpClient();
		// NetworkHelper.setProxy(client);
		// HttpGet request = new HttpGet(APP_UPDATE_SITE);
		try {

			// HttpResponse response = client.execute(request);
			String url=APP_UPDATE_SITE;
			if(App.DEBUG){
				url=APP_UPDATE_SITE_BETA;
			}
			HttpResponse response = ConnectionManager.get(APP_UPDATE_SITE);
			int statusCode = response.getStatusLine().getStatusCode();
			if (App.DEBUG) {
				Log.d(TAG, "statusCode=" + statusCode);
			}
			if (statusCode == 200) {
				String content = EntityUtils.toString(response.getEntity(),
						HTTP.UTF_8);
				if (App.DEBUG) {
					Log.d(TAG, "response=" + content);
				}
				return VersionInfo.parse(content);
			}
		} catch (IOException e) {
			if (App.DEBUG) {
				e.printStackTrace();
			}
		} catch (Exception e) {
			if (App.DEBUG) {
				e.printStackTrace();
			}
		}
		return null;
	}

	public static void notifyUpdate(VersionInfo info, Context context) {
		String versionInfo = info.versionName;
		NotificationManager nm = (NotificationManager) context
				.getSystemService(Context.NOTIFICATION_SERVICE);
		Notification notification = new Notification(R.drawable.ic_notify_icon,
				"饭否客户端有新版本：" + versionInfo, System.currentTimeMillis());

		PendingIntent contentIntent = PendingIntent.getActivity(context, 0,
				getNewVersionIntent(context, info), 0);
		notification.setLatestEventInfo(context, "饭否客户端有新版本：" + versionInfo,
				"点击查看更新内容", contentIntent);
		notification.flags |= Notification.FLAG_AUTO_CANCEL;
		nm.notify(2, notification);

	}

	public static void showUpdateConfirmDialog(final Context context,
			final VersionInfo info) {
		DialogInterface.OnClickListener downListener = new DialogInterface.OnClickListener() {

			@Override
			public void onClick(DialogInterface dialog, int which) {
				startDownload(context, info.downloadUrl);
			}
		};
		AlertDialog.Builder builder = new AlertDialog.Builder(context);
		builder.setTitle("发现新版本，是否升级？").setCancelable(true)
				.setNegativeButton("以后再说", null);
		builder.setPositiveButton("立即升级", downListener);
		StringBuffer sb = new StringBuffer();
		sb.append("安装版本： ").append(App.me.appVersionName).append("(Build")
				.append(App.me.appVersionCode).append(")");
		sb.append("\n最新版本： ").append(info.versionName).append("(Build")
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
		Intent intent = new Intent(context, NewVersionPage.class);
		intent.putExtra(Commons.EXTRA_DATA, info);
		return intent;
	}

}
