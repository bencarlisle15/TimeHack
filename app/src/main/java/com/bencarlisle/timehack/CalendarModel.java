package com.bencarlisle.timehack;

import android.content.Context;
import android.content.res.Resources;
import android.util.Log;
import android.util.TypedValue;

import java.util.ArrayList;
import java.util.Calendar;

abstract class CalendarModel {

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

    void poll() {
        synchronized (events) {
            ArrayList<Event> newEvents = dataControl.getEvents();
            Log.e("EVENT", "FOUND " + newEvents.size() + " events");
            for (Event event : newEvents) {
                if (!events.contains(event)) {
                    Log.e("EVENT FOUND", event.toString());
                    events.add(event);
                    addEventToCalendar(event);
                }
            }
            for (Event event : events) {
                if (!newEvents.contains(event)) {
                    Log.e("REMOVING EVENT ", event.toString());
                    deleteEvent(event.getId());
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
            addEvent(new Event(time, endTime, "HELLO FRIEND"), false);
        }).start();
    }

    private void initCalendar() {
        //will be deleted
//        dataControl.clear();
        synchronized (events) {
            for (Event event : events) {
                Log.e("EVENT", event.toString());
                addEvent(event, false);
            }
        }
    }

    void clear() {
        clearViews();
        dataControl.clear();
        events.clear();
    }

    float convertDPtoPixels(float dp) {
        Resources r = context.getResources();
        return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, r.getDisplayMetrics());
    }

    int getWindowWidth() {
        return Resources.getSystem().getDisplayMetrics().widthPixels;
    }

    Event addEvent(final Event event, @SuppressWarnings("SameParameterValue") boolean force) {
//        if (!force) {
//            for (Event e : events) {
//                if (e.isOverlapping(event)) {
//                    return e;
//                }
//            }
//        }
        events.add(event);
        dataControl.addEvent(event);
        Log.e("EVENT", event.toString());
        addEventToCalendar(event);
        return null;
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
