package com.bencarlisle.timehack.events;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.content.Context;
import android.content.Intent;
import android.widget.RemoteViews;

import com.bencarlisle.timehack.R;
import com.bencarlisle.timehack.main.DataControl;
import com.bencarlisle.timelibrary.events.EventsModel;
import com.bencarlisle.timelibrary.main.Event;

class WidgetEventsHandler extends EventsModel {
    private AppWidgetManager appWidgetManager;
    private int appWidgetId;

    WidgetEventsHandler(Context context, AppWidgetManager appWidgetManager, int appWidgetId) {
        super(new DataControl(context), context);
        this.appWidgetManager = appWidgetManager;
        this.appWidgetId = appWidgetId;
        startPolling(1);
//        createTempEvent();
    }

    protected void deleteEvent(int id) {
        synchronized (events) {
            dataControl.removeEvent(id);
            TimeRemoteViewsFactory.removeView(id);
            for (int i = 0; i < events.size(); i++) {
                if (events.get(i).getId() == id) {
                    events.remove(i);
                    break;
                }
            }
        }
        updateWidget();
    }

    protected void addEventViewToCalendar(int height, int width, int spacerHeight, int newDescriptionTextSize, Event event) {
        width -= convertDPtoPixels(25);
        RemoteViews newEvent = new RemoteViews(context.getPackageName(), R.layout.calendar_event);
        newEvent.setFloat(R.id.event_text, "setTextSize", newDescriptionTextSize);
        newEvent.setViewPadding(R.id.event_text, 15,15, 0, 0);
        newEvent.setTextViewText(R.id.event_text, event.getDescription());
        newEvent.setInt(R.id.event_text, "setHeight", height);
        newEvent.setInt(R.id.event_text, "setWidth", width);
        newEvent.setInt(R.id.event_spacer, "setHeight", spacerHeight);
        newEvent.setOnClickPendingIntent(R.id.event_text, getPendingSelfIntent("deleteEvent" + event.getId()));
        TimeRemoteViewsFactory.addView(event.getId(), newEvent);
        updateWidget();
    }

    private void updateWidget() {
        appWidgetManager.notifyAppWidgetViewDataChanged(appWidgetId, R.id.scroll);
    }

    private PendingIntent getPendingSelfIntent(String action) {
        Intent intent = new Intent(context, EventsWidget.class);
        intent.setAction(action);
        return PendingIntent.getBroadcast(context, 0, intent, 0);
    }
}