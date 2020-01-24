package com.bencarlisle.timehack.main;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.bencarlisle.timelibrary.main.DataControllable;
import com.bencarlisle.timelibrary.main.Event;
import com.bencarlisle.timelibrary.main.Returnable;
import com.bencarlisle.timelibrary.main.Task;

import java.util.ArrayList;
import java.util.Calendar;

public class DataControl extends SQLiteOpenHelper implements DataControllable {
    private SQLiteDatabase db;

    public DataControl(Context context) {
        super(context, "Calendar.db", null, 1);
        db = this.getWritableDatabase();
//        onUpgrade(db, 0,0);
        setIds();
    }

    private void setIds() {
        Event.setEventId(getNextEventId());
        Returnable.setReturnableId(getNextReturnableId());
        Task.setTaskId(getNextTaskId());
    }

    void clearEvents() {
        db.execSQL("DELETE FROM Calendar;");
    }

    public ArrayList<Event> getEvents() {
        Cursor cursor = db.rawQuery("SELECT * FROM Calendar;",null);
        return getEvents(cursor);
    }

    public ArrayList<Returnable> getReturnables() {
        Cursor cursor = db.rawQuery("SELECT * FROM Returnables;",null);
        return getReturnables(cursor);
    }

    public ArrayList<Task> getTasks() {
        Cursor cursor = db.rawQuery("SELECT * FROM Tasks;",null);
        return getTasks(cursor);
    }

    void runOrganizer() {
        int day = Calendar.getInstance().get(Calendar.DATE);
        db.execSQL("UPDATE Organized SET lastDay=" + day + ";");
    }


    boolean isAlreadyRun() {
        int day = Calendar.getInstance().get(Calendar.DATE);
        Cursor cursor = db.rawQuery("SELECT * FROM Organized;",null);
        cursor.moveToFirst();
        int lastDay = cursor.getInt(cursor.getColumnIndex("lastDay"));
        cursor.close();
        return lastDay == day;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE Calendar (id INTEGER UNIQUE PRIMARY KEY NOT NULL, description TEXT NOT NULL, startTime BIGINTEGER NOT NULL, endTime BIGINTEGER NOT NULL, taskId INTEGER NOT NULL, timestamp DATETIME DEFAULT CURRENT_TIMESTAMP);");
        db.execSQL("CREATE TABLE Returnables (id INTEGER UNIQUE PRIMARY KEY NOT NULL, days TEXT NOT NULL, eventId INTEGER NOT NULL, description TEXT NOT NULL, startTime BIGINTEGER NOT NULL, endTime BIGINTEGER NOT NULL, taskId INTEGER NOT NULL, timestamp DATETIME DEFAULT CURRENT_TIMESTAMP);");
        db.execSQL("CREATE TABLE Tasks (id INTEGER UNIQUE PRIMARY KEY NOT NULL, description TEXT NOT NULL, dueDate BIGINTEGER NOT NULL, priority INTEGER, hoursRequired FLOAT, hoursCompleted FLOAT, timestamp DATETIME DEFAULT CURRENT_TIMESTAMP);");
        db.execSQL("CREATE TABLE Organized (lastDay INTEGER NOT NULL)");
        db.execSQL("INSERT INTO Organized (lastDay) VALUES (-1)");
    }

    int getNextEventId() {
        //sql injection is trivial
        Cursor cursor = db.rawQuery(getSQL("getNextEventId", null), null);
        return getNextId(cursor);
    }

    int getNextReturnableId() {
        //sql injection is trivial
        Cursor cursor = db.rawQuery(getSQL("getNextReturnableId", null), null);
        return getNextId(cursor);
    }

    int getNextTaskId() {
        //sql injection is trivial
        Cursor cursor = db.rawQuery(getSQL("getNextTaskId", null), null);
        return getNextId(cursor);
    }


    public void addEvent(Event event) {
        //sql injection is trivial
        Log.e("DATA EVENT", "Adding event " + event);
        db.execSQL(getSQL("addEvent", event));
    }

    public void addReturnable(Returnable returnable) {
        Log.e("DATA RETURNABLE", "ADDING returnable" + returnable);
        db.execSQL(getSQL("addReturnable", returnable));
    }

    public void addTask(Task task) {
        //sql injection is trivial
        Log.e("DATA TASK", "ADDING TASK " + task);
        db.execSQL(getSQL("addTask", task));
    }

    void reboot() {
        onUpgrade(db, 0,0);
    }

    public void removeEvent(int id) {
        db.execSQL(getSQL("removeEvent", id));
    }

    public void removeReturnable(int id) {
        db.execSQL(getSQL("removeReturnable", id));
    }

    public void removeTask(int id) {
        db.execSQL(getSQL("removeTask", id));
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS Calendar");
        db.execSQL("DROP TABLE IF EXISTS Returnables");
        db.execSQL("DROP TABLE IF EXISTS Tasks");
        db.execSQL("DROP TABLE IF EXISTS Organized");
        onCreate(db);
    }

    ArrayList<Returnable> getReturnablesOnDay(int day) {
        Cursor cursor = db.rawQuery("SELECT * FROM Returnables WHERE SUBSTR(days, " + (day + 1) + ", 1)='1';", null);
        ArrayList<Returnable> returnables = new ArrayList<>();
        if (cursor.moveToFirst()) {
            while (!cursor.isAfterLast()) {
                int id = cursor.getInt(cursor.getColumnIndex("id"));
                String bitfield = cursor.getString(cursor.getColumnIndex("days"));
                int eventId = cursor.getInt(cursor.getColumnIndex("eventId"));
                String description = cursor.getString(cursor.getColumnIndex("description"));
                long startTime = cursor.getLong(cursor.getColumnIndex("startTime"));
                long endTime = cursor.getLong(cursor.getColumnIndex("endTime"));
                int taskId = cursor.getInt(cursor.getColumnIndex("taskId"));
                Event event = new Event(eventId, description, startTime, endTime, taskId);
                returnables.add(new Returnable(id, bitfield, event));
                cursor.moveToNext();
            }
        }
        cursor.close();
        return returnables;
    }

