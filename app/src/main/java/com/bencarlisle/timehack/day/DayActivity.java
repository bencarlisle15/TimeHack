package com.bencarlisle.timehack.day;

import android.os.Bundle;
import android.view.View;

import com.bencarlisle.timehack.R;
import com.bencarlisle.timehack.main.Event;
import com.bencarlisle.timehack.main.GeneralActivity;
import com.bencarlisle.timehack.main.Parser;

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

    protected String checkAndParseResult(String result) {
        Event event = Parser.parseEventResult(result);
        if (event == null) {
            return null;
        }
        Event eventResult = calendar.addEvent(event, false);
        return eventResult  == null ? "Successfully Added" : ("Conflicting event: " + eventResult );
    }
}
