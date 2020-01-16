package com.bencarlisle.timehack.day;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.content.Context;
import android.content.Intent;
import android.widget.RemoteViews;

import com.bencarlisle.timehack.R;
import com.bencarlisle.timelibrary.day.CalendarModel;
import com.bencarlisle.timelibrary.main.Event;

class WidgetCalendarControl extends CalendarModel {
    private AppWidgetManager appWidgetManager;
    private int appWidgetId;

    WidgetCalendarControl(Context context, AppWidgetManager appWidgetManager, int appWidgetId) {
        super(context);
        this.appWidgetManager = appWidgetManager;
        this.appWidgetId = appWidgetId;
        start();
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

    public void clearViews() {
        synchronized (events) {
            for (int i = 0; i < events.size(); i++) {
                TimeRemoteViewsFactory.removeView(events.get(i).getId());
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
        Intent intent = new Intent(context, DayWidget.class);
        intent.setAction(action);
        return PendingIntent.getBroadcast(context, 0, intent, 0);
    }
}