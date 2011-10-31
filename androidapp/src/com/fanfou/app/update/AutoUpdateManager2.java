package com.fanfou.app.update;

import java.io.IOException;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.EntityUtils;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
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
public final class AutoUpdateManager2 {
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
		NetworkHelper.setProxy(client);
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



}
