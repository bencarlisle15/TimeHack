package com.bencarlisle.timehack;

import android.util.Log;

import com.framgia.library.calendardayview.CalendarDayView;

import java.util.ArrayList;

class CalendarControl {
    private MainActivity mainActivity;
    private ArrayList<Event> events = new ArrayList<>();

    CalendarControl(MainActivity mainActivity) {
        this.mainActivity = mainActivity;
    }

    Event  addEvent(Event event, boolean force) {
//        if (!force) {
//            for (Event e : events) {
//                if (e.isOverlapping(event)) {
//                    return e;
//                }
//            }
//        }
        events.add(event);
        Log.e("EVENT", event.toString());
        refreshCalendar();
        return null;
    }

    private void refreshCalendar() {
        mainActivity.runOnUiThread(new Runnable() {
            public void run() {
                CalendarDayView calendar = mainActivity.findViewById(R.id.calendar);
                calendar.setEvents(events);
                calendar.setPopups(events);
                calendar.refresh();
            }
        });
    }
}
