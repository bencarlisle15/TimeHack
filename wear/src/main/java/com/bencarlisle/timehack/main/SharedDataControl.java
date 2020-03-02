package com.bencarlisle.timehack.main;

import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.Handler;
import android.os.Looper;
import android.provider.CalendarContract;
import android.support.wearable.provider.WearableCalendarContract;
import android.util.Log;
import android.widget.Toast;

import com.bencarlisle.timelibrary.main.DataControllable;
import com.bencarlisle.timelibrary.main.Helper;
import com.bencarlisle.timelibrary.main.Returnable;
import com.bencarlisle.timelibrary.main.Task;
import com.google.android.gms.wearable.DataMap;
import com.google.android.gms.wearable.PutDataMapRequest;
import com.google.android.gms.wearable.Wearable;
import com.google.api.services.calendar.model.Event;

import net.openid.appauth.AuthState;

import org.json.JSONException;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Random;
import java.util.TimeZone;

public class SharedDataControl implements DataControllable {

    private Context context;
    private WearMessageHandler messageHandler;
    private static volatile int ID = new Random().nextInt();

    public SharedDataControl(Context context) {
        this.context = context;
        messageHandler = new WearMessageHandler();
    }

    public void addEvent(Event event) {
        sendAngIgnore("addEvent", Helper.serializeEvent(event));
    }

    public void addReturnable(Returnable returnable) {
        sendAngIgnore("addReturnable", returnable.serialize());
    }

    public void addTask(Task task) {
        sendAngIgnore("addTask", task.serialize());
    }

    @Override
    public void setAuthState(String authState) {
        context.getSharedPreferences("APP_AUTH", Context.MODE_PRIVATE).edit().putString("APP_AUTH", authState).apply();
    }

    public AuthState getAuthState() {
        String jsonString = context.getSharedPreferences("APP_AUTH", Context.MODE_PRIVATE).getString("APP_AUTH", null);
        if (jsonString != null) {
            try {
                return AuthState.jsonDeserialize(jsonString);
            } catch (JSONException e) {
                return null;
            }
        }
        return null;
    }

    @Override
    public void close() {
    }

    private int sendByteMessage(String message, byte[] data) {
        PutDataMapRequest putDataMapRequest = PutDataMapRequest.create("/bencarlisle");
        DataMap dataMap = putDataMapRequest.getDataMap();
        int id = getNextId();
        dataMap.putInt("id", id);
        dataMap.putBoolean("isResponse", false);
        dataMap.putByteArray("message", message.getBytes());
        dataMap.putByteArray("data", data);
        Wearable.getDataClient(context).putDataItem(putDataMapRequest.asPutDataRequest());
        return id;
    }

    private void sendAngIgnore(String message, byte[] data) {
        int id = sendByteMessage(message, data);
        new Thread(() -> {
            if (receivedMessage(id)) {
                showNoResponse(context);
            } else {
                showResponse(context);
            }
        }).start();
    }

    private boolean receivedMessage(int id) {
        long startTime = Calendar.getInstance().getTimeInMillis();
        while (!messageHandler.hasMessage(id) && Calendar.getInstance().getTimeInMillis() - startTime < 5000) {
            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
                Thread.yield();
            }
        }
        return messageHandler.getMessage(id);
    }

    private void showNoResponse(final Context context) {
        Helper.makeToast(context, "No response detected");
    }

    private  void showResponse(final Context context) {
        Helper.makeToast(context, "Successfully sent");
    }

    private synchronized static int getNextId() {
        return ID++;
    }

    public ArrayList<Event> getEvents() {
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.HOUR, 0);
        long dayStart = Calendar.getInstance().getTimeInMillis();
        calendar = Calendar.getInstance();
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.HOUR, 0);
        calendar.add(Calendar.DAY_OF_YEAR, 1);
        long dayEnd = calendar.getTimeInMillis();

        Uri.Builder builder = WearableCalendarContract.Instances.CONTENT_URI.buildUpon();
        ContentUris.appendId(builder, dayStart);
        ContentUris.appendId(builder, dayEnd);
        Uri uri = builder.build();

        String[] fields = {
                CalendarContract.Instances.TITLE,
                CalendarContract.Instances.BEGIN,
                CalendarContract.Instances.END,
                CalendarContract.Instances.DESCRIPTION
        };

        ContentResolver contentResolver = context.getContentResolver();
        Cursor cursor = contentResolver.query(uri, fields, null, null, null);
        ArrayList<Event> events = new ArrayList<>();
        if (cursor == null) {
            return null;
        }

        for (cursor.moveToFirst(); !cursor.isAfterLast(); cursor.moveToNext()) {
            String title = cursor.getString(0);
            String start = cursor.getString(1);
            String end = cursor.getString(2);
            String description = cursor.getString(3);
            Calendar startTime = Calendar.getInstance();
            calendar.setTimeZone(TimeZone.getDefault());
            startTime.setTimeInMillis(Long.parseLong(start));
            Calendar endTime = Calendar.getInstance();
            endTime.setTimeInMillis(Long.parseLong(end));
            if (description.matches("-?\\d+")) {
                Event event = Helper.getEvent(startTime, endTime, title, Integer.parseInt(description));
                event.setId(String.valueOf(getNextId()));
                events.add(event);
            }
            //is not an event we want otherwise
        }
        cursor.close();
        return events;
    }

    public void removeEvent(Event event) {
        sendAngIgnore("removeEvent", Helper.serializeEvent(event));
    }
}
