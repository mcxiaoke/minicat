package com.fanfou.app.hd.appwidget;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.content.Intent;
import android.widget.RemoteViews;

import com.fanfou.app.hd.R;
import com.fanfou.app.hd.UIHome;
import com.fanfou.app.hd.UIProfile;
import com.fanfou.app.hd.UISearch;
import com.fanfou.app.hd.UIStart;
import com.fanfou.app.hd.service.Constants;

/**
 * @author mcxiaoke
 * @version 1.0 2011.11.08
 * @version 1.5 2011.11.11
 * @version 1.6 2011.12.19
 * 
 */
public class WidgetSmall extends AppWidgetProvider {

	@Override
	public void onUpdate(Context context, AppWidgetManager appWidgetManager,
			int[] appWidgetIds) {
		super.onUpdate(context, appWidgetManager, appWidgetIds);

		RemoteViews views = new RemoteViews(context.getPackageName(),
				R.layout.widget_small);
		views.setOnClickPendingIntent(R.id.widget_home,
				getSplashPendingIntent(context));
		views.setOnClickPendingIntent(R.id.widget_write,
				getWritePendingIntent(context));
		views.setOnClickPendingIntent(R.id.widget_gallery,
				getGalleryPendingIntent(context));
		views.setOnClickPendingIntent(R.id.widget_camera,
				getCameraPendingIntent(context));
		appWidgetManager.updateAppWidget(appWidgetIds, views);

	}

	private PendingIntent getSplashPendingIntent(Context context) {
		Intent intent = new Intent(context, UIStart.class);
		return getPendingIntent(context, intent);
	}

	private PendingIntent getMentionPendingIntent(Context context) {
		Intent intent = new Intent(context, UIHome.class);
//		intent.putExtra(Constants.EXTRA_PAGE, 1);
		return getPendingIntent(context, intent);
	}

	private PendingIntent getMessagePendingIntent(Context context) {
		Intent intent = new Intent(context, UIHome.class);
//		intent.putExtra(Constants.EXTRA_PAGE, 2);
		return getPendingIntent(context, intent);
	}

	private PendingIntent getPublicPendingIntent(Context context) {
		Intent intent = new Intent(context, UIHome.class);
//		intent.putExtra(Constants.EXTRA_PAGE, 3);
		return getPendingIntent(context, intent);
	}

	private PendingIntent getWritePendingIntent(Context context) {
		Intent intent = new Intent(Constants.ACTION_SEND);
		intent.setPackage(context.getPackageName());
		return getPendingIntent(context, intent);
	}

	private PendingIntent getGalleryPendingIntent(Context context) {
		Intent intent = new Intent(Constants.ACTION_SEND_FROM_GALLERY);
		intent.setPackage(context.getPackageName());
		return getPendingIntent(context, intent);
	}

	private PendingIntent getCameraPendingIntent(Context context) {
		Intent intent = new Intent(Constants.ACTION_SEND_FROM_CAMERA);
		intent.setPackage(context.getPackageName());
		return getPendingIntent(context, intent);
	}

	private PendingIntent getMyProfilePendingIntent(Context context) {
		Intent intent = new Intent(context, UIProfile.class);
		return getPendingIntent(context, intent);
	}

	private PendingIntent getSearchPendingIntent(Context context) {
		Intent intent = new Intent(context, UISearch.class);
		return getPendingIntent(context, intent);
	}

	private PendingIntent getPendingIntent(Context context, Intent intent) {
		PendingIntent pi = PendingIntent.getActivity(context, 0, intent, 0);
		return pi;
	}

}
