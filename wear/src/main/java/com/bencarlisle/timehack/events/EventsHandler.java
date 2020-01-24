package com.bencarlisle.timehack.events;

import android.app.Activity;

import com.bencarlisle.timehack.main.SharedDataControl;

class EventsHandler extends com.bencarlisle.timelibrary.events.EventsHandler {

    EventsHandler(Activity activity) {
        super(new SharedDataControl(activity), activity, 10);
    }
}
