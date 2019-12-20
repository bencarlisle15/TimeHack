package com.bencarlisle.timehack.tasks;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.util.Log;
import android.widget.RemoteViews;

import com.bencarlisle.timehack.R;
import com.bencarlisle.timehack.main.DataControl;

import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Implementation of App Widget functionality.
 */
public class TaskWidget extends AppWidgetProvider {

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        // There may be multiple widgets active, so update all of them
        for (int appWidgetId : appWidgetIds) {
            new WidgetTaskHandler(context, appWidgetManager, appWidgetId);
            Intent intent = new Intent(context, TaskViewsService.class);
            intent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetId);
            intent.setData(Uri.parse(intent.toUri(Intent.URI_INTENT_SCHEME)));
            RemoteViews rv = new RemoteViews(context.getPackageName(), R.layout.main_tasks_layout);
            rv.setRemoteAdapter(R.id.tasks, intent);
            rv.setOnClickPendingIntent(R.id.start_button, getPendingSelfIntent(context));
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

    public void onReceive(Context context, Intent intent) {
        super.onReceive(context, intent);
        Log.e("TASK", "NEW ACTION " + intent.getAction());
        Matcher matcher = Pattern.compile("deleteTask(\\d+)").matcher(Objects.requireNonNull(intent.getAction()));
        if (matcher.find()) {
            new DataControl(context).removeTask(Integer.parseInt(Objects.requireNonNull(matcher.group(1))));
        }
    }

    private PendingIntent getPendingSelfIntent(Context context) {
        Intent activityIntent = new Intent(context, VoiceRecognitionActivity.class);
        return PendingIntent.getActivity(context, 0, activityIntent, 0);
    }
}

