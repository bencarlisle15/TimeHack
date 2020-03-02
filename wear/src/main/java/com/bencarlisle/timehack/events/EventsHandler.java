package com.bencarlisle.timehack.events;

import android.app.Activity;
import android.content.res.Resources;
import android.util.Log;
import android.util.TypedValue;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.bencarlisle.timehack.R;
import com.bencarlisle.timehack.main.SharedDataControl;
import com.bencarlisle.timelibrary.main.Helper;
import com.bencarlisle.timelibrary.main.Merger;
import com.bencarlisle.timelibrary.main.Pollable;
import com.google.api.services.calendar.model.Event;

import java.util.ArrayList;
import java.util.Calendar;

class EventsHandler implements Pollable {

    private final ArrayList<Event> events = new ArrayList<>();
    private SharedDataControl dataControl;
    private EventsAdapter adapter;
    private Activity activity;
    private Merger merger;

    EventsHandler(Activity activity) {
        this.activity = activity;
        dataControl = new SharedDataControl(activity);
        adapter = new EventsAdapter(activity);
        ((ListView) activity.findViewById(R.id.scroll)).setAdapter(adapter);
        startPolling();
//        createTempEvent();
    }

    void startPolling() {
        merger = new Merger(this, 60);
        merger.start();
    }

    void stopPolling() {
        merger.interrupt();
        merger = null;
    }

    public void poll() {
        synchronized (events) {
            ArrayList<Event> newEvents = dataControl.getEvents();
            if (newEvents == null) {
                return;
            }
            for (Event event : newEvents) {
                if (!events.contains(event)) {
                    Log.e("EVENT FOUND", event.toString());
                    events.add(event);
                    addEventToCalendar(event);
                }
            }
            for (int i = 0; i < events.size(); i++) {
                if (!newEvents.contains(events.get(i))) {
                    Log.e("REMOVING EVENT ", events.get(i).toString());
                    deleteEvent(events.get(i).getId());
                }
            }
        }
    }

    private void createTempEvent() {
        new Thread(() -> {
            try {
                Thread.sleep(2200);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            Calendar time = Calendar.getInstance();
            time.set(Calendar.HOUR_OF_DAY, 23);
            time.set(Calendar.MINUTE, 5);
            Calendar endTime = Calendar.getInstance();
            endTime.set(Calendar.HOUR_OF_DAY, 23);
            endTime.set(Calendar.MINUTE, 45);
            addEvent(Helper.getEvent(time, endTime, "HELLO FRIEND", -1));
        }).start();
    }

    private float convertDPtoPixels(float dp) {
        Resources r = activity.getResources();
        return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, r.getDisplayMetrics());
    }

    private int getWindowWidth() {
        return Resources.getSystem().getDisplayMetrics().widthPixels;
    }

    private void addEvent(final Event event) {
        synchronized (events) {
            events.add(event);
            dataControl.addEvent(event);
            addEventToCalendar(event);
        }
    }

    void deleteEvent(View view) {
        int id = view.getId();
        deleteEvent(String.valueOf(id));
    }

    private void addEventToCalendar(Event event) {
        Calendar start = Helper.getCalendar(event.getStart());
        Calendar end = Helper.getCalendar(event.getEnd());
        int startHour = start.get(Calendar.HOUR_OF_DAY);
        int startMinute = start.get(Calendar.MINUTE);
        int endHour = end.get(Calendar.HOUR_OF_DAY);
        int endMinute = end.get(Calendar.MINUTE);
        double numberOfHoursBeforeStart = startHour + startMinute / 60.0;
        double numberOfHours = (endHour - startHour) + (endMinute - startMinute) / 60.0;

        float heightOffset = convertDPtoPixels(30);
        float rowHeight = convertDPtoPixels(50);
        float rowOffset = convertDPtoPixels(70);
        int height = (int) (rowHeight * numberOfHours);
        int width = (int) (getWindowWidth() - rowOffset);
        int spacerHeight = (int) (heightOffset + rowHeight * numberOfHoursBeforeStart);

        int newDescriptionTextSize = 10;
        if (numberOfHours >= 0.5) {
            newDescriptionTextSize = 15;
        } else if (numberOfHours >= 0.25) {
            newDescriptionTextSize = 12;
        }
        addEventViewToCalendar(height, width, spacerHeight, newDescriptionTextSize, event);
    }

    private void deleteEvent(String id) {
        activity.runOnUiThread(() -> {
            synchronized (events) {
                for (int i = 0; i < events.size(); i++) {
                    if (events.get(i).getId().equals(id)) {
                        dataControl.removeEvent(events.get(i));
                        adapter.removeView(i);
                        events.remove(i);
                        break;
                    }
                }
            }
        });
    }

    private void addEventViewToCalendar(int height, int width, int spacerHeight, int newDescriptionTextSize, Event event) {
        activity.runOnUiThread(() -> addEventViewToCalendarRunnable(height, width, spacerHeight, newDescriptionTextSize, event));
    }

    private void addEventViewToCalendarRunnable(int height, int width, int spacerHeight, int newDescriptionTextSize, Event event) {
        LinearLayout newEvent = (LinearLayout) View.inflate(activity, R.layout.calendar_event, null);
        TextView newDescription = newEvent.findViewById(R.id.event_text);
        TextView spacer = newEvent.findViewById(R.id.event_spacer);
        newDescription.setTextSize(newDescriptionTextSize);
        newDescription.setText(event.getSummary());
        newDescription.setHeight(height);
        newDescription.setWidth(width);
        newEvent.setMinimumWidth(getWindowWidth());
        spacer.setHeight(spacerHeight);
        newDescription.setId(Integer.parseInt(event.getId()));
        adapter.addView(newEvent);
    }
}