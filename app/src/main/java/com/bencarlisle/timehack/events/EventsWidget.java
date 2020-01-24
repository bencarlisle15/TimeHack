package com.bencarlisle.timehack.events;

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
import com.bencarlisle.timelibrary.main.VoiceRecognitionActivity;

import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class EventsWidget extends AppWidgetProvider {

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        // There may be multiple widgets active, so update all of them
        for (int appWidgetId : appWidgetIds) {
            new WidgetEventsHandler(context, appWidgetManager, appWidgetId);
            Intent intent = new Intent(context, TimeViewsService.class);
            intent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetId);
            intent.setData(Uri.parse(intent.toUri(Intent.URI_INTENT_SCHEME)));
            RemoteViews rv = new RemoteViews(context.getPackageName(), R.layout.main_day_layout);
            rv.setRemoteAdapter(R.id.scroll, intent);
            rv.setOnClickPendingIntent(R.id.start_button, getPendingSelfIntent(context));
            appWidgetManager.updateAppWidget(appWidgetId, rv);
        }
    }

    private PendingIntent getPendingSelfIntent(Context context) {
        Intent activityIntent = new Intent(context, VoiceRecognitionActivity.class);
        return PendingIntent.getActivity(context, 0, activityIntent, 0);
    }

    @Override
    public void onEnabled(Context context) {
    }

    public void onReceive(Context context, Intent intent) {
        super.onReceive(context, intent);
        Log.e("EVENT", "NEW ACTION " + intent.getAction());
        Matcher matcher = Pattern.compile("deleteEvent(\\d+)").matcher(Objects.requireNonNull(intent.getAction()));
        if (matcher.find()) {
            new DataControl(context).removeEvent(Integer.parseInt(Objects.requireNonNull(matcher.group(1))));
        }
    }

    @Override
    public void onDisabled(Context context) {
        // Enter relevant functionality for when the last widget is disabled
    }
}

