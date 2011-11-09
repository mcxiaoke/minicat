package com.fanfou.app;

import com.fanfou.app.config.Actions;
import com.fanfou.app.config.Commons;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.content.Intent;
import android.widget.RemoteViews;

public class SimpleAppWidget extends AppWidgetProvider {

	@Override
	public void onReceive(Context context, Intent intent) {
		super.onReceive(context, intent);
	}

	@Override
	public void onUpdate(Context context, AppWidgetManager appWidgetManager,
			int[] appWidgetIds) {
		super.onUpdate(context, appWidgetManager, appWidgetIds);

		RemoteViews views = new RemoteViews(context.getPackageName(),
				R.layout.simplewidget);
		views.setOnClickPendingIntent(R.id.button1,
				getHomePendingIntent(context));
		views.setOnClickPendingIntent(R.id.button2,
				getPublicPendingIntent(context));
		views.setOnClickPendingIntent(R.id.button3,
				getWritePendingIntent(context));
		views.setOnClickPendingIntent(R.id.button4,
				getGalleryPendingIntent(context));
		views.setOnClickPendingIntent(R.id.button5,
				getCameraPendingIntent(context));
		appWidgetManager.updateAppWidget(appWidgetIds, views);

	}

	@Override
	public void onEnabled(Context context) {
		super.onEnabled(context);
	}

	@Override
	public void onDisabled(Context context) {
		super.onDisabled(context);
	}

	private PendingIntent getHomePendingIntent(Context context) {
		Intent intent = new Intent(context, HomePage.class);
		return getPendingIntent(context, intent);
	}

	private PendingIntent getMentionPendingIntent(Context context) {
		Intent intent = new Intent(context, HomePage.class);
		intent.putExtra(Commons.EXTRA_PAGE, 1);
		return getPendingIntent(context, intent);
	}

	private PendingIntent getMessagePendingIntent(Context context) {
		Intent intent = new Intent(context, HomePage.class);
		intent.putExtra(Commons.EXTRA_PAGE, 2);
		return getPendingIntent(context, intent);
	}

	private PendingIntent getPublicPendingIntent(Context context) {
		Intent intent = new Intent(context, HomePage.class);
		intent.putExtra(Commons.EXTRA_PAGE, 3);
		return getPendingIntent(context, intent);
	}

	private PendingIntent getWritePendingIntent(Context context) {
		Intent intent = new Intent(Actions.ACTION_SEND);
		intent.setPackage(context.getPackageName());
		return getPendingIntent(context, intent);
	}

	private PendingIntent getGalleryPendingIntent(Context context) {
		Intent intent = new Intent(Actions.ACTION_SEND_FROM_GALLERY);
		intent.setPackage(context.getPackageName());
		return getPendingIntent(context, intent);
	}

	private PendingIntent getCameraPendingIntent(Context context) {
		Intent intent = new Intent(Actions.ACTION_SEND_FROM_CAMERA);
		intent.setPackage(context.getPackageName());
		return getPendingIntent(context, intent);
	}

	private PendingIntent getMyProfilePendingIntent(Context context) {
		Intent intent = new Intent(context, MyProfilePage.class);
		return getPendingIntent(context, intent);
	}
	
	private PendingIntent getSearchPendingIntent(Context context) {
		Intent intent = new Intent(context, SearchPage.class);
		return getPendingIntent(context, intent);
	}

	private PendingIntent getPendingIntent(Context context, Intent intent) {
		PendingIntent pi = PendingIntent.getActivity(context, 0, intent, 0);
		return pi;
	}

}
