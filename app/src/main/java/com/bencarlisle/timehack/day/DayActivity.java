package com.bencarlisle.timehack.day;

import android.os.Bundle;
import android.view.View;

import com.bencarlisle.timehack.R;
import com.bencarlisle.timehack.main.GeneralActivity;

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