    ArrayList<Event> getTaskEvents() {
        Cursor cursor = db.rawQuery("SELECT * FROM Calendar WHERE taskId != '-1';", null);
        ArrayList<Event> events = new ArrayList<>();
        if (cursor.moveToFirst()) {
            while (!cursor.isAfterLast()) {
                int id = cursor.getInt(cursor.getColumnIndex("id"));
                String description = cursor.getString(cursor.getColumnIndex("description"));
                long startTime = cursor.getLong(cursor.getColumnIndex("startTime"));
                long endTime = cursor.getLong(cursor.getColumnIndex("endTime"));
                int taskId = cursor.getInt(cursor.getColumnIndex("taskId"));
                events.add(new Event(id, description, startTime, endTime, taskId));
                cursor.moveToNext();
            }
        }
        cursor.close();
        return events;
    }

    void addTaskHours(int taskId, float hoursCompleted) {
        db.execSQL("UPDATE Tasks SET hoursCompleted=(hoursCompleted + " + hoursCompleted + ") WHERE id='" + taskId + "';");
    }

    void cleanTasks() {
        db.execSQL("DELETE FROM Tasks where hoursCompleted >= hoursRequired");
    }

    private static ArrayList<Event> getEvents(Cursor cursor) {
        ArrayList<Event> events = new ArrayList<>();
        if (cursor.moveToFirst()) {
            while (!cursor.isAfterLast()) {
                int id = cursor.getInt(cursor.getColumnIndex("id"));
                String description = cursor.getString(cursor.getColumnIndex("description"));
                long startTime = cursor.getLong(cursor.getColumnIndex("startTime"));
                long endTime = cursor.getLong(cursor.getColumnIndex("endTime"));
                int taskId = cursor.getInt(cursor.getColumnIndex("taskId"));
                events.add(new Event(id, description, startTime, endTime, taskId));
                cursor.moveToNext();
            }
        }
        cursor.close();
        return events;
    }

    private static ArrayList<Returnable> getReturnables(Cursor cursor) {
        ArrayList<Returnable> returnables = new ArrayList<>();
        if (cursor.moveToFirst()) {
            while (!cursor.isAfterLast()) {
                int id = cursor.getInt(cursor.getColumnIndex("id"));
                String bitfield = cursor.getString(cursor.getColumnIndex("days"));
                int eventId = cursor.getInt(cursor.getColumnIndex("eventId"));
                String description = cursor.getString(cursor.getColumnIndex("description"));
                long startTime = cursor.getLong(cursor.getColumnIndex("startTime"));
                long endTime = cursor.getLong(cursor.getColumnIndex("endTime"));
                int taskId = cursor.getInt(cursor.getColumnIndex("taskId"));
                Event event = new Event(eventId, description, startTime, endTime, taskId);
                returnables.add(new Returnable(id, bitfield, event));
                cursor.moveToNext();
            }
        }
        cursor.close();
        return returnables;
    }

    private static ArrayList<Task> getTasks(Cursor cursor) {
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

    private static int getNextId(Cursor cursor) {
        int id = 0;
        if (cursor.moveToFirst()) {
            if (!cursor.isAfterLast()) {
                id = cursor.getInt(cursor.getColumnIndex("id")) + 1;
            }
        }
        cursor.close();
        return id;
    }

    private static String getSQL(String function, Object extra) {
        switch (function) {
            case "getNextEventId":
                return "SELECT id FROM Calendar ORDER BY id DESC;";
            case "getNextReturnableId":
                return "SELECT id FROM Returnables ORDER BY id DESC;";
            case "getNextTaskId":
                return "SELECT id FROM Tasks ORDER BY id DESC;";
            case "addEvent":
                Event event = (Event) extra;
                return "INSERT INTO Calendar (id, description, startTime, endTime, taskId) VALUES ('" + event.getId() + "', '" + event.getDescription() + "', '" + event.getStartTime().getTimeInMillis() + "', '" + event.getEndTime().getTimeInMillis() + "', '" + event.getTaskId() + "');";
            case "addReturnable":
                Returnable returnable = (Returnable) extra;
                return "INSERT INTO Returnables (id, days, eventId, description, startTime, endTime, taskId) VALUES ('" + returnable.getId() + "', '" + returnable.getBitfield() + "', '" + returnable.getEvent().getId() + "', '" + returnable.getEvent().getDescription() + "', '" + returnable.getEvent().getStartTime().getTimeInMillis() + "', '" + returnable.getEvent().getEndTime().getTimeInMillis() + "', '" + returnable.getEvent().getTaskId() + "');";
            case "addTask":
                Task task = (Task) extra;
                return "INSERT INTO Tasks (id, description, dueDate, priority, hoursRequired, hoursCompleted) VALUES ('" + task.getId() + "', '" + task.getDescription() + "', '" + task.getDueDate().getTimeInMillis() + "', '" + task.getPriority() + "', '" + task.getHoursRequired() + "', '" + task.getHoursCompleted() + "');";
            case "removeEvent":
                Integer eventId = (Integer) extra;
                return "DELETE FROM Calendar WHERE id='" + eventId + "';";
            case "removeReturnable":
                Integer returnableId = (Integer) extra;
                return "DELETE FROM Returnables WHERE id='" + returnableId + "';";
            case "removeTask":
                Integer taskId = (Integer) extra;
                return "DELETE FROM Tasks WHERE id='" + taskId + "';";
            default:
                return "";
        }
    }
}
