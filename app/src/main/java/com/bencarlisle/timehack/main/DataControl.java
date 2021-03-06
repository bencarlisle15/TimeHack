package com.bencarlisle.timehack.main;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.bencarlisle.timelibrary.main.DataControllable;
import com.bencarlisle.timelibrary.main.Helper;
import com.bencarlisle.timelibrary.main.Returnable;
import com.bencarlisle.timelibrary.main.Task;
import com.google.api.services.calendar.model.Event;

import net.openid.appauth.AuthState;

import org.json.JSONException;

import java.util.ArrayList;
import java.util.Calendar;

public class DataControl extends SQLiteOpenHelper implements DataControllable {

    private SQLiteDatabase db;
    private Context context;

    public DataControl(Context context) {
        super(context, "Calendar.db", null, 1);
        this.context = context;
        db = this.getWritableDatabase();
//        reboot();
    }

    void setAuthState(String authState) {
        if (getAuthState() == null) {
            db.execSQL("INSERT INTO AuthState VALUES ('" + authState + "');");
        } else {
            db.execSQL("UPDATE AuthState SET authState='" + authState + "';");
        }
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
        db.execSQL("CREATE TABLE AuthState (authState VARCHAR(256) UNIQUE PRIMARY KEY NOT NULL)");
        db.execSQL("CREATE TABLE Returnables (id INTEGER UNIQUE PRIMARY KEY NOT NULL, days TEXT NOT NULL, description TEXT NOT NULL, startTime BIGINTEGER NOT NULL, endTime BIGINTEGER NOT NULL, taskId INTEGER NOT NULL, timestamp DATETIME DEFAULT CURRENT_TIMESTAMP);");
        db.execSQL("CREATE TABLE Tasks (id INTEGER UNIQUE PRIMARY KEY NOT NULL, description TEXT NOT NULL, dueDate BIGINTEGER NOT NULL, priority INTEGER, hoursRequired FLOAT, hoursCompleted FLOAT, timestamp DATETIME DEFAULT CURRENT_TIMESTAMP);");
        db.execSQL("CREATE TABLE Organized (lastDay INTEGER NOT NULL)");
        db.execSQL("INSERT INTO Organized (lastDay) VALUES (-1)");
    }

