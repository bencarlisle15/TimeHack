package com.bencarlisle.timehack.main;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.util.ArrayList;


public class DataControl extends SQLiteOpenHelper  {
    private SQLiteDatabase db;

    public DataControl(Context context) {
        super(context, "Calendar.db", null, 1);
        db = this.getWritableDatabase();
//        onUpgrade(db, 0,0);
//        onCreate(db);
        Event.setEventId(getNextEventId());
        Returnable.setReturnableId(getNextReturnableId());
        Task.setTaskId(getNextTaskId());
    }

    public void clearEvents() {
        db.execSQL("DELETE FROM Calendar;");
    }
    public void clearTasks() {
        db.execSQL("DELETE FROM Tasks;");
    }

    public ArrayList<Event> getEvents() {
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

    public ArrayList<Returnable> getReturnables() {
        Cursor cursor = db.rawQuery("SELECT * FROM Returnables",null);
        ArrayList<Returnable> returnables = new ArrayList<>();
        if (cursor.moveToFirst()) {
            while (!cursor.isAfterLast()) {
                int id = cursor.getInt(cursor.getColumnIndex("id"));
                String bitfield = cursor.getString(cursor.getColumnIndex("days"));
                int eventId = cursor.getInt(cursor.getColumnIndex("eventId"));
                Event event = getEvent(eventId);
                if (event == null) {
                    cursor.moveToNext();
                    continue;
                }
                returnables.add(new Returnable(id, bitfield, event));
                cursor.moveToNext();
            }
        }
        cursor.close();
        return returnables;
    }

    private Event getEvent(int eventId) {
        Cursor cursor = db.rawQuery("SELECT * FROM Calendar WHERE id='" + eventId + "'", null);
        Event event = null;
        if (cursor.moveToFirst() && !cursor.isAfterLast()) {
            int id = cursor.getInt(cursor.getColumnIndex("id"));
            String description = cursor.getString(cursor.getColumnIndex("description"));
            long startTime = cursor.getLong(cursor.getColumnIndex("startTime"));
            long endTime = cursor.getLong(cursor.getColumnIndex("endTime"));
            event = new Event(id, description, startTime, endTime);
        }
        cursor.close();
        return event;
    }

    public ArrayList<Task> getTasks() {
        Cursor cursor = db.rawQuery("SELECT * FROM Tasks",null);
        ArrayList<Task> tasks = new ArrayList<>();
        if (cursor.moveToFirst()) {
            while (!cursor.isAfterLast()) {
                int id = cursor.getInt(cursor.getColumnIndex("id"));
                String description = cursor.getString(cursor.getColumnIndex("description"));
                long dueDate = cursor.getLong(cursor.getColumnIndex("dueDate"));
                float hoursRequired = cursor.getFloat(cursor.getColumnIndex("hoursRequired"));
                float hoursCompleted = cursor.getFloat(cursor.getColumnIndex("hoursCompleted"));
                int priority = cursor.getInt(cursor.getColumnIndex("priority"));
                tasks.add(new Task(id, dueDate, description, priority, hoursRequired, hoursCompleted));
                cursor.moveToNext();
            }
        }
        cursor.close();
        return tasks;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE Calendar (id INTEGER UNIQUE PRIMARY KEY NOT NULL, description TEXT NOT NULL, startTime BIGINTEGER NOT NULL, endTime BIGINTEGER NOT NULL, timestamp DATETIME DEFAULT CURRENT_TIMESTAMP);");
        db.execSQL("CREATE TABLE Returnables (id INTEGER UNIQUE PRIMARY KEY NOT NULL, days TEXT NOT NULL, eventId INTEGER NOT NULL, timestamp DATETIME DEFAULT CURRENT_TIMESTAMP);");
        db.execSQL("CREATE TABLE Tasks (id INTEGER UNIQUE PRIMARY KEY NOT NULL, description TEXT NOT NULL, dueDate BIGINTEGER NOT NULL, priority INTEGER, hoursRequired FLOAT, hoursCompleted FLOAT, timestamp DATETIME DEFAULT CURRENT_TIMESTAMP);");
    }

    private int getNextEventId() {
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

    private int getNextReturnableId() {
        //sql injection is trivial
        Cursor cursor = db.rawQuery("SELECT id FROM Returnables ORDER BY id DESC", null);
        int id = 0;
        if (cursor.moveToFirst()) {
            if (!cursor.isAfterLast()) {
                id = cursor.getInt(cursor.getColumnIndex("id")) + 1;
            }
        }
        cursor.close();
        return id;
    }

    private int getNextTaskId() {
        //sql injection is trivial
        Cursor cursor = db.rawQuery("SELECT id FROM Tasks ORDER BY id DESC", null);
        int id = 0;
        if (cursor.moveToFirst()) {
            if (!cursor.isAfterLast()) {
                id = cursor.getInt(cursor.getColumnIndex("id")) + 1;
            }
        }
        cursor.close();
        return id;
    }


    public void addEvent(Event event) {
        //sql injection is trivial
        Log.e("EVENT", "s " + event.getStartTime().getTimeInMillis() + " e " + event.getEndTime().getTimeInMillis());
        db.execSQL("INSERT INTO Calendar (id, description, startTime, endTime) VALUES ('" + event.getId() + "', '" + event.getDescription() + "', '" + event.getStartTime().getTimeInMillis() + "', '" + event.getEndTime().getTimeInMillis() + "');");
    }

    public void addReturnable(Returnable returnable) {
        Log.e("RETURNABLE", "ADDING returnable" + returnable);
        db.execSQL("INSERT INTO Returnables (id, days, eventId) VALUES ('" + returnable.getId() + "', '" + returnable.getBitfield() + "', '" + returnable.getEvent().getId() + "');");
    }

    public void addTask(Task task) {
        //sql injection is trivial
        Log.e("TASK", "ADDING TASK " + task);
        db.execSQL("INSERT INTO Tasks (id, description, dueDate, priority, hoursRequired, hoursCompleted) VALUES ('" + task.getId() + "', '" + task.getDescription() + "', '" + task.getDueDate().getTimeInMillis() + "', '" + task.getPriority() + "', '" + task.getHoursRequired() + "', '" + task.getHoursCompleted() + "');");
    }

    public void removeEvent(int id) {
        db.execSQL("DELETE FROM Calendar WHERE id='" + id + "'");
    }

    public void removeReturnables(int id) {
        db.execSQL("DELETE FROM Returnables WHERE id='" + id + "'");
    }

    public void removeTask(int id) {
        db.execSQL("DELETE FROM Tasks WHERE id='" + id + "'");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS Calendar");
        db.execSQL("DROP TABLE IF EXISTS Returnables");
        db.execSQL("DROP TABLE IF EXISTS Tasks");
        onCreate(db);
    }
}
