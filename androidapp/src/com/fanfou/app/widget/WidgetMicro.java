package com.fanfou.app.widget;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.content.Intent;
import android.widget.RemoteViews;

import com.fanfou.app.R;
import com.fanfou.app.config.Actions;

/**
 * @author mcxiaoke
 * @version 1.0 2011.11.08
 * @version 1.5 2011.11.11
 * @version 2.0 2011.11.14
 * 
 */
public class WidgetMicro extends AppWidgetProvider {

	@Override
	public void onUpdate(Context context, AppWidgetManager appWidgetManager,
			int[] appWidgetIds) {
		super.onUpdate(context, appWidgetManager, appWidgetIds);

		RemoteViews views = new RemoteViews(context.getPackageName(),
				R.layout.widget_micro);
		views.setOnClickPendingIntent(R.id.widget_write,
				getWritePendingIntent(context));
		appWidgetManager.updateAppWidget(appWidgetIds, views);

	}

	private PendingIntent getWritePendingIntent(Context context) {
		Intent intent = new Intent(Actions.ACTION_SEND);
		intent.setPackage(context.getPackageName());
		return getPendingIntent(context, intent);
	}

	private PendingIntent getPendingIntent(Context context, Intent intent) {
		PendingIntent pi = PendingIntent.getActivity(context, 0, intent, 0);
		return pi;
	}

}
