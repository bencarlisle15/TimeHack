package com.bencarlisle.timehack.day;

import android.app.Activity;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.bencarlisle.timehack.main.Event;
import com.bencarlisle.timehack.R;

class CalendarControl extends CalendarModel {
    private Adapter adapter;
    private Activity activity;

    CalendarControl(Activity activity) {
        super(activity);
        this.activity = activity;
        adapter =  new Adapter(activity);
        ((ListView) activity.findViewById(R.id.scroll)).setAdapter(adapter);
        start();
    }

    public void clearViews() {
        ListView calendar = activity.findViewById(R.id.scroll);
        calendar.removeViewsInLayout(1, calendar.getChildCount() - 1);
    }

    void deleteEvent(View view) {
        int id = view.getId();
        deleteEvent(id);
    }

    void deleteEvent(int id) {
        activity.runOnUiThread(() -> {
            synchronized (events) {
                dataControl.removeEvent(id);
                for (int i = 0; i < events.size(); i++) {
                    if (events.get(i).getId() == id) {
                        adapter.removeView(i);
                        events.remove(i);
                        break;
                    }
                }
            }
        });
    }

    void addEventViewToCalendar(int height, int width, int spacerHeight, int newDescriptionTextSize, Event event) {
        activity.runOnUiThread(() -> addEventViewToCalendarRunnable(height, width, spacerHeight, newDescriptionTextSize, event));
    }

    private void addEventViewToCalendarRunnable(int height, int width, int spacerHeight, int newDescriptionTextSize, Event event) {
        LinearLayout newEvent= (LinearLayout) activity.getLayoutInflater().inflate(R.layout.calendar_event, null);
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
