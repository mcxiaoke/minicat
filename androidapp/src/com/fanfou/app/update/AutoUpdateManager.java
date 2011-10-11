package com.fanfou.app.update;

import java.io.IOException;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.EntityUtils;

import android.app.AlertDialog;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.util.Log;

import com.fanfou.app.App;
import com.fanfou.app.R;
import com.fanfou.app.config.Commons;
import com.fanfou.app.service.DownloadService;
import com.fanfou.app.util.NetworkHelper;

/**
 * @author mcxiaoke
 * @version 1.0 20110904
 * 
 */
public final class AutoUpdateManager {
	/**
	 * 
	 * <以下说明过时，现在直接读update.json，不必分开读取> 升级流程 首先联网读取 update.txt文件
	 * 如果读取的版本号大于本地版本号，说明有新版本，接着联网读取update.json，解析升级数据并返回
	 * 如果读取的版本号小于或等于本地版本号，不再读取update.json，
	 * 
	 * */
	public static final String APP_UPDATE_SITE = "http://apps.fanfou.com/android/update.json";

	// public static final String
	// APP_UPDATE_CODE="http://apps.fanfou.com/android/update.txt";

	public static VersionInfo fetchVersionInfo() {
		HttpClient client = NetworkHelper.newHttpClient();
		HttpGet request = new HttpGet(APP_UPDATE_SITE);
		try {
			HttpResponse response = client.execute(request);
			int statusCode = response.getStatusLine().getStatusCode();
			if (App.DEBUG) {
				Log.d("AutoUpdateManager", "statusCode=" + statusCode);
			}
			if (statusCode == 200) {
				String content = EntityUtils.toString(response.getEntity(),
						HTTP.UTF_8);
				if (App.DEBUG) {
					Log.d("AutoUpdateManager", "response=" + content);
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

	public static void checkUpdate(Context context) {
		VersionInfo info = fetchVersionInfo();
		if (App.DEBUG) {
			if (info != null) {
				notifyUpdate(info, context);
			}
			return;
		}
		if (info != null && info.versionCode > App.me.appVersionCode) {
			notifyUpdate(info, context);
		}
	}

	public static void notifyUpdate(VersionInfo info, Context context) {
		String versionInfo = info.versionName + "(Build" + info.versionCode
				+ ")";
		NotificationManager nm = (NotificationManager) context
				.getSystemService(Context.NOTIFICATION_SERVICE);
		Notification notification = new Notification(R.drawable.icon,
				"饭否客户端，发现新版本：" + versionInfo, System.currentTimeMillis());
		Intent intent = new Intent(context, DownloadService.class);
		intent.putExtra(Commons.EXTRA_URL, info.downloadUrl);
		PendingIntent contentIntent = PendingIntent.getService(context, 0,
				intent, 0);
		notification.setLatestEventInfo(context, "饭否客户端有更新，点击开始下载", "版本号："
				+ versionInfo, contentIntent);
		notification.flags |= Notification.FLAG_AUTO_CANCEL;
		nm.notify(2, notification);

	}

	public static void startDownload(Context context, String url) {
		Intent intent = new Intent(context, DownloadService.class);
		intent.putExtra(Commons.EXTRA_URL, url);
		context.startService(intent);
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
		builder.setTitle("发现新版本，是否更新？").setCancelable(true)
				.setNegativeButton("以后再说", null);
		builder.setPositiveButton("立即更新", downListener);
		StringBuffer sb = new StringBuffer();
		sb.append("安装版本： ").append(App.me.appVersionName).append("(Build")
				.append(App.me.appVersionCode).append(")");
		sb.append("\n最新版本： ").append(info.versionName).append("(Build")
				.append(info.versionCode).append(")");
		sb.append("\n更新日期：").append(info.releaseDate);
		sb.append("\n更新级别：").append(info.forceUpdate ? "重要更新" : "一般更新");
		sb.append("\n更新内容：\n").append(info.changelog);
		builder.setMessage(sb.toString());
		AlertDialog dialog = builder.create();
		dialog.show();

	}

}
