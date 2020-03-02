package com.bencarlisle.timehack.main;

import android.content.Context;
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

import net.openid.appauth.AuthState;
import net.openid.appauth.AuthorizationService;

import java.io.IOException;
import java.util.ArrayList;

class CalendarControl {

    static void addEvent(Event event, Context context, AuthState authState) {
        if (authState.getNeedsTokenRefresh()) {
            AuthorizationService authorizationService = new AuthorizationService(context);
            authState.performActionWithFreshTokens(authorizationService, (accessToken, idToken, ex) -> {
                if (accessToken != null) {
                    new Thread(() -> addEvent(event, accessToken)).start();
                } else {
                    Log.e("CC", "Creation failed calendar is null " + Log.getStackTraceString(ex));
                }
            });
            authorizationService.dispose();
        } else {
            new Thread(() -> addEvent(event, authState.getAccessToken())).start();
        }
    }

    private static void addEvent(Event event, String accessToken) {
        Calendar calendar = getCalendar(accessToken);
        if (calendar == null) {
            Log.e("CC", "Creation failed calendar is null");
            return;
        }
        try {
            calendar.events().insert("primary", event).execute();
            Log.e("CC", "Successfully executed");
        } catch (IOException e) {
            Log.e("CC", "Creation failed " + Log.getStackTraceString(e));
        }
    }

    static void removeEvent(Event event, Context context, AuthState authState) {
        if (authState.getNeedsTokenRefresh()) {
            AuthorizationService authorizationService = new AuthorizationService(context);
            authState.performActionWithFreshTokens(authorizationService, (accessToken, idToken, ex) -> {
                if (accessToken != null) {
                    new Thread(() -> removeEvent(event, accessToken)).start();
                } else {
                    Log.e("CC", "Creation failed calendar is null " + Log.getStackTraceString(ex));
                }
            });
            authorizationService.dispose();
        } else {
            new Thread(() -> removeEvent(event, authState.getAccessToken())).start();
        }
    }

    private static void removeEvent(Event event, String accessToken) {
        Calendar calendar = getCalendar(accessToken);
        ArrayList<Event> events = getEvents(accessToken, true);
        if (calendar == null || events == null) {
            Log.e("CC", "Creation failed calendar is null");
            return;
        }

        String id = null;
        for (Event currentEvent: events) {
            if (currentEvent.getSummary().equals(event.getSummary()) && Helper.isEqualDates(currentEvent.getStart(), event.getStart()) && Helper.isEqualDates(currentEvent.getEnd(), event.getEnd())) {
                id = currentEvent.getId();
                Log.e("FOUND id" ,id );
                break;
            }
        }
        if (id == null) {
            Log.e("CC", "Event not found");
            return;
        }
        try {
            calendar.events().delete("primary", id).execute();
            Log.e("CC", "Successfully executed");
        } catch (IOException e) {
            Log.e("CC", "Creation failed " + Log.getStackTraceString(e));
        }
    }

    static ArrayList<Event> getTaskEvents(AuthState authState) {
        ArrayList<Event> allEvents = getEvents(authState.getAccessToken(), false);
        if (allEvents == null) {
            return null;
        }
        ArrayList<Event> events = new ArrayList<>();
        for (Event event: allEvents) {
            if (Helper.isTask(event)) {
                events.add(event);
            }
        }
        return events;
    }

    private static ArrayList<Event> getEvents(String accessToken, boolean isToday) {
        Calendar calendar = getCalendar(accessToken);
        if (calendar == null) {
            Log.e("CC", "Failed to get events, calendar is null");
            return null;
        }
        java.util.Calendar currentDate = java.util.Calendar.getInstance();
        if (!isToday) {
            currentDate.add(java.util.Calendar.DAY_OF_YEAR, -1);
        }
        currentDate.set(java.util.Calendar.HOUR, 0);
        currentDate.set(java.util.Calendar.MINUTE, 0);
        java.util.Calendar tomorrowDate = java.util.Calendar.getInstance();
        if (isToday) {
            tomorrowDate.add(java.util.Calendar.DAY_OF_YEAR, 1);
        }
        tomorrowDate.set(java.util.Calendar.HOUR, 0);
        tomorrowDate.set(java.util.Calendar.MINUTE, 0);
        try {
            Events events = calendar.events().list("primary").setTimeMin(new DateTime(currentDate.getTime())).setTimeMax(new DateTime(tomorrowDate.getTime())).execute();
            return new ArrayList<>(events.getItems());
        } catch (IOException e) {
            Log.e("CC", "Failed to get events " + Log.getStackTraceString(e));
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
