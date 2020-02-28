package com.bencarlisle.timehack.main;

import android.util.Log;

import com.bencarlisle.timelibrary.main.Helper;
import com.google.api.client.googleapis.auth.oauth2.GoogleCredential;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.client.util.DateTime;
import com.google.api.services.calendar.Calendar;
import com.google.api.services.calendar.model.Event;
import com.google.api.services.calendar.model.Events;

import java.io.IOException;
import java.util.ArrayList;

class CalendarControl {

    static void addEvent(Event event, String token) {
        Calendar calendar = getCalendar(token);
        if (calendar == null) {
            Log.e("Calendar", "IS null " + token);
            return;
        }

        try {
            calendar.events().insert("primary", event).execute();
            Log.e("CC", "Successfully executed");
        } catch (IOException e) {
            Log.e("CC", "Creation failed " + Log.getStackTraceString(e));
        }
    }

    static ArrayList<Event> getTaskEvents(String token) {
        Calendar calendar = getCalendar(token);
        if (calendar == null) {
            Log.e("event", "isnull '" + token + "'");
            return null;
        }
        java.util.Calendar currentDate = java.util.Calendar.getInstance();
        currentDate.add(java.util.Calendar.DAY_OF_YEAR, -1);
        currentDate.set(java.util.Calendar.HOUR, 0);
        currentDate.set(java.util.Calendar.MINUTE, 0);
        java.util.Calendar tomorrowDate = java.util.Calendar.getInstance();
        tomorrowDate.set(java.util.Calendar.HOUR, 0);
        tomorrowDate.set(java.util.Calendar.MINUTE, 0);
        try {
            Events allEvents = calendar.events().list("primary").setTimeMin(new DateTime(currentDate.getTime())).setTimeMax(new DateTime(tomorrowDate.getTime())).execute();
            ArrayList<Event> events = new ArrayList<>();
            for (Event event: allEvents.getItems()) {
                if (Helper.isTask(event)) {
                    events.add(event);
                }
            }
            return events;
        } catch (IOException e) {
            Log.e("CC", "Failed to get events");
        }
        return null;
    }

    private static Calendar getCalendar(String token) {
        if (token == null) {
            return null;
        }
        JsonFactory jsonFactory = JacksonFactory.getDefaultInstance();

        final NetHttpTransport httpTransport = new NetHttpTransport();
        return new Calendar.Builder(httpTransport, jsonFactory, new GoogleCredential().setAccessToken(token))
                .setApplicationName("TimeHack")
                .build();
    }
}
