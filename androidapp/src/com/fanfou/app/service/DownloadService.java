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

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.net.Uri;
import android.os.Handler;
import android.util.Log;
import android.webkit.MimeTypeMap;
import android.widget.RemoteViews;
import com.fanfou.app.App;
import com.fanfou.app.HomePage;
import com.fanfou.app.R;
import com.fanfou.app.config.Commons;
import com.fanfou.app.util.IOHelper;
import com.fanfou.app.util.StringHelper;
import com.fanfou.app.util.Utils;

/**
 * @author mcxiaoke
 * @version 1.0 20110904
 * 
 */
public class DownloadService extends BaseIntentService {
	private static final int NOTIFICATION_PROGRESS_ID=1;
	private NotificationManager nm;
	private Notification notification;
	private RemoteViews remoteViews;
	private Handler mHandler;
	
	private void log(String message){
		Log.d("DownloadService", message);
	}

	public DownloadService() {
		super("DownloadService");
	}

	@Override
	protected void onHandleIntent(Intent intent) {
		nm=(NotificationManager) getSystemService(NOTIFICATION_SERVICE);
		mHandler=new Handler();
		String url=intent.getStringExtra(Commons.EXTRA_URL);
		if(!StringHelper.isEmpty(url)){
			download(url);
		}
	}

	private void download(String url) {
		showProgress();
		InputStream is=null;
		FileOutputStream fos=null;
		try {
			HttpClient client = new DefaultHttpClient();
			HttpGet request = new HttpGet(url);
			HttpResponse response = client.execute(request);
			int statusCode=response.getStatusLine().getStatusCode();
			if(statusCode==200){
				HttpEntity entity=response.getEntity();
				long total=entity.getContentLength();
				long download=0;
				is=entity.getContent();
				File file=new File(IOHelper.getDownloadDir(this),"fanfou.apk");
				fos=new FileOutputStream(file);
				byte[] buffer=new byte[20480];
				int read=-1;
				
				while((read=is.read(buffer))!=-1){
					fos.write(buffer, 0, read);
					download+=read;
					
					int progress=(int)(100*download/(total*1.0));
					updateProgress(progress,file.getAbsolutePath());
					if(App.DEBUG){
						log("progress="+progress);
					}
				}
				fos.flush();
				if(download>=total){
					nm.cancel(NOTIFICATION_PROGRESS_ID);
					Utils.open(this, file.getAbsolutePath());
				}
			}
		} catch (IOException e) {
			if (App.DEBUG)
				e.printStackTrace();
			nm.cancel(NOTIFICATION_PROGRESS_ID);
		}finally{
			IOHelper.forceClose(is);
			IOHelper.forceClose(fos);
		}
	}
	
	private void showProgress(){
		notification=new Notification(R.drawable.statusbar_icon,"正在下载饭否客户端",System.currentTimeMillis());
		notification.flags|=Notification.FLAG_ONGOING_EVENT;
		notification.flags|=Notification.FLAG_AUTO_CANCEL;
		remoteViews=new RemoteViews(getPackageName(), R.layout.download_notification);
		remoteViews.setTextViewText(R.id.download_notification_text, "正在下载饭否客户端 0%");
		remoteViews.setProgressBar(R.id.download_notification_progress, 100, 0, false);
		notification.contentView=remoteViews;
		
		Intent notificationIntent = new Intent(this, HomePage.class);
		PendingIntent contentIntent = PendingIntent.getActivity(this, 0, notificationIntent, 0);
		notification.contentIntent = contentIntent;

		nm.notify(NOTIFICATION_PROGRESS_ID, notification);
			
	}
	
	private void updateProgress(final int progress,String fileName){
				if(progress<=100){
					notification.contentView.setTextViewText(R.id.download_notification_text, "正在下载饭否客户端 "+progress+"%");
					notification.contentView.setInt(R.id.download_notification_progress, "setProgress", progress);
					nm.notify(NOTIFICATION_PROGRESS_ID, notification);	
				}

		
//		if(progress<100){
//			notification.contentView.setTextViewText(R.id.download_notification_text, "正在下载更新 "+progress+"%");
//			notification.contentView.setInt(R.id.download_notification_progress, "setProgress", progress);
//			nm.notify(NOTIFICATION_PROGRESS_ID, notification);	
//		}else{
//			notification.contentView.setTextViewText(R.id.download_notification_text, "饭否客户端新版本下载完成，点击安装");
//			notification.contentView.setInt(R.id.download_notification_progress, "setProgress", 100);
//			notification.contentIntent=getInstallPendingIntent(fileName);
//			nm.notify(NOTIFICATION_PROGRESS_ID, notification);	
//		}
	}
	
	@SuppressWarnings("unused")
	private PendingIntent getInstallPendingIntent(String fileName){
		String mimeType = MimeTypeMap.getSingleton().getMimeTypeFromExtension(
				Utils.getExtension(fileName));
		if (mimeType != null) {
			Intent intent=new Intent(Intent.ACTION_VIEW);
			intent.setDataAndType(Uri.fromFile(new File(fileName)), mimeType);
			intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
			PendingIntent pi = PendingIntent.getActivity(this, 0, intent, 0);
			return pi;
		}
		return null;
	}

}
