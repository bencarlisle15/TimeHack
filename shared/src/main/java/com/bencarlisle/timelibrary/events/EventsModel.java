package com.bencarlisle.timelibrary.events;

import android.content.Context;
import android.content.res.Resources;
import android.util.Log;
import android.util.TypedValue;

import com.bencarlisle.timelibrary.main.DataControllable;
import com.bencarlisle.timelibrary.main.Event;
import com.bencarlisle.timelibrary.main.Merger;
import com.bencarlisle.timelibrary.main.Pollable;

import java.util.ArrayList;
import java.util.Calendar;

public abstract class EventsModel implements Pollable {

    protected final ArrayList<Event> events = new ArrayList<>();
    protected Context context;
    protected DataControllable dataControl;
    private Merger merger;

    protected abstract void addEventViewToCalendar(int height, int width, int spacerHeight, int newDescriptionTextSize, Event event);

    protected abstract void deleteEvent(int id);

    protected EventsModel(DataControllable dataControl, Context context) {
        this.dataControl = dataControl;
        this.context = context;
    }

    public void startPolling(int secondsToWait) {
        merger = new Merger(this, secondsToWait);
        merger.start();
    }

    public void stopPolling() {
        merger.interrupt();
        merger = null;
    }

    public synchronized void poll() {
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

    protected void createTempEvent() {
        new Thread(() -> {
            try {
                Thread.sleep(2200);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            Calendar time = Calendar.getInstance();
            time.set(Calendar.HOUR_OF_DAY, 3);
            time.set(Calendar.MINUTE, 0);
            Calendar endTime = Calendar.getInstance();
            endTime.set(Calendar.HOUR_OF_DAY, 3);
            endTime.set(Calendar.MINUTE, 45);
            addEvent(new Event(time, endTime, "HELLO FRIEND", -1));
        }).start();
    }

    protected float convertDPtoPixels(float dp) {
        Resources r = context.getResources();
        return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, r.getDisplayMetrics());
    }

    int getWindowWidth() {
        return Resources.getSystem().getDisplayMetrics().widthPixels;
    }

    private void addEvent(final Event event) {
        synchronized (events) {
            events.add(event);
            dataControl.addEvent(event);
            addEventToCalendar(event);
        }
    }

    private void addEventToCalendar(Event event) {
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
