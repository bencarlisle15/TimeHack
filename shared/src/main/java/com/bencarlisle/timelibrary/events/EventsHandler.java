package com.bencarlisle.timelibrary.events;

import android.app.Activity;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.bencarlisle.timelibrary.R;
import com.bencarlisle.timelibrary.main.DataControllable;
import com.bencarlisle.timelibrary.main.Event;
import com.bencarlisle.timelibrary.main.EventAlarmManager;

public class EventsHandler extends EventsModel {
    private EventsAdapter adapter;
    private Activity activity;

    public EventsHandler(DataControllable dataControllable, Activity activity, int secondsToWait) {
        super(dataControllable, activity);
        this.activity = activity;
        adapter = new EventsAdapter(activity);
        ((ListView) activity.findViewById(R.id.scroll)).setAdapter(adapter);
        startPolling(secondsToWait);
//        createTempEvent();
    }

    public void deleteEvent(View view) {
        int id = view.getId();
        deleteEvent(id);
    }

    protected void deleteEvent(int id) {
       activity.runOnUiThread(() -> {
            synchronized (events) {
                dataControl.removeEvent(id);
                for (int i = 0; i < events.size(); i++) {
                    if (events.get(i).getId() == id) {
                        adapter.removeView(i);
                        EventAlarmManager.removeAlarm(context, events.get(i));
                        events.remove(i);
                        break;
                    }
                }
            }
        });
    }

    protected void addEventViewToCalendar(int height, int width, int spacerHeight, int newDescriptionTextSize, Event event) {
        EventAlarmManager.addAlarm(activity, event);
        activity.runOnUiThread(() -> addEventViewToCalendarRunnable(height, width, spacerHeight, newDescriptionTextSize, event));
    }

    private void addEventViewToCalendarRunnable(int height, int width, int spacerHeight, int newDescriptionTextSize, Event event) {
        LinearLayout newEvent = (LinearLayout) View.inflate(context, R.layout.calendar_event, null);
        TextView newDescription = newEvent.findViewById(R.id.event_text);
        TextView spacer = newEvent.findViewById(R.id.event_spacer);
        newDescription.setTextSize(newDescriptionTextSize);
        newDescription.setText(event.getDescription());
        newDescription.setHeight(height);
        newDescription.setWidth(width);
        newEvent.setMinimumWidth(getWindowWidth());
        spacer.setHeight(spacerHeight);
        newDescription.setId(event.getId());
        adapter.addView(newEvent);
    }
}
