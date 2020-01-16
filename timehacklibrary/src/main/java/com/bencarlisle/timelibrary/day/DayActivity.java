package com.bencarlisle.timelibrary.day;

import android.os.Bundle;
import android.view.View;

import com.bencarlisle.timelibrary.main.GeneralActivity;
import com.bencarlisle.timelibrary.R;

public class DayActivity extends GeneralActivity {
    private transient CalendarControl calendar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_day);
        calendar = new CalendarControl(this);
    }

    public void deleteEvent(View view) {
        calendar.deleteEvent(view);
    }
}