    public void addEvent(Event event) {
        //sql injection is trivial
        Log.e("DATA EVENT", "Adding event " + event);
        CalendarControl.addEvent(event, context, getAuthState());
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

    public void addFuture(Event event) {
        //sql injection is trivial
        Log.e("DATA TASK", "ADDING FUTURE " + event);
        //same as adding event
        CalendarControl.addEvent(event, context, getAuthState());
    }

    void reboot() {
        onUpgrade(db, 0,0);
    }

    AuthState getAuthState() {
        Cursor cursor = db.rawQuery("SELECT authState FROM AuthState;", null);
        AuthState authState = null;
        if (cursor.moveToFirst()) {
            String authStateString = cursor.getString(cursor.getColumnIndex("authState"));
            try {
                authState = AuthState.jsonDeserialize(authStateString);
            } catch (JSONException ignored) {

            }
        }
        cursor.close();
        return authState;
    }

    public void removeEvent(Event event) {
        CalendarControl.removeEvent(event, context, getAuthState());
    }

    public void removeReturnable(int id) {
        db.execSQL(getSQL("removeReturnable", id));
    }

    public void removeTask(int id) {
        db.execSQL(getSQL("removeTask", id));
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS AuthState;");
        db.execSQL("DROP TABLE IF EXISTS Returnables;");
        db.execSQL("DROP TABLE IF EXISTS Tasks;");
        db.execSQL("DROP TABLE IF EXISTS Organized;");
        onCreate(db);
    }

    ArrayList<Returnable> getReturnablesOnDay(int day) {
        Cursor cursor = db.rawQuery("SELECT * FROM Returnables WHERE SUBSTR(days, " + (day + 1) + ", 1)='1';", null);
        ArrayList<Returnable> returnables = new ArrayList<>();
        if (cursor.moveToFirst()) {
            while (!cursor.isAfterLast()) {
                String bitfield = cursor.getString(cursor.getColumnIndex("days"));
                String description = cursor.getString(cursor.getColumnIndex("description"));
                long startTime = cursor.getLong(cursor.getColumnIndex("startTime"));
                long endTime = cursor.getLong(cursor.getColumnIndex("endTime"));
                int taskId = cursor.getInt(cursor.getColumnIndex("taskId"));
                Event event = Helper.getEvent(Helper.initCalendar(startTime), Helper.initCalendar(endTime), description, taskId);
                returnables.add(new Returnable(bitfield, event));
                cursor.moveToNext();
            }
        }
        cursor.close();
        return returnables;
    }


    ArrayList<Event> getFuturesOnDay() {
        return CalendarControl.getEvents(getAuthState());
    }

    ArrayList<Event> getTaskEvents() {
        return CalendarControl.getTaskEvents(getAuthState());
    }

    void addTaskHours(int taskId, float hoursCompleted) {
        db.execSQL("UPDATE Tasks SET hoursCompleted=(hoursCompleted + " + hoursCompleted + ") WHERE id='" + taskId + "';");
    }

    void cleanTasks() {
        db.execSQL("DELETE FROM Tasks where hoursCompleted >= hoursRequired");
    }

    private static ArrayList<Returnable> getReturnables(Cursor cursor) {
        ArrayList<Returnable> returnables = new ArrayList<>();
        if (cursor.moveToFirst()) {
            while (!cursor.isAfterLast()) {
                String bitfield = cursor.getString(cursor.getColumnIndex("days"));
                String description = cursor.getString(cursor.getColumnIndex("description"));
                long startTime = cursor.getLong(cursor.getColumnIndex("startTime"));
                long endTime = cursor.getLong(cursor.getColumnIndex("endTime"));
                int taskId = cursor.getInt(cursor.getColumnIndex("taskId"));
                Event event = Helper.getEvent(Helper.initCalendar(startTime), Helper.initCalendar(endTime), description, taskId);
                returnables.add(new Returnable(bitfield, event));
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
                String description = cursor.getString(cursor.getColumnIndex("description"));
                long dueDate = cursor.getLong(cursor.getColumnIndex("dueDate"));
                float hoursRequired = cursor.getFloat(cursor.getColumnIndex("hoursRequired"));
                float hoursCompleted = cursor.getFloat(cursor.getColumnIndex("hoursCompleted"));
                int priority = cursor.getInt(cursor.getColumnIndex("priority"));
                tasks.add(new Task(dueDate, description, priority, hoursRequired, hoursCompleted));
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
            case "getNextReturnableId":
                return "SELECT id FROM Returnables ORDER BY id DESC;";
            case "getNextTaskId":
                return "SELECT id FROM Tasks ORDER BY id DESC;";
            case "addReturnable":
                Returnable returnable = (Returnable) extra;
                return "INSERT INTO Returnables (id, days, description, startTime, endTime, taskId) VALUES ('" + returnable.hashCode() + "', '" + returnable.getBitfield() + "', '" + returnable.getEvent().getSummary() + "', '" + returnable.getEvent().getStart().getDateTime().getValue() + "', '" + returnable.getEvent().getEnd().getDateTime().getValue() + "', '" + returnable.getEvent().getDescription() + "');";
            case "addTask":
                Task task = (Task) extra;
                return "INSERT INTO Tasks (id, description, dueDate, priority, hoursRequired, hoursCompleted) VALUES ('" + task.hashCode() + "', '" + task.getDescription() + "', '" + task.getDueDate().getTimeInMillis() + "', '" + task.getPriority() + "', '" + task.getHoursRequired() + "', '" + task.getHoursCompleted() + "');";
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
