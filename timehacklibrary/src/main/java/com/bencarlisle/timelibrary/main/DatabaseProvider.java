package com.bencarlisle.timelibrary.main;

import androidx.annotation.NonNull;
import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.net.Uri;

import java.util.HashMap;
import java.util.Objects;

public class DatabaseProvider extends ContentProvider {
    private static final String PROVIDER_NAME = "com.bencarlisle.timehack.main.MyProvider";
    private static final String URL = "content://" + PROVIDER_NAME + "/bencarlisle";
    public static final Uri CONTENT_URI = Uri.parse(URL);


    private DataControl db = null;
    private static final int uriCode = 1;
    private static final UriMatcher uriMatcher;
    private static HashMap<String, String> values;

    static {
        uriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
        uriMatcher.addURI(PROVIDER_NAME, "bencarlisle", uriCode);
        uriMatcher.addURI(PROVIDER_NAME, "bencarlisle15/*", uriCode);
    }

    @Override
    public int delete(@NonNull Uri uri, String selection, String[] selectionArgs) {
        if (uriMatcher.match(uri) == uriCode) {
            switch (selection) {
                case "events":
                    if (selectionArgs.length == 0) {
                        db.clearEvents();
                    } else {
                        for (String id : selectionArgs) {
                            db.removeEvent(Integer.parseInt(id));
                        }
                    }
                    break;
                case "tasks":
                    for (String id : selectionArgs) {
                        db.removeTask(Integer.parseInt(id));
                    }
                    break;
                case "returnables":
                    for (String id : selectionArgs) {
                        db.removeReturnable(Integer.parseInt(id));
                    }
                    break;
            }
        } else {
            throw new IllegalArgumentException("Unknown URI " + uri);
        }
        Objects.requireNonNull(getContext()).getContentResolver().notifyChange(uri, null);
        return 0;
    }

    @Override
    public String getType(@NonNull Uri uri) {
        return null;
    }

    @Override
    public Uri insert(@NonNull Uri uri, ContentValues values) {
        String tableName = values.getAsString("tableName");
        int id = values.getAsInteger("id");
        String description = values.getAsString("description");
        switch (tableName) {
            case "events":
                long startTime = values.getAsLong("startTime");
                long endTime = values.getAsLong("endTime");
                int taskId = values.getAsInteger("taskId");
                db.addEvent(new Event(id, description, startTime, endTime, taskId));
                break;
            case "returnables":
                String bitfield = values.getAsString("bitfield");
                int eventId = values.getAsInteger("eventId");
                String eventDescription = values.getAsString("eventDescription");
                long eventStartTime = values.getAsLong("startTime");
                long eventEndTime = values.getAsLong("endTime");
                int eventTaskId = values.getAsInteger("taskId");
                db.addReturnable(new Returnable(id, bitfield, new Event(eventId, eventDescription, eventStartTime, eventEndTime, eventTaskId)));
                break;
            case "tasks":
                long dueDateMillis = values.getAsLong("dueDataMillis");
                int priority = values.getAsInteger("priority");
                float hoursRequired = values.getAsFloat("hoursRequired");
                float hoursCompleted = values.getAsFloat("hoursCompleted");
                db.addTask(new Task(id, dueDateMillis, description, priority, hoursRequired, hoursCompleted));
                break;
        }
        return uri;
    }

    @Override
    public boolean onCreate() {
        db = new DataControl(getContext());
        return true;
    }

    @Override
    public Cursor query(@NonNull Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {
        return db.rawQuery(selection);
    }

    @Override
    public int update(@NonNull Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        return 0;
    }
}