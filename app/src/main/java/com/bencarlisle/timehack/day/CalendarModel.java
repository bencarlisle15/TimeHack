package com.bencarlisle.timehack.day;

import android.content.Context;
import android.content.res.Resources;
import android.util.Log;
import android.util.TypedValue;

import com.bencarlisle.timehack.main.DataControl;
import com.bencarlisle.timehack.main.Event;
import com.bencarlisle.timehack.main.Merger;
import com.bencarlisle.timehack.main.Pollable;

import java.util.ArrayList;
import java.util.Calendar;

abstract class CalendarModel implements Pollable {

    final ArrayList<Event> events = new ArrayList<>();
    Context context;
    DataControl dataControl;

    abstract void clearViews();

    abstract void addEventViewToCalendar(int height, int width, int spacerHeight, int newDescriptionTextSize, Event event);

    abstract void deleteEvent(int id);

    CalendarModel(Context context) {
        this.context = context;
    }

    void start() {
        dataControl = new DataControl(context);
        initCalendar();
        new Merger(this).start();
        new Resetter(this).start();
//        createTempEvent();
    }

    public void poll() {
        synchronized (events) {
            ArrayList<Event> newEvents = dataControl.getEvents();
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
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            Calendar time = Calendar.getInstance();
            time.set(Calendar.HOUR_OF_DAY, 3);
            time.set(Calendar.MINUTE, 0);
            Calendar endTime = Calendar.getInstance();
            endTime.set(Calendar.HOUR_OF_DAY, 3);
            endTime.set(Calendar.MINUTE, 45);
            addEvent(new Event(time, endTime, "HELLO FRIEND"));
        }).start();
    }

    private void initCalendar() {
        //will be deleted
//        dataControl.clear();
        synchronized (events) {
            for (Event event : events) {
                Log.e("EVENT", event.toString());
                addEvent(event);
            }
        }
    }

    void clear() {
        clearViews();
        dataControl.clearEvents();
        events.clear();
    }

    float convertDPtoPixels(float dp) {
        Resources r = context.getResources();
        return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, r.getDisplayMetrics());
    }

    int getWindowWidth() {
        return Resources.getSystem().getDisplayMetrics().widthPixels;
    }

    private void addEvent(final Event event) {
        events.add(event);
        dataControl.addEvent(event);
        Log.e("EVENT", event.toString());
        addEventToCalendar(event);
    }

    private void addEventToCalendar(Event event) {
        Log.e("EVENT", "ADDING NEW EVENT" + event.toString());
        int startHour = event.getStartTime().get(Calendar.HOUR_OF_DAY);
        int startMinute = event.getStartTime().get(Calendar.MINUTE);
        int endHour = event.getEndTime().get(Calendar.HOUR_OF_DAY);
        int endMinute = event.getEndTime().get(Calendar.MINUTE);
        double numberOfHoursBeforeStart = startHour + startMinute / 60.0;
        double numberOfHours = (endHour - startHour) + (endMinute - startMinute) / 60.0;

        float rowHeight = convertDPtoPixels(75);
        float rowOffset = convertDPtoPixels(70);
        int height = (int) (rowHeight * numberOfHours);
        int width = (int) (getWindowWidth() - rowOffset);
        int spacerHeight = (int) (rowHeight * numberOfHoursBeforeStart);

        int newDescriptionTextSize = 10;
        if (numberOfHours >= 0.5) {
            newDescriptionTextSize = 15;
        } else if (numberOfHours >= 0.25) {
            newDescriptionTextSize = 12;
        }
        addEventViewToCalendar(height, width, spacerHeight, newDescriptionTextSize, event);
    }
}
