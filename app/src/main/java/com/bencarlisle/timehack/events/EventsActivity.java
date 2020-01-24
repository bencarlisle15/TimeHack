package com.bencarlisle.timehack.events;

import android.os.Bundle;
import android.view.View;

import com.bencarlisle.timehack.main.DataControl;
import com.bencarlisle.timelibrary.events.EventsHandler;
import com.bencarlisle.timehack.main.GeneralActivity;
import com.bencarlisle.timelibrary.R;

public class EventsActivity extends GeneralActivity {
    private transient EventsHandler calendar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_day);
        calendar = new EventsHandler(new DataControl(this), this, 1);
    }

    @SuppressWarnings("unused")
    public void deleteEvent(View view) {
        calendar.deleteEvent(view);
    }
}
