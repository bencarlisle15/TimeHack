package com.bencarlisle.timehack.main;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.content.Intent;
import android.widget.RemoteViews;

import com.bencarlisle.timehack.R;
import com.bencarlisle.timelibrary.main.VoiceRecognitionActivity;

/**
 * Implementation of App Widget functionality.
 */
public class StartWidget extends AppWidgetProvider {

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        // There may be multiple widgets active, so update all of them
        for (int appWidgetId : appWidgetIds) {
            RemoteViews rv = new RemoteViews(context.getPackageName(), R.layout.start_layout);
            Intent activityIntent = new Intent(context, VoiceRecognitionActivity.class);
            PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, activityIntent, 0);
            rv.setOnClickPendingIntent(R.id.start_button, pendingIntent);

            appWidgetManager.updateAppWidget(appWidgetId, rv);
        }
    }

    @Override
    public void onEnabled(Context context) {
        // Enter relevant functionality for when the first widget is created
    }

    @Override
    public void onDisabled(Context context) {
        // Enter relevant functionality for when the last widget is disabled
    }
}

