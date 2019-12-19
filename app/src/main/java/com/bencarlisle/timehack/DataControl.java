package com.bencarlisle.timehack;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.util.ArrayList;


class DataControl extends SQLiteOpenHelper  {
    private SQLiteDatabase db;

    DataControl(Context context) {
        super(context, "Calendar.db", null, 1);
        db = this.getWritableDatabase();
        Event.setEventId(getNextId());
    }

    void clear() {
        db.execSQL("DELETE FROM Calendar;");
    }

    ArrayList<Event> getEvents() {
        Cursor cursor = db.rawQuery("SELECT * FROM Calendar",null);
        ArrayList<Event> events = new ArrayList<>();
        if (cursor.moveToFirst()) {
            while (!cursor.isAfterLast()) {
                int id = cursor.getInt(cursor.getColumnIndex("id"));
                String description = cursor.getString(cursor.getColumnIndex("description"));
                long startTime = cursor.getLong(cursor.getColumnIndex("startTime"));
                long endTime = cursor.getLong(cursor.getColumnIndex("endTime"));
                events.add(new Event(id, description, startTime, endTime));
                cursor.moveToNext();
            }
        }
        cursor.close();
        return events;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE Calendar (id INTEGER UNIQUE PRIMARY KEY NOT NULL, description TEXT NOT NULL, startTime BIGINTEGER NOT NULL, endTime BIGINTEGER NOT NULL, timestamp DATETIME DEFAULT CURRENT_TIMESTAMP);");
    }

    private int getNextId() {
        //sql injection is trivial
        Cursor cursor = db.rawQuery("SELECT id FROM Calendar ORDER BY id DESC", null);
        int id = 0;
        if (cursor.moveToFirst()) {
            if (!cursor.isAfterLast()) {
                id = cursor.getInt(cursor.getColumnIndex("id")) + 1;
            }
        }
        cursor.close();
        return id;
    }

    void addEvent(Event event) {
        //sql injection is trivial
        Log.e("EVENT", "s " + event.getStartTime().getTimeInMillis() + " e " + event.getEndTime().getTimeInMillis());
        db.execSQL("INSERT INTO Calendar (id, description, starttime, endtime) VALUES ('" + event.getId() + "', '" + event.getDescription() + "', '" + event.getStartTime().getTimeInMillis() + "', '" + event.getEndTime().getTimeInMillis() + "');");
    }

    void removeEvent(Event event) {
        db.execSQL("DELETE FROM Calendar WHERE id='" + event.getId() + "'");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS Calendar");
        onCreate(db);
    }
}
